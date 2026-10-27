package com.autodev.licencias.servicio;

import static com.google.common.base.Preconditions.checkArgument;

import com.autodev.licencias.dao.AlertaDAO;
import com.autodev.licencias.dao.LicenciaDAO;
import com.autodev.licencias.modelo.EstadoLicencia;
import com.autodev.licencias.modelo.Licencia;
import com.autodev.licencias.modelo.TipoLicencia;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Casos de uso de las licencias: registrar ventas, renovar y mantener los estados al día.
 *
 * <p>Depende de las interfaces {@link LicenciaDAO} y {@link AlertaDAO}, no de MySQL
 * (principio de inversión de dependencias). Por eso se puede probar con DAO en memoria.</p>
 */
public class LicenciaServicio {

    private static final Logger log = LoggerFactory.getLogger(LicenciaServicio.class);
    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    private final LicenciaDAO licenciaDAO;
    private final AlertaDAO alertaDAO;
    private final Clock reloj;
    private final int diasAviso;

    /**
     * @param licenciaDAO acceso a las licencias
     * @param alertaDAO acceso a las alertas (para cerrarlas al renovar)
     * @param reloj fuente de la fecha actual (en las pruebas es una fecha fija)
     * @param diasAviso días antes del vencimiento en que la licencia pasa a "Por vencer"
     */
    public LicenciaServicio(LicenciaDAO licenciaDAO, AlertaDAO alertaDAO, Clock reloj, int diasAviso) {
        this.licenciaDAO = licenciaDAO;
        this.alertaDAO = alertaDAO;
        this.reloj = reloj;
        this.diasAviso = diasAviso;
    }

    /** @return la fecha de hoy según el reloj del sistema */
    public LocalDate hoy() {
        return LocalDate.now(reloj);
    }

    /** @return días antes del vencimiento en que se avisa */
    public int getDiasAviso() {
        return diasAviso;
    }

    /**
     * Registra la venta de una licencia nueva o la renovación de una anterior.
     * Calcula el vencimiento (un año) y el estado.
     *
     * @param venta datos ingresados por el vendedor
     * @return la licencia guardada
     * @throws IllegalArgumentException si algún dato no es válido
     */
    public Licencia registrarVenta(NuevaVenta venta) {
        validar(venta);

        Licencia licencia = new Licencia();
        licencia.setCliente(venta.cliente());
        licencia.setPrograma(venta.programa());
        licencia.setTipo(venta.tipo());
        licencia.setFechaVenta(venta.fechaVenta());
        licencia.setMonto(venta.monto().setScale(2, RoundingMode.HALF_UP));
        licencia.setDescuento(venta.descuentoPorcentaje().divide(CIEN, 4, RoundingMode.HALF_UP));
        licencia.setMedioPago(venta.medioPago().trim());
        licencia.setNotas(StringUtils.trimToNull(venta.notas()));

        Optional<Licencia> anterior = Optional.empty();
        if (venta.tipo() == TipoLicencia.RENOVACION) {
            anterior = licenciaARenovar(venta);
            checkArgument(anterior.isPresent(),
                    "El cliente no tiene una licencia de %s para renovar", venta.programa().getNombre());
            licencia.setFechaEmision(inicioRenovacion(anterior.get(), venta.fechaVenta()));
        } else {
            licencia.setFechaEmision(venta.fechaVenta());
        }
        licencia.calcularVencimiento();
        licencia.actualizarEstado(hoy(), diasAviso);
        licenciaDAO.guardar(licencia);

        anterior.ifPresent(this::marcarRenovada);
        log.info("Venta registrada: licencia {} de {} para {} por S/ {}", licencia.getIdLicencia(),
                licencia.getPrograma().getNombre(), licencia.getCliente().getNombre(), licencia.getMontoFinal());
        return licencia;
    }

    private void validar(NuevaVenta venta) {
        checkArgument(venta.cliente() != null, "Selecciona un cliente");
        checkArgument(venta.programa() != null, "Selecciona un programa");
        checkArgument(venta.tipo() != null, "Selecciona el tipo de venta");
        checkArgument(venta.fechaVenta() != null, "Ingresa la fecha de venta");
        checkArgument(!venta.fechaVenta().isAfter(hoy()), "La fecha de venta no puede ser futura");
        checkArgument(venta.monto() != null && venta.monto().signum() > 0, "El monto debe ser mayor a cero");
        checkArgument(venta.descuentoPorcentaje() != null
                        && venta.descuentoPorcentaje().signum() >= 0
                        && venta.descuentoPorcentaje().compareTo(CIEN) <= 0,
                "El descuento debe estar entre 0 y 100 %");
        checkArgument(StringUtils.isNotBlank(venta.medioPago()), "Selecciona el medio de pago");
    }

    /** La última licencia (no renovada) del mismo cliente y programa. */
    private Optional<Licencia> licenciaARenovar(NuevaVenta venta) {
        return licenciaDAO.listar().stream()
                .filter(l -> l.getCliente().equals(venta.cliente()))
                .filter(l -> l.getPrograma().equals(venta.programa()))
                .filter(l -> l.getEstado() != EstadoLicencia.RENOVADA)
                .max(Comparator.comparing(Licencia::getFechaVencimiento));
    }

    /** Si la licencia anterior aún no vence, la nueva empieza cuando vence; si ya venció, empieza en la venta. */
    private LocalDate inicioRenovacion(Licencia anterior, LocalDate fechaVenta) {
        return anterior.getFechaVencimiento().isAfter(fechaVenta) ? anterior.getFechaVencimiento() : fechaVenta;
    }

    private void marcarRenovada(Licencia anterior) {
        anterior.setEstado(EstadoLicencia.RENOVADA);
        licenciaDAO.actualizarEstado(anterior.getIdLicencia(), EstadoLicencia.RENOVADA);
        alertaDAO.atenderPorLicencia(anterior.getIdLicencia());
    }

    /**
     * Lista todas las licencias con el estado recalculado a la fecha de hoy.
     *
     * @return licencias de la más reciente a la más antigua
     */
    public List<Licencia> listar() {
        List<Licencia> licencias = licenciaDAO.listar();
        LocalDate hoy = hoy();
        int cambios = 0;
        for (Licencia licencia : licencias) {
            EstadoLicencia antes = licencia.getEstado();
            if (licencia.actualizarEstado(hoy, diasAviso) != antes) {
                licenciaDAO.actualizarEstado(licencia.getIdLicencia(), licencia.getEstado());
                cambios++;
            }
        }
        if (cambios > 0) {
            log.info("Se actualizó el estado de {} licencias", cambios);
        }
        return licencias;
    }
}

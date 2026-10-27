package com.autodev.licencias.modelo;

import com.google.common.base.Preconditions;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Licencia anual de un programa vendida a un cliente. Cada venta genera una licencia.
 *
 * <p>Reglas del negocio:</p>
 * <ul>
 *   <li>Vence un año después de la fecha de emisión.</li>
 *   <li>Con 30 días o menos para vencer pasa a "Por vencer".</li>
 *   <li>Si pasa la fecha de vencimiento sin renovarse queda "Vencida".</li>
 * </ul>
 */
public class Licencia {

    private int idLicencia;
    private Cliente cliente;
    private Programa programa;
    private TipoLicencia tipo = TipoLicencia.NUEVA;
    private LocalDate fechaVenta;
    private BigDecimal monto = BigDecimal.ZERO;
    private BigDecimal descuento = BigDecimal.ZERO;
    private String medioPago;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private EstadoLicencia estado = EstadoLicencia.VIGENTE;
    private String notas;

    /**
     * Calcula la fecha de vencimiento: un año después de la emisión.
     *
     * @return la fecha de vencimiento calculada
     * @throws IllegalStateException si no tiene fecha de emisión
     */
    public LocalDate calcularVencimiento() {
        Preconditions.checkState(fechaEmision != null, "La licencia no tiene fecha de emisión");
        fechaVencimiento = fechaEmision.plusYears(1);
        return fechaVencimiento;
    }

    /**
     * Días que faltan para el vencimiento. Es negativo si ya venció.
     *
     * @param hoy fecha de referencia
     * @return días restantes
     */
    public long diasRestantes(LocalDate hoy) {
        return ChronoUnit.DAYS.between(hoy, fechaVencimiento);
    }

    /**
     * Recalcula el estado según los días que faltan para vencer.
     * Una licencia renovada ya no cambia de estado.
     *
     * @param hoy fecha de referencia
     * @param diasAviso días antes del vencimiento en que pasa a "Por vencer" (30)
     * @return el estado actualizado
     */
    public EstadoLicencia actualizarEstado(LocalDate hoy, int diasAviso) {
        if (estado == EstadoLicencia.RENOVADA) {
            return estado;
        }
        long dias = diasRestantes(hoy);
        if (dias < 0) {
            estado = EstadoLicencia.VENCIDA;
        } else if (dias <= diasAviso) {
            estado = EstadoLicencia.POR_VENCER;
        } else {
            estado = EstadoLicencia.VIGENTE;
        }
        return estado;
    }

    /**
     * Total cobrado: monto menos el descuento.
     *
     * @return total con dos decimales
     */
    public BigDecimal getMontoFinal() {
        return monto.multiply(BigDecimal.ONE.subtract(descuento)).setScale(2, RoundingMode.HALF_UP);
    }

    public int getIdLicencia() {
        return idLicencia;
    }

    public void setIdLicencia(int idLicencia) {
        this.idLicencia = idLicencia;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public TipoLicencia getTipo() {
        return tipo;
    }

    public void setTipo(TipoLicencia tipo) {
        this.tipo = tipo;
    }

    public LocalDate getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDate fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    /** @return descuento como fracción: 0.10 es 10 % */
    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public EstadoLicencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoLicencia estado) {
        this.estado = estado;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}

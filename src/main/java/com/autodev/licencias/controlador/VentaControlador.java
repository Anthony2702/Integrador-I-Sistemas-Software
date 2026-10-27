package com.autodev.licencias.controlador;

import com.autodev.licencias.Contexto;
import com.autodev.licencias.modelo.Cliente;
import com.autodev.licencias.modelo.Licencia;
import com.autodev.licencias.modelo.Programa;
import com.autodev.licencias.modelo.TipoLicencia;
import com.autodev.licencias.servicio.NuevaVenta;
import com.autodev.licencias.vista.ClienteDialogo;
import com.autodev.licencias.vista.Dialogos;
import com.autodev.licencias.vista.Estilos;
import com.autodev.licencias.vista.VentaPanel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Controlador de la pantalla "Registrar venta".
 */
public class VentaControlador {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final VentaPanel vista;
    private final Contexto contexto;
    private final Navegacion navegacion;

    public VentaControlador(VentaPanel vista, Contexto contexto, Navegacion navegacion) {
        this.vista = vista;
        this.contexto = contexto;
        this.navegacion = navegacion;

        vista.getCboPrograma().addActionListener(e -> ponerPrecioDeLista());
        vista.getCboTipo().addActionListener(e -> actualizarCalculo());
        vista.getBtnNuevoCliente().addActionListener(e -> nuevoCliente());
        vista.getBtnRegistrar().addActionListener(e -> registrar());
        vista.getBtnLimpiar().addActionListener(e -> limpiar());
        DocumentListener alEscribir = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarCalculo();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarCalculo();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizarCalculo();
            }
        };
        vista.getTxtFecha().getDocument().addDocumentListener(alEscribir);
        vista.getTxtMonto().getDocument().addDocumentListener(alEscribir);
        vista.getTxtDescuento().getDocument().addDocumentListener(alEscribir);
    }

    /** Recarga clientes y programas sin perder lo que ya estaba elegido. */
    public void refrescar() {
        Cliente cliente = (Cliente) vista.getCboCliente().getSelectedItem();
        Programa programa = (Programa) vista.getCboPrograma().getSelectedItem();
        vista.cargarClientes(contexto.getClientes().listar(), cliente);
        vista.cargarProgramas(contexto.getProgramas().listar(), programa);
        if (StringUtils.isBlank(vista.getTxtFecha().getText())) {
            vista.getTxtFecha().setText(Estilos.fecha(contexto.getLicencias().hoy()));
        }
        actualizarCalculo();
    }

    /**
     * Llena el formulario para renovar una licencia.
     *
     * @param licencia licencia que se va a renovar
     */
    public void prepararRenovacion(Licencia licencia) {
        limpiar();
        refrescar();
        vista.getCboCliente().setSelectedItem(licencia.getCliente());
        vista.getCboPrograma().setSelectedItem(licencia.getPrograma());
        vista.getCboTipo().setSelectedItem(TipoLicencia.RENOVACION);
        vista.getTxtNotas().setText("Renovación de la licencia N° " + licencia.getIdLicencia());
        actualizarCalculo();
    }

    private void ponerPrecioDeLista() {
        Programa programa = (Programa) vista.getCboPrograma().getSelectedItem();
        if (programa != null) {
            vista.getTxtMonto().setText(programa.getPrecio().toPlainString());
        }
    }

    /** Muestra el total y el vencimiento mientras el vendedor escribe. */
    private void actualizarCalculo() {
        String total = "S/ 0.00";
        String vence = "-";
        try {
            BigDecimal monto = leerNumero(vista.getTxtMonto().getText(), "monto");
            BigDecimal descuento = leerDescuento();
            total = Estilos.soles(monto.multiply(BigDecimal.ONE.subtract(
                    descuento.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)))
                    .setScale(2, RoundingMode.HALF_UP));
        } catch (IllegalArgumentException e) {
            // Mientras el dato esté incompleto no se muestra total
        }
        if (vista.getCboTipo().getSelectedItem() == TipoLicencia.RENOVACION) {
            vence = "1 año después del vencimiento anterior";
        } else {
            try {
                vence = Estilos.fecha(leerFecha().plusYears(1));
            } catch (IllegalArgumentException e) {
                // fecha incompleta
            }
        }
        vista.mostrarCalculo(total, vence);
    }

    private void registrar() {
        try {
            NuevaVenta venta = new NuevaVenta(
                    (Cliente) vista.getCboCliente().getSelectedItem(),
                    (Programa) vista.getCboPrograma().getSelectedItem(),
                    (TipoLicencia) vista.getCboTipo().getSelectedItem(),
                    leerFecha(),
                    leerNumero(vista.getTxtMonto().getText(), "monto"),
                    leerDescuento(),
                    (String) vista.getCboMedioPago().getSelectedItem(),
                    vista.getTxtNotas().getText());
            Licencia licencia = contexto.getLicencias().registrarVenta(venta);
            Dialogos.info(vista, String.format("Venta registrada.%n%nLicencia N° %d de %s para %s.%n"
                            + "Total cobrado: %s%nVence el: %s",
                    licencia.getIdLicencia(), licencia.getPrograma().getNombre(), licencia.getCliente().getNombre(),
                    Estilos.soles(licencia.getMontoFinal()), Estilos.fecha(licencia.getFechaVencimiento())));
            limpiar();
            navegacion.alertasCambiaron();
        } catch (RuntimeException e) {
            Dialogos.error(vista, e);
        }
    }

    private void nuevoCliente() {
        ClienteDialogo dialogo = new ClienteDialogo(SwingUtilities.getWindowAncestor(vista), null);
        dialogo.alGuardar(() -> {
            try {
                Cliente cliente = contexto.getClientes().registrar(
                        dialogo.getNombre(), dialogo.getRuc(), dialogo.getContacto());
                dialogo.dispose();
                vista.cargarClientes(contexto.getClientes().listar(), cliente);
            } catch (RuntimeException e) {
                Dialogos.error(dialogo, e);
            }
        });
        dialogo.setVisible(true);
    }

    private void limpiar() {
        vista.getCboCliente().setSelectedIndex(-1);
        vista.getCboTipo().setSelectedItem(TipoLicencia.NUEVA);
        vista.getTxtFecha().setText(Estilos.fecha(contexto.getLicencias().hoy()));
        vista.getTxtDescuento().setText("");
        vista.getCboMedioPago().setSelectedIndex(0);
        vista.getTxtNotas().setText("");
        ponerPrecioDeLista();
        actualizarCalculo();
    }

    private LocalDate leerFecha() {
        try {
            return LocalDate.parse(vista.getTxtFecha().getText().trim(), FORMATO_FECHA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La fecha de venta debe tener el formato dd/mm/aaaa");
        }
    }

    private BigDecimal leerDescuento() {
        String texto = vista.getTxtDescuento().getText();
        return StringUtils.isBlank(texto) ? BigDecimal.ZERO : leerNumero(texto, "descuento");
    }

    private static BigDecimal leerNumero(String texto, String campo) {
        String limpio = StringUtils.trimToEmpty(texto).replace(",", ".");
        if (!NumberUtils.isCreatable(limpio)) {
            throw new IllegalArgumentException("Ingresa un " + campo + " válido");
        }
        return new BigDecimal(limpio);
    }
}

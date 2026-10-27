package com.autodev.licencias.vista;

import com.autodev.licencias.modelo.Cliente;
import com.autodev.licencias.modelo.Programa;
import com.autodev.licencias.modelo.TipoLicencia;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Pantalla "Registrar venta": formulario para vender o renovar una licencia.
 */
public class VentaPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /** Medios de pago que acepta AUTODEV. */
    public static final String[] MEDIOS_PAGO = {"Yape", "Plin", "Transferencia", "Mercado Pago", "Efectivo"};

    private final JComboBox<Cliente> cboCliente = new JComboBox<>();
    private final JButton btnNuevoCliente = new JButton("+ Nuevo cliente");
    private final JComboBox<Programa> cboPrograma = new JComboBox<>();
    private final JComboBox<TipoLicencia> cboTipo = new JComboBox<>(TipoLicencia.values());
    private final JTextField txtFecha = new JTextField(12);
    private final JTextField txtMonto = new JTextField(12);
    private final JTextField txtDescuento = new JTextField(12);
    private final JComboBox<String> cboMedioPago = new JComboBox<>(MEDIOS_PAGO);
    private final JTextArea txtNotas = new JTextArea(3, 30);
    private final JLabel lblTotal = new JLabel("S/ 0.00");
    private final JLabel lblVence = new JLabel("-");
    private final JButton btnRegistrar = Estilos.botonPrincipal("Registrar venta");
    private final JButton btnLimpiar = new JButton("Limpiar");

    public VentaPanel() {
        setLayout(new BorderLayout());
        add(Estilos.encabezado("Registrar venta de licencia",
                "Venta nueva o renovación. El vencimiento se calcula solo (un año)."), BorderLayout.NORTH);

        txtFecha.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "dd/mm/aaaa");
        txtDescuento.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "0");
        txtNotas.setLineWrap(true);
        txtNotas.setWrapStyleWord(true);

        JPanel formulario = Estilos.tarjeta(new GridBagLayout());
        JPanel filaCliente = new JPanel(new BorderLayout(8, 0));
        filaCliente.setOpaque(false);
        filaCliente.add(cboCliente, BorderLayout.CENTER);
        filaCliente.add(btnNuevoCliente, BorderLayout.EAST);

        int fila = 0;
        campo(formulario, fila++, "Cliente *", filaCliente, 3);
        campo(formulario, fila, "Programa *", cboPrograma, 1);
        campo(formulario, fila++, 2, "Tipo *", cboTipo);
        campo(formulario, fila, "Fecha de venta *", txtFecha, 1);
        campo(formulario, fila++, 2, "Medio de pago *", cboMedioPago);
        campo(formulario, fila, "Monto S/ *", txtMonto, 1);
        campo(formulario, fila++, 2, "Descuento %", txtDescuento);
        campo(formulario, fila++, "Notas", new JScrollPane(txtNotas), 3);

        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 20f));
        lblTotal.setForeground(Estilos.PRIMARIO);
        lblVence.setFont(lblVence.getFont().deriveFont(Font.BOLD, 15f));
        JPanel totales = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        totales.setOpaque(false);
        totales.add(Estilos.nota("Total a cobrar:  "));
        totales.add(lblTotal);
        totales.add(Estilos.nota("      Vence el:  "));
        totales.add(lblVence);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = fila++;
        c.gridwidth = 4;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(14, 0, 6, 0);
        formulario.add(totales, c);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(btnLimpiar);
        botones.add(btnRegistrar);
        c.gridy = fila;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(8, 0, 0, 0);
        formulario.add(botones, c);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setOpaque(false);
        contenedor.add(formulario, BorderLayout.NORTH);
        contenedor.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 120));
        add(contenedor, BorderLayout.CENTER);
    }

    /** Agrega etiqueta y campo en la columna izquierda. */
    private void campo(JPanel panel, int fila, String etiqueta, JComponent componente, int ancho) {
        campo(panel, fila, 0, etiqueta, componente, ancho);
    }

    /** Agrega etiqueta y campo en la columna indicada. */
    private void campo(JPanel panel, int fila, int columna, String etiqueta, JComponent componente) {
        campo(panel, fila, columna, etiqueta, componente, 1);
    }

    private void campo(JPanel panel, int fila, int columna, String etiqueta, JComponent componente, int ancho) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = columna;
        c.gridy = fila;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(7, columna == 0 ? 0 : 24, 7, 12);
        panel.add(new JLabel(etiqueta), c);
        c.gridx = columna + 1;
        c.gridwidth = ancho;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(7, 0, 7, 0);
        panel.add(componente, c);
    }

    /**
     * Carga la lista de clientes y vuelve a seleccionar el que estaba elegido.
     *
     * @param clientes clientes ordenados por nombre
     * @param seleccionado cliente a seleccionar (puede ser null)
     */
    public void cargarClientes(List<Cliente> clientes, Cliente seleccionado) {
        cboCliente.removeAllItems();
        clientes.forEach(cboCliente::addItem);
        cboCliente.setSelectedItem(seleccionado);
        if (seleccionado == null) {
            cboCliente.setSelectedIndex(-1);
        }
    }

    /**
     * Carga la lista de programas.
     *
     * @param programas programas con su precio
     * @param seleccionado programa a seleccionar (puede ser null)
     */
    public void cargarProgramas(List<Programa> programas, Programa seleccionado) {
        cboPrograma.removeAllItems();
        programas.forEach(cboPrograma::addItem);
        if (seleccionado != null) {
            cboPrograma.setSelectedItem(seleccionado);
        }
    }

    public JComboBox<Cliente> getCboCliente() {
        return cboCliente;
    }

    public JButton getBtnNuevoCliente() {
        return btnNuevoCliente;
    }

    public JComboBox<Programa> getCboPrograma() {
        return cboPrograma;
    }

    public JComboBox<TipoLicencia> getCboTipo() {
        return cboTipo;
    }

    public JTextField getTxtFecha() {
        return txtFecha;
    }

    public JTextField getTxtMonto() {
        return txtMonto;
    }

    public JTextField getTxtDescuento() {
        return txtDescuento;
    }

    public JComboBox<String> getCboMedioPago() {
        return cboMedioPago;
    }

    public JTextArea getTxtNotas() {
        return txtNotas;
    }

    public JButton getBtnRegistrar() {
        return btnRegistrar;
    }

    public JButton getBtnLimpiar() {
        return btnLimpiar;
    }

    /** Muestra el total y la fecha de vencimiento calculados. */
    public void mostrarCalculo(String total, String vence) {
        lblTotal.setText(total);
        lblVence.setText(vence);
    }
}

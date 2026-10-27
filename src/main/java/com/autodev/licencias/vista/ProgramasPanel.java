package com.autodev.licencias.vista;

import com.autodev.licencias.modelo.Programa;
import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Pantalla "Programas": precios de lista de cada programa de AUTODEV.
 */
public class ProgramasPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final ModeloTabla modelo = new ModeloTabla(new String[] {"N°", "Programa", "Precio de lista S/"},
            new Class<?>[] {Integer.class, String.class, BigDecimal.class});
    private final JTable tabla = new JTable(modelo);
    private final JButton btnCambiarPrecio = Estilos.botonPrincipal("Cambiar precio");
    private final List<Programa> programas = new ArrayList<>();

    /**
     * @param esAdmin si es false, el botón para cambiar precios queda desactivado
     */
    public ProgramasPanel(boolean esAdmin) {
        setLayout(new BorderLayout());
        add(Estilos.encabezado("Programas y precios", "Precio de lista anual de cada programa", btnCambiarPrecio),
                BorderLayout.NORTH);
        Estilos.tabla(tabla);
        tabla.getColumnModel().getColumn(0).setMaxWidth(60);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        JLabel nota = Estilos.nota(esAdmin
                ? "El precio de lista se propone al registrar una venta; el vendedor puede aplicar un descuento."
                : "Solo el administrador puede cambiar los precios.");
        nota.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        centro.add(nota, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);
        btnCambiarPrecio.setEnabled(esAdmin);
    }

    /**
     * Muestra los programas en la tabla.
     *
     * @param lista programas con su precio
     */
    public void cargar(List<Programa> lista) {
        programas.clear();
        programas.addAll(lista);
        modelo.limpiar();
        for (Programa p : lista) {
            modelo.addRow(new Object[] {p.getIdPrograma(), p.getNombre(), p.getPrecio()});
        }
    }

    /** @return el programa de la fila seleccionada, o null */
    public Programa getProgramaSeleccionado() {
        int fila = tabla.getSelectedRow();
        return fila < 0 ? null : programas.get(fila);
    }

    public JButton getBtnCambiarPrecio() {
        return btnCambiarPrecio;
    }
}

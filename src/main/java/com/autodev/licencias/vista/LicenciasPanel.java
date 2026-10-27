package com.autodev.licencias.vista;

import com.autodev.licencias.modelo.EstadoLicencia;
import com.autodev.licencias.modelo.Licencia;
import com.autodev.licencias.modelo.Programa;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import org.apache.commons.lang3.StringUtils;

/**
 * Pantalla "Licencias": lista de licencias con filtros, renovación y exportación a Excel.
 */
public class LicenciasPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final String TODOS = "Todos";
    private static final int COL_CLIENTE = 1;
    private static final int COL_PROGRAMA = 2;
    private static final int COL_ESTADO = 8;

    private final ModeloTabla modelo = new ModeloTabla(
            new String[] {"N°", "Cliente", "Programa", "Tipo", "Venta", "Total S/", "Vence", "Días rest.", "Estado"},
            new Class<?>[] {Integer.class, String.class, String.class, String.class, String.class,
                BigDecimal.class, String.class, Long.class, EstadoLicencia.class});
    private final JTable tabla = new JTable(modelo);
    private final TableRowSorter<ModeloTabla> ordenador = new TableRowSorter<>(modelo);
    private final JTextField txtBuscar = new JTextField(18);
    private final JComboBox<Object> cboEstado = new JComboBox<>();
    private final JComboBox<String> cboPrograma = new JComboBox<>();
    private final JButton btnRenovar = Estilos.botonPrincipal("Renovar");
    private final JButton btnExportar = new JButton("Exportar a Excel");
    private final JLabel lblTotal = Estilos.nota(" ");
    private final List<Licencia> licencias = new ArrayList<>();

    public LicenciasPanel() {
        setLayout(new BorderLayout());
        add(Estilos.encabezado("Licencias", "Licencias registradas y su estado a la fecha",
                btnExportar, btnRenovar), BorderLayout.NORTH);

        Estilos.tabla(tabla);
        tabla.setRowSorter(ordenador);
        tabla.getColumnModel().getColumn(COL_ESTADO).setCellRenderer(new EstadoRenderer());
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabla.getColumnModel().getColumn(COL_CLIENTE).setPreferredWidth(230);
        tabla.getColumnModel().getColumn(COL_PROGRAMA).setPreferredWidth(130);

        txtBuscar.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Buscar cliente o programa");
        cboEstado.addItem(TODOS);
        for (EstadoLicencia estado : EstadoLicencia.values()) {
            cboEstado.addItem(estado);
        }
        cboPrograma.addItem(TODOS);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtros.setOpaque(false);
        filtros.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        filtros.add(txtBuscar);
        filtros.add(new JLabel("  Estado:"));
        filtros.add(cboEstado);
        filtros.add(new JLabel("  Programa:"));
        filtros.add(cboPrograma);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(filtros, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        lblTotal.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        centro.add(lblTotal, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);

        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }
        });
        cboEstado.addActionListener(e -> filtrar());
        cboPrograma.addActionListener(e -> filtrar());
    }

    /**
     * Muestra las licencias en la tabla.
     *
     * @param lista licencias con su estado actualizado
     * @param programas programas para el filtro
     * @param hoy fecha para calcular los días restantes
     */
    public void cargar(List<Licencia> lista, List<Programa> programas, LocalDate hoy) {
        licencias.clear();
        licencias.addAll(lista);
        modelo.limpiar();
        for (Licencia l : lista) {
            modelo.addRow(new Object[] {l.getIdLicencia(), l.getCliente().getNombre(), l.getPrograma().getNombre(),
                l.getTipo().getEtiqueta(), Estilos.fecha(l.getFechaVenta()), l.getMontoFinal(),
                Estilos.fecha(l.getFechaVencimiento()), l.diasRestantes(hoy), l.getEstado()});
        }
        Object programaElegido = cboPrograma.getSelectedItem();
        cboPrograma.removeAllItems();
        cboPrograma.addItem(TODOS);
        programas.forEach(p -> cboPrograma.addItem(p.getNombre()));
        cboPrograma.setSelectedItem(programaElegido == null ? TODOS : programaElegido);
        filtrar();
    }

    private void filtrar() {
        List<RowFilter<ModeloTabla, Integer>> filtros = new ArrayList<>();
        String texto = StringUtils.trimToEmpty(txtBuscar.getText());
        if (!texto.isEmpty()) {
            filtros.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(texto), COL_CLIENTE, COL_PROGRAMA));
        }
        if (cboEstado.getSelectedItem() instanceof EstadoLicencia estado) {
            filtros.add(new RowFilter<>() {
                @Override
                public boolean include(Entry<? extends ModeloTabla, ? extends Integer> fila) {
                    return fila.getValue(COL_ESTADO) == estado;
                }
            });
        }
        Object programa = cboPrograma.getSelectedItem();
        if (programa != null && !TODOS.equals(programa)) {
            filtros.add(RowFilter.regexFilter("^" + java.util.regex.Pattern.quote(programa.toString()) + "$", COL_PROGRAMA));
        }
        ordenador.setRowFilter(filtros.isEmpty() ? null : RowFilter.andFilter(filtros));
        lblTotal.setText("Mostrando " + tabla.getRowCount() + " de " + modelo.getRowCount() + " licencias");
    }

    /** @return la licencia de la fila seleccionada, o null */
    public Licencia getLicenciaSeleccionada() {
        int fila = tabla.getSelectedRow();
        return fila < 0 ? null : licencias.get(tabla.convertRowIndexToModel(fila));
    }

    /** @return las licencias que se ven con los filtros actuales */
    public List<Licencia> getLicenciasVisibles() {
        List<Licencia> visibles = new ArrayList<>();
        for (int i = 0; i < tabla.getRowCount(); i++) {
            visibles.add(licencias.get(tabla.convertRowIndexToModel(i)));
        }
        return visibles;
    }

    public JButton getBtnRenovar() {
        return btnRenovar;
    }

    public JButton getBtnExportar() {
        return btnExportar;
    }
}

package com.autodev.licencias.vista;

import com.autodev.licencias.modelo.Cliente;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
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
 * Pantalla "Clientes": lista, búsqueda, registro y edición de clientes.
 */
public class ClientesPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final ModeloTabla modelo = new ModeloTabla(new String[] {"N°", "Nombre o razón social", "RUC", "Contacto"},
            new Class<?>[] {Integer.class, String.class, String.class, String.class});
    private final JTable tabla = new JTable(modelo);
    private final TableRowSorter<ModeloTabla> ordenador = new TableRowSorter<>(modelo);
    private final JTextField txtBuscar = new JTextField(22);
    private final JButton btnNuevo = Estilos.botonPrincipal("+ Nuevo cliente");
    private final JButton btnEditar = new JButton("Editar");
    private final List<Cliente> clientes = new ArrayList<>();

    public ClientesPanel() {
        setLayout(new BorderLayout());
        add(Estilos.encabezado("Clientes", "Empresas y personas que compran licencias", btnEditar, btnNuevo),
                BorderLayout.NORTH);

        Estilos.tabla(tabla);
        tabla.setRowSorter(ordenador);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(320);

        txtBuscar.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Buscar por nombre o RUC");
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
        JPanel buscador = new JPanel(new BorderLayout());
        buscador.setOpaque(false);
        buscador.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        buscador.add(txtBuscar, BorderLayout.WEST);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(buscador, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
    }

    private void filtrar() {
        String texto = StringUtils.trimToEmpty(txtBuscar.getText());
        ordenador.setRowFilter(texto.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(texto), 1, 2));
    }

    /**
     * Muestra los clientes en la tabla.
     *
     * @param lista clientes ordenados por nombre
     */
    public void cargar(List<Cliente> lista) {
        clientes.clear();
        clientes.addAll(lista);
        modelo.limpiar();
        for (Cliente c : lista) {
            modelo.addRow(new Object[] {c.getIdCliente(), c.getNombre(),
                c.getRuc() == null ? "" : c.getRuc(), c.getContacto() == null ? "" : c.getContacto()});
        }
        filtrar();
    }

    /** @return el cliente de la fila seleccionada, o null */
    public Cliente getClienteSeleccionado() {
        int fila = tabla.getSelectedRow();
        return fila < 0 ? null : clientes.get(tabla.convertRowIndexToModel(fila));
    }

    public JButton getBtnNuevo() {
        return btnNuevo;
    }

    public JButton getBtnEditar() {
        return btnEditar;
    }
}

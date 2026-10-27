package com.autodev.licencias.vista;

import com.autodev.licencias.modelo.Alerta;
import com.autodev.licencias.modelo.Licencia;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Pantalla "Alertas": licencias por vencer o vencidas que el vendedor debe atender.
 */
public class AlertasPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final ModeloTabla modelo = new ModeloTabla(
            new String[] {"Cliente", "Programa", "Vence", "Situación", "Contacto", "Alerta generada"},
            new Class<?>[] {String.class, String.class, String.class, String.class, String.class, String.class});
    private final JTable tabla = new JTable(modelo);
    private final JButton btnRevisar = new JButton("Revisar vencimientos ahora");
    private final JButton btnContactar = Estilos.botonPrincipal("Contactar por WhatsApp");
    private final JButton btnRenovar = new JButton("Renovar");
    private final JButton btnAtendida = new JButton("Marcar como atendida");
    private final JLabel lblEstado = Estilos.nota(" ");
    private final List<Alerta> alertas = new ArrayList<>();
    private final List<Long> diasPorFila = new ArrayList<>();

    public AlertasPanel() {
        setLayout(new BorderLayout());
        add(Estilos.encabezado("Alertas de vencimiento",
                "Licencias que vencen en 30 días o menos, o que vencieron sin renovarse", btnRevisar),
                BorderLayout.NORTH);

        Estilos.tabla(tabla);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(220);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(170);
        tabla.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public java.awt.Component getTableCellRendererComponent(JTable t, Object valor, boolean sel,
                                                                    boolean foco, int fila, int col) {
                java.awt.Component celda = super.getTableCellRendererComponent(t, valor, sel, foco, fila, col);
                if (!sel) {
                    long dias = diasPorFila.get(t.convertRowIndexToModel(fila));
                    celda.setForeground(dias < 0 ? Estilos.ROJO : Estilos.AMBAR);
                }
                celda.setFont(celda.getFont().deriveFont(java.awt.Font.BOLD));
                return celda;
            }
        });

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acciones.setOpaque(false);
        acciones.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        acciones.add(btnContactar);
        acciones.add(btnRenovar);
        acciones.add(btnAtendida);
        acciones.add(lblEstado);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        centro.add(acciones, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);
    }

    /**
     * Muestra las alertas pendientes.
     *
     * @param lista alertas sin atender
     * @param hoy fecha para calcular la situación de cada licencia
     */
    public void cargar(List<Alerta> lista, LocalDate hoy) {
        alertas.clear();
        alertas.addAll(lista);
        diasPorFila.clear();
        modelo.limpiar();
        for (Alerta alerta : lista) {
            Licencia l = alerta.getLicencia();
            long dias = l.diasRestantes(hoy);
            diasPorFila.add(dias);
            modelo.addRow(new Object[] {l.getCliente().getNombre(), l.getPrograma().getNombre(),
                Estilos.fecha(l.getFechaVencimiento()), situacion(dias),
                l.getCliente().getContacto() == null ? "-" : l.getCliente().getContacto(),
                Estilos.fecha(alerta.getFechaGenerada().toLocalDate())});
        }
        lblEstado.setText(lista.isEmpty() ? "   No hay alertas pendientes." : "   " + lista.size() + " alertas pendientes");
    }

    private static String situacion(long dias) {
        if (dias < 0) {
            return "Vencida hace " + Math.abs(dias) + (dias == -1 ? " día" : " días");
        }
        if (dias == 0) {
            return "Vence hoy";
        }
        return "Vence en " + dias + (dias == 1 ? " día" : " días");
    }

    /** @return la alerta de la fila seleccionada, o null */
    public Alerta getAlertaSeleccionada() {
        int fila = tabla.getSelectedRow();
        return fila < 0 ? null : alertas.get(tabla.convertRowIndexToModel(fila));
    }

    public JButton getBtnRevisar() {
        return btnRevisar;
    }

    public JButton getBtnContactar() {
        return btnContactar;
    }

    public JButton getBtnRenovar() {
        return btnRenovar;
    }

    public JButton getBtnAtendida() {
        return btnAtendida;
    }
}

package com.autodev.licencias.vista;

import com.autodev.licencias.modelo.ResumenMes;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * Pantalla "Resumen": ingresos del mes elegido, ingresos por programa y estado de la cartera.
 */
public class ResumenPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter MES = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("es-PE"));

    private final JComboBox<YearMonth> cboMes = new JComboBox<>();
    private final JLabel lblIngresos = new JLabel();
    private final JLabel lblMeta = new JLabel();
    private final JProgressBar barraMeta = new JProgressBar(0, 100);
    private final JLabel lblAvanceMeta = Estilos.nota("");
    private final JLabel lblVendidas = new JLabel();
    private final JLabel lblVigentes = new JLabel();
    private final JLabel lblPorVencer = new JLabel();
    private final JLabel lblVencidas = new JLabel();
    private final JLabel lblClientes = new JLabel();
    private final JPanel panelProgramas = Estilos.tarjeta(new GridBagLayout());

    public ResumenPanel() {
        setLayout(new BorderLayout());
        cboMes.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                          boolean seleccionado, boolean foco) {
                Object texto = valor instanceof YearMonth mes ? nombreMes(mes) : valor;
                return super.getListCellRendererComponent(lista, texto, indice, seleccionado, foco);
            }
        });
        add(Estilos.encabezado("Resumen", "Ventas del mes seleccionado y estado actual de la cartera",
                new JLabel("Mes:"), cboMes), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));

        JPanel meta = Estilos.indicador("Meta del mes", lblMeta);
        JPanel avance = new JPanel(new BorderLayout(0, 4));
        avance.setOpaque(false);
        avance.add(barraMeta, BorderLayout.NORTH);
        avance.add(lblAvanceMeta, BorderLayout.SOUTH);
        meta.add(avance, BorderLayout.SOUTH);

        JPanel ventas = fila(3);
        ventas.add(Estilos.indicador("Ingresos del mes", lblIngresos));
        ventas.add(meta);
        ventas.add(Estilos.indicador("Licencias vendidas", lblVendidas));
        cuerpo.add(izquierda(Estilos.seccion("Ventas del mes")));
        cuerpo.add(izquierda(ventas));

        cuerpo.add(izquierda(Estilos.seccion("Ingresos por programa")));
        cuerpo.add(izquierda(panelProgramas));

        JPanel cartera = fila(4);
        cartera.add(Estilos.indicador("Vigentes", lblVigentes));
        cartera.add(Estilos.indicador("Por vencer (30 días)", lblPorVencer));
        cartera.add(Estilos.indicador("Vencidas sin renovar", lblVencidas));
        cartera.add(Estilos.indicador("Clientes activos", lblClientes));
        cuerpo.add(izquierda(Estilos.seccion("Estado actual de la cartera")));
        cuerpo.add(izquierda(cartera));

        JPanel arriba = new JPanel(new BorderLayout());
        arriba.setOpaque(false);
        arriba.add(cuerpo, BorderLayout.NORTH);
        add(arriba, BorderLayout.CENTER);
    }

    private static JPanel fila(int columnas) {
        JPanel panel = new JPanel(new GridLayout(1, columnas, 12, 0));
        panel.setOpaque(false);
        return panel;
    }

    private static Component izquierda(javax.swing.JComponent componente) {
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
        return componente;
    }

    private static String nombreMes(YearMonth mes) {
        return StringUtils.capitalize(mes.format(MES));
    }

    /**
     * Carga los meses que se pueden elegir.
     *
     * @param meses meses del más reciente al más antiguo
     */
    public void cargarMeses(List<YearMonth> meses) {
        cboMes.removeAllItems();
        meses.forEach(cboMes::addItem);
    }

    public JComboBox<YearMonth> getCboMes() {
        return cboMes;
    }

    /**
     * Muestra los datos del reporte.
     *
     * @param resumen datos calculados por el servicio de reportes
     */
    public void mostrar(ResumenMes resumen) {
        lblIngresos.setText(Estilos.soles(resumen.ingresos()));
        lblMeta.setText(Estilos.soles(resumen.meta()));
        barraMeta.setValue(Math.min(100, resumen.porcentajeMeta()));
        lblAvanceMeta.setText(resumen.porcentajeMeta() + " % de la meta alcanzado");
        lblVendidas.setText(String.valueOf(resumen.licenciasVendidas()));
        lblVigentes.setText(String.valueOf(resumen.vigentes()));
        lblPorVencer.setText(String.valueOf(resumen.porVencer()));
        lblPorVencer.setForeground(resumen.porVencer() > 0 ? Estilos.AMBAR : Estilos.PRIMARIO);
        lblVencidas.setText(String.valueOf(resumen.vencidas()));
        lblVencidas.setForeground(resumen.vencidas() > 0 ? Estilos.ROJO : Estilos.PRIMARIO);
        lblClientes.setText(String.valueOf(resumen.clientesActivos()));
        mostrarProgramas(resumen.ingresosPorPrograma());
    }

    private void mostrarProgramas(Map<String, BigDecimal> ingresos) {
        panelProgramas.removeAll();
        int maximo = ingresos.values().stream().mapToInt(BigDecimal::intValue).max().orElse(0);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 0, 4, 12);
        c.gridy = 0;
        for (Map.Entry<String, BigDecimal> fila : ingresos.entrySet()) {
            JLabel nombre = new JLabel(fila.getKey());
            JProgressBar barra = new JProgressBar(0, Math.max(maximo, 1));
            barra.setValue(fila.getValue().intValue());
            JLabel monto = new JLabel(Estilos.soles(fila.getValue()), SwingConstants.RIGHT);

            c.gridx = 0;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.WEST;
            panelProgramas.add(nombre, c);
            c.gridx = 1;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            panelProgramas.add(barra, c);
            c.gridx = 2;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.EAST;
            panelProgramas.add(monto, c);
            c.gridy++;
        }
        panelProgramas.revalidate();
        panelProgramas.repaint();
    }
}

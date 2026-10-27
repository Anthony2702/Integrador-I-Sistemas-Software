package com.autodev.licencias.vista;

import com.autodev.licencias.modelo.EstadoLicencia;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * Colores, fuentes y componentes con el estilo visual del sistema.
 */
public final class Estilos {

    public static final Color PRIMARIO = new Color(0x1E3A5F);
    public static final Color ACENTO = new Color(0x2F6FB2);
    public static final Color FONDO = new Color(0xF4F6F9);
    public static final Color BORDE = new Color(0xDDE3EA);
    public static final Color TEXTO_SUAVE = new Color(0x5F6B7A);
    public static final Color VERDE = new Color(0x2E7D32);
    public static final Color AMBAR = new Color(0xB26A00);
    public static final Color ROJO = new Color(0xC62828);

    public static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat SOLES = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));

    private Estilos() {
    }

    /** Aplica el tema FlatLaf claro con el color de AUTODEV. */
    public static void aplicarTema() {
        FlatLaf.setGlobalExtraDefaults(Map.of("@accentColor", "#2F6FB2"));
        FlatLightLaf.setup();
        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("Table.rowHeight", 28);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("TableHeader.height", 30);
        UIManager.put("defaultFont", new Font("SansSerif", Font.PLAIN, 13));
    }

    /** @return importe con formato "S/ 1,234.00" */
    public static String soles(BigDecimal monto) {
        return "S/ " + SOLES.format(monto);
    }

    /** @return fecha con formato dd/MM/yyyy */
    public static String fecha(LocalDate fecha) {
        return fecha == null ? "" : fecha.format(FECHA);
    }

    /** @return color con el que se pinta cada estado */
    public static Color colorEstado(EstadoLicencia estado) {
        return switch (estado) {
            case VIGENTE -> VERDE;
            case POR_VENCER -> AMBAR;
            case VENCIDA -> ROJO;
            case RENOVADA -> TEXTO_SUAVE;
        };
    }

    /**
     * Encabezado de cada pantalla: título, subtítulo y botones a la derecha.
     *
     * @param titulo título grande
     * @param subtitulo texto explicativo
     * @param acciones botones o filtros de la derecha
     * @return panel del encabezado
     */
    public static JPanel encabezado(String titulo, String subtitulo, JComponent... acciones) {
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 22f));
        lblTitulo.setForeground(PRIMARIO);
        JLabel lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setForeground(TEXTO_SUAVE);

        JPanel textos = new JPanel(new BorderLayout(0, 2));
        textos.setOpaque(false);
        textos.add(lblTitulo, BorderLayout.NORTH);
        textos.add(lblSubtitulo, BorderLayout.SOUTH);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        derecha.setOpaque(false);
        for (JComponent accion : acciones) {
            derecha.add(accion);
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        panel.add(textos, BorderLayout.WEST);
        panel.add(derecha, BorderLayout.EAST);
        return panel;
    }

    /** @return título pequeño para separar secciones */
    public static JLabel seccion(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(etiqueta.getFont().deriveFont(Font.BOLD, 15f));
        etiqueta.setForeground(PRIMARIO);
        etiqueta.setBorder(BorderFactory.createEmptyBorder(14, 0, 8, 0));
        return etiqueta;
    }

    /** @return borde de tarjeta blanca */
    public static Border bordeTarjeta() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16));
    }

    /** @return panel blanco con borde, para agrupar contenido */
    public static JPanel tarjeta(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(Color.WHITE);
        panel.setBorder(bordeTarjeta());
        return panel;
    }

    /**
     * Tarjeta con un indicador: título pequeño arriba y valor grande abajo.
     *
     * @param titulo nombre del indicador
     * @param valor etiqueta que muestra el valor
     * @return panel de la tarjeta
     */
    public static JPanel indicador(String titulo, JLabel valor) {
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(TEXTO_SUAVE);
        valor.setFont(valor.getFont().deriveFont(Font.BOLD, 24f));
        valor.setForeground(PRIMARIO);
        JPanel panel = tarjeta(new BorderLayout(0, 6));
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(valor, BorderLayout.CENTER);
        return panel;
    }

    /** @return botón azul para la acción principal de la pantalla */
    public static JButton botonPrincipal(String texto) {
        JButton boton = new JButton(texto);
        boton.putClientProperty(FlatClientProperties.STYLE,
                "background:#2F6FB2; foreground:#FFFFFF; hoverBackground:#28609B; pressedBackground:#1E3A5F;"
                        + " borderWidth:0; focusWidth:0; font:bold");
        return boton;
    }

    /** @return etiqueta de texto gris */
    public static JLabel nota(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setForeground(TEXTO_SUAVE);
        return etiqueta;
    }

    /** Configura una tabla de solo lectura con selección de una fila. */
    public static void tabla(JTable tabla) {
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setAutoCreateRowSorter(false);
    }
}

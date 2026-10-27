package com.autodev.licencias.vista;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Ventana principal: menú lateral a la izquierda y la pantalla elegida a la derecha.
 */
public class PrincipalVista extends JFrame {

    private static final long serialVersionUID = 1L;

    public static final String RESUMEN = "Resumen";
    public static final String VENTA = "Registrar venta";
    public static final String LICENCIAS = "Licencias";
    public static final String ALERTAS = "Alertas";
    public static final String CLIENTES = "Clientes";
    public static final String PROGRAMAS = "Programas";

    private static final String ESTILO_BOTON = "arc:10; borderWidth:0; focusWidth:0; innerFocusWidth:0;"
            + " foreground:#FFFFFF; hoverBackground:#2A4D7A; pressedBackground:#2F6FB2; margin:8,12,8,12;";

    private final CardLayout cartas = new CardLayout();
    private final JPanel contenido = new JPanel(cartas);
    private final JPanel menu = new JPanel();
    private final Map<String, JButton> botones = new LinkedHashMap<>();
    private final JButton btnCerrarSesion = new JButton("Cerrar sesión");
    private final JLabel lblUsuario = new JLabel();

    /**
     * @param nombreUsuario nombre del usuario que inició sesión
     * @param rol rol del usuario
     */
    public PrincipalVista(String nombreUsuario, String rol) {
        super("AUTODEV - Gestión de Licencias");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(Estilos.PRIMARIO);
        menu.setBorder(BorderFactory.createEmptyBorder(22, 14, 18, 14));
        menu.setPreferredSize(new Dimension(220, 0));

        JLabel marca = new JLabel("AUTODEV");
        marca.setFont(marca.getFont().deriveFont(Font.BOLD, 24f));
        marca.setForeground(Color.WHITE);
        JLabel sistema = new JLabel("Gestión de Licencias");
        sistema.setForeground(new Color(0xB8C7DA));
        menu.add(alinear(marca));
        menu.add(alinear(sistema));
        menu.add(Box.createVerticalStrut(28));

        lblUsuario.setText("<html><b>" + nombreUsuario + "</b><br>" + rol + "</html>");
        lblUsuario.setForeground(new Color(0xDCE4EE));
        btnCerrarSesion.putClientProperty(FlatClientProperties.STYLE, ESTILO_BOTON + " background:#16304F;");

        contenido.setBackground(Estilos.FONDO);
        add(menu, BorderLayout.WEST);
        add(contenido, BorderLayout.CENTER);
        setSize(1200, 740);
        setMinimumSize(new Dimension(1050, 660));
        setLocationRelativeTo(null);
    }

    private static JComponent alinear(JComponent componente) {
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
        return componente;
    }

    /**
     * Agrega una pantalla y su botón en el menú.
     *
     * @param nombre nombre de la pantalla (también es el texto del botón)
     * @param panel contenido de la pantalla
     */
    public void agregarPantalla(String nombre, JComponent panel) {
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        panel.setBackground(Estilos.FONDO);
        contenido.add(panel, nombre);

        JButton boton = new JButton(nombre);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        boton.setFont(boton.getFont().deriveFont(14f));
        alinear(boton);
        botones.put(nombre, boton);
        marcar(boton, false);
        menu.add(boton);
        menu.add(Box.createVerticalStrut(4));
    }

    /** Agrega al final del menú el usuario y el botón para cerrar sesión. */
    public void terminarMenu() {
        menu.add(Box.createVerticalGlue());
        menu.add(alinear(lblUsuario));
        menu.add(Box.createVerticalStrut(10));
        btnCerrarSesion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        menu.add(alinear(btnCerrarSesion));
    }

    /**
     * Muestra una pantalla y resalta su botón en el menú.
     *
     * @param nombre nombre de la pantalla
     */
    public void mostrar(String nombre) {
        cartas.show(contenido, nombre);
        botones.forEach((clave, boton) -> marcar(boton, clave.equals(nombre)));
    }

    private void marcar(JButton boton, boolean seleccionado) {
        String fondo = seleccionado ? "#2F6FB2" : "#1E3A5F";
        boton.putClientProperty(FlatClientProperties.STYLE, ESTILO_BOTON + " background:" + fondo + ";"
                + (seleccionado ? " font:bold;" : ""));
    }

    public JButton getBoton(String nombre) {
        return botones.get(nombre);
    }

    public JButton getBtnCerrarSesion() {
        return btnCerrarSesion;
    }

    /** Cambia el texto de un botón del menú (por ejemplo, "Alertas (3)"). */
    public void setTextoBoton(String nombre, String texto) {
        botones.get(nombre).setText(texto);
    }
}

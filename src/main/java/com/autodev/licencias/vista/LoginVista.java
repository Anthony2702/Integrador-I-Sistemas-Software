package com.autodev.licencias.vista;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Pantalla de inicio de sesión (vista del patrón MVC: solo muestra componentes).
 */
public class LoginVista extends JFrame {

    private static final long serialVersionUID = 1L;

    private final JTextField txtUsuario = new JTextField(20);
    private final JPasswordField txtClave = new JPasswordField(20);
    private final JButton btnIngresar = Estilos.botonPrincipal("Ingresar");
    private final JLabel lblMensaje = new JLabel(" ", SwingConstants.CENTER);

    public LoginVista() {
        super("AUTODEV - Licencias");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel marca = new JLabel("AUTODEV", SwingConstants.CENTER);
        marca.setFont(marca.getFont().deriveFont(Font.BOLD, 30f));
        marca.setForeground(Estilos.PRIMARIO);
        JLabel sistema = new JLabel("Gestión de Licencias", SwingConstants.CENTER);
        sistema.setForeground(Estilos.TEXTO_SUAVE);
        JLabel titulo = new JLabel("Iniciar sesión");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));

        txtUsuario.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "usuario");
        txtClave.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "contraseña");
        txtClave.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true");
        lblMensaje.setForeground(Estilos.ROJO);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 40, 24, 40));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(4, 0, 4, 0);
        c.gridy = 0;
        panel.add(marca, c);
        c.gridy = 1;
        panel.add(sistema, c);
        c.gridy = 2;
        c.insets = new Insets(22, 0, 8, 0);
        panel.add(titulo, c);
        c.insets = new Insets(4, 0, 4, 0);
        c.gridy = 3;
        panel.add(new JLabel("Usuario"), c);
        c.gridy = 4;
        panel.add(txtUsuario, c);
        c.gridy = 5;
        panel.add(new JLabel("Contraseña"), c);
        c.gridy = 6;
        panel.add(txtClave, c);
        c.gridy = 7;
        c.insets = new Insets(16, 0, 4, 0);
        panel.add(btnIngresar, c);
        c.gridy = 8;
        c.insets = new Insets(8, 0, 0, 0);
        panel.add(lblMensaje, c);

        setContentPane(panel);
        getRootPane().setDefaultButton(btnIngresar);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public JTextField getTxtUsuario() {
        return txtUsuario;
    }

    public JPasswordField getTxtClave() {
        return txtClave;
    }

    public JButton getBtnIngresar() {
        return btnIngresar;
    }

    /** Muestra un mensaje en rojo debajo del botón. */
    public void mostrarMensaje(String mensaje) {
        lblMensaje.setText(mensaje);
    }

    /** Borra la contraseña escrita. */
    public void limpiarClave() {
        txtClave.setText("");
    }

    /** Bloquea o desbloquea los campos (después de varios intentos fallidos). */
    public void setBloqueado(boolean bloqueado) {
        txtUsuario.setEnabled(!bloqueado);
        txtClave.setEnabled(!bloqueado);
        btnIngresar.setEnabled(!bloqueado);
    }
}

package com.autodev.licencias.vista;

import com.autodev.licencias.modelo.Cliente;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Ventana para registrar o editar un cliente.
 */
public class ClienteDialogo extends JDialog {

    private static final long serialVersionUID = 1L;

    private final JTextField txtNombre = new JTextField(28);
    private final JTextField txtRuc = new JTextField(28);
    private final JTextField txtContacto = new JTextField(28);
    private final JButton btnGuardar = Estilos.botonPrincipal("Guardar");
    private final JButton btnCancelar = new JButton("Cancelar");

    /**
     * @param padre ventana desde la que se abre
     * @param cliente cliente a editar, o null para uno nuevo
     */
    public ClienteDialogo(Window padre, Cliente cliente) {
        super(padre, cliente == null ? "Nuevo cliente" : "Editar cliente", ModalityType.APPLICATION_MODAL);
        if (cliente != null) {
            txtNombre.setText(cliente.getNombre());
            txtRuc.setText(cliente.getRuc());
            txtContacto.setText(cliente.getContacto());
        }

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(18, 20, 8, 20));
        agregar(formulario, 0, "Nombre o razón social *", txtNombre);
        agregar(formulario, 1, "RUC (11 dígitos, opcional)", txtRuc);
        agregar(formulario, 2, "Contacto (celular o correo)", txtContacto);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.setBorder(BorderFactory.createEmptyBorder(0, 16, 12, 16));
        botones.add(btnCancelar);
        botones.add(btnGuardar);
        btnCancelar.addActionListener(e -> dispose());

        add(formulario, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnGuardar);
        pack();
        setResizable(false);
        setLocationRelativeTo(padre);
    }

    private void agregar(JPanel panel, int fila, String etiqueta, JTextField campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = fila * 2;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(6, 0, 2, 0);
        panel.add(new JLabel(etiqueta), c);
        c.gridy = fila * 2 + 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 4, 0);
        panel.add(campo, c);
    }

    /**
     * Acción del botón Guardar. El controlador la define y cierra la ventana si todo sale bien.
     *
     * @param accion lo que se hace al guardar
     */
    public void alGuardar(Runnable accion) {
        btnGuardar.addActionListener(e -> accion.run());
    }

    public String getNombre() {
        return txtNombre.getText();
    }

    public String getRuc() {
        return txtRuc.getText();
    }

    public String getContacto() {
        return txtContacto.getText();
    }
}

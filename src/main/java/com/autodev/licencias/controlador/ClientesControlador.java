package com.autodev.licencias.controlador;

import com.autodev.licencias.Contexto;
import com.autodev.licencias.modelo.Cliente;
import com.autodev.licencias.vista.ClienteDialogo;
import com.autodev.licencias.vista.ClientesPanel;
import com.autodev.licencias.vista.Dialogos;
import javax.swing.SwingUtilities;

/**
 * Controlador de la pantalla "Clientes".
 */
public class ClientesControlador {

    private final ClientesPanel vista;
    private final Contexto contexto;

    public ClientesControlador(ClientesPanel vista, Contexto contexto) {
        this.vista = vista;
        this.contexto = contexto;
        vista.getBtnNuevo().addActionListener(e -> abrirDialogo(null));
        vista.getBtnEditar().addActionListener(e -> editar());
    }

    /** Vuelve a leer los clientes. */
    public void refrescar() {
        vista.cargar(contexto.getClientes().listar());
    }

    private void editar() {
        Cliente cliente = vista.getClienteSeleccionado();
        if (cliente == null) {
            Dialogos.aviso(vista, "Selecciona el cliente que quieres editar");
            return;
        }
        abrirDialogo(cliente);
    }

    /** Abre la ventana del cliente; si cliente es null, registra uno nuevo. */
    private void abrirDialogo(Cliente cliente) {
        ClienteDialogo dialogo = new ClienteDialogo(SwingUtilities.getWindowAncestor(vista), cliente);
        dialogo.alGuardar(() -> {
            try {
                if (cliente == null) {
                    contexto.getClientes().registrar(dialogo.getNombre(), dialogo.getRuc(), dialogo.getContacto());
                } else {
                    contexto.getClientes().actualizar(cliente, dialogo.getNombre(), dialogo.getRuc(),
                            dialogo.getContacto());
                }
                dialogo.dispose();
                refrescar();
            } catch (RuntimeException e) {
                Dialogos.error(dialogo, e);
            }
        });
        dialogo.setVisible(true);
    }
}

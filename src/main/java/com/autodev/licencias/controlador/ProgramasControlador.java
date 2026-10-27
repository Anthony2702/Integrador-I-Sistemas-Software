package com.autodev.licencias.controlador;

import com.autodev.licencias.Contexto;
import com.autodev.licencias.modelo.Programa;
import com.autodev.licencias.modelo.Usuario;
import com.autodev.licencias.vista.Dialogos;
import com.autodev.licencias.vista.ProgramasPanel;
import java.math.BigDecimal;
import javax.swing.JOptionPane;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Controlador de la pantalla "Programas".
 */
public class ProgramasControlador {

    private final ProgramasPanel vista;
    private final Contexto contexto;
    private final Usuario usuario;

    public ProgramasControlador(ProgramasPanel vista, Contexto contexto, Usuario usuario) {
        this.vista = vista;
        this.contexto = contexto;
        this.usuario = usuario;
        vista.getBtnCambiarPrecio().addActionListener(e -> cambiarPrecio());
    }

    /** Vuelve a leer los programas. */
    public void refrescar() {
        vista.cargar(contexto.getProgramas().listar());
    }

    private void cambiarPrecio() {
        Programa programa = vista.getProgramaSeleccionado();
        if (programa == null) {
            Dialogos.aviso(vista, "Selecciona un programa");
            return;
        }
        String texto = (String) JOptionPane.showInputDialog(vista, "Nuevo precio de " + programa.getNombre() + " (S/):",
                "Cambiar precio", JOptionPane.QUESTION_MESSAGE, null, null, programa.getPrecio().toPlainString());
        if (texto == null) {
            return;
        }
        String limpio = StringUtils.trimToEmpty(texto).replace(",", ".");
        if (!NumberUtils.isCreatable(limpio)) {
            Dialogos.aviso(vista, "Ingresa un precio válido");
            return;
        }
        try {
            contexto.getProgramas().cambiarPrecio(usuario, programa, new BigDecimal(limpio));
            refrescar();
        } catch (RuntimeException e) {
            Dialogos.error(vista, e);
        }
    }
}

package com.autodev.licencias.controlador;

import com.autodev.licencias.modelo.Usuario;
import com.autodev.licencias.servicio.AutenticacionServicio;
import com.autodev.licencias.vista.Dialogos;
import com.autodev.licencias.vista.LoginVista;
import java.util.Optional;
import java.util.function.Consumer;
import javax.swing.Timer;

/**
 * Controlador del login: valida los datos con el servicio y, tras 3 intentos fallidos,
 * bloquea la pantalla 30 segundos.
 */
public class LoginControlador {

    private static final int MAX_INTENTOS = 3;
    private static final int SEGUNDOS_BLOQUEO = 30;

    private final LoginVista vista;
    private final AutenticacionServicio servicio;
    private final Consumer<Usuario> alIngresar;
    private int intentosFallidos;

    /**
     * @param vista pantalla de login
     * @param servicio servicio de autenticación
     * @param alIngresar acción que se ejecuta con el usuario cuando el login es correcto
     */
    public LoginControlador(LoginVista vista, AutenticacionServicio servicio, Consumer<Usuario> alIngresar) {
        this.vista = vista;
        this.servicio = servicio;
        this.alIngresar = alIngresar;
        vista.getBtnIngresar().addActionListener(e -> ingresar());
    }

    /** Muestra la pantalla de login. */
    public void iniciar() {
        vista.setVisible(true);
    }

    private void ingresar() {
        try {
            Optional<Usuario> usuario = servicio.iniciarSesion(vista.getTxtUsuario().getText(),
                    vista.getTxtClave().getPassword());
            vista.limpiarClave();
            if (usuario.isPresent()) {
                vista.dispose();
                alIngresar.accept(usuario.get());
                return;
            }
            intentosFallidos++;
            if (intentosFallidos >= MAX_INTENTOS) {
                bloquear();
            } else {
                vista.mostrarMensaje("Usuario o contraseña incorrectos (intento "
                        + intentosFallidos + " de " + MAX_INTENTOS + ")");
            }
        } catch (RuntimeException e) {
            Dialogos.error(vista, e);
        }
    }

    private void bloquear() {
        intentosFallidos = 0;
        vista.setBloqueado(true);
        vista.mostrarMensaje("Demasiados intentos. Espera " + SEGUNDOS_BLOQUEO + " segundos.");
        Timer temporizador = new Timer(SEGUNDOS_BLOQUEO * 1000, e -> {
            vista.setBloqueado(false);
            vista.mostrarMensaje(" ");
        });
        temporizador.setRepeats(false);
        temporizador.start();
    }
}

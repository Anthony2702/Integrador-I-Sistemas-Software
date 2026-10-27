package com.autodev.licencias;

import com.autodev.licencias.config.Configuracion;
import com.autodev.licencias.controlador.LoginControlador;
import com.autodev.licencias.controlador.PrincipalControlador;
import com.autodev.licencias.vista.Estilos;
import com.autodev.licencias.vista.LoginVista;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Punto de entrada del sistema AUTODEV - Gestión de Licencias.
 */
public final class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    private App() {
    }

    /**
     * Carga la configuración, comprueba la base de datos y muestra el login.
     *
     * @param args no se usan
     */
    public static void main(String[] args) {
        Estilos.aplicarTema();
        SwingUtilities.invokeLater(App::iniciar);
    }

    private static void iniciar() {
        try {
            Configuracion configuracion = Configuracion.cargar();
            Contexto contexto = new Contexto(configuracion);
            if (!contexto.getConexion().probar()) {
                salirConError("No se pudo conectar a la base de datos.\n"
                        + "Revisa que MySQL esté encendido y los datos de config.properties.");
                return;
            }
            contexto.getAutenticacion().crearUsuariosIniciales(
                    configuracion.claveInicialAdmin(), configuracion.claveInicialVendedor());
            log.info("Sistema iniciado");
            mostrarLogin(contexto);
        } catch (IllegalStateException e) {
            salirConError(e.getMessage());
        }
    }

    private static void mostrarLogin(Contexto contexto) {
        LoginControlador login = new LoginControlador(new LoginVista(), contexto.getAutenticacion(),
                usuario -> new PrincipalControlador(contexto, usuario, () -> mostrarLogin(contexto)).iniciar());
        login.iniciar();
    }

    private static void salirConError(String mensaje) {
        log.error(mensaje);
        JOptionPane.showMessageDialog(null, mensaje, "AUTODEV - Licencias", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}

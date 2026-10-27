package com.autodev.licencias.controlador;

import com.autodev.licencias.Contexto;
import com.autodev.licencias.modelo.Licencia;
import com.autodev.licencias.modelo.Usuario;
import com.autodev.licencias.vista.AlertasPanel;
import com.autodev.licencias.vista.ClientesPanel;
import com.autodev.licencias.vista.Dialogos;
import com.autodev.licencias.vista.LicenciasPanel;
import com.autodev.licencias.vista.PrincipalVista;
import com.autodev.licencias.vista.ProgramasPanel;
import com.autodev.licencias.vista.ResumenPanel;
import com.autodev.licencias.vista.VentaPanel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador de la ventana principal: arma las pantallas, maneja el menú
 * y revisa los vencimientos una vez al día.
 */
public class PrincipalControlador implements Navegacion {

    private static final Logger log = LoggerFactory.getLogger(PrincipalControlador.class);

    private final Contexto contexto;
    private final Usuario usuario;
    private final Runnable alCerrarSesion;
    private final PrincipalVista vista;
    private final Map<String, Runnable> refrescos = new HashMap<>();
    private final VentaControlador venta;
    private ScheduledExecutorService revisionDiaria;

    /**
     * @param contexto servicios del sistema
     * @param usuario usuario que inició sesión
     * @param alCerrarSesion acción para volver al login
     */
    public PrincipalControlador(Contexto contexto, Usuario usuario, Runnable alCerrarSesion) {
        this.contexto = contexto;
        this.usuario = usuario;
        this.alCerrarSesion = alCerrarSesion;
        this.vista = new PrincipalVista(usuario.getNombre(), usuario.getRol().getEtiqueta());

        ResumenPanel resumenPanel = new ResumenPanel();
        ResumenControlador resumen = new ResumenControlador(resumenPanel, contexto);
        VentaPanel ventaPanel = new VentaPanel();
        venta = new VentaControlador(ventaPanel, contexto, this);
        LicenciasPanel licenciasPanel = new LicenciasPanel();
        LicenciasControlador licencias = new LicenciasControlador(licenciasPanel, contexto, this);
        AlertasPanel alertasPanel = new AlertasPanel();
        AlertasControlador alertas = new AlertasControlador(alertasPanel, contexto, this);
        ClientesPanel clientesPanel = new ClientesPanel();
        ClientesControlador clientes = new ClientesControlador(clientesPanel, contexto);
        ProgramasPanel programasPanel = new ProgramasPanel(usuario.esAdmin());
        ProgramasControlador programas = new ProgramasControlador(programasPanel, contexto, usuario);

        registrar(PrincipalVista.RESUMEN, resumenPanel, resumen::refrescar);
        registrar(PrincipalVista.VENTA, ventaPanel, venta::refrescar);
        registrar(PrincipalVista.LICENCIAS, licenciasPanel, licencias::refrescar);
        registrar(PrincipalVista.ALERTAS, alertasPanel, alertas::refrescar);
        registrar(PrincipalVista.CLIENTES, clientesPanel, clientes::refrescar);
        registrar(PrincipalVista.PROGRAMAS, programasPanel, programas::refrescar);
        vista.terminarMenu();
        vista.getBtnCerrarSesion().addActionListener(e -> cerrarSesion());
    }

    private void registrar(String nombre, JComponent panel, Runnable refrescar) {
        vista.agregarPantalla(nombre, panel);
        refrescos.put(nombre, refrescar);
        vista.getBoton(nombre).addActionListener(e -> irA(nombre));
    }

    /** Genera las alertas del día, muestra el Resumen y programa la revisión diaria. */
    public void iniciar() {
        try {
            contexto.getAlertas().generarAlertas();
        } catch (RuntimeException e) {
            Dialogos.error(vista, e);
        }
        alertasCambiaron();
        irA(PrincipalVista.RESUMEN);
        vista.setVisible(true);
        programarRevisionDiaria();
    }

    /**
     * Muestra una pantalla con sus datos actualizados.
     *
     * @param nombre nombre de la pantalla
     */
    public void irA(String nombre) {
        try {
            refrescos.get(nombre).run();
        } catch (RuntimeException e) {
            Dialogos.error(vista, e);
        }
        vista.mostrar(nombre);
    }

    /** @return la ventana principal */
    public PrincipalVista getVista() {
        return vista;
    }

    @Override
    public void renovar(Licencia licencia) {
        venta.prepararRenovacion(licencia);
        vista.mostrar(PrincipalVista.VENTA);
    }

    @Override
    public void alertasCambiaron() {
        try {
            int pendientes = contexto.getAlertas().listarPendientes().size();
            vista.setTextoBoton(PrincipalVista.ALERTAS,
                    pendientes > 0 ? "Alertas (" + pendientes + ")" : "Alertas");
        } catch (RuntimeException e) {
            log.error("No se pudo contar las alertas", e);
        }
    }

    /** Mientras el sistema está abierto, revisa los vencimientos cada 24 horas. */
    private void programarRevisionDiaria() {
        revisionDiaria = Executors.newSingleThreadScheduledExecutor(tarea -> {
            Thread hilo = new Thread(tarea, "revision-vencimientos");
            hilo.setDaemon(true);
            return hilo;
        });
        revisionDiaria.scheduleAtFixedRate(() -> {
            try {
                contexto.getAlertas().generarAlertas();
                SwingUtilities.invokeLater(this::alertasCambiaron);
            } catch (RuntimeException e) {
                log.error("Falló la revisión diaria de vencimientos", e);
            }
        }, 24, 24, TimeUnit.HOURS);
    }

    private void cerrarSesion() {
        if (!Dialogos.confirmar(vista, "¿Cerrar la sesión?")) {
            return;
        }
        revisionDiaria.shutdownNow();
        log.info("Cierre de sesión: {}", usuario.getUsuario());
        vista.dispose();
        alCerrarSesion.run();
    }
}

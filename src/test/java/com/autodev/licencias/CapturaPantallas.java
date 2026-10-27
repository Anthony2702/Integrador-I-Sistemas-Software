package com.autodev.licencias;

import com.autodev.licencias.config.Configuracion;
import com.autodev.licencias.controlador.PrincipalControlador;
import com.autodev.licencias.modelo.EstadoLicencia;
import com.autodev.licencias.modelo.Licencia;
import com.autodev.licencias.modelo.Usuario;
import com.autodev.licencias.vista.ClienteDialogo;
import com.autodev.licencias.vista.Estilos;
import com.autodev.licencias.vista.LoginVista;
import com.autodev.licencias.vista.PrincipalVista;
import com.autodev.licencias.vista.VentaPanel;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

/**
 * Herramienta de documentación: genera capturas PNG de las pantallas reales con los datos
 * de la base de demostración. No forma parte del sistema (está en src/test).
 *
 * <p>Uso: {@code java ... com.autodev.licencias.CapturaPantallas docs/capturas}</p>
 */
public final class CapturaPantallas {

    private static final int ESCALA = 2;

    private CapturaPantallas() {
    }

    public static void main(String[] args) throws Exception {
        Path carpeta = Path.of(args.length > 0 ? args[0] : "docs/capturas");
        Files.createDirectories(carpeta);
        Estilos.aplicarTema();
        int codigo = 0;
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    capturar(carpeta);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            codigo = 1;
        }
        System.exit(codigo);
    }

    private static void capturar(Path carpeta) throws IOException {
        Configuracion configuracion = Configuracion.cargar();
        Contexto contexto = new Contexto(configuracion);
        contexto.getAutenticacion().crearUsuariosIniciales(configuracion.claveInicialAdmin(),
                configuracion.claveInicialVendedor());

        LoginVista login = new LoginVista();
        login.getTxtUsuario().setText("admin");
        login.getTxtClave().setText("********");
        guardar(login, carpeta.resolve("01_login.png"));

        Usuario admin = contexto.getAutenticacion()
                .iniciarSesion("admin", configuracion.claveInicialAdmin().toCharArray()).orElseThrow();
        PrincipalControlador principal = new PrincipalControlador(contexto, admin, () -> { });
        contexto.getAlertas().generarAlertas();
        principal.alertasCambiaron();
        PrincipalVista ventana = principal.getVista();

        String[][] pantallas = {
            {PrincipalVista.RESUMEN, "02_resumen.png"},
            {PrincipalVista.VENTA, "03_registrar_venta.png"},
            {PrincipalVista.LICENCIAS, "04_licencias.png"},
            {PrincipalVista.ALERTAS, "05_alertas.png"},
            {PrincipalVista.CLIENTES, "06_clientes.png"},
            {PrincipalVista.PROGRAMAS, "07_programas.png"},
        };
        for (String[] pantalla : pantallas) {
            principal.irA(pantalla[0]);
            if (PrincipalVista.VENTA.equals(pantalla[0])) {
                // Venta de ejemplo para que se vea el total y el vencimiento calculados
                VentaPanel venta = buscar(ventana.getContentPane(), VentaPanel.class);
                venta.getCboCliente().setSelectedIndex(0);
                venta.getTxtDescuento().setText("10");
            }
            guardar(ventana, carpeta.resolve(pantalla[1]));
        }

        Licencia porVencer = contexto.getLicencias().listar().stream()
                .filter(l -> l.getEstado() == EstadoLicencia.POR_VENCER).findFirst().orElseThrow();
        principal.renovar(porVencer);
        guardar(ventana, carpeta.resolve("03b_renovacion.png"));

        ClienteDialogo dialogo = new ClienteDialogo(ventana, null);
        guardar(dialogo, carpeta.resolve("08_nuevo_cliente.png"));
    }

    private static <T> T buscar(Container contenedor, Class<T> tipo) {
        for (Component hijo : contenedor.getComponents()) {
            if (tipo.isInstance(hijo)) {
                return tipo.cast(hijo);
            }
            if (hijo instanceof Container interno) {
                T encontrado = buscar(interno, tipo);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }
        return null;
    }

    private static void guardar(Window ventana, Path archivo) throws IOException {
        ventana.addNotify();
        ventana.validate();
        javax.swing.JRootPane raiz = ((RootPaneContainer) ventana).getRootPane();
        raiz.setSize(raiz.getPreferredSize().width > ventana.getWidth() ? raiz.getPreferredSize() : ventana.getSize());
        raiz.doLayout();
        raiz.validate();
        int ancho = raiz.getWidth();
        int alto = raiz.getHeight();
        BufferedImage imagen = new BufferedImage(ancho * ESCALA, alto * ESCALA, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagen.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.scale(ESCALA, ESCALA);
        raiz.printAll(g);
        g.dispose();
        ImageIO.write(imagen, "png", archivo.toFile());
        System.out.println("Captura: " + archivo);
    }
}

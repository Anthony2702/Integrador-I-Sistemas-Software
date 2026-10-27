package com.autodev.licencias.controlador;

import com.autodev.licencias.Contexto;
import com.autodev.licencias.modelo.EstadoLicencia;
import com.autodev.licencias.modelo.Licencia;
import com.autodev.licencias.vista.Dialogos;
import com.autodev.licencias.vista.LicenciasPanel;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador de la pantalla "Licencias".
 */
public class LicenciasControlador {

    private static final Logger log = LoggerFactory.getLogger(LicenciasControlador.class);

    private final LicenciasPanel vista;
    private final Contexto contexto;
    private final Navegacion navegacion;

    public LicenciasControlador(LicenciasPanel vista, Contexto contexto, Navegacion navegacion) {
        this.vista = vista;
        this.contexto = contexto;
        this.navegacion = navegacion;
        vista.getBtnRenovar().addActionListener(e -> renovar());
        vista.getBtnExportar().addActionListener(e -> exportar());
    }

    /** Vuelve a leer las licencias con su estado actualizado. */
    public void refrescar() {
        vista.cargar(contexto.getLicencias().listar(), contexto.getProgramas().listar(),
                contexto.getLicencias().hoy());
    }

    private void renovar() {
        Licencia licencia = vista.getLicenciaSeleccionada();
        if (licencia == null) {
            Dialogos.aviso(vista, "Selecciona la licencia que quieres renovar");
            return;
        }
        if (licencia.getEstado() == EstadoLicencia.RENOVADA) {
            Dialogos.aviso(vista, "Esa licencia ya fue renovada");
            return;
        }
        navegacion.renovar(licencia);
    }

    private void exportar() {
        List<Licencia> visibles = vista.getLicenciasVisibles();
        if (visibles.isEmpty()) {
            Dialogos.aviso(vista, "No hay licencias para exportar con los filtros actuales");
            return;
        }
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new FileNameExtensionFilter("Libro de Excel (*.xlsx)", "xlsx"));
        String nombre = "licencias_" + contexto.getLicencias().hoy().format(DateTimeFormatter.BASIC_ISO_DATE) + ".xlsx";
        selector.setSelectedFile(new File(System.getProperty("user.home"), nombre));
        if (selector.showSaveDialog(vista) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File archivo = selector.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".xlsx")) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + ".xlsx");
        }
        try {
            Path destino = archivo.toPath();
            contexto.getExportador().exportarLicencias(visibles, contexto.getLicencias().hoy(), destino);
            if (Dialogos.confirmar(vista, "Se exportaron " + visibles.size() + " licencias.\n¿Abrir el archivo?")
                    && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivo);
            }
        } catch (IOException e) {
            log.error("No se pudo exportar a Excel", e);
            Dialogos.aviso(vista, "No se pudo guardar el archivo. ¿Está abierto en Excel?");
        }
    }
}

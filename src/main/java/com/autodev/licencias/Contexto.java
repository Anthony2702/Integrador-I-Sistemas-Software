package com.autodev.licencias;

import com.autodev.licencias.config.Configuracion;
import com.autodev.licencias.dao.AlertaDAO;
import com.autodev.licencias.dao.ClienteDAO;
import com.autodev.licencias.dao.LicenciaDAO;
import com.autodev.licencias.dao.ProgramaDAO;
import com.autodev.licencias.dao.UsuarioDAO;
import com.autodev.licencias.dao.mysql.AlertaDAOMySQL;
import com.autodev.licencias.dao.mysql.ClienteDAOMySQL;
import com.autodev.licencias.dao.mysql.ConexionBD;
import com.autodev.licencias.dao.mysql.LicenciaDAOMySQL;
import com.autodev.licencias.dao.mysql.ProgramaDAOMySQL;
import com.autodev.licencias.dao.mysql.UsuarioDAOMySQL;
import com.autodev.licencias.servicio.AlertaServicio;
import com.autodev.licencias.servicio.AutenticacionServicio;
import com.autodev.licencias.servicio.ClienteServicio;
import com.autodev.licencias.servicio.ExportadorExcel;
import com.autodev.licencias.servicio.LicenciaServicio;
import com.autodev.licencias.servicio.ProgramaServicio;
import com.autodev.licencias.servicio.ReporteServicio;
import java.time.Clock;

/**
 * Arma las dependencias del sistema en un solo lugar: crea los DAO de MySQL y se los pasa
 * a los servicios por constructor (inyección de dependencias manual).
 * Si mañana se cambia MySQL por otra base, solo se cambia esta clase.
 */
public final class Contexto {

    private final ConexionBD conexion;
    private final AutenticacionServicio autenticacion;
    private final ClienteServicio clientes;
    private final ProgramaServicio programas;
    private final LicenciaServicio licencias;
    private final AlertaServicio alertas;
    private final ReporteServicio reportes;
    private final ExportadorExcel exportador;

    /**
     * @param configuracion datos leídos de config.properties
     */
    public Contexto(Configuracion configuracion) {
        this.conexion = new ConexionBD(configuracion);
        Clock reloj = Clock.systemDefaultZone();

        UsuarioDAO usuarioDAO = new UsuarioDAOMySQL(conexion);
        ClienteDAO clienteDAO = new ClienteDAOMySQL(conexion);
        ProgramaDAO programaDAO = new ProgramaDAOMySQL(conexion);
        LicenciaDAO licenciaDAO = new LicenciaDAOMySQL(conexion);
        AlertaDAO alertaDAO = new AlertaDAOMySQL(conexion);

        this.autenticacion = new AutenticacionServicio(usuarioDAO);
        this.clientes = new ClienteServicio(clienteDAO);
        this.programas = new ProgramaServicio(programaDAO);
        this.licencias = new LicenciaServicio(licenciaDAO, alertaDAO, reloj, configuracion.diasAviso());
        this.alertas = new AlertaServicio(licencias, alertaDAO, reloj);
        this.reportes = new ReporteServicio(licencias, programaDAO, configuracion.metaMensual());
        this.exportador = new ExportadorExcel();
    }

    public ConexionBD getConexion() {
        return conexion;
    }

    public AutenticacionServicio getAutenticacion() {
        return autenticacion;
    }

    public ClienteServicio getClientes() {
        return clientes;
    }

    public ProgramaServicio getProgramas() {
        return programas;
    }

    public LicenciaServicio getLicencias() {
        return licencias;
    }

    public AlertaServicio getAlertas() {
        return alertas;
    }

    public ReporteServicio getReportes() {
        return reportes;
    }

    public ExportadorExcel getExportador() {
        return exportador;
    }
}

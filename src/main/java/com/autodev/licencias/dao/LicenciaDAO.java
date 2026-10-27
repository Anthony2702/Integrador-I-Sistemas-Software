package com.autodev.licencias.dao;

import com.autodev.licencias.modelo.EstadoLicencia;
import com.autodev.licencias.modelo.Licencia;
import java.util.List;

/**
 * Acceso a datos de las licencias (cada venta genera una licencia).
 */
public interface LicenciaDAO {

    /**
     * Guarda una licencia nueva y le asigna su id.
     *
     * @param licencia licencia a guardar
     * @return la misma licencia con su id
     */
    Licencia guardar(Licencia licencia);

    /**
     * Cambia el estado de una licencia.
     *
     * @param idLicencia id de la licencia
     * @param estado estado nuevo
     */
    void actualizarEstado(int idLicencia, EstadoLicencia estado);

    /** @return todas las licencias con su cliente y programa, de la más reciente a la más antigua */
    List<Licencia> listar();
}

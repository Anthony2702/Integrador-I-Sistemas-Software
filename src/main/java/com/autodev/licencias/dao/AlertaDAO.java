package com.autodev.licencias.dao;

import com.autodev.licencias.modelo.Alerta;
import java.util.List;

/**
 * Acceso a datos de las alertas de vencimiento.
 */
public interface AlertaDAO {

    /**
     * Guarda una alerta nueva y le asigna su id.
     *
     * @param alerta alerta a guardar
     * @return la misma alerta con su id
     */
    Alerta guardar(Alerta alerta);

    /**
     * @param idLicencia id de la licencia
     * @return true si la licencia ya tiene una alerta (atendida o no)
     */
    boolean existeParaLicencia(int idLicencia);

    /** @return alertas que el vendedor todavía no atiende, con su licencia, cliente y programa */
    List<Alerta> listarPendientes();

    /**
     * Marca una alerta como atendida.
     *
     * @param idAlerta id de la alerta
     */
    void marcarAtendida(int idAlerta);

    /**
     * Marca como atendidas todas las alertas de una licencia (por ejemplo, al renovarla).
     *
     * @param idLicencia id de la licencia
     */
    void atenderPorLicencia(int idLicencia);
}

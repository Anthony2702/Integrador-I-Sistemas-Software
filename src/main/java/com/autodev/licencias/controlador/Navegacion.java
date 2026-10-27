package com.autodev.licencias.controlador;

import com.autodev.licencias.modelo.Licencia;

/**
 * Acciones de navegación que una pantalla le pide a la ventana principal.
 * Así los controladores no dependen directamente de la ventana principal.
 */
public interface Navegacion {

    /**
     * Abre la pantalla de venta con los datos de la licencia a renovar.
     *
     * @param licencia licencia que se va a renovar
     */
    void renovar(Licencia licencia);

    /** Avisa que cambió la cantidad de alertas pendientes (para actualizar el menú). */
    void alertasCambiaron();
}

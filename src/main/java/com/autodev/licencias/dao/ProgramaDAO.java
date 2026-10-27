package com.autodev.licencias.dao;

import com.autodev.licencias.modelo.Programa;
import java.math.BigDecimal;
import java.util.List;

/**
 * Acceso a datos de los programas de AUTODEV.
 */
public interface ProgramaDAO {

    /** @return todos los programas en el orden en que se registraron */
    List<Programa> listar();

    /**
     * Cambia el precio de lista de un programa.
     *
     * @param idPrograma id del programa
     * @param precio precio nuevo
     */
    void actualizarPrecio(int idPrograma, BigDecimal precio);
}

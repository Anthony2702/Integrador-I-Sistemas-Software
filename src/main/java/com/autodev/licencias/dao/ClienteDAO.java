package com.autodev.licencias.dao;

import com.autodev.licencias.modelo.Cliente;
import java.util.List;
import java.util.Optional;

/**
 * Acceso a datos de los clientes.
 */
public interface ClienteDAO {

    /**
     * Guarda un cliente nuevo y le asigna su id.
     *
     * @param cliente cliente a guardar
     * @return el mismo cliente con su id
     */
    Cliente guardar(Cliente cliente);

    /**
     * Actualiza nombre, RUC y contacto de un cliente existente.
     *
     * @param cliente cliente con los datos nuevos
     */
    void actualizar(Cliente cliente);

    /**
     * @param ruc RUC de 11 dígitos
     * @return el cliente con ese RUC, si existe
     */
    Optional<Cliente> buscarPorRuc(String ruc);

    /** @return todos los clientes ordenados por nombre */
    List<Cliente> listar();
}

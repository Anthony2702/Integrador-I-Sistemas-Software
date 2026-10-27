package com.autodev.licencias.dao.memoria;

import com.autodev.licencias.dao.ClienteDAO;
import com.autodev.licencias.modelo.Cliente;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** DAO falso en memoria para probar los servicios sin MySQL. */
public class ClienteDAOMemoria implements ClienteDAO {

    private final List<Cliente> clientes = new ArrayList<>();

    @Override
    public Cliente guardar(Cliente cliente) {
        cliente.setIdCliente(clientes.size() + 1);
        clientes.add(cliente);
        return cliente;
    }

    @Override
    public void actualizar(Cliente cliente) {
        clientes.replaceAll(c -> c.getIdCliente() == cliente.getIdCliente() ? cliente : c);
    }

    @Override
    public Optional<Cliente> buscarPorRuc(String ruc) {
        return clientes.stream().filter(c -> Objects.equals(c.getRuc(), ruc)).findFirst();
    }

    @Override
    public List<Cliente> listar() {
        return new ArrayList<>(clientes);
    }
}

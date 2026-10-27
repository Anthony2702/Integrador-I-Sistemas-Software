package com.autodev.licencias.servicio;

import static com.google.common.base.Preconditions.checkArgument;

import com.autodev.licencias.dao.ClienteDAO;
import com.autodev.licencias.modelo.Cliente;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registro y edición de clientes, con validación de sus datos.
 */
public class ClienteServicio {

    private static final Logger log = LoggerFactory.getLogger(ClienteServicio.class);

    private final ClienteDAO clienteDAO;

    public ClienteServicio(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    /** @return todos los clientes ordenados por nombre */
    public List<Cliente> listar() {
        return clienteDAO.listar();
    }

    /**
     * Registra un cliente nuevo.
     *
     * @param nombre razón social o nombre completo (obligatorio)
     * @param ruc RUC de 11 dígitos (opcional)
     * @param contacto celular o correo (opcional)
     * @return el cliente guardado
     * @throws IllegalArgumentException si los datos no son válidos o el RUC ya existe
     */
    public Cliente registrar(String nombre, String ruc, String contacto) {
        Cliente cliente = new Cliente();
        llenar(cliente, nombre, ruc, contacto);
        clienteDAO.guardar(cliente);
        log.info("Cliente registrado: {}", cliente.getNombre());
        return cliente;
    }

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param cliente cliente a editar
     * @param nombre nombre nuevo
     * @param ruc RUC nuevo
     * @param contacto contacto nuevo
     */
    public void actualizar(Cliente cliente, String nombre, String ruc, String contacto) {
        llenar(cliente, nombre, ruc, contacto);
        clienteDAO.actualizar(cliente);
        log.info("Cliente actualizado: {}", cliente.getNombre());
    }

    private void llenar(Cliente cliente, String nombre, String ruc, String contacto) {
        String nombreLimpio = StringUtils.normalizeSpace(nombre);
        String rucLimpio = StringUtils.trimToNull(ruc);
        checkArgument(StringUtils.isNotBlank(nombreLimpio), "Ingresa el nombre del cliente");
        checkArgument(nombreLimpio.length() <= 150, "El nombre es muy largo (máximo 150 caracteres)");
        if (rucLimpio != null) {
            checkArgument(rucLimpio.length() == 11 && StringUtils.isNumeric(rucLimpio),
                    "El RUC debe tener 11 dígitos");
            Optional<Cliente> conEseRuc = clienteDAO.buscarPorRuc(rucLimpio);
            checkArgument(conEseRuc.isEmpty() || conEseRuc.get().getIdCliente() == cliente.getIdCliente(),
                    "Ya existe un cliente con el RUC %s", rucLimpio);
        }
        cliente.setNombre(nombreLimpio);
        cliente.setRuc(rucLimpio);
        cliente.setContacto(StringUtils.trimToNull(contacto));
    }
}

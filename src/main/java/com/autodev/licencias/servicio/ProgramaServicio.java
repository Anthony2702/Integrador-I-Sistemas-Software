package com.autodev.licencias.servicio;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import com.autodev.licencias.dao.ProgramaDAO;
import com.autodev.licencias.modelo.Programa;
import com.autodev.licencias.modelo.Usuario;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Consulta de programas y cambio de precios.
 */
public class ProgramaServicio {

    private static final Logger log = LoggerFactory.getLogger(ProgramaServicio.class);

    private final ProgramaDAO programaDAO;

    public ProgramaServicio(ProgramaDAO programaDAO) {
        this.programaDAO = programaDAO;
    }

    /** @return los programas de AUTODEV con su precio de lista */
    public List<Programa> listar() {
        return programaDAO.listar();
    }

    /**
     * Cambia el precio de lista de un programa. Solo lo puede hacer el administrador.
     *
     * @param usuario usuario que hace el cambio
     * @param programa programa a modificar
     * @param precio precio nuevo (mayor a cero)
     * @throws IllegalStateException si el usuario no es administrador
     */
    public void cambiarPrecio(Usuario usuario, Programa programa, BigDecimal precio) {
        checkState(usuario != null && usuario.esAdmin(), "Solo el administrador puede cambiar precios");
        checkArgument(precio != null && precio.signum() > 0, "El precio debe ser mayor a cero");
        BigDecimal nuevo = precio.setScale(2, RoundingMode.HALF_UP);
        programaDAO.actualizarPrecio(programa.getIdPrograma(), nuevo);
        log.info("{} cambió el precio de {} de S/ {} a S/ {}", usuario.getUsuario(), programa.getNombre(),
                programa.getPrecio(), nuevo);
        programa.setPrecio(nuevo);
    }
}

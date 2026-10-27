package com.autodev.licencias.servicio;

import com.autodev.licencias.dao.UsuarioDAO;
import com.autodev.licencias.modelo.Rol;
import com.autodev.licencias.modelo.Usuario;
import com.autodev.licencias.util.SeguridadClave;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Inicio de sesión y creación de los usuarios iniciales.
 */
public class AutenticacionServicio {

    private static final Logger log = LoggerFactory.getLogger(AutenticacionServicio.class);
    private static final int LARGO_MINIMO_CLAVE = 8;

    private final UsuarioDAO usuarioDAO;

    public AutenticacionServicio(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Valida usuario y clave. La clave se borra de la memoria al terminar.
     *
     * @param usuario nombre de usuario
     * @param clave clave escrita en la pantalla de login
     * @return el usuario si los datos son correctos y está activo
     */
    public Optional<Usuario> iniciarSesion(String usuario, char[] clave) {
        try {
            if (StringUtils.isBlank(usuario) || clave == null || clave.length == 0) {
                return Optional.empty();
            }
            Optional<Usuario> encontrado = usuarioDAO.buscarPorUsuario(usuario.trim())
                    .filter(Usuario::isActivo)
                    .filter(u -> SeguridadClave.verificar(clave, u.getClaveHash()));
            // Nunca se registra la clave, solo el usuario y el resultado
            if (encontrado.isPresent()) {
                log.info("Inicio de sesión correcto: {}", usuario.trim());
            } else {
                log.warn("Inicio de sesión fallido: {}", usuario.trim());
            }
            return encontrado;
        } finally {
            SeguridadClave.limpiar(clave);
        }
    }

    /**
     * Crea los usuarios "admin" y "vendedor" la primera vez que se usa el sistema.
     * Si ya hay usuarios no hace nada.
     *
     * @param claveAdmin clave inicial del administrador (mínimo 8 caracteres)
     * @param claveVendedor clave inicial del vendedor (mínimo 8 caracteres)
     * @throws IllegalStateException si alguna clave es muy corta
     */
    public void crearUsuariosIniciales(String claveAdmin, String claveVendedor) {
        if (usuarioDAO.contar() > 0) {
            return;
        }
        if (StringUtils.length(claveAdmin) < LARGO_MINIMO_CLAVE || StringUtils.length(claveVendedor) < LARGO_MINIMO_CLAVE) {
            throw new IllegalStateException("Las claves iniciales de config.properties deben tener al menos "
                    + LARGO_MINIMO_CLAVE + " caracteres");
        }
        usuarioDAO.guardar(new Usuario("admin", SeguridadClave.generarHash(claveAdmin.toCharArray()),
                "Administrador AUTODEV", Rol.ADMIN));
        usuarioDAO.guardar(new Usuario("vendedor", SeguridadClave.generarHash(claveVendedor.toCharArray()),
                "Vendedor AUTODEV", Rol.VENDEDOR));
        log.info("Se crearon los usuarios iniciales admin y vendedor");
    }
}

package com.autodev.licencias.modelo;

/**
 * Roles de los usuarios. Solo el administrador puede cambiar los precios de los programas.
 */
public enum Rol {
    ADMIN("Administrador"),
    VENDEDOR("Vendedor");

    private final String etiqueta;

    Rol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** @return nombre del rol para mostrar en pantalla */
    public String getEtiqueta() {
        return etiqueta;
    }
}

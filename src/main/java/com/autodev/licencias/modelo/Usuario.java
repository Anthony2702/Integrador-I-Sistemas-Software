package com.autodev.licencias.modelo;

/**
 * Persona que usa el sistema. La clave solo se guarda como hash.
 */
public class Usuario {

    private int idUsuario;
    private String usuario;
    private String claveHash;
    private String nombre;
    private Rol rol;
    private boolean activo = true;

    public Usuario() {
    }

    public Usuario(String usuario, String claveHash, String nombre, Rol rol) {
        this.usuario = usuario;
        this.claveHash = claveHash;
        this.nombre = nombre;
        this.rol = rol;
    }

    /** @return true si el usuario es administrador */
    public boolean esAdmin() {
        return rol == Rol.ADMIN;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClaveHash() {
        return claveHash;
    }

    public void setClaveHash(String claveHash) {
        this.claveHash = claveHash;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}

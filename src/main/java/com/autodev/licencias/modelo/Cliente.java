package com.autodev.licencias.modelo;

import java.util.Objects;

/**
 * Empresa o persona que compra licencias de AUTODEV.
 */
public class Cliente {

    private int idCliente;
    private String nombre;
    private String ruc;
    private String contacto;

    public Cliente() {
    }

    public Cliente(int idCliente, String nombre, String ruc, String contacto) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.ruc = ruc;
        this.contacto = contacto;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    /** Dos clientes son iguales si tienen el mismo id. */
    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Cliente)) {
            return false;
        }
        Cliente cliente = (Cliente) otro;
        return idCliente != 0 && idCliente == cliente.idCliente;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCliente);
    }

    /** Se muestra así en las listas desplegables. */
    @Override
    public String toString() {
        return nombre;
    }
}

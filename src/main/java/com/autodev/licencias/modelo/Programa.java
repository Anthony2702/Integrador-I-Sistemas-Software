package com.autodev.licencias.modelo;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Programa de AUTODEV que se vende con licencia anual (AUTODEV, AUTOSIRE, AUTOGUIAS, ...).
 */
public class Programa {

    private int idPrograma;
    private String nombre;
    private BigDecimal precio;

    public Programa() {
    }

    public Programa(int idPrograma, String nombre, BigDecimal precio) {
        this.idPrograma = idPrograma;
        this.nombre = nombre;
        this.precio = precio;
    }

    public int getIdPrograma() {
        return idPrograma;
    }

    public void setIdPrograma(int idPrograma) {
        this.idPrograma = idPrograma;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    /** Dos programas son iguales si tienen el mismo id. */
    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Programa)) {
            return false;
        }
        Programa programa = (Programa) otro;
        return idPrograma != 0 && idPrograma == programa.idPrograma;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPrograma);
    }

    /** Se muestra así en las listas desplegables. */
    @Override
    public String toString() {
        return nombre;
    }
}

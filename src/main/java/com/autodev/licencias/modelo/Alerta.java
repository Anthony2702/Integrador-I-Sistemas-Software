package com.autodev.licencias.modelo;

import java.time.LocalDateTime;

/**
 * Aviso que genera el sistema cuando una licencia está por vencer o ya venció,
 * para que el vendedor contacte al cliente y le ofrezca la renovación.
 */
public class Alerta {

    private int idAlerta;
    private Licencia licencia;
    private LocalDateTime fechaGenerada;
    private int diasRestantes;
    private boolean atendida;

    public Alerta() {
    }

    public Alerta(Licencia licencia, LocalDateTime fechaGenerada, int diasRestantes) {
        this.licencia = licencia;
        this.fechaGenerada = fechaGenerada;
        this.diasRestantes = diasRestantes;
    }

    /** Marca la alerta como atendida (el vendedor ya contactó al cliente). */
    public void marcarAtendida() {
        this.atendida = true;
    }

    public int getIdAlerta() {
        return idAlerta;
    }

    public void setIdAlerta(int idAlerta) {
        this.idAlerta = idAlerta;
    }

    public Licencia getLicencia() {
        return licencia;
    }

    public void setLicencia(Licencia licencia) {
        this.licencia = licencia;
    }

    public LocalDateTime getFechaGenerada() {
        return fechaGenerada;
    }

    public void setFechaGenerada(LocalDateTime fechaGenerada) {
        this.fechaGenerada = fechaGenerada;
    }

    /** @return días que le quedaban a la licencia cuando se generó la alerta */
    public int getDiasRestantes() {
        return diasRestantes;
    }

    public void setDiasRestantes(int diasRestantes) {
        this.diasRestantes = diasRestantes;
    }

    public boolean isAtendida() {
        return atendida;
    }

    public void setAtendida(boolean atendida) {
        this.atendida = atendida;
    }
}

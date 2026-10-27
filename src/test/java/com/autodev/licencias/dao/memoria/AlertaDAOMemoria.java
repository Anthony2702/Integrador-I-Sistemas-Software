package com.autodev.licencias.dao.memoria;

import com.autodev.licencias.dao.AlertaDAO;
import com.autodev.licencias.modelo.Alerta;
import java.util.ArrayList;
import java.util.List;

/** DAO falso en memoria para probar los servicios sin MySQL. */
public class AlertaDAOMemoria implements AlertaDAO {

    private final List<Alerta> alertas = new ArrayList<>();
    private int siguienteId = 1;

    @Override
    public Alerta guardar(Alerta alerta) {
        alerta.setIdAlerta(siguienteId++);
        alertas.add(alerta);
        return alerta;
    }

    @Override
    public boolean existeParaLicencia(int idLicencia) {
        return alertas.stream().anyMatch(a -> a.getLicencia().getIdLicencia() == idLicencia);
    }

    @Override
    public List<Alerta> listarPendientes() {
        return alertas.stream().filter(a -> !a.isAtendida()).toList();
    }

    @Override
    public void marcarAtendida(int idAlerta) {
        alertas.stream().filter(a -> a.getIdAlerta() == idAlerta).forEach(Alerta::marcarAtendida);
    }

    @Override
    public void atenderPorLicencia(int idLicencia) {
        alertas.stream().filter(a -> a.getLicencia().getIdLicencia() == idLicencia).forEach(Alerta::marcarAtendida);
    }
}

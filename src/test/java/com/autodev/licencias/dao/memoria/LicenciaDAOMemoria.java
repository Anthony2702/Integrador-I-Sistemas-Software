package com.autodev.licencias.dao.memoria;

import com.autodev.licencias.dao.LicenciaDAO;
import com.autodev.licencias.modelo.EstadoLicencia;
import com.autodev.licencias.modelo.Licencia;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** DAO falso en memoria para probar los servicios sin MySQL. */
public class LicenciaDAOMemoria implements LicenciaDAO {

    private final List<Licencia> licencias = new ArrayList<>();
    private int siguienteId = 1;

    @Override
    public Licencia guardar(Licencia licencia) {
        licencia.setIdLicencia(siguienteId++);
        licencias.add(licencia);
        return licencia;
    }

    @Override
    public void actualizarEstado(int idLicencia, EstadoLicencia estado) {
        licencias.stream().filter(l -> l.getIdLicencia() == idLicencia).forEach(l -> l.setEstado(estado));
    }

    @Override
    public List<Licencia> listar() {
        List<Licencia> copia = new ArrayList<>(licencias);
        copia.sort(Comparator.comparing(Licencia::getFechaVenta).reversed());
        return copia;
    }

    public Licencia buscar(int idLicencia) {
        return licencias.stream().filter(l -> l.getIdLicencia() == idLicencia).findFirst().orElseThrow();
    }
}

package com.autodev.licencias.dao.memoria;

import com.autodev.licencias.dao.ProgramaDAO;
import com.autodev.licencias.modelo.Programa;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** DAO falso en memoria para probar los servicios sin MySQL. */
public class ProgramaDAOMemoria implements ProgramaDAO {

    private final List<Programa> programas = new ArrayList<>();

    public ProgramaDAOMemoria(Programa... programas) {
        this.programas.addAll(List.of(programas));
    }

    @Override
    public List<Programa> listar() {
        return new ArrayList<>(programas);
    }

    @Override
    public void actualizarPrecio(int idPrograma, BigDecimal precio) {
        programas.stream().filter(p -> p.getIdPrograma() == idPrograma).forEach(p -> p.setPrecio(precio));
    }
}

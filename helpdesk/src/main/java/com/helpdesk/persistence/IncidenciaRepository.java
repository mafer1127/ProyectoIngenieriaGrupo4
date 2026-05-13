package com.helpdesk.persistence;
package com.helpdesk.model.Incidencia;

import java.util.ArrayList;

public class IncidenciaRepository {
    private ArrayList<Incidencia> incidencias = new ArrayList<>();
    private int contadorId = 1;

    public int generarId() {
        return contadorId++;
    }

    public void agregar(Incidencia i) {
        incidencias.add(i);
    }

    public Incidencia buscarPorId(int id) {
        for (Incidencia i : incidencias) {
            if (i.getId() == id) return i;
        }
        return null;
    }

    public ArrayList<Incidencia> obtenerTodas() {
        return incidencias;
    }

    public boolean eliminar(int id) {
        return incidencias.removeIf(i -> i.getId() == id);
    }
}

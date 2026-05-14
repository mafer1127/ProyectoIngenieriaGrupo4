package com.helpdesk.persistence;

import java.util.ArrayList;
import java.util.List;

import com.helpdesk.model.Tecnico;

public class TecnicoRepository {

    private List<Tecnico> tecnicos = new ArrayList<>();

    public List<Tecnico> getAll() {
        return tecnicos;
    }

    public void agregar(Tecnico t) {
        tecnicos.add(t);
    }

    public Tecnico buscarPorId(int id) {
        return tecnicos.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
    }
}

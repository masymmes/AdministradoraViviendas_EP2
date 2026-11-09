package cl.duoc.viviendas.repository;

import cl.duoc.viviendas.dao.ArchivoDAO;
import cl.duoc.viviendas.dao.ArchivoDAOJson;
import cl.duoc.viviendas.model.Vivienda;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViviendaRepository {

    private ArchivoDAO dao = new ArchivoDAOJson();
    private List<Vivienda> viviendas = new ArrayList<>();

    public void cargar() throws IOException {
        viviendas = dao.leer();
    }

    public List<Vivienda> listar() {
        return viviendas;
    }

    public void agregar(Vivienda vivienda) throws IOException {
        viviendas.add(vivienda);
        dao.guardar(viviendas);
    }

    public void eliminar(Vivienda vivienda) throws IOException {
        viviendas.remove(vivienda);
        dao.guardar(viviendas);
    }
}

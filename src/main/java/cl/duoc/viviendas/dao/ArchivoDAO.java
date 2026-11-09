package cl.duoc.viviendas.dao;

import cl.duoc.viviendas.model.Vivienda;

import java.io.IOException;
import java.util.List;

public interface ArchivoDAO {

    List<Vivienda> leer() throws IOException;

    void guardar(List<Vivienda> viviendas) throws IOException;
}

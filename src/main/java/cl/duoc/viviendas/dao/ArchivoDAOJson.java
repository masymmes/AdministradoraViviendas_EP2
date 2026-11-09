package cl.duoc.viviendas.dao;

import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArchivoDAOJson implements ArchivoDAO {

    private String archivoDepartamentos = "departamentos.json";
    private String archivoCasas = "casas.json";
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public List<Vivienda> leer() throws IOException {
        List<Vivienda> lista = new ArrayList<>();

        // como Vivienda es abstracta se lee cada tipo en su archivo
        File f1 = new File(archivoDepartamentos);
        if (f1.exists()) {
            FileReader reader = new FileReader(f1);
            Departamento[] departamentos = gson.fromJson(reader, Departamento[].class);
            reader.close();
            if (departamentos != null) {
                for (Departamento d : departamentos) {
                    lista.add(d);
                }
            }
        }

        File f2 = new File(archivoCasas);
        if (f2.exists()) {
            FileReader reader = new FileReader(f2);
            Casa[] casas = gson.fromJson(reader, Casa[].class);
            reader.close();
            if (casas != null) {
                for (Casa c : casas) {
                    lista.add(c);
                }
            }
        }

        return lista;
    }

    @Override
    public void guardar(List<Vivienda> viviendas) throws IOException {
        List<Departamento> departamentos = new ArrayList<>();
        List<Casa> casas = new ArrayList<>();

        for (Vivienda v : viviendas) {
            if (v instanceof Departamento) {
                departamentos.add((Departamento) v);
            } else {
                casas.add((Casa) v);
            }
        }

        FileWriter writer = new FileWriter(archivoDepartamentos);
        gson.toJson(departamentos, writer);
        writer.close();

        FileWriter writer2 = new FileWriter(archivoCasas);
        gson.toJson(casas, writer2);
        writer2.close();
    }
}

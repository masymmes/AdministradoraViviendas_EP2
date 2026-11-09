package cl.duoc.viviendas.controller;

import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;
import cl.duoc.viviendas.repository.ViviendaRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class MainController {

    @FXML
    private ComboBox<String> cbTipo;

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtSuperficie;

    @FXML
    private TextField txtHabitaciones;

    @FXML
    private TextField txtPiso;

    @FXML
    private CheckBox chkGastoComun;

    @FXML
    private CheckBox chkEstacionamiento;

    @FXML
    private CheckBox chkPatio;

    @FXML
    private Button btnRegistrar;

    @FXML
    private Button btnEliminar;

    @FXML
    private TextField txtBuscar;

    @FXML
    private TableView<Vivienda> tablaViviendas;

    @FXML
    private TableColumn<Vivienda, String> colTipo;

    @FXML
    private TableColumn<Vivienda, String> colCodigo;

    @FXML
    private TableColumn<Vivienda, Double> colSuperficie;

    @FXML
    private TableColumn<Vivienda, Integer> colHabitaciones;

    @FXML
    private TableColumn<Vivienda, String> colCosto;

    @FXML
    private Label lblMensaje;

    private ViviendaRepository repository = new ViviendaRepository();
    private ObservableList<Vivienda> lista = FXCollections.observableArrayList();
    private FilteredList<Vivienda> filtro;

    @FXML
    public void initialize() {
        cbTipo.getItems().add("Departamento");
        cbTipo.getItems().add("Casa");

        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colSuperficie.setCellValueFactory(new PropertyValueFactory<>("superficie"));
        colHabitaciones.setCellValueFactory(new PropertyValueFactory<>("habitaciones"));
        // el costo lo calcula cada clase del modelo
        colCosto.setCellValueFactory(cell -> new SimpleStringProperty("$" + Math.round(cell.getValue().calcularCostoArriendo())));

        // la tabla muestra la lista filtrada, la original no se modifica
        filtro = new FilteredList<>(lista, v -> true);
        tablaViviendas.setItems(filtro);

        txtBuscar.textProperty().addListener((obs, anterior, nuevo) -> {
            String texto = nuevo.toLowerCase();
            filtro.setPredicate(v -> v.getCodigo().toLowerCase().contains(texto)
                    || v.getTipo().toLowerCase().contains(texto));
        });

        activarCampos();

        try {
            repository.cargar();
            lista.setAll(repository.listar());
        } catch (Exception e) {
            mostrarError("No se pudieron cargar los datos guardados");
        }
    }

    @FXML
    void cambiarTipo(ActionEvent event) {
        activarCampos();
    }

    @FXML
    void registrar(ActionEvent event) {
        String tipo = cbTipo.getValue();
        if (tipo == null) {
            mostrarError("Debe seleccionar el tipo de vivienda");
            return;
        }

        if (txtCodigo.getText().trim().isEmpty()) {
            mostrarError("El código no puede estar vacío");
            return;
        }

        double superficie;
        try {
            superficie = Double.parseDouble(txtSuperficie.getText().trim());
        } catch (NumberFormatException e) {
            mostrarError("La superficie debe ser un número");
            return;
        }

        int habitaciones;
        try {
            habitaciones = Integer.parseInt(txtHabitaciones.getText().trim());
        } catch (NumberFormatException e) {
            mostrarError("Las habitaciones deben ser un número entero");
            return;
        }

        int piso = 0;
        if (tipo.equals("Departamento")) {
            try {
                piso = Integer.parseInt(txtPiso.getText().trim());
            } catch (NumberFormatException e) {
                mostrarError("El piso debe ser un número entero");
                return;
            }
            if (piso <= 0) {
                mostrarError("El piso debe ser mayor que 0");
                return;
            }
        }

        Vivienda vivienda;
        try {
            if (tipo.equals("Departamento")) {
                Departamento d = new Departamento(txtCodigo.getText(), superficie, habitaciones, piso, chkGastoComun.isSelected());
                if (chkEstacionamiento.isSelected()) {
                    d.asignarEstacionamiento();
                }
                vivienda = d;
            } else {
                vivienda = new Casa(txtCodigo.getText(), superficie, habitaciones, chkPatio.isSelected());
            }
        } catch (IllegalArgumentException e) {
            // los set del modelo validan superficie y habitaciones
            mostrarError(e.getMessage());
            return;
        }

        try {
            repository.agregar(vivienda);
            lista.setAll(repository.listar());
            mostrarMensaje("Vivienda " + vivienda.getCodigo() + " registrada correctamente");
            limpiar();
        } catch (Exception e) {
            mostrarError("Error al guardar la vivienda");
        }
    }

    @FXML
    void eliminar(ActionEvent event) {
        Vivienda seleccionada = tablaViviendas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Debe seleccionar una vivienda de la tabla");
            return;
        }

        try {
            repository.eliminar(seleccionada);
            lista.setAll(repository.listar());
            mostrarMensaje("Vivienda " + seleccionada.getCodigo() + " eliminada");
        } catch (Exception e) {
            mostrarError("Error al eliminar la vivienda");
        }
    }

    private void activarCampos() {
        String tipo = cbTipo.getValue();
        boolean depto = "Departamento".equals(tipo);
        boolean casa = "Casa".equals(tipo);

        txtPiso.setDisable(!depto);
        chkGastoComun.setDisable(!depto);
        chkEstacionamiento.setDisable(!depto);
        chkPatio.setDisable(!casa);
    }

    private void limpiar() {
        // el tipo se deja seleccionado
        txtCodigo.clear();
        txtSuperficie.clear();
        txtHabitaciones.clear();
        txtPiso.clear();
        chkGastoComun.setSelected(false);
        chkEstacionamiento.setSelected(false);
        chkPatio.setSelected(false);
    }

    private void mostrarMensaje(String texto) {
        lblMensaje.setStyle("-fx-text-fill: green;");
        lblMensaje.setText(texto);
    }

    private void mostrarError(String texto) {
        lblMensaje.setStyle("-fx-text-fill: red;");
        lblMensaje.setText(texto);
    }
}

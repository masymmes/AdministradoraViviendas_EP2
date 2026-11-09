module cl.duoc.viviendas {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens cl.duoc.viviendas.controller to javafx.fxml;
    opens cl.duoc.viviendas.model to javafx.base, com.google.gson;

    exports cl.duoc.viviendas;
}

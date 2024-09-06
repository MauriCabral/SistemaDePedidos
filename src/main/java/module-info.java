module org.example.kaos {
    requires javafx.fxml;
    requires java.logging;
    requires java.sql;
    requires javafx.base;
    requires org.json;
    requires javafx.graphics;
    requires javafx.controls;


    opens org.example.kaos.window to javafx.fxml;
    exports org.example.kaos.controller;
    opens org.example.kaos.controller to javafx.fxml;
    exports org.example.kaos.application;
    opens org.example.kaos.application to javafx.fxml;
    opens org.example.kaos.entity to javafx.base;

//    exports org.example.kaos;
}
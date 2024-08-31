module org.example.kaos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.sql;
    requires org.json;


    opens org.example.kaos.window to javafx.fxml;
//    exports org.example.kaos;
    exports org.example.kaos.controller;
    opens org.example.kaos.controller to javafx.fxml;
    exports org.example.kaos.application;
    opens org.example.kaos.application to javafx.fxml;
}
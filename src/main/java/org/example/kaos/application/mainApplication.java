package org.example.kaos.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class mainApplication extends Application {
    private static final Logger logger = Logger.getLogger(mainApplication.class.getName());

    public static void main(String[] args) {
        launch();
    }
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(mainApplication.class.getResource("/org/example/kaos/window/main.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 600);

            scene.setOnMouseClicked((MouseEvent event) -> {
                try {
                    openNewWindow();
                    stage.close();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Failed to open new window", e);
                }
            });

            stage.setTitle("Kaos");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load the main FXML file", e);
        }
    }

    private void openNewWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(mainApplication.class.getResource("/org/example/kaos/window/pedido.fxml"));
        Scene newScene = new Scene(fxmlLoader.load(), 1000, 600);
        Stage newStage = new Stage();
        newStage.setTitle("Pedidos");
        newStage.setScene(newScene);
        newStage.show();
    }
}
package org.example.kaos.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import org.example.kaos.controller.PedidoController;
import org.example.kaos.manager.ControllerManager;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApplication extends Application {
    private static final Logger logger = Logger.getLogger(MainApplication.class.getName());

    public static void main(String[] args) {
        launch();
    }
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/org/example/kaos/window/Main.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 620);
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
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/org/example/kaos/window/Pedido.fxml"));
        Scene newScene = new Scene(fxmlLoader.load(), 1000, 620);
        PedidoController controller = fxmlLoader.getController();
        ControllerManager.getInstance().setPedidoController(controller);
        PedidoApplication pedidoApp = new PedidoApplication();
        controller.setPedidoApp(pedidoApp);
        Stage newStage = new Stage();
        newStage.setTitle("Pedidos");
        newStage.setScene(newScene);
        newStage.show();
    }
}
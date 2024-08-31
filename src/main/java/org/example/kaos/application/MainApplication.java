package org.example.kaos.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import org.example.kaos.controller.PedidoController;
import org.example.kaos.manager.ControllerManager;
import org.example.kaos.service.PedidoService;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApplication extends Application {
    private static final Logger logger = Logger.getLogger(MainApplication.class.getName());
    private PedidoService pedidoService;
    private PedidoApplication pedidoApp;

    public static void main(String[] args) {
        launch();
    }
    @Override
    public void start(Stage stage) {
        try {
            pedidoService = new PedidoService();
            pedidoApp = new PedidoApplication();
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

        System.out.println("Controlador cargado desde FXML: " + controller);

        if (controller != null) {
            controller.setPedidoService(pedidoService);
            controller.setPedidoApp(pedidoApp);
            ControllerManager.getInstance().setPedidoController(controller);
            System.out.println("PedidoController se ha establecido correctamente.");
        } else {
            System.out.println("El controlador de pedidos es null.");
        }

        Stage newStage = new Stage();
        newStage.setTitle("Pedidos");
        newStage.setScene(newScene);
        newStage.show();
    }
}
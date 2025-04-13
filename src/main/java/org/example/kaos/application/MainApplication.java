package org.example.kaos.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.kaos.controller.PedidoController;
import org.example.kaos.manager.ControllerManager;
import org.example.kaos.repository.HamburguesaDAO;
import org.example.kaos.repository.HamburguesaTipoDAO;
import org.example.kaos.service.PedidoService;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApplication extends Application {
    private static final Logger logger = Logger.getLogger(MainApplication.class.getName());
    private PedidoService pedidoService;
    private PedidoApplication pedidoApp;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            pedidoService = new PedidoService(new HamburguesaDAO(), new HamburguesaTipoDAO());
            pedidoApp = new PedidoApplication();
            openNewWindow();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to open the pedidos window", e);
        }
    }

    private void openNewWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/org/example/kaos/window/Pedido.fxml"));
        Scene newScene = new Scene(fxmlLoader.load(), 1000, 620);
        PedidoController controller = fxmlLoader.getController();
        if (controller != null) {
            controller.setPedidoService(pedidoService);
            controller.setPedidoApp(pedidoApp);
            ControllerManager.getInstance().setPedidoController(controller);
        } else {
            logger.severe("El controlador de pedidos es null.");
        }
        Stage newStage = new Stage();
        newStage.setTitle("Pedidos");
        newStage.setScene(newScene);
        newStage.getIcons().add(new Image(MainApplication.class.getResourceAsStream("/org/example/kaos/image/Recurso_35.png")));
        newStage.show();
    }
}
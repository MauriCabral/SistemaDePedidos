package org.example.kaos.application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.kaos.controller.DatoClienteController;
import org.example.kaos.controller.DetalleController;
import org.example.kaos.service.PedidoService;

import java.io.IOException;

public class PedidoApplication {

    public void openDetalleWindow(String menuNombre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/kaos/window/Detalle.fxml"));
            Parent detallePane = loader.load();

            DetalleController detalleController = loader.getController();
            detalleController.setDetalle(menuNombre);

            Stage detalleStage = new Stage();
            detalleStage.setTitle("Detalle");
            detalleStage.initModality(Modality.APPLICATION_MODAL);
            detalleStage.setScene(new Scene(detallePane));
            detalleController.setStage(detalleStage);
            detalleStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openDatoClienteWindow(PedidoService pedidoService) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/kaos/window/DatoCliente.fxml"));
            Parent root = loader.load();
            DatoClienteController controller = loader.getController();
            controller.setPedidoService(pedidoService);
            Stage stage = new Stage();
            stage.setTitle("Datos del Cliente");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, 380, 250));
            controller.setStage(stage);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
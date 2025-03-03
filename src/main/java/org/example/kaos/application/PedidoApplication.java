package org.example.kaos.application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.kaos.controller.DatoClienteController;
import org.example.kaos.controller.DetalleController;
import org.example.kaos.controller.DetallePedidoHistoricoController;
import org.example.kaos.controller.HamburguesaController;
import org.example.kaos.entity.HamburguesaTipo;
import org.example.kaos.entity.Pedido;
import org.example.kaos.service.DetalleService;
import org.example.kaos.service.PedidoService;

import java.io.IOException;
import java.util.List;

public class PedidoApplication {

    private PedidoService pedidoService;
    private DetalleService detalleService;

    public PedidoApplication() {
        this.pedidoService = new PedidoService();
        this.detalleService = new DetalleService();
    }

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
            controller.setDetalleService(this.detalleService);
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

    public void abrirVentanaDetallePedido(Pedido pedido) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/kaos/window/DetallePedidoHistorico.fxml"));
            Parent root = loader.load();
            DetallePedidoHistoricoController detalleController = loader.getController();
            detalleController.setPedidoService(this.pedidoService);
            detalleController.cargarDetallePedido(pedido.getId());
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void agregarHamburguesa(String menuNombre, List<HamburguesaTipo> idHamburguesaList, boolean esEditar) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/kaos/window/HamburguesaAgregar.fxml"));
            Parent root = loader.load();
            HamburguesaController hamburgusaController = loader.getController();
            hamburgusaController.setHamburguesaEditar(menuNombre, idHamburguesaList, esEditar);
            Stage stage = new Stage();
            if (esEditar){
                stage.setTitle("Editar Hamburguesa");
            }else {
                stage.setTitle("Agregar Hamburguesa");
            }
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
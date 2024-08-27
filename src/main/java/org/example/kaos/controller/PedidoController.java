package org.example.kaos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.geometry.Insets;

import java.io.IOException;
import java.util.Optional;

import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.kaos.dao.hamburguesaDAO;
import org.example.kaos.entity.Hamburgusa;
import org.example.kaos.manager.ControllerManager;
import org.example.kaos.entity.Toppings;
import java.util.List;

public class PedidoController {

    private final hamburguesaDAO hamburguesaDAO = new hamburguesaDAO();
    @FXML
    private Button exitButton, cbButton, deletePedido;
    @FXML
    private Pane leftPanel;
    @FXML
    private Pane menuPane;
    @FXML
    private Pane rightPane;
    @FXML
    private VBox detallePedidos;

    @FXML
    private void handleExitButtonClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de Salida");
        alert.setHeaderText(null);
        alert.setContentText("¿Seguro que quieres salir?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("INFORMATION");
            info.setHeaderText(null);
            info.setContentText("Enviar msj con monto ganado a wsp");
            info.showAndWait();
            System.exit(0);
        }
    }

    @FXML
    private void handlePedidosButtonClick() {
        menuPane.setVisible(true);
        rightPane.setVisible(true);
    }

    @FXML
    private void handleImageButtonClick(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        String menuCode = (String) sourceButton.getUserData();
        String res = switch (menuCode) {
            case "cb", "di", "kk", "kl", "mn", "rm", "tn", "v", "vr" -> menuCode;
            default -> " ";
        };
        Hamburgusa selectedMenu = hamburguesaDAO.getMenuByCode(res);
        if (selectedMenu != null) {
            openDetalleWindow(selectedMenu.getNombre());
        } else {
            System.out.println("Menu no encontrado.");
        }
    }

    private void openDetalleWindow(String menuNombre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/kaos/window/detalle.fxml"));
            Pane detallePane = loader.load();

            DetalleController detalleController = loader.getController();

            detalleController.setDetalle(menuNombre);

            ControllerManager.getInstance().setPedidoController(this);

            Stage detalleStage = new Stage();
            detalleStage.setTitle("Detalle");
            detalleStage.initModality(Modality.APPLICATION_MODAL);
            detalleStage.initOwner(leftPanel.getScene().getWindow());
            detalleStage.setScene(new Scene(detallePane));
            detalleStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        menuPane.setVisible(false);
        rightPane.setVisible(false);
    }

    public void actualizarDetalles(String nombre, String tipo, int cantidad, double precio, List<Toppings> toppingsList) {
        VBox vBox = new VBox(5);
        vBox.setPadding(new Insets(2, 8, 0, 8));
        HBox pedidoBox = new HBox(5);
        Label pedidoLabel = new Label("(x" + cantidad + ") " + nombre + " " + tipo);
        Label precioLabel = new Label(String.format("$%d", (int) precio));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        pedidoBox.getChildren().addAll(pedidoLabel, spacer, precioLabel);
        vBox.getChildren().add(pedidoBox);
        if (toppingsList != null && !toppingsList.isEmpty()) {
            VBox toppingsBox = new VBox(5);
            toppingsBox.setPadding(new Insets(5, 0, 0, 0));
            for (Toppings topping : toppingsList) {
                if (topping.esExtra()) {
                    if (topping.getPrecio() != null) {
                        int precioTop = (int) Math.round(topping.getPrecio());
                        Label toppingLabel = new Label("Extra: " + topping.getNombre() + ": ($" + precioTop + ")");
                        toppingsBox.getChildren().add(toppingLabel);
                    } else {
                        Label toppingLabel = new Label("Extra: " + topping.getNombre());
                        toppingsBox.getChildren().add(toppingLabel);
                    }
                } else {
                    Label toppingLabel = new Label("Sin: " + topping.getNombre());
                    toppingsBox.getChildren().add(toppingLabel);
                }
            }
            vBox.getChildren().add(toppingsBox);
        }
        detallePedidos.getChildren().add(vBox);
    }

    public void deletePedidos(ActionEvent actionEvent) {
    }
}
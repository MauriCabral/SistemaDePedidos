package org.example.kaos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.geometry.Insets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.kaos.entity.DetallePedido;
import org.example.kaos.entity.HamburguesaTipo;
import org.example.kaos.entity.Topping;
import org.example.kaos.repository.hamburguesaDAO;
import org.example.kaos.repository.hamburguesaTipoDAO;
import org.example.kaos.entity.Hamburgusa;
import org.example.kaos.manager.ControllerManager;
import org.example.kaos.repository.detallePedidoDAO;
import org.example.kaos.repository.pedidoDAO;

import java.util.List;

public class PedidoController {

    private final hamburguesaDAO hamburguesaDAO = new hamburguesaDAO();
    private final hamburguesaTipoDAO hamburguesaTipoDAO = new hamburguesaTipoDAO();
    private boolean deleteButtonsVisible = false;
    private List<DetallePedido> detallesPedidosList;

    List<Integer> listPrecio = new ArrayList<>();
    @FXML
    private Button exitButton, cbButton, deletePedido;
    @FXML
    private Pane leftPanel, menuPane, rightPane;
    @FXML
    private VBox detallePedidos;
    @FXML
    private Label lblTotal;
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
//            detallePane.setPrefWidth(178);
//            detallePane.setPrefHeight(600);
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
        detallesPedidosList = new ArrayList<>();
    }

    public void actualizarDetalles(String nombreHamburguesa, String tipoHamburguesa, int cantidad, double precio, List<Topping> toppingList) {
        VBox vBox = new VBox(5);
        vBox.setPadding(new Insets(2, 8, 0, 8));
        HBox pedidoBox = new HBox(5);

        HamburguesaTipo hamburguesaTipo = hamburguesaTipoDAO.getHamburguesaTipo(nombreHamburguesa, tipoHamburguesa);

        if (hamburguesaTipo == null) {
            System.out.println("No se encontró el tipo de hamburguesa especificado.");
            return;
        }
        List<Integer> hamburguesaTipos = new ArrayList<>();
        hamburguesaTipos.add(hamburguesaTipo.getId());

        for (Integer tipo : hamburguesaTipos){
            System.out.println("Id: " + tipo);
        }

        Label pedidoLabel = new Label("(x" + cantidad + ") " + nombreHamburguesa + " " + tipoHamburguesa + " " + "($" + (int) precio + ")");

        List<Integer> toppingIds = new ArrayList<>();
        if (toppingList != null && !toppingList.isEmpty()) {
            for (Topping topping : toppingList) {
                System.out.println("Id top: " + topping.getId());
                toppingIds.add(topping.getId());
                if (topping.getPrecio() != null) {
                    precio += topping.getPrecio();
                }
            }
        }

        Label precioLabel = new Label(String.format("$%d", (int) precio));
        listPrecio.add((int) precio);
        actualizarTotal();
        Button deleteButton = new Button();
        try {
            Image image = new Image(getClass().getResource("/org/example/kaos/image/trash.png").toExternalForm());
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(12.0);
            imageView.setFitWidth(12.0);
            deleteButton.setGraphic(imageView);
            deleteButton.setVisible(false);
        } catch (NullPointerException e) {
            System.out.println("No se pudo cargar la imagen: " + e.getMessage());
        }
        DetallePedido detallePedido = new DetallePedido(detallesPedidosList.size() + 1, cantidad, hamburguesaTipos, toppingIds, precio);
        detallesPedidosList.add(detallePedido);

        final int precioFinal = (int) precio;
        deleteButton.setOnAction(event -> {
            detallePedidos.getChildren().remove(vBox);
            listPrecio.remove((Integer) precioFinal);
            actualizarTotal();
            detallesPedidosList.remove(detallePedido);
        });
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        pedidoBox.getChildren().addAll(pedidoLabel, spacer, precioLabel, deleteButton);
        vBox.getChildren().add(pedidoBox);
        if (toppingList != null && !toppingList.isEmpty()) {
            VBox toppingsBox = new VBox(5);
            toppingsBox.setPadding(new Insets(5, 0, 0, 0));
            for (Topping topping : toppingList) {
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

    public void deletePedidos() {
        deleteButtonsVisible = !deleteButtonsVisible;
        for (var node : detallePedidos.getChildren()) {
            if (node instanceof VBox) {
                VBox vBox = (VBox) node;
                for (var child : vBox.getChildren()) {
                    if (child instanceof HBox) {
                        HBox hBox = (HBox) child;
                        for (var hboxChild : hBox.getChildren()) {
                            if (hboxChild instanceof Button) {
                                Button deleteButton = (Button) hboxChild;
                                deleteButton.setVisible(deleteButtonsVisible);
                            }
                        }
                    }
                }
            }
        }
    }

    private void actualizarTotal() {
        int total = getPrecioTotalPedido();
        lblTotal.setText("TOTAL: $" + total);
    }

    private int getPrecioTotalPedido() {
        int precioTotal = 0;
        for (Integer precio : listPrecio) {
            precioTotal += precio;
        }
        return precioTotal;
    }

    public void aceptarPedido(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/kaos/window/datoCliente.fxml"));
            Parent root = loader.load();
            DatoClienteController controller = loader.getController();
            Stage stage = new Stage();
            controller.setStage(stage);
            stage.setWidth(380);
            stage.setHeight(250);
            stage.setTitle("Datos del Cliente");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
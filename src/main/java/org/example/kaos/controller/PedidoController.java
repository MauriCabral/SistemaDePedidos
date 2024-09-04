package org.example.kaos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.geometry.Insets;

import java.util.ArrayList;
import java.util.Optional;

import org.example.kaos.application.PedidoApplication;
import org.example.kaos.entity.DetallePedido;
import org.example.kaos.entity.Topping;
import org.example.kaos.repository.HamburguesaDAO;
import org.example.kaos.entity.Hamburgusa;
import org.example.kaos.service.PedidoService;

import java.util.List;

public class PedidoController {

    private PedidoApplication pedidoApp;
    private final HamburguesaDAO hamburguesaDAO = new HamburguesaDAO();
    private boolean deleteButtonsVisible = false;
    private List<DetallePedido> detallesPedidosList;
    private PedidoService pedidoService;

    @FXML
    private Pane menuPane, rightPane;
    @FXML
    private VBox detallePedidos;
    @FXML
    private Label lblTotal;

    @FXML
    private void initialize() {
        menuPane.setVisible(false);
        rightPane.setVisible(false);
        detallesPedidosList = new ArrayList<>();
        if (pedidoService == null) {
            pedidoService = new PedidoService();
        }
        pedidoService.setPedidoController(this);
    }

    public VBox getDetallePedidos() {
        return detallePedidos;
    }

    public void setPedidoApp(PedidoApplication pedidoApp) {
        this.pedidoApp = pedidoApp;
    }

    public void setPedidoService(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
        System.out.println("PedidoService set en el controlador: " + this.pedidoService);
    }

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
            pedidoApp.openDetalleWindow(selectedMenu.getNombre());
        } else {
            System.out.println("Menu no encontrado.");
        }
    }

    public void actualizarDetalles(String nombreHamburguesa, String tipoHamburguesa, int cantidad, double precio, List<Topping> toppingList) {
        VBox vBox = new VBox(5);
        vBox.setPadding(new Insets(2, 8, 0, 8));
        HBox pedidoBox = new HBox(5);

        double precioToppings = pedidoService.getPrecioTotalTopping(toppingList);
        double total = precio + precioToppings;

        int detalleId = detallesPedidosList.size() + 1;
        DetallePedido detallePedido = new DetallePedido(detalleId, cantidad, pedidoService.getHamburguesaTipo(nombreHamburguesa, tipoHamburguesa), toppingList, total);
        detallesPedidosList.add(detallePedido);
        pedidoService.addDetallePedido(nombreHamburguesa, tipoHamburguesa, cantidad, total, toppingList);

        Label pedidoLabel = new Label("(x" + cantidad + ") " + nombreHamburguesa + " " + tipoHamburguesa + " " + "($" + (int) precio + ")");
        Label precioLabel = new Label(String.format("$%d", (int) total));

        int precioTotal = pedidoService.actualizarTotal();
        lblTotal.setText("TOTAL: $" + (precioTotal));
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

        deleteButton.setUserData(detalleId);
        deleteButton.setOnAction(event -> {
            Integer id = (Integer) deleteButton.getUserData();
            System.out.println("Intentando eliminar detalle con ID: " + id);

            DetallePedido detalleEliminar = detallesPedidosList.stream()
                    .filter(detalle -> detalle.getId() == id)
                    .findFirst()
                    .orElse(null);

            if (detalleEliminar != null) {
                System.out.println("Eliminando detalle: " + detalleEliminar);
                detallesPedidosList.remove(detalleEliminar);
                pedidoService.removeDetallePedido(detalleEliminar);
                detallePedidos.getChildren().remove(vBox);
                int precioTotalActualizado = pedidoService.actualizarTotal();
                lblTotal.setText("TOTAL: $" + precioTotalActualizado);
                System.out.println("Total después de la eliminación y actualización: $" + precioTotalActualizado);
            } else {
                System.out.println("No se encontró el detalle con ID: " + id);
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        pedidoBox.getChildren().addAll(pedidoLabel, spacer, precioLabel, deleteButton);
        vBox.getChildren().add(pedidoBox);
        if (toppingList != null && !toppingList.isEmpty()) {
            VBox toppingsBox = new VBox(5);
            toppingsBox.setPadding(new Insets(5, 0, 0, 0));
            for (Topping topping : toppingList) {
                if (topping.getEsExtra()) {
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

    public void aceptarPedido(ActionEvent actionEvent) {
        System.out.println("PedidoService en PedidoController: " + pedidoService.hashCode());
        pedidoApp.openDatoClienteWindow(pedidoService);
    }

    public void cancelarPedido(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de Cancelación");
        alert.setHeaderText(null);
        alert.setContentText("¿Está seguro de que desea cancelar el pedido?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (pedidoService != null) {
                pedidoService.getDetallesPedidosList().clear();
            } else {
                System.out.println("PedidoService no está inicializado.");
            }
            detallesPedidosList.clear();
            detallePedidos.getChildren().clear();
            lblTotal.setText(" ");
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Pedido Cancelado");
            info.setHeaderText(null);
            info.setContentText("El pedido ha sido cancelado.");
            info.showAndWait();
        }
    }

    public void limpiarLblTotal() {
        lblTotal.setText("");
    }
}
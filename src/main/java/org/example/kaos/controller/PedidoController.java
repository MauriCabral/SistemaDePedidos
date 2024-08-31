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
import org.example.kaos.entity.HamburguesaTipo;
import org.example.kaos.entity.Topping;
import org.example.kaos.repository.HamburguesaDAO;
import org.example.kaos.repository.HamburguesaTipoDAO;
import org.example.kaos.entity.Hamburgusa;
import org.example.kaos.service.PedidoService;

import java.util.List;

public class PedidoController {

    private PedidoApplication pedidoApp;
    private final HamburguesaDAO hamburguesaDAO = new HamburguesaDAO();
    private final HamburguesaTipoDAO hamburguesaTipoDAO = new HamburguesaTipoDAO();
    private boolean deleteButtonsVisible = false;
    private List<DetallePedido> detallesPedidosList;
    private PedidoService pedidoService;

    @FXML
    private Pane menuPane, rightPane;
    @FXML
    private VBox detallePedidos;
    @FXML
    private Label lblTotal;

    private List<Integer> listPrecio = new ArrayList<>();

    @FXML
    private void initialize() {
        menuPane.setVisible(false);
        rightPane.setVisible(false);
        detallesPedidosList = new ArrayList<>();
    }

    public PedidoController() {this.pedidoService = new PedidoService();}

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

        System.out.println("Precio inicial: " + precio);
        List<Integer> toppingIds = new ArrayList<>();
        double precioTotal = precio;
        if (toppingList != null && !toppingList.isEmpty()) {
            for (Topping topping : toppingList) {
                System.out.println("Id top: " + topping.getId());
                toppingIds.add(topping.getId());
                if (topping.getPrecio() != null) {
                    precioTotal  += topping.getPrecio();
                }
            }
        }
        pedidoService.addDetallePedido(nombreHamburguesa, tipoHamburguesa, cantidad, precio, toppingList);

        Label pedidoLabel = new Label("(x" + cantidad + ") " + nombreHamburguesa + " " + tipoHamburguesa + " " + "($" + (int) precioTotal + ")");
        Label precioLabel = new Label(String.format("$%d", (int) precioTotal));
        System.out.println("Añadiendo precioTotal a listPrecio: " + (int) precioTotal);
        listPrecio.add((int) precioTotal);
        System.out.println("Contenido de listPrecio después de agregar: " + listPrecio);
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
        DetallePedido detallePedido = new DetallePedido(detallesPedidosList.size() + 1, cantidad, hamburguesaTipos, toppingIds, precioTotal);
        detallesPedidosList.add(detallePedido);

        final int precioFinal = (int) precio;
        deleteButton.setOnAction(event -> {
            detallePedidos.getChildren().remove(vBox);
            listPrecio.remove((Integer) precioFinal);
            System.out.println("Contenido de listPrecio después de eliminar: " + listPrecio);
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
        System.out.println("Llamando a actualizarTotal()");
        int total = pedidoService.getPrecioTotalPedido();
        System.out.println("Contenido de listPrecio en actualizarTotal: " + listPrecio);
        System.out.println("Total calculado en actualizarTotal: " + total);
        if (total == 0) {
            System.out.println("El precio total es inválido: 0");
        }
        lblTotal.setText("TOTAL: $" + total);
    }

    public void aceptarPedido(ActionEvent actionEvent) {
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
            listPrecio.clear();
            actualizarTotal();
            detallePedidos.getChildren().clear();
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Pedido Cancelado");
            info.setHeaderText(null);
            info.setContentText("El pedido ha sido cancelado.");
            info.showAndWait();
        }
    }
}
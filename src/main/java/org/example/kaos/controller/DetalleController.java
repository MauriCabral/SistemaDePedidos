package org.example.kaos.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.kaos.entity.Topping;
import org.example.kaos.manager.ControllerManager;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.example.kaos.service.DetalleService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DetalleController implements Initializable {
    @FXML
    private Label nombre;
    @FXML
    private ComboBox<String> comboBoxTipo;
    @FXML
    private Button btnCancelar, btnAceptar;
    @FXML
    private Label counterLabel;
    @FXML
    private CheckBox cmbCheddar, cmbBacon, cmbLechuga, cmbTomate, cmbCebolla, cmbCebollaCrisp, cmbTomateConf;
    @FXML
    private CheckBox cmbCheddar1, cmbBacon1, cmbLechuga1, cmbTomate1, cmbCebolla1, cmbCebollaCrisp1, cmbTomateConf1, quitarSalsa;

    private DetalleService detalleService;
    private Stage stage;
    private int count = 1;
    private int pedidoId;

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        btnCancelar.setOnAction(event -> closeWindow());
        btnAceptar.setOnAction(event -> aceptarPedido());
        detalleService = new DetalleService();
        detalleService.setCheckBoxes(cmbCheddar, cmbBacon, cmbLechuga, cmbTomate, cmbCebolla, cmbCebollaCrisp, cmbTomateConf,
                cmbCheddar1, cmbBacon1, cmbLechuga1, cmbTomate1, cmbCebolla1, cmbCebollaCrisp1, cmbTomateConf1, quitarSalsa);

    }

//    public void setDetalleService(DetalleService detalleService) {
//        this.detalleService = detalleService;
//        System.out.println("DetalleService en DatoClienteController: " + detalleService.hashCode());
//    }

    @FXML
    private void handleComboBoxAction() {
        String selectedType = comboBoxTipo.getValue();
        System.out.println("Selected type: " + selectedType);
    }

    public void setDetalle(String nombreMenu) {
        nombre.setText(nombreMenu);
        comboBoxTipo.setItems(detalleService.getTiposHamburguesa(nombreMenu));
    }

    public void setPedidoId(int pedidoId) {
        this.pedidoId = pedidoId;
    }

    @FXML
    private void incrementCounter() {
        count++;
        updateCounterLabel();
    }

    @FXML
    private void decrementCounter() {
        if (count > 0) {
            count--;
        }
        updateCounterLabel();
    }

    private void updateCounterLabel() {
        counterLabel.setText(String.valueOf(count));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void aceptarPedido() {
        String tipo = comboBoxTipo.getValue();
        int cantidad = count;
        String nombreProducto = nombre.getText();
        if (tipo == null) {
            showError("Porfavor seleccione un tipo de hamburguesa");
        }
        double precioProducto = detalleService.obtenerPrecio(tipo, cantidad, nombreProducto);

//        List<Topping> toppingListExtra = toppingService.getExtraToppings(
//                cmbCheddar, cmbBacon, cmbLechuga, cmbTomate, cmbCebolla, cmbCebollaCrisp, cmbTomateConf);
//
//        List<Topping> toppingListRemove = toppingService.getRemovedToppings(
//                cmbCheddar1, cmbBacon1, cmbLechuga1, cmbTomate1, cmbCebolla1, cmbCebollaCrisp1, cmbTomateConf1, quitarSalsa);

        detalleService.updateExtraToppings();
        detalleService.updateRemovedToppings();
        List<Topping> toppingListExtra = detalleService.getToppingListExtra();
        List<Topping> toppingListRemove = detalleService.getToppingListRemove();

//        double totalPrecio = precioProducto + toppingListExtra.stream().mapToDouble(Topping::getPrecio).sum();

        actualizarVentanaPedido(nombreProducto, tipo, cantidad, (int) precioProducto, toppingListExtra, toppingListRemove);

        if (stage != null) {
            stage.close();
        }
    }

    public void showError(String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void actualizarVentanaPedido(String nombre, String tipo, int cantidad, double precio, List<Topping> toppingListExtra, List<Topping> toppingListRemove) {
        PedidoController pedidoCtrl = ControllerManager.getInstance().getPedidoController();

        if (pedidoCtrl == null) {
            System.out.println("Controlador de Pedido no encontrado. Verifica si está correctamente inicializado.");
            return;
        }

        for (Topping topping : toppingListExtra) {
            System.out.println("Topping Extra: " + topping.getNombre() + ", Precio: " + topping.getPrecio());
        }
        for (Topping topping : toppingListRemove) {
            System.out.println("Topping Removido: " + topping.getNombre());
        }

        System.out.println("Invocando Platform.runLater...");
        Platform.runLater(() -> {
            System.out.println("Dentro de Platform.runLater...");
            pedidoCtrl.actualizarDetalles(nombre, tipo, cantidad, precio, toppingListExtra, toppingListRemove);
        });
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}
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
        detalleService = new DetalleService();
        btnCancelar.setOnAction(event -> closeWindow());
        btnAceptar.setOnAction(event -> aceptarPedido());
        detalleService.setCheckBoxes(cmbCheddar, cmbBacon, cmbLechuga, cmbTomate, cmbCebolla, cmbCebollaCrisp, cmbTomateConf,
                cmbCheddar1, cmbBacon1, cmbLechuga1, cmbTomate1, cmbCebolla1, cmbCebollaCrisp1, cmbTomateConf1, quitarSalsa);
    }

    @FXML
    private void handleComboBoxAction() {
        String selectedType = comboBoxTipo.getValue();
        System.out.println("Selected type: " + selectedType);
    }

    public void setDetalle(String nombreMenu) {
        nombre.setText(nombreMenu);
        comboBoxTipo.setItems(detalleService.getTiposHamburguesa());
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
        if (count > 1) {
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
            return;
        }
        double precioProducto = detalleService.obtenerPrecio(tipo, cantidad, nombreProducto);

        List<Topping> toppingList = detalleService.getSelectedToppings();

        actualizarVentanaPedido(nombreProducto, tipo, cantidad, (int) precioProducto, toppingList);

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

    private void actualizarVentanaPedido(String nombre, String tipo, int cantidad, double precio, List<Topping> toppingList) {
        PedidoController pedidoCtrl = ControllerManager.getInstance().getPedidoController();

        if (pedidoCtrl == null) {
            System.out.println("Controlador de Pedido no encontrado. Verifica si está correctamente inicializado.");
            return;
        }
        Platform.runLater(() -> {
            pedidoCtrl.actualizarDetalles(nombre, tipo, cantidad, precio, toppingList);
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
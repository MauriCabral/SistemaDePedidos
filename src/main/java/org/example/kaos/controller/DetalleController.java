package org.example.kaos.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.kaos.entity.Topping;
import org.example.kaos.manager.ControllerManager;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.example.kaos.service.DetalleService;

import java.net.URL;
import java.util.ArrayList;
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
    private CheckBox cmbCheddar, cmbBacon, cmbLechuga, cmbTomate, cmbCebolla, cmbCebollaCrisp, cmbTomateConf, cmbMedallon;
    @FXML
    private CheckBox cmbCheddar1, cmbBacon1, cmbLechuga1, cmbTomate1, cmbCebolla1, cmbCebollaCrisp1, cmbTomateConf1, quitarSalsa;
    @FXML
    private TextArea txtObservaciones;
    @FXML
    private TextField toppingCh, toppingB, toppingL, toppingT, toppingC, toppingCC, toppingTC, toppingM;
    @FXML
    private Pane panelPreciosTopping, paneleditar;

    private DetalleService detalleService;
    private Stage stage;
    private int count = 1;
    private int pedidoId;
    private List<Topping> toppingList;

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        detalleService = new DetalleService();
        btnCancelar.setOnAction(event -> closeWindow());
        btnAceptar.setOnAction(event -> aceptarPedido());
        detalleService.setCheckBoxes(cmbCheddar, cmbBacon, cmbLechuga, cmbTomate, cmbCebolla, cmbCebollaCrisp, cmbTomateConf, cmbMedallon,
                cmbCheddar1, cmbBacon1, cmbLechuga1, cmbTomate1, cmbCebolla1, cmbCebollaCrisp1, cmbTomateConf1, quitarSalsa);
        panelPreciosTopping.setVisible(false);
        toppingList = new ArrayList<>();
        Platform.runLater(() -> btnAceptar.requestFocus());
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
        if(panelPreciosTopping.isVisible() && toppingList != null){
            updatePreciosTopping();
        } else {
            String tipo = comboBoxTipo.getValue();
            int cantidad = count;
            String nombreProducto = nombre.getText();
            if (tipo == null) {
                showError("Porfavor seleccione un tipo de hamburguesa");
                return;
            }
            double precioProducto = detalleService.obtenerPrecio(tipo, cantidad, nombreProducto);

            List<Topping> toppingList = detalleService.getSelectedToppings();

            actualizarVentanaPedido(nombreProducto, tipo, cantidad, (int) precioProducto, toppingList, txtObservaciones.getText().trim());

            if (stage != null) {
                stage.close();
            }
        }
    }

    private void updatePreciosTopping() {
        for (Topping topp : toppingList) {
            try {
                double nuevoPrecio = 0.0;
                switch (topp.getId()) {
                    case 1:
                        nuevoPrecio = Double.parseDouble(toppingCh.getText());
                        break;
                    case 2:
                        nuevoPrecio = Double.parseDouble(toppingB.getText());
                        break;
                    case 3:
                        nuevoPrecio = Double.parseDouble(toppingL.getText());
                        break;
                    case 4:
                        nuevoPrecio = Double.parseDouble(toppingT.getText());
                        break;
                    case 5:
                        nuevoPrecio = Double.parseDouble(toppingC.getText());
                        break;
                    case 6:
                        nuevoPrecio = Double.parseDouble(toppingCC.getText());
                        break;
                    case 7:
                        nuevoPrecio = Double.parseDouble(toppingTC.getText());
                        break;
                    case 8:
                        nuevoPrecio = Double.parseDouble(toppingM.getText());
                        break;
                    default:
                        continue;
                }
                topp.setPrecio(nuevoPrecio);
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingresa un número válido para el topping " + topp.getId());
            }
        }
        int res = detalleService.actualizarPreciosTopping(toppingList);
        if(res>0){
            panelPreciosTopping.setVisible(false);
        }
        mostrarMensaje(res);
    }

    private void mostrarMensaje(int resultado) {
        Alert alert = new Alert(resultado == 1 ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle("Actualización de Precios");
        alert.setHeaderText(null);
        alert.setContentText(resultado == 1 ? "✅ Precios actualizados correctamente." : "❌ No se pudieron actualizar los precios.");
        alert.showAndWait();
    }


    public void showError(String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void actualizarVentanaPedido(String nombre, String tipo, int cantidad, double precio, List<Topping> toppingList, String txtObservaciones) {
        PedidoController pedidoCtrl = ControllerManager.getInstance().getPedidoController();
        if (pedidoCtrl == null) {
            System.out.println("Controlador de Pedido no encontrado. Verifica si está correctamente inicializado.");
            return;
        }
        Platform.runLater(() -> {pedidoCtrl.actualizarDetalles(nombre, tipo, cantidad, precio, toppingList, txtObservaciones);});
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    public void editarTopping(ActionEvent actionEvent) {
        if (paneleditar.getStyle().contains("-fx-border-color: blue;")) {
            paneleditar.setStyle("");
            panelPreciosTopping.setVisible(false);
        } else {
            paneleditar.setStyle("-fx-border-color: blue; -fx-border-width: 2px;");
            panelPreciosTopping.setVisible(true);
            cargarPreciosTopping();
        }
    }

    private void cargarPreciosTopping() {
        toppingList = detalleService.getPrecioTopping();
        for (Topping topp : toppingList) {
            switch (topp.getId()) {
                case 1:
                    toppingCh.setText(String.valueOf(topp.getPrecio()));
                    break;
                case 2:
                    toppingB.setText(String.valueOf(topp.getPrecio()));
                    break;
                case 3:
                    toppingL.setText(String.valueOf(topp.getPrecio()));
                    break;
                case 4:
                    toppingT.setText(String.valueOf(topp.getPrecio()));
                    break;
                case 5:
                    toppingC.setText(String.valueOf(topp.getPrecio()));
                    break;
                case 6:
                    toppingCC.setText(String.valueOf(topp.getPrecio()));
                    break;
                case 7:
                    toppingTC.setText(String.valueOf(topp.getPrecio()));
                    break;
                case 8:
                    toppingM.setText(String.valueOf(topp.getPrecio()));
                    break;
                default:
                    break;
            }
        }
    }
}
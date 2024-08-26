package org.example.kaos.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.kaos.dao.tipoHamburguesaDAO;
import org.example.kaos.dao.toppingsDAO;
import org.example.kaos.entity.HamburguesaTipo;
import org.example.kaos.dao.hamburguesaTipoDAO;
import org.example.kaos.entity.Toppings;
import org.example.kaos.manager.ControllerManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DetalleController implements Initializable {
    public Button btnPedidoRapido;
    @FXML
    private Label nombre;
    @FXML
    private Label precio;
    private int count = 1;
    @FXML
    private ComboBox<String> comboBoxTipo;
    private final tipoHamburguesaDAO typeDAO = new tipoHamburguesaDAO();
    private final hamburguesaTipoDAO hamburguesaTipoDAO = new hamburguesaTipoDAO();
    private Stage pedidoStage;
    @FXML
    private CheckBox cmbCheddar, cmbBacon, cmbLechuga, cmbTomate, cmbCebolla, cmbCebollaCrisp, cmbTomateConf, cmbCambiarSalsa;
    @FXML
    private CheckBox cmbCheddar1, cmbBacon1, cmbLechuga1, cmbTomate1, cmbCebolla1, cmbCebollaCrisp1, cmbTomateConf1, cmbCambiarSalsa1;
    @FXML
    private TextField txtCambiarSalsa;

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> types = typeDAO.getAllTipoHamburguesa();
        comboBoxTipo.setItems(types);
    }

    @FXML
    private void handleComboBoxAction() {
        String selectedType = comboBoxTipo.getValue();
        System.out.println("Selected type: " + selectedType);
    }

    public void setDetalle(String nombreMenu) {
        nombre.setText(nombreMenu);
    }

    @FXML
    private Label counterLabel;

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

    @FXML
    private void pedidoRapido() {
        String tipo = comboBoxTipo.getValue();
        int cantidad = count;
        String nombreProducto = nombre.getText();
        double precioProducto = obtenerPrecio(tipo, cantidad);

        List<Toppings> toppingsList = getSelectedToppings();

        if (precioProducto != -1) {
            actualizarVentanaPedido(nombreProducto, tipo, cantidad, precioProducto, toppingsList);
        } else {
            mostrarError();
        }
    }

    private double obtenerPrecio(String tipo, int cantidad) {
        HamburguesaTipo hamburguesaTipo = hamburguesaTipoDAO.getHamburguesaTipo(nombre.getText(), tipo);
        if (hamburguesaTipo != null) {
            double precioBase = hamburguesaTipo.getPrecios();
            System.out.println(nombre.getText() + " (x" + cantidad + ") " + "$ " + precioBase);
            return precioBase * cantidad;
        }
        return -1;
    }

    private void actualizarVentanaPedido(String nombre, String tipo, int cantidad, double precio, List<Toppings> toppingsList) {
        PedidoController pedidoCtrl = ControllerManager.getInstance().getPedidoController();
        if (pedidoCtrl != null) {
            Platform.runLater(() -> pedidoCtrl.actualizarDetalles(nombre, tipo, cantidad, precio, toppingsList));
        } else {
            System.out.println("Controlador de Pedido no encontrado.");
        }
    }

//    public void setPedidoStage(Stage stage) {
//        this.pedidoStage = stage;
//    }

    private void mostrarError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Error al calcular el precio.");
        alert.showAndWait();
    }

    @FXML
    private void cambiarSalsaCheckBox() {
        txtCambiarSalsa.setVisible(cmbCambiarSalsa.isSelected());
    }

    private List<Toppings> getSelectedToppings() {
        List<Toppings> toppingsList = new ArrayList<>();
        toppingsDAO toppingDAO = new toppingsDAO();

        try {
            if (cmbCheddar.isSelected()) toppingsList.add(toppingsDAO.getToppingById(1)); // Asume ID
            if (cmbBacon.isSelected()) toppingsList.add(toppingsDAO.getToppingById(2));
            if (cmbLechuga.isSelected()) toppingsList.add(toppingsDAO.getToppingById(3));
            if (cmbTomate.isSelected()) toppingsList.add(toppingsDAO.getToppingById(4));
            if (cmbCebolla.isSelected()) toppingsList.add(toppingsDAO.getToppingById(5));
            if (cmbCebollaCrisp.isSelected()) toppingsList.add(toppingsDAO.getToppingById(6));
            if (cmbTomateConf.isSelected()) toppingsList.add(toppingsDAO.getToppingById(7));
            if (cmbCambiarSalsa.isSelected() && !txtCambiarSalsa.getText().isEmpty()) {
                toppingsList.add(new Toppings(8, "Salsa: " + txtCambiarSalsa.getText(), 0.0));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toppingsList;
    }
}
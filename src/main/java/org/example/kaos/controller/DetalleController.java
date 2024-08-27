package org.example.kaos.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.kaos.dao.tipoHamburguesaDAO;
import org.example.kaos.dao.toppingsDAO;
import org.example.kaos.entity.HamburguesaTipo;
import org.example.kaos.dao.hamburguesaTipoDAO;
import org.example.kaos.entity.Toppings;
import org.example.kaos.manager.ControllerManager;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DetalleController implements Initializable {
    @FXML
    private Label nombre;
    @FXML
    private ComboBox<String> comboBoxTipo;
    @FXML
    private CheckBox cmbCheddar, cmbBacon, cmbLechuga, cmbTomate, cmbCebolla, cmbCebollaCrisp, cmbTomateConf, cmbCambiarSalsa;
    @FXML
    private CheckBox cmbCheddar1, cmbBacon1, cmbLechuga1, cmbTomate1, cmbCebolla1, cmbCebollaCrisp1, cmbTomateConf1, quitarSalsa;
    @FXML
    private TextField txtCambiarSalsa;
    private int count = 1;
    private final tipoHamburguesaDAO typeDAO = new tipoHamburguesaDAO();
    private final hamburguesaTipoDAO hamburguesaTipoDAO = new hamburguesaTipoDAO();

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
    private void aceptarPedido() {
        String tipo = comboBoxTipo.getValue();
        int cantidad = count;
        String nombreProducto = nombre.getText();
        double precioProducto = obtenerPrecio(tipo, cantidad);
        if (tipo == null) {
            showError("Porfavor seleccione un tipo");
        }
        else {
            List<Toppings> toppingsList = getSelectedToppings();
            actualizarVentanaPedido(nombreProducto, tipo, cantidad, precioProducto, toppingsList);        }
    }

    public void showError(String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(content);
        alert.showAndWait();
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
        for (Toppings topping : toppingsList) {
            System.out.println("Topping: " + topping.getNombre() + ", Precio: " + topping.getPrecio());
        }
        if (pedidoCtrl != null) {
            Platform.runLater(() -> pedidoCtrl.actualizarDetalles(nombre, tipo, cantidad, precio, toppingsList));
        } else {
            System.out.println("Controlador de Pedido no encontrado.");
        }
    }

    @FXML
    private void cambiarSalsaCheckBox() {
        txtCambiarSalsa.setVisible(cmbCambiarSalsa.isSelected());
    }

    private List<Toppings> getSelectedToppings() {
        List<Toppings> toppingsList = new ArrayList<>();

        try {
            if (cmbCheddar.isSelected()) toppingsList.add(toppingsDAO.getToppingById(1, true));
            if (cmbBacon.isSelected()) toppingsList.add(toppingsDAO.getToppingById(2, true));
            if (cmbLechuga.isSelected()) toppingsList.add(toppingsDAO.getToppingById(3, true));
            if (cmbTomate.isSelected()) toppingsList.add(toppingsDAO.getToppingById(4, true));
            if (cmbCebolla.isSelected()) toppingsList.add(toppingsDAO.getToppingById(5, true));
            if (cmbCebollaCrisp.isSelected()) toppingsList.add(toppingsDAO.getToppingById(6, true));
            if (cmbTomateConf.isSelected()) toppingsList.add(toppingsDAO.getToppingById(7, true));
            if (cmbCheddar1.isSelected()) toppingsList.add(toppingsDAO.getToppingById(1, false));
            if (cmbBacon1.isSelected()) toppingsList.add(toppingsDAO.getToppingById(2, false));
            if (cmbLechuga1.isSelected()) toppingsList.add(toppingsDAO.getToppingById(3, false));
            if (cmbTomate1.isSelected()) toppingsList.add(toppingsDAO.getToppingById(4, false));
            if (cmbCebolla1.isSelected()) toppingsList.add(toppingsDAO.getToppingById(5, false));
            if (cmbCebollaCrisp1.isSelected()) toppingsList.add(toppingsDAO.getToppingById(6, false));
            if (cmbTomateConf1.isSelected()) toppingsList.add(toppingsDAO.getToppingById(7, false));
            if (cmbCambiarSalsa.isSelected() && !txtCambiarSalsa.getText().isEmpty()) {
                toppingsList.add(new Toppings(8, "Salsa" + txtCambiarSalsa.getText(), true));
            }
            if (quitarSalsa.isSelected() && !txtCambiarSalsa.getText().isEmpty()) {
                toppingsList.add(new Toppings(8, "Salsa", false));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        toppingsList.sort((t1, t2) -> {
            if (t1 == null && t2 == null) {
                return 0;
            }
            if (t1 == null) {
                return 1;
            }
            if (t2 == null) {
                return -1;
            }
            return Boolean.compare(t2.esExtra(), t1.esExtra());
        });
        return toppingsList;
    }
}
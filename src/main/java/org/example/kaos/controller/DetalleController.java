package org.example.kaos.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.kaos.entity.Topping;
import org.example.kaos.repository.TipoHamburguesaDAO;
import org.example.kaos.repository.ToppingDAO;
import org.example.kaos.entity.HamburguesaTipo;
import org.example.kaos.repository.HamburguesaTipoDAO;
import org.example.kaos.manager.ControllerManager;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.example.kaos.service.DetalleService;
import org.example.kaos.service.PedidoService;
import org.example.kaos.service.ToppingService;

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
    @FXML
    private Button btnCancelar, btnAceptar;
    @FXML
    private Pane pnlDetalle;
    @FXML
    private Label counterLabel;
    
    private Stage stage;
    private int count = 1;

    private final DetalleService detalleService = new DetalleService();
    private final ToppingService toppingService = new ToppingService();
    private final PedidoService pedidoService = new PedidoService();

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        btnCancelar.setOnAction(event -> closeWindow());
        btnAceptar.setOnAction(event -> aceptarPedido());
    }

    @FXML
    private void handleComboBoxAction() {
        String selectedType = comboBoxTipo.getValue();
        System.out.println("Selected type: " + selectedType);
    }

    public void setDetalle(String nombreMenu) {
        nombre.setText(nombreMenu);
        comboBoxTipo.setItems(detalleService.getTiposHamburguesa(nombreMenu));
    }

//    private void getTipoHamburguesa(String nombreMenu) {
//        ObservableList<String> tiposHamburguesa = typeDAO.getAllTipoHamburguesa();
//        if (tiposHamburguesa != null && !tiposHamburguesa.isEmpty()) {
//            comboBoxTipo.setItems(tiposHamburguesa);
//        } else {
//            showError("No se encontraron tipos de hamburguesa para: " + nombreMenu);
//        }
//    }

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
        double precioProducto = detalleService.obtenerPrecio(tipo, cantidad, nombreProducto);
        if (tipo == null) {
            showError("Porfavor seleccione un tipo");
        }
        else {
            List<Topping> toppingList = toppingService.getSelectedToppings(cmbCheddar, cmbBacon, cmbLechuga, cmbTomate, cmbCebolla, cmbCebollaCrisp, cmbTomateConf, cmbCheddar1, cmbBacon1, cmbLechuga1, cmbTomate1, cmbCebolla1, cmbCebollaCrisp1, cmbTomateConf1, cmbCambiarSalsa, quitarSalsa, txtCambiarSalsa);
            actualizarVentanaPedido(nombreProducto, tipo, cantidad, precioProducto, toppingList);
            if (stage != null) {
                stage.close();
            }
        }
    }

    public void showError(String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(content);
        alert.showAndWait();
    }

//    private double obtenerPrecio(String tipo, int cantidad) {
//        HamburguesaTipo hamburguesaTipo = hamburguesaTipoDAO.getHamburguesaTipo(nombre.getText(), tipo);
//        if (hamburguesaTipo != null) {
//            double precioBase = hamburguesaTipo.getPrecios();
//            System.out.println(nombre.getText() + " (x" + cantidad + ") " + "$ " + precioBase);
//            return precioBase * cantidad;
//        }
//        return -1;
//    }

    private void actualizarVentanaPedido(String nombre, String tipo, int cantidad, double precio, List<Topping> toppingList) {
        PedidoController pedidoCtrl = ControllerManager.getInstance().getPedidoController();
        for (Topping topping : toppingList) {
            System.out.println("Topping: " + topping.getNombre() + ", Precio: " + topping.getPrecio());
        }
        if (pedidoCtrl != null) {
            Platform.runLater(() -> pedidoCtrl.actualizarDetalles(nombre, tipo, cantidad, precio, toppingList));
        } else {
            System.out.println("Controlador de Pedido no encontrado.");
        }
    }

    @FXML
    private void cambiarSalsaCheckBox() {
        txtCambiarSalsa.setVisible(cmbCambiarSalsa.isSelected());
    }

//    private List<Topping> getSelectedToppings() {
//        List<Topping> toppingList = new ArrayList<>();
//
//        try {
//            if (cmbCheddar.isSelected()) toppingList.add(ToppingDAO.getToppingById(1, true));
//            if (cmbBacon.isSelected()) toppingList.add(ToppingDAO.getToppingById(2, true));
//            if (cmbLechuga.isSelected()) toppingList.add(ToppingDAO.getToppingById(3, true));
//            if (cmbTomate.isSelected()) toppingList.add(ToppingDAO.getToppingById(4, true));
//            if (cmbCebolla.isSelected()) toppingList.add(ToppingDAO.getToppingById(5, true));
//            if (cmbCebollaCrisp.isSelected()) toppingList.add(ToppingDAO.getToppingById(6, true));
//            if (cmbTomateConf.isSelected()) toppingList.add(ToppingDAO.getToppingById(7, true));
//            if (cmbCheddar1.isSelected()) toppingList.add(ToppingDAO.getToppingById(1, false));
//            if (cmbBacon1.isSelected()) toppingList.add(ToppingDAO.getToppingById(2, false));
//            if (cmbLechuga1.isSelected()) toppingList.add(ToppingDAO.getToppingById(3, false));
//            if (cmbTomate1.isSelected()) toppingList.add(ToppingDAO.getToppingById(4, false));
//            if (cmbCebolla1.isSelected()) toppingList.add(ToppingDAO.getToppingById(5, false));
//            if (cmbCebollaCrisp1.isSelected()) toppingList.add(ToppingDAO.getToppingById(6, false));
//            if (cmbTomateConf1.isSelected()) toppingList.add(ToppingDAO.getToppingById(7, false));
//            if (cmbCambiarSalsa.isSelected() && !txtCambiarSalsa.getText().isEmpty()) {
//                toppingList.add(new Topping(8, "Salsa" + txtCambiarSalsa.getText(), true));
//            }
//            if (quitarSalsa.isSelected() && !txtCambiarSalsa.getText().isEmpty()) {
//                toppingList.add(new Topping(8, "Salsa", false));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        toppingList.sort((t1, t2) -> {
//            if (t1 == null && t2 == null) {
//                return 0;
//            }
//            if (t1 == null) {
//                return 1;
//            }
//            if (t2 == null) {
//                return -1;
//            }
//            return Boolean.compare(t2.esExtra(), t1.esExtra());
//        });
//        return toppingList;
//    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}
package org.example.kaos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.kaos.entity.TipoPago;
import org.example.kaos.repository.TipoPagoDAO;

import java.util.List;

public class DatoClienteController {

    @FXML
    private TextField txtNombreCliente;

    @FXML
    private TextField txtDireccion;

    @FXML
    private ComboBox<String> cmbFormaPago;

    private Stage stage;

    @FXML
    public void initialize() {
        cargarFormasPago();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void cargarFormasPago() {
        TipoPagoDAO formaPagoDAO = new TipoPagoDAO();
        List<TipoPago> tipoPagos = formaPagoDAO.getAllTipoPago();
        for (TipoPago tipoPago : tipoPagos) {
            cmbFormaPago.getItems().add(tipoPago.getNombre());
        }
    }

    @FXML
    private void aceptarDatos() {
        String nombreCliente = txtNombreCliente.getText();
        String direccion = txtDireccion.getText();
        String formaPago = cmbFormaPago.getValue();
        System.out.println(nombreCliente + " " + direccion + " " + formaPago);
        stage.close();
    }

    @FXML
    private void cancelarVentana() {
        stage.close();
    }
}

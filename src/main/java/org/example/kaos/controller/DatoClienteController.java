package org.example.kaos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.kaos.entity.DetallePedido;
import org.example.kaos.entity.TipoPago;
import org.example.kaos.entity.ToppingPedido;
import org.example.kaos.manager.ControllerManager;
import org.example.kaos.repository.TipoPagoDAO;
import org.example.kaos.repository.ToppingDAO;
import org.example.kaos.service.PedidoService;
import org.example.kaos.service.DetalleService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class DatoClienteController {

    private final TipoPagoDAO tipoPagoDAO = new TipoPagoDAO();
    private final ToppingDAO toppingDAO = new ToppingDAO();
    private PedidoService pedidoService;
    private DetalleService detalleService;
    private Stage stage;

    @FXML
    private TextField txtNombreCliente, txtDireccion, txtCostoEnvio;
    @FXML
    private ComboBox<String> cmbFormaPago;

    @FXML
    public void initialize() {
        cargarFormasPago();
        txtCostoEnvio.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtCostoEnvio.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        if (detalleService == null) {
            this.detalleService = new DetalleService();
        }
    }

    public void setPedidoService(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
        System.out.println("PedidoService en DatoClienteController: " + pedidoService.hashCode());
    }

    public void setDetalleService(DetalleService detalleService) {
        this.detalleService = detalleService;
        System.out.println("DetalleService en DatoClienteController: " + detalleService.hashCode());
        if (this.detalleService == null) {
            throw new IllegalStateException("DetalleService no ha sido inicializado");
        }
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
    private void aceptarDatos() throws SQLException {
        StringBuilder errores = new StringBuilder();
        if (txtNombreCliente.getText().isEmpty()) {
            errores.append("Ingrese el nombre del cliente.\n");
        }
        if (txtDireccion.getText().isEmpty()) {
            errores.append("Ingrese la dirección del pedido.\n");
        }
        if (cmbFormaPago.getValue() == null || cmbFormaPago.getValue().isEmpty()) {
            errores.append("Seleccione la forma de pago.\n");
        }
        if (txtCostoEnvio.getText().isEmpty()) {
            errores.append("Ingrese el precio de envío.\n");
        }
        if (errores.length() > 0) {
            showError(errores.toString());
            return;
        }

        String nombreCliente = txtNombreCliente.getText();
        String direccion = txtDireccion.getText();
        String formaPago = cmbFormaPago.getValue();
        double costoEnvio = Double.parseDouble(txtCostoEnvio.getText());
        int idTipoPago = tipoPagoDAO.getIdTipoPagoFromNombre(formaPago);
        System.out.println(nombreCliente + " " + direccion + " " + formaPago + " " + costoEnvio + " " + idTipoPago);
        int precioTotal = (int) pedidoService.getPrecioTotalPedido();
        System.out.println("precio total pedido service: " + precioTotal);
        if (precioTotal <= 0) {
            System.out.println("El precio total es inválido: " + precioTotal);
            return;
        }

        List<DetallePedido> detallesPedidosList = pedidoService.getDetallesPedidosList();

        JSONArray detallesJson = new JSONArray();

        for (DetallePedido detalle : detallesPedidosList) {
            JSONObject detalleJson = new JSONObject();
            detalleJson.put("cantidad", detalle.getCantidad());
            List<Integer> hamburguesaTipoIds = detalle.getTiposHamburguesa();
            if (!hamburguesaTipoIds.isEmpty()) {
                detalleJson.put("hamburguesa_tipo_id", hamburguesaTipoIds.get(0));
                detalleJson.put("precio_unitario", detalle.getPrecio_unitario());
                JSONArray toppingsJson = new JSONArray();

                List<ToppingPedido> detalleToppingPedidosList = pedidoService.getDetallesToppingPedidosList();

                if (detalleToppingPedidosList.isEmpty()){
                    detalleJson.put("toppings", JSONObject.NULL);
                } else {
                    for (ToppingPedido detalleTopping : detalleToppingPedidosList) {
                        JSONObject toppingJson = new JSONObject();
                        toppingJson.put("id_topping", detalleTopping.getIdTopping());
                        toppingJson.put("Agregado", detalleTopping.isAgregado() ? 1 : 0);

                        toppingsJson.put(toppingJson);
                        detalleJson.put("toppings", toppingsJson);
                    }
                }
            }
            detallesJson.put(detalleJson);
        }

        boolean exito = pedidoService.insertarPedido(
                nombreCliente,
                direccion,
                new Timestamp(System.currentTimeMillis()),
                idTipoPago,
                costoEnvio,
                precioTotal,
                detallesJson
        );
        if (exito) {
            showConfirmation("El pedido se ha insertado correctamente.");
            PedidoController pedidoController = ControllerManager.getInstance().getPedidoController();
            if (pedidoController != null) {
                pedidoService.getDetallesPedidosList().clear();
                pedidoController.limpiarLblTotal();
                pedidoController.getDetallePedidos().getChildren().clear();
            } else {
                System.out.println("PedidoController es null.");
            }
            if (stage != null) {
                stage.close();
            } else {
                System.out.println("El objeto Stage es null");
            }
        } else {
            showError("No se pudo insertar el pedido. Intente nuevamente.");
        }
    }

    @FXML
    private void cancelarVentana() {
        stage.close();
    }

    public void showError(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void showConfirmation(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

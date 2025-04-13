package org.example.kaos.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.kaos.entity.*;
import org.example.kaos.manager.ControllerManager;
import org.example.kaos.repository.TipoPagoDAO;
import org.example.kaos.service.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.List;

public class DatoClienteController {

    private final TipoPagoDAO tipoPagoDAO = new TipoPagoDAO();
    private PedidoService pedidoService = new PedidoService();
    private DetalleService detalleService = new DetalleService();
    private TicketPrinterService ticketPrinterService = new TicketPrinterService();
    private ExtraService extraService = new ExtraService();
    private Stage stage;
    private int idPedidoImprimir = 0;

    @FXML
    private TextField txtNombreCliente, txtDireccion, txtCostoEnvio, txtDescuento;
    @FXML
    private ComboBox<String> cmbFormaPago;
    @FXML
    private Button btnAceptar;
    @FXML
    private CheckBox chkDescuento;

    @FXML
    public void initialize() {
        cargarFormasPago();
        txtCostoEnvio.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtCostoEnvio.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        txtDescuento.setVisible(false);
        Platform.runLater(() -> btnAceptar.requestFocus());
    }

    public void setPedidoService(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
        System.out.println("PedidoService en DatoClienteController: " + pedidoService.hashCode());
    }

    public void setDetalleService(DetalleService detalleService) {
        this.detalleService = detalleService;
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
    private void aceptarDatos() {
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
        int precioDescuento = txtDescuento.getText().isEmpty() ? 0 : Integer.parseInt(txtDescuento.getText());
        if(chkDescuento.isSelected() && !txtDescuento.getText().isEmpty()) {

            precioTotal -= precioDescuento;
        }

        System.out.println("precio total pedido service: " + precioTotal);
        if (precioTotal <= 0) {
            System.out.println("El precio total es inválido: " + precioTotal);
            errores.append("No hay productos en el pedido.\n");
            showError(errores.toString());
            this.cancelarVentana();
            return;
        }

        List<DetallePedido> detallesPedidosList = pedidoService.getDetallesPedidosList();
        JSONArray detallesJson = new JSONArray();

        for (DetallePedido detalle : detallesPedidosList) {
            JSONObject detalleJson = new JSONObject();
            detalleJson.put("cantidad", detalle.getCantidad());
            if(detalle.getTipoHamburguesa() > 0) {
                int hamburguesaTipoId = detalle.getTipoHamburguesa();
                if (hamburguesaTipoId > 0) {
                    detalleJson.put("hamburguesa_tipo_id", hamburguesaTipoId);
                    detalleJson.put("precio_unitario", detalle.getPrecio_unitario());
                    detalleJson.put("observacion", detalle.getObservacion());

                    List<Topping> toppingsDelDetalle = pedidoService.getToppingsByDetalleId(detalle.getId());
                    JSONArray toppingsJson = new JSONArray();
                    for (Topping topping : toppingsDelDetalle) {
                        JSONObject toppingJson = new JSONObject();
                        toppingJson.put("id_topping", topping.getId());
                        toppingJson.put("Agregado", topping.getPrecio() == null ? 0 : 1);
                        toppingsJson.put(toppingJson);
                    }
                    detalleJson.put("toppings", toppingsJson);
                }
                detallesJson.put(detalleJson);
            } else {
                if(detalle.getExtra_id() > 0){
                    Extra extra = extraService.getExtra(detalle.getExtra_id());
                    if(extra != null) {
                        detalleJson.put("precio", extra.getPrecio());
                        detalleJson.put("id_extra", extra.getId_extra());
                    }
                }
                detallesJson.put(detalleJson);
            }
        }

        System.out.println("Detalles del Pedido (detallesJson): " + detallesJson);

        int idPedido = pedidoService.insertarPedido(
                nombreCliente,
                direccion,
                new Timestamp(System.currentTimeMillis()),
                idTipoPago,
                costoEnvio,
                precioTotal,
                precioDescuento,
                detallesJson
        );
        idPedidoImprimir = idPedido;
        if (idPedido>0) {
            showConfirmation("El pedido se ha insertado correctamente.");

            printTicket(idPedidoImprimir);

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

    private void printTicket(int idPedidoImprimir) {
        ticketPrinterService.imprimir(idPedidoImprimir);
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

    public void descuentoPedido(ActionEvent actionEvent) {
        if (chkDescuento.isSelected()){
            txtDescuento.setVisible(true);
        } else {
            txtDescuento.setVisible(false);
        }
    }
}

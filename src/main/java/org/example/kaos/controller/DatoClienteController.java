package org.example.kaos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.kaos.entity.DetallePedido;
import org.example.kaos.entity.TipoPago;
import org.example.kaos.entity.Topping;
import org.example.kaos.repository.TipoPagoDAO;
import org.example.kaos.service.PedidoService;
import org.example.kaos.manager.ControllerManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.sql.Timestamp;

public class DatoClienteController {

    private final TipoPagoDAO tipoPagoDAO = new TipoPagoDAO();
    private PedidoService pedidoService;
    private PedidoController pedidoController;
    private Stage stage;

    @FXML
    private TextField txtNombreCliente, txtDireccion, txtCostoEnvio;
    @FXML
    private ComboBox<String> cmbFormaPago;

    @FXML
    public void initialize() {
        cargarFormasPago();
    }

    public void setPedidoController(PedidoController pedidoController) {
        this.pedidoController = pedidoController;
        System.out.println("PedidoController establecido en DatoClienteController: " + pedidoController);
    }

    public void setPedidoService(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
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
        if (stage != null) {
            stage.close();
        } else {
            System.out.println("El objeto Stage es null");
        }
        String nombreCliente = txtNombreCliente.getText();
        String direccion = txtDireccion.getText();
        String formaPago = cmbFormaPago.getValue();
        double costoEnvio = Double.parseDouble(txtCostoEnvio.getText());
        int idTipoPago = tipoPagoDAO.getIdTipoPagoFromNombre(formaPago);
        System.out.println(nombreCliente + " " + direccion + " " + formaPago + " " + costoEnvio + " " + idTipoPago);
        System.out.println("listPrecio en aceptarDatos: " + pedidoService.getListPrecio());
        int precioTotal = pedidoService.getPrecioTotalPedido();
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
            List<Integer> hamburguesaTipos = detalle.getId_tipo_hamburgusa();
            if (!hamburguesaTipos.isEmpty()) {
                detalleJson.put("hamburguesa_tipo_id", hamburguesaTipos.get(0));
                detalleJson.put("precio_unitario", detalle.getPrecio_unitario());
                JSONArray toppingsJson = new JSONArray();
//                for (Topping topping : detalle.getToppings()) {
//                    JSONObject toppingJson = new JSONObject();
//                    toppingJson.put("id_topping", topping.getId());
//                    int esExtraible = topping.getEsExtra() ? 1 : 0;
//                    toppingJson.put("es_extraible", esExtraible);
//                    toppingsJson.put(toppingJson);
//                }
                detalleJson.put("toppings", toppingsJson);
            }
            detallesJson.put(detalleJson);
        }
        System.out.println("Detalles JSON: " + detallesJson.toString());
        pedidoService.insertarPedido(
                nombreCliente,
                direccion,
                new Timestamp(System.currentTimeMillis()),
                idTipoPago,
                costoEnvio,
                precioTotal,
                detallesJson
        );
        PedidoController pedidoController = ControllerManager.getInstance().getPedidoController();
        if (pedidoController != null) {
            pedidoService.getDetallesPedidosList().clear();
            pedidoService.getListPrecio().clear();
//            pedidoController.actualizarTotal();
            pedidoController.getDetallePedidos().getChildren().clear();
        } else {
            System.out.println("PedidoController es null.");
        }
        if (stage != null) {
            stage.close();
        } else {
            System.out.println("El objeto Stage es null");
        }
    }

    @FXML
    private void cancelarVentana() {
        stage.close();
    }
}

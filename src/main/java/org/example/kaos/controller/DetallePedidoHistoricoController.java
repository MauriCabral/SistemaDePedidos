package org.example.kaos.controller;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.example.kaos.application.PedidoApplication;
import org.example.kaos.entity.*;
import org.example.kaos.manager.ControllerManager;
import org.example.kaos.repository.*;
import org.example.kaos.service.ExtraService;
import org.example.kaos.service.FormaPagoService;
import org.example.kaos.service.PedidoService;
import org.example.kaos.service.ToppingService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DetallePedidoHistoricoController implements Initializable {

    @FXML
    private VBox vboxPedidosDetalle;
    @FXML
    private Button btnCancelar;
    @FXML
    private ComboBox comboFPago;
    @FXML
    private Pane panelEditarPedido;
    @FXML
    private TextField nombrePedido, direcPedido, envio, totalPedido, txtEf, txtMp;
    @FXML
    private Label lblTitulo, lblEf, lblMp;
    @FXML
    private ScrollPane detalleScroll;

    private final DetallePedidoDAO detallePedidoDAO = new DetallePedidoDAO();
    private final HamburguesaTipoDAO hamburguesaTipoDAO = new HamburguesaTipoDAO();
    private final HamburguesaDAO hamburguesaDAO = new HamburguesaDAO();
    private final ToppingPedidoDAO toppingPedidoDAO = new ToppingPedidoDAO();
    private final ToppingService toppingService = new ToppingService();
    private PedidoService pedidoService;
    private final ExtraService extraService = new ExtraService();
    private final FormaPagoService formaPagoservice = new FormaPagoService();

    int idPedido = 0;

    public DetallePedidoHistoricoController() {}

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        btnCancelar.setOnAction(event -> closeWindow());
        cargarFormaPagoAmbas(false);
    }

    private void cargarFormaPagoAmbas(boolean visible){
        lblEf.setVisible(visible);
        lblMp.setVisible(visible);
        txtEf.setVisible(visible);
        txtMp.setVisible(visible);
    }

    public void setPedidoService(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    public void cargarDetallePedido(int id, boolean editar) {
        if(!editar) {
            panelEditarPedido.setVisible(false);
        }
        Pedido pedido = pedidoService.getPedidoId(id);
        List<DetallePedido> detallesPedido = detallePedidoDAO.getDetallesByPedidoId(pedido.getId());
        vboxPedidosDetalle.getChildren().clear();
        for (DetallePedido detallePedido : detallesPedido) {
            VBox detalleBox = new VBox(10);
            detalleBox.setPadding(new Insets(5));
            detalleBox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10; -fx-border-radius: 5; -fx-border-color: #dcdcdc;");
            if(detallePedido.getTipoHamburguesa() > 0) {
                HamburguesaTipo hamburguesaTipo = hamburguesaTipoDAO.getHamburguesaTipoByID(detallePedido.getTipoHamburguesa());
                Hamburguesa hamburguesa = hamburguesaDAO.getMenuById(hamburguesaTipo.getHamburguesa_id());
                HBox hamburguesaBox = new HBox(20);
                hamburguesaBox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10; -fx-border-radius: 5; -fx-border-color: #dcdcdc;");
                Label labelTipo = new Label("Hamburguesa: " + hamburguesa.getNombre());
                labelTipo.setFont(new Font(16));
                labelTipo.setTextFill(Color.BLACK);
                Label labelCantidad = new Label("Cantidad: " + detallePedido.getCantidad());
                labelCantidad.setFont(new Font(16));
                labelCantidad.setTextFill(Color.BLACK);
                Label labelPrecio = new Label("Precio: $" + detallePedido.getPrecio_unitario());
                labelPrecio.setFont(new Font(16));
                labelPrecio.setTextFill(Color.BLACK);
                hamburguesaBox.getChildren().addAll(labelTipo, labelCantidad, labelPrecio);
                vboxPedidosDetalle.getChildren().add(hamburguesaBox);
            }
            if (detallePedido.getObservacion() != null && !detallePedido.getObservacion().isEmpty()) {
                Label labelObservacion = new Label("Observación: " + detallePedido.getObservacion());
                labelObservacion.setFont(new Font(14));
                labelObservacion.setTextFill(Color.DARKBLUE);
                detalleBox.getChildren().add(labelObservacion);
            }
            List<ToppingPedido> toppingPedidosList = toppingPedidoDAO.getToppingByDetallePedidoId(detallePedido.getId());
            VBox toppingsBox = new VBox(5);
            for (ToppingPedido toppingPedido : toppingPedidosList) {
                Topping topping = toppingService.getToppingById(toppingPedido.getIdTopping(), toppingPedido.isAgregado());
                Label toppingLabel;
                if (topping != null) {
                    if (toppingPedido.isAgregado()) {
                        toppingLabel = new Label("Topping: " + topping.getNombre() + " - Precio: $" + topping.getPrecio());
                        toppingLabel.setTextFill(Color.GREEN);
                    } else {
                        toppingLabel = new Label("Sin Topping: " + topping.getNombre());
                        toppingLabel.setTextFill(Color.RED);
                    }
                } else {
                    toppingLabel = new Label("Sin Topping");
                    toppingLabel.setTextFill(Color.GRAY);
                }
                toppingLabel.setFont(new Font(14));
                toppingsBox.getChildren().add(toppingLabel);
            }
            if (!toppingsBox.getChildren().isEmpty()) {
                detalleBox.getChildren().add(toppingsBox);
            }
            for (DetallePedido detallePedido1 : detallesPedido) {
                Label extra = null;
                if (detallePedido1.getExtra_id() > 0) {
                    Extra extras;
                    extras = extraService.getExtra(detallePedido1.getExtra_id());
                    extra = new Label("Extra: " + extras.getNombre() + " - Precio: $" + extras.getPrecio());
                    extra.setTextFill(Color.ORANGE);
                    extra.setFont(new Font(14));
                    toppingsBox.getChildren().add(extra);
                }
            }
            vboxPedidosDetalle.getChildren().add(detalleBox);
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    public void aceptarPedido(ActionEvent actionEvent) {
        TipoPago tipoPago = (TipoPago) comboFPago.getSelectionModel().getSelectedItem();
        int resTotal = 0;

        Pedido pedido = pedidoService.getPedidoId(idPedido);
        double totalPedidoDouble = Double.parseDouble(envio.getText().trim()) + (pedido.getPrecio_total());
        int totalPedidoValue = (int) Math.round(totalPedidoDouble);
        if (tipoPago != null) {
            if (tipoPago.getId() == 1) {
                txtEf.setText(String.valueOf(totalPedidoValue));
                txtMp.setText("0");
            } else if (tipoPago.getId() == 2) {
                txtMp.setText(String.valueOf(totalPedidoValue));
                txtEf.setText("0");
            }
        }
        try {
            resTotal = Integer.parseInt(txtEf.getText()) + Integer.parseInt(txtMp.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Por favor, ingrese valores numéricos válidos para efectivo y transferencia.");
            alert.showAndWait();
            return;
        }

        if (resTotal == totalPedidoValue) {
            int res = pedidoService.updateFPagoPedido(idPedido, tipoPago.getId(), Integer.parseInt(envio.getText()), Integer.parseInt(txtEf.getText()), Integer.parseInt(txtMp.getText()));

            if (res > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Forma de pago actualizada.");
                alert.showAndWait();

                this.closeWindow();

                PedidoController pedidoController = ControllerManager.getInstance().getPedidoController();
                if (pedidoController != null) {
                    javafx.application.Platform.runLater(() -> {
                        pedidoController.recargarHistorico();
                    });
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("El total de efectivo y transferencia no coincide con el total del pedido.");
            alert.showAndWait();
        }
    }

    public void cargarEditarPedido(Pedido pedido, boolean editar) {
        if(editar){
            idPedido = pedido.getId();
            botonesIniciar(true);
            comboFPago.setItems(formaPagoservice.getFPago());

            nombrePedido.setText(pedido.getCliente_nombre());
            direcPedido.setText(pedido.getDireccion());
            envio.setText(String.valueOf(pedido.getPrecio_envio()));
            totalPedido.setText(String.valueOf(pedido.getPrecio_total() + pedido.getPrecio_envio()));
            txtEf.setText(String.valueOf(pedido.getTotal_efectivo()));
            txtMp.setText(String.valueOf(pedido.getTotal_transferencia()));

            nombrePedido.setDisable(true);
            direcPedido.setDisable(true);
            envio.setDisable(false);
            totalPedido.setDisable(true);

            int idPagoPedido = pedido.getId_pago();
            for (TipoPago formaPago : formaPagoservice.getFPago()) {
                if (formaPago.getId() == idPagoPedido) {
                    comboFPago.getSelectionModel().select(formaPago);
                    if(formaPago.getId() == 3){
                        cargarFormaPagoAmbas(true);
                    } else {
                        cargarFormaPagoAmbas(false);
                    }
                    break;
                }
            }
            comboFPago.getSelectionModel().selectedIndexProperty().addListener((observable, oldIndex, newIndex) -> {
                if (newIndex.intValue() >= 0) {
                    TipoPago formaPagoSeleccionada = formaPagoservice.getFPago().get(newIndex.intValue());
                    cargarFormaPagoAmbas(formaPagoSeleccionada.getId() == 3);
                }
            });
        }
    }

    private void botonesIniciar(boolean visible) {
        panelEditarPedido.setVisible(visible);
        if (visible) {
            panelEditarPedido.toFront();
        } else {
            detalleScroll.toFront();
        }
        lblTitulo.setVisible(!visible);
        detalleScroll.setVisible(!visible);
        btnCancelar.setVisible(!visible);
    }
}

package org.example.kaos.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.example.kaos.entity.*;
import org.example.kaos.repository.*;
import org.example.kaos.service.PedidoService;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class DetallePedidoHistoricoController implements Initializable {

    @FXML
    private VBox vboxPedidosDetalle;
    @FXML
    private Button btnCancelar;

    private final DetallePedidoDAO detallePedidoDAO = new DetallePedidoDAO();
    private final HamburguesaTipoDAO hamburguesaTipoDAO = new HamburguesaTipoDAO();
    private final HamburguesaDAO hamburguesaDAO = new HamburguesaDAO();
    private final ToppingPedidoDAO toppingPedidoDAO = new ToppingPedidoDAO();
    private final ToppingDAO toppingDAO = new ToppingDAO();
    private PedidoService pedidoService;

    public DetallePedidoHistoricoController() {}

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        btnCancelar.setOnAction(event -> closeWindow());
    }

    public void setPedidoService(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    public void cargarDetallePedido(int id) {
        try {
            Pedido pedido = pedidoService.getPedidoId(id);

            List<DetallePedido> detallesPedido = detallePedidoDAO.getDetallesByPedidoId(pedido.getId());
            vboxPedidosDetalle.getChildren().clear();

            for (DetallePedido detallePedido : detallesPedido) {
                for (Integer detalleHamburguesa : detallePedido.getTiposHamburguesa()) {
                    HamburguesaTipo hamburguesaTipo = hamburguesaTipoDAO.getHamburguesaTipoByID(detalleHamburguesa);
                    //TipoHamburguesa tipoHamburguesa = tipoHamburguesaDAO.getTipoHamburguesaById(hamburguesaTipo.getTipo_id());
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

                List<ToppingPedido> toppingPedidosList = toppingPedidoDAO.getToppingByDetallePedidoId(detallePedido.getId());
                for (ToppingPedido toppingPedido : toppingPedidosList) {
                    Topping topping = toppingDAO.getToppingById(toppingPedido.getIdTopping(), toppingPedido.isAgregado());

                    if (topping != null) {
                        Label toppingLabel;
                        if (toppingPedido.isAgregado()) {
                            toppingLabel = new Label("Topping: " + topping.getNombre() + " - Precio: $" + topping.getPrecio());
                            toppingLabel.setTextFill(Color.GREEN);
                        } else {
                            toppingLabel = new Label("Sin Topping: " + topping.getNombre());
                            toppingLabel.setTextFill(Color.RED);
                        }
                        toppingLabel.setFont(new Font(14));
                        vboxPedidosDetalle.getChildren().add(toppingLabel);
                    } else {
                        Label toppingLabel = new Label("Sin Topping");
                        toppingLabel.setFont(new Font(14));
                        toppingLabel.setTextFill(Color.GRAY);
                        vboxPedidosDetalle.getChildren().add(toppingLabel);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}

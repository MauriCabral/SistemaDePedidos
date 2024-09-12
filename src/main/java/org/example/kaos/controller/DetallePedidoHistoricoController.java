package org.example.kaos.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import org.example.kaos.entity.*;
import org.example.kaos.repository.DetallePedidoDAO;
import org.example.kaos.repository.HamburguesaDAO;
import org.example.kaos.repository.HamburguesaTipoDAO;
import org.example.kaos.repository.TipoHamburguesaDAO;
import org.example.kaos.service.PedidoService;

public class DetallePedidoHistoricoController {

    @FXML
    private VBox vboxPedidosDetalle;
    @FXML
    private Label idLabel;
    @FXML
    private Label clienteNombreLabel;
    @FXML
    private Label direccionLabel;
    @FXML
    private Label fechaPedidoLabel;
    @FXML
    private Label precioEnvioLabel;
    @FXML
    private Label precioTotalLabel;

    private final DetallePedidoDAO detallePedidoDAO = new DetallePedidoDAO();
    private final HamburguesaTipoDAO hamburguesaTipoDAO = new HamburguesaTipoDAO();
    private final TipoHamburguesaDAO tipoHamburguesaDAO = new TipoHamburguesaDAO();
    private final HamburguesaDAO hamburguesaDAO = new HamburguesaDAO();
    private PedidoService pedidoService;

    public DetallePedidoHistoricoController() {}

    public void setPedidoService(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    public void cargarDetallePedido(int id) {
        Pedido pedido = pedidoService.getPedidoId(id);
        DetallePedido detallePedido = detallePedidoDAO.getDetallePedidoById(pedido.getId());
        vboxPedidosDetalle.getChildren().clear();
        HamburguesaTipo hamburguesaTipo = hamburguesaTipoDAO.getHamburguesaTipoByID(pedido.getId());
        TipoHamburguesa tipoHamburguesa = tipoHamburguesaDAO.getTipoHamburguesaById(hamburguesaTipo.getTipo_id());
        Hamburguesa hamburgusa = hamburguesaDAO.getMenuById(hamburguesaTipo.getHamburguesa_id());
        System.out.println("detalle: "+ detallePedido);
        System.out.println("hamburguesa tipo: " + hamburguesaTipo);
        System.out.println("tipo hamburguesa: " + tipoHamburguesa);
        System.out.println("hamburguesa: " + hamburgusa);
//            HBox hamburguesaBox = new HBox();
//            hamburguesaBox.setSpacing(10);
//
//            Label labelTipo = new Label("Hamburguesa: " + hamburguesa.getTipo());
//            labelTipo.setFont(new Font(16));
//
//            Label labelCantidad = new Label("Cantidad: " + hamburguesa.getCantidad());
//            labelCantidad.setFont(new Font(16));
//
//            Label labelPrecio = new Label("Precio: $" + hamburguesa.getPrecio());
//            labelPrecio.setFont(new Font(16));
//
//            hamburguesaBox.getChildren().addAll(labelTipo, labelCantidad, labelPrecio);
//
//            vboxPedidosDetalle.getChildren().add(hamburguesaBox);
//
//            if (hamburguesa.getToppings() != null && !hamburguesa.getToppings().isEmpty()) {
//                for (Topping topping : hamburguesa.getToppings()) {
//                    Label toppingLabel = new Label("Topping: " + topping.getNombre() + " - Precio: $" + topping.getPrecio());
//                    toppingLabel.setFont(new Font(14));
//                    vboxPedidosDetalle.getChildren().add(toppingLabel);
//                }
//            }
//        }
    }

    public void setPedidoId(int id) {
        Pedido pedido = pedidoService.getPedidoId(id);
        if (pedidoService != null) {
            if (pedido != null) {
                System.out.println(String.valueOf(pedido.getId()));
                System.out.println(pedido.getCliente_nombre());
                System.out.println(pedido.getDireccion());
                System.out.println(pedido.getFecha_pedido().toString());
                System.out.println(String.valueOf(pedido.getPrecio_envio()));
                System.out.println(String.valueOf(pedido.getPrecio_total()));

            } else {
                System.err.println("PedidoService no ha sido inicializado.");
            }
        }
    }
}

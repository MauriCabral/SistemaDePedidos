package org.example.kaos.service;

import org.example.kaos.controller.PedidoController;
import org.example.kaos.entity.*;
import org.example.kaos.repository.HamburguesaDAO;
import org.example.kaos.repository.HamburguesaTipoDAO;
import org.example.kaos.repository.PedidoDAO;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import org.json.JSONArray;

public class PedidoService {
    private final HamburguesaDAO hamburguesaDAO;
    private final HamburguesaTipoDAO hamburguesaTipoDAO;
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private PedidoController pedidoController;

    private final List<DetallePedido> detallesPedidosList;
    private Runnable onTotalCleared;

    public PedidoService() {
        this.hamburguesaDAO = new HamburguesaDAO();
        this.hamburguesaTipoDAO = new HamburguesaTipoDAO();
        this.detallesPedidosList = new ArrayList<>();
    }

    public List<HamburguesaTipo> getHamburguesaTipo(String nombreHamburguesa, String tipoHamburguesa) {
        List<HamburguesaTipo> listHambTip = new ArrayList<>();
        listHambTip.add(hamburguesaTipoDAO.getHamburguesaTipo(nombreHamburguesa, tipoHamburguesa));
        return listHambTip;
    }

    public Hamburgusa getMenuByCode(String code) {
        return hamburguesaDAO.getMenuByCode(code);
    }

    public DetallePedido addDetallePedido(String nombre, String tipo, int cantidad, double precio, List<Topping> toppingList) {
        DetallePedido detallePedido = new DetallePedido(detallesPedidosList.size() + 1, cantidad, getHamburguesaTipo(nombre, tipo), toppingList, precio);
        detallesPedidosList.add(detallePedido);
        return detallePedido;
    }

    public List<Integer> getIdToppingDetalle (List<Topping> toppingList) {
        List<Integer> toppingIds = new ArrayList<>();
        if (toppingList != null && !toppingList.isEmpty()) {
            for (Topping topping : toppingList) {
                toppingIds.add(topping.getId());
            }
        }
        return toppingIds;
    }

    public void removeDetallePedido(DetallePedido detallePedido) {
        int index = -1;for (int i = 0; i < detallesPedidosList.size(); i++) {
            if (detallesPedidosList.get(i).getId() == detallePedido.getId()) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            DetallePedido detalleEliminar = detallesPedidosList.remove(index);
            actualizarTotal();
        }
    }

    public void setPedidoController(PedidoController pedidoController) {
        this.pedidoController = pedidoController;
    }

    public int getPrecioTotalPedido() {
        return actualizarTotal();
    }

    public List<DetallePedido> getDetallesPedidosList() {
        return detallesPedidosList;
    }

    public void insertarPedido(String nombreCliente, String direccion, Timestamp fecha, int idTipoPago, double costoEnvio, double precioTotal, JSONArray detallesJson) {
        try {
            pedidoDAO.insertarPedido(nombreCliente, direccion, fecha, idTipoPago, costoEnvio, precioTotal, detallesJson);
            System.out.println("Pedido insertado exitosamente.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al insertar el pedido: " + e.getMessage());
        }
    }

    public double getPrecioTotalTopping(List<Topping> toppingList) {
        double totalTop = 0;
        if (toppingList != null && !toppingList.isEmpty()) {
            for (Topping topping : toppingList) {
                if(topping.getPrecio() == null) {totalTop += 0;}
                else {
                totalTop += topping.getPrecio();}
            }
        }
        return totalTop;
    }

    public int actualizarTotal() {
        int totalFinal = 0;
        for (DetallePedido detalle : detallesPedidosList) {
            totalFinal += detalle.getCantidad() * (int) detalle.getPrecio_unitario();
        }
        System.out.println("total de actualizar: " + totalFinal);
        return totalFinal;
    }

    public void setOnTotalCleared(Runnable onTotalCleared) {
        this.onTotalCleared = onTotalCleared;
    }

    public void limpiarDatos() {
        actualizarTotal();
    }

    public List<Pedido> getDalyPedidos() {
        return pedidoDAO.getAllDalyPedido();
    }
}
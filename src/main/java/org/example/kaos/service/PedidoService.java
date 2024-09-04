package org.example.kaos.service;

import org.example.kaos.controller.PedidoController;
import org.example.kaos.entity.DetallePedido;
import org.example.kaos.entity.HamburguesaTipo;
import org.example.kaos.entity.Hamburgusa;
import org.example.kaos.entity.Topping;
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
    private final List<Integer> listPrecio;
//    private final Map<DetallePedido, Integer> detallePrecioMap;


    public PedidoService() {
        this.hamburguesaDAO = new HamburguesaDAO();
        this.hamburguesaTipoDAO = new HamburguesaTipoDAO();
        this.detallesPedidosList = new ArrayList<>();
        this.listPrecio = new ArrayList<>();
//        this.detallePrecioMap = new HashMap<>();
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
//        listPrecio.add((int) precio);
        System.out.println("listPrecio después de añadir: " + listPrecio);
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
            final int precioDelete = (int)Math.round(detalleEliminar.getPrecio_unitario());
            listPrecio.remove(Integer.valueOf(precioDelete));
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

    public List<Integer> getListPrecio() {
        System.out.println("Contenido de listPrecio en getListPrecio: " + listPrecio);
        return listPrecio;
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
        int i = 0;
        for (DetallePedido precioList : detallesPedidosList) {
            totalFinal += precioList.getPrecio_unitario();
            i ++;
            System.out.println("numero de la lista : "  + i);
        }
        System.out.println("total de actualizar: " + totalFinal);
        return totalFinal;
    }
}
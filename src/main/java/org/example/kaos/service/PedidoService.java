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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

public class PedidoService {
    private final HamburguesaDAO hamburguesaDAO;
    private final HamburguesaTipoDAO hamburguesaTipoDAO;
    private PedidoDAO pedidoDAO = new PedidoDAO();
    private PedidoController pedidoController;

    private final List<DetallePedido> detallesPedidosList;
    private final List<Integer> listPrecio;
    private final Map<DetallePedido, Integer> detallePrecioMap;
    private static PedidoService instance;

    public PedidoService() {
        this.hamburguesaDAO = new HamburguesaDAO();
        this.hamburguesaTipoDAO = new HamburguesaTipoDAO();
        this.detallesPedidosList = new ArrayList<>();
        this.listPrecio = new ArrayList<>();
        this.detallePrecioMap = new HashMap<>();
    }

    public static PedidoService getInstance() {
        if (instance == null) {
            instance = new PedidoService();
        }
        return instance;
    }

    public void clearDetails() {
        detallesPedidosList.clear();
        listPrecio.clear();
    }

    public HamburguesaTipo getHamburguesaTipo(String nombreHamburguesa, String tipoHamburguesa) {
        return hamburguesaTipoDAO.getHamburguesaTipo(nombreHamburguesa, tipoHamburguesa);
    }

    public Hamburgusa getMenuByCode(String code) {
        return hamburguesaDAO.getMenuByCode(code);
    }

    public void addDetallePedido(String nombreHamburguesa, String tipoHamburguesa, int cantidad, double precio, List<Topping> toppingList) {
        HamburguesaTipo hamburguesaTipo = getHamburguesaTipo(nombreHamburguesa, tipoHamburguesa);
        if (hamburguesaTipo == null) {
            System.out.println("No se encontró el tipo de hamburguesa especificado.");
            return;
        }

        List<Integer> hamburguesaTipos = new ArrayList<>();
        hamburguesaTipos.add(hamburguesaTipo.getId());

        List<Integer> toppingIds = new ArrayList<>();
        if (toppingList != null && !toppingList.isEmpty()) {
            for (Topping topping : toppingList) {
                if (topping.getPrecio() != null) {
                    precio += topping.getPrecio();
                }
                toppingIds.add(topping.getId());
            }
        }

        DetallePedido detallePedido = new DetallePedido(detallesPedidosList.size() + 1, cantidad, hamburguesaTipos, toppingIds, precio, toppingList);
        detallesPedidosList.add(detallePedido);
        listPrecio.add((int) precio);
        System.out.println("listPrecio después de añadir: " + listPrecio);
    }

    public void removeDetallePedido(DetallePedido detallePedido) {
        detallesPedidosList.remove(detallePedido);
        listPrecio.removeIf(precio -> precio == detallePedido.getPrecio_unitario());
        System.out.println("Detalle eliminado. Contenido actual de listPrecio: " + listPrecio);
    }

    public int getPrecioTotalPedido() {
        int total = listPrecio.stream().mapToInt(Integer::intValue).sum();
        System.out.println("Total calculado en getPrecioTotalPedido: " + total);
        return total;
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
}

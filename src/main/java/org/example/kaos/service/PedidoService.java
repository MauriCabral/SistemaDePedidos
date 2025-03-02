package org.example.kaos.service;

import org.example.kaos.controller.PedidoController;
import org.example.kaos.entity.*;
import org.example.kaos.repository.HamburguesaDAO;
import org.example.kaos.repository.HamburguesaTipoDAO;
import org.example.kaos.repository.PedidoDAO;

import java.sql.Timestamp;
import java.util.*;

import org.json.JSONArray;

public class PedidoService {
    private final HamburguesaDAO hamburguesaDAO;
    private final HamburguesaTipoDAO hamburguesaTipoDAO;
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private PedidoController pedidoController;

        private final List<DetallePedido> detallesPedidosList;
        private final List<ToppingPedido> detalleToppingPedidoList;
        private Runnable onTotalCleared;

        public PedidoService(HamburguesaDAO hamburguesaDAO, HamburguesaTipoDAO hamburguesaTipoDAO) {
            this.hamburguesaDAO = hamburguesaDAO;
            this.hamburguesaTipoDAO = hamburguesaTipoDAO;
            this.detallesPedidosList = new ArrayList<>();
            this.detalleToppingPedidoList = new ArrayList<>();
        }

    public PedidoService() {
        this(new HamburguesaDAO(), new HamburguesaTipoDAO());
    }

    public Hamburguesa getMenuByCode(String code) {
        return hamburguesaDAO.getMenuByCode(code);
    }

    public DetallePedido addDetallePedido(String nombre, String tipo, int cantidad, double precio) {
        DetallePedido detallePedido = new DetallePedido(detallesPedidosList.size(), cantidad, getHamburguesaTipo(nombre, tipo), precio);
        detallesPedidosList.add(detallePedido);
        return detallePedido;
    }

    public ToppingPedido addDetalleToppingPedido(int idTopping, boolean agregago){
        ToppingPedido detalleToppingPedido = new ToppingPedido(0, detallesPedidosList.size() + 1, idTopping, agregago);
        detalleToppingPedidoList.add(detalleToppingPedido);
        return detalleToppingPedido;
    }

    public List<Integer> getHamburguesaTipo(String nombreHamburguesa, String tipoHamburguesa) {
        return hamburguesaTipoDAO.getHamburguesaTipoIds(nombreHamburguesa, tipoHamburguesa);
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
        int index = -1;
        for (int i = 0; i < detallesPedidosList.size(); i++) {
            if (detallesPedidosList.get(i).getId() == detallePedido.getId()) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            Iterator<ToppingPedido> iterator = detalleToppingPedidoList.iterator();
            while (iterator.hasNext()) {
                ToppingPedido toppingPedido = iterator.next();
                if (toppingPedido.getIdDetallePedido() == detallePedido.getId()) {
                    iterator.remove();
                }
            }

            detallesPedidosList.remove(index);
            actualizarTotal();
        }
    }


    public void setPedidoController(PedidoController pedidoController) {
        this.pedidoController = pedidoController;
    }

    public double getPrecioTotalPedido() {
        return actualizarTotal();
    }

    public List<DetallePedido> getDetallesPedidosList() {
        return detallesPedidosList;
    }

    public List<ToppingPedido> getDetallesToppingPedidosList() {
        return detalleToppingPedidoList;
    }

    public boolean insertarPedido(String nombreCliente, String direccion, Timestamp fecha, int idTipoPago, double costoEnvio, double precioTotal, JSONArray detallesJson) {
        boolean exito = false;
        exito = pedidoDAO.insertarPedido(nombreCliente, direccion, fecha, idTipoPago, costoEnvio, precioTotal, detallesJson);
        return exito;
    }


    public double getPrecioTotalTopping(List<Topping> toppingList) {
        double totalTop = 0.0;
        if (toppingList != null || !toppingList.isEmpty()) {
            for (Topping topping : toppingList) {
                if (topping.getPrecio() != null) {
                    totalTop += topping.getPrecio();
                }
            }
        }
        return totalTop;
    }

    public double actualizarTotal() {
        double totalFinal = 0;
        for (DetallePedido detalle : detallesPedidosList) {
            totalFinal += detalle.getPrecio_unitario();
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

    public Pedido getPedidoId(int id) {
        return pedidoDAO.getPedidoById(id);
    }
}
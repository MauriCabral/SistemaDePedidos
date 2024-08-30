package org.example.kaos.service;

import org.example.kaos.entity.DetallePedido;
import org.example.kaos.entity.HamburguesaTipo;
import org.example.kaos.entity.Hamburgusa;
import org.example.kaos.entity.Topping;
import org.example.kaos.repository.HamburguesaDAO;
import org.example.kaos.repository.HamburguesaTipoDAO;

import java.util.ArrayList;
import java.util.List;

public class PedidoService {
    private final HamburguesaDAO hamburguesaDAO;
    private final HamburguesaTipoDAO hamburguesaTipoDAO;

    private final List<DetallePedido> detallesPedidosList;
    private final List<Integer> listPrecio;

    public PedidoService() {
        this.hamburguesaDAO = new HamburguesaDAO();
        this.hamburguesaTipoDAO = new HamburguesaTipoDAO();
        this.detallesPedidosList = new ArrayList<>();
        this.listPrecio = new ArrayList<>();
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

        DetallePedido detallePedido = new DetallePedido(detallesPedidosList.size() + 1, cantidad, hamburguesaTipos, toppingIds, precio);
        detallesPedidosList.add(detallePedido);
        listPrecio.add((int) precio);
    }

    public void removeDetallePedido(DetallePedido detallePedido) {
        detallesPedidosList.remove(detallePedido);
        listPrecio.remove((int) detallePedido.getPrecio_unitario());
    }

    public int getPrecioTotalPedido() {
        int precioTotal = 0;
        for (Integer precio : listPrecio) {
            precioTotal += precio;
        }
        return precioTotal;
    }

    public List<DetallePedido> getDetallesPedidosList() {
        return detallesPedidosList;
    }
}

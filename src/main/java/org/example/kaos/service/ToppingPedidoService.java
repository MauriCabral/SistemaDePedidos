package org.example.kaos.service;

import org.example.kaos.entity.Topping;
import org.example.kaos.entity.ToppingPedido;
import org.example.kaos.repository.ToppingDAO;
import org.example.kaos.repository.ToppingPedidoDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ToppingPedidoService {
    public List<ToppingPedido> getToppingPedido(int idDetalle) {
        List<ToppingPedido> toppingList = new ArrayList<>();
        try {
            toppingList = ToppingPedidoDAO.getToppingByDetallePedidoId(idDetalle);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toppingList;
    }

    public Topping getTopping(int idTopping, boolean agregado) {
        Topping top = null;
        try {
            top = ToppingDAO.getToppingById(idTopping, agregado);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return top;
    }
}

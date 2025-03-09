package org.example.kaos.service;

import org.example.kaos.entity.ToppingPedido;
import org.example.kaos.repository.ToppingPedidoDAO;

import java.util.List;

public class ToppingPedidoService {
    public List<ToppingPedido> getToppingPedido(int idDetalle) {
        return ToppingPedidoDAO.getToppingByDetallePedidoId(idDetalle);
    }
}

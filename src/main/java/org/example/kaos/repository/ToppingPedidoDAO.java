package org.example.kaos.repository;

import org.example.kaos.entity.ToppingPedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ToppingPedidoDAO {

    public static List<ToppingPedido> getToppingByDetallePedidoId(int DetallePedidoId) throws SQLException {
        List<ToppingPedido> toppingPedidos = new ArrayList<>();
        String query = "SELECT * FROM topping_pedido WHERE detalle_pedido_id = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, DetallePedidoId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int idDetallePedido = rs.getInt("detalle_pedido_id");
                int idTopping = rs.getInt("topping_id");
                boolean agregado = rs.getInt("agregado") == 1;

                toppingPedidos.add(new ToppingPedido(id, idDetallePedido, idTopping, agregado));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return toppingPedidos;
    }
}

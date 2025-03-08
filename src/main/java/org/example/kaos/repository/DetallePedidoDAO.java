package org.example.kaos.repository;

import org.example.kaos.entity.DetallePedido;
import org.example.kaos.entity.HamburguesaTipo;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class DetallePedidoDAO {

    public List<DetallePedido> getDetallesByPedidoId(int idPedido) {
        List<DetallePedido> detalles = new ArrayList<>();
        String sql = "SELECT * FROM detalle_pedido WHERE pedido_id = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPedido);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    detalles.add(new DetallePedido(
                            rs.getInt("id"),
                            rs.getInt("pedido_id"),
                            rs.getInt("cantidad"),
                            rs.getInt("hamburguesa_tipo_id"),
                            rs.getDouble("precio_unitario"),
                            rs.getString("observacion"),
                            rs.getInt("extra_id")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener los detalles del pedido con ID: " + idPedido, e);
        }
        return detalles;
    }
}
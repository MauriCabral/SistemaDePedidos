package org.example.kaos.repository;

import org.example.kaos.entity.DetallePedido;
import org.example.kaos.entity.HamburguesaTipo;

import java.util.List;
import java.sql.*;

public class DetallePedidoDAO {
    public void insertDetallePedido(List<DetallePedido> detallesPedidosList, int idPedido) {
        String sql = "INSERT INTO detalle_pedido (idPedido, cantidad, hamburguesaTipoId, precioUnitario) VALUES (?, ?, ?, ?)";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (DetallePedido detalle : detallesPedidosList) {
                detalle.setId_pedido(idPedido);
                for (HamburguesaTipo hamburguesaTipoId : detalle.getId_tipo_hamburgusa()) {
                    stmt.setInt(1, idPedido);
                    stmt.setInt(2, detalle.getCantidad());
                    stmt.setInt(3, hamburguesaTipoId.getHamburguesa_id());
                    stmt.setDouble(4, detalle.getPrecio_unitario());
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
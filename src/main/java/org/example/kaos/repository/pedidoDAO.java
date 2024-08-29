package org.example.kaos.repository;

import java.sql.*;
import java.time.LocalDateTime;

public class pedidoDAO {
    public int createPedido() {
        String sql = "INSERT INTO pedido (precioTotal, fecha) VALUES (?, ?)";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setDouble(1, 0.0);
            stmt.setNull(2, java.sql.Types.DATE);
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void updatePedido(int idPedido, double precioTotal, LocalDateTime fecha, String nombreCliente, String direccion, int formaPago) {
        String sql = "UPDATE pedido SET precio_total = ?, fecha = ?, cliente_nombre = ?, direccion = ?, id_tipo_pago = ? WHERE id = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, precioTotal);
            stmt.setTimestamp(2, Timestamp.valueOf(fecha));
            stmt.setString(3, nombreCliente);
            stmt.setString(4,direccion);
            stmt.setInt(6, formaPago);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
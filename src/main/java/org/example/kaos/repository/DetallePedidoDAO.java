package org.example.kaos.repository;

import org.example.kaos.entity.DetallePedido;
import org.example.kaos.entity.Hamburguesa;
import org.example.kaos.entity.HamburguesaTipo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.sql.*;
import java.util.stream.Collectors;

public class DetallePedidoDAO {
    public void insertDetallePedido(List<DetallePedido> detallesPedidosList, int idPedido) {
        String sql = "INSERT INTO detalle_pedido (idPedido, cantidad, hamburguesaTipoId, precioUnitario) VALUES (?, ?, ?, ?)";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (DetallePedido detalle : detallesPedidosList) {
                detalle.setId_pedido(idPedido);
                for (Integer hamburguesaTipoId : detalle.getId_tipo_hamburgusa()) {
                    stmt.setInt(1, idPedido);
                    stmt.setInt(2, detalle.getCantidad());
                    stmt.setInt(3, hamburguesaTipoId);
                    stmt.setDouble(4, detalle.getPrecio_unitario());
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DetallePedido getDetallePedidoById(int idPedido) {
        String sql = "SELECT * FROM detalle_pedido WHERE id = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPedido);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    List<Integer> idTipoHamburguesa = new ArrayList<>();
                    String tipoHamburguesaStr = rs.getString("id_tipo_hamburguesa");
                    if (tipoHamburguesaStr != null && !tipoHamburguesaStr.isEmpty()) {
                        idTipoHamburguesa = Arrays.stream(tipoHamburguesaStr.split(","))
                                .map(Integer::parseInt)
                                .collect(Collectors.toList());
                    }
                    List<Integer> idTopping = new ArrayList<>();
                    String toppingStr = rs.getString("id_topping");
                    if (toppingStr != null && !toppingStr.isEmpty()) {
                        idTopping = Arrays.stream(toppingStr.split(","))
                                .map(Integer::parseInt)
                                .collect(Collectors.toList());
                    }

                    return new DetallePedido(
                            rs.getInt("id"),
                            rs.getInt("cantidad"),
                            idTipoHamburguesa,
                            idTopping,
                            rs.getDouble("precio_unitario")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
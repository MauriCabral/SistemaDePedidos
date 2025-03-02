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

    private HamburguesaTipoDAO hamburguesaTipoDAO = new HamburguesaTipoDAO();

    public void insertDetallePedido(List<DetallePedido> detallesPedidosList, int idPedido) {
        String sql = "INSERT INTO detalle_pedido (idPedido, cantidad, hamburguesaTipoId, precioUnitario) VALUES (?, ?, ?, ?)";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (DetallePedido detalle : detallesPedidosList) {
                detalle.setId_pedido(idPedido);
                for (Integer hamburguesaTipo : detalle.getTiposHamburguesa()) {
                    stmt.setInt(1, idPedido);
                    stmt.setInt(2, detalle.getCantidad());
                    stmt.setInt(3, hamburguesaTipo);
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
        String sql = "SELECT * FROM detalle_pedido WHERE id_pedido = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPedido);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    List<Integer> tiposHamburguesa = new ArrayList<>();
                    String tipoHamburguesaStr = rs.getString("id_tipo_hamburguesa");
                    if (tipoHamburguesaStr != null && !tipoHamburguesaStr.isEmpty()) {
                        String[] tipoIds = tipoHamburguesaStr.split(",");
                        for (String id : tipoIds) {
                            int tipoId = Integer.parseInt(id);
                            HamburguesaTipo tipoHamburguesa = hamburguesaTipoDAO.getHamburguesaTipoByID(tipoId);
                            if (tipoHamburguesa != null) {
                                tiposHamburguesa.add(tipoHamburguesa.getId());
                            }
                        }
                    }
                    return new DetallePedido(
                            rs.getInt("id"),
                            rs.getInt("cantidad"),
                            tiposHamburguesa,
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
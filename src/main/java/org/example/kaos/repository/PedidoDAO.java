package org.example.kaos.repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.example.kaos.entity.Pedido;
import org.json.JSONArray;

public class PedidoDAO {
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

    public boolean insertarPedido(String nombreCliente, String direccion, Timestamp fecha, int idTipoPago, double costoEnvio, double precioTotal, JSONArray detallesJson, JSONArray removeToppingsJson) {
        boolean exito = false;
        try (Connection conn = DataBase.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{call CrearPedidoConDetallesYtoppings(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, nombreCliente);
            stmt.setString(2, direccion);
            stmt.setTimestamp(3, fecha);
            stmt.setInt(4, idTipoPago);
            stmt.setDouble(5, costoEnvio);
            stmt.setDouble(6, precioTotal);
            stmt.setString(7, detallesJson.toString());
            stmt.setString(8, removeToppingsJson.toString());
            stmt.registerOutParameter(9, Types.INTEGER);

            stmt.execute();

            int filasAfectadas = stmt.getInt(9);
            System.out.println("Filas afectadas: " + filasAfectadas);

            exito = filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exito;
    }


    public List<Pedido> getAllDalyPedido() {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * \n" +
                "FROM pedido  \n" +
                "WHERE DATE(fecha) = CURRENT_DATE\n" +
                "Order by fecha DESC";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String clienteNombre = rs.getString("cliente_nombre");
                String direccion = rs.getString("direccion");
                LocalDateTime fechaPedido = rs.getObject("fecha", LocalDateTime.class);
                int idFormaPago = rs.getInt("id_tipo_pago");
                int costoEnvio = rs.getInt("precio_envio");
                double precioTotal = rs.getDouble("precio_total");

                pedidos.add(new Pedido(id, clienteNombre, direccion, fechaPedido, idFormaPago, costoEnvio, precioTotal));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return pedidos;
    }

    public Pedido getPedidoById(int id) {
        Pedido pedido = null;
        String sql = "SELECT * FROM pedido WHERE id = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int idPedido = rs.getInt("id");
                String clienteNombre = rs.getString("cliente_nombre");
                String direccion = rs.getString("direccion");
                LocalDateTime fechaPedido = rs.getObject("fecha", LocalDateTime.class);
                int idFormaPago = rs.getInt("id_tipo_pago");
                int costoEnvio = rs.getInt("precio_envio");
                double precioTotal = rs.getDouble("precio_total");

                pedido = new Pedido(idPedido, clienteNombre, direccion, fechaPedido, idFormaPago, costoEnvio, precioTotal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pedido;
    }
}
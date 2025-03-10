package org.example.kaos.repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.kaos.entity.Pedido;
import org.json.JSONArray;

public class PedidoDAO {
    public static List<Pedido> getPedidoByName(String nombre) {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedido WHERE LOWER(cliente_nombre) LIKE LOWER(?)";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String clienteNombre = rs.getString("cliente_nombre");
                String direccion = rs.getString("direccion");
                LocalDateTime fechaPedido = rs.getTimestamp("fecha").toLocalDateTime();
                int idPago = rs.getInt("id_tipo_pago");
                int precioEnvio = rs.getInt("precio_envio");
                double precioTotal = rs.getDouble("precio_total");

                Pedido pedido = new Pedido(id, clienteNombre, direccion, fechaPedido, idPago, precioEnvio, precioTotal);
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pedidos;
    }

    public static int setFPagpPedido(int id, int fPago) {
        int actualizado = -1;
        try (Connection conn = DataBase.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{call SetFPagoPedido(?, ?, ?)}");
            stmt.setInt(1, id);
            stmt.setInt(2, fPago);

            stmt.registerOutParameter(3, Types.INTEGER);
            stmt.execute();
            actualizado = stmt.getInt(3);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actualizado;
    }

    public int insertarPedido(String nombreCliente, String direccion, Timestamp fecha, int idTipoPago, double costoEnvio, double precioTotal, JSONArray detallesJson) {
        int pedidoId = -1;
        try (Connection conn = DataBase.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{call CrearPedidoConDetallesYtoppings(?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, nombreCliente);
            stmt.setString(2, direccion);
            stmt.setTimestamp(3, fecha);
            stmt.setInt(4, idTipoPago);
            stmt.setDouble(5, costoEnvio);
            stmt.setDouble(6, precioTotal);
            stmt.setString(7, detallesJson.toString());

            stmt.registerOutParameter(8, Types.INTEGER);
            stmt.execute();
            pedidoId = stmt.getInt(8);
            System.out.println("Pedido insertado con ID: " + pedidoId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pedidoId;
    }

    public List<Pedido> getAllPedidos() {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedido ";
        sql += "Order by fecha DESC";
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

    public List<Pedido> getAllPedidosDaily() {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedido ";
        sql += "WHERE DATE(fecha) = CURRENT_DATE ";
        sql += "Order by fecha DESC";
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

    public boolean eliminarPedido(int pedidoId) {
        boolean exito = false;
        try (Connection conn = DataBase.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{call EliminarPedido(?)}");
            stmt.setInt(1, pedidoId);
            stmt.execute();

            exito = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exito;
    }
}
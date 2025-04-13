package org.example.kaos.repository;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
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
                int idFormaPago = rs.getInt("id_tipo_pago");
                int total_efectivo = rs.getInt("total_efectivo");
                int total_transferencia = rs.getInt("total_transferencia");
                int precioEnvio = rs.getInt("precio_envio");
                double precioTotal = rs.getDouble("precio_total");

                Pedido pedido = new Pedido(id, clienteNombre, direccion, idFormaPago, fechaPedido, total_efectivo, total_transferencia, precioEnvio, precioTotal);
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pedidos;
    }

    public static int setFPagpPedido(int id, int fPago, int envio, int total_ef, int total_transf) {
        int actualizado = -1;
        try (Connection conn = DataBase.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{call SetFPagoPedido(?, ?, ?, ?, ?, ?)}");
            stmt.setInt(1, id);
            stmt.setInt(2, fPago);
            stmt.setInt(3, envio);
            stmt.setInt(4, total_ef);
            stmt.setInt(5, total_transf);

            stmt.registerOutParameter(6, Types.INTEGER);
            stmt.execute();
            actualizado = stmt.getInt(6);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actualizado;
    }

    public int insertarPedido(String nombreCliente, String direccion, Timestamp fecha, int idTipoPago, double costoEnvio, double precioTotal, int precioDescuento, JSONArray detallesJson) {
        int pedidoId = -1;
        try (Connection conn = DataBase.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{call CrearPedidoConDetallesYtoppings(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, nombreCliente);
            stmt.setString(2, direccion);
            stmt.setTimestamp(3, fecha);
            stmt.setInt(4, idTipoPago);
            stmt.setDouble(5, costoEnvio);
            stmt.setDouble(6, precioTotal);
            stmt.setInt(7, precioDescuento);
            stmt.setString(8, detallesJson.toString());

            stmt.registerOutParameter(9, Types.INTEGER);
            stmt.execute();
            pedidoId = stmt.getInt(9);
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
                int total_efectivo = rs.getInt("total_efectivo");
                int total_transferencia = rs.getInt("total_transferencia");
                int costoEnvio = rs.getInt("precio_envio");
                double precioTotal = rs.getDouble("precio_total");

                pedidos.add(new Pedido(id, clienteNombre, direccion, idFormaPago, fechaPedido, total_efectivo, total_transferencia, costoEnvio, precioTotal));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return pedidos;
    }

    public List<Pedido> getAllPedidosDaily() {
        List<Pedido> pedidos = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.getHour() < 3
                ? now.minusDays(1).withHour(3).withMinute(0).withSecond(0)
                : now.withHour(3).withMinute(0).withSecond(0);

        LocalDateTime endDate = startDate.plusHours(23).plusMinutes(59).plusSeconds(59);

        String sql = "SELECT * FROM pedido WHERE fecha >= ? AND fecha < ? ORDER BY fecha DESC";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, startDate);
            stmt.setObject(2, endDate);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String clienteNombre = rs.getString("cliente_nombre");
                String direccion = rs.getString("direccion");
                LocalDateTime fechaPedido = rs.getObject("fecha", LocalDateTime.class);
                int idFormaPago = rs.getInt("id_tipo_pago");
                int total_efectivo = rs.getInt("total_efectivo");
                int total_transferencia = rs.getInt("total_transferencia");
                int costoEnvio = rs.getInt("precio_envio");
                double precioTotal = rs.getDouble("precio_total");

                pedidos.add(new Pedido(id, clienteNombre, direccion, idFormaPago,fechaPedido, total_efectivo, total_transferencia, costoEnvio, precioTotal));
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
                int total_efectivo = rs.getInt("total_efectivo");
                int total_transferencia = rs.getInt("total_transferencia");
                int costoEnvio = rs.getInt("precio_envio");
                double precioTotal = rs.getDouble("precio_total");

                pedido = new Pedido(idPedido, clienteNombre, direccion, idFormaPago, fechaPedido, total_efectivo, total_transferencia, costoEnvio, precioTotal);
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

    public int getEfectivoDiario(boolean esDiario) {
        int efectivo = 0;
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDate finSemana = inicioSemana.plusDays(7);
        String sql = "SELECT SUM(total_efectivo) FROM pedido";
        if (esDiario) {
            sql += " WHERE pedido.fecha >= DATE_ADD(CURRENT_DATE, INTERVAL 3 HOUR)";
            sql += " AND pedido.fecha < DATE_ADD(CURRENT_DATE, INTERVAL 27 HOUR)";
        } else {
            sql += " WHERE pedido.fecha >= ? AND pedido.fecha < ?";
        }
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (!esDiario) {
                stmt.setTimestamp(1, Timestamp.valueOf(inicioSemana.atTime(3, 0)));
                stmt.setTimestamp(2, Timestamp.valueOf(finSemana.atTime(3, 0)));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int totalEfectivo = rs.getInt(1);
                    efectivo += totalEfectivo;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return efectivo;
    }

    public int getTransferenciaDiario(boolean esDiario) {
        int transferencia = 0;
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.with(DayOfWeek.TUESDAY);
        LocalDate finSemana = inicioSemana.plusDays(7);
        String sql = "SELECT SUM(total_transferencia) FROM pedido";
        if (esDiario) {
            sql += " WHERE pedido.fecha >= DATE_ADD(CURRENT_DATE, INTERVAL 3 HOUR)";
            sql += " AND pedido.fecha < DATE_ADD(CURRENT_DATE, INTERVAL 27 HOUR)";
        } else {
            sql += " WHERE pedido.fecha >= ? AND pedido.fecha < ?";
        }
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (!esDiario) {
                stmt.setTimestamp(1, Timestamp.valueOf(inicioSemana.atTime(3, 0)));
                stmt.setTimestamp(2, Timestamp.valueOf(finSemana.atTime(3, 0)));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int totalTransferencia = rs.getInt(1);
                    transferencia += totalTransferencia;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transferencia;
    }
}
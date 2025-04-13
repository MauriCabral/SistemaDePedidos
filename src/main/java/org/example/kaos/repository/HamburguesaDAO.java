package org.example.kaos.repository;

import org.example.kaos.entity.Hamburguesa;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class HamburguesaDAO {
    public Hamburguesa getMenuByCode(String code) {
        String sql = "SELECT * FROM hamburguesa WHERE codigo = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Hamburguesa(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("codigo")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Hamburguesa getMenuById(int idH) {
        String sql = "SELECT * FROM hamburguesa WHERE id = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idH);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Hamburguesa(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("codigo")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Integer> obtenerCantidadHamburguesasDiarias(boolean esDiario) {
        Map<String, Integer> cantidadHamburguesas = new HashMap<>();
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.with(DayOfWeek.TUESDAY);
        LocalDate finSemana = inicioSemana.plusDays(7);
        String sql = "SELECT CONCAT(hamburguesa.nombre, ' - ', tipo_Hamburguesa.tipo) AS hamburguesa, SUM(detalle_pedido.cantidad) AS cantidad";
        sql += " FROM detalle_pedido";
        sql += " LEFT OUTER JOIN hamburguesa_tipo ON detalle_pedido.hamburguesa_tipo_id = hamburguesa_tipo.id";
        sql += " LEFT OUTER JOIN hamburguesa ON hamburguesa_tipo.hamburguesa_id = hamburguesa.id";
        sql += " LEFT OUTER JOIN tipo_Hamburguesa ON hamburguesa_tipo.tipo_id = tipo_Hamburguesa.id";
        sql += " LEFT OUTER JOIN pedido ON detalle_pedido.pedido_id = pedido.id";
        sql += " WHERE 1=1";
        if (esDiario) {
            sql += " AND pedido.fecha >= DATE_ADD(CURRENT_DATE, INTERVAL 3 HOUR)";
            sql += " AND pedido.fecha < DATE_ADD(CURRENT_DATE, INTERVAL 27 HOUR)";
        } else {
            sql += " AND pedido.fecha >= ? AND pedido.fecha < ?";
        }
        sql += " GROUP BY hamburguesa.nombre, tipo_Hamburguesa.tipo";
        sql += " ORDER BY cantidad DESC";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (!esDiario) {
                stmt.setTimestamp(1, Timestamp.valueOf(inicioSemana.atTime(3, 0)));
                stmt.setTimestamp(2, Timestamp.valueOf(finSemana.atTime(3, 0)));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nombreHamburguesa = rs.getString("hamburguesa");
                    int cantidad = rs.getInt("cantidad");
                    cantidadHamburguesas.put(nombreHamburguesa, cantidad);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cantidadHamburguesas;
    }

    public Map<String, Integer> getHamburguesasMasVendidasDiario(boolean esDiario) {
        Map<String, Integer> masVendida = new HashMap<>();
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDate finSemana = inicioSemana.plusDays(7);
        String sql = "SELECT CONCAT(hamburguesa.nombre, ' - ', tipo_Hamburguesa.tipo) AS hamburguesa, SUM(detalle_pedido.cantidad) AS cantidad";
        sql += " FROM detalle_pedido";
        sql += " LEFT OUTER JOIN hamburguesa_tipo ON detalle_pedido.hamburguesa_tipo_id = hamburguesa_tipo.id";
        sql += " LEFT OUTER JOIN hamburguesa ON hamburguesa_tipo.hamburguesa_id = hamburguesa.id";
        sql += " LEFT OUTER JOIN tipo_Hamburguesa ON hamburguesa_tipo.tipo_id = tipo_Hamburguesa.id";
        sql += " LEFT OUTER JOIN pedido ON detalle_pedido.pedido_id = pedido.id";
        sql += " WHERE 1=1";
        if (esDiario) {
            sql += " AND pedido.fecha >= DATE_ADD(CURRENT_DATE, INTERVAL 3 HOUR)";
            sql += " AND pedido.fecha < DATE_ADD(CURRENT_DATE, INTERVAL 27 HOUR)";
        } else {
            sql += " AND pedido.fecha >= ? AND pedido.fecha < ?";
        }
        sql += " GROUP BY hamburguesa.nombre, tipo_Hamburguesa.tipo";
        sql += " ORDER BY cantidad DESC";
        sql += " LIMIT 3;";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (!esDiario) {
                stmt.setTimestamp(1, Timestamp.valueOf(inicioSemana.atTime(3, 0)));
                stmt.setTimestamp(2, Timestamp.valueOf(finSemana.atTime(3, 0)));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nombreHamburguesa = rs.getString("hamburguesa");
                    int cantidad = rs.getInt("cantidad");
                    masVendida.put(nombreHamburguesa, cantidad);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return masVendida;
    }
}

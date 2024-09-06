package org.example.kaos.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.example.kaos.entity.TipoPago;

public class TipoPagoDAO {
    public List<TipoPago> getAllTipoPago() {
        List<TipoPago> tipoPagos = new ArrayList<>();
        String sql = "SELECT * FROM tipo_pago";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nombre = rs.getString("nombre");
                    TipoPago tipoPago = new TipoPago(id, nombre);
                    tipoPagos.add(tipoPago);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tipoPagos;
    }

    public int getIdTipoPagoFromNombre(String nombreTipoPago) {
        int idTipoPago = -1;
        String sql = "SELECT id FROM tipo_pago WHERE nombre = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombreTipoPago);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    idTipoPago = rs.getInt("id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idTipoPago;
    }

    public String getNameTipoPagoFromId(int id) {
        String nombre = "";
        String sql = "SELECT nombre FROM tipo_pago WHERE id = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    nombre = rs.getString("nombre");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nombre;
    }
}

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
}

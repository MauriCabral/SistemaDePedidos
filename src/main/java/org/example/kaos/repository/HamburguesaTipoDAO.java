package org.example.kaos.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.example.kaos.entity.HamburguesaTipo;

public class HamburguesaTipoDAO {
    public List<Integer> getHamburguesaTipoIds(String nombre, String tipo) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT ht.id FROM hamburguesa_tipo ht INNER JOIN hamburguesa h ON h.id = ht.hamburguesa_id INNER JOIN tipo_hamburguesa th ON th.id = ht.tipo_id WHERE h.nombre = ? AND th.tipo = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, tipo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }


    public HamburguesaTipo getHamburguesaTipoByID(int id) {
        String sql = "SELECT * FROM hamburguesa_tipo WHERE id = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new HamburguesaTipo(
                            rs.getInt("id"),
                            rs.getInt("hamburguesa_id"),
                            rs.getDouble("precio"),
                            rs.getInt("tipo_id")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
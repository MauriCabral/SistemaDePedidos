package org.example.kaos.repository;

import org.example.kaos.entity.Hamburguesa;

import java.sql.*;

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
}

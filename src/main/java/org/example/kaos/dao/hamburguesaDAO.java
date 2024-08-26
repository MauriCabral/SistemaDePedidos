package org.example.kaos.dao;

import org.example.kaos.entity.hamburgusa;

import java.sql.*;

public class hamburguesaDAO {
    public hamburgusa getMenuByCode(String code) {
        String sql = "SELECT * FROM hamburguesa WHERE codigo = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new hamburgusa(
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

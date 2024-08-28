package org.example.kaos.repository;

import java.sql.*;

import org.example.kaos.entity.HamburguesaTipo;

public class hamburguesaTipoDAO {
    public HamburguesaTipo getHamburguesaTipo(String nombre, String tipo) {
        String sql = "SELECT * FROM hamburguesa_tipo ht INNER JOIN hamburguesa h ON h.id = ht.hamburguesa_id INNER JOIN tipo_hamburguesa th ON th.id = ht.tipo_id WHERE h.nombre = ? AND th.tipo = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, nombre);
            stmt.setString(2, tipo);
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
            e.printStackTrace();
        }
        return null;
    }
}
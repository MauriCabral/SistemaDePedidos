package org.example.kaos.dao;

import org.example.kaos.entity.Toppings;

import java.sql.*;

public class toppingsDAO {
    public static Toppings getToppingById(int toppingId) throws SQLException {
        String query = "SELECT nombre, precio FROM Toppings WHERE id = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, toppingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nombre = rs.getString("nombre");
                double precio = rs.getDouble("precio");
                return new Toppings(toppingId, nombre, precio);
            } else {
                return null;
            }
        }
    }
}

package org.example.kaos.repository;

import org.example.kaos.entity.Topping;

import java.sql.*;

public class ToppingDAO {
    public static Topping getToppingById(int toppingId) throws SQLException {
        String query = "SELECT * FROM Topping WHERE id = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, toppingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String nombre = rs.getString("nombre");
                double precio = rs.getDouble("precio");
                return new Topping(toppingId, nombre, precio);
            } else {
                return null;
            }
        }
    }

    public double getToppingPrecio(int toppingId) throws SQLException {
        double toppingPrecio = 0.0;
        String query = "SELECT precio FROM topping WHERE id = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, toppingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    toppingPrecio = rs.getDouble("precio");
                }
            }
        }
        return toppingPrecio;
    }
}

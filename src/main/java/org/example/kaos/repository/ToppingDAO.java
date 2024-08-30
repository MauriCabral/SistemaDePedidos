package org.example.kaos.repository;

import org.example.kaos.entity.Topping;

import java.sql.*;

public class ToppingDAO {
    public static Topping getToppingById(int toppingId, Boolean ban) throws SQLException {
        String query = " ";
        if (ban) {
            query = "SELECT nombre, precio FROM Topping WHERE id = ?";
        }
        else {
            query = "SELECT nombre FROM Topping WHERE id = ?";}
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, toppingId);
            ResultSet rs = stmt.executeQuery();
            if (ban) {
                if (rs.next()) {
                    String nombre = rs.getString("nombre");
                    double precio = rs.getDouble("precio");
                    return new Topping(toppingId, nombre, precio, true);
                } else {
                    return null;
                }
            }
            else {
                if (rs.next()) {
                    String nombre = rs.getString("nombre");
                    return new Topping(toppingId, nombre, false);
                } else {
                    return null;
                }
            }
        }
    }
}

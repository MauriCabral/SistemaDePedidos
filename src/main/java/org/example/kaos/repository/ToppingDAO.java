package org.example.kaos.repository;

import org.example.kaos.entity.Topping;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ToppingDAO {
    public static Topping getToppingById(int toppingId, boolean agregado) throws SQLException {
        String query = "SELECT * FROM Topping WHERE id = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, toppingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String nombre = rs.getString("nombre");
                Double precio = agregado ? rs.getDouble("precio") : null;
                return new Topping(toppingId, nombre, precio);
            } else {
                return null;
            }
        }
    }

    public List<Topping> getPreciosToppings() {
        List<Topping> toppingList = new ArrayList<>();
        String sql = "SELECT * FROM topping ORDER BY ID";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Topping tippong = new Topping(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio")
                );
                toppingList.add(tippong);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toppingList;
    }

    public int setPrecioTopping(List<Topping> toppingList) {
        String query = "UPDATE topping SET precio = ? WHERE id = ?";
        int res = 0;
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            for (Topping topp : toppingList) {
                stmt.setDouble(1, topp.getPrecio());
                stmt.setInt(2, topp.getId());
                stmt.executeUpdate();
                res += stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return res > 0 ? 1 : 0;
    }
}

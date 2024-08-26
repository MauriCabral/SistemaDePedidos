package org.example.kaos.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.kaos.entity.TipoHamburguesa;

import java.sql.*;

public class tipoHamburguesaDAO {
    public ObservableList<String> getAllTipoHamburguesa() {
        ObservableList<String> tipos = FXCollections.observableArrayList();
        String sql = "SELECT tipo FROM tipo_hamburguesa";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                tipos.add(rs.getString("tipo"));
            }
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
            e.printStackTrace();
        }
        return tipos;
    }

    public TipoHamburguesa getTipoHamburguesa(int code) {
        String sql = "SELECT * FROM tipo_hamburguesa WHERE id = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new TipoHamburguesa(
                            rs.getInt("id"),
                            rs.getString("tipo")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
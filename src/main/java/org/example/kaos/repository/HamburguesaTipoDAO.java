package org.example.kaos.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.example.kaos.entity.HamburguesaTipo;
import org.example.kaos.entity.TipoHamburguesa;

public class HamburguesaTipoDAO {
    public int getHamburguesaTipoIds(String nombre, String tipo) {
        int id = 0;
        String sql = "SELECT ht.id FROM hamburguesa_tipo ht INNER JOIN hamburguesa h ON h.id = ht.hamburguesa_id INNER JOIN tipo_hamburguesa th ON th.id = ht.tipo_id WHERE h.nombre = ? AND th.tipo = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, tipo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    id = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public List<HamburguesaTipo> getHamburguesaTipoIds(int hamburguesaId) {
        List<HamburguesaTipo> ids = new ArrayList<>();
        String sql = "SELECT * FROM hamburguesa_tipo WHERE hamburguesa_id = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hamburguesaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HamburguesaTipo hamburguesaTipo = new HamburguesaTipo(
                            rs.getInt("id"),
                            rs.getInt("hamburguesa_id"),
                            rs.getInt("tipo_id"),
                            rs.getDouble("precio")
                    );
                    ids.add(hamburguesaTipo);
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
                            rs.getInt("tipo_id"),
                            rs.getDouble("precio")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public boolean updateHamburguesa(String nombre, int hamburguesa_id, int tipo_id, double precio) {
        boolean exito = false;
        try (Connection conn = DataBase.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{call UpdateHamburguesa(?, ?, ?, ?, ?)}");
            stmt.setString(1, nombre);
            stmt.setInt(2, hamburguesa_id);
            stmt.setInt(3, tipo_id);
            stmt.setDouble(4, precio);
            stmt.registerOutParameter(5, Types.INTEGER);
            stmt.execute();

            exito = stmt.getInt(5) == 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exito;
    }
}
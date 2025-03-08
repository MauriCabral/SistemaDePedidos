package org.example.kaos.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.kaos.entity.Extra;

public class ExtraPromoDAO {
    public static Extra getExtraPapa() {
        String sql = "SELECT * FROM extra WHERE id_tipo = 1";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int id_extra = rs.getInt("id_extra");
                String nombre = rs.getString("nombre");
                int precio = rs.getInt("precio");
                int id_tipo = rs.getInt("id_tipo");
                return new Extra(id_extra, nombre, precio, id_tipo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getPrecioSalsa(int id) {
        int precio = 0;
        String sql = "SELECT precio FROM extra WHERE id_tipo = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    precio = rs.getInt("precio");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return precio;
    }

    public static boolean setPrecio(int idTipo, double precio) {
        boolean exito = false;
        try (Connection conn = DataBase.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{call UpdatePrecioExtra(?, ?, ?)}");
            stmt.setInt(1, idTipo);
            stmt.setDouble(2, precio);
            stmt.registerOutParameter(3, Types.INTEGER);
            stmt.execute();

            exito = stmt.getInt(3) == 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exito;
    }

    public static Double getPrecioPromo(int id) {
        double precio = 0.0;
        String sql = "SELECT precio FROM extra WHERE id_extra = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    precio = rs.getDouble("precio");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return precio;
    }

    public static void setPrecioPromo(int id, double precio) {
        try (Connection conn = DataBase.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{call UpdatePrecioPromo(?, ?)}");
            stmt.setInt(1, id);
            stmt.setDouble(2, precio);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addPromo(String nombrePromo, double precio, int idTipo) {
        try (Connection conn = DataBase.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{call AddPromo(?, ?, ?)}");
            stmt.setString(1, nombrePromo);
            stmt.setDouble(2, precio);
            stmt.setInt(3, idTipo);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Extra getExtraById(int idExtra) {
        String sql = "SELECT * FROM extra WHERE id_extra = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idExtra);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Extra(
                            rs.getInt("id_extra"),
                            rs.getString("nombre"),
                            rs.getInt("precio"),
                            rs.getInt("id_tipo")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ObservableList<Extra> getAllExtraCombo(int id_tipo) {
        ObservableList<Extra> extraCbo = FXCollections.observableArrayList();
        String sql = "SELECT id_extra, nombre FROM extra WHERE id_tipo = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id_tipo);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int idExtra = rs.getInt("id_extra");
                String nombre = rs.getString("nombre");
                extraCbo.add(new Extra(idExtra, nombre));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return extraCbo;
    }
}

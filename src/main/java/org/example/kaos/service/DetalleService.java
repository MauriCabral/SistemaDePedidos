package org.example.kaos.service;

import javafx.collections.ObservableList;
import org.example.kaos.entity.HamburguesaTipo;
import org.example.kaos.repository.HamburguesaTipoDAO;
import org.example.kaos.repository.TipoHamburguesaDAO;

public class DetalleService {

    private final TipoHamburguesaDAO typeDAO = new TipoHamburguesaDAO();
    private final HamburguesaTipoDAO hamburguesaTipoDAO = new HamburguesaTipoDAO();

    public ObservableList<String> getTiposHamburguesa(String nombreMenu) {
        return typeDAO.getAllTipoHamburguesa();
    }

    public double obtenerPrecio(String tipo, int cantidad, String nombreProducto) {
        HamburguesaTipo hamburguesaTipo = hamburguesaTipoDAO.getHamburguesaTipo(nombreProducto, tipo);
        if (hamburguesaTipo != null) {
            double precioBase = hamburguesaTipo.getPrecios();
            return precioBase * cantidad;
        }
        return -1;
    }
}

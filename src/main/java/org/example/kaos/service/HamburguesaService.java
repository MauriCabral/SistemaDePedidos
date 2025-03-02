package org.example.kaos.service;

import org.example.kaos.repository.HamburguesaTipoDAO;

public class HamburguesaService {

    private final HamburguesaTipoDAO hamburguesaTipoDAO = new HamburguesaTipoDAO();

    public boolean updateHamburguesa(String nombre, int hamburguesa_id, int tipo_id, double precio) {
        boolean exito = hamburguesaTipoDAO.updateHamburguesa(nombre, hamburguesa_id, tipo_id, precio);
        return exito;
    }
}

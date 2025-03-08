package org.example.kaos.service;

import org.example.kaos.entity.Hamburguesa;
import org.example.kaos.entity.HamburguesaTipo;
import org.example.kaos.entity.TipoHamburguesa;
import org.example.kaos.repository.HamburguesaDAO;
import org.example.kaos.repository.HamburguesaTipoDAO;
import org.example.kaos.repository.TipoHamburguesaDAO;

public class HamburguesaService {

    private final HamburguesaTipoDAO hamburguesaTipoDAO = new HamburguesaTipoDAO();
    private final HamburguesaDAO hamburguesaDAO = new HamburguesaDAO();
    private final TipoHamburguesaDAO tipoHamburguesaDAO = new TipoHamburguesaDAO();

    public boolean updateHamburguesa(String nombre, int hamburguesa_id, int tipo_id, double precio) {
        boolean exito = hamburguesaTipoDAO.updateHamburguesa(nombre, hamburguesa_id, tipo_id, precio);
        return exito;
    }

    public Hamburguesa getHamburguesaByID(int id) {
        return hamburguesaDAO.getMenuById(id);
    }

    public HamburguesaTipo getHamburguesaTipoByID(int tiposHamburguesa) {
        HamburguesaTipo hamburguesaTipo = hamburguesaTipoDAO.getHamburguesaTipoByID(tiposHamburguesa);
        return hamburguesaTipo;
    }

    public TipoHamburguesa getTipoHamburguesa(int tipoId) {
        return TipoHamburguesaDAO.getTipoHamburguesaById(tipoId);
    }
}

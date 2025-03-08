package org.example.kaos.service;

import org.example.kaos.entity.Extra;
import org.example.kaos.repository.ExtraPromoDAO;

public class ExtraService {

    public int getPrecioExtra(int id) {
        return ExtraPromoDAO.getPrecioSalsa(id);
    }

    public boolean setPrecio(int idTipo, double precio) {
        boolean res  = ExtraPromoDAO.setPrecio(idTipo, precio);
        return res;
    }

    public Double getPrecioPromo(int id) {
        return ExtraPromoDAO.getPrecioPromo(id);
    }

    public void setPrecioPromo(int id, double precio) {
        ExtraPromoDAO.setPrecioPromo(id, precio);
    }

    public void addPromo(String nombrePromo, double precio, int id_tipo) {
        ExtraPromoDAO.addPromo(nombrePromo, precio, id_tipo);
    }

    public Extra getExtra(int extraId) {
        return ExtraPromoDAO.getExtraById(extraId);
    }
}

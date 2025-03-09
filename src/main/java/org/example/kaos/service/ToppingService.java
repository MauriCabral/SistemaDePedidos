package org.example.kaos.service;

import org.example.kaos.entity.Topping;
import org.example.kaos.repository.ToppingDAO;

import java.sql.SQLException;

public class ToppingService {

    public Topping getToppingById(int idTopping, boolean agregado) {
        return ToppingDAO.getToppingById(idTopping, agregado);
    }
    public Topping getTopping(int idTopping, boolean agregado) {
        return ToppingDAO.getToppingById(idTopping, agregado);
    }
}

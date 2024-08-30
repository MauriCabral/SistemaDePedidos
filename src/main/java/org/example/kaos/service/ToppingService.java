package org.example.kaos.service;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.example.kaos.entity.Topping;
import org.example.kaos.repository.ToppingDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ToppingService {
    public List<Topping> getSelectedToppings(
            CheckBox cmbCheddar, CheckBox cmbBacon, CheckBox cmbLechuga,
            CheckBox cmbTomate, CheckBox cmbCebolla, CheckBox cmbCebollaCrisp,
            CheckBox cmbTomateConf, CheckBox cmbCheddar1, CheckBox cmbBacon1,
            CheckBox cmbLechuga1, CheckBox cmbTomate1, CheckBox cmbCebolla1,
            CheckBox cmbCebollaCrisp1, CheckBox cmbTomateConf1,
            CheckBox cmbCambiarSalsa, CheckBox quitarSalsa, TextField txtCambiarSalsa) {

        List<Topping> toppingList = new ArrayList<>();

        try {
            if (cmbCheddar.isSelected()) toppingList.add(ToppingDAO.getToppingById(1, true));
            if (cmbBacon.isSelected()) toppingList.add(ToppingDAO.getToppingById(2, true));
            if (cmbLechuga.isSelected()) toppingList.add(ToppingDAO.getToppingById(3, true));
            if (cmbTomate.isSelected()) toppingList.add(ToppingDAO.getToppingById(4, true));
            if (cmbCebolla.isSelected()) toppingList.add(ToppingDAO.getToppingById(5, true));
            if (cmbCebollaCrisp.isSelected()) toppingList.add(ToppingDAO.getToppingById(6, true));
            if (cmbTomateConf.isSelected()) toppingList.add(ToppingDAO.getToppingById(7, true));
            if (cmbCheddar1.isSelected()) toppingList.add(ToppingDAO.getToppingById(1, false));
            if (cmbBacon1.isSelected()) toppingList.add(ToppingDAO.getToppingById(2, false));
            if (cmbLechuga1.isSelected()) toppingList.add(ToppingDAO.getToppingById(3, false));
            if (cmbTomate1.isSelected()) toppingList.add(ToppingDAO.getToppingById(4, false));
            if (cmbCebolla1.isSelected()) toppingList.add(ToppingDAO.getToppingById(5, false));
            if (cmbCebollaCrisp1.isSelected()) toppingList.add(ToppingDAO.getToppingById(6, false));
            if (cmbTomateConf1.isSelected()) toppingList.add(ToppingDAO.getToppingById(7, false));
            if (cmbCambiarSalsa.isSelected() && !txtCambiarSalsa.getText().isEmpty()) {
                toppingList.add(new Topping(8, "Salsa " + txtCambiarSalsa.getText(), true));
            }
            if (quitarSalsa.isSelected()) {
                toppingList.add(new Topping(8, "Salsa", false));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        toppingList.sort((t1, t2) -> {
            if (t1 == null && t2 == null) return 0;
            if (t1 == null) return 1;
            if (t2 == null) return -1;
            return Boolean.compare(t2.esExtra(), t1.esExtra());
        });

        return toppingList;
    }
}

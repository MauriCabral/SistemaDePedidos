package org.example.kaos.service;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import org.example.kaos.entity.HamburguesaTipo;
import org.example.kaos.entity.Topping;
import org.example.kaos.repository.HamburguesaTipoDAO;
import org.example.kaos.repository.TipoHamburguesaDAO;
import org.example.kaos.repository.ToppingDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetalleService {

    private final TipoHamburguesaDAO typeDAO = new TipoHamburguesaDAO();
    private final HamburguesaTipoDAO hamburguesaTipoDAO = new HamburguesaTipoDAO();
    private final ToppingDAO toppingDAO = new ToppingDAO();
    private List<Topping> toppingListExtra;
    private List<Topping> toppingListRemove;

    @FXML
    private CheckBox cmbCheddar, cmbBacon, cmbLechuga, cmbTomate, cmbCebolla, cmbCebollaCrisp, cmbTomateConf;
    @FXML
    private CheckBox cmbCheddar1, cmbBacon1, cmbLechuga1, cmbTomate1, cmbCebolla1, cmbCebollaCrisp1, cmbTomateConf1, quitarSalsa;

    private final Map<CheckBox, Integer> extraToppingsMap = new HashMap<>();
    private final Map<CheckBox, Integer> removedToppingsMap = new HashMap<>();

    public DetalleService() {
        toppingListExtra = new ArrayList<>();
        toppingListRemove = new ArrayList<>();
    }
    public void setCheckBoxes(CheckBox cheddar, CheckBox bacon, CheckBox lechuga, CheckBox tomate, CheckBox cebolla, CheckBox cebollaCrisp, CheckBox tomateConf,
                              CheckBox cheddar1, CheckBox bacon1, CheckBox lechuga1, CheckBox tomate1, CheckBox cebolla1, CheckBox cebollaCrisp1, CheckBox tomateConf1, CheckBox salsa) {
        this.cmbCheddar = cheddar;
        this.cmbBacon = bacon;
        this.cmbLechuga = lechuga;
        this.cmbTomate = tomate;
        this.cmbCebolla = cebolla;
        this.cmbCebollaCrisp = cebollaCrisp;
        this.cmbTomateConf = tomateConf;
        this.cmbCheddar1 = cheddar1;
        this.cmbBacon1 = bacon1;
        this.cmbLechuga1 = lechuga1;
        this.cmbTomate1 = tomate1;
        this.cmbCebolla1 = cebolla1;
        this.cmbCebollaCrisp1 = cebollaCrisp1;
        this.cmbTomateConf1 = tomateConf1;
        this.quitarSalsa = salsa;

        configureToppingsMap();
    }

    private void configureToppingsMap() {
        extraToppingsMap.put(cmbCheddar, 1);
        extraToppingsMap.put(cmbBacon, 2);
        extraToppingsMap.put(cmbLechuga, 3);
        extraToppingsMap.put(cmbTomate, 4);
        extraToppingsMap.put(cmbCebolla, 5);
        extraToppingsMap.put(cmbCebollaCrisp, 6);
        extraToppingsMap.put(cmbTomateConf, 7);

        removedToppingsMap.put(cmbCheddar1, 1);
        removedToppingsMap.put(cmbBacon1, 2);
        removedToppingsMap.put(cmbLechuga1, 3);
        removedToppingsMap.put(cmbTomate1, 4);
        removedToppingsMap.put(cmbCebolla1, 5);
        removedToppingsMap.put(cmbCebollaCrisp1, 6);
        removedToppingsMap.put(cmbTomateConf1, 7);
        removedToppingsMap.put(quitarSalsa, 8);
    }

    public ObservableList<String> getTiposHamburguesa(String nombreMenu) {
        return typeDAO.getAllTipoHamburguesa();
    }

    public double obtenerPrecio(String tipo, int cantidad, String nombreProducto) {
        List<Integer> hamburguesaTipo = hamburguesaTipoDAO.getHamburguesaTipoIds(nombreProducto, tipo);
        for(Integer hamburTipo : hamburguesaTipo) {
            HamburguesaTipo hamburguesaTipo1 = hamburguesaTipoDAO.getHamburguesaTipoByID(hamburTipo);
            double precioBase = hamburguesaTipo1.getPrecios();
            return precioBase * cantidad;
        }
        return -1;
    }

    public void updateExtraToppings() {
        toppingListExtra.clear();
        toppingListExtra.addAll(getExtraToppings());
    }

    public void updateRemovedToppings() {
        toppingListRemove.clear();
        toppingListRemove.addAll(getRemovedToppings());
    }

    public List<Topping> getExtraToppings() {
        List<Topping> toppingListExtra = new ArrayList<>();
        addSelectedToppings(extraToppingsMap, toppingListExtra);
        System.out.println("Extra Toppings after adding: " + toppingListExtra);
        return toppingListExtra;
    }

    public List<Topping> getRemovedToppings() {
        List<Topping> toppingListRemove = new ArrayList<>();
        addSelectedToppings(removedToppingsMap, toppingListRemove);
        System.out.println("Toppings Removed: " + toppingListRemove);
        return toppingListRemove;
    }

    private void addSelectedToppings(Map<CheckBox, Integer> toppingsMap, List<Topping> toppingList) {
        for (Map.Entry<CheckBox, Integer> entry : toppingsMap.entrySet()) {
            CheckBox checkBox = entry.getKey();
            int toppingId = entry.getValue();
            if (checkBox.isSelected()) {
                try {
                    Topping topping = toppingDAO.getToppingById(toppingId);
                    if (topping != null && !toppingList.contains(topping)) {
                        toppingList.add(topping);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Topping> getToppingListExtra() {
        System.out.println("Topping List Extra: " + toppingListExtra);
        return new ArrayList<>(toppingListExtra);
    }

    public List<Topping> getToppingListRemove() {
        System.out.println("Topping List Remove: " + toppingListRemove);
        return new ArrayList<>(toppingListRemove);
    }

    public double getToppingPrecio(int toppingId) throws SQLException {
        return toppingDAO.getToppingPrecio(toppingId);
    }
}

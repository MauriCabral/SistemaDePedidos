package org.example.kaos.service;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import org.example.kaos.entity.DetallePedido;
import org.example.kaos.entity.Extra;
import org.example.kaos.entity.HamburguesaTipo;
import org.example.kaos.entity.Topping;
import org.example.kaos.repository.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetalleService {

    private final TipoHamburguesaDAO typeDAO = new TipoHamburguesaDAO();
    private final HamburguesaTipoDAO hamburguesaTipoDAO = new HamburguesaTipoDAO();
    private final ToppingDAO toppingDAO = new ToppingDAO();
    private final ExtraPromoDAO extraPromoDAO = new ExtraPromoDAO();
    private final DetallePedidoDAO detallePedidoDAO = new DetallePedidoDAO();
    private final ToppingService toppingService = new ToppingService();

    @FXML
    private CheckBox cmbCheddar, cmbBacon, cmbLechuga, cmbTomate, cmbCebolla, cmbCebollaCrisp, cmbTomateConf;
    @FXML
    private CheckBox cmbCheddar1, cmbBacon1, cmbLechuga1, cmbTomate1, cmbCebolla1, cmbCebollaCrisp1, cmbTomateConf1, quitarSalsa;

    public ObservableList<Extra> getComboExtra(int id_tipo) {
        return extraPromoDAO.getAllExtraCombo(id_tipo);
    }

    public ObservableList<String> getTiposHamburguesa() {
        return typeDAO.getAllTipoHamburguesa();
    }

    public double obtenerPrecio(String tipo, int cantidad, String nombreProducto) {
        int hamburguesaTipo = hamburguesaTipoDAO.getHamburguesaTipoIds(nombreProducto, tipo);
        HamburguesaTipo hamburguesaTipo1 = hamburguesaTipoDAO.getHamburguesaTipoByID(hamburguesaTipo);
        double precioBase = hamburguesaTipo1.getPrecios();
        return precioBase * cantidad;
    }

    public void setCheckBoxes(CheckBox... checkBoxes) {
        this.cmbCheddar = checkBoxes[0];
        this.cmbBacon = checkBoxes[1];
        this.cmbLechuga = checkBoxes[2];
        this.cmbTomate = checkBoxes[3];
        this.cmbCebolla = checkBoxes[4];
        this.cmbCebollaCrisp = checkBoxes[5];
        this.cmbTomateConf = checkBoxes[6];
        this.cmbCheddar1 = checkBoxes[7];
        this.cmbBacon1 = checkBoxes[8];
        this.cmbLechuga1 = checkBoxes[9];
        this.cmbTomate1 = checkBoxes[10];
        this.cmbCebolla1 = checkBoxes[11];
        this.cmbCebollaCrisp1 = checkBoxes[12];
        this.cmbTomateConf1 = checkBoxes[13];
        this.quitarSalsa = checkBoxes[14];
    }

    public List<Topping> getSelectedToppings() {
        List<Topping> toppingList = new ArrayList<>();
        addToppingIfSelected(cmbCheddar, 1, toppingList, true);
        addToppingIfSelected(cmbBacon, 2, toppingList, true);
        addToppingIfSelected(cmbLechuga, 3, toppingList, true);
        addToppingIfSelected(cmbTomate, 4, toppingList, true);
        addToppingIfSelected(cmbCebolla, 5, toppingList, true);
        addToppingIfSelected(cmbCebollaCrisp, 6, toppingList, true);
        addToppingIfSelected(cmbTomateConf, 7, toppingList, true);
        addToppingIfSelected(cmbCheddar1, 1, toppingList, false);
        addToppingIfSelected(cmbBacon1, 2, toppingList, false);
        addToppingIfSelected(cmbLechuga1, 3, toppingList, false);
        addToppingIfSelected(cmbTomate1, 4, toppingList, false);
        addToppingIfSelected(cmbCebolla1, 5, toppingList, false);
        addToppingIfSelected(cmbCebollaCrisp1, 6, toppingList, false);
        addToppingIfSelected(cmbTomateConf1, 7, toppingList, false);

        if (quitarSalsa.isSelected()) {
            toppingList.add(new Topping(8, "Salsa"));
        }
        return toppingList;
    }

    private void addToppingIfSelected(CheckBox checkBox, int id, List<Topping> toppingList, boolean agregado) {
        if (checkBox.isSelected()) {
            toppingList.add(toppingService.getToppingById(id, agregado));
        }
    }

    public Extra getExtraById(int idExtra) {
        return extraPromoDAO.getExtraById(idExtra);
    }

    public List<Topping> getPrecioTopping() {
        return toppingDAO.getPreciosToppings();
    }

    public int actualizarPreciosTopping(List<Topping> toppingList) {
       return toppingDAO.setPrecioTopping(toppingList);
    }

    public List<DetallePedido> getDetallePedidoList(int idPedidoRes) {
        return detallePedidoDAO.getDetallesByPedidoId(idPedidoRes);
    }
}
package org.example.kaos.entity;

import java.util.List;

public class DetallePedido {
    private int id;
    private int cantidad;
    private List<HamburguesaTipo> id_tipo_hamburgusa;
    private List<Topping> id_topping;
    private double tipo_unitario;

    public DetallePedido(int id, int cantidad, List<HamburguesaTipo> id_tipo_hamburgusa, List<Topping> id_topping, double tipo_unitario) {
        this.id = id;
        this.cantidad = cantidad;
        this.id_tipo_hamburgusa = id_tipo_hamburgusa;
        this.id_topping = id_topping;
        this.tipo_unitario = tipo_unitario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getTipo_unitario() {
        return tipo_unitario;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setTipo_unitario(double tipo_unitario) {
        this.tipo_unitario = tipo_unitario;
    }

    public List<HamburguesaTipo> getId_tipo_hamburgusa() {
        return id_tipo_hamburgusa;
    }

    public void setId_tipo_hamburgusa(List<HamburguesaTipo> id_tipo_hamburgusa) {
        this.id_tipo_hamburgusa = id_tipo_hamburgusa;
    }

    public List<Topping> getId_topping() {
        return id_topping;
    }

    public void setId_topping(List<Topping> id_topping) {
        this.id_topping = id_topping;
    }

    @Override
    public String toString() {
        return "DetallePedido{" +
                "id=" + id +
                ", cantidad=" + cantidad +
                ", id_tipo_hamburgusa=" + id_tipo_hamburgusa +
                ", id_topping=" + id_topping +
                ", tipo_unitario=" + tipo_unitario +
                '}';
    }
}

package org.example.kaos.entity;

import java.util.List;

public class DetalleTopping {
    private int id;
    private int id_detalle_pedido;
    private List<Integer> toppings_id;
    private double precio_final;

    public DetalleTopping(int id, int id_detalle_pedido, List<Integer> toppings_id, double precio_final) {
        this.id = id;
        this.id_detalle_pedido = id_detalle_pedido;
        this.toppings_id = toppings_id;
        this.precio_final = precio_final;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_detalle_pedido() {
        return id_detalle_pedido;
    }

    public void setId_detalle_pedido(int id_detalle_pedido) {
        this.id_detalle_pedido = id_detalle_pedido;
    }

    public List<Integer> getToppings_id() {
        return toppings_id;
    }

    public void setToppings_id(List<Integer> toppings_id) {
        this.toppings_id = toppings_id;
    }

    public double getPrecio_final() {
        return precio_final;
    }

    public void setPrecio_final(double precio_final) {
        this.precio_final = precio_final;
    }

    @Override
    public String toString() {
        return "DetalleTopping{" +
                "id=" + id +
                ", id_detalle_pedido=" + id_detalle_pedido +
                ", toppings_id=" + toppings_id +
                ", precio_final=" + precio_final +
                '}';
    }
}

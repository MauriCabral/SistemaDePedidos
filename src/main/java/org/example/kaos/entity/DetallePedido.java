package org.example.kaos.entity;

import java.util.List;

public class DetallePedido {
    private int id;
    private int id_pedido;
    private int cantidad;
    private List<Integer> id_tipo_hamburgusa;
    private List<Integer> id_topping;
    private double precio_unitario;

    public DetallePedido(int id, int cantidad, List<Integer> id_tipo_hamburgusa, List<Integer> id_topping, double precio_unitario) {
        this.id = id;
        this.cantidad = cantidad;
        this.id_tipo_hamburgusa = id_tipo_hamburgusa;
        this.id_topping = id_topping;
        this.precio_unitario = precio_unitario;
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

    public double getPrecio_unitario() {
        return precio_unitario;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setPrecio_unitario(double precio_unitario) {
        this.precio_unitario = precio_unitario;
    }

    public List<Integer> getId_tipo_hamburgusa() {
        return id_tipo_hamburgusa;
    }

    public void setId_tipo_hamburgusa(List<Integer> id_tipo_hamburgusa) {
        this.id_tipo_hamburgusa = id_tipo_hamburgusa;
    }

    public List<Integer> getId_topping() {
        return id_topping;
    }

    public void setId_topping(List<Integer> id_topping) {
        this.id_topping = id_topping;
    }

    public int getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(int id_pedido) {
        this.id_pedido = id_pedido;
    }

    @Override
    public String toString() {
        return "DetallePedido{" +
                "id=" + id +
                ", cantidad=" + cantidad +
                ", id_tipo_hamburgusa=" + id_tipo_hamburgusa +
                ", id_topping=" + id_topping +
                ", tipo_unitario=" + precio_unitario +
                '}';
    }
}

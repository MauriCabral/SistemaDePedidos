package org.example.kaos.entity;

import java.util.List;

public class DetallePedido {
    private int id;
    private int id_pedido;
    private int cantidad;
    private List<Integer> tiposHamburguesa;
    private double precio_unitario;

    public DetallePedido(int id, int cantidad, List<Integer> tiposHamburguesa, double precio_unitario) {
        this.id = id;
        this.cantidad = cantidad;
        this.tiposHamburguesa = tiposHamburguesa;
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

    public List<Integer> getTiposHamburguesa() {
        return tiposHamburguesa;
    }

    public void setTiposHamburguesa(List<Integer> tiposHamburguesa) {
        this.tiposHamburguesa = tiposHamburguesa;
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
                ", tiposHamburguesa=" + tiposHamburguesa +
                ", tipo_unitario=" + precio_unitario +
                '}';
    }
}
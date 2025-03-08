package org.example.kaos.entity;

import org.example.kaos.repository.ExtraPromoDAO;

import java.util.List;

public class DetallePedido {
    private int id;
    private int id_pedido;
    private int cantidad;
    private int tipoHamburguesa;
    private double precio_unitario;
    private String observacion;
    private int extra_id;

    public DetallePedido(int id, int cantidad, int tipoHamburguesa, double precio_unitario, String observacion) {
        this.id = id;
        this.cantidad = cantidad;
        this.tipoHamburguesa = tipoHamburguesa;
        this.precio_unitario = precio_unitario;
        this.observacion = observacion;
    }

    public DetallePedido(int id, int id_pedido, int cantidad, int tipoHamburguesa, double precio_unitario, String observacion, int extra_id) {
        this.id = id;
        this.id_pedido = id_pedido;
        this.cantidad = cantidad;
        this.tipoHamburguesa = tipoHamburguesa;
        this.precio_unitario = precio_unitario;
        this.observacion = observacion;
        this.extra_id = extra_id;
    }

    public DetallePedido(int id, int cantidad, double precio_unitario, int extra_id) {
        this.id = id;
        this.cantidad = cantidad;
        this.precio_unitario = precio_unitario;
        this.extra_id = extra_id;
    }

    public int getExtra_id() {
        return extra_id;
    }

    public void setExtra_id(int extra_id) {
        this.extra_id = extra_id;
    }

    public static Extra getExtraPapa() {
        return ExtraPromoDAO.getExtraPapa();
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

    public int getTipoHamburguesa() {
        return tipoHamburguesa;
    }

    public void setTipoHamburguesa(int tipoHamburguesa) {
        this.tipoHamburguesa = tipoHamburguesa;
    }

    public int getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(int id_pedido) {
        this.id_pedido = id_pedido;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    @Override
    public String toString() {
        return "DetallePedido{" +
                "id=" + id +
                ", cantidad=" + cantidad +
                ", tiposHamburguesa=" + tipoHamburguesa +
                ", tipo_unitario=" + precio_unitario +
                ", observacion=" + observacion +
                ", extra_id=" + extra_id +
                '}';
    }
}
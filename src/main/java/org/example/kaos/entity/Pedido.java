package org.example.kaos.entity;

import java.time.LocalDateTime;

public class Pedido {
    private int id;
    private String cliente_nombre;
    private String direccion;
    private LocalDateTime fecha_pedido;
    private int id_pago;
    private int total_efectivo;
    private int total_transferencia;
    private int precio_envio;
    private double precio_total;

    public Pedido(int id, String cliente_nombre, String direccion, int id_pago, LocalDateTime fecha_pedido, int total_efectivo, int total_transferencia, int precio_envio, double precio_total) {
        this.id = id;
        this.cliente_nombre = cliente_nombre;
        this.direccion = direccion;
        this.id_pago = id_pago;
        this.fecha_pedido = fecha_pedido;
        this.total_efectivo = total_efectivo;
        this.total_transferencia = total_transferencia;
        this.precio_envio = precio_envio;
        this.precio_total = precio_total;
    }

    public int getTotal_efectivo() {
        return total_efectivo;
    }

    public void setTotal_efectivo(int total_efectivo) {
        this.total_efectivo = total_efectivo;
    }

    public int getTotal_transferencia() {
        return total_transferencia;
    }

    public void setTotal_transferencia(int total_transferencia) {
        this.total_transferencia = total_transferencia;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCliente_nombre() {
        return cliente_nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public LocalDateTime getFecha_pedido() {
        return fecha_pedido;
    }

    public double getPrecio_total() {
        return precio_total;
    }

    public void setCliente_nombre(String cliente_nombre) {
        this.cliente_nombre = cliente_nombre;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setFecha_pedido(LocalDateTime fecha_pedido) {
        this.fecha_pedido = fecha_pedido;
    }

    public void setPrecio_total(double precio_total) {
        this.precio_total = precio_total;
    }

    public int getId_pago() {
        return id_pago;
    }

    public void setId_pago(int id_pago) {
        this.id_pago = id_pago;
    }

    public int getPrecio_envio() {
        return precio_envio;
    }

    public void setPrecio_envio(int precio_envio) {
        this.precio_envio = precio_envio;
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", cliente_nombre='" + cliente_nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", fecha_pedido=" + fecha_pedido +
                ", id_pago=" + id_pago +
                ", total_efectivo=" + total_efectivo +
                ", total_transferencia=" + total_transferencia +
                ", precio_envio=" + precio_envio +
                ", precio_total=" + precio_total +
                '}';
    }
}

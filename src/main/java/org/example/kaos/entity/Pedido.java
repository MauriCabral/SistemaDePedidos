package org.example.kaos.entity;

import java.time.LocalDateTime;

public class Pedido {
    private int id;
    private String clienteNombre;
    private String direccion;
    private LocalDateTime fecha;

    public Pedido(String clienteNombre, String direccion, int id, LocalDateTime fecha) {
        this.clienteNombre = clienteNombre;
        this.direccion = direccion;
        this.id = id;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", clienteNombre='" + clienteNombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", fecha=" + fecha +
                '}';
    }
}

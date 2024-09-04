package org.example.kaos.entity;

public class Topping {
    private int id;
    private String nombre;
    private Double precio;
    private Boolean esExtra;

    public Topping(int id, String nombre, Double precio, Boolean esExtra) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.esExtra = esExtra;
    }

    public Topping(int id, String nombre, Double precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }

    public Topping(int id, String nombre, Boolean esExtra) {
        this.id = id;
        this.nombre = nombre;
        this.precio = null;
        this.esExtra = esExtra;
    }

    public Boolean getEsExtra() {
        return esExtra;
    }

    public void setEsExtra(Boolean esExtra) {
        this.esExtra = esExtra;
    }

    public void setExtra(boolean added) {
        esExtra = added;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "Topping{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                '}';
    }
}

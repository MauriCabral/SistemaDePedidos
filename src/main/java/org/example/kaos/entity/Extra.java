package org.example.kaos.entity;

public class Extra {
    private int id_extra;
    private String nombre;
    private int precio;
    private int id_tipo;

    public Extra(int id_extra, String nombre, int precio, int id_tipo) {
        this.id_extra = id_extra;
        this.nombre = nombre;
        this.precio = precio;
        this.id_tipo = id_tipo;
    }

    public int getId_tipo() {
        return id_tipo;
    }

    public void setId_tipo(int id_tipo) {
        this.id_tipo = id_tipo;
    }

    public Extra(int id_extra, String nombre) {
        this.id_extra = id_extra;
        this.nombre = nombre;
    }

    public int getId_extra() {
        return id_extra;
    }

    public void setId_extra(int id_extra) {
        this.id_extra = id_extra;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return nombre;
    }
}

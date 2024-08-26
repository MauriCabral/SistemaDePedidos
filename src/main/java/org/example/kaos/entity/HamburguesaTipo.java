package org.example.kaos.entity;

public class HamburguesaTipo {
    private int id;
    private int hamburguesa_id;
    private int tipo_id;
    private Double precio;

    public HamburguesaTipo(int id, int hamburguesa_id, Double precio, int tipo_id) {
        this.id = id;
        this.hamburguesa_id = hamburguesa_id;
        this.precio = precio;
        this.tipo_id = tipo_id;
    }

    public int getId() {
        return id;
    }

    public int getTipo_id() {
        return tipo_id;
    }

    public int getHamburguesa_id() {
        return hamburguesa_id;
    }

    public Double getPrecios() {
        return precio;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHamburguesa_id(int hamburguesa_id) {
        this.hamburguesa_id = hamburguesa_id;
    }

    public void setTipo_id(int tipo_id) {
        this.tipo_id = tipo_id;
    }

    public void setPrecios(Double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "HamburguesaTipo{" +
                "id=" + id +
                ", hamburguesa_id=" + hamburguesa_id +
                ", tipo_id=" + tipo_id +
                ", precios=" + precio +
                '}';
    }
}

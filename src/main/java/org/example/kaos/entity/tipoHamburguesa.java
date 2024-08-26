package org.example.kaos.entity;

public class tipoHamburguesa {
    private int id;
    private String tipo;

    public tipoHamburguesa(int id, String tipo) {
        this.id = id;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "tipoHamburguesa{" +
                "id=" + id +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}

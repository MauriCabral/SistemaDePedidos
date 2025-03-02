package org.example.kaos.entity;

public class ToppingPedido {
    private int id;
    private int idDetallePedido;
    private int idTopping;
    private boolean agregado;

    public ToppingPedido(int id, int idDetallePedido, int idTopping, boolean agregado) {
        this.id = id;
        this.idDetallePedido = idDetallePedido;
        this.idTopping = idTopping;
        this.agregado = agregado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdDetallePedido() {
        return idDetallePedido;
    }

    public void setIdDetallePedido(int idDetallePedido) {
        this.idDetallePedido = idDetallePedido;
    }

    public int getIdTopping() {
        return idTopping;
    }

    public void setIdTopping(int idTopping) {
        this.idTopping = idTopping;
    }

    public boolean isAgregado() {
        return agregado;
    }

    public void setAgregado(boolean agregado) {
        this.agregado = agregado;
    }

    @Override
    public String toString() {
        return "ToppingPedido{" +
                "id=" + id +
                ", idDetallePedido=" + idDetallePedido +
                ", idTopping=" + idTopping +
                ", agregado=" + agregado +
                '}';
    }
}

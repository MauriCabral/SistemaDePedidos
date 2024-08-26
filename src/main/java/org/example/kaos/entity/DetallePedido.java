package org.example.kaos.entity;

public class DetallePedido {
    private int id;
    private int pedidoId;
    private int menuId;
    private int cantidad;

    public DetallePedido(int cantidad, int menuId, int pedidoId, int id) {
        this.cantidad = cantidad;
        this.menuId = menuId;
        this.pedidoId = pedidoId;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getPedidoId() {
        return pedidoId;
    }

    public int getMenuId() {
        return menuId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public void setPedidoId(int pedidoId) {
        this.pedidoId = pedidoId;
    }

    @Override
    public String toString() {
        return "DetallePedido{" +
                "id=" + id +
                ", pedidoId=" + pedidoId +
                ", menuId=" + menuId +
                ", cantidad=" + cantidad +
                '}';
    }
}

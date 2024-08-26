package org.example.kaos.manager;

import org.example.kaos.controller.PedidoController;

public class ControllerManager {
    private static ControllerManager instance;
    private PedidoController pedidoCtrl;

    private ControllerManager() {}

    public static ControllerManager getInstance() {
        if (instance == null) {
            instance = new ControllerManager();
        }
        return instance;
    }

    public void setPedidoController(PedidoController controller) {
        this.pedidoCtrl = controller;
    }

    public PedidoController getPedidoController() {
        return pedidoCtrl;
    }
}
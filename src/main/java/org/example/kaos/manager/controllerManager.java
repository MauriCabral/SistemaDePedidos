package org.example.kaos.manager;

import org.example.kaos.controller.pedidoController;

public class controllerManager {
    private static controllerManager instance;
    private pedidoController pedidoCtrl;

    private controllerManager() {}

    public static controllerManager getInstance() {
        if (instance == null) {
            instance = new controllerManager();
        }
        return instance;
    }

    public void setPedidoController(pedidoController controller) {
        this.pedidoCtrl = controller;
    }

    public pedidoController getPedidoController() {
        return pedidoCtrl;
    }
}
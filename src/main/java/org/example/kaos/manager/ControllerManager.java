package org.example.kaos.manager;

import org.example.kaos.controller.PedidoController;

public class ControllerManager {
    private static ControllerManager instance;
    private PedidoController pedidoCtrl;

    private ControllerManager() {}

    public static ControllerManager getInstance() {
        if (instance == null) {
            instance = new ControllerManager();
            System.out.println("Nueva instancia de ControllerManager creada.");
        }
        return instance;
    }

    public void setPedidoController(PedidoController controller) {
        this.pedidoCtrl = controller;
        System.out.println("PedidoController se ha establecido en ControllerManager.");
    }

    public PedidoController getPedidoController() {
        if (pedidoCtrl == null) {
            System.out.println("PedidoController es null en ControllerManager.");
        } else {
            System.out.println("PedidoController recuperado: " + pedidoCtrl);
        }
        return pedidoCtrl;
    }
}
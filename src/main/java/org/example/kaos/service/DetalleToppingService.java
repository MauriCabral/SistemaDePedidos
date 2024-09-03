package org.example.kaos.service;

import org.example.kaos.entity.Topping;

import java.util.List;

public class DetalleToppingService {
    public int getTotalPedidoToppings (List<Topping> toppingList) {
        int precio = 0;
        if (toppingList != null && !toppingList.isEmpty()) {
            for (Topping topping : toppingList) {
                if (topping.getPrecio() != null) {
                    precio += topping.getPrecio();
                }
            }
        }
        else {
            System.out.println("Lista de topping vacia...");
        }
        return precio;
    }

}
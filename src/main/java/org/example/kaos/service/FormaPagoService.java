package org.example.kaos.service;

import javafx.collections.ObservableList;
import org.example.kaos.entity.TipoPago;
import org.example.kaos.repository.TipoPagoDAO;

public class FormaPagoService {
    public ObservableList<TipoPago> getFPago() {
        return TipoPagoDAO.getAllFPago();
    }
}

package org.example.kaos.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.kaos.entity.Extra;
import org.example.kaos.manager.ControllerManager;
import org.example.kaos.service.DetalleService;
import org.example.kaos.service.ExtraService;
import org.example.kaos.service.PedidoService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ExtraPromoController implements Initializable {
    @FXML
    public Button cancelar, aceptar, agregar, editar, promoContMenos, promoContMas, papasMenos, papasMas, incrementS, decrementS;
    @FXML
    private Label counterLabelPromo, counterLabelPapa, lblPromo, counterLabelSalsa, labelAgregar;
    @FXML
    private ComboBox<Extra> comboBoxPromo, comboBoxSalsa, comboBoxPromo1;
    @FXML
    private Pane panelEdit, panelEditarPromo;
    @FXML
    private TextField precioSalsa, precioPromo, nombrePromo, precioPromoAgregar, precioPapa;

    private int count = 0, count1 = 0, count2;
    private PedidoService pedidoService;
    private DetalleService detalleService;
    private ExtraService extraService;

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        detalleService = new DetalleService();
        extraService = new ExtraService();
        if (counterLabelPapa != null) {
            counterLabelPapa.setText("0");
        }
        if (counterLabelPromo != null) {
            counterLabelPromo.setText("0");
        }
        cargarCombos();
    }

    private void aceptarExtra() {
        StringBuilder errores = new StringBuilder();

        Extra extraSalsaSelect = comboBoxSalsa.getSelectionModel().getSelectedItem();
        Extra extraPromoSelect = comboBoxPromo.getSelectionModel().getSelectedItem();

        int contPapas = Integer.parseInt(counterLabelPapa.getText());
        int contSalsa = Integer.parseInt(counterLabelSalsa.getText());
        int contPromo = Integer.parseInt(counterLabelPromo.getText());
        List<Extra> extraList = new ArrayList<>();
        if(contPapas > 0){
            extraList.add(detalleService.getExtraById(1));
        }
        if (extraSalsaSelect != null) {
            if (extraSalsaSelect.getId_extra() > 0 && contSalsa > 0) {
                extraList.add(detalleService.getExtraById(extraSalsaSelect.getId_extra()));
            } else {
                errores.append("Ingrese una cantidad mayor a 0 en salsa.\n");
            }
        } else if (contSalsa > 0) {
            errores.append("Seleccione una salsa.\n");
        }
        if (extraPromoSelect != null) {
            if (extraPromoSelect.getId_extra() > 0 && contPromo > 0) {
                extraList.add(detalleService.getExtraById(extraPromoSelect.getId_extra()));
            } else {
                errores.append("Ingrese una cantidad mayor a 0 en promo.\n");
            }
        } else if (contPromo > 0) {
            errores.append("Seleccione una promo.\n");
        }
        if (errores.length() > 0) {
            mostrarError(errores.toString());
            return;
        } else {
            actualizar(contPapas, contPromo, contSalsa, extraList);
            this.close();
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void actualizar(int contPapas, int contPromo, int contSalsa, List<Extra> extraList) {
        PedidoController pedidoCtrl = ControllerManager.getInstance().getPedidoController();
        if (pedidoCtrl == null) {
            return;
        }
        Platform.runLater(() -> {
            pedidoCtrl.actualizarDetalleExtras(contPapas, contPromo, contSalsa, extraList);
        });
    }

    public void aceptar(ActionEvent actionEvent) {
        if(!panelEdit.getStyle().contains("-fx-border-color: blue;")) {
            aceptarExtra();
        } else {
            if (precioPapa.isVisible() && !precioPapa.isDisable() && Integer.parseInt(precioPapa.getText()) > 0) {
                boolean res = extraService.setPrecio(1,Double.parseDouble(precioPapa.getText()));
                if(res){
                    mensaje("Precio de papas actualizado");
                } else {
                    mensaje("Error al actualizar precio de papas");
                }
            }
            if(precioSalsa.isVisible() && !precioSalsa.isDisable() && Integer.parseInt(precioSalsa.getText()) > 0){
                boolean res = extraService.setPrecio(2,Double.parseDouble(precioSalsa.getText()));
                if(res){
                    mensaje("Precio de salsa actualizado");
                } else {
                    mensaje("Error al actualizar precio de salsa");
                }
            }
            if(panelEditarPromo.getStyle().contains("-fx-border-color: orange;")) {
                if(comboBoxPromo1.getValue() != null && Double.parseDouble(precioPromo.getText()) > 0) {
                    extraService.setPrecioPromo(comboBoxPromo1.getSelectionModel().getSelectedItem().getId_extra(), Double.parseDouble(precioPromo.getText()));
                }
                if(!nombrePromo.getText().trim().isEmpty() && Integer.parseInt(precioPromoAgregar.getText().trim()) > 0) {
                    extraService.addPromo(nombrePromo.getText(), Double.parseDouble(precioPromoAgregar.getText()), 3);
                }
            }
        }
    }

    public void mensaje (String mensaje){
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText(null);
        info.setContentText(mensaje);
        info.showAndWait();
    }

    public void setPedidoService(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    public void cargarCombos() {
        panelEditarPromo.setVisible(false);
        precioSalsa.setVisible(false);
        precioPapa.setVisible(false);
        agregar.setVisible(false);

        comboBoxSalsa.setItems(detalleService.getComboExtra(2));
        comboBoxPromo.setItems(detalleService.getComboExtra(3));
        comboBoxPromo1.setItems(detalleService.getComboExtra(3));

        comboBoxSalsa.setConverter(new StringConverter<Extra>() {
            @Override
            public String toString(Extra extra) {
                return (extra != null) ? extra.getNombre() : "";
            }
            @Override
            public Extra fromString(String s) {
                return null;
            }
        });
        comboBoxSalsa.setOnAction(event -> {
            comboBoxSalsa.getSelectionModel().getSelectedItem();
        });
        comboBoxPromo.setConverter(new StringConverter<Extra>() {
            @Override
            public String toString(Extra extra) {
                return (extra != null) ? extra.getNombre() : "";
            }
            @Override
            public Extra fromString(String s) {
                return null;
            }
        });
        comboBoxPromo.setOnAction(event -> {
            if (comboBoxPromo.getValue() == null) {
                precioPromo.setDisable(true);
                precioPromo.setText(String.valueOf(extraService.getPrecioPromo(comboBoxPromo.getSelectionModel().getSelectedItem().getId_extra())));
            } else {
                precioPromo.setDisable(false);
            }
        });

        comboBoxPromo1.setConverter(new StringConverter<Extra>() {
            @Override
            public String toString(Extra extra) {
                return (extra != null) ? extra.getNombre() : "";
            }
            @Override
            public Extra fromString(String s) {
                return null;
            }
        });
        comboBoxPromo1.setOnAction(event -> {
            if (comboBoxPromo1.getValue() == null) {
                precioPromo.setDisable(true);
                precioPromo.setText(String.valueOf(extraService.getPrecioPromo(comboBoxPromo1.getSelectionModel().getSelectedItem().getId_extra())));
            } else {
                precioPromo.setDisable(false);
            }
        });
    }

    public void close(){
        Stage stage = (Stage) cancelar.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    public void cerrar(ActionEvent actionEvent) {
        this.close();
    }

    @FXML
    public void incrementCounterPromo(ActionEvent actionEvent) {
        count1++;
        updateCounterPromo();
    }
    @FXML
    public void decrementCounterPromo(ActionEvent actionEvent) {
        if (count1 > 0) {
            count1--;
        }
        updateCounterPromo();
    }

    private void updateCounterPromo() {
        counterLabelPromo.setText(String.valueOf(count1));
    }

    @FXML
    public void incrementCounterS(ActionEvent actionEvent) {
        count2++;
        updateCounterLabelSalsa();
    }

    @FXML
    public void decrementCounterS(ActionEvent actionEvent) {
        if (count2 > 0) {
            count2--;
        }
        updateCounterLabelSalsa();
    }

    private void updateCounterLabelSalsa() {
        counterLabelSalsa.setText(String.valueOf(count2));
    }

    @FXML
    public void incrementCounter(ActionEvent actionEvent) {
        count++;
        updateCounterLabel();
    }
    @FXML
    public void decrementCounter(ActionEvent actionEvent) {
        if (count > 0) {
            count--;
        }
        updateCounterLabel();
    }

    private void updateCounterLabel() {
        counterLabelPapa.setText(String.valueOf(count));
    }

    public void editarExtra(ActionEvent actionEvent) {
        if (panelEdit.getStyle().contains("-fx-border-color: blue;")) {
            panelEdit.setStyle("");
            panelEditarPromo.setVisible(false);
            botonesEditar(true);
            comboBoxPromo1.setVisible(false);
            cargarCombos();
        } else {
            panelEdit.setStyle("-fx-border-color: blue; -fx-border-width: 2px;");
            precioPapa.setText(String.valueOf(extraService.getPrecioExtra(1)));
            precioSalsa.setText(String.valueOf(extraService.getPrecioExtra(2)));
            botonesEditar(false);
        }
    }

    private void botonesEditar(boolean visible) {
        agregar.setVisible(!visible);
        precioPapa.setVisible(!visible);
        precioSalsa.setVisible(!visible);

        papasMas.setVisible(visible);
        papasMenos.setVisible(visible);
        counterLabelPapa.setVisible(visible);

        incrementS.setVisible(visible);
        decrementS.setVisible(visible);
        counterLabelSalsa.setVisible(visible);

        comboBoxSalsa.setDisable(!visible);
        comboBoxPromo.setDisable(!visible);

        promoContMenos.setDisable(!visible);
        promoContMas.setDisable(!visible);
        counterLabelPromo.setDisable(!visible);
    }

    public void agregarExtra(ActionEvent actionEvent) {
        if (panelEditarPromo.getStyle().contains("-fx-border-color: orange;")) {
            panelEditarPromo.setStyle("");
            panelEditarPromo.setVisible(false);
            botonesAgregarExtra(true);

        } else {
            panelEditarPromo.setStyle("-fx-border-color: orange; -fx-border-width: 2px;");
            panelEditarPromo.setVisible(true);
            panelEditarPromo.toFront();
            botonesAgregarExtra(false);

            comboBoxPromo1.valueProperty().addListener((observable, oldValue, newValue) -> {
                precioPromo.setText(String.valueOf(extraService.getPrecioPromo(comboBoxPromo1.getSelectionModel().getSelectedItem().getId_extra())));
            });
        }
    }

    private void botonesAgregarExtra(boolean visible) {
        precioPapa.setDisable(!visible);
        precioSalsa.setDisable(!visible);
        editar.setDisable(!visible);

        comboBoxPromo.setDisable(!visible);
        promoContMas.setVisible(visible);
        promoContMenos.setVisible(visible);
        counterLabelPromo.setVisible(visible);

        precioPromo.setDisable(visible);
        nombrePromo.setDisable(visible);
        precioPromoAgregar.setDisable(visible);

        precioPromo.setEditable(true);
        nombrePromo.setEditable(true);
        precioPromoAgregar.setEditable(true);
    }
}
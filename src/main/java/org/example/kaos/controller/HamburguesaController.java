package org.example.kaos.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.kaos.entity.HamburguesaTipo;
import org.example.kaos.service.HamburguesaService;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HamburguesaController {
    @FXML
    private ImageView imageView;
    @FXML
    private TextField nombreHamburguesa, precioSimple, precioDoble, precioTriple;
    @FXML
    private Button cargarImagen;

    private HamburguesaService hamburguesaService;
    private List<HamburguesaTipo> hamburguesasList;

    @FXML
    public void initialize() {
        hamburguesasList = new ArrayList<>();
        hamburguesaService = new HamburguesaService();

        setNumericField(precioSimple);
        setNumericField(precioDoble);
        setNumericField(precioTriple);
    }

    private void setNumericField(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (textField.getText().isEmpty()) {
                textField.setText("");
            }
        });
    }

    private String formatPrecio(Double precio) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(precio);
    }

    public void setHamburguesaEditar(String menuNombre, List<HamburguesaTipo> idHamburguesaList, boolean esEditar) {
        if (menuNombre == null || menuNombre.isEmpty()) {
            nombreHamburguesa.setText("");
        } else {
            nombreHamburguesa.setText(menuNombre);
        }
        if (esEditar) {
            cargarImagen.setDisable(true);
        }
        if (idHamburguesaList == null || idHamburguesaList.isEmpty()) {
            precioSimple.setText("");
            precioDoble.setText("");
            precioTriple.setText("");
        } else {
            if (hamburguesasList == null) {
                hamburguesasList = new ArrayList<>();
            }

            hamburguesasList.clear();

            for (HamburguesaTipo tipo : idHamburguesaList) {
                hamburguesasList.add(tipo);
                if (tipo.getTipo_id() == 1) {
                    precioSimple.setText(formatPrecio(tipo.getPrecios()));
                } else if (tipo.getTipo_id() == 2) {
                    precioDoble.setText(formatPrecio(tipo.getPrecios()));
                } else if (tipo.getTipo_id() == 3) {
                    precioTriple.setText(formatPrecio(tipo.getPrecios()));
                }
            }
        }
    }

    @FXML
    private void loadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagenes", "*.jpg", "*.png"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
        }
    }

    @FXML
    private void handleAccept() {
        double precio = 0.0;
        boolean precioValido = true;
        for (HamburguesaTipo tipo : hamburguesasList) {
            if (tipo.getTipo_id() == 1 && !precioSimple.getText().isEmpty()) {
                precio = Double.parseDouble(precioSimple.getText());
                if (precio <= 0) {
                    precioValido = false;
                    showErrorMessage("El precio del producto debe ser mayor a cero.");
                    break;
                }
            } else if (tipo.getTipo_id() == 2 && !precioDoble.getText().isEmpty()) {
                precio = Double.parseDouble(precioDoble.getText());
                if (precio <= 0) {
                    precioValido = false;
                    showErrorMessage("El precio del producto debe ser mayor a cero.");
                    break;
                }
            } else if (tipo.getTipo_id() == 3 && !precioTriple.getText().isEmpty()) {
                precio = Double.parseDouble(precioTriple.getText());
                if (precio <= 0) {
                    precioValido = false;
                    showErrorMessage("El precio del producto debe ser mayor a cero.");
                    break;
                }
            }
            if (precioValido) {
                hamburguesaService.updateHamburguesa(nombreHamburguesa.getText(), tipo.getHamburguesa_id(), tipo.getTipo_id(), precio);
            }
        }
        if (precioValido) {
            showSuccessMessage("Los cambios se han actualizado correctamente.");
            close();
        }
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Precio");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Actualización Exitosa");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void close() {
        Stage stage = (Stage) nombreHamburguesa.getScene().getWindow();
        stage.close();
    }

    public void handleCancelar(ActionEvent actionEvent) {
        this.close();
    }
}
package org.example.kaos.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.kaos.application.PedidoApplication;
import org.example.kaos.entity.*;
import org.example.kaos.repository.HamburguesaDAO;
import org.example.kaos.repository.HamburguesaTipoDAO;
import org.example.kaos.repository.TipoPagoDAO;
import org.example.kaos.service.DetalleService;
import org.example.kaos.service.HamburguesaService;
import org.example.kaos.service.PedidoService;

public class PedidoController {

    private PedidoApplication pedidoApp;
    private final HamburguesaDAO hamburguesaDAO = new HamburguesaDAO();
    private final HamburguesaTipoDAO hamburguesaTipoDAO = new HamburguesaTipoDAO();
    private final TipoPagoDAO tipoPagoDAO = new TipoPagoDAO();
    private boolean deleteButtonsVisible = false;
    private List<DetallePedido> detallesPedidosList = new ArrayList<>();
    private List<Extra> extraList = new ArrayList<>();
    private PedidoService pedidoService = new PedidoService();
    private DetalleService detalleService = new DetalleService();
    private HamburguesaService hamburguesaService = new HamburguesaService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static int detalleId = 0;;
    private List<Topping> listTopping = new ArrayList<>();

    @FXML
    private Pane menuPane, rightPane, pnHistorico, pnDashboard;
    @FXML
    private VBox detallePedidos;
    @FXML
    private Label lblTotal, TotalMp, TotalEf, TotalPedidosCount;
    @FXML
    private TableView<Pedido> tableHistorico;
    @FXML
    private TableColumn<Pedido, Integer> colId;
    @FXML
    private TableColumn<Pedido, String> colNombre;
    @FXML
    private TableColumn<Pedido, String> colDireccion;
    @FXML
    private TableColumn<Pedido, Integer> colCostoEnvio;
    @FXML
    private TableColumn<Pedido, String> colFormaPago;
    @FXML
    private TableColumn<Pedido, Integer> colTotalTransferencia;
    @FXML
    private TableColumn<Pedido, Integer> colTotalEfectivo;
    @FXML
    private TableColumn<Pedido, String> colFecha;
    @FXML
    private TableColumn<Pedido, Integer> colTotalPedido;
    @FXML
    private TableColumn<Pedido, Integer> colTotal;
    @FXML
    private ScrollPane spnDashboard;

    @FXML
    private TextField nombreCliente;
    @FXML
    private ImageView editando;
    @FXML
    private CheckBox chkHoy, chkFpAmbas;
    @FXML
    private Button btnAceptar, exitButton;

    @FXML
    private void initialize() {
        menuPane.setVisible(false);
        rightPane.setVisible(false);
        pnHistorico.setVisible(false);
        pnDashboard.setVisible(false);

        pedidoService.setPedidoController(this);

        Platform.runLater(() -> btnAceptar.requestFocus());
    }

    public VBox getDetallePedidos() {
        return detallePedidos;
    }

    public void setPedidoApp(PedidoApplication pedidoApp) {
        this.pedidoApp = pedidoApp;
    }

    public void setPedidoService(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
        System.out.println("PedidoService set en el controlador: " + this.pedidoService);
    }

    @FXML
    private void exit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de Salida");
        alert.setHeaderText(null);
        alert.setContentText("¿Seguro que quieres salir?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            /*Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("INFORMATION");
            info.setHeaderText(null);
            info.setContentText("Enviar msj con monto ganado a wsp");
            info.showAndWait();*/
            System.exit(0);
        }
    }

    public void close(){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    @FXML
    private void handlePedidosButtonClick() {
        menuPane.setVisible(true);
        rightPane.setVisible(true);
        pnHistorico.setVisible(false);
        pnDashboard.setVisible(false);
    }

    @FXML
    private void handleImageButtonClick(ActionEvent event) {
        String currentStyle = menuPane.getStyle();

        Button sourceButton = (Button) event.getSource();
        String menuCode = (String) sourceButton.getUserData();

        if (currentStyle.contains("blue")) {
            Hamburguesa selectedHamburguesa = hamburguesaDAO.getMenuByCode(menuCode);
            if (selectedHamburguesa != null) {
                List<HamburguesaTipo> hamburguesaTipoIds = hamburguesaTipoDAO.getHamburguesaTipoIds(selectedHamburguesa.getId());
                pedidoApp.agregarHamburguesa(selectedHamburguesa.getNombre(), hamburguesaTipoIds, true);
            } else {
                System.out.println("Hamburguesa no encontrada.");
            }
        } else if (Objects.equals(menuCode, "extra")) {
            pedidoApp.openExtraPromo();
        } else {
            String res = switch (menuCode) {
                case "cb", "di", "kk", "kl", "mn", "rm", "tn", "v", "vr" -> menuCode;
                default -> " ";
            };
            Hamburguesa selectedMenu = hamburguesaDAO.getMenuByCode(res);
            if (selectedMenu != null) {
                pedidoApp.openDetalleWindow(selectedMenu.getNombre());
            } else {
                System.out.println("Menu no encontrado.");
            }
        }
    }

    public void actualizarDetalles(String nombreHamburguesa, String tipoHamburguesa, int cantidad, double precio, List<Topping> toppingList, String txtObservaciones) {
        VBox vBox = new VBox(5);
        vBox.setPadding(new Insets(2, 8, 0, 8));
        HBox pedidoBox = new HBox(5);

        Map<String, Integer> toppingsMultiplicados = new HashMap<>();

        if (txtObservaciones != null && !txtObservaciones.trim().isEmpty()) {
            Pattern pattern = Pattern.compile("(\\d+)x([\\w\\s]+)");
            Matcher matcher = pattern.matcher(txtObservaciones);
            while (matcher.find()) {
                int cantidadTop = Integer.parseInt(matcher.group(1));
                String nombre = matcher.group(2).replaceAll("\\s+", "").toLowerCase();
                toppingsMultiplicados.put(nombre, cantidadTop);
            }
        }

        double totalToppings = 0;

        for (Topping topping : toppingList) {
            String nombreTopping = topping.getNombre().replaceAll("\\s+", "").toLowerCase();
            int cantidadTop = toppingsMultiplicados.getOrDefault(nombreTopping, 1);
            if (topping.getPrecio() != null) {
                totalToppings += topping.getPrecio() * cantidadTop;
            }
        }

        double totalDetalle = precio + totalToppings;
        int idActual = detalleId;
        int hamburguesaID = pedidoService.getHamburguesaTipo(nombreHamburguesa, tipoHamburguesa);

        DetallePedido detallePedido = new DetallePedido(detalleId, cantidad, hamburguesaID, totalDetalle, txtObservaciones);
        detallesPedidosList.add(detallePedido);

        pedidoService.agregarDetallePedido(detallePedido);
        pedidoService.agregarToppingsADetalle(detalleId, toppingList);
        detalleId++;

        Label pedidoLabel = new Label("(x" + cantidad + ") " + nombreHamburguesa + " " + tipoHamburguesa + " ($" + (int) precio + ")");
        Label precioLabel = new Label(String.format("$%d", (int) totalDetalle));
        int precioTotal = (int) pedidoService.actualizarTotal();
        lblTotal.setText("TOTAL: $" + precioTotal);

        Button deleteButton = new Button();
        try {
            Image image = new Image(getClass().getResource("/org/example/kaos/image/trash.png").toExternalForm());
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(12.0);
            imageView.setFitWidth(12.0);
            deleteButton.setGraphic(imageView);
            deleteButton.setVisible(false);
        } catch (NullPointerException e) {
            System.out.println("No se pudo cargar la imagen: " + e.getMessage());
        }
        deleteButton.setUserData(idActual);
        deleteButton.setOnAction(event -> {
            Integer id = (Integer) deleteButton.getUserData();
            System.out.println("Intentando eliminar detalle con ID: " + id);
            DetallePedido detalleEliminar = detallesPedidosList.stream()
                    .filter(detalle -> detalle.getId() == id)
                    .findFirst()
                    .orElse(null);

            if (detalleEliminar != null) {
                detallesPedidosList.remove(detalleEliminar);
                detallePedidos.getChildren().remove(vBox);
                pedidoService.removeDetallePedido(detalleEliminar);

                int precioTotalActualizado = (int) pedidoService.actualizarTotal();
                lblTotal.setText("TOTAL: $" + precioTotalActualizado);
                System.out.println("Total después de la eliminación y actualización: $" + precioTotalActualizado);
            } else {
                System.out.println("No se encontró el detalle con ID: " + id);
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        pedidoBox.getChildren().addAll(pedidoLabel, spacer, precioLabel, deleteButton);
        vBox.getChildren().add(pedidoBox);

        if (toppingList != null && !toppingList.isEmpty()) {
            VBox toppingsBox = new VBox(5);
            toppingsBox.setPadding(new Insets(5, 0, 0, 0));

            for (Topping topping : toppingList) {
                String nombreTopping = topping.getNombre().replaceAll("\\s+", "").toLowerCase();
                int cantidadTop = toppingsMultiplicados.getOrDefault(nombreTopping, 1);
                if (topping.getPrecio() != null) {
                    int precioTop = (int) Math.round(topping.getPrecio() * cantidadTop);
                    Label toppingLabel = new Label("Extra: " + topping.getNombre() + " ($" + precioTop + ")");
                    toppingsBox.getChildren().add(toppingLabel);
                } else {
                    Label toppingLabel = new Label("Sin: " + topping.getNombre());
                    toppingsBox.getChildren().add(toppingLabel);
                }
            }
            vBox.getChildren().add(toppingsBox);
        }

        if (txtObservaciones != null && !txtObservaciones.trim().isEmpty()) {
            Label observacionesLabel = new Label("Observaciones: " + txtObservaciones);
            observacionesLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #555555;");
            vBox.getChildren().add(observacionesLabel);
        }
        detallePedidos.getChildren().add(vBox);
        this.listTopping = toppingList;
    }

    public void deletePedidos() {
        deleteButtonsVisible = !deleteButtonsVisible;
        if (!detallesPedidosList.isEmpty() || !extraList.isEmpty()) {
            for (var node : detallePedidos.getChildren()) {
                if (node instanceof VBox) {
                    VBox vBox = (VBox) node;
                    for (var child : vBox.getChildren()) {
                        if (child instanceof HBox) {
                            HBox hBox = (HBox) child;
                            for (var hboxChild : hBox.getChildren()) {
                                if (hboxChild instanceof Button) {
                                    Button deleteButton = (Button) hboxChild;
                                    deleteButton.setVisible(deleteButtonsVisible);
                                }
                            }
                        }
                        if (child instanceof Button) {
                            Button deleteButton = (Button) child;
                            deleteButton.setVisible(deleteButtonsVisible);
                        }
                    }
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se ha insertado ningún detalle en el pedido");
            alert.showAndWait();
        }
    }

    public void aceptarPedido(ActionEvent actionEvent) {
        if (!detallesPedidosList.isEmpty() || !extraList.isEmpty()) {
            pedidoService.setDetalleTopping(listTopping);
            pedidoService.setDetalleExtra(extraList);
            pedidoApp.openDatosClienteWindow(pedidoService);
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se ha insertado ningún detalle en el pedido");
            alert.showAndWait();
        }
    }

    public void cancelarPedido(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de Cancelación");
        alert.setHeaderText(null);
        alert.setContentText("¿Está seguro de que desea cancelar el pedido?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (pedidoService != null) {
                pedidoService.getDetallesPedidosList().clear();
            } else {
                System.out.println("PedidoService no está inicializado.");
            }
            detallesPedidosList.clear();
            detallePedidos.getChildren().clear();
            lblTotal.setText(" ");
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Pedido Cancelado");
            info.setHeaderText(null);
            info.setContentText("El pedido ha sido cancelado.");
            info.showAndWait();
        }
    }

    public void limpiarLblTotal() {
        lblTotal.setText("");
    }

    public void historico(ActionEvent actionEvent) {
        menuPane.setVisible(false);
        rightPane.setVisible(false);
        pnDashboard.setVisible(false);
        pnHistorico.setVisible(true);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setVisible(false);
        colNombre.setCellValueFactory(new PropertyValueFactory<>("cliente_nombre"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colFormaPago.setCellValueFactory(cellData -> {
            int idTipoPago = cellData.getValue().getId_pago();
            String nombreTipoPago = tipoPagoDAO.getNameTipoPagoFromId(idTipoPago);
            String primeraLetra = nombreTipoPago.length() > 0 ? nombreTipoPago.substring(0, 1) : "";
            return new SimpleStringProperty(primeraLetra);
        });
        colTotalEfectivo.setCellValueFactory(new PropertyValueFactory<>("total_efectivo"));
        colTotalTransferencia.setCellValueFactory(new PropertyValueFactory<>("total_transferencia"));
        colCostoEnvio.setCellValueFactory(new PropertyValueFactory<>("precio_envio"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        colFecha.setCellValueFactory(cellData -> {
            LocalDateTime fecha = cellData.getValue().getFecha_pedido();
            String fechaFormateada = (fecha != null) ? fecha.format(formatter) : "";
            return new SimpleStringProperty(fechaFormateada);
        });
        colTotalPedido.setCellValueFactory(cellData -> {
            double precioTotal = cellData.getValue().getPrecio_total();
            return new SimpleIntegerProperty((int) precioTotal).asObject();
        });
        colTotal.setCellValueFactory(cellData -> {
            double precioEnvio = cellData.getValue().getPrecio_envio();
            double precioTotal = cellData.getValue().getPrecio_total();
            int suma = (int) (precioEnvio + precioTotal);
            return new SimpleIntegerProperty(suma).asObject();
        });
        colTotal.setCellFactory(col -> new TableCell<Pedido, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("$" + item);
                    setStyle("-fx-font-weight: bold; -fx-alignment: CENTER-RIGHT;");
                }
            }
        });

        cargarTablaPedidosHoy();
        //cargarTablaPedidos();

        tableHistorico.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Pedido selectedItem = tableHistorico.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    pedidoApp.abrirVentanaDetallePedido(selectedItem, false);
                }
            }
        });
    }

    private void cargarTablaPedidos() {
        List<Pedido> pedidos = pedidoService.getAllPedidos();
        tableHistorico.getItems().setAll(pedidos);
    }

    public void deletePedidosCreados() {
        Pedido selectedPedido = tableHistorico.getSelectionModel().getSelectedItem();
        if (selectedPedido != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText("¿Está seguro de que desea eliminar este pedido?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    int pedidoId = selectedPedido.getId();
                    boolean exito = pedidoService.eliminarPedido(pedidoId);

                    if (exito) {
                        System.out.println("Pedido eliminado exitosamente");
                        cargarTablaPedidos();
                    } else {
                        System.out.println("Error al eliminar el pedido");
                    }
                } else {
                    System.out.println("Eliminación cancelada");
                }
            });
        } else {
            System.out.println("Por favor, seleccione un pedido para eliminar.");
        }
    }

    public void editButton(ActionEvent event) {
        String currentStyle = menuPane.getStyle();
        editando.setVisible(true);
        if (currentStyle.contains("blue")) {
            menuPane.setStyle(currentStyle.replace("-fx-border-color: blue; -fx-border-width: 2px;", ""));
            editando.setVisible(false);
        } else {
            menuPane.setStyle("-fx-border-color: blue; -fx-border-width: 2px;");
        }
    }

    public void excel(ActionEvent actionEvent) {
        List<Pedido> pedidos = pedidoService.getAllPedidos();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Archivo Excel");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx"));

        fileChooser.setInitialFileName("historico_pedidos.xlsx");

        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            String filePath = file.getAbsolutePath();
            pedidoService.exportarPedidos(pedidos, filePath);
            System.out.println("Exportación completada en: " + filePath);
        } else {
            System.out.println("Exportación cancelada");
        }
    }

    public void search(ActionEvent actionEvent) {
        filtrarPedidosPorNombre();
    }

    @FXML
    private void filtrarPedidosPorNombre() {
        String nombre = nombreCliente.getText().trim();
        if(!nombre.isEmpty()) {
            List<Pedido> pedidos = pedidoService.buscarPedidosPorNombre(nombre);
            tableHistorico.setItems(FXCollections.observableArrayList(pedidos));
        } else {
            filtrarPedidosDaily(chkHoy.isSelected());
        }
    }

    public void pedidosDelDia(ActionEvent actionEvent) {
        if (chkHoy.isSelected()){
            filtrarPedidosDaily(true);
        } else {
            filtrarPedidosDaily(false);
        }
    }

    @FXML
    private void filtrarPedidosDaily(boolean esHoy) {
        try {
            int contEf = 0, contMp = 0, contPedidos = 0;
            List<Pedido> pedidos;
            if (esHoy) {
                pedidos = pedidoService.getAllPedidosDaily();
            } else {
                pedidos = pedidoService.getAllPedidos();
            }
            if (chkFpAmbas.isSelected()) {
                pedidos = pedidos.stream()
                        .filter(p -> p.getId_pago() == 3)
                        .collect(Collectors.toList());
            }
            if (!chkHoy.isSelected() && !chkFpAmbas.isSelected()) {
                pedidos = pedidoService.getAllPedidos();
            }
            ObservableList<Pedido> filteredList = FXCollections.observableArrayList(pedidos);
            for (Pedido pedido : filteredList) {
                contPedidos++;
                if (pedido.getId_pago() == 1) {
                    contEf += pedido.getTotal_efectivo();
                } else if (pedido.getId_pago() == 2){
                    contMp += pedido.getTotal_transferencia();
                } else if (pedido.getId_pago() == 3) {
                    contEf += pedido.getTotal_efectivo();
                    contMp += pedido.getTotal_transferencia();
                }
            }
            tableHistorico.setItems(filteredList);
            TotalPedidosCount.setText(String.valueOf(contPedidos));
            TotalEf.setText("$" + String.valueOf(contEf));
            TotalMp.setText("$" + String.valueOf(contMp));

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Entrada");
            alert.setHeaderText(null);
            alert.setContentText("Por favor ingrese un ID de pedido válido.");
            alert.showAndWait();
        }
    }

    public void cargarTablaPedidosHoy() {
        chkHoy.setSelected(true);
        if(chkHoy.isSelected()){
            filtrarPedidosDaily(true);
        }
    }

    public void actualizarDetalleExtras(int contPapas, int contPromo, int contSalsa, List<Extra> ListaExtra) {
        VBox vBoxExtras = new VBox(5);
        vBoxExtras.setPadding(new Insets(2, 8, 0, 8));
        int idActual = detalleId;
        if (contPapas > 0) {
            Extra extraPapa = detalleService.getExtraById(1);
            Label lblPapasTitulo = new Label("(x" + contPapas + ") Papas Extra" + " " + "($" + (contPapas * extraPapa.getPrecio()) + ")");
            vBoxExtras.getChildren().add(lblPapasTitulo);

            DetallePedido detallePedido = new DetallePedido(detalleId, contPapas, (contPapas * extraPapa.getPrecio()), extraPapa.getId_extra());
            detallesPedidosList.add(detallePedido);
            pedidoService.agregarDetallePedido(detallePedido);
        }
        for(Extra extralist1 : ListaExtra){
            if(extralist1.getId_tipo() == 2){
                Label lblSalsasTitulo = new Label("(x" + contSalsa + ") Salsa Extra " + extralist1.getNombre() + " " + "($" + (contSalsa * extralist1.getPrecio()) + ")");
                vBoxExtras.getChildren().add(lblSalsasTitulo);
                DetallePedido detallePedido = new DetallePedido(detalleId, contSalsa, (contSalsa * extralist1.getPrecio()), extralist1.getId_extra());
                detallesPedidosList.add(detallePedido);
                pedidoService.agregarDetallePedido(detallePedido);
            }
            else if(extralist1.getId_tipo() == 3){
                Label lblPromoTitulo = new Label("(x" + contPromo + ") Promo " + extralist1.getNombre() + " " + "($" + (contPromo * extralist1.getPrecio()) + ")");
                vBoxExtras.getChildren().add(lblPromoTitulo);
                DetallePedido detallePedido = new DetallePedido(detalleId, contPapas, (contPromo * extralist1.getPrecio()), extralist1.getId_extra());
                detallesPedidosList.add(detallePedido);
                pedidoService.agregarDetallePedido(detallePedido);
            }
        }
        detalleId++;

        pedidoService.addDetalleExtra(extraList);
        int precioTotal = (int) pedidoService.actualizarTotal();
        lblTotal.setText("TOTAL: $" + (precioTotal));
        Button deleteButton = new Button();
        try {
            Image image = new Image(getClass().getResource("/org/example/kaos/image/trash.png").toExternalForm());
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(12.0);
            imageView.setFitWidth(12.0);
            deleteButton.setGraphic(imageView);
            deleteButton.setVisible(false);
        } catch (NullPointerException e) {
            System.out.println("No se pudo cargar la imagen: " + e.getMessage());
        }
        deleteButton.setVisible(deleteButtonsVisible);

        deleteButton.setUserData(idActual);

        deleteButton.setOnAction(event -> {
            Integer id = (Integer) deleteButton.getUserData();
            System.out.println("Eliminando conjunto de extras con ID: " + id);

            Iterator<DetallePedido> iterator = detallesPedidosList.iterator();
            while (iterator.hasNext()) {
                DetallePedido detalleExtra = iterator.next();
                if (detalleExtra.getId() == id) {
                    pedidoService.removeDetallePedido(detalleExtra);
                    iterator.remove();
                    detallePedidos.getChildren().remove(vBoxExtras);
                }
            }
            int precioTotalActualizado = (int) pedidoService.actualizarTotal();
            lblTotal.setText("TOTAL: $" + precioTotalActualizado);
            System.out.println("Total después de la eliminación y actualización: $" + precioTotalActualizado);
        });
        vBoxExtras.getChildren().add(deleteButton);
        if (contPapas > 0 || ListaExtra.stream().count() > 0) {
            detallePedidos.getChildren().add(vBoxExtras);
        }
    }

    public void editarPedido(ActionEvent actionEvent) {
        Pedido selectedItem = tableHistorico.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            pedidoApp.abrirVentanaDetallePedido(selectedItem, true);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, seleccione un pedido para editar.");
            alert.showAndWait();
        }
    }

    public void recargarHistorico() {
        historico(null);
    }

    public void pedidosFpAmbas(ActionEvent actionEvent) {
        if (chkFpAmbas.isSelected()){
            filtrarPedidosFpAmbas(true);
        } else {
            filtrarPedidosFpAmbas(false);
        }
    }

    private void filtrarPedidosFpAmbas(boolean b) {
        filtrarPedidosDaily(chkHoy.isSelected());
    }

    public void dashboard(ActionEvent actionEvent) {
        menuPane.setVisible(false);
        rightPane.setVisible(false);
        pnHistorico.setVisible(false);
        pnDashboard.setVisible(true);

        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        VBox contenidoTotal = new VBox(20);
        contenidoTotal.setPadding(new Insets(10));
        contenidoTotal.setAlignment(Pos.CENTER);
        contenidoTotal.setStyle("-fx-background-color: white;");

        contenidoTotal.getChildren().add(cargarGraficosHamburguesas());
        contenidoTotal.getChildren().add(cargarHamburguesaMasVendida());
        contenidoTotal.getChildren().add(cargarResumenGanancias());

        spnDashboard.setContent(contenidoTotal);
        spnDashboard.setFitToWidth(true);
        spnDashboard.setFitToHeight(true);
    }

    private Node cargarHamburguesaMasVendida() {
        Map<String, Integer> masVendidaDiario = hamburguesaService.getHamburguesasMasVendidasDiario(true);
        List<Map.Entry<String, Integer>> ordenado = new ArrayList<>(masVendidaDiario.entrySet());
        ordenado.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        Map<String, Integer> masVendidaSemanal = hamburguesaService.getHamburguesasMasVendidasDiario(false);
        List<Map.Entry<String, Integer>> ordenado1 = new ArrayList<>(masVendidaSemanal.entrySet());
        ordenado1.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        Label tituloMasVendidas = new Label("Mas Vendidas");
        tituloMasVendidas.setFont(Font.font(18));
        tituloMasVendidas.setAlignment(Pos.CENTER);
        tituloMasVendidas.setTextFill(Color.RED);

        Label diarioTitulo = new Label("Diario");
        diarioTitulo.setFont(Font.font(16));
        diarioTitulo.setTextFill(Color.GREEN);

        VBox diarioBox = new VBox(5, diarioTitulo);
        diarioBox.setAlignment(Pos.TOP_LEFT);
        int cont = 0, cont1 = 0;
        for (Map.Entry<String, Integer> entry : ordenado) {
            String nombre = "";
            Integer cantidad = entry.getValue();
            switch (cont) {
                case 0:
                    nombre += " 1_ " + entry.getKey();
                    break;
                case 1:
                    nombre += " 2_ " + entry.getKey();
                    break;
                case 2:
                    nombre += " 3_ " + entry.getKey();
                    break;
            }
            Label lblMasVendidaDiario = new Label(nombre + " (" + cantidad + ").");
            lblMasVendidaDiario.setFont(Font.font(14));
            lblMasVendidaDiario.setStyle("-fx-font-weight: bold;");
            diarioBox.getChildren().add(lblMasVendidaDiario);
            cont++;
        }

        Label SemanalTitulo = new Label("Semanal");
        SemanalTitulo.setFont(Font.font(16));
        SemanalTitulo.setTextFill(Color.GREEN);

        VBox semanalBox = new VBox(5, SemanalTitulo);
        semanalBox.setAlignment(Pos.TOP_LEFT);
        for (Map.Entry<String, Integer> entry : ordenado1) {
            String nombre = "";
            Integer cantidad = entry.getValue();
            switch (cont1) {
                case 0:
                    nombre += " 1_ " + entry.getKey();
                    break;
                case 1:
                    nombre += " 2_ " + entry.getKey();
                    break;
                case 2:
                    nombre += " 3_ " + entry.getKey();
                    break;
            }
            Label lblMasVendidaSemanal= new Label(nombre + " (" + cantidad + ").");
            lblMasVendidaSemanal.setFont(Font.font(14));
            lblMasVendidaSemanal.setStyle("-fx-font-weight: bold;");
            semanalBox.getChildren().add(lblMasVendidaSemanal);
            cont1++;
        }

        HBox resumen = new HBox(50, diarioBox, semanalBox);
        resumen.setAlignment(Pos.CENTER);
        resumen.setPadding(new Insets(20, 0, 0, 0));

        VBox contenido = new VBox(10, tituloMasVendidas, resumen);
        contenido.setAlignment(Pos.CENTER);

        return contenido;
    }

    private Node cargarGraficosHamburguesas(){
        VBox contenido = new VBox(20);
        contenido.setPadding(new Insets(10));
        contenido.setAlignment(Pos.CENTER);
        contenido.setStyle("-fx-background-color: white;");

        Node graficoDiario = crearGraficoHamburguesas("Hamburguesas Diarias", true);
        Node graficoSemanal = crearGraficoHamburguesas("Hamburguesas Semanales", false);

        HBox fila = new HBox(30, graficoDiario, graficoSemanal);
        fila.setAlignment(Pos.CENTER);

        contenido.getChildren().add(fila);

        return contenido;
    }

    private Node crearGraficoHamburguesas(String titulo, boolean esDiario) {
        PieChart pieChart = new PieChart();
        pieChart.setLegendVisible(false);
        pieChart.setLabelsVisible(false);
        pieChart.setPrefSize(250, 250);
        pieChart.setMaxSize(250, 250);
        pieChart.setMinSize(250, 250);

        Map<String, Integer> hamburguesas = hamburguesaService.getHamburguesas(esDiario);

        List<Map.Entry<String, Integer>> ordenado = new ArrayList<>(hamburguesas.entrySet());
        ordenado.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        Map<PieChart.Data, String> colorMap = new HashMap<>();

        for (Map.Entry<String, Integer> entry : ordenado) {
            String nombre = entry.getKey();
            Integer cantidad = entry.getValue();
            PieChart.Data data = new PieChart.Data(nombre, cantidad);
            pieChart.getData().add(data);
            Tooltip tooltip = new Tooltip(nombre + ": " + cantidad);
            Tooltip.install(data.getNode(), tooltip);
            String color = getColorHex();
            colorMap.put(data, color);
        }

        Platform.runLater(() -> {
            for (Map.Entry<PieChart.Data, String> entry : colorMap.entrySet()) {
                PieChart.Data data = entry.getKey();
                String color = entry.getValue();
                data.getNode().setStyle("-fx-pie-color: " + color + ";");
            }
        });

        GridPane leyendaGrid = new GridPane();
        leyendaGrid.setHgap(20);
        leyendaGrid.setVgap(5);
        leyendaGrid.setPadding(new Insets(10, 0, 0, 0));

        List<PieChart.Data> dataList = new ArrayList<>(pieChart.getData());
        for (int i = 0; i < dataList.size(); i++) {
            PieChart.Data data = dataList.get(i);
            Region colorBox = new Region();
            colorBox.setPrefSize(10, 10);
            colorBox.setStyle("-fx-background-color: " + colorMap.get(data) + "; -fx-border-color: black;");
            Label label = new Label(data.getName() + " (" + (int) data.getPieValue() + ")");
            label.setFont(Font.font(10));
            HBox item = new HBox(5, colorBox, label);
            item.setAlignment(Pos.CENTER_LEFT);

            int col = i < 14 ? 0 : 1;
            int row = i < 14 ? i : i - 14;
            leyendaGrid.add(item, col, row);
        }

        Label title = new Label(titulo);
        title.setFont(Font.font(18));
        title.setAlignment(Pos.CENTER);

        VBox contenedor = new VBox(5);
        contenedor.setPadding(new Insets(10));
        contenedor.setAlignment(Pos.TOP_LEFT);
        contenedor.getChildren().addAll(title, pieChart, leyendaGrid);

        return contenedor;
    }

    private String getColorHex() {
        Random random = new Random();
        int r = random.nextInt(200) + 30;
        int g = random.nextInt(200) + 30;
        int b = random.nextInt(200) + 30;
        return String.format("#%02X%02X%02X", r, g, b);
    }

    private Node cargarResumenGanancias() {
        int totalEfectivoDiario = pedidoService.getEfectivoDiario(true);
        int totalTransferenciaDiario = pedidoService.getTransferenciaDiario(true);
        int totalEfectivoSemanal = pedidoService.getEfectivoDiario(false);
        int totalTransferenciaSemanal = pedidoService.getTransferenciaDiario(false);

        int totalEfectivo = totalEfectivoDiario + totalEfectivoSemanal;
        int totalTransferencia = totalTransferenciaDiario + totalTransferenciaSemanal;

        Label tituloGanancias = new Label("Ganancias");
        tituloGanancias.setFont(Font.font(18));
        tituloGanancias.setAlignment(Pos.CENTER);
        tituloGanancias.setTextFill(Color.RED);;

        Label diarioTitulo = new Label("Diario");
        diarioTitulo.setFont(Font.font(16));
        diarioTitulo.setTextFill(Color.GREEN);
        Label efectivoDiario = new Label("Efectivo: $" + totalEfectivoDiario);
        efectivoDiario.setFont(Font.font(14));
        efectivoDiario.setStyle("-fx-font-weight: bold;");
        Label transferenciaDiario = new Label("Transferencia: $" + totalTransferenciaDiario);
        transferenciaDiario.setFont(Font.font(14));
        transferenciaDiario.setStyle("-fx-font-weight: bold;");

        VBox diarioBox = new VBox(5, diarioTitulo, efectivoDiario, transferenciaDiario);
        diarioBox.setAlignment(Pos.TOP_LEFT);

        Label semanalTitulo = new Label("Semanal");
        semanalTitulo.setFont(Font.font(16));
        semanalTitulo.setTextFill(Color.GREEN);
        Label efectivoSemanal = new Label("Efectivo: $" + totalEfectivoSemanal);
        efectivoSemanal.setFont(Font.font(14));
        efectivoSemanal.setStyle("-fx-font-weight: bold;");
        Label transferenciaSemanal = new Label("Transferencia: $" + totalTransferenciaSemanal);
        transferenciaSemanal.setFont(Font.font(14));
        transferenciaSemanal.setStyle("-fx-font-weight: bold;");

        VBox semanalBox = new VBox(5, semanalTitulo, efectivoSemanal, transferenciaSemanal);
        semanalBox.setAlignment(Pos.TOP_LEFT);

        Label totalTitulo = new Label("Total");
        totalTitulo.setFont(Font.font(16));
        totalTitulo.setTextFill(Color.GREEN);
        Label totalEfectivoLabel = new Label("$" + totalEfectivo);
        totalEfectivoLabel.setFont(Font.font(14));
        totalEfectivoLabel.setStyle("-fx-font-weight: bold;");
        Label totalTransferenciaLabel = new Label("$" + totalTransferencia);
        totalTransferenciaLabel.setFont(Font.font(14));
        totalTransferenciaLabel.setStyle("-fx-font-weight: bold;");

        VBox totalBox = new VBox(5, totalTitulo, totalEfectivoLabel, totalTransferenciaLabel);
        totalBox.setAlignment(Pos.TOP_LEFT);

        HBox resumen = new HBox(50, diarioBox, semanalBox, totalBox);
        resumen.setAlignment(Pos.CENTER);
        resumen.setPadding(new Insets(20, 0, 0, 0));

        VBox contenido = new VBox(10, tituloGanancias, resumen);
        contenido.setAlignment(Pos.CENTER);

        return contenido;
    }
}
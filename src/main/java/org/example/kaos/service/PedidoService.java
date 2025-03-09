package org.example.kaos.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.kaos.controller.PedidoController;
import org.example.kaos.entity.*;
import org.example.kaos.repository.HamburguesaDAO;
import org.example.kaos.repository.HamburguesaTipoDAO;
import org.example.kaos.repository.PedidoDAO;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import org.example.kaos.repository.TipoPagoDAO;
import org.json.JSONArray;

public class PedidoService {
    private final HamburguesaDAO hamburguesaDAO = new HamburguesaDAO();
    private final HamburguesaTipoDAO hamburguesaTipoDAO = new HamburguesaTipoDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private static final TipoPagoDAO tipoPagoDAO = new TipoPagoDAO();
    private PedidoController pedidoController;

    private final List<DetallePedido> detallesPedidosList = new ArrayList<>();
    private final List<ToppingPedido> detalleToppingPedidoList = new ArrayList<>();
    private List<Topping> toppingList = new ArrayList<>();
    private List<Extra> listaDetalleExtra = new ArrayList<>();
    private List<DetallePedido> detallesExtraPedidosList = new ArrayList<>();
    private Map<Integer, List<Topping>> toppingsPorDetalle = new HashMap<>();

    public PedidoService(HamburguesaDAO hamburguesaDAO, HamburguesaTipoDAO hamburguesaTipoDAO) {}

    public PedidoService() {
        this(new HamburguesaDAO(), new HamburguesaTipoDAO());
    }

    public DetallePedido addDetallePedido(int hamburguesaId, int cantidad, double precio, String observaciones) {
        DetallePedido detallePedido = new DetallePedido(detallesPedidosList.size(), cantidad, hamburguesaId, precio, observaciones);
        detallesPedidosList.add(detallePedido);
        return detallePedido;
    }

    public ToppingPedido addDetalleToppingPedido(int idTopping, boolean agregago){
        ToppingPedido detalleToppingPedido = new ToppingPedido(0, detallesPedidosList.size() + 1, idTopping, agregago);
        detalleToppingPedidoList.add(detalleToppingPedido);
        return detalleToppingPedido;
    }

    public int getHamburguesaTipo(String nombreHamburguesa, String tipoHamburguesa) {
        return hamburguesaTipoDAO.getHamburguesaTipoIds(nombreHamburguesa, tipoHamburguesa);
    }

    public void removeDetallePedido(DetallePedido detallePedido) {
        int index = -1;
        for (int i = 0; i < detallesPedidosList.size(); i++) {
            if (detallesPedidosList.get(i).getId() == detallePedido.getId()) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            Iterator<ToppingPedido> iterator = detalleToppingPedidoList.iterator();
            while (iterator.hasNext()) {
                ToppingPedido toppingPedido = iterator.next();
                if (toppingPedido.getIdDetallePedido() == detallePedido.getId()) {
                    iterator.remove();
                }
            }
            detallesPedidosList.remove(index);
            double nuevoTotal = actualizarTotal();
            System.out.println("Nuevo total después de eliminar: " + nuevoTotal);
        }
    }

    public void setPedidoController(PedidoController pedidoController) {
        this.pedidoController = pedidoController;
    }

    public double getPrecioTotalPedido() {
        return actualizarTotal();
    }

    public List<DetallePedido> getDetallesPedidosList() {
        return detallesPedidosList;
    }

    public List<ToppingPedido> getDetallesToppingPedidosList() {
        return detalleToppingPedidoList;
    }

    public int insertarPedido(String nombreCliente, String direccion, Timestamp fecha, int idTipoPago, double costoEnvio, double precioTotal, JSONArray detallesJson) {
        int idPedido = pedidoDAO.insertarPedido(nombreCliente, direccion, fecha, idTipoPago, costoEnvio, precioTotal, detallesJson);
        return idPedido;
    }

    public boolean eliminarPedido(int pedidoId) {
        return pedidoDAO.eliminarPedido(pedidoId);
    }

    public double getPrecioTotalTopping(List<Topping> toppingList) {
        double totalTop = 0.0;
        if (toppingList != null && !toppingList.isEmpty()) {
            for (Topping topping : toppingList) {
                if (topping.getPrecio() != null) {
                    totalTop += topping.getPrecio();
                }
            }
        }
        return totalTop;
    }

    public double actualizarTotal() {
        double totalFinal = 0;
        for (DetallePedido detalle : detallesPedidosList) {
            totalFinal += detalle.getPrecio_unitario();
        }
        for (DetallePedido extra : detallesExtraPedidosList){
            totalFinal += extra.getPrecio_unitario();
        }
        System.out.println("total de actualizar: " + totalFinal);
        return totalFinal;
    }

    public List<Pedido> getAllPedidos() {
        return pedidoDAO.getAllPedidos();
    }

    public List<Pedido> getAllPedidosDaily() {
        return pedidoDAO.getAllPedidosDaily();
    }

    public Pedido getPedidoId(int id) {
        return pedidoDAO.getPedidoById(id);
    }

    public static void exportarPedidos(List<Pedido> pedidos, String rutaArchivo) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Histórico de Pedidos");
        Row headerRow = sheet.createRow(0);
        String[] columnas = {"ID", "Cliente", "Dirección", "Forma de Pago", "Fecha", "Costo Envío", "Total"};
        CellStyle headerStyle = getHeaderStyle(workbook);
        for (int i = 0; i < columnas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }
        int rowNum = 1;
        for (Pedido pedido : pedidos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(pedido.getId());
            row.createCell(1).setCellValue(pedido.getCliente_nombre());
            row.createCell(2).setCellValue(pedido.getDireccion());

            String formaPago = tipoPagoDAO.getNameTipoPagoFromId(pedido.getId_pago());

            row.createCell(3).setCellValue(formaPago);
            row.createCell(4).setCellValue(pedido.getFecha_pedido().toString());
            row.createCell(5).setCellValue(pedido.getPrecio_envio());
            row.createCell(6).setCellValue(pedido.getPrecio_total());
        }
        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }
        try (FileOutputStream fileOut = new FileOutputStream(rutaArchivo)) {
            workbook.write(fileOut);
            System.out.println("Archivo Excel creado: " + rutaArchivo);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static CellStyle getHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    public void setDetalleTopping(List<Topping> listTopping) {
        this.toppingList = listTopping;
    }

    public void addDetalleExtra(List<Extra> extraList) {
        listaDetalleExtra.addAll(extraList);
    }

    public void removeDetalleExtra(DetallePedido extras) {
        int index = -1;
        for (int i = 0; i < detallesExtraPedidosList.size(); i++) {
            if (detallesExtraPedidosList.get(i).getId() == extras.getId()) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            Iterator<ToppingPedido> iterator = detalleToppingPedidoList.iterator();
            while (iterator.hasNext()) {
                ToppingPedido toppingPedido = iterator.next();
                if (toppingPedido.getIdDetallePedido() == extras.getId()) {
                    iterator.remove();
                }
            }
            detallesExtraPedidosList.remove(index);
            actualizarTotal();
        }
    }

    public void setDetalleExtra(List<Extra> extraList) {
        this.listaDetalleExtra = extraList;
    }

    public void agregarToppingsADetalle(int detalleId, List<Topping> toppings) {
        toppingsPorDetalle.put(detalleId, toppings);
        System.out.println("Toppings almacenados para detalle " + detalleId + ": " + toppings);
    }

    public List<Topping> getToppingsByDetalleId(int detalleId) {
        List<Topping> toppings = toppingsPorDetalle.getOrDefault(detalleId, new ArrayList<>());
        System.out.println("Toppings recuperados para detalle " + detalleId + ": " + toppings);
        return toppings;
    }

    public void agregarDetallePedido(DetallePedido detallePedido) {
        detallesPedidosList.add(detallePedido);
        System.out.println("Detalle agregado: " + detallePedido);
    }

    public List<Pedido> buscarPedidosPorNombre(String nombre) {
        List<Pedido> pedidoBuscar = PedidoDAO.getPedidoByName(nombre);
        return pedidoBuscar;
    }

    public int updateFPagoPedido(int id, int fPago) {
        int res = PedidoDAO.setFPagpPedido(id, fPago);
        return res;
    }
}
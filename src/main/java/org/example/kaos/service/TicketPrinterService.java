package org.example.kaos.service;

import org.example.kaos.entity.*;

import javax.print.PrintService;
import java.awt.*;
import java.awt.print.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class TicketPrinterService implements Printable {
    private DetalleService detalleService = new DetalleService();
    private PedidoService pedidoService = new PedidoService();
    private HamburguesaService hamburguesaService = new HamburguesaService();
    private ExtraService extraService = new ExtraService();
    private ToppingService toppingService = new ToppingService();
    private ToppingPedidoService toppingPedidoService = new ToppingPedidoService();
    private List<DetallePedido> detallesPedido = new ArrayList<>();

    private Pedido pedido;
    private Image logo;

    public TicketPrinterService() {
        try {
            logo = ImageIO.read(new File("src/main/resources/org/example/kaos/image/kaoslogo.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void imprimir(int idPedido) {
        cargarPedido(idPedido);
        PrinterJob job = PrinterJob.getPrinterJob();
        PrintService[] printers = PrinterJob.lookupPrintServices();
        for (PrintService printer : printers) {
            if (printer.getName().equals("POS-80-Series")) {
                try {
                    job.setPrintService(printer);
                    break;
                } catch (PrinterException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
        job.setPrintable(this);
        /*try {
            job.print();
        } catch (PrinterException e) {
            e.printStackTrace();
        }*/
        for (int i = 0; i < 2; i++) {
            try {
                job.print();
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        }
    }

    private void cargarPedido(int idPedido) {
        pedido = pedidoService.getPedidoId(idPedido);
        detallesPedido = detalleService.getDetallePedidoList(idPedido);
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        int totalExtra = 0;
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        Font boldFont = new Font("Monospaced", Font.BOLD, 9);
        Font plainFont = new Font("Monospaced", Font.PLAIN, 9);
        int y = 20;

        if (logo != null) {
            g.drawImage(logo, 45, y, 93, 20, null);
            y += 40;
        }

        g2d.setFont(boldFont);
        g2d.drawString("******** TICKET ********", 35, y);
        y += 15;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        g2d.drawString("Fecha: " + pedido.getFecha_pedido().format(formatter), 10, y);
        y += 15;
        g2d.drawString("Cliente: " + pedido.getCliente_nombre().trim(), 10, y);
        y += 15;
        g2d.drawString("Dirección: " + pedido.getDireccion().trim(), 10, y);
        y += 15;
        g2d.drawString("----------------------------------", 10, y);
        y += 15;

        for (DetallePedido detalle : detallesPedido) {
            g2d.setFont(boldFont);
            g2d.drawString(detalle.getCantidad() + "x", 10, y);
            g2d.setFont(plainFont);

            if (detalle.getTipoHamburguesa() > 0) {
                HamburguesaTipo hamburguesaTipo = hamburguesaService.getHamburguesaTipoByID(detalle.getTipoHamburguesa());

                if (hamburguesaTipo != null && hamburguesaTipo.getHamburguesa_id() > 0) {
                    Hamburguesa hamburguesa = hamburguesaService.getHamburguesaByID(hamburguesaTipo.getHamburguesa_id());
                    TipoHamburguesa tipoHamburguesa = hamburguesaService.getTipoHamburguesa(hamburguesaTipo.getTipo_id());

                    if (hamburguesa != null && hamburguesa.getCodigo() != null && !hamburguesa.getCodigo().isEmpty()) {
                        String item = " " + hamburguesa.getCodigo().toUpperCase().trim() + " (" + tipoHamburguesa.getTipo() + ")";
                        g2d.drawString(item, 20, y);
                    }
                }

                String precio = "$" + Math.round(detalle.getCantidad() * hamburguesaTipo.getPrecios());
                g2d.drawString(precio, 190 - g2d.getFontMetrics().stringWidth(precio), y);
                y += 15;
            }

            if (detalle.getObservacion() != null && !detalle.getObservacion().isEmpty()) {
                g2d.drawString("Observación: " + detalle.getObservacion().trim(), 20, y);
                y += 15;
            }

            List<ToppingPedido> toppingPedidos = toppingPedidoService.getToppingPedido(detalle.getId());
            if (toppingPedidos != null && !toppingPedidos.isEmpty()) {
                for (ToppingPedido topping : toppingPedidos) {
                    Topping top = toppingService.getTopping(topping.getIdTopping(), topping.isAgregado());
                    if (top != null) {
                        if(topping.isAgregado()){
                            g2d.drawString("Extra: " + top.getNombre().trim(), 20, y);
                        } else {
                            g2d.drawString("Sin: " + top.getNombre().trim(), 20, y);
                            y += 15;
                        }
                    }
                    if(topping.isAgregado()) {
                        String precioTopping = "$" + Math.round(top.getPrecio());
                        g2d.drawString(precioTopping, 190 - g2d.getFontMetrics().stringWidth(precioTopping), y);
                        y += 15;
                    }
                }
            }

            if (detalle.getExtra_id() > 0) {
                Extra extra = extraService.getExtra(detalle.getExtra_id());
                String extraDescripcion = " ";

                if (extra.getId_tipo() == 2) {
                    extraDescripcion += "Salsa " + extra.getNombre();
                    totalExtra += detalle.getCantidad() * extra.getPrecio();
                } else if (extra.getId_tipo() == 3) {
                    extraDescripcion += "PROMO " + extra.getNombre();
                    totalExtra += detalle.getCantidad() * extra.getPrecio();
                } else {
                    extraDescripcion += extra.getNombre();
                    totalExtra += detalle.getCantidad() * extra.getPrecio();
                }

                g2d.drawString(extraDescripcion, 20, y);

                String precioExtra = "$" + Math.round(detalle.getCantidad() * extra.getPrecio());
                g2d.drawString(precioExtra, 190 - g2d.getFontMetrics().stringWidth(precioExtra), y);
                y += 15;
            }
        }

        g2d.drawString("----------------------------------", 10, y);
        y += 15;

        g2d.setFont(plainFont);
        String envio = "$" + Math.round(pedido.getPrecio_envio());
        g2d.drawString("Envio: " + envio, 150 - g2d.getFontMetrics().stringWidth(envio), y);
        y += 15;

        g2d.setFont(boldFont);
        String total = "$" + Math.round(pedido.getPrecio_total() + pedido.getPrecio_envio());
        g2d.drawString("Total: " + total, 150 - g2d.getFontMetrics().stringWidth(total), y);

        return PAGE_EXISTS;
    }
}
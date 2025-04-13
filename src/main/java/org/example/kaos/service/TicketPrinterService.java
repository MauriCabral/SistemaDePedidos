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
import java.awt.FontMetrics;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;

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

    private int cont = 0;
    private int idDetallePedido = 0;

    public TicketPrinterService() {
        try {
            logo = ImageIO.read(new File("C:/Kaos/kaoslogo.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void imprimir(int idPedido) {
        cargarPedido(idPedido);
        PrinterJob job = PrinterJob.getPrinterJob();
        PrintService[] printers = PrinterJob.lookupPrintServices();
        for (PrintService printer : printers) {
            if (printer.getName().equals("POS-80-Series")) { //POS-80-Series - Microsoft print to pdf
                try {
                    job.setPrintService(printer);
                    break;
                } catch (PrinterException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        PageFormat pf = job.defaultPage();
        Paper paper = new Paper();

        double ancho = 7.21 * 28.35;
        double alto = 21 * 28.35;

        paper.setSize(ancho, alto);
        paper.setImageableArea(0, 0, ancho, alto);
        pf.setPaper(paper);

        job.setPrintable(this, pf);

        try {
            job.print();
            Thread.sleep(1000);
            job.print();
        } catch (PrinterException | InterruptedException e) {
            e.printStackTrace();
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
        Font boldFont = new Font("Monospaced", Font.BOLD, 11);
        Font plainFont = new Font("Monospaced", Font.BOLD, 10);
        int y = 20;

        if (logo != null) {
            g.drawImage(logo, 65, y, 93, 20, null);
            y += 40;
        }

        g2d.setFont(plainFont);
        if (cont == 1) {
            g2d.drawString("******** TICKET ********", 40, y);
        } else {
            g2d.drawString("******** COPIA ********", 40, y);
        }
        y += 15;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        g2d.drawString(pedido.getFecha_pedido().format(formatter), 10, y);
        y += 15;
        g2d.drawString("Para: " + pedido.getCliente_nombre().trim(), 10, y);
        y += 15;
        g2d.drawString("Dir: " + pedido.getDireccion().trim(), 10, y);
        y += 15;
        g2d.setFont(boldFont);
        g2d.drawString("--------------------------------------", 20, y);
        y += 15;
        g2d.setFont(plainFont);

        for (DetallePedido detalle : detallesPedido) {
            idDetallePedido = detalle.getId();
            g2d.setFont(boldFont);
            String cantidadTexto = detalle.getCantidad() + "x ";
            g2d.drawString(cantidadTexto, 10, y);
            int textWidth = g2d.getFontMetrics().stringWidth(cantidadTexto);

            g2d.setFont(plainFont);

            if (detalle.getTipoHamburguesa() > 0) {
                HamburguesaTipo hamburguesaTipo = hamburguesaService.getHamburguesaTipoByID(detalle.getTipoHamburguesa());

                if (hamburguesaTipo != null && hamburguesaTipo.getHamburguesa_id() > 0) {
                    Hamburguesa hamburguesa = hamburguesaService.getHamburguesaByID(hamburguesaTipo.getHamburguesa_id());
                    TipoHamburguesa tipoHamburguesa = hamburguesaService.getTipoHamburguesa(hamburguesaTipo.getTipo_id());

                    if (hamburguesa != null && hamburguesa.getCodigo() != null && !hamburguesa.getCodigo().isEmpty()) {
                        g2d.setFont(boldFont);
                        String item = hamburguesa.getCodigo().toUpperCase().trim() + " (" + tipoHamburguesa.getTipo() + ")";
                        g2d.drawString(item, 10 + textWidth, y);
                        g2d.setFont(plainFont);

                        String precio = "$" + Math.round(detalle.getCantidad() * hamburguesaTipo.getPrecios());
                        int precioX = 190 - g2d.getFontMetrics().stringWidth(precio);
                        g2d.drawString(precio, precioX, y);
                    }
                }

                y += g2d.getFontMetrics().getHeight();
            }

            if (detalle.getObservacion() != null && !detalle.getObservacion().isEmpty()) {
                String observacion = detalle.getObservacion().trim();

                int maxLineWidth = 180;
                FontMetrics fontMetrics = g2d.getFontMetrics(boldFont);
                String[] lines = splitTextIntoLines(observacion, fontMetrics, maxLineWidth);

                for (String line : lines) {
                    g2d.drawString("Obs: " + line, 20, y);
                    y += 15;
                }
            }

            List<ToppingPedido> toppingPedidos = toppingPedidoService.getToppingPedido(detalle.getId());
            Map<String, Integer> toppingsObservados = new HashMap<>();

            if (detalle.getObservacion() != null && !detalle.getObservacion().isEmpty()) {
                Pattern pattern = Pattern.compile("(\\d+)x(\\w+)");
                Matcher matcher = pattern.matcher(detalle.getObservacion());

                while (matcher.find()) {
                    int cantidadTop = Integer.parseInt(matcher.group(1));
                    String toppingNombre = matcher.group(2).toLowerCase();
                    toppingsObservados.put(toppingNombre, cantidadTop);
                }
            }
            for (ToppingPedido topping : toppingPedidos) {
                double precioTopping = 0.0;
                String precioToppingStr = "";
                if (topping.getIdTopping() != 9) {
                    Topping top = toppingService.getTopping(topping.getIdTopping(), topping.isAgregado());
                    if (top != null) {
                        int cantidadFinal = toppingsObservados.getOrDefault(top.getNombre().toLowerCase(), 1);
                        if (top.getPrecio() != null) {
                            precioTopping = top.getPrecio() * cantidadFinal;
                        }
                        if (topping.isAgregado()) {
                            g2d.drawString("Extra: " + cantidadFinal + "x " + top.getNombre().trim(), 20, y);
                        } else {
                            g2d.drawString("Sin: " + top.getNombre().trim(), 20, y);
                        }
                    }
                } else {
                    List<ToppingPedido> toppingSalsa = toppingPedidoService.getToppingPedido(idDetallePedido);
                    for (ToppingPedido salsatop : toppingSalsa) {
                        if (salsatop.getIdTopping() == 9) {
                            g2d.drawString("Sin: Salsa", 20, y);
                        }
                    }
                }

                if (precioTopping > 0) {
                    precioToppingStr = "$" + Math.round(precioTopping);
                } else {
                    precioToppingStr = "";
                }

                g2d.drawString(precioToppingStr, 190 - g2d.getFontMetrics().stringWidth(precioToppingStr), y);
                y += 15;
            }

            if (detalle.getExtra_id() > 0) {
                Extra extra = extraService.getExtra(detalle.getExtra_id());
                String extraDescripcion = " ";

                if (extra.getId_tipo() == 2) {
                    extraDescripcion += "Salsa " + extra.getNombre();
                    totalExtra += detalle.getCantidad() * extra.getPrecio();
                } else if (extra.getId_tipo() == 3) {
                    extraDescripcion += extra.getNombre();
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

        g2d.setFont(boldFont);
        g2d.drawString("--------------------------------------", 20, y);
        y += 15;

        g2d.setFont(plainFont);
        String envio = "$" + Math.round(pedido.getPrecio_envio());
        g2d.drawString("Envío: " + envio, 150 - g2d.getFontMetrics().stringWidth(envio), y);
        y += 15;

        g2d.setFont(boldFont);
        String total = "$" + Math.round(pedido.getPrecio_total() + pedido.getPrecio_envio());
        g2d.drawString("TOTAL: " + total, 150 - g2d.getFontMetrics().stringWidth(total), y);

        cont++;
        return PAGE_EXISTS;
    }

    public String[] splitTextIntoLines(String text, FontMetrics fontMetrics, int maxLineWidth) {
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        String[] words = text.split(" ");

        for (String word : words) {
            if (fontMetrics.stringWidth(currentLine.toString() + word) > maxLineWidth) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word + " ");
            } else {
                currentLine.append(word).append(" ");
            }
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString().trim());
        }

        return lines.toArray(new String[0]);
    }
}
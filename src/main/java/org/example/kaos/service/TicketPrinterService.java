package org.example.kaos.service;

import org.example.kaos.entity.*;

import javax.print.PrintService;
import javax.swing.*;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TicketPrinterService::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Ticket Printer Service");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextField idPedidoField = new JTextField(10);
        panel.add(new JLabel("ID del Pedido:"));
        panel.add(idPedidoField);

        JButton imprimirButton = new JButton("Imprimir Ticket");
        imprimirButton.addActionListener(e -> {
            int idPedido = Integer.parseInt(idPedidoField.getText());
            TicketPrinterService ticketPrinter = new TicketPrinterService();
            ticketPrinter.imprimir(idPedido);
        });
        panel.add(imprimirButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    public void imprimir(int idPedido) {
        cargarPedido(idPedido);
        PrinterJob job = PrinterJob.getPrinterJob();
        PrintService[] printers = PrinterJob.lookupPrintServices();
        for (PrintService printer : printers) {
            if (printer.getName().equals("Microsoft Print to PDF")) {
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
        try {
            job.print();
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }

    private void cargarPedido(int idPedido) {
        pedido = pedidoService.getPedidoId(idPedido);
        detallesPedido = detalleService.getDetallePedidoList(idPedido);
    }

    private String generarFormato(Pedido pedido) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        sb.append("Fecha: ").append(pedido.getFecha_pedido().format(formatter)).append("\n");
        sb.append("\n");
        sb.append("Cliente: ").append(pedido.getCliente_nombre().trim()).append("\n");
        sb.append("\n");
        sb.append("Dirección: ").append(pedido.getDireccion().trim()).append("\n");
        sb.append("\n");
        sb.append("-----------------------------------\n");
        for (DetallePedido detalle : detallesPedido) {
            if (detalle.getTipoHamburguesa() > 0) {
                HamburguesaTipo hamburguesaTipo = hamburguesaService.getHamburguesaTipoByID(detalle.getTipoHamburguesa());
                Hamburguesa hamburguesa = hamburguesaService.getHamburguesaByID(hamburguesaTipo.getHamburguesa_id());
                TipoHamburguesa tipoHamburguesa = hamburguesaService.getTipoHamburguesa(hamburguesaTipo.getTipo_id());

                int precio = (int) Math.round(detalle.getCantidad() * hamburguesaTipo.getPrecios());
                sb.append(detalle.getCantidad()).append(" x ").append(hamburguesa.getCodigo().toUpperCase().trim()).append(" (").append(tipoHamburguesa.getTipo()).append(") ").append(" - $").append(precio).append("\n");
                sb.append("\n");
                if (detalle.getObservacion() != null && !detalle.getObservacion().isEmpty()) {
                    sb.append("Observación: ").append(detalle.getObservacion().trim()).append("\n");
                }
                List<ToppingPedido> toppingPedido = toppingPedidoService.getToppingPedido(detalle.getId());
                if (toppingPedido != null && !toppingPedido.isEmpty()) {
                    for (ToppingPedido topping : toppingPedido) {
                        Topping top = toppingPedidoService.getTopping(topping.getIdTopping(), Boolean.valueOf(topping.isAgregado()));
                        if (top != null && topping.isAgregado()) {
                            sb.append("Extra: ").append(top.getNombre().trim()).append("\n");
                        } else {
                            sb.append("Sin: ").append(top.getNombre().trim()).append("\n");
                        }
                    }
                }
                sb.append("\n");
            } else {
                if (detalle.getExtra_id() > 0) {
                    Extra extra = extraService.getExtra(detalle.getExtra_id());
                    if(extra.getId_tipo() == 2){
                        sb.append(detalle.getCantidad()).append(" x Salsa ").append(extra.getNombre()).append(" - $").append(detalle.getCantidad() * extra.getPrecio()).append("\n");
                    } else if(extra.getId_tipo() == 3){
                        sb.append(detalle.getCantidad()).append(" x PROMO ").append(extra.getNombre()).append(" - $").append(detalle.getCantidad() * extra.getPrecio()).append("\n");
                    } else {
                        sb.append(detalle.getCantidad()).append(" x ").append(extra.getNombre()).append(" - $").append(detalle.getCantidad() * extra.getPrecio()).append("\n");
                    }
                    sb.append("\n");
                }
            }
        }
        sb.append("-----------------------------------\n");
        sb.append("Envio: $").append(pedido.getPrecio_envio());
        sb.append("\n");
        int precio = (int) Math.round(pedido.getPrecio_total());
        sb.append("Total: $").append(precio).append("\n");
        sb.append("***********************************\n");
        return sb.toString();
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        Font titleFont = new Font("Monospaced", Font.BOLD, 8);
        Font infoFont = new Font("Monospaced", Font.PLAIN, 8);
        g2d.setFont(titleFont);
        int y = 20;
        if (logo != null) {
            int logoWidth = 100;
            int logoHeight = 20;
            g.drawImage(logo, 2, y, logoWidth, logoHeight, null);
            y += logoHeight + 10;
        }
        g2d.drawString("******** TICKET ********", 2, y);
        y += 10;
        String[] lines = generarFormato(this.pedido).split("\n");
        for (String line : lines) {
            if (line.length() > 32) {
                line = line.substring(0, 32);
            }
            if (line.startsWith("Cliente:") || line.startsWith("Dirección:") || line.startsWith("Fecha:") || line.startsWith("Envio:") || line.startsWith("Total:")) {
                g2d.setFont(infoFont);
            }else {
                g2d.setFont(infoFont);
            }
            g2d.drawString(line, 2, y);
            y += 10;
        }
        return PAGE_EXISTS;
    }
}
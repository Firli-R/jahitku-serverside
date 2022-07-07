/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.id.jahitku.serverside.service;

import co.id.jahitku.serverside.model.JenisJahitanOrder;
import co.id.jahitku.serverside.model.Order;
import co.id.jahitku.serverside.model.Pembayaran;
import co.id.jahitku.serverside.model.User;
import co.id.jahitku.serverside.model.dto.EmailData;
import co.id.jahitku.serverside.model.dto.OrderData;
import co.id.jahitku.serverside.model.dto.OrderDataAdmin;
import co.id.jahitku.serverside.model.dto.OrderJenisJahitan;
import co.id.jahitku.serverside.model.dto.ResponseData;
import co.id.jahitku.serverside.repository.OrderRepository;
import co.id.jahitku.serverside.repository.PembayaranRepository;
import co.id.jahitku.serverside.repository.UserRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author DELL ALIENWARE
 */
@Service
@AllArgsConstructor
public class OrderService {

    private OrderRepository orderRepository;
    private ModelMapper modelMapper;
    private UserRepository userRepository;
    private JenisJahitanOrderService jenisJahitanOrderService;
    private PembayaranRepository pembayaranRepository;
    private EmailService emailService;

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public List<Order> getOrderByUsername(String username) {
        return orderRepository.findByUsername(username);
    }

    public List<Order> getRecentOrder() {
        return orderRepository.findAllOrderByTanggalMasukDesc();
    }

    public List<Order> getUnpaid() {
        return orderRepository.findByStatusPembayaran(0L);
    }

    public HashMap<String, String> getCountStatus() {
        String count = orderRepository.countByStatusPesanan();
        String[] countSplit = count.split(",");
        HashMap<String, String> countStatus = new HashMap<>();
        countStatus.put("Dalam_Proses", countSplit[3]);
        countStatus.put("Belum_Dikerjakan", countSplit[2]);
        countStatus.put("Butuh_Approval", countSplit[0]);
        countStatus.put("Belum_Dibayar", orderRepository.countUnpaid());
        countStatus.put("Selesai", countSplit[4]);
        countStatus.put("Batal", countSplit[1]);
        return countStatus;
    }

    public Order getById(Long id) {
        return orderRepository.findById(id).get();
    }

    public ResponseData deleteOrder(Long id) {
        try {
            Order order = getById(id);
            orderRepository.delete(order);
            return new ResponseData("success", "data berhasil dihapus");
        } catch (Exception e) {
            return new ResponseData("fail", e.getMessage());
        }

    }

    public Order createOrder(String username) {
        User user = userRepository.findByUsername(username);

        Order order = new Order();
        order.setId(null);
        order.setNoOrder(generator());
        order.setUser(user);
        order.setTanggalMasuk(LocalDateTime.now());

        return orderRepository.save(order);
    }

    public Order createOrderCustomer(String username, Set<OrderJenisJahitan> orderJenisJahitan) {

        Order order = createOrder(username);

        Pembayaran pembayaran = new Pembayaran();
        pembayaran.setOrder(order);

        Set<JenisJahitanOrder> jenisJahitanOrderSet = new HashSet<JenisJahitanOrder>();
        orderJenisJahitan.forEach((jahitan)
                -> jenisJahitanOrderSet.add(jenisJahitanOrderService.create(order, jahitan)));

        order.setPembayaran(pembayaran);
        order.setJenisJahitanOrder(jenisJahitanOrderSet);

        return orderRepository.save(order);
    }

    public Order createOrderAdmin(OrderDataAdmin orderDataAdmin) {
        Order order = createOrder(orderDataAdmin.getUsername());
        order.setTanggalSelesai(orderDataAdmin.getTanggalSelesai());
        order.setStatusPesanan(orderDataAdmin.getStatusPesanan());

        order = setJenisJahitanAndProgress(order, orderDataAdmin);

        Pembayaran pembayaran = modelMapper.map(orderDataAdmin, Pembayaran.class);
        pembayaran.setOrder(order);

        order.setPembayaran(pembayaran);
        return orderRepository.save(order);
    }

    public Order update(Long orderId, OrderData orderDataAdmin) {
        Order order = orderRepository.findById(orderId).get();
        order.setId(orderId);
        order.setTanggalMasuk(orderDataAdmin.getTanggalMasuk());
        order.setTanggalSelesai(orderDataAdmin.getTanggalSelesai());
        order.setStatusPesanan(orderDataAdmin.getStatusPesanan());
        order.setLokasiBarang(orderDataAdmin.getLokasiBarang());
        order.setUser(userRepository.findByUsername(orderDataAdmin.getUsername()));
        Set<JenisJahitanOrder> jenisJahitanOrderSet = new HashSet<JenisJahitanOrder>();
        orderDataAdmin.getJenisJahitanOrder().forEach((data) -> {
            jenisJahitanOrderSet.add(jenisJahitanOrderService.updateJenisJahitanOrder(data));
        });
        Double cp = countProgress(orderId, jenisJahitanOrderSet.size());
        if(cp >= 100.0){
            order.setProgress(100.0);
        }else{
            order.setProgress(cp);
        }
        

        order.setJenisJahitanOrder(jenisJahitanOrderSet);
        Pembayaran pembayaran = pembayaranRepository.findById(orderId).get();

        order.setPembayaran(pembayaran);
        order.getPembayaran().setTotalBiaya(orderDataAdmin.getTotalBiaya());

        String linkAlamat = "https://maps.app.goo.gl/G2TPikNikbxVPw3n6";
        String link = "http://localhost:8083/api/order/export-pdf/" + order.getNoOrder();

        if (orderDataAdmin.getStatusPesanan() == Order.Status.BELUM_DIKERJAKAN) {
            EmailData emailData = new EmailData(order.getUser().getEmail(), order.getNoOrder(), "Pemberitahuan Pesanan Jahitku", buildEmailOrder(order.getUser().getNama(), linkAlamat, order.getNoOrder()), "no atch");
            emailService.sendVerification(emailData);
        }

        if (orderDataAdmin.getStatusPesanan() == Order.Status.DALAM_PROSES) {
            EmailData emailData = new EmailData(order.getUser().getEmail(), order.getNoOrder(), "Pemberitahuan Pesanan Jahitku", buildEmailProcess(order.getUser().getNama(), "http://localhost:8085/homepage", linkAlamat, order.getProgress()), "no atch");
            emailService.sendVerification(emailData);
        }
        if (orderDataAdmin.getStatusPesanan() == Order.Status.SELESAI) {
            EmailData emailData = new EmailData(order.getUser().getEmail(), order.getNoOrder(), "Pemberitahuan Pesanan Jahitku", buildEmailSuccess(order.getUser().getNama(), link, order.getProgress()), "no atch");
            if (order.getStatusOrder() == false) {
                emailService.sendVerification(emailData);
                order.setStatusOrder(true);
            }
        }

        return orderRepository.save(order);
    }

    private Order setJenisJahitanAndProgress(Order order, OrderDataAdmin orderDataAdmin) {
        Set<JenisJahitanOrder> jenisJahitanOrderSet = new HashSet<JenisJahitanOrder>();
        orderDataAdmin.getJenisJahitanOrder().forEach((jahitan)
                -> jenisJahitanOrderSet.add(jenisJahitanOrderService.create(order, jahitan)));
        order.setJenisJahitanOrder(jenisJahitanOrderSet);

        order.setProgress(countProgress(order.getId(), jenisJahitanOrderSet.size()));

        return order;
    }

    private String generator() {
        int year = LocalDateTime.now().getYear();
        String random = UUID.randomUUID().toString().substring(0, 4);
        String noOrder = "JK-" + year + "-" + random;
        return noOrder;
    }

    public void exportPdf(HttpServletResponse response, String noOrder) throws IOException {
        Order order = orderRepository.findByNoOrder(noOrder).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Order tidak ditemukan")
        );

        Document document = new Document(PageSize.A6);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font fontTitle = FontFactory.getFont(FontFactory.TIMES_BOLD);
        fontTitle.setSize(18);

        Font fontParagraph = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        fontParagraph.setSize(12);

        Paragraph title = new Paragraph("Nota Pemesanan Jahitku", fontTitle);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        Paragraph space1 = new Paragraph(" === ", fontParagraph);
        space1.setAlignment(Paragraph.ALIGN_CENTER);
        Paragraph space2 = new Paragraph(" === ", fontParagraph);
        space2.setAlignment(Paragraph.ALIGN_CENTER);
        Paragraph space3 = new Paragraph(" === ", fontParagraph);
        space3.setAlignment(Paragraph.ALIGN_CENTER);

        Paragraph p1 = new Paragraph("No Order:" + noOrder, fontParagraph);
        p1.setAlignment(Paragraph.ALIGN_CENTER);
        Paragraph p2 = new Paragraph("Tanggal Masuk:" + order.getTanggalMasuk(), fontParagraph);
        p2.setAlignment(Paragraph.ALIGN_CENTER);
        Paragraph p3 = new Paragraph("Tanggal Selesai:" + order.getTanggalSelesai(), fontParagraph);
        p3.setAlignment(Paragraph.ALIGN_CENTER);
        Paragraph p4 = new Paragraph("Nama:" + order.getUser().getNama(), fontParagraph);
        p4.setAlignment(Paragraph.ALIGN_CENTER);

        List<String> jenisJahitan = new ArrayList<>();
        List<String> listKeterangan = new ArrayList<>();

        order.getJenisJahitanOrder().forEach(data -> {
            jenisJahitan.add(data.getJenisJahitan().getNama());
            listKeterangan.add(data.getKeterangan());
        });
        Paragraph p5 = new Paragraph("Jenis Jahitan:" + jenisJahitan, fontParagraph);
        p5.setAlignment(Paragraph.ALIGN_CENTER);

        Paragraph p6 = new Paragraph("Keterangan:" + listKeterangan, fontParagraph);
        p6.setAlignment(Paragraph.ALIGN_CENTER);

        Paragraph p7 = new Paragraph("Total Biaya: Rp. " + order.getPembayaran().getTotalBiaya().toString(), fontParagraph);
        p7.setAlignment(Paragraph.ALIGN_CENTER);
        Paragraph p8 = new Paragraph("Status Pembayaran: " + order.getPembayaran().getStatusPembayaran(), fontParagraph);
        p8.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(title);
        document.add(space1);
        document.add(space2);
        document.add(space3);
        document.add(p1);
        document.add(p2);
        document.add(p3);
        document.add(p4);
        document.add(p5);
        document.add(p6);
        document.add(p7);
        document.add(p8);

        document.close();
    }

    private Double countProgress(Long OrderId, int totalJenisJahitan) {
        Long jenisJahitanSelesai = orderRepository.countStatusJahitanByOrderId(OrderId);
        Double progress = (jenisJahitanSelesai * 100.0) / totalJenisJahitan;
        String nilai = String.format("%.2f", progress);
        double nilaiDouble = Double.parseDouble(nilai);
        return nilaiDouble;
    }

    private String buildEmailOrder(String name, String link, String noOrder) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n"
                + "\n"
                + "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n"
                + "\n"
                + "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n"
                + "    <tbody><tr>\n"
                + "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n"
                + "        \n"
                + "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n"
                + "          <tbody><tr>\n"
                + "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n"
                + "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
                + "                  <tbody><tr>\n"
                + "                    <td style=\"padding-left:10px\">\n"
                + "                  \n"
                + "                    </td>\n"
                + "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n"
                + "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Pemberitahuan Pesanan Jahitku</span>\n"
                + "                    </td>\n"
                + "                  </tr>\n"
                + "                </tbody></table>\n"
                + "              </a>\n"
                + "            </td>\n"
                + "          </tr>\n"
                + "        </tbody></table>\n"
                + "        \n"
                + "      </td>\n"
                + "    </tr>\n"
                + "  </tbody></table>\n"
                + "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
                + "    <tbody><tr>\n"
                + "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n"
                + "      <td>\n"
                + "        \n"
                + "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
                + "                  <tbody><tr>\n"
                + "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n"
                + "                  </tr>\n"
                + "                </tbody></table>\n"
                + "        \n"
                + "      </td>\n"
                + "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n"
                + "    </tr>\n"
                + "  </tbody></table>\n"
                + "\n"
                + "\n"
                + "\n"
                + "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
                + "    <tbody><tr>\n"
                + "      <td height=\"30\"><br></td>\n"
                + "    </tr>\n"
                + "    <tr>\n"
                + "      <td width=\"10\" valign=\"middle\"><br></td>\n"
                + "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n"
                + "        \n"
                + "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Terima Kasih sudah memesan di Jahitku, Selesaikan Pembayaran yang tersedia dengan no.order " + noOrder + " atau silahkan kunjungi toko untuk mengirimkan barang yang tertera pada link dibawah ini: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Alamat toko</a> </p></blockquote>\n happy a nice day <p>See you soon</p>"
                + "        \n"
                + "      </td>\n"
                + "      <td width=\"10\" valign=\"middle\"><br></td>\n"
                + "    </tr>\n"
                + "    <tr>\n"
                + "      <td height=\"30\"><br></td>\n"
                + "    </tr>\n"
                + "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n"
                + "\n"
                + "</div></div>";
    }

    private String buildEmailProcess(String name, String link, String linkMaps, Double progress) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n"
                + "\n"
                + "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n"
                + "\n"
                + "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n"
                + "    <tbody><tr>\n"
                + "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n"
                + "        \n"
                + "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n"
                + "          <tbody><tr>\n"
                + "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n"
                + "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
                + "                  <tbody><tr>\n"
                + "                    <td style=\"padding-left:10px\">\n"
                + "                  \n"
                + "                    </td>\n"
                + "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n"
                + "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Pemberitahuan Pesanan Jahitku</span>\n"
                + "                    </td>\n"
                + "                  </tr>\n"
                + "                </tbody></table>\n"
                + "              </a>\n"
                + "            </td>\n"
                + "          </tr>\n"
                + "        </tbody></table>\n"
                + "        \n"
                + "      </td>\n"
                + "    </tr>\n"
                + "  </tbody></table>\n"
                + "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
                + "    <tbody><tr>\n"
                + "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n"
                + "      <td>\n"
                + "        \n"
                + "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
                + "                  <tbody><tr>\n"
                + "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n"
                + "                  </tr>\n"
                + "                </tbody></table>\n"
                + "        \n"
                + "      </td>\n"
                + "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n"
                + "    </tr>\n"
                + "  </tbody></table>\n"
                + "\n"
                + "\n"
                + "\n"
                + "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
                + "    <tbody><tr>\n"
                + "      <td height=\"30\"><br></td>\n"
                + "    </tr>\n"
                + "    <tr>\n"
                + "      <td width=\"10\" valign=\"middle\"><br></td>\n"
                + "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n"
                + "        \n"
                + "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Terima Kasih sudah memesan, Pesanan anda Sedang dalam proses Pengerjaan dengan progress " + progress + " %. Silahkan kunjungi Website kami yang tertera pada link dibawah ini: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"><a href=\"" + link + "\">Lihat Pesanan kamu di website</a> </p></blockquote>\n happy a nice day <p>See you soon</p>"
                + "        \n"
                + "      </td>\n"
                + "      <td width=\"10\" valign=\"middle\"><br></td>\n"
                + "    </tr>\n"
                + "    <tr>\n"
                + "      <td height=\"30\"><br></td>\n"
                + "    </tr>\n"
                + "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n"
                + "\n"
                + "</div></div>";
    }

    private String buildEmailSuccess(String name, String link, Double progress) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n"
                + "\n"
                + "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n"
                + "\n"
                + "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n"
                + "    <tbody><tr>\n"
                + "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n"
                + "        \n"
                + "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n"
                + "          <tbody><tr>\n"
                + "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n"
                + "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
                + "                  <tbody><tr>\n"
                + "                    <td style=\"padding-left:10px\">\n"
                + "                  \n"
                + "                    </td>\n"
                + "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n"
                + "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Pemberitahuan Pesanan Jahitku</span>\n"
                + "                    </td>\n"
                + "                  </tr>\n"
                + "                </tbody></table>\n"
                + "              </a>\n"
                + "            </td>\n"
                + "          </tr>\n"
                + "        </tbody></table>\n"
                + "        \n"
                + "      </td>\n"
                + "    </tr>\n"
                + "  </tbody></table>\n"
                + "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
                + "    <tbody><tr>\n"
                + "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n"
                + "      <td>\n"
                + "        \n"
                + "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
                + "                  <tbody><tr>\n"
                + "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n"
                + "                  </tr>\n"
                + "                </tbody></table>\n"
                + "        \n"
                + "      </td>\n"
                + "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n"
                + "    </tr>\n"
                + "  </tbody></table>\n"
                + "\n"
                + "\n"
                + "\n"
                + "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
                + "    <tbody><tr>\n"
                + "      <td height=\"30\"><br></td>\n"
                + "    </tr>\n"
                + "    <tr>\n"
                + "      <td width=\"10\" valign=\"middle\"><br></td>\n"
                + "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n"
                + "        \n"
                + "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Terima Kasih sudah memesan, Pesanan anda Sudah Selesai Dikerjakan dengan progress" + progress + " %. Silahkan kunjungi gerai atau toko untuk melakukan pengambilan pesananan anda.Berikut adalah nota anda: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">unduh disini</a> </p></blockquote>\n Jangan Lupa membawa nota anda. <p>See you soon</p>"
                + "        \n"
                + "      </td>\n"
                + "      <td width=\"10\" valign=\"middle\"><br></td>\n"
                + "    </tr>\n"
                + "    <tr>\n"
                + "      <td height=\"30\"><br></td>\n"
                + "    </tr>\n"
                + "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n"
                + "\n"
                + "</div></div>";
    }
}

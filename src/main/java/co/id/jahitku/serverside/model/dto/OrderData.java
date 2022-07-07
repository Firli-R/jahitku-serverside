/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.model.dto;

import co.id.jahitku.serverside.model.Order;
import co.id.jahitku.serverside.model.Pembayaran;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;

/**
 *
 * @author Firli
 */
@Data
public class OrderData {
    private LocalDateTime tanggalMasuk;
    private LocalDateTime tanggalSelesai;
    private String lokasiBarang;
    private Order.Status statusPesanan;
    private Boolean statusOrder;
    private String username;
    private String nama;
    private Pembayaran.StatusBayar statusPembayaran;
    private Long totalBiaya;
    Set<JenisJahitanOrderData> jenisJahitanOrder;
}

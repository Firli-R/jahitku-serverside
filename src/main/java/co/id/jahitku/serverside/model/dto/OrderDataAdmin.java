/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.id.jahitku.serverside.model.dto;

import co.id.jahitku.serverside.model.Order.Status;
import co.id.jahitku.serverside.model.Pembayaran.StatusBayar;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;

/**
 *
 * @author DELL ALIENWARE
 */
@Data
public class OrderDataAdmin {

    private LocalDateTime tanggalMasuk;
    private LocalDateTime tanggalSelesai;
    private String lokasiBarang;
    private Status statusPesanan;
    private Boolean statusOrder;
    private String username;
    private String nama;

    private StatusBayar statusPembayaran;
    private Long totalBiaya;

    Set<OrderJenisJahitan> jenisJahitanOrder;
}

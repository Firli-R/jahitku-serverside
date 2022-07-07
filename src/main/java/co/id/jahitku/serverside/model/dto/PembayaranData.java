/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.model.dto;

import co.id.jahitku.serverside.model.Pembayaran.StatusBayar;
import lombok.Data;

/**
 *
 * @author LENOVO
 */
@Data
public class PembayaranData {
    private String noOrder;
    private Long totalBiaya;
    private StatusBayar statusPembayaran;
}

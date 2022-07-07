/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.model.dto;

import co.id.jahitku.serverside.model.Pembayaran.StatusBayar;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Firli
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JenisOrderStatusBayarData {

    private Long jenisJahitanId;

    private Long orderId;

    private StatusBayar statusBayar;
}

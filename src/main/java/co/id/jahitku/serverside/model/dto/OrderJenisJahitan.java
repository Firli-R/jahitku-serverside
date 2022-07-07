/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.id.jahitku.serverside.model.dto;

import lombok.Data;

/**
 *
 * @author DELL ALIENWARE
 */
@Data
public class OrderJenisJahitan {

    private Long jenisJahitanId;
    private Long kuantitas;
    private String keterangan;
    private Long harga;
    private boolean status;
}

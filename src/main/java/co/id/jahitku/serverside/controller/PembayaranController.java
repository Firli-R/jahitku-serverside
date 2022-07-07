/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.controller;

import co.id.jahitku.serverside.model.JenisJahitanOrder;
import co.id.jahitku.serverside.model.Pembayaran;
import co.id.jahitku.serverside.model.dto.JenisOrderStatusBayarData;
import co.id.jahitku.serverside.model.dto.PembayaranData;
import co.id.jahitku.serverside.service.JenisJahitanOrderService;
import co.id.jahitku.serverside.service.PembayaranService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author LENOVO
 */
@RestController
@RequestMapping("/pembayaran")
@AllArgsConstructor
public class PembayaranController {
    private PembayaranService pembayaranService;
    private JenisJahitanOrderService jenisJahitanOrder;
    
    @PostMapping
    public Pembayaran getPembayaran(@RequestBody PembayaranData pembayaranData) {
        return pembayaranService.create(pembayaranData);
    }
    
    @PutMapping("/update-status-bayar")
    public ResponseEntity<JenisJahitanOrder> updateStatusBayar(@RequestBody JenisOrderStatusBayarData josbd){
        return new ResponseEntity(jenisJahitanOrder.updateStatusBayar(josbd), HttpStatus.OK);
    }
    
}

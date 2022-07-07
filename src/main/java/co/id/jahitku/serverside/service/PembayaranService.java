/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.service;

import co.id.jahitku.serverside.model.Order;
import co.id.jahitku.serverside.model.Pembayaran;
import co.id.jahitku.serverside.model.dto.PembayaranData;
import co.id.jahitku.serverside.repository.OrderRepository;
import co.id.jahitku.serverside.repository.PembayaranRepository;
import com.midtrans.Midtrans;
import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author LENOVO
 */
@Service
@AllArgsConstructor
public class PembayaranService {

    private PembayaranRepository pembayaranRepository;
    private OrderRepository orderRepository;
    
    public Pembayaran getById(Long id) {
        return pembayaranRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tidak ditemukan")
        );
    }
    
    public Pembayaran create (PembayaranData pembayaranData) {
        Order order = orderRepository.findByNoOrder(pembayaranData.getNoOrder()).get();
        Pembayaran pembayaran = new Pembayaran();
        pembayaran.setOrder(order);
        pembayaran.setId(order.getId());
        pembayaran.setTotalBiaya(pembayaranData.getTotalBiaya());
        pembayaran.setStatusPembayaran(pembayaranData.getStatusPembayaran());
        return pembayaranRepository.save(pembayaran);
    }
    
}

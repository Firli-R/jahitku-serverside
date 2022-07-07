/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.id.jahitku.serverside.service;

import co.id.jahitku.serverside.model.JenisJahitan;
import co.id.jahitku.serverside.model.JenisJahitanOrder;
import co.id.jahitku.serverside.model.JenisJahitanOrderKey;
import co.id.jahitku.serverside.model.Order;
import co.id.jahitku.serverside.model.Pembayaran;
import co.id.jahitku.serverside.model.dto.JenisJahitanOrderData;
import co.id.jahitku.serverside.model.dto.JenisOrderStatusBayarData;
import co.id.jahitku.serverside.model.dto.OrderJenisJahitan;
import co.id.jahitku.serverside.model.dto.ResponseData;
import co.id.jahitku.serverside.repository.JenisJahitanOrderRepository;
import co.id.jahitku.serverside.repository.JenisJahitanRepository;
import co.id.jahitku.serverside.repository.OrderRepository;
import java.util.List;
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
public class JenisJahitanOrderService {

    private JenisJahitanOrderRepository jenisJahitanOrderRepository;
    private OrderRepository orderRepository;
    private JenisJahitanRepository jenisJahitanRepository;
    private JenisJahitanService jenisJahitanService;
    private ModelMapper modelMapper;

    public JenisJahitanOrder create(Order order, OrderJenisJahitan orderJenisJahitan) {
        JenisJahitan jenisJahitan = jenisJahitanService.getById(orderJenisJahitan.getJenisJahitanId());
        JenisJahitanOrder jahitanOrder = modelMapper.map(orderJenisJahitan, JenisJahitanOrder.class);
        JenisJahitanOrderKey orderKey = new JenisJahitanOrderKey(orderJenisJahitan.getJenisJahitanId(), order.getId());
        jahitanOrder.setIdJenisOrder(orderKey);

        jahitanOrder.setOrder(order);
        jahitanOrder.setJenisJahitan(jenisJahitan);
        return jenisJahitanOrderRepository.save(jahitanOrder);
    }

    public List<JenisJahitanOrder> getAll(Long id) {
        return jenisJahitanOrderRepository.findByOrderId(id).orElseThrow(() -> {
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Jenis Order tidak ditemukan");
        });
    }

    public JenisJahitanOrder getJenisJahitanOrder(Long idJenis, Long idOrder) {
        JenisJahitanOrderKey orderKey = new JenisJahitanOrderKey(idJenis, idOrder);
        return jenisJahitanOrderRepository.findByIdJenisOrder(orderKey);
    }

    public JenisJahitanOrder updateJenisJahitanOrder(JenisJahitanOrderData jenisJahitanOrderData) {
        JenisJahitanOrder jenisJahitanOrder = getJenisJahitanOrder(jenisJahitanOrderData.getJenisJahitanId(), jenisJahitanOrderData.getOrderId());
        if (jenisJahitanOrder != null) {
            jenisJahitanOrder.setKuantitas(jenisJahitanOrderData.getKuantitas());
            jenisJahitanOrder.setHarga(jenisJahitanOrderData.getHarga());
            jenisJahitanOrder.setKeterangan(jenisJahitanOrderData.getKeterangan());
            jenisJahitanOrder.setStatus(jenisJahitanOrderData.isStatus());
            return jenisJahitanOrderRepository.save(jenisJahitanOrder);
        } else {
            JenisJahitanOrderKey orderKey = new JenisJahitanOrderKey(jenisJahitanOrderData.getJenisJahitanId(), jenisJahitanOrderData.getOrderId());
            JenisJahitan jenisJahitan = jenisJahitanRepository.findById(jenisJahitanOrderData.getJenisJahitanId()).get();
            Order order = orderRepository.findById(jenisJahitanOrderData.getOrderId()).get();
            JenisJahitanOrder jJo = new JenisJahitanOrder(orderKey, jenisJahitan, order,
                    jenisJahitanOrderData.getKuantitas(), jenisJahitanOrderData.getHarga(),
                    jenisJahitanOrderData.getKeterangan(), jenisJahitanOrderData.isStatus());
            return jenisJahitanOrderRepository.save(jJo);
        }
    }

    public ResponseData deleteJenisJahitanOrder(Long idJenis, Long idOrder) {
        try {
            JenisJahitanOrderKey orderKey = new JenisJahitanOrderKey(idJenis, idOrder);
            jenisJahitanOrderRepository.deleteById(orderKey);
            return new ResponseData("success", "berhasil delete");
        } catch (Exception e) {
            return new ResponseData("error", e.getMessage());
        }

    }

    public Order updateStatusBayar(JenisOrderStatusBayarData statusBayar) {
        Order order = orderRepository.findById(statusBayar.getOrderId()).get();
        order.getPembayaran().setStatusPembayaran(statusBayar.getStatusBayar());
        order.getPembayaran().setTotalBiaya(statusBayar.getJenisJahitanId());
        if (statusBayar.getStatusBayar() == Pembayaran.StatusBayar.BATAL) {
            order.setStatusPesanan(Order.Status.BATAL);
        }
        return orderRepository.save(order);
    }
}

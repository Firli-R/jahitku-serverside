/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.id.jahitku.serverside.controller;

import co.id.jahitku.serverside.model.JenisJahitanOrder;
import co.id.jahitku.serverside.model.Order;
import co.id.jahitku.serverside.model.dto.JenisJahitanOrderData;
import co.id.jahitku.serverside.model.dto.OrderData;
import co.id.jahitku.serverside.model.dto.OrderDataAdmin;
import co.id.jahitku.serverside.model.dto.OrderJenisJahitan;
import co.id.jahitku.serverside.model.dto.ResponseData;
import co.id.jahitku.serverside.service.JenisJahitanOrderService;
import co.id.jahitku.serverside.service.OrderService;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author DELL ALIENWARE
 */
@RestController
@RequestMapping("/order")
@AllArgsConstructor
public class OrderController {

    private OrderService orderService;
    private JenisJahitanOrderService jenisJahitanOrderService;

    @GetMapping("/admin")
    public ResponseEntity<List<Order>> getAll() {
        return new ResponseEntity(orderService.getAll(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrderByUsername(Authentication auth) {
        return new ResponseEntity(orderService.getOrderByUsername(auth.getName()), HttpStatus.OK);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Order>> getRecentOrder() {
        return new ResponseEntity(orderService.getRecentOrder(), HttpStatus.OK);
    }

    @GetMapping("/unpaid")
    public ResponseEntity<List<Order>> getUnpaid() {
        return new ResponseEntity(orderService.getUnpaid(), HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<HashMap<String, String>> getCountByStatus() {
        return new ResponseEntity(orderService.getCountStatus(), HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getById(@PathVariable Long orderId) {
        return new ResponseEntity(orderService.getById(orderId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Order> createOrderCustomer(@RequestBody Set<OrderJenisJahitan> orderDataCustomer, Authentication auth) {
        return new ResponseEntity(orderService.createOrderCustomer(auth.getName(), orderDataCustomer), HttpStatus.CREATED);
    }

    @PostMapping("/admin")
    public ResponseEntity<Order> createOrderAdmin(@RequestBody OrderDataAdmin orderDataAdmin) {
        return new ResponseEntity(orderService.createOrderAdmin(orderDataAdmin), HttpStatus.CREATED);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Order> update(@PathVariable Long orderId, @RequestBody OrderData orderDataAdmin) {
        return new ResponseEntity(orderService.update(orderId, orderDataAdmin), HttpStatus.OK);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Order> delete(@PathVariable Long orderId) {
        return new ResponseEntity(orderService.deleteOrder(orderId), HttpStatus.OK);
    }

    @GetMapping("/export-pdf/{noOrder}")
    public void generatePdf(HttpServletResponse response, @PathVariable String noOrder) throws IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String curentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=jahitku_" + curentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        this.orderService.exportPdf(response, noOrder);

    }

    @PutMapping("/order-jahitan/update")
    public ResponseEntity<JenisJahitanOrder> updateJenisJahitanOrder(@RequestBody JenisJahitanOrderData jjod) {
        return new ResponseEntity(jenisJahitanOrderService.updateJenisJahitanOrder(jjod), HttpStatus.OK);
    }

    @DeleteMapping("/jenis-order")
    public ResponseEntity<ResponseData> deleteJJO(Long jenisJahitanId, Long orderId) {
        return new ResponseEntity(jenisJahitanOrderService.deleteJenisJahitanOrder(jenisJahitanId,orderId), HttpStatus.OK);

    }
}

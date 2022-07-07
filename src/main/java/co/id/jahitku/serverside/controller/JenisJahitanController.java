/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.id.jahitku.serverside.controller;

import co.id.jahitku.serverside.model.JenisJahitan;
import co.id.jahitku.serverside.model.JenisJahitanOrder;
import co.id.jahitku.serverside.model.dto.JenisJahitanOrderData;
import co.id.jahitku.serverside.service.JenisJahitanOrderService;
import co.id.jahitku.serverside.service.JenisJahitanService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author DELL ALIENWARE
 */
@RestController
@RequestMapping("/jenisjahitan")
@AllArgsConstructor
public class JenisJahitanController {

    private JenisJahitanService jahitanService;
    private JenisJahitanOrderService jenisJahitanOrderService;

    @GetMapping
    public ResponseEntity<List<JenisJahitan>> getAll() {
        return new ResponseEntity(jahitanService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JenisJahitan> getById(@PathVariable Long id) {
        return new ResponseEntity(jahitanService.getById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<JenisJahitan> create(@RequestBody JenisJahitan jenisJahitan) {
        return new ResponseEntity(jahitanService.create(jenisJahitan), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JenisJahitan> update(@PathVariable Long id, @RequestBody JenisJahitan jenisJahitan) {
        return new ResponseEntity(jahitanService.update(id, jenisJahitan), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<JenisJahitan> deleteJenis(@PathVariable Long id) {
        return new ResponseEntity(jahitanService.delete(id), HttpStatus.OK);
    }

    @GetMapping("/order")
    public ResponseEntity<List<JenisJahitanOrder>> getListJenisOrder(@RequestParam("id") Long id) {
        return new ResponseEntity(jenisJahitanOrderService.getAll(id), HttpStatus.OK);
    }

    @GetMapping("/order-jahitan")
    public ResponseEntity<JenisJahitanOrder> getJenisJahitanOrder(@RequestParam Long idJenis, @RequestParam Long idOrder) {
        return new ResponseEntity(jenisJahitanOrderService.getJenisJahitanOrder(idJenis, idOrder), HttpStatus.OK);
    }

}

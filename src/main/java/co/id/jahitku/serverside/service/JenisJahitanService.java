/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.id.jahitku.serverside.service;

import co.id.jahitku.serverside.model.JenisJahitan;
import co.id.jahitku.serverside.repository.JenisJahitanRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author DELL ALIENWARE
 */
@Service
@AllArgsConstructor
public class JenisJahitanService {

    private JenisJahitanRepository jahitanRepository;

    public List<JenisJahitan> getAll() {
        return jahitanRepository.findAll();
    }

    public JenisJahitan getById(Long id) {
        return jahitanRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jenis jahitan tidak ditemukan")
        );
    }

    public JenisJahitan create(JenisJahitan jenisJahitan) {
        if (jahitanRepository.findByNama(jenisJahitan.getNama()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Jenis jahitan ini sudah ada");
        }
        jenisJahitan.setId(null);
        return jahitanRepository.save(jenisJahitan);
    }

    public JenisJahitan update(Long id, JenisJahitan jenisJahitan) {
        if (jahitanRepository.findByNama(jenisJahitan.getNama()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Jenis jahitan ini sudah ada");
        }
        jenisJahitan.setId(id);
        return jahitanRepository.save(jenisJahitan);
    }

    public JenisJahitan delete(Long id) {
        JenisJahitan jenisJahitan = getById(id);
        jahitanRepository.delete(jenisJahitan);
        return jenisJahitan;
    }
}

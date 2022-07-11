/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.service;

import co.id.jahitku.serverside.model.Role;
import co.id.jahitku.serverside.model.dto.ResponseData;
import co.id.jahitku.serverside.repository.RoleRepository;
import co.id.jahitku.serverside.service.crud.crudInterface;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Firli
 */
@Service
@AllArgsConstructor
public class RoleService implements crudInterface<Role>{
    private RoleRepository roleRepository;

    @Override
    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    @Override
    public Role getById(Long id) {
        return roleRepository.findById(id).orElseThrow(
        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role Tidak ditemukan")
        );
    }
    
    public ResponseData createRole(String nama){
        Role role = new Role();
        role.setNama(nama);
        try {
            roleRepository.save(role);
            return new ResponseData("success", "berhasil menyimpan role");
        } catch (Exception e) {
            return new ResponseData("error", e.getMessage());
        }
    }
    public ResponseData updateRole(Role role){
        try {
            roleRepository.save(role);
            return new ResponseData("success", "berhasil menyimpan role");
        } catch (Exception e) {
            return new ResponseData("error", e.getMessage());
        }
    }
    
    
    @Override
    public Role delete(Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

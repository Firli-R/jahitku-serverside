/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.controller;

import co.id.jahitku.serverside.model.Role;
import co.id.jahitku.serverside.model.dto.ResponseData;
import co.id.jahitku.serverside.service.RoleService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Firli
 */
@RestController
@AllArgsConstructor
@RequestMapping("/role")
public class RoleController {
    private final RoleService roleService;
    
    @GetMapping
    public List<Role> getAllRole(){
        return roleService.getAll();
    }
    
    @GetMapping("/create")
    public ResponseEntity<ResponseData> createRole(String nama){
        return new ResponseEntity(roleService.createRole(nama), HttpStatus.OK);
    }
    
    @PutMapping
    public ResponseEntity<ResponseData> updateRole(Role role){
        return new ResponseEntity(roleService.updateRole(role), HttpStatus.OK);
    }
}

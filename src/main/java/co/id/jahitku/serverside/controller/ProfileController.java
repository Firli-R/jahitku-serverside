/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.controller;

import co.id.jahitku.serverside.model.User;
import co.id.jahitku.serverside.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Firli
 */
@RestController
@AllArgsConstructor
@RequestMapping("/profile")
public class ProfileController {
    private UserService userSevice;
    
    @GetMapping
    public ResponseEntity<User> getByName(Authentication auth){
        return new ResponseEntity(userSevice.getByUsername(auth.getName()),HttpStatus.OK);
    }
}

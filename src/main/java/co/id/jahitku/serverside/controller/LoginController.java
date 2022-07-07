/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.controller;

import co.id.jahitku.serverside.model.dto.LoginData;
import co.id.jahitku.serverside.model.dto.LoginResponseData;
import co.id.jahitku.serverside.service.LoginService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Firli
 */
@RestController
@RequestMapping("/login")
@AllArgsConstructor
public class LoginController {
    private LoginService loginService;
    
    @PostMapping
    public ResponseEntity<LoginResponseData>  login(@RequestBody LoginData loginData){
        if(loginData != null){
            return new ResponseEntity(loginService.login(loginData), HttpStatus.OK);
        }else{
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.controller;

import co.id.jahitku.serverside.model.User;
import co.id.jahitku.serverside.model.VerificationToken;
import co.id.jahitku.serverside.model.dto.LoginData;
import co.id.jahitku.serverside.model.dto.LoginResponseData;
import co.id.jahitku.serverside.model.dto.ResponseData;
import co.id.jahitku.serverside.model.dto.UpdateData;
import co.id.jahitku.serverside.model.dto.UserData;
import co.id.jahitku.serverside.service.LoginService;
import co.id.jahitku.serverside.service.RegistrationService;
import co.id.jahitku.serverside.service.UserService;
import co.id.jahitku.serverside.service.VerificationTokenService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Firli
 */
@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Slf4j
public class UserController {
    
    private UserService userService;
    private LoginService loginService;
    private RegistrationService regisService;
    private VerificationTokenService verifTokenService;

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return new ResponseEntity(userService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable("id") Long id) {
        return new ResponseEntity(userService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/get/username")
    public ResponseEntity<User> getByUsername(Authentication auth) {
        return new ResponseEntity(userService.getByAuth(auth.getName()), HttpStatus.OK);
    }
    @GetMapping("/validation/{username}")
    public ResponseEntity<ResponseData> getByUsername(@PathVariable String username) {
        return new ResponseEntity(userService.getByUsername(username), HttpStatus.OK);
    }
    @GetMapping("/validationEmail/{email}")
    public ResponseEntity<ResponseData> getByEmail(@PathVariable String email) {
        return new ResponseEntity(userService.getByEmail(email), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> delete(@PathVariable Long id) {
        return new ResponseEntity(userService.delete(id), HttpStatus.OK);
    }

    @DeleteMapping("/get/{username}")
    public ResponseEntity<User> deleteByUsername(@PathVariable String username) {
        return new ResponseEntity(userService.deleteByUsername(username), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseData> create(@RequestBody User user) {
        return new ResponseEntity(regisService.regis(user), HttpStatus.CREATED);
    }

    @GetMapping("/verify-page/{token}")
    public ResponseEntity<VerificationToken> getDataToken(@PathVariable String token) {
        return new ResponseEntity(verifTokenService.getToken(token), HttpStatus.OK);
    }

    @PostMapping("/verify-page")
    public ResponseEntity<ResponseData> pushToken(Authentication auth) {
        return new ResponseEntity(regisService.manualSendEmail(auth.getName()), HttpStatus.OK);
    }

    @GetMapping("/verify")
    public ResponseEntity<ResponseData> getToken(@RequestParam String token) {
        log.error("ini adalah token="+token);
        return new ResponseEntity(regisService.confirmToken(token), HttpStatus.OK);
    }

    @PostMapping("/verify")
    public ResponseEntity<ResponseData> pushTokenVerify(@RequestParam(name = "token") String token) {
//        log.error("ini adalah token"+token);
        return new ResponseEntity(regisService.manualSendEmailVerify(token), HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<User> update(@RequestBody UserData user) {
        return new ResponseEntity(userService.update(user), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseData> loginRequest(@RequestBody LoginData loginData) {
        return new ResponseEntity(loginService.login(loginData), HttpStatus.OK);
    }

    @PutMapping("/gantiEmail")
    public ResponseEntity<ResponseData> changeEmail(@RequestBody UpdateData updateData) {
        return new ResponseEntity(userService.updateEmail(updateData), HttpStatus.OK);
    }

    @PutMapping("/gantiPassword")
    public ResponseEntity<User> changePassword(@RequestBody UpdateData updateData) {
        return new ResponseEntity(userService.updatePassword(updateData), HttpStatus.OK);
    }
    
    @GetMapping("/forgotPassword")
    public ResponseEntity<ResponseData> forgotPassword(String email){
        return new ResponseEntity(userService.forgotPass(email), HttpStatus.OK);
    }

}

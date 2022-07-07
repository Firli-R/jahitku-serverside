/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.service;

import co.id.jahitku.serverside.model.User;
import co.id.jahitku.serverside.model.dto.LoginData;
import co.id.jahitku.serverside.model.dto.LoginResponseData;
import co.id.jahitku.serverside.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Firli
 */
@Service
@AllArgsConstructor
public class LoginService {

    private AppUserDetailService appUserDetailService;
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;

    public LoginResponseData login(LoginData loginData) {
        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(loginData.getUsername(), loginData.getPassword());
       
        Authentication auth = authenticationManager.authenticate(authReq);
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);

        UserDetails userDetails = appUserDetailService.loadUserByUsername(loginData.getUsername());
        List<String> authorities = userDetails.getAuthorities()
                .stream().map(authority -> authority.getAuthority())
                .collect(Collectors.toList());
        User user = userRepository.findByUsernameOrEmail(loginData.getUsername(), loginData.getUsername()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "data tidak tersedia") 
        );
        
        return new LoginResponseData(user.getNama(), user.getPhone(), user.getUsername(), user.getEmail(), user.getAlamat(), authorities);
    }
}

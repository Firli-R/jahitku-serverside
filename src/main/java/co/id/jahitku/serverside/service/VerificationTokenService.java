/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.service;

import co.id.jahitku.serverside.model.User;
import co.id.jahitku.serverside.model.VerificationToken;
import co.id.jahitku.serverside.repository.UserRepository;
import co.id.jahitku.serverside.repository.VerificationTokenRepository;
import java.time.LocalDateTime;
import java.util.Optional;
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
public class VerificationTokenService {
    private VerificationTokenRepository verificationTokenRepository;
    private UserRepository userRepository;
    
    public void saveVerificationToken(VerificationToken token){
        verificationTokenRepository.save(token);
    }
    public VerificationToken getToken(String token){
        return verificationTokenRepository.findByToken(token).orElseThrow(
                ()-> {return new ResponseStatusException(HttpStatus.NOT_FOUND, "Token User Not Found");}
        );
    }
    public int setConfirmedAt(String token){
        return verificationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }
    
    public VerificationToken getUserToken(String username){
        User user = userRepository.findByUsername(username);   
        return  verificationTokenRepository.findByUserId(user.getId()).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Data Not Found")
        );
    }
}

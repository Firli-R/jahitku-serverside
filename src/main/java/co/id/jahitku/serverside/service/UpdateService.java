/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.service;

import co.id.jahitku.serverside.model.User;
import co.id.jahitku.serverside.model.dto.ResponseData;
import co.id.jahitku.serverside.model.dto.UpdateData;
import co.id.jahitku.serverside.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
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
public class UpdateService {
    private UserRepository userRespository;
    private RegistrationService regis;
    
     public ResponseData updateEmail(UpdateData updateData) {
        User userLama = userRespository.findByUsername(updateData.getUsername());
        User userBaru = userRespository.findByEmail(updateData.getDataBaru());
        if (userLama != null) {
            if (userBaru != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email Already Registred");
            } else {
                userLama.setEmail(updateData.getDataBaru());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Data Not Found");
        }
        Map<String,String> token = new HashMap();
        userLama.getVerificationToken().forEach(
                (data) ->{
                    token.put("tokenLama", data.getToken());
                }
        );
        userRespository.save(userLama);
        
        return regis.manualSendEmail(updateData.getUsername());
    }
}

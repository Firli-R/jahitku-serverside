/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.service;

import co.id.jahitku.serverside.model.AppUserDetail;
import co.id.jahitku.serverside.model.User;
import co.id.jahitku.serverside.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Firli
 */
@Service
@AllArgsConstructor
public class AppUserDetailService implements UserDetailsService {
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElseThrow(
        ()-> new UsernameNotFoundException("User not Registered"));
        
        return new AppUserDetail(user);
    }
    
}

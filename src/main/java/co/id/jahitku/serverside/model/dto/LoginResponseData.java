/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author Firli
 */
@Data
@AllArgsConstructor
public class LoginResponseData {
    private String nama;
    private String phone;
    private String username;
    private String email;
    private String alamat;
    private List<String> authorities;
}

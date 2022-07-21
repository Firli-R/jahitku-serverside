/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.service;

import co.id.jahitku.serverside.model.User;
import co.id.jahitku.serverside.model.VerificationToken;
import co.id.jahitku.serverside.model.dto.EmailData;
import co.id.jahitku.serverside.model.dto.ResponseData;
import co.id.jahitku.serverside.repository.VerificationTokenRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Firli
 */
@Service
@AllArgsConstructor
@Slf4j
public class RegistrationService {

    private UserService userService;
    private EmailService emailService;
    private EmailValidator emailValidator;
    private VerificationTokenService verificationTokenService;
    private PasswordEncoder passwordEndocer;
    private VerificationTokenRepository verificationTokenRepository;

    public ResponseData regis(User user) {
        try {
            user.setPassword(passwordEndocer.encode(user.getPassword()));
            Boolean cekEmail = emailValidator.test(user.getEmail());
            if (!cekEmail) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email Not Valid");
            }
            String token = userService.create(user);
            String link = "https://jahitku.herokuapp.com/verification-page?token=" + token;
            EmailData emailData = new EmailData(user.getEmail(), token, "Confirm your email", userService.buildEmail(user.getNama(), link), "no Atch");
            emailService.sendVerification(emailData);

            return new ResponseData("success", token);
        } catch (Exception e) {
            return new ResponseData("error", e.getMessage());
        }

    }

    public ResponseData manualSendEmail(String username) {
        VerificationToken dataToken = verificationTokenService.getUserToken(username);
        String newToken = UUID.randomUUID().toString();
        dataToken.setToken(newToken);
        dataToken.setCreatedAt(LocalDateTime.now());
        dataToken.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        verificationTokenRepository.save(dataToken);
//        String link = "http://localhost:8083/api/user/verify?token=" + newToken;
        String link = "https://jahitku.herokuapp.com/verification-page?token=" + newToken;
        EmailData emailData = new EmailData(dataToken.getUser().getEmail(), newToken, "Confirm your email", userService.buildEmail(dataToken.getUser().getNama(), link), "no Atch");
        emailService.sendVerification(emailData);
        return new ResponseData("success", "cek email anda");
    }

    public ResponseData manualSendEmailVerify(String token) {

        VerificationToken dataToken = verificationTokenService.getToken(token);
        log.info("ini adalah token =" + token);
        String newToken = UUID.randomUUID().toString();
        dataToken.setToken(newToken);
        dataToken.setCreatedAt(LocalDateTime.now());
        dataToken.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        verificationTokenRepository.save(dataToken);
        String link = "https://jahitku-api.herokuapp.com/api/user/verify?token=" + newToken;
        EmailData emailData = new EmailData(dataToken.getUser().getEmail(), newToken, "Confirm your email", userService.buildEmail(dataToken.getUser().getNama(), link), "no Atch");
        emailService.sendVerification(emailData);
        return new ResponseData("success", "cek email anda");
    }

    @Transactional
    public ResponseData confirmToken(String token) {

        VerificationToken verificationToken = verificationTokenService.getToken(token);
        verificationToken.setConfirmedAt(LocalDateTime.now());
        verificationTokenRepository.save(verificationToken);
        if (verificationToken.getConfirmedAt() != null) {
            userService.enableAppUser(verificationToken.getUser().getEmail());
            return new ResponseData(HttpStatus.CONFLICT.toString(), "Email Already Confirmed");

        }
        LocalDateTime expiredAt = verificationToken.getExpiresAt();
        //sudah melalui waktu sekarang
        if (expiredAt.isBefore(LocalDateTime.now())) {
            return new ResponseData(HttpStatus.BAD_GATEWAY.toString(), "Verification Link Expired");
        }
//        verificationTokenService.setConfirmedAt(verificationToken.getToken());

        return new ResponseData("200", "success");
    }

}

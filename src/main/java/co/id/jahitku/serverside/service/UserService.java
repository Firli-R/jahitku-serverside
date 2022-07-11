/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.service;

import co.id.jahitku.serverside.model.Role;
import co.id.jahitku.serverside.model.User;
import co.id.jahitku.serverside.model.VerificationToken;
import co.id.jahitku.serverside.model.dto.EmailData;
import co.id.jahitku.serverside.model.dto.ResponseData;
import co.id.jahitku.serverside.model.dto.UpdateData;
import co.id.jahitku.serverside.model.dto.UserData;
import co.id.jahitku.serverside.repository.UserRepository;
import co.id.jahitku.serverside.repository.VerificationTokenRepository;
import co.id.jahitku.serverside.service.crud.crudInterface;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Firli
 */
@Service
@AllArgsConstructor
public class UserService implements crudInterface<User> {

    private UserRepository userRepository;
    private RoleService roleService;
    private VerificationTokenService verificationTokenService;
    private PasswordEncoder passwordEncoder;
    private VerificationTokenRepository verificationTokenRepository;
    private EmailService emailService;

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan")
        );
    }

    @Override
    public User delete(Long id) {
        User user = getById(id);
        userRepository.disableAppUser(id);
        userRepository.delete(user);
        return user;
    }

    public User deleteByUsername(String username) {
        User user = userRepository.findByUsername(username);
        userRepository.delete(user);
        return user;
    }

    public User getByAuth(String username) {
        return userRepository.findByUsername(username);
    }

    public ResponseData getByUsername(String username) {
        User user = userRepository.findByUsername(username);
        ResponseData responseData = new ResponseData();
        if (user != null) {
            responseData.setStatus("success");
            responseData.setMessage("Username valid");
            return responseData;
        } else {
            responseData.setStatus("error");
            responseData.setMessage("Terjadi kesalahan username atau password tidak sesuai");
            return responseData;
        }

    }

    public ResponseData getByEmail(String email) {
        User user = userRepository.findByEmail(email);
        ResponseData responseData = new ResponseData();
        if (user != null) {
            responseData.setStatus("success");
            responseData.setMessage("email Already taken");
            return responseData;
        } else {
            responseData.setStatus("error");
            responseData.setMessage("email available");
            return responseData;
        }

    }

    public String create(User user) {
        User cekUsername = userRepository.findByUsername(user.getUsername());
        User cekEmail = userRepository.findByUsername(user.getEmail());
        if (cekUsername != null || cekEmail != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username or Email Already taken");
        }
        user.setId(null);
        user.setRoles(setRole());
        userRepository.save(user);
        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken(token,
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), user);
        verificationTokenService.saveVerificationToken(verificationToken);

        return token;
    }

    public int enableAppUser(String email) {
        return userRepository.enableAppUser(email);
    }

    public User update(UserData obj) {
        User user = userRepository.findByUsername(obj.getUsername());

//        user.setId(user.getId());
//        user.setEmail(obj.getEmail());
        if (user != null) {
            if (user.getUsername().equalsIgnoreCase(obj.getUsername())) {
                user.setUsername(obj.getUsername());
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username Already exist");
            }
        } else {
            user.setUsername(obj.getUsername());
        }

        user.setAlamat(obj.getAlamat());
        user.setNama(obj.getNama());
        user.setPhone(obj.getPhone());
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        user.setRoles(setRole());

        return userRepository.save(user);
    }

    public ResponseData updateEmail(UpdateData updateData) {
        User userLama = userRepository.findByUsername(updateData.getUsername());
        User userBaru = userRepository.findByEmail(updateData.getDataBaru());
        if (userLama != null) {
            if (userBaru != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email Already Registred");
            } else {
                userLama.setEmail(updateData.getDataBaru());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Data Not Found");
        }
        userRepository.save(userLama);
        VerificationToken dataToken = verificationTokenService.getUserToken(updateData.getUsername());
        String newToken = UUID.randomUUID().toString();
        dataToken.setToken(newToken);
        dataToken.setCreatedAt(LocalDateTime.now());
        dataToken.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        dataToken.setConfirmedAt(null);
        verificationTokenRepository.save(dataToken);
        String link = "https://jahitku.herokuapp.com/verification-page?token=" + newToken;

        userRepository.disableAppUser(userLama.getId());
        EmailData emailData = new EmailData(dataToken.getUser().getEmail(), newToken, "Confirm your email", buildEmail(dataToken.getUser().getNama(), link), "no Atch");
        emailService.sendVerification(emailData);

        return new ResponseData("success", newToken);
    }

    public User updatePassword(UpdateData updateData) {
        String passBaru = passwordEncoder.encode(updateData.getDataBaru());
        User user = userRepository.findByUsername(updateData.getUsername());
        if (user != null) {
            user.setPassword(passBaru);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Data Not Found");
        }

        return userRepository.save(user);
    }

    public ResponseData forgotPass(String email) {
        User user = userRepository.findByEmail(email);
        try {
            if (user != null) {
                String code = UUID.randomUUID().toString().substring(0, 6);
                user.setPassword(passwordEncoder.encode(code));
                userRepository.save(user);
                EmailData emailData = new EmailData(email, "", "Pemberitahuan Password Baru", buildEmailForgotPass(user.getNama(), code), "");
                emailService.sendVerification(emailData);
                return new ResponseData("success", "cek email anda untuk perubahan password");
            } else {
                return new ResponseData("error", "email not valid");
            }
        } catch (Exception e) {
            return new ResponseData("error", e.getMessage());
        }
    }

    public List<Role> setRole() {
        List<Role> roles = new ArrayList<>();
        roles.add(roleService.getById(1L));
        return roles;
    }

    public String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n"
                + "\n"
                + "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n"
                + "\n"
                + "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n"
                + "    <tbody><tr>\n"
                + "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n"
                + "        \n"
                + "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n"
                + "          <tbody><tr>\n"
                + "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n"
                + "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
                + "                  <tbody><tr>\n"
                + "                    <td style=\"padding-left:10px\">\n"
                + "                  \n"
                + "                    </td>\n"
                + "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n"
                + "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n"
                + "                    </td>\n"
                + "                  </tr>\n"
                + "                </tbody></table>\n"
                + "              </a>\n"
                + "            </td>\n"
                + "          </tr>\n"
                + "        </tbody></table>\n"
                + "        \n"
                + "      </td>\n"
                + "    </tr>\n"
                + "  </tbody></table>\n"
                + "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
                + "    <tbody><tr>\n"
                + "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n"
                + "      <td>\n"
                + "        \n"
                + "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
                + "                  <tbody><tr>\n"
                + "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n"
                + "                  </tr>\n"
                + "                </tbody></table>\n"
                + "        \n"
                + "      </td>\n"
                + "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n"
                + "    </tr>\n"
                + "  </tbody></table>\n"
                + "\n"
                + "\n"
                + "\n"
                + "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
                + "    <tbody><tr>\n"
                + "      <td height=\"30\"><br></td>\n"
                + "    </tr>\n"
                + "    <tr>\n"
                + "      <td width=\"10\" valign=\"middle\"><br></td>\n"
                + "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n"
                + "        \n"
                + "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 30 minutes. <p>See you soon</p>"
                + "        \n"
                + "      </td>\n"
                + "      <td width=\"10\" valign=\"middle\"><br></td>\n"
                + "    </tr>\n"
                + "    <tr>\n"
                + "      <td height=\"30\"><br></td>\n"
                + "    </tr>\n"
                + "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n"
                + "\n"
                + "</div></div>";
    }

    public String buildEmailForgotPass(String name, String code) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n"
                + "\n"
                + "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n"
                + "\n"
                + "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n"
                + "    <tbody><tr>\n"
                + "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n"
                + "        \n"
                + "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n"
                + "          <tbody><tr>\n"
                + "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n"
                + "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
                + "                  <tbody><tr>\n"
                + "                    <td style=\"padding-left:10px\">\n"
                + "                  \n"
                + "                    </td>\n"
                + "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n"
                + "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n"
                + "                    </td>\n"
                + "                  </tr>\n"
                + "                </tbody></table>\n"
                + "              </a>\n"
                + "            </td>\n"
                + "          </tr>\n"
                + "        </tbody></table>\n"
                + "        \n"
                + "      </td>\n"
                + "    </tr>\n"
                + "  </tbody></table>\n"
                + "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
                + "    <tbody><tr>\n"
                + "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n"
                + "      <td>\n"
                + "        \n"
                + "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
                + "                  <tbody><tr>\n"
                + "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n"
                + "                  </tr>\n"
                + "                </tbody></table>\n"
                + "        \n"
                + "      </td>\n"
                + "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n"
                + "    </tr>\n"
                + "  </tbody></table>\n"
                + "\n"
                + "\n"
                + "\n"
                + "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
                + "    <tbody><tr>\n"
                + "      <td height=\"30\"><br></td>\n"
                + "    </tr>\n"
                + "    <tr>\n"
                + "      <td width=\"10\" valign=\"middle\"><br></td>\n"
                + "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n"
                + "        \n"
                + "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Terima kasih telah meminta bantuan . Berikut password terbaru anda : </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> " + code + "</p></blockquote>\n Harap berhati - hati. <p>See you soon</p>"
                + "        \n"
                + "      </td>\n"
                + "      <td width=\"10\" valign=\"middle\"><br></td>\n"
                + "    </tr>\n"
                + "    <tr>\n"
                + "      <td height=\"30\"><br></td>\n"
                + "    </tr>\n"
                + "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n"
                + "\n"
                + "</div></div>";
    }
}

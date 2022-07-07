/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.id.jahitku.serverside.service;

import co.id.jahitku.serverside.model.dto.EmailData;
import java.io.File;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author LENOVO
 */
@Service
public class EmailService {

    private JavaMailSender mailSender;
    private final static Logger LOGGER = LoggerFactory
            .getLogger(EmailService.class);

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public EmailData sendSimpleEmail(EmailData emailData) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("notreroll1@gmail.com");
            message.setTo(emailData.getToEmail());
            message.setSubject(emailData.getSubject());
            message.setText(emailData.getBody());
            mailSender.send(message);
        } catch (MailException mx) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mx.getMessage());
        }
        return emailData;
    }

    public EmailData sendAttachment(EmailData emailData) throws MessagingException {
        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("notreroll1@gmail.com");
            helper.setTo(emailData.getToEmail());
            helper.setSubject(emailData.getSubject());
            helper.setText(emailData.getBody());

            FileSystemResource file = new FileSystemResource(new File(emailData.getAttachment()));
            helper.addAttachment(file.getFilename(), file);
            mailSender.send(message);
        } catch (MailException mx) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mx.getMessage());
        }
        return emailData;
    }

    public EmailData sendHtml(EmailData emailData) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            helper.setFrom("notreroll1@gmail.com");
            helper.setTo(emailData.getToEmail());
            helper.setSubject(emailData.getSubject());
            helper.setText(emailData.getBody(), true);

            FileSystemResource file = new FileSystemResource(new File(emailData.getAttachment()));
            helper.addAttachment(file.getFilename(), file);
            mailSender.send(message);
        } catch (MailException mx) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mx.getMessage());
        }
        return emailData;
    }
    
    public void sendVerification(EmailData emailData) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(emailData.getBody(), true);
            helper.setTo(emailData.getToEmail());
            helper.setSubject(emailData.getSubject());
            helper.setFrom("jahitku.tailorship@gmail.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }
    
    
    
}

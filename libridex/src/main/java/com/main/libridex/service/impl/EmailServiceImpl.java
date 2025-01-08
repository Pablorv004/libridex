package com.main.libridex.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.main.libridex.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service("emailService")
public class EmailServiceImpl implements EmailService{

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @Override
    public void sendEmailWithImage(String to, String subject, String text, String bookName, String bookImageUrl) {
        MimeMessage message = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            String[] parts = to.split("@");
            String username = parts[0];
            String htmlMsg = "<html><body>"
                    + "<h1>Libridex</h1>"
                    + "<br>"
                    + "<h2>Dear " + username + ",</h2>"
                    + "<h3>Congrats!</h3>"
                    + "<img src='" + bookImageUrl + "' alt='Book Image' />"
                    + "<p>Your reserved book <b>" + bookName + "</b> is now available for lending. You have a total of one week to return it.</p>"
                    + "<p>You may find more information on how to return it and your profile on our site.</p>"
                    + "<p>Thank you for using our services!</p>"
                    + "<p>Best regards,</p>"
                    + "<p>Libridex Team</p>"
                    + "</body></html>";
            helper.setText(htmlMsg, true);
            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    
}

package com.main.libridex.service;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text);
    void sendEmailWithImage(String to, String subject, String text, String bookName, String bookImageUrl);
}

package com.main.libridex.service;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text);
    void sendEmailReservationAvailable(String to, String bookName, String bookImageUrl);
    void sendEmailLending(String to, String bookName, String bookImageUrl);
    void sendEmailReturn(String to, String bookName, String bookImageUrl);
    void sendEmailReturnLate(String to, String bookName, String bookImageUrl);
}

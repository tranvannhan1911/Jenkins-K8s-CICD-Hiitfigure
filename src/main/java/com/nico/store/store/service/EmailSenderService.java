package com.nico.store.store.service;

public interface EmailSenderService {
    void sendEmail(String toEmail,
                          String subject,
                          String body);
}

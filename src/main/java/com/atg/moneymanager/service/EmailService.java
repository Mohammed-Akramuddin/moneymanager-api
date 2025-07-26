package com.atg.moneymanager.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.apache.commons.validator.routines.EmailValidator;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromMail;

    public void sendMail(String to, String subject, String text) {
        // Validate email address
        if (to == null || to.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipient email address cannot be null or empty");
        }
        if (!EmailValidator.getInstance().isValid(to)) {
            throw new IllegalArgumentException("Invalid email address format: " + to);
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromMail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email to " + to + ": " + e.getMessage(), e);
        }
    }
}
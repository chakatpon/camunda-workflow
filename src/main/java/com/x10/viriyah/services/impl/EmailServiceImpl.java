package com.x10.viriyah.services.impl;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender sender;

    @Value("${fixed.mail.from}")
    private String mailFrom;
    
    @Override
    public void sendEmail(String mailSendTo, String mailSubject, String mailContent) {
        MimeMessage msg = sender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setFrom(mailFrom);
            helper.setTo(InternetAddress.parse(mailSendTo));
            helper.setSubject(mailSubject);
            helper.setText(mailContent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sender.send(msg);

    }
    
}

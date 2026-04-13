package com.example.coreservice.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String code){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@aicompanion.com");
        message.setTo(to);
        message.setSubject("Mã xác thực tài khoản AI Companion");
        message.setText("Chào bạn,\n\nMã xác thực của bạn là: " + code +
                "\nMã này có hiệu lực trong 15 phút. Vui lòng không chia sẻ mã này cho bất kỳ ai.");

        mailSender.send(message);
    }
}

package com.example.project_bookstore.Service;


import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendHtmlEmail(String to, String subject, String templateName, Context context) {
        try {
            System.out.println("[EMAIL DEBUG] Đang gửi email tới: " + to);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String html = templateEngine.process(templateName, context);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);

            System.out.println("[EMAIL DEBUG] Gửi email thành công tới: " + to);

        } catch (org.springframework.mail.MailAuthenticationException e) {
            System.err.println("[EMAIL ERROR] Lỗi xác thực SMTP - Kiểm tra email và app password!");
            System.err.println("[EMAIL ERROR] " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[EMAIL ERROR] Lỗi gửi email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

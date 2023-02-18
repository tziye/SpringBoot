package com.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Objects;

@Slf4j
class MailTest extends ApplicationTest {

    @Autowired
    JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String account;

    @Test
    void test() throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        helper = new MimeMessageHelper(message, true);
        helper.setFrom(account);
        helper.setTo(account);
        helper.setSubject("springboot send mail");
        helper.setText("一封来自springboot的邮件", false);
        Resource image = new ClassPathResource("static/head.jpg");
        helper.addAttachment(Objects.requireNonNull(image.getFilename()), image);
        Resource file = new ClassPathResource("application.yaml");
        helper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
        mailSender.send(message);
    }
}

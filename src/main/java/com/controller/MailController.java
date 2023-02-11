package com.controller;

import com.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Objects;

@RestController
@RequestMapping("/mail")
public class MailController {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String account;

    @GetMapping("/send")
    public Result<String> send() throws MessagingException {
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
        return Result.success();
    }

}
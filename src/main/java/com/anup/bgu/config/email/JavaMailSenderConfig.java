package com.anup.bgu.config.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Configuration
public class JavaMailSenderConfig {

    @Autowired
    private final EmailProperties emailProperties;

    public JavaMailSenderConfig(EmailProperties emailProperties) {
        this.emailProperties = emailProperties;
    }

    @Bean
    public Map<String, JavaMailSender> mailSenders() {
        Map<String, JavaMailSender> mailSenderMap = new HashMap<>();
        List<EmailProperties.EmailAccount> accounts = emailProperties.getAccounts();

        for (EmailProperties.EmailAccount account : accounts) {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

            mailSender.setHost("smtp.gmail.com"); // Update for your SMTP host
            mailSender.setPort(587); // Update for your SMTP port

            mailSender.setUsername(account.getEmail());
            mailSender.setPassword(account.getPassword());

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

            mailSenderMap.put(account.getEmail(), mailSender);
        }

        return mailSenderMap;
    }
}

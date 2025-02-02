package com.anup.bgu.mail.service.impl;

import com.anup.bgu.config.email.EmailProperties;
import com.anup.bgu.mail.dto.MailData;
import com.anup.bgu.mail.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final SpringTemplateEngine templateEngine;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Map<String, JavaMailSender> mailSenders;
    private final EmailProperties emailProperties;
    private static final String EMAIL_INDEX_KEY = "EMAIL_INDEX";

    private void sendEmail(MailData mailData) {
        JavaMailSender mailSender = getMailSender();

        log.info("sendEmail()->  {}",mailData);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(mimeMessage, true);
            // Prepare the Thymeleaf context
            Context context = new Context();
            context.setVariables(mailData.variables());

            // Process the Thymeleaf template
            String htmlContent = templateEngine.process(mailData.templateName(), context);

            // Set email properties
            helper.setTo(mailData.to());
            helper.setSubject(mailData.subject());
            helper.setText(htmlContent, true);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        // Send the email
        mailSender.send(mimeMessage);
    }

    private int getAndUpdateIndex() {
        Long index = redisTemplate.opsForValue().increment(EMAIL_INDEX_KEY);
        // Calculate index using modulo to wrap around when it exceeds the account size
        List<EmailProperties.EmailAccount> accounts = emailProperties.getAccounts();
        return (index != null) ? (index.intValue() % accounts.size()) : 0;
    }

    private JavaMailSender getMailSender() {
        int index = getAndUpdateIndex();
        List<EmailProperties.EmailAccount> accounts = emailProperties.getAccounts();
        EmailProperties.EmailAccount account = accounts.get(index);

        log.debug("getMailSender()->  Sending mail from: {}",account.getEmail());

        return mailSenders.get(account.getEmail());
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();

        MailData mailData = (MailData) serializer.deserialize(message.getBody());

        sendEmail(mailData);
    }
}

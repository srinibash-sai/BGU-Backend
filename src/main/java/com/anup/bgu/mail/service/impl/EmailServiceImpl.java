package com.anup.bgu.mail.service.impl;

import com.anup.bgu.mail.dto.MailData;
import com.anup.bgu.mail.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    private void sendEmail(MailData mailData){

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

    @Override
    public void onMessage(Message message, byte[] pattern) {
        JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();

        MailData mailData = (MailData) serializer.deserialize(message.getBody());

        sendEmail(mailData);

        log.info(mailData.toString());
    }
}

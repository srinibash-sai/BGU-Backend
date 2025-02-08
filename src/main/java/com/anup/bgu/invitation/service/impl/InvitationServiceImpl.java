package com.anup.bgu.invitation.service.impl;

import com.anup.bgu.excel.service.ExcelService;
import com.anup.bgu.exceptions.models.BadRequestException;
import com.anup.bgu.invitation.entities.InvitedEmail;
import com.anup.bgu.invitation.repo.InvitedEmailRepository;
import com.anup.bgu.invitation.service.InvitationService;
import com.anup.bgu.mail.dto.MailData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

    private final ExcelService excelService;
    private final InvitedEmailRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SpringTemplateEngine templateEngine;

    @Value("${secret.api-base-url}")
    private String API_BASE_URL;

    @Value("${secret.ticket-code}")
    private String TICKET_CODE;

    @Override
    @Transactional
    public void uploadExcel(MultipartFile file) {
        List<String> emails = excelService.emailExcelToList(file);
        List<InvitedEmail> emailList = new ArrayList<>();
        for (String email : emails) {
            InvitedEmail invitedEmail = InvitedEmail.builder()
                    .id(UUID.randomUUID().toString())
                    .email(email)
                    .isConsumed(false)
                    .build();
            emailList.add(invitedEmail);
        }
        repository.saveAll(emailList);

        for (InvitedEmail invitedEmail : emailList) {
            Map<String, Object> variables = new HashMap<>();
            String url = API_BASE_URL + "/invitation/ticket/" + invitedEmail.getId();
            variables.put("ticketUrl", url);

            String subject = "You are invited to BGU.";
            MailData mailData = new MailData(
                    invitedEmail.getEmail(),
                    subject,
                    "mail-templates/ticket",
                    variables
            );
            redisTemplate.convertAndSend("mail", mailData);
        }
    }

    @Override
    public void consumeTicket(String id, String code) {
        if (!code.equals(TICKET_CODE)) {
            log.debug("consumeTicket()-> Invalid code! Code:{}", code);
            throw new BadRequestException("Invalid Code");
        }
        InvitedEmail invitedEmail = repository.findById(id)
                .orElseThrow(() -> {
                    log.debug("consumeTicket()-> Invalid id! id:{}", id);
                    return new BadRequestException("Invalid Ticket");
                });
        invitedEmail.setIsConsumed(true);
        repository.save(invitedEmail);
        log.debug("consumeTicket()-> Ticket Consumed! id:{}", id);
    }

    @Override
    public String getTicketPage(String id) {
        Optional<InvitedEmail> invitedEmailOptional = repository.findById(id);
        if (invitedEmailOptional.isEmpty()) {
            log.debug("getTicketPage()-> Ticket not found! id:{}", id);
            return templateEngine.process("ticketPage/404", new Context());
        }
        InvitedEmail invitedEmail = invitedEmailOptional.get();

        if (invitedEmail.getIsConsumed()) {
            log.debug("getTicketPage()-> Ticket Consumed! id:{}", id);
            return templateEngine.process("ticketPage/consumed", new Context());
        }

        log.debug("getTicketPage()-> Ticket Page for id:{}", id);
        return templateEngine.process("ticketPage/notconsumed", new Context());
    }
}
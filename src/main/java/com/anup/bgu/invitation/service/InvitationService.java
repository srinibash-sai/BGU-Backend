package com.anup.bgu.invitation.service;

import org.springframework.web.multipart.MultipartFile;

public interface InvitationService {
    void uploadExcel(MultipartFile file);
    void consumeTicket(String id,String code);
    String getTicketPage(String id);
}

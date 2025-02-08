package com.anup.bgu.invitation.controller;

import com.anup.bgu.invitation.service.InvitationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/invitation")
@AllArgsConstructor
public class InvitationController {

    private final InvitationService service;

    @PostMapping("/upload/excel")
    public ResponseEntity<Void> uploadExcel(
            @RequestParam("file") @Valid @NotNull MultipartFile file
    ) {
        service.uploadExcel(file);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ticket/{id}")
    public ResponseEntity<Void> consumeTicket(
            @PathVariable("id") @NotEmpty String id,
            @RequestParam(value = "code") @NotNull String code
    ) {
        service.consumeTicket(id,code);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ticket/{id}")
    public ResponseEntity<String>  ticketPage(
            @PathVariable("id") @NotEmpty String id
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");
        return new ResponseEntity<>(service.getTicketPage(id), headers, HttpStatus.OK);
    }
}

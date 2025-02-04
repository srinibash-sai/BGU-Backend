package com.anup.bgu.notification.controller;


import com.anup.bgu.notification.dto.NotificationRequest;
import com.anup.bgu.notification.dto.TokenRequest;
import com.anup.bgu.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
@AllArgsConstructor
public class NotificationController {

    private final NotificationService service;

    // push Notification
    @PostMapping
    public ResponseEntity<Void> pushNotification(
            @RequestBody @Valid NotificationRequest request
    ) {
        service.pushNotification(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/subscribe")
    ResponseEntity<Void> sendToken(
            @RequestBody @Valid TokenRequest token
    ) {
        service.subscribe(token);
        return ResponseEntity.ok().build();
    }
}

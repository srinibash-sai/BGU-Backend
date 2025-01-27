package com.anup.bgu.admin.controller;

import com.anup.bgu.admin.dto.AuthRequest;
import com.anup.bgu.admin.dto.AuthResponse;
import com.anup.bgu.admin.service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final AdminService service;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return new ResponseEntity<>(service.login(request), HttpStatus.OK);
    }
}
package com.anup.bgu.noticeboard.controller;

import com.anup.bgu.noticeboard.dto.NoticeRequest;
import com.anup.bgu.noticeboard.dto.NoticeResponse;
import com.anup.bgu.noticeboard.service.NoticeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {
    private final NoticeService service;

    @PostMapping
    public ResponseEntity<Void> createNotice(
            @RequestBody @Valid NoticeRequest request
    ) {
        service.addNotice(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<NoticeResponse>> getAllNotice() {
        return new ResponseEntity<>(service.getAllNotice(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable("id") @NotEmpty String id) {
        service.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }
}

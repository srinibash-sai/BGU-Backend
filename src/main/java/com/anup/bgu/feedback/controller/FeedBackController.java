package com.anup.bgu.feedback.controller;


import com.anup.bgu.feedback.dto.FeedbackRequest;
import com.anup.bgu.feedback.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feedback")
@AllArgsConstructor
public class FeedBackController {

    private final FeedbackService service;

    @PostMapping
    public ResponseEntity<Void> postFeedback(@RequestBody @Valid FeedbackRequest request) {
        service.saveFeedback(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

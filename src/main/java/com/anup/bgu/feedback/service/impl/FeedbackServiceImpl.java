package com.anup.bgu.feedback.service.impl;

import com.anup.bgu.feedback.dto.FeedbackRequest;
import com.anup.bgu.feedback.dto.FeedbackResponse;
import com.anup.bgu.feedback.entities.Feedback;
import com.anup.bgu.feedback.repo.FeedbackRepository;
import com.anup.bgu.feedback.service.FeedbackService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedBackRepository;

    @Override
    public void saveFeedback(FeedbackRequest request) {
        Feedback feedback = Feedback.builder()
                .email(request.email())
                .message(request.message())
                .build();
        feedBackRepository.save(feedback);
    }

    @Override
    public List<FeedbackResponse> getAllFeedback() {
        return feedBackRepository.findAll().stream()
                .map(feedback -> new FeedbackResponse(
                        feedback.getEmail(),
                        feedback.getMessage(),
                        feedback.getTimestamp().toString()))
                .collect(Collectors.toList());
    }
}

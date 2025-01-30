package com.anup.bgu.feedback.service.impl;

import com.anup.bgu.feedback.dto.FeedbackRequest;
import com.anup.bgu.feedback.repo.FeedbackRepository;
import com.anup.bgu.feedback.service.FeedbackService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedBackRepository;


    @Override
    public void saveFeedback(FeedbackRequest request) {

    }
}

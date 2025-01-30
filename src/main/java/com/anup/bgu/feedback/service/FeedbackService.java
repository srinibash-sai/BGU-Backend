package com.anup.bgu.feedback.service;


import com.anup.bgu.feedback.dto.FeedbackRequest;

public interface FeedbackService {
    void saveFeedback(FeedbackRequest request);

}

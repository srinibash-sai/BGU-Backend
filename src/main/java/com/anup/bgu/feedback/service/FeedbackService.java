package com.anup.bgu.feedback.service;

import com.anup.bgu.feedback.dto.FeedbackRequest;
import com.anup.bgu.feedback.dto.FeedbackResponse;

import java.util.List;

public interface FeedbackService {
    void saveFeedback(FeedbackRequest request);

    List<FeedbackResponse> getAllFeedback();
}

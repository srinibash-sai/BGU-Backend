package com.anup.bgu.Noticeboard.service;

import com.anup.bgu.Noticeboard.dto.NoticeRequest;
import com.anup.bgu.Noticeboard.dto.NoticeResponse;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public interface NoticeService {
    void addNotice(NoticeRequest request);
    List<NoticeResponse> getAllNotice();
    void deleteNotice(String id);
}

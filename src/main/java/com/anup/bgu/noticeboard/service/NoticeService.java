package com.anup.bgu.noticeboard.service;

import com.anup.bgu.noticeboard.dto.NoticeRequest;
import com.anup.bgu.noticeboard.dto.NoticeResponse;

import java.util.List;

public interface NoticeService {
    void addNotice(NoticeRequest request);
    List<NoticeResponse> getAllNotice();
    void deleteNotice(String id);
}

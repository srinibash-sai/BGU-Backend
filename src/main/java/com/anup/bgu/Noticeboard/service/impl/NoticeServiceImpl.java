package com.anup.bgu.Noticeboard.service.impl;

import com.anup.bgu.Noticeboard.dto.NoticeRequest;
import com.anup.bgu.Noticeboard.dto.NoticeResponse;
import com.anup.bgu.Noticeboard.service.NoticeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class NoticeServiceImpl implements NoticeService {


    @Override
    public void addNotice(NoticeRequest request) {

    }

    @Override
    public List<NoticeResponse> getAllNotice() {
        return List.of();
    }

    @Override
    public void deleteNotice(String id) {

    }
}

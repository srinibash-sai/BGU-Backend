package com.anup.bgu.Noticeboard.service.impl;

import com.anup.bgu.Noticeboard.dto.NoticeRequest;
import com.anup.bgu.Noticeboard.dto.NoticeResponse;
import com.anup.bgu.Noticeboard.entities.Notice;
import com.anup.bgu.Noticeboard.repo.NoticeCacheRepo;
import com.anup.bgu.Noticeboard.repo.NoticeRepository;
import com.anup.bgu.Noticeboard.service.NoticeService;
import com.anup.bgu.exceptions.models.BadRequestException;
import com.anup.bgu.exceptions.models.InvalidRequestException;
import com.anup.bgu.exceptions.models.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class NoticeServiceImpl implements NoticeService {
    private final NoticeRepository noticeRepository;
    private final NoticeCacheRepo noticeCache;

    @Override
    public void addNotice(NoticeRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime expireDateTime = LocalDateTime.parse(request.expire(), formatter);

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));

        if (expireDateTime.isBefore(now)) {
            throw new InvalidRequestException("Expire time must be after now.");
        }

        Notice notice = Notice.builder()
                .notice(request.notice())
                .expire(expireDateTime)
                .build();

        noticeRepository.save(notice);
        noticeCache.save(notice);

        log.info("addNotice()-> {}", notice);
    }

    @Override
    public List<NoticeResponse> getAllNotice() {
        List<Notice> notices;
        if (!noticeCache.isCacheEmpty()) notices = noticeCache.fetchAll(); //fetch from cache
        else {
            notices = noticeRepository.findAll(); //fetch from db
            noticeCache.saveAll(notices);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        return notices.stream()
                .filter(notice ->
                        notice.getExpire().isAfter(LocalDateTime.now(ZoneId.of("Asia/Kolkata"))))
                .map(notice ->
                        new NoticeResponse(
                                notice.getId().toString(),
                                notice.getNotice(),
                                notice.getExpire().format(formatter)
                        )
                ).toList();
    }

    @Override
    public void deleteNotice(String id) {
        Long noticeId = Long.valueOf(id);
        if (noticeRepository.existsById(noticeId)) {
            noticeRepository.deleteById(noticeId);
            noticeCache.delete(id);
            log.info("Notice with id {} deleted successfully", id);
        } else {
            throw new NotFoundException("Notice with ID:" + id + " not found!");
        }
    }
}

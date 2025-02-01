package com.anup.bgu.Noticeboard.repo;

import com.anup.bgu.Noticeboard.entities.Notice;

import java.util.List;

public interface NoticeCacheRepo {
    void save(Notice notice);
    List<Notice> fetchAll();
    void delete(String noticeId);
    boolean isCacheEmpty();
    void saveAll(List<Notice> notices);
}

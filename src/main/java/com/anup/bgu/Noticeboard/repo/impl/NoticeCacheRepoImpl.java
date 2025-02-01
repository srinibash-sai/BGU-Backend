package com.anup.bgu.Noticeboard.repo.impl;

import com.anup.bgu.Noticeboard.entities.Notice;
import com.anup.bgu.Noticeboard.repo.NoticeCacheRepo;
import com.anup.bgu.event.entities.Event;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NoticeCacheRepoImpl implements NoticeCacheRepo {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String HASH_KEY = "NOTICE_CACHE";
    private final Duration timeToLive = Duration.ofMinutes(10);

    @Override
    public void save(Notice notice) {
        redisTemplate.opsForHash().put(HASH_KEY, notice.getId().toString(), notice);
        redisTemplate.expire(HASH_KEY, timeToLive);
    }

    @Override
    public List<Notice> fetchAll() {
        List<Object> notices = redisTemplate.opsForHash().values(HASH_KEY);
        redisTemplate.expire(HASH_KEY, timeToLive);
        return notices.stream().map(notice -> (Notice) notice).collect(Collectors.toList());
    }

    @Override
    public void delete(String noticeId) {
        redisTemplate.opsForHash().delete(HASH_KEY, noticeId);
    }

    @Override
    public boolean isCacheEmpty() {
        return !redisTemplate.hasKey(HASH_KEY);
    }

    @Override
    public void saveAll(List<Notice> notices) {
        for (Notice notice : notices)
            redisTemplate.opsForHash().put(HASH_KEY, notice.getId().toString(), notice);
        redisTemplate.expire(HASH_KEY, timeToLive);
    }
}

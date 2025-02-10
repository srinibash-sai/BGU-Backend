package com.anup.bgu.event.repo.impl;

import com.anup.bgu.event.entities.Event;
import com.anup.bgu.event.repo.EventCacheRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EventCacheRepoImpl implements EventCacheRepo {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String HASH_KEY = "EVENT_CACHE";
    private final Duration timeToLive = Duration.ofMinutes(20);

    @Override
    public Event save(Event event) {
        if(redisTemplate.hasKey(HASH_KEY)){
            redisTemplate.opsForHash().put(HASH_KEY, event.getId(), event);
            redisTemplate.expire(HASH_KEY, timeToLive);
            log.info("save()-> Event Saved in Cache! {}", event.getId());
        }
        return event;
    }

    @Override
    public void delete(Event event) {
        redisTemplate.opsForHash().delete(HASH_KEY, event.getId());
    }

    @Override
    public Optional<List<Event>> findAll() {
        Map<Object, Object> allEvents = redisTemplate.opsForHash().entries(HASH_KEY);

        if (allEvents.isEmpty()) {
            return Optional.empty();
        }

        List<Event> eventList = allEvents.values().stream()
                .map(event -> (Event) event)
                .collect(Collectors.toList());

        return Optional.of(eventList);
    }

    @Override
    public Optional<Event> findById(String id) {
        Object o = redisTemplate.opsForHash().get(HASH_KEY, id);

        return Optional.ofNullable((Event) o);
    }

    @Override
    public void saveAll(List<Event> events) {
        for (Event event : events) {
            redisTemplate.opsForHash().put(HASH_KEY, event.getId(), event);
        }
        redisTemplate.expire(HASH_KEY, timeToLive);
    }
}

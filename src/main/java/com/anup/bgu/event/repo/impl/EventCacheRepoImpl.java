package com.anup.bgu.event.repo.impl;

import com.anup.bgu.event.entities.Event;
import com.anup.bgu.event.entities.EventType;
import com.anup.bgu.event.entities.Status;
import com.anup.bgu.event.repo.EventCacheRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EventCacheRepoImpl implements EventCacheRepo {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String HASH_KEY = "EVENT_CACHE";
    private final Duration timeToLive = Duration.ofMinutes(10);

    @Override
    public Event save(Event event) {
        redisTemplate.opsForHash().put(HASH_KEY, event.getId(), event);
        redisTemplate.expire(HASH_KEY, timeToLive);

        return event;
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
    public Optional<List<Event>> findByEventTypeAndStatus(EventType eventType, Status status) {
        Map<Object, Object> allEvents = redisTemplate.opsForHash().entries(HASH_KEY);

        if (allEvents.isEmpty()) {
            return Optional.empty();
        }

        List<Event> eventList = allEvents.values().stream()
                .map(event -> (Event) event)
                .filter(event -> event.getEventType() == eventType && event.getStatus() == status)
                .collect(Collectors.toList());

        return eventList.isEmpty() ? Optional.empty() : Optional.of(eventList);
    }

    @Override
    public Optional<List<Event>> findByEventType(EventType eventType) {
        Map<Object, Object> allEvents = redisTemplate.opsForHash().entries(HASH_KEY);

        if (allEvents.isEmpty()) {
            return Optional.empty();
        }

        List<Event> eventList = allEvents.values().stream()
                .map(event -> (Event) event)
                .filter(event -> event.getEventType() == eventType)
                .collect(Collectors.toList());

        return eventList.isEmpty() ? Optional.empty() : Optional.of(eventList);
    }

    @Override
    public Optional<List<Event>> findByStatus(Status status) {
        Map<Object, Object> allEvents = redisTemplate.opsForHash().entries(HASH_KEY);

        if (allEvents.isEmpty()) {
            return Optional.empty();
        }

        List<Event> eventList = allEvents.values().stream()
                .map(event -> (Event) event)
                .filter(event -> event.getStatus() == status)
                .collect(Collectors.toList());

        return eventList.isEmpty() ? Optional.empty() : Optional.of(eventList);
    }

    @Override
    public void saveAll(List<Event> events) {
        for (Event event : events) {
            redisTemplate.opsForHash().put(HASH_KEY, event.getId(), event);
        }
        redisTemplate.expire(HASH_KEY, timeToLive);
    }
}

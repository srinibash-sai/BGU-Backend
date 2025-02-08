package com.anup.bgu.event.service.impl;

import com.anup.bgu.event.dto.EventRequest;
import com.anup.bgu.event.dto.EventResponse;
import com.anup.bgu.event.dto.EventUpdateRequest;
import com.anup.bgu.event.entities.Event;
import com.anup.bgu.event.entities.EventTeamType;
import com.anup.bgu.event.entities.EventType;
import com.anup.bgu.event.entities.Status;
import com.anup.bgu.event.mapper.EventMapper;
import com.anup.bgu.event.repo.EventCacheRepo;
import com.anup.bgu.event.repo.EventRepository;
import com.anup.bgu.event.service.EventService;
import com.anup.bgu.exceptions.models.EventNotFoundException;
import com.anup.bgu.exceptions.models.InvalidRequestException;
import com.anup.bgu.image.service.ImageService;
import com.anup.bgu.notification.dto.NotificationRequest;
import com.anup.bgu.registration.repo.SoloRegistrationRepository;
import com.anup.bgu.registration.repo.TeamRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final ImageService imageService;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventCacheRepo eventCacheRepo;
    private final SoloRegistrationRepository soloRegistrationRepository;
    private final TeamRegistrationRepository teamRegistrationRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Event createEvent(EventRequest eventRequest) {
        final String id = UUID.randomUUID().toString();

        Event event = Event.builder()
                .id(id)
                .title(eventRequest.title())
                .description(eventRequest.description())
                .status(Status.valueOf(eventRequest.status()))
                .rules(eventRequest.rules())
                .dateTime(eventRequest.dateTime())
                .amount(eventRequest.amount())
                .eventType(EventType.valueOf(eventRequest.eventType()))
                .coordinatorName(eventRequest.coordinatorName())
                .coordinatorNumber(eventRequest.coordinatorNumber())
                .teamType(EventTeamType.valueOf(eventRequest.teamType()))
                .build();

        if (event.getTeamType().equals(EventTeamType.TEAM)) {
            event.setMinMember(eventRequest.minTeamSize());
            event.setMaxMember(eventRequest.maxTeamSize());
        }

        event = eventRepository.save(event);
        log.info("createEvent()-> New event Created in db! {}", event);
        eventCacheRepo.save(event);
        log.info("createEvent()-> Event Saved in cache! {}", event.getId());

        //send notification
        NotificationRequest notificationRequest = new NotificationRequest(
                "🎉 New Event Alert: " + event.getTitle() + " - Join Now! 🚀",
                "📅 Event Name: " + event.getTitle() + "\n" +
                        "🗓 Date & Time: " + event.getDateTime() + "\n" +
                        "🔗 Don't miss out! Register now! 💥"
        );

        redisTemplate.convertAndSend("notification", notificationRequest);

        return event;
    }

    @Override
    @Transactional
    public Event updateEvent(String id, EventUpdateRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event id: " + id + "does not exist!"));

        if (request.title() != null) {
            event.setTitle(request.title());
        }
        if (request.description() != null) {
            event.setDescription(request.description());
        }
        if (request.status() != null) {
            event.setStatus(Status.valueOf(request.status()));
            if (event.getStatus().equals(Status.ONGOING)) {
                //send notification
                NotificationRequest notificationRequest = new NotificationRequest(
                        "🎉 Event Open for Registration: " + event.getTitle() + " - Join Now! 🚀",
                        "📅 Event Name: " + event.getTitle() + "\n" +
                                "🗓 Date & Time: " + event.getDateTime() + "\n" +
                                "📝 Registration Open! Don't miss out on this amazing opportunity!\n" +
                                "🔗 Register now and be part of the excitement! 💥"
                );

                redisTemplate.convertAndSend("notification", notificationRequest);
            }
            if (event.getStatus().equals(Status.CLOSED)) {
                //send notification
                NotificationRequest notificationRequest = new NotificationRequest(
                        "❌ Event Closed: " + event.getTitle() + " - Stay Tuned! 📅",
                        "🚫 The event '" + event.getTitle() + "' is now closed for registration.\n" +
                                "🎯 Don't worry, more exciting events are coming soon!\n" +
                                "📆 Stay updated and be ready for the next one! 💪"
                );
                redisTemplate.convertAndSend("notification", notificationRequest);
            }
        }
        if (request.rules() != null) {
            event.setRules(request.rules());
        }
        if (request.dateTime() != null) {
            event.setDateTime(request.dateTime());
        }
        if (request.coordinatorName() != null) {
            event.setCoordinatorName(request.coordinatorName());
        }
        if (request.coordinatorNumber() != null) {
            event.setCoordinatorNumber(request.coordinatorNumber());
        }
        if (request.amount() != null) {
            event.setAmount(request.amount());
        }
        if (request.teamType() != null) {
            event.setTeamType(EventTeamType.valueOf(request.teamType()));
            if (request.teamType().equals("TEAM") && request.maxTeamSize() < request.minTeamSize()) {
                throw new InvalidRequestException("Team events require at least minimum 2 or higher.");
            } else if (request.teamType().equals("TEAM")) {
                event.setMinMember(request.minTeamSize());
                event.setMaxMember(request.maxTeamSize());
            } else {
                event.setMinMember(0);
                event.setMaxMember(0);
            }
        }
        if (request.eventType() != null) {
            event.setEventType(EventType.valueOf(request.eventType()));
        }
        log.info("updateEvent()-> event updated! {}", event);
        event = eventRepository.save(event);
        eventCacheRepo.save(event);
        log.info("updateEvent()-> Event Saved! {}", event.getId());
        return event;
    }

    @Override
    @Transactional
    public Event deleteEvent(String id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event id: " + id + "does not exist!"));

        soloRegistrationRepository.deleteByEvent(event);
        teamRegistrationRepository.deleteByEvent(event);

        eventRepository.delete(event);
        eventCacheRepo.delete(event);
        log.info("deleteEvent()-> Event deleted! {}", event);
        return event;
    }

    @Override
    public List<EventResponse> getAllEvents(String eventType, String status) {

        Optional<List<Event>> cachedEvents = eventCacheRepo.findAll();
        List<Event> events = cachedEvents.orElseGet(() -> {
            List<Event> fetchedEvents = eventRepository.findAll();
            eventCacheRepo.saveAll(fetchedEvents);
            return fetchedEvents;
        });

        if (eventType != null && status != null) {
            return eventMapper.toEventResponseList(
                    events.stream()
                            .filter(event ->
                                    event.getEventType().name().equals(eventType) && event.getStatus().name().equals(status))
                            .toList()
            );
        } else if (eventType != null) {
            return eventMapper.toEventResponseList(
                    events.stream()
                            .filter(event -> event.getEventType().name().equals(eventType))
                            .toList()
            );
        } else if (status != null) {
            return eventMapper.toEventResponseList(
                    events.stream()
                            .filter(event -> event.getStatus().name().equals(status))
                            .toList()
            );
        } else {
            return eventMapper.toEventResponseList(events);
        }
    }

    @Override
    public Event getEventById(String id) {
        Optional<Event> cachedEvent = eventCacheRepo.findById(id);

        return cachedEvent.orElseGet(() -> eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event id: " + id + " does not exist!")));
    }

    @Override
    @Cacheable(value = "eventPictures", key = "#id")
    public byte[] getEventImage(String id) {
        Event event = getEventById(id);
        log.debug("getEventImage()-> Getting image from file system. {}", event.getPathToImage());
        return imageService.getImage(event.getPathToImage());
    }

    @Override
    @Transactional
    public String updateEventImage(String id, MultipartFile file) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event id: " + id + "does not exist!"));
        String imagePath = imageService.saveImage(file, id);
        event.setPathToImage(imagePath);
        eventRepository.save(event);
        eventCacheRepo.save(event);
        log.info("updateEventImage()-> Event Image Saved! Event ID: {}, Path: {} ", event.getId(), imagePath);
        return imagePath;
    }

    @Override
    @Transactional
    public void increaseRegistrationCount(String id) {
        eventRepository.findById(id).ifPresent(event -> {
            event.setCurrentRegistration(event.getCurrentRegistration() + 1);
            eventRepository.save(event);
            eventCacheRepo.save(event);
        });
    }
}
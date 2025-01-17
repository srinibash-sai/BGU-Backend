package com.anup.bgu.event.repo;

import com.anup.bgu.event.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, String> {
}

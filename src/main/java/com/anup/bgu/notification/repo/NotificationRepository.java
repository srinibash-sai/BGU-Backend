package com.anup.bgu.notification.repo;

import com.anup.bgu.notification.entities.NotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationHistory, Long> {
}

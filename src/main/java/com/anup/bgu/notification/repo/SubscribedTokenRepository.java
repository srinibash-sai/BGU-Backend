package com.anup.bgu.notification.repo;

import com.anup.bgu.notification.entities.SubscribedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscribedTokenRepository extends JpaRepository<SubscribedToken, Long> {
    boolean existsByToken(String token);
}

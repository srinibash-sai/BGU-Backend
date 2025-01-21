package com.anup.bgu.registration.repo.impl;

import com.anup.bgu.registration.entities.SoloRegistration;
import com.anup.bgu.registration.entities.TeamRegistration;
import com.anup.bgu.registration.repo.NonBguRegistrationCacheRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NonBguRegistrationCacheRepoImpl implements NonBguRegistrationCacheRepo {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String HASH_KEY_SOLO = "Non_BGU_SOLO";
    private final String HASH_KEY_TEAM = "Non_BGU_TEAM";
    private final Duration timeToLive = Duration.ofMinutes(5);

    @Override
    public SoloRegistration save(SoloRegistration soloRegistration) {
        redisTemplate.opsForHash().put(HASH_KEY_SOLO, soloRegistration.getId(), soloRegistration);
        redisTemplate.expire(HASH_KEY_SOLO, timeToLive);

        return soloRegistration;
    }

    @Override
    public TeamRegistration save(TeamRegistration teamRegistration) {
        redisTemplate.opsForHash().put(HASH_KEY_TEAM, teamRegistration.getId(), teamRegistration);
        redisTemplate.expire(HASH_KEY_TEAM, timeToLive);

        return teamRegistration;
    }

    @Override
    public Optional<SoloRegistration> findSoloRegistrationById(String id) {
        Object o = redisTemplate.opsForHash().get(HASH_KEY_SOLO, id);
        if(o==null)
        {
            return Optional.empty();
        }
        return Optional.of((SoloRegistration) o);
    }

    @Override
    public Optional<TeamRegistration> findTeamRegistrationById(String id) {
        Object o = redisTemplate.opsForHash().get(HASH_KEY_SOLO, id);
        if(o==null)
        {
            return Optional.empty();
        }
        return Optional.of((TeamRegistration) o);
    }
}

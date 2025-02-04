package com.anup.bgu.config;

import com.anup.bgu.mail.service.EmailService;
import com.anup.bgu.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfigurations {

    @Bean
    RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues();
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(configuration)
                .build();
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory factory,
            StringRedisSerializer keySerializer,
            JdkSerializationRedisSerializer valueSerializer
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);

        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    JdkSerializationRedisSerializer valueSerializer() {
        return new JdkSerializationRedisSerializer();
    }

    @Bean
    StringRedisSerializer keySerializer() {
        return new StringRedisSerializer();
    }


    @Bean
    MessageListenerAdapter mailMessageListenerAdapter(EmailService emailService) {
        return new MessageListenerAdapter(emailService);
    }

    @Bean
    MessageListenerAdapter notificationMessageListenerAdapter(NotificationService notificationService) {
        return new MessageListenerAdapter(notificationService);
    }

    @Bean
    RedisMessageListenerContainer container(
            RedisConnectionFactory factory,
            @Qualifier("mailMessageListenerAdapter")
            MessageListenerAdapter mailMessageListenerAdapter,
            @Qualifier("notificationMessageListenerAdapter")
            MessageListenerAdapter notificationMessageListenerAdapter,
            @Qualifier("redisMailTaskExecutor")
            TaskExecutor taskExecutor
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(mailMessageListenerAdapter, ChannelTopic.of("mail"));
        container.addMessageListener(notificationMessageListenerAdapter, ChannelTopic.of("notification"));
        container.setTaskExecutor(taskExecutor);
        return container;
    }
}

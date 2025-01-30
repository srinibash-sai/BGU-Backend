package com.anup.bgu.config.email;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@EnableConfigurationProperties(EmailProperties.class)
@ConfigurationProperties("email-id")
@Getter
@Setter
public class EmailProperties {
    private List<EmailAccount> accounts;

    @Getter
    @Setter
    public static class EmailAccount {
        private String email;
        private String password;
    }
}

package com.anup.bgu.config;

import com.anup.bgu.invitation.repo.InvitedEmailRepository;
import com.anup.bgu.noticeboard.repo.NoticeRepository;
import com.anup.bgu.admin.repo.UserRepository;
import com.anup.bgu.event.repo.EventRepository;
import com.anup.bgu.feedback.repo.FeedbackRepository;
import com.anup.bgu.notification.repo.NotificationRepository;
import com.anup.bgu.notification.repo.SubscribedTokenRepository;
import com.anup.bgu.payments.repo.PaymentRepository;
import com.anup.bgu.registration.repo.SoloRegistrationRepository;
import com.anup.bgu.registration.repo.TeamRegistrationRepository;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.anup.bgu",
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                EventRepository.class,
                PaymentRepository.class,
                SoloRegistrationRepository.class,
                TeamRegistrationRepository.class,
                NotificationRepository.class,
                FeedbackRepository.class,
                NoticeRepository.class,
                UserRepository.class,
                InvitedEmailRepository.class,
                SubscribedTokenRepository.class
        })
)
@EnableJpaAuditing
@EnableTransactionManagement
public class RepositoryConfigurations {
}

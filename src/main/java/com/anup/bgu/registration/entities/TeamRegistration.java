package com.anup.bgu.registration.entities;

import com.anup.bgu.event.entities.Event;
import com.anup.bgu.payments.entities.Payment;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TeamRegistration {
    @Id
    private String id;

    @Column(nullable = false, length = 200)
    private String leaderName;

    @Column(nullable = false, length = 200)
    private String teamName;

    @NotNull
    @Email
    private String email;

    @Column(length = 10, nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    private StudentType studentType;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne
    private Event event;

    @OneToMany(mappedBy = "teamRegistration", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TeamMember> teamMembers;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private Instant registrationDate;

    @OneToOne(optional = true)
    @JoinColumn(nullable = true)
    private Payment payment;
}

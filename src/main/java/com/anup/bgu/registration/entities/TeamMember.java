package com.anup.bgu.registration.entities;

import com.anup.bgu.event.entities.Event;
import jakarta.persistence.Entity;
import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TeamMember implements Serializable {
    @Id
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @NotNull
    @Email
    private String email;

    @ManyToOne
    @JoinColumn(name = "team_registration_id")
    private TeamRegistration teamRegistration;
}

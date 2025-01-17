package com.anup.bgu.event.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Event {
    @Id
    private String id;
    @Column(length = 200)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String pathToImage;

    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(columnDefinition = "TEXT")
    private String rules;
    private String dateTime;
    private int amount;
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    private String coordinatorName;
    private String coordinatorNumber;

    @Enumerated(EnumType.STRING)
    private EventTeamType teamType;
    private int maxMember;
    private int minMember;

    private int currentRegistration;
}

package com.anup.bgu.invitation.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvitedEmail {
    @Id
    private String id;

    @Column(nullable = false, length = 100)
    private String email;

    private Boolean isConsumed;
}

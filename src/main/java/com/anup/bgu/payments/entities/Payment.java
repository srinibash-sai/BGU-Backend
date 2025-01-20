package com.anup.bgu.payments.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Payment {
    @Id
    private String id;
    @Column(length = 40, unique = true)
    private String transactionId;

    private String pathToScreenshot;
    private Integer amount;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private Instant createdAt;

}

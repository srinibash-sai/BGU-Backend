package com.anup.bgu.Noticeboard.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor@Getter
@Setter
@Builder
public class Notice implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 500,nullable = false)
    private String notice;

    @Column(nullable = false)
    private LocalDateTime expire;
}

package com.anup.bgu.Noticeboard.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@ToString
@AllArgsConstructor
@NoArgsConstructor@Getter
@Setter
@Builder
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String notice;

    private String expire;

}

package com.anup.bgu.registration.entities;


import com.anup.bgu.event.entities.Event;
import com.anup.bgu.payments.entities.Payment;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SoloRegistration {
    @Id
    private String id;
    @ManyToOne
    private Event event;
    @OneToOne
    private Payment payment;
}

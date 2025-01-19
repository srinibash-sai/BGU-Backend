package com.anup.bgu.payments.repo;

import com.anup.bgu.payments.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {

}

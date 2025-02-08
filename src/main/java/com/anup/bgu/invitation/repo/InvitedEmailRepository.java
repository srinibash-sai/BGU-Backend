package com.anup.bgu.invitation.repo;

import com.anup.bgu.invitation.entities.InvitedEmail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitedEmailRepository extends JpaRepository<InvitedEmail, String> {
}
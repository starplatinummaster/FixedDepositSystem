package com.DigitalFixedDepositSystem.repository;

import com.DigitalFixedDepositSystem.model.SupportMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SupportMessageRepository extends JpaRepository<SupportMessage, UUID> {
    List<SupportMessage> findByTicketIdOrderBySentAtAsc(UUID ticketId);
}
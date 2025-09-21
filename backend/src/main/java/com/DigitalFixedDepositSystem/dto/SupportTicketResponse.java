package com.DigitalFixedDepositSystem.dto;

import com.DigitalFixedDepositSystem.model.SupportTicket;

import java.time.LocalDateTime;
import java.util.UUID;

public record SupportTicketResponse(
        UUID id,
        UUID userId,
        UUID fdId,
        String subject,
        String description,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static SupportTicketResponse fromEntity(SupportTicket ticket) {
        return new SupportTicketResponse(
                ticket.getId(),
                ticket.getUserId(),
                ticket.getFdId(),
                ticket.getSubject(),
                ticket.getDescription(),
                String.valueOf(ticket.getStatus()),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }
}
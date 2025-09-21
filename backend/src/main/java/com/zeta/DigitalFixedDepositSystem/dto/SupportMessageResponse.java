package com.zeta.DigitalFixedDepositSystem.dto;

import com.zeta.DigitalFixedDepositSystem.model.SupportMessage;

import java.time.LocalDateTime;
import java.util.UUID;

public record SupportMessageResponse(
        UUID id,
        UUID senderId,
        String senderName,
        String senderRole,
        String message,
        LocalDateTime createdAt
) {
    public static SupportMessageResponse fromEntity(SupportMessage message) {
        return new SupportMessageResponse(
                message.getId(),
                message.getSenderId(),
                "", // senderName - will be populated separately if needed
                message.getSenderRole(),
                message.getMessage(),
                message.getSentAt() != null ? message.getSentAt() : message.getCreatedAt()
        );
    }
}
package com.zeta.DigitalFixedDepositSystem.service;

import com.zeta.DigitalFixedDepositSystem.dto.*;
import com.zeta.DigitalFixedDepositSystem.model.enums.SupportTicketStatus;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public interface SupportTicketService {
    CompletionStage<SupportTicketResponse> createTicket(SupportTicketRequest request);
    CompletionStage<List<SupportTicketResponse>> getUserTickets();
    CompletionStage<List<SupportMessageResponse>> getTicketMessages(UUID ticketId);
    CompletionStage<SupportMessageResponse> addMessage(UUID ticketId, SupportMessageRequest request);
    CompletionStage<List<SupportTicketResponse>> getAllTickets();
    CompletionStage<List<SupportMessageResponse>> getTicketMessagesAdmin(UUID ticketId);
    CompletionStage<SupportMessageResponse> addMessageAdmin(UUID ticketId, SupportMessageRequest request);
    CompletionStage<SupportTicketResponse> updateTicketStatus(UUID ticketId, SupportTicketStatus status);
}
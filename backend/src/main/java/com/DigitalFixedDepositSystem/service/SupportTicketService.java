package com.DigitalFixedDepositSystem.service;

import com.DigitalFixedDepositSystem.dto.SupportMessageRequest;
import com.DigitalFixedDepositSystem.dto.SupportMessageResponse;
import com.DigitalFixedDepositSystem.dto.SupportTicketRequest;
import com.DigitalFixedDepositSystem.dto.SupportTicketResponse;
import com.zeta.DigitalFixedDepositSystem.dto.*;
import com.DigitalFixedDepositSystem.model.enums.SupportTicketStatus;

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
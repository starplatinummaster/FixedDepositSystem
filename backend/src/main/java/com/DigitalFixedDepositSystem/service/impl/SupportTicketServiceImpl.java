package com.DigitalFixedDepositSystem.service.impl;

import com.DigitalFixedDepositSystem.dto.SupportMessageRequest;
import com.DigitalFixedDepositSystem.dto.SupportMessageResponse;
import com.DigitalFixedDepositSystem.dto.SupportTicketRequest;
import com.DigitalFixedDepositSystem.dto.SupportTicketResponse;
import com.DigitalFixedDepositSystem.model.SupportMessage;
import com.DigitalFixedDepositSystem.model.SupportTicket;
import com.DigitalFixedDepositSystem.repository.SupportMessageRepository;
import com.DigitalFixedDepositSystem.repository.SupportTicketRepository;
import com.DigitalFixedDepositSystem.repository.UserRepository;
import com.zeta.DigitalFixedDepositSystem.dto.*;
import com.zeta.DigitalFixedDepositSystem.model.*;
import com.DigitalFixedDepositSystem.model.enums.SupportTicketStatus;
import com.zeta.DigitalFixedDepositSystem.repository.*;
import com.DigitalFixedDepositSystem.service.AuthService;
import com.DigitalFixedDepositSystem.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
public class SupportTicketServiceImpl implements SupportTicketService {
    
    private final SupportTicketRepository supportTicketRepository;
    private final SupportMessageRepository supportMessageRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final Executor asyncExecutor;
    
    @Override
    public CompletionStage<SupportTicketResponse> createTicket(SupportTicketRequest request) {
        return authService.getCurrentUser()
                .thenApplyAsync(user -> performCreateTicket(user.id(), request), asyncExecutor);
    }

    @Override
    public CompletionStage<List<SupportTicketResponse>> getUserTickets() {
        return authService.getCurrentUser()
                .thenApplyAsync(user -> performGetUserTickets(user.id()), asyncExecutor);
    }

    @Transactional
    public SupportTicketResponse performCreateTicket(UUID userId, SupportTicketRequest request) {
        SupportTicket ticket = SupportTicket.builder()
                .userId(userId)
                .fdId(request.getFdId())
                .subject(request.getSubject())
                .description(request.getDescription())
                .status(SupportTicketStatus.OPEN)
                .build();

        SupportTicket savedTicket = supportTicketRepository.save(ticket);
        return SupportTicketResponse.fromEntity(savedTicket);
    }
    
    @Transactional(readOnly = true)
    public List<SupportTicketResponse> performGetUserTickets(UUID userId) {
        List<SupportTicket> tickets = supportTicketRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return tickets.stream()
                .map(SupportTicketResponse::fromEntity)
                .toList();
    }
    
    @Override
    public CompletionStage<List<SupportMessageResponse>> getTicketMessages(UUID ticketId) {
        return authService.getCurrentUser()
                .thenApplyAsync(user -> performGetTicketMessages(ticketId, user.id()), asyncExecutor);
    }
    
    @Override
    public CompletionStage<SupportMessageResponse> addMessage(UUID ticketId, SupportMessageRequest request) {
        return authService.getCurrentUser()
                .thenApplyAsync(user -> performAddMessage(ticketId, user.id(), request), asyncExecutor);
    }
    
    @Transactional(readOnly = true)
    public List<SupportMessageResponse> performGetTicketMessages(UUID ticketId, UUID userId) {
        // Verify user has access to this ticket
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        if (!ticket.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        return supportMessageRepository.findByTicketIdOrderBySentAtAsc(ticketId)
                .stream()
                .map(SupportMessageResponse::fromEntity)
                .toList();
    }
    
    @Transactional
    public SupportMessageResponse performAddMessage(UUID ticketId, UUID userId, SupportMessageRequest request) {
        // Verify user has access to this ticket
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        if (!ticket.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        SupportMessage message = SupportMessage.builder()
                .ticketId(ticketId)
                .senderId(userId)
                .message(request.getMessage())
                .senderRole("USER")
                .sentAt(LocalDateTime.now())
                .build();
        
        SupportMessage savedMessage = supportMessageRepository.save(message);
        return SupportMessageResponse.fromEntity(savedMessage);
    }
    
    @Override
    public CompletionStage<List<SupportTicketResponse>> getAllTickets() {
        return authService.getCurrentUser()
                .thenApplyAsync(user -> performGetAllTickets(user.id()), asyncExecutor);
    }
    
    @Override
    public CompletionStage<List<SupportMessageResponse>> getTicketMessagesAdmin(UUID ticketId) {
        return authService.getCurrentUser()
                .thenApplyAsync(user -> performGetTicketMessagesAdmin(ticketId, user.id()), asyncExecutor);
    }
    
    @Override
    public CompletionStage<SupportMessageResponse> addMessageAdmin(UUID ticketId, SupportMessageRequest request) {
        return authService.getCurrentUser()
                .thenApplyAsync(user -> performAddMessageAdmin(ticketId, user.id(), request), asyncExecutor);
    }
    
    @Transactional(readOnly = true)
    public List<SupportTicketResponse> performGetAllTickets(UUID userId) {
        // Check if user is admin
        if (!isAdmin(userId)) {
            throw new RuntimeException("Access denied - Admin role required");
        }
        
        List<SupportTicket> tickets = supportTicketRepository.findAllByOrderByCreatedAtDesc();
        return tickets.stream()
                .map(SupportTicketResponse::fromEntity)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<SupportMessageResponse> performGetTicketMessagesAdmin(UUID ticketId, UUID userId) {
        // Check if user is admin
        if (!isAdmin(userId)) {
            throw new RuntimeException("Access denied - Admin role required");
        }
        
        return supportMessageRepository.findByTicketIdOrderBySentAtAsc(ticketId)
                .stream()
                .map(SupportMessageResponse::fromEntity)
                .toList();
    }
    
    @Transactional
    public SupportMessageResponse performAddMessageAdmin(UUID ticketId, UUID userId, SupportMessageRequest request) {
        // Check if user is admin
        if (!isAdmin(userId)) {
            throw new RuntimeException("Access denied - Admin role required");
        }
        
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        SupportMessage message = SupportMessage.builder()
                .ticketId(ticketId)
                .senderId(userId)
                .message(request.getMessage())
                .senderRole("ADMIN")
                .sentAt(LocalDateTime.now())
                .build();
        
        SupportMessage savedMessage = supportMessageRepository.save(message);
        return SupportMessageResponse.fromEntity(savedMessage);
    }
    
    @Override
    public CompletionStage<SupportTicketResponse> updateTicketStatus(UUID ticketId, SupportTicketStatus status) {
        return authService.getCurrentUser()
                .thenApplyAsync(user -> performUpdateTicketStatus(ticketId, user.id(), status), asyncExecutor);
    }
    
    @Transactional
    public SupportTicketResponse performUpdateTicketStatus(UUID ticketId, UUID userId, SupportTicketStatus status) {
        // Check if user is admin
        if (!isAdmin(userId)) {
            throw new RuntimeException("Access denied - Admin role required");
        }
        
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        ticket.setStatus(status);
        SupportTicket savedTicket = supportTicketRepository.save(ticket);
        return SupportTicketResponse.fromEntity(savedTicket);
    }
    
    private boolean isAdmin(UUID userId) {
        return userRepository.findById(userId)
                .map(user -> "ADMIN".equals(user.getRole()))
                .orElse(false);
    }
}
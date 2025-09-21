package com.zeta.DigitalFixedDepositSystem.controller;

import com.zeta.DigitalFixedDepositSystem.dto.*;
import com.zeta.DigitalFixedDepositSystem.model.enums.SupportTicketStatus;
import com.zeta.DigitalFixedDepositSystem.service.SupportTicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportTicketController {
    
    private final SupportTicketService supportTicketService;

    @PostMapping("")
    public CompletionStage<ResponseEntity<SupportTicketResponse>> createTicket(@Valid @RequestBody SupportTicketRequest request) {
        System.out.println("Creating support ticket: " + request);
        return supportTicketService.createTicket(request)
                .thenApply(ResponseEntity::ok);
    }
    
    @GetMapping("")
    public CompletionStage<ResponseEntity<List<SupportTicketResponse>>> getUserTickets() {
        return supportTicketService.getUserTickets()
                .thenApply(ResponseEntity::ok);
    }
    
    @GetMapping("/{ticketId}/messages")
    public CompletionStage<ResponseEntity<List<SupportMessageResponse>>> getTicketMessages(@PathVariable UUID ticketId) {
        return supportTicketService.getTicketMessages(ticketId)
                .thenApply(ResponseEntity::ok);
    }
    
    @PostMapping("/{ticketId}/messages")
    public CompletionStage<ResponseEntity<SupportMessageResponse>> addMessage(
            @PathVariable UUID ticketId,
            @Valid @RequestBody SupportMessageRequest request) {
        return supportTicketService.addMessage(ticketId, request)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/tickets")
    @PreAuthorize("hasRole('ADMIN')")
    public CompletionStage<ResponseEntity<List<SupportTicketResponse>>> getAllTickets() {
        return supportTicketService.getAllTickets()
                .thenApply(ResponseEntity::ok);
    }
    
    @GetMapping("/admin/{ticketId}/messages")
    public CompletionStage<ResponseEntity<List<SupportMessageResponse>>> getTicketMessagesAdmin(@PathVariable UUID ticketId) {
        return supportTicketService.getTicketMessagesAdmin(ticketId)
                .thenApply(ResponseEntity::ok);
    }
    
    @PostMapping("/admin/{ticketId}/messages")
    public CompletionStage<ResponseEntity<SupportMessageResponse>> addMessageAdmin(
            @PathVariable UUID ticketId,
            @Valid @RequestBody SupportMessageRequest request) {
        return supportTicketService.addMessageAdmin(ticketId, request)
                .thenApply(ResponseEntity::ok);
    }
    
    @PutMapping("/admin/{ticketId}/status")
    public CompletionStage<ResponseEntity<SupportTicketResponse>> updateTicketStatus(
            @PathVariable UUID ticketId,
            @RequestParam SupportTicketStatus status) {
        return supportTicketService.updateTicketStatus(ticketId, status)
                .thenApply(ResponseEntity::ok);
    }
}
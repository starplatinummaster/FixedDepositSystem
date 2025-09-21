package com.zeta.DigitalFixedDepositSystem.controller;

import com.zeta.DigitalFixedDepositSystem.dto.*;
import com.zeta.DigitalFixedDepositSystem.model.enums.SupportTicketStatus;
import com.zeta.DigitalFixedDepositSystem.service.SupportTicketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupportTicketControllerTest {

    @Mock
    private SupportTicketService supportTicketService;

    @InjectMocks
    private SupportTicketController supportTicketController;

    @Test
    void testCreateTicket() {
        // Arrange
        SupportTicketRequest request = new SupportTicketRequest();
        request.setSubject("Test Subject");
        request.setDescription("Test Description");

        SupportTicketResponse response = new SupportTicketResponse(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Test Subject",
                "Test Description",
                "OPEN",
                null,
                null
        );

        when(supportTicketService.createTicket(any(SupportTicketRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        // Act
        var result = supportTicketController.createTicket(request);
        var actualResponse = result.toCompletableFuture().join();

        // Assert
        assertNotNull(actualResponse);
        assertEquals(200, actualResponse.getStatusCode().value());
        assertEquals("Test Subject", actualResponse.getBody().subject());
    }

    @Test
    void testGetUserTickets() {
        // Arrange
        SupportTicketResponse response = new SupportTicketResponse(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Test Subject",
                "Test Description",
                "OPEN",
                null,
                null
        );

        when(supportTicketService.getUserTickets())
                .thenReturn(CompletableFuture.completedFuture(Arrays.asList(response)));

        // Act
        var result = supportTicketController.getUserTickets();
        var actualResponse = result.toCompletableFuture().join();

        // Assert
        assertNotNull(actualResponse);
        assertEquals(200, actualResponse.getStatusCode().value());
        assertEquals(1, actualResponse.getBody().size());
    }

    @Test
    void testUserMessaging() {
        // Arrange - Get Messages
        UUID ticketId = UUID.randomUUID();
        SupportMessageResponse messageResponse = new SupportMessageResponse(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "John Doe",
                "USER",
                "Test message",
                LocalDateTime.now()
        );

        when(supportTicketService.getTicketMessages(eq(ticketId)))
                .thenReturn(CompletableFuture.completedFuture(Arrays.asList(messageResponse)));

        // Act & Assert - Get Messages
        var getResult = supportTicketController.getTicketMessages(ticketId);
        var getResponse = getResult.toCompletableFuture().join();
        assertEquals(200, getResponse.getStatusCode().value());
        assertEquals("Test message", getResponse.getBody().get(0).message());

        // Arrange - Add Message
        SupportMessageRequest request = new SupportMessageRequest();
        request.setMessage("User reply");
        SupportMessageResponse addResponse = new SupportMessageResponse(
                UUID.randomUUID(), UUID.randomUUID(), "John Doe", "USER", "User reply", LocalDateTime.now()
        );
        when(supportTicketService.addMessage(eq(ticketId), any(SupportMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(addResponse));

        // Act & Assert - Add Message
        var addResult = supportTicketController.addMessage(ticketId, request);
        var addActualResponse = addResult.toCompletableFuture().join();
        assertEquals(200, addActualResponse.getStatusCode().value());
        assertEquals("User reply", addActualResponse.getBody().message());
    }

    @Test
    void testAdminViewAllTickets() {
        // Arrange
        SupportTicketResponse response1 = new SupportTicketResponse(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "User Issue", "Description", "OPEN", null, null
        );
        when(supportTicketService.getAllTickets())
                .thenReturn(CompletableFuture.completedFuture(Arrays.asList(response1)));

        // Act & Assert
        var result = supportTicketController.getAllTickets();
        var actualResponse = result.toCompletableFuture().join();
        assertEquals(200, actualResponse.getStatusCode().value());
        assertEquals(1, actualResponse.getBody().size());
    }

    @Test
    void testAdminMessaging() {
        // Arrange
        UUID ticketId = UUID.randomUUID();
        SupportMessageResponse adminMessage = new SupportMessageResponse(
                UUID.randomUUID(), UUID.randomUUID(), "Admin", "ADMIN", "Admin response", LocalDateTime.now()
        );

        when(supportTicketService.getTicketMessagesAdmin(eq(ticketId)))
                .thenReturn(CompletableFuture.completedFuture(Arrays.asList(adminMessage)));
        
        SupportMessageRequest request = new SupportMessageRequest();
        request.setMessage("Admin reply");
        when(supportTicketService.addMessageAdmin(eq(ticketId), any(SupportMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(adminMessage));

        // Act & Assert - Get Messages
        var getResult = supportTicketController.getTicketMessagesAdmin(ticketId);
        assertEquals(200, getResult.toCompletableFuture().join().getStatusCode().value());
        
        // Act & Assert - Add Message
        var addResult = supportTicketController.addMessageAdmin(ticketId, request);
        assertEquals("ADMIN", addResult.toCompletableFuture().join().getBody().senderRole());
    }

    @Test
    void testAdminUpdateTicketStatus() {
        // Arrange
        UUID ticketId = UUID.randomUUID();
        SupportTicketResponse response = new SupportTicketResponse(
                ticketId, UUID.randomUUID(), UUID.randomUUID(),
                "Test Subject", "Test Description", "RESOLVED", null, null
        );
        when(supportTicketService.updateTicketStatus(eq(ticketId), eq(SupportTicketStatus.RESOLVED)))
                .thenReturn(CompletableFuture.completedFuture(response));

        // Act & Assert
        var result = supportTicketController.updateTicketStatus(ticketId, SupportTicketStatus.RESOLVED);
        var actualResponse = result.toCompletableFuture().join();
        assertEquals(200, actualResponse.getStatusCode().value());
        assertEquals("RESOLVED", actualResponse.getBody().status());
    }
}
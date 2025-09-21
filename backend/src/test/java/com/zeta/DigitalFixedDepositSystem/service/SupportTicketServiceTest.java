package com.zeta.DigitalFixedDepositSystem.service;

import com.zeta.DigitalFixedDepositSystem.dto.*;
import com.zeta.DigitalFixedDepositSystem.model.*;
import com.zeta.DigitalFixedDepositSystem.model.enums.SupportTicketStatus;
import com.zeta.DigitalFixedDepositSystem.repository.*;
import com.zeta.DigitalFixedDepositSystem.service.impl.SupportTicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SupportTicketServiceTest {

    @Mock
    private SupportTicketRepository supportTicketRepository;
    
    @Mock
    private SupportMessageRepository supportMessageRepository;
    
    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @Mock
    private Executor asyncExecutor;

    @InjectMocks
    private SupportTicketServiceImpl supportTicketService;

    private UUID userId;
    private UUID adminUserId;
    private UUID fdId;
    private UUID ticketId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        adminUserId = UUID.randomUUID();
        fdId = UUID.randomUUID();
        ticketId = UUID.randomUUID();
    }

    @Test
    void testCreateTicket() {
        // Arrange
        SupportTicketRequest request = new SupportTicketRequest();
        request.setSubject("Test Subject");
        request.setDescription("Test Description");
        request.setFdId(fdId);

        SupportTicket savedTicket = SupportTicket.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .fdId(fdId)
                .subject("Test Subject")
                .description("Test Description")
                .status(SupportTicketStatus.OPEN)
                .build();

        when(supportTicketRepository.save(any(SupportTicket.class))).thenReturn(savedTicket);

        // Act
        SupportTicketResponse response = supportTicketService.performCreateTicket(userId, request);

        // Assert
        assertNotNull(response);
        assertEquals("Test Subject", response.subject());
        assertEquals("Test Description", response.description());
        assertEquals("OPEN", response.status());
        assertEquals(userId, response.userId());
        assertEquals(fdId, response.fdId());
        verify(supportTicketRepository, times(1)).save(any(SupportTicket.class));
    }

    @Test
    void testGetUserTickets() {
        // Arrange
        SupportTicket ticket1 = SupportTicket.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .subject("Subject 1")
                .description("Description 1")
                .status(SupportTicketStatus.OPEN)
                .build();

        SupportTicket ticket2 = SupportTicket.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .subject("Subject 2")
                .description("Description 2")
                .status(SupportTicketStatus.RESOLVED)
                .build();

        List<SupportTicket> tickets = Arrays.asList(ticket1, ticket2);
        when(supportTicketRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(tickets);

        // Act
        List<SupportTicketResponse> responses = supportTicketService.performGetUserTickets(userId);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Subject 1", responses.get(0).subject());
        assertEquals("Subject 2", responses.get(1).subject());
        verify(supportTicketRepository, times(1)).findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    void testUserMessaging_Success() {
        // Arrange
        SupportTicket ticket = SupportTicket.builder()
                .id(ticketId).userId(userId).subject("Test Subject").build();
        SupportMessage message = SupportMessage.builder()
                .id(UUID.randomUUID()).ticketId(ticketId).senderId(userId)
                .message("User message").senderRole("USER").sentAt(LocalDateTime.now()).build();
        
        when(supportTicketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(supportMessageRepository.findByTicketIdOrderBySentAtAsc(ticketId))
                .thenReturn(Arrays.asList(message));
        when(supportMessageRepository.save(any(SupportMessage.class))).thenReturn(message);

        // Act & Assert - Get Messages
        List<SupportMessageResponse> responses = supportTicketService.performGetTicketMessages(ticketId, userId);
        assertEquals(1, responses.size());
        assertEquals("User message", responses.get(0).message());

        // Act & Assert - Add Message
        SupportMessageRequest request = new SupportMessageRequest();
        request.setMessage("User reply");
        SupportMessageResponse addResponse = supportTicketService.performAddMessage(ticketId, userId, request);
        assertEquals("USER", addResponse.senderRole());
    }

    @Test
    void testUserMessaging_AccessDenied() {
        // Arrange
        SupportTicket ticket = SupportTicket.builder()
                .id(ticketId).userId(UUID.randomUUID()).subject("Test Subject").build();
        when(supportTicketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            supportTicketService.performGetTicketMessages(ticketId, userId);
        });
        assertEquals("Access denied", exception.getMessage());
    }

    @Test
    void testAdminGetAllTickets_Success() {
        // Arrange
        User adminUser = User.builder().id(adminUserId).role("ADMIN").build();
        SupportTicket ticket = SupportTicket.builder()
                .id(UUID.randomUUID()).userId(userId).subject("User Issue")
                .status(SupportTicketStatus.OPEN).build();
        
        when(userRepository.findById(adminUserId)).thenReturn(Optional.of(adminUser));
        when(supportTicketRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(Arrays.asList(ticket));

        // Act & Assert
        List<SupportTicketResponse> responses = supportTicketService.performGetAllTickets(adminUserId);
        assertEquals(1, responses.size());
        assertEquals("User Issue", responses.get(0).subject());
    }

    @Test
    void testAdminAccess_Denied() {
        // Arrange
        User regularUser = User.builder().id(userId).role("USER").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(regularUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            supportTicketService.performGetAllTickets(userId);
        });
        assertEquals("Access denied - Admin role required", exception.getMessage());
    }

    @Test
    void testAdminMessaging() {
        // Arrange
        User adminUser = User.builder().id(adminUserId).role("ADMIN").build();
        SupportTicket ticket = SupportTicket.builder().id(ticketId).userId(userId).subject("Test").build();
        SupportMessage message = SupportMessage.builder()
                .id(UUID.randomUUID()).ticketId(ticketId).senderId(adminUserId)
                .message("Admin response").senderRole("ADMIN").sentAt(LocalDateTime.now()).build();

        when(userRepository.findById(adminUserId)).thenReturn(Optional.of(adminUser));
        when(supportTicketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(supportMessageRepository.findByTicketIdOrderBySentAtAsc(ticketId))
                .thenReturn(Arrays.asList(message));
        when(supportMessageRepository.save(any(SupportMessage.class))).thenReturn(message);

        // Act & Assert - Get Messages
        List<SupportMessageResponse> responses = supportTicketService.performGetTicketMessagesAdmin(ticketId, adminUserId);
        assertEquals(1, responses.size());
        
        // Act & Assert - Add Message
        SupportMessageRequest request = new SupportMessageRequest();
        request.setMessage("Admin response");
        SupportMessageResponse addResponse = supportTicketService.performAddMessageAdmin(ticketId, adminUserId, request);
        assertEquals("ADMIN", addResponse.senderRole());
    }

    @Test
    void testAdminUpdateTicketStatus() {
        // Arrange
        User adminUser = User.builder().id(adminUserId).role("ADMIN").build();
        SupportTicket ticket = SupportTicket.builder()
                .id(ticketId).userId(userId).subject("Test").status(SupportTicketStatus.OPEN).build();
        SupportTicket updatedTicket = SupportTicket.builder()
                .id(ticketId).userId(userId).subject("Test").status(SupportTicketStatus.RESOLVED).build();

        when(userRepository.findById(adminUserId)).thenReturn(Optional.of(adminUser));
        when(supportTicketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(supportTicketRepository.save(any(SupportTicket.class))).thenReturn(updatedTicket);

        // Act & Assert
        SupportTicketResponse response = supportTicketService.performUpdateTicketStatus(ticketId, adminUserId, SupportTicketStatus.RESOLVED);
        assertEquals("RESOLVED", response.status());
        verify(supportTicketRepository, times(1)).save(any(SupportTicket.class));
    }

    @Test
    void testTicketNotFound() {
        // Arrange
        when(supportTicketRepository.findById(ticketId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            supportTicketService.performGetTicketMessages(ticketId, userId);
        });
        assertEquals("Ticket not found", exception.getMessage());
    }
}
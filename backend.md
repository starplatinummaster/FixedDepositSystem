# Backend - Digital Fixed Deposit System

## Overview
Spring Boot 3.5.4 application providing REST APIs for a digital fixed deposit banking system. Built with Java 17, PostgreSQL, JWT authentication, and comprehensive testing.

## Technology Stack
- **Framework**: Spring Boot 3.5.4
- **Language**: Java 17
- **Database**: PostgreSQL
- **Security**: Spring Security + JWT
- **ORM**: Spring Data JPA + Hibernate
- **Migration**: Flyway
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito
- **Code Coverage**: JaCoCo
- **Development**: Spring Boot DevTools, Lombok

## Project Structure

### 📁 Main Application
- **DigitalFixedDepositSystemApplication.java**: Main Spring Boot application class with `@EnableAsync` for asynchronous processing

### 📁 Configuration (`config/`)
Configuration classes for application-wide settings:

- **AllowedFixedDepositSchemes.java**: Defines valid FD schemes and their parameters
- **FixedDepositRateConfig.java**: Interest rate configurations for different tenures

### 📁 Controllers (`controller/`)
REST API endpoints handling HTTP requests:

- **AuthController.java**: Authentication endpoints (`/api/auth`)
  - POST `/login` - User authentication
  - POST `/register` - User registration
  - GET `/profile` - User profile retrieval

- **FixedDepositController.java**: FD management endpoints (`/api/fd`)
  - POST `/` - Create new fixed deposit
  - GET `/` - Get user's all fixed deposits
  - GET `/active` - Get user's active fixed deposits
  - GET `/{fdId}/interest` - Calculate accrued interest
  - PUT `/{fdId}/status` - Update FD status
  - GET `/{fdId}/break-preview` - Preview FD break calculation
  - POST `/{fdId}/break` - Execute FD break

- **HealthCheckController.java**: System health monitoring
  - GET `/health` - Application health status

- **SupportTicketController.java**: Customer support system (`/api/support`)
  - POST `/tickets` - Create support ticket
  - GET `/tickets` - Get user tickets
  - POST `/tickets/{ticketId}/messages` - Add message to ticket
  - GET `/tickets/{ticketId}/messages` - Get ticket messages

### 📁 DTOs (`dto/`)
Data Transfer Objects for API request/response:

**Authentication DTOs:**
- **AuthRequest.java**: Login credentials
- **AuthResponse.java**: JWT token response
- **RegisterRequest.java**: User registration data
- **UserProfileResponse.java**: User profile information

**Fixed Deposit DTOs:**
- **FixedDepositBookingRequest.java**: FD creation request
- **FixedDepositResponse.java**: Basic FD information
- **FixedDepositDetailsResponse.java**: Detailed FD information
- **InterestResponse.java**: Interest calculation response
- **UpdateFdStatusRequest.java**: Status update request
- **FDBreakRequest.java**: FD break request
- **FDBreakResponse.java**: FD break calculation response
- **RateAndMonth.java**: Rate and tenure mapping

**Support DTOs:**
- **SupportTicketRequest.java**: Ticket creation request
- **SupportTicketResponse.java**: Ticket information
- **SupportMessageRequest.java**: Message creation request
- **SupportMessageResponse.java**: Message information

### 📁 Exception Handling (`exception/`)
Custom exceptions and global error handling:

- **GlobalExceptionHandler.java**: Centralized exception handling with `@ControllerAdvice`
- **FDCantBeBrokenException.java**: FD break validation errors
- **FixedDepositNotFoundException.java**: FD not found errors
- **InvalidBreakDateException.java**: Invalid break date errors
- **InvalidCredentialsException.java**: Authentication errors
- **ResourceNotFoundException.java**: Generic resource not found
- **UnauthorizedAccessException.java**: Authorization errors
- **UserAlreadyExistsException.java**: User registration conflicts
- **UserNotFoundException.java**: User lookup errors

### 📁 Models (`model/`)
JPA entities representing database tables:

**Core Entities:**
- **User.java**: User account information with roles
- **FixedDeposit.java**: Fixed deposit records with interest calculations
- **FDBreak.java**: Fixed deposit break/premature closure records
- **SupportTicket.java**: Customer support tickets
- **SupportMessage.java**: Messages within support tickets

**Enums:**
- **FixedDepositStatus.java**: ACTIVE, MATURED, BROKEN
- **FDBreakStatus.java**: PENDING, APPROVED, REJECTED
- **SupportTicketStatus.java**: OPEN, IN_PROGRESS, RESOLVED, CLOSED

### 📁 Repositories (`repository/`)
Spring Data JPA repositories for database operations:

- **UserRepository.java**: User CRUD operations with custom queries
- **FixedDepositRepository.java**: FD operations with status filtering
- **FDBreakRepository.java**: FD break record management
- **SupportTicketRepository.java**: Support ticket queries
- **SupportMessageRepository.java**: Message retrieval operations

### 📁 Security (`security/`)
JWT-based authentication and authorization:

- **SecurityConfig.java**: Spring Security configuration with JWT filter chain
- **JWTAuthenticationFilter.java**: JWT token validation filter
- **JWTUtil.java**: JWT token generation and validation utilities
- **CustomerDetailsService.java**: Custom UserDetailsService implementation

### 📁 Services (`service/`)
Business logic layer with interface-implementation pattern:

**Service Interfaces:**
- **AuthService.java**: Authentication operations
- **FixedDepositService.java**: FD business logic
- **SupportTicketService.java**: Support system operations
- **UserService.java**: User management operations

**Service Implementations (`impl/`):**
- **AuthServiceImpl.java**: JWT authentication, user registration/login
- **FixedDepositServiceImpl.java**: FD creation, interest calculation, break processing
- **SupportTicketServiceImpl.java**: Ticket management, message handling
- **UserServiceImpl.java**: User profile management

### 📁 Utils (`utils/`)
Utility classes and constants:

- **AppConstants.java**: Application-wide constants
- **FDBreakMapper.java**: Mapping between FDBreak entity and DTO

### 📁 Database (`resources/db/migration/`)
Flyway migration scripts:

- **V1.0.1__initial_schema.sql**: Database schema creation
- **V1.0.2__indexes.sql**: Performance optimization indexes
- **V1.0.3__seed_data.sql**: Initial data seeding

## Key Features

### 🔐 Authentication & Security
- JWT-based stateless authentication
- Password encryption with BCrypt
- Role-based access control
- CORS configuration for frontend integration

### 💰 Fixed Deposit Management
- Multiple FD schemes with different interest rates
- Automatic interest calculation based on tenure
- Premature break with penalty calculation
- Status tracking (Active, Matured, Broken)

### 🎯 Asynchronous Processing
- Non-blocking API responses using CompletionStage
- Improved performance for database operations
- Better user experience with faster response times

### 📊 Support System
- Multi-level support ticket system
- Real-time message threading
- Ticket status management
- User-admin communication

## API Documentation

### Authentication Endpoints
```
POST /api/auth/login
POST /api/auth/register
GET /api/auth/profile
```

### Fixed Deposit Endpoints
```
POST /api/fd                    # Create FD
GET /api/fd                     # Get all user FDs
GET /api/fd/active              # Get active FDs
GET /api/fd/{id}/interest       # Calculate interest
PUT /api/fd/{id}/status         # Update status
GET /api/fd/{id}/break-preview  # Preview break
POST /api/fd/{id}/break         # Execute break
```

### Support Endpoints
```
POST /api/support/tickets
GET /api/support/tickets
POST /api/support/tickets/{id}/messages
GET /api/support/tickets/{id}/messages
```

## Configuration

### Database Configuration
```properties
spring.datasource.url=jdbc:postgresql://db:5432/fd_db
spring.datasource.username=fd_user
spring.datasource.password=fd_pass
spring.jpa.hibernate.ddl-auto=update
```

### JWT Configuration
```properties
jwt.secret=supersecretkeysupersecretkeysupersecretkey12
jwt.expiration=86400000  # 24 hours
```

## Architecture Patterns

### Layered Architecture
- **Controller Layer**: HTTP request handling
- **Service Layer**: Business logic
- **Repository Layer**: Data access
- **Model Layer**: Data representation

### Dependency Injection
- Constructor injection with Lombok's `@RequiredArgsConstructor`
- Interface-based service design for testability

### Exception Handling
- Global exception handler with `@ControllerAdvice`
- Custom exceptions for specific business scenarios
- Consistent error response format

### Asynchronous Processing
- CompletionStage for non-blocking operations
- `@EnableAsync` for background processing
- Improved scalability and performance
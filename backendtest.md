# Backend Testing Documentation

## Overview
Comprehensive testing strategy using JUnit 5, Mockito, and Spring Boot Test. Covers unit tests, integration tests, and container-based testing with high code coverage.

## Testing Framework Stack
- **JUnit 5**: Core testing framework
- **Mockito**: Mocking framework for unit tests
- **Spring Boot Test**: Integration testing support
- **Spring Security Test**: Security layer testing
- **JaCoCo**: Code coverage analysis
- **Testcontainers**: Container-based integration testing

## Test Structure

### 📁 Controller Tests (`controller/`)

#### **AuthControllerTest.java**
Tests authentication endpoints with security context:

**Test Coverage:**
- `testLogin_Success()` - Valid credentials authentication
- `testLogin_InvalidCredentials()` - Invalid login handling
- `testRegister_Success()` - New user registration
- `testRegister_UserAlreadyExists()` - Duplicate user handling
- `testGetProfile_Success()` - Authenticated profile retrieval
- `testGetProfile_Unauthorized()` - Unauthenticated access

**Key Testing Patterns:**
```java
@MockMvc // Web layer testing
@WithMockUser // Security context simulation
@MockBean // Service layer mocking
```

#### **FixedDepositControllerTest.java**
Tests FD management endpoints with async operations:

**Test Coverage:**
- `testCreateFixedDeposit_Success()` - FD creation with valid data
- `testCreateFixedDeposit_InvalidData()` - Validation error handling
- `testGetUserFixedDeposits_Success()` - User FD retrieval
- `testGetActiveFixedDeposits_Success()` - Active FD filtering
- `testGetAccruedInterest_Success()` - Interest calculation
- `testUpdateStatus_Success()` - Status update operations
- `testBreakPreview_Success()` - Break calculation preview
- `testExecuteBreak_Success()` - FD break execution
- `testExecuteBreak_InvalidDate()` - Break validation errors

**Async Testing Pattern:**
```java
@Test
void testCreateFixedDeposit_Success() {
    CompletionStage<FixedDepositResponse> futureResponse = 
        CompletableFuture.completedFuture(expectedResponse);
    
    when(service.createFixedDeposit(any())).thenReturn(futureResponse);
    
    // Test async endpoint
}
```

#### **SupportTicketControllerTest.java**
Tests customer support system endpoints:

**Test Coverage:**
- `testCreateTicket_Success()` - Ticket creation
- `testGetUserTickets_Success()` - User ticket retrieval
- `testAddMessage_Success()` - Message addition
- `testGetTicketMessages_Success()` - Message retrieval
- `testCreateTicket_Unauthorized()` - Security validation
- `testAddMessage_TicketNotFound()` - Error handling

### 📁 Service Tests (`service/`)

#### **AuthServiceTest.java**
Tests authentication business logic:

**Test Coverage:**
- `testLogin_Success()` - Successful authentication flow
- `testLogin_InvalidCredentials()` - Authentication failure
- `testRegister_Success()` - User registration process
- `testRegister_UserExists()` - Duplicate user handling
- `testGenerateToken()` - JWT token generation
- `testValidateToken()` - JWT token validation

**Mocking Pattern:**
```java
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JWTUtil jwtUtil;
    @InjectMocks private AuthServiceImpl authService;
}
```

#### **FixedDepositServiceImplTest.java**
Tests FD business logic with complex calculations:

**Test Coverage:**
- `testCreateFixedDeposit_Success()` - FD creation logic
- `testCreateFixedDeposit_InvalidScheme()` - Scheme validation
- `testCalculateAccruedInterest()` - Interest calculation accuracy
- `testPreviewBreak_Success()` - Break calculation logic
- `testExecuteBreak_Success()` - Break execution process
- `testExecuteBreak_AlreadyBroken()` - Status validation
- `testUpdateStatus_Success()` - Status transition logic
- `testGetUserFixedDeposits()` - User FD filtering

**Interest Calculation Testing:**
```java
@Test
void testCalculateAccruedInterest() {
    // Given
    FixedDeposit fd = createTestFD(10000, 7.5, 365);
    
    // When
    InterestResponse response = service.getAccruedInterest(fd.getId());
    
    // Then
    assertThat(response.getAccruedInterest())
        .isEqualByComparingTo(new BigDecimal("750.00"));
}
```

#### **FixedDepositBookingServiceTest.java**
Tests FD booking workflow:

**Test Coverage:**
- `testBookingValidation()` - Input validation
- `testRateCalculation()` - Interest rate determination
- `testMaturityCalculation()` - Maturity date calculation
- `testBookingPersistence()` - Database operations

#### **SupportTicketServiceTest.java**
Tests support system business logic:

**Test Coverage:**
- `testCreateTicket_Success()` - Ticket creation workflow
- `testAddMessage_Success()` - Message addition logic
- `testGetUserTickets()` - User ticket filtering
- `testUpdateTicketStatus()` - Status management
- `testGetTicketMessages()` - Message retrieval ordering

### 📁 Integration Tests

#### **BasicContainerTest.java**
Container-based integration testing:

**Test Coverage:**
- Database connectivity testing
- Flyway migration validation
- Application context loading
- End-to-end API testing

**Container Setup:**
```java
@Testcontainers
class BasicContainerTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_pass");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```

## Testing Strategies

### 🎯 Unit Testing
- **Isolation**: Each component tested in isolation using mocks
- **Coverage**: All business logic paths covered
- **Edge Cases**: Boundary conditions and error scenarios
- **Fast Execution**: Quick feedback loop for developers

### 🔗 Integration Testing
- **Database Integration**: Real database operations with Testcontainers
- **Security Integration**: Authentication and authorization flows
- **API Integration**: End-to-end request/response testing
- **Configuration Testing**: Application properties and profiles

### 🚀 Async Testing
- **CompletionStage Testing**: Async operation validation
- **Timeout Handling**: Long-running operation testing
- **Error Propagation**: Exception handling in async flows

### 🔒 Security Testing
- **Authentication Testing**: Login/logout flows
- **Authorization Testing**: Role-based access control
- **JWT Testing**: Token generation and validation
- **CORS Testing**: Cross-origin request handling

## Test Data Management

### Test Fixtures
```java
public class TestDataFactory {
    public static User createTestUser() {
        return User.builder()
            .username("testuser")
            .email("test@example.com")
            .password("encodedPassword")
            .build();
    }
    
    public static FixedDeposit createTestFD(double amount, double rate, int days) {
        return FixedDeposit.builder()
            .principalAmount(BigDecimal.valueOf(amount))
            .interestRate(BigDecimal.valueOf(rate))
            .tenureInDays(days)
            .status(FixedDepositStatus.ACTIVE)
            .build();
    }
}
```

### Database State Management
- **@Transactional**: Automatic rollback after tests
- **@Sql**: Custom SQL scripts for test data
- **@DirtiesContext**: Context refresh for isolation

## Code Coverage

### JaCoCo Configuration
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Coverage Targets
- **Line Coverage**: >85%
- **Branch Coverage**: >80%
- **Method Coverage**: >90%
- **Class Coverage**: >95%

## Running Tests

### All Tests
```bash
mvn test
```

### Specific Test Class
```bash
mvn -Dtest=FixedDepositServiceImplTest test
```

### Integration Tests Only
```bash
mvn test -Dtest=*IntegrationTest
```

### With Coverage Report
```bash
mvn clean test jacoco:report
```

### Coverage Report Location
```
target/site/jacoco/index.html
```

## Test Profiles

### Default Profile
- In-memory H2 database
- Mock external services
- Fast execution

### Integration Profile
```bash
mvn test -Pintegration
```
- PostgreSQL Testcontainers
- Real database operations
- Full application context

## Best Practices

### 🎯 Test Naming
```java
// Pattern: methodName_scenario_expectedResult
testCreateFixedDeposit_ValidInput_ReturnsSuccess()
testExecuteBreak_AlreadyBroken_ThrowsException()
```

### 🔧 Test Structure (AAA Pattern)
```java
@Test
void testMethodName() {
    // Arrange
    TestData data = createTestData();
    when(mockService.method()).thenReturn(expectedResult);
    
    // Act
    Result result = serviceUnderTest.method(data);
    
    // Assert
    assertThat(result).isEqualTo(expectedResult);
    verify(mockService).method();
}
```

### 🚫 Test Independence
- Each test should be independent
- No shared state between tests
- Proper setup and teardown

### 📊 Assertion Quality
```java
// Good: Specific assertions
assertThat(response.getStatus()).isEqualTo(FixedDepositStatus.ACTIVE);
assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("10000.00"));

// Avoid: Generic assertions
assertThat(response).isNotNull();
```

## Continuous Integration

### GitHub Actions Integration
```yaml
- name: Run Tests
  run: mvn clean test
  
- name: Generate Coverage Report
  run: mvn jacoco:report
  
- name: Upload Coverage
  uses: codecov/codecov-action@v1
```

### Quality Gates
- All tests must pass
- Coverage thresholds must be met
- No critical security vulnerabilities
- Code quality metrics satisfied
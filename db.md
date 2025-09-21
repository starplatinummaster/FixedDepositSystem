# Database Documentation - PostgreSQL Support Ticket System

## Database Overview
- **Database**: PostgreSQL 16
- **Host**: localhost (via Docker)
- **Port**: 5432
- **Database Name**: fd_db
- **Username**: fd_user
- **Password**: fd_password

## Connection Details

### Docker Container Access
```bash
# Connect to database container
docker compose exec db psql -U fd_user -d fd_db

# Alternative connection
docker exec -it db psql -U fd_user -d fd_db
```

### External Connection (from host machine)
```bash
# Using psql client
psql -h localhost -p 5432 -U fd_user -d fd_db

# Connection URL
postgresql://fd_user:fd_password@localhost:5432/fd_db
```

## Database Schema

### Support Tickets Table Structure
```sql
CREATE TABLE support_tickets (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    fd_id UUID,                    -- Optional: links to fixed_deposits table
    subject VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Field Explanations
| Field | Type | Constraints | Purpose |
|-------|------|-------------|---------|
| `id` | UUID | PRIMARY KEY, AUTO-GENERATED | Unique ticket identifier |
| `user_id` | UUID | NOT NULL | References user who created ticket |
| `fd_id` | UUID | NULLABLE | Optional reference to specific FD |
| `subject` | VARCHAR(255) | NOT NULL | Brief ticket description |
| `description` | TEXT | NOT NULL | Detailed ticket content |
| `status` | VARCHAR(20) | NOT NULL, DEFAULT 'OPEN' | Ticket status (OPEN/RESOLVED/CLOSED) |
| `created_at` | TIMESTAMP | DEFAULT NOW() | When ticket was created |
| `updated_at` | TIMESTAMP | DEFAULT NOW() | Last modification time |

### Related Tables (from other modules)
```sql
-- Users table (Module 1)
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(10) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Fixed Deposits table (Module 2)
CREATE TABLE fixed_deposits (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    interest_rate DECIMAL(5, 2) NOT NULL,
    tenure_months INTEGER NOT NULL,
    start_date DATE NOT NULL,
    maturity_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    accrued_interest DECIMAL(15, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Database Operations

### 1. Frontend → Backend → Database Flow

#### Create Support Ticket
```
Frontend (Vue.js)
    ↓ HTTP POST /api/support
Backend (Spring Boot)
    ↓ supportTicketService.createTicket()
    ↓ supportTicketRepository.save()
    ↓ JPA/Hibernate
Database (PostgreSQL)
    ↓ INSERT INTO support_tickets
```

**Generated SQL:**
```sql
INSERT INTO support_tickets (id, user_id, fd_id, subject, description, status, created_at, updated_at) 
VALUES (uuid_generate_v4(), ?, ?, ?, ?, 'OPEN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
```

#### Get User Tickets
```
Frontend (Vue.js)
    ↓ HTTP GET /api/support/user/{userId}
Backend (Spring Boot)
    ↓ supportTicketService.getUserTickets()
    ↓ supportTicketRepository.findByUserIdOrderByCreatedAtDesc()
    ↓ JPA/Hibernate
Database (PostgreSQL)
    ↓ SELECT * FROM support_tickets WHERE user_id = ? ORDER BY created_at DESC
```

### 2. Manual Database Operations

#### Insert Sample Data
```sql
-- Insert a test user
INSERT INTO users (id, name, email, password) 
VALUES ('123e4567-e89b-12d3-a456-426614174000', 'John Doe', 'john@example.com', 'hashed_password');

-- Insert a test FD
INSERT INTO fixed_deposits (id, user_id, amount, interest_rate, tenure_months, start_date, maturity_date) 
VALUES ('123e4567-e89b-12d3-a456-426614174001', '123e4567-e89b-12d3-a456-426614174000', 10000.00, 7.5, 12, '2025-01-01', '2026-01-01');

-- Insert a support ticket
INSERT INTO support_tickets (user_id, fd_id, subject, description) 
VALUES ('123e4567-e89b-12d3-a456-426614174000', '123e4567-e89b-12d3-a456-426614174001', 'FD Interest Query', 'I want to know about my FD interest calculation');
```

#### Query Operations
```sql
-- Get all tickets for a user
SELECT * FROM support_tickets 
WHERE user_id = '123e4567-e89b-12d3-a456-426614174000' 
ORDER BY created_at DESC;

-- Get tickets with FD information
SELECT st.*, fd.amount, fd.interest_rate 
FROM support_tickets st
LEFT JOIN fixed_deposits fd ON st.fd_id = fd.id
WHERE st.user_id = '123e4567-e89b-12d3-a456-426614174000';

-- Count tickets by status
SELECT status, COUNT(*) 
FROM support_tickets 
GROUP BY status;

-- Get recent tickets (last 7 days)
SELECT * FROM support_tickets 
WHERE created_at >= NOW() - INTERVAL '7 days'
ORDER BY created_at DESC;
```

#### Update Operations
```sql
-- Update ticket status
UPDATE support_tickets 
SET status = 'RESOLVED', updated_at = CURRENT_TIMESTAMP 
WHERE id = '565c3ad1-2403-4d02-b643-16dc1bef76d9';

-- Add response to ticket (if response column exists)
UPDATE support_tickets 
SET response = 'Your FD interest is calculated monthly at 7.5% annual rate', 
    status = 'RESOLVED',
    updated_at = CURRENT_TIMESTAMP 
WHERE id = '565c3ad1-2403-4d02-b643-16dc1bef76d9';
```

## Database Triggers and Functions

### Auto-Update Timestamp Trigger
```sql
-- Function to update updated_at column
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE 'plpgsql';

-- Trigger for support_tickets table
CREATE TRIGGER update_support_tickets_updated_at
    BEFORE UPDATE ON support_tickets
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
```

## Indexes for Performance

### Existing Indexes (from V1.0.2__indexes.sql)
```sql
-- Index on user_id for faster user ticket queries
CREATE INDEX IF NOT EXISTS idx_support_tickets_user_id ON support_tickets(user_id);

-- Index on status for filtering
CREATE INDEX IF NOT EXISTS idx_support_tickets_status ON support_tickets(status);

-- Composite index for user queries with status
CREATE INDEX IF NOT EXISTS idx_support_tickets_user_status ON support_tickets(user_id, status);

-- Index on created_at for date-based queries
CREATE INDEX IF NOT EXISTS idx_support_tickets_created_at ON support_tickets(created_at DESC);
```

### Query Performance
```sql
-- Fast query (uses index)
EXPLAIN ANALYZE SELECT * FROM support_tickets WHERE user_id = '123e4567-e89b-12d3-a456-426614174000';

-- Result: Index Scan using idx_support_tickets_user_id (cost=0.15..8.17 rows=1 width=...)
```

## Database Monitoring

### Check Table Size
```sql
SELECT 
    schemaname,
    tablename,
    attname,
    n_distinct,
    correlation
FROM pg_stats 
WHERE tablename = 'support_tickets';
```

### Check Active Connections
```sql
SELECT 
    pid,
    usename,
    application_name,
    client_addr,
    state,
    query
FROM pg_stat_activity 
WHERE datname = 'fd_db';
```

### Table Statistics
```sql
SELECT 
    relname AS table_name,
    n_tup_ins AS inserts,
    n_tup_upd AS updates,
    n_tup_del AS deletes,
    n_live_tup AS live_rows,
    n_dead_tup AS dead_rows
FROM pg_stat_user_tables 
WHERE relname = 'support_tickets';
```

## Backup and Restore

### Backup Database
```bash
# Full database backup
docker compose exec db pg_dump -U fd_user fd_db > backup.sql

# Table-specific backup
docker compose exec db pg_dump -U fd_user -t support_tickets fd_db > support_tickets_backup.sql
```

### Restore Database
```bash
# Restore full database
docker compose exec -T db psql -U fd_user fd_db < backup.sql

# Restore specific table
docker compose exec -T db psql -U fd_user fd_db < support_tickets_backup.sql
```

## Data Migration (Flyway)

### Migration Files Location
```
backend/src/main/resources/db/migration/
├── V1.0.1__initial_schema.sql      # Creates all tables
├── V1.0.2__indexes.sql             # Creates indexes
└── V1.0.3__seed_data.sql           # Sample data
```

### Migration Commands
```bash
# Check migration status
docker compose exec backend mvn flyway:info

# Run migrations
docker compose exec backend mvn flyway:migrate

# Rollback (if supported)
docker compose exec backend mvn flyway:undo
```

## Connection Pooling

### HikariCP Configuration (application.properties)
```properties
# Connection pool settings
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=600000
spring.datasource.hikari.connection-timeout=20000
```

## Database Security

### User Permissions
```sql
-- Check current user permissions
SELECT * FROM information_schema.role_table_grants 
WHERE grantee = 'fd_user';

-- Grant specific permissions
GRANT SELECT, INSERT, UPDATE ON support_tickets TO fd_user;
```

### Connection Security
- Database runs in Docker network (isolated)
- Only exposed port 5432 for development
- Production should use SSL connections
- Environment variables for credentials

## Troubleshooting

### Common Issues

#### Connection Refused
```bash
# Check if container is running
docker compose ps

# Check container logs
docker compose logs db

# Restart database
docker compose restart db
```

#### Permission Denied
```sql
-- Check user exists
SELECT usename FROM pg_user WHERE usename = 'fd_user';

-- Check database exists
SELECT datname FROM pg_database WHERE datname = 'fd_db';
```

#### Table Not Found
```bash
# Check if migrations ran
docker compose logs flyway

# Run migrations manually
docker compose up flyway
```

### Useful Queries for Debugging
```sql
-- Check table exists
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public' AND table_name = 'support_tickets';

-- Check table structure
\d support_tickets

-- Check recent tickets
SELECT * FROM support_tickets ORDER BY created_at DESC LIMIT 5;

-- Check for orphaned tickets (user doesn't exist)
SELECT st.* FROM support_tickets st
LEFT JOIN users u ON st.user_id = u.id
WHERE u.id IS NULL;
```
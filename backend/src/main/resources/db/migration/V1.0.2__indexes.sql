CREATE INDEX idx_users_email ON users(email);                    -- For login queries
CREATE INDEX idx_users_role ON users(role);                      -- For admin/user filtering
CREATE INDEX idx_users_role_created_at ON users(role, created_at); -- For admin dashboard queries

-- Composite indexes for common query patterns
CREATE INDEX idx_fixed_deposits_user_id_status ON fixed_deposits(user_id, status);
CREATE INDEX idx_fixed_deposits_status_maturity ON fixed_deposits(status, maturity_date);
CREATE INDEX idx_fixed_deposits_user_id_created_at ON fixed_deposits(user_id, created_at);

-- Single column indexes for specific needs
CREATE INDEX idx_fixed_deposits_created_at ON fixed_deposits(created_at);        -- For date range reports

-- Composite indexes for common queries
CREATE INDEX idx_support_tickets_user_id_status ON support_tickets(user_id, status);
CREATE INDEX idx_support_tickets_status_created_at ON support_tickets(status, created_at);
CREATE INDEX idx_support_tickets_fd_id_status ON support_tickets(fd_id, status) WHERE fd_id IS NOT NULL;

-- Single column index for admin queries
CREATE INDEX idx_support_tickets_created_at ON support_tickets(created_at);

-- Composite indexes for support ticket messages
CREATE INDEX idx_support_ticket_messages_ticket_id_sent_at ON support_ticket_messages(ticket_id, sent_at);
CREATE INDEX idx_support_ticket_messages_sender_id ON support_ticket_messages(sender_id);
CREATE INDEX idx_support_ticket_messages_sender_role ON support_ticket_messages(sender_role);

-- For conversation threading and pagination
CREATE INDEX idx_support_ticket_messages_ticket_id ON support_ticket_messages(ticket_id);

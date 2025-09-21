-- Insert seed users with roles
INSERT INTO users (id, name, email, password, role, created_at, updated_at) VALUES
    -- Admins
    ('550e8400-e29b-41d4-a716-446655440001', 'John Admin', 'admin@fdapp.com', '$2a$10$eI2YDMuJhMPcvMBqkyG8lOYfene2j8Ht2C1gAk6sI.iYLqNxcc57m', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440004', 'Support Admin', 'support@fdapp.com', '$2a$10$eI2YDMuJhMPcvMBqkyG8lOYfene2j8Ht2C1gAk6sI.iYLqNxcc57m', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440005', 'Sarah Manager', 'manager@fdapp.com', '$2a$10$eI2YDMuJhMPcvMBqkyG8lOYfene2j8Ht2C1gAk6sI.iYLqNxcc57m', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- Regular Users
    ('550e8400-e29b-41d4-a716-446655440002', 'Jane User', 'user@fdapp.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7bFNLCLQZLYz1BLZ.cIxN1Z.WGXY1VG', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440003', 'Test Customer', 'test@fdapp.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7bFNLCLQZLYz1BLZ.cIxN1Z.WGXY1VG', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440006', 'Robert Smith', 'robert.smith@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7bFNLCLQZLYz1BLZ.cIxN1Z.WGXY1VG', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440007', 'Emily Johnson', 'emily.johnson@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7bFNLCLQZLYz1BLZ.cIxN1Z.WGXY1VG', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440008', 'Michael Brown', 'michael.brown@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7bFNLCLQZLYz1BLZ.cIxN1Z.WGXY1VG', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440009', 'Lisa Wilson', 'lisa.wilson@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7bFNLCLQZLYz1BLZ.cIxN1Z.WGXY1VG', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440010', 'David Davis', 'david.davis@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7bFNLCLQZLYz1BLZ.cIxN1Z.WGXY1VG', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440011', 'Jennifer Garcia', 'jennifer.garcia@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7bFNLCLQZLYz1BLZ.cIxN1Z.WGXY1VG', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440012', 'Christopher Miller', 'chris.miller@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7bFNLCLQZLYz1BLZ.cIxN1Z.WGXY1VG', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655449999', 'Alex Parker', 'alex.parker@email.com', '$2a$10$xw3RWGOoRSJbDLVSYQK7W.4MPcg9d1tI9N1sSplPnHJArCxmjc6TK', 'USER',CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


INSERT INTO fixed_deposits
    (id, user_id, amount, interest_rate, tenure_months, start_date, maturity_date, status, created_at, updated_at)
VALUES
    -- Jane User's FDs
    ('650e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', 100000.00, 7.500, 12, CURRENT_DATE - 90, CURRENT_DATE - 90 + INTERVAL '12 months', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '3 months', CURRENT_TIMESTAMP),
    ('650e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', 250000.00, 8.250, 24, CURRENT_DATE - 180, CURRENT_DATE - 180 + INTERVAL '24 months', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '6 months', CURRENT_TIMESTAMP),

    -- Test Customer's FD
    ('650e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440003', 50000.00, 6.750, 6, CURRENT_DATE - 60, CURRENT_DATE - 60 + INTERVAL '6 months', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '2 months', CURRENT_TIMESTAMP),

    -- Robert Smith's FDs
    ('650e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440006', 500000.00, 9.000, 36, CURRENT_DATE - 365, CURRENT_DATE - 365 + INTERVAL '36 months', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '12 months', CURRENT_TIMESTAMP),
    ('650e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440006', 75000.00, 7.250, 12, CURRENT_DATE, CURRENT_DATE + INTERVAL '12 months', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- Emily Johnson's FDs
    ('650e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440007', 300000.00, 8.500, 18, CURRENT_DATE - 120, CURRENT_DATE - 120 + INTERVAL '18 months', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '4 months', CURRENT_TIMESTAMP),
    ('650e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440007', 150000.00, 7.750, 24, CURRENT_DATE - 540, CURRENT_DATE - 540 + INTERVAL '24 months', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '18 months', CURRENT_TIMESTAMP),

    -- Michael Brown's FD
    ('650e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440008', 200000.00, 8.000, 12, CURRENT_DATE - 450, CURRENT_DATE - 450 + INTERVAL '12 months', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '15 months', CURRENT_TIMESTAMP - INTERVAL '3 months'),
    ('650e8400-e29b-41d4-a716-446655440009', '550e8400-e29b-41d4-a716-446655440008', 350000.00, 8.750, 36, CURRENT_DATE - 30, CURRENT_DATE - 30 + INTERVAL '36 months', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '1 month', CURRENT_TIMESTAMP),

    -- Lisa Wilson's FDs
    ('650e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440009', 125000.00, 7.500, 12, CURRENT_DATE - 240, CURRENT_DATE - 240 + INTERVAL '12 months', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '8 months', CURRENT_TIMESTAMP),
    ('650e8400-e29b-41d4-a716-446655440011', '550e8400-e29b-41d4-a716-446655440009', 80000.00, 6.500, 6, CURRENT_DATE - 150, CURRENT_DATE - 150 + INTERVAL '6 months', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '5 months', CURRENT_TIMESTAMP),

    -- David Davis's FD
    ('650e8400-e29b-41d4-a716-446655440012', '550e8400-e29b-41d4-a716-446655440010', 400000.00, 9.250, 48, CURRENT_DATE - 180, CURRENT_DATE - 180 + INTERVAL '48 months', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '6 months', CURRENT_TIMESTAMP),

    -- Jennifer Garcia's FDs
    ('650e8400-e29b-41d4-a716-446655440013', '550e8400-e29b-41d4-a716-446655440011', 60000.00, 6.750, 9, CURRENT_DATE - 90, CURRENT_DATE - 90 + INTERVAL '9 months', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '3 months', CURRENT_TIMESTAMP),
    ('650e8400-e29b-41d4-a716-446655440014', '550e8400-e29b-41d4-a716-446655440011', 180000.00, 8.000, 24, CURRENT_DATE - 300, CURRENT_DATE - 300 + INTERVAL '24 months', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '10 months', CURRENT_TIMESTAMP),

    -- Christopher Miller's FD
    ('650e8400-e29b-41d4-a716-446655440015', '550e8400-e29b-41d4-a716-446655440012', 90000.00, 7.000, 18, CURRENT_DATE - 30, CURRENT_DATE - 30 + INTERVAL '18 months', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '1 month', CURRENT_TIMESTAMP),

        ('650e8400-e29b-41d4-a716-446655449901', '550e8400-e29b-41d4-a716-446655449999', 100000.00, 7.500, 12, CURRENT_DATE - 30, CURRENT_DATE - 30 + INTERVAL '12 months', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '1 month', CURRENT_TIMESTAMP),
        ('650e8400-e29b-41d4-a716-446655449902', '550e8400-e29b-41d4-a716-446655449999', 200000.00, 8.000, 24, CURRENT_DATE - 400, CURRENT_DATE - 400 + INTERVAL '24 months', 'BROKEN', CURRENT_TIMESTAMP - INTERVAL '13 months', CURRENT_TIMESTAMP - INTERVAL '1 month'),
        ('650e8400-e29b-41d4-a716-446655449903', '550e8400-e29b-41d4-a716-446655449999', 150000.00, 6.750, 6, CURRENT_DATE - 90, CURRENT_DATE - 90 + INTERVAL '6 months', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '3 months', CURRENT_TIMESTAMP),
        ('650e8400-e29b-41d4-a716-446655449904', '550e8400-e29b-41d4-a716-446655449999', 50000.00, 7.250, 9, CURRENT_DATE - 300, CURRENT_DATE - 300 + INTERVAL '9 months', 'MATURED', CURRENT_TIMESTAMP - INTERVAL '9 months', CURRENT_TIMESTAMP - INTERVAL '2 months'),
        ('650e8400-e29b-41d4-a716-446655449905', '550e8400-e29b-41d4-a716-446655449999', 275000.00, 9.000, 36, CURRENT_DATE - 60, CURRENT_DATE - 60 + INTERVAL '36 months', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '2 months', CURRENT_TIMESTAMP),
        ('650e8400-e29b-41d4-a716-446655449906', '550e8400-e29b-41d4-a716-446655449999', 80000.00, 6.500, 6, CURRENT_DATE - 20, CURRENT_DATE - 20 + INTERVAL '6 months', 'MATURED', CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP),
        ('650e8400-e29b-41d4-a716-446655449907', '550e8400-e29b-41d4-a716-446655449999', 400000.00, 9.250, 48, CURRENT_DATE - 500, CURRENT_DATE - 500 + INTERVAL '48 months', 'BROKEN', CURRENT_TIMESTAMP - INTERVAL '18 months', CURRENT_TIMESTAMP - INTERVAL '1 month'),
        ('650e8400-e29b-41d4-a716-446655449908', '550e8400-e29b-41d4-a716-446655449999', 120000.00, 8.750, 18, CURRENT_DATE, CURRENT_DATE + INTERVAL '18 months', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);



-- Insert seed support tickets with variety of statuses and types
INSERT INTO support_tickets (id, user_id, fd_id, subject, description, status, created_at, updated_at) VALUES
    ('750e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440001', 'Interest calculation question', 'I have a question about how the interest is calculated on my FD', 'OPEN', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP),
    ('750e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440003', NULL, 'Account access issue', 'Cannot login to my account', 'RESOLVED', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '4 days'),
    ('750e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440006', '650e8400-e29b-41d4-a716-446655440004', 'Early withdrawal query', 'I need to withdraw my FD before maturity due to emergency. What are the penalties?', 'OPEN', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
    ('750e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440007', '650e8400-e29b-41d4-a716-446655440006', 'Interest payment schedule', 'When will I receive my interest payments? Monthly or at maturity?', 'RESOLVED', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '2 days'),
    ('750e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440008', NULL, 'Update contact information', 'I have moved to a new address and need to update my contact details', 'RESOLVED', CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP - INTERVAL '6 days'),
    ('750e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440009', '650e8400-e29b-41d4-a716-446655440010', 'Tax certificate request', 'I need TDS certificate for my fixed deposits for income tax filing', 'OPEN', CURRENT_TIMESTAMP - INTERVAL '4 hours', CURRENT_TIMESTAMP),
    ('750e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440010', '650e8400-e29b-41d4-a716-446655440012', 'Maturity renewal options', 'My FD is maturing in 6 months. What are my renewal options?', 'OPEN', CURRENT_TIMESTAMP - INTERVAL '6 hours', CURRENT_TIMESTAMP),
    ('750e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440011', NULL, 'Mobile app issues', 'The mobile app crashes when I try to view my FD details', 'RESOLVED', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 day'),
    ('750e8400-e29b-41d4-a716-446655440009', '550e8400-e29b-41d4-a716-446655440012', '650e8400-e29b-41d4-a716-446655440015', 'Interest rate inquiry', 'I see different interest rates for similar tenure. Can you explain the difference?', 'OPEN', CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP),
    ('750e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440002', NULL, 'Password reset request', 'I forgot my password and the reset email is not arriving', 'RESOLVED', CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP - INTERVAL '7 days'),
        ('750e8400-e29b-41d4-a716-446655449901', '550e8400-e29b-41d4-a716-446655449999', '650e8400-e29b-41d4-a716-446655449901',
         'Interest payout clarification', 'Need clarity if interest is paid monthly or yearly', 'OPEN', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP),
        ('750e8400-e29b-41d4-a716-446655449902', '550e8400-e29b-41d4-a716-446655449999', '650e8400-e29b-41d4-a716-446655449902',
         'Maturity withdrawal delay', 'My FD matured but amount not credited yet', 'RESOLVED', CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP - INTERVAL '10 days');



-- Insert seed support ticket messages for comprehensive conversations
INSERT INTO support_ticket_messages (id, ticket_id, sender_id, message, sent_at, sender_role, created_at, updated_at) VALUES
    -- Ticket 1 conversation (Interest calculation)
    ('850e8400-e29b-41d4-a716-446655440001', '750e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', 'I would like to understand how the interest is calculated on my fixed deposit.', CURRENT_TIMESTAMP - INTERVAL '2 days', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('850e8400-e29b-41d4-a716-446655440002', '750e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'The interest is calculated monthly based on your principal amount and the agreed interest rate. Would you like me to explain the specific calculation for your deposit?', CURRENT_TIMESTAMP - INTERVAL '2 days' + INTERVAL '30 minutes', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('850e8400-e29b-41d4-a716-446655440003', '750e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', 'Yes, please explain it for my ₹1,00,000 deposit at 7.5% for 12 months.', CURRENT_TIMESTAMP - INTERVAL '2 days' + INTERVAL '1 hour', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- Ticket 2 conversation (Account access - resolved)
    ('850e8400-e29b-41d4-a716-446655440004', '750e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440003', 'I cannot access my account. The login page shows an error.', CURRENT_TIMESTAMP - INTERVAL '5 days', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('850e8400-e29b-41d4-a716-446655440005', '750e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440004', 'I have reset your password. Please try logging in again and let me know if you still face issues.', CURRENT_TIMESTAMP - INTERVAL '5 days' + INTERVAL '2 hours', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('850e8400-e29b-41d4-a716-446655440006', '750e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440003', 'Thank you! I can now log in successfully.', CURRENT_TIMESTAMP - INTERVAL '4 days', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('850e8400-e29b-41d4-a716-446655440007', '750e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440004', 'Great! Is there anything else I can help you with?', CURRENT_TIMESTAMP - INTERVAL '4 days' + INTERVAL '15 minutes', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- Ticket 3 conversation (Early withdrawal)
    ('850e8400-e29b-41d4-a716-446655440008', '750e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440006', 'I need to withdraw my FD before maturity due to emergency. What are the penalties?', CURRENT_TIMESTAMP - INTERVAL '1 day', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('850e8400-e29b-41d4-a716-446655440009', '750e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440005', 'For premature withdrawal, there is typically a penalty of 1% on the interest rate. Let me check your specific FD terms and get back to you with exact calculations.', CURRENT_TIMESTAMP - INTERVAL '1 day' + INTERVAL '45 minutes', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- Ticket 4 conversation (Interest payment - resolved)
    ('850e8400-e29b-41d4-a716-446655440010', '750e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440007', 'When will I receive my interest payments? Monthly or at maturity?', CURRENT_TIMESTAMP - INTERVAL '3 days', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('850e8400-e29b-41d4-a716-446655440011', '750e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440001', 'For your 18-month FD, interest is compounded quarterly and paid at maturity. You will receive all accrued interest when the FD matures.', CURRENT_TIMESTAMP - INTERVAL '3 days' + INTERVAL '1 hour', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('850e8400-e29b-41d4-a716-446655440012', '750e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440007', 'Perfect! That clarifies my doubt. Thank you.', CURRENT_TIMESTAMP - INTERVAL '2 days', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- Ticket 5 conversation (Contact update - closed)
    ('850e8400-e29b-41d4-a716-446655440013', '750e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440008', 'I have moved to a new address and need to update my contact details', CURRENT_TIMESTAMP - INTERVAL '7 days', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('850e8400-e29b-41d4-a716-446655440014', '750e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440005', 'I have updated your address in our system. You will receive a confirmation email shortly.', CURRENT_TIMESTAMP - INTERVAL '7 days' + INTERVAL '2 hours', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('850e8400-e29b-41d4-a716-446655440015', '750e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440008', 'Received the confirmation. Thank you for the quick update!', CURRENT_TIMESTAMP - INTERVAL '6 days', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- Ticket 6 conversation (Tax certificate)
    ('850e8400-e29b-41d4-a716-446655440016', '750e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440009', 'I need TDS certificate for my fixed deposits for income tax filing', CURRENT_TIMESTAMP - INTERVAL '4 hours', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- Ticket 7 conversation (Maturity renewal)
    ('850e8400-e29b-41d4-a716-446655440017', '750e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440010', 'My FD is maturing in 6 months. What are my renewal options?', CURRENT_TIMESTAMP - INTERVAL '6 hours', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- Ticket 8 conversation (Mobile app - resolved)
    ('850e8400-e29b-41d4-a716-446655440018', '750e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440011', 'The mobile app crashes when I try to view my FD details', CURRENT_TIMESTAMP - INTERVAL '2 days', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('850e8400-e29b-41d4-a716-446655440019', '750e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440001', 'We have identified and fixed this issue in our latest app update. Please update your app from the store and try again.', CURRENT_TIMESTAMP - INTERVAL '2 days' + INTERVAL '3 hours', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('850e8400-e29b-41d4-a716-446655440020', '750e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440011', 'Updated the app and it works perfectly now! Thank you.', CURRENT_TIMESTAMP - INTERVAL '1 day', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- Ticket 9 conversation (Interest rate inquiry)
    ('850e8400-e29b-41d4-a716-446655440021', '750e8400-e29b-41d4-a716-446655440009', '550e8400-e29b-41d4-a716-446655440012', 'I see different interest rates for similar tenure. Can you explain the difference?', CURRENT_TIMESTAMP - INTERVAL '3 hours', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- Ticket 10 conversation (Password reset - closed)
    ('850e8400-e29b-41d4-a716-446655440022', '750e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440002', 'I forgot my password and the reset email is not arriving', CURRENT_TIMESTAMP - INTERVAL '8 days', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('850e8400-e29b-41d4-a716-446655440023', '750e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440004', 'I have manually reset your password and sent you the new temporary password via SMS. Please change it after logging in.', CURRENT_TIMESTAMP - INTERVAL '8 days' + INTERVAL '1 hour', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('850e8400-e29b-41d4-a716-446655440024', '750e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440002', 'Got it! Successfully logged in and changed my password. Thanks!', CURRENT_TIMESTAMP - INTERVAL '7 days', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('850e8400-e29b-41d4-a716-446655449901', '750e8400-e29b-41d4-a716-446655449901', '550e8400-e29b-41d4-a716-446655449999',
         'Is my ₹1,00,000 FD interest credited monthly or only at maturity?', CURRENT_TIMESTAMP - INTERVAL '3 days', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        ('850e8400-e29b-41d4-a716-446655449902', '750e8400-e29b-41d4-a716-446655449901', '550e8400-e29b-41d4-a716-446655440001',
         'Your FD is annual payout. Interest is paid at the end of each year.', CURRENT_TIMESTAMP - INTERVAL '3 days' + INTERVAL '2 hours', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

        ('850e8400-e29b-41d4-a716-446655449903', '750e8400-e29b-41d4-a716-446655449902', '550e8400-e29b-41d4-a716-446655449999',
         'My matured FD has not been credited to my bank account.', CURRENT_TIMESTAMP - INTERVAL '15 days', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        ('850e8400-e29b-41d4-a716-446655449904', '750e8400-e29b-41d4-a716-446655449902', '550e8400-e29b-41d4-a716-446655440004',
         'We have processed your request. The maturity amount has now been credited.', CURRENT_TIMESTAMP - INTERVAL '14 days', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
        ('850e8400-e29b-41d4-a716-446655449905', '750e8400-e29b-41d4-a716-446655449902', '550e8400-e29b-41d4-a716-446655449999',
         'Yes, I have received it. Thank you for resolving quickly.', CURRENT_TIMESTAMP - INTERVAL '10 days', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

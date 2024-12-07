INSERT INTO users (first_name, last_name, phone_number, email, username, password)
VALUES
('John', 'Doe', '123456789', 'john.doe@gmail.com', 'john_doe', 'password123'),
('Jane', 'Doe', '987654321', 'jane.doe@gmail.com', 'jane_doe', 'password456'),
('Alice', 'Smith', '345678901', 'alice.smith@gmail.com', 'alice_smith', 'password789'),
('Bob', 'Johnson', '456789012', 'bob.johnson@gmail.com', 'bob_johnson', 'passwordabc'),
('Emily', 'Wilson', '567890123', 'emily.wilson@gmail.com', 'emily_wilson', 'passwordxyz');

INSERT INTO accounts (account_number, account_type, base_currency, balance, user_id)
VALUES
(1234567890, 'CHECKING', 'USD', 5000.00, 1),
( 0987654321, 'SAVING', 'EUR', 10000.00, 1),
( 2345678901, 'CHECKING', 'GBP', 3000.00, 2),
(9876543210, 'SAVING', 'TRY', 7500.00, 2),
(3456789012, 'CHECKING', 'CAD', 7000.00, 3),
( 8765432109, 'SAVING', 'AUD', 12000.00, 3),
(4567890123, 'CHECKING', 'JPY', 4500.00, 4),
( 7654321098, 'SAVING', 'GBP', 8000.00, 4),
( 5678901234, 'CHECKING', 'GBP', 6000.00, 5),
( 6543210987, 'SAVING', 'TRY', 9500.00, 5);
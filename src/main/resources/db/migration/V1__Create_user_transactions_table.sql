-- Flyway migration script V1
-- Create user_transactions table

CREATE TABLE user_transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    date_time DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user_id (user_id),
    INDEX idx_date_time (date_time),
    INDEX idx_user_date (user_id, date_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertar datos de ejemplo para desarrollo
INSERT INTO user_transactions (user_id, amount, date_time) VALUES
(1, 100.50, '2024-01-15 10:30:00'),
(1, -25.75, '2024-01-15 14:20:00'),
(2, 200.00, '2024-01-16 09:15:00'),
(2, -50.25, '2024-01-16 16:45:00'),
(3, 75.30, '2024-01-17 11:00:00'),
(1, 150.00, '2024-01-18 08:30:00'),
(3, -30.00, '2024-01-18 13:15:00');

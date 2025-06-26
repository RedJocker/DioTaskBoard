CREATE TABLE IF NOT EXISTS CARD (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_blocked BOOL NOT NULL DEFAULT FALSE
);

-- INSERT INTO CARD (name, description) values (
--                          'TaskBoard',
--                          'Create a taskBoard'
--                         );
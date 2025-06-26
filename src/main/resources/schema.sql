CREATE TABLE IF NOT EXISTS Board (
    board_id LONG AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Column (
    column_id LONG AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    board_id LONG NOT NULL,
    FOREIGN KEY (board_id) references Board(board_id)
);

CREATE TABLE IF NOT EXISTS Card (
    card_id LONG AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_blocked BOOL NOT NULL DEFAULT FALSE,
    column_id Long NOT NULL,
    FOREIGN KEY (column_id) references Column(column_id)
);

-- INSERT INTO Board (name) VALUES ('TaskBoardApp');
-- INSERT INTO Column (NAME, BOARD_ID) VALUES ('initial', 1);
INSERT INTO Card (NAME, DESCRIPTION, COLUMN_ID)
    VALUES ( 'TaskBoard', 'Create a taskboard', 2);

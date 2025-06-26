CREATE TABLE IF NOT EXISTS Board (
    board_id LONG AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Column (
    column_id LONG AUTO_INCREMENT PRIMARY KEY,
    column_type VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    column_order INTEGER NOT NULL,
    board_id LONG NOT NULL,
    FOREIGN KEY (board_id) references Board(board_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Card (
    card_id LONG AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_blocked BOOL NOT NULL DEFAULT FALSE,
    column_id Long NOT NULL,
    FOREIGN KEY (column_id) references Column(column_id) ON DELETE CASCADE
);

INSERT INTO Board (name) VALUES ('TaskBoardApp');
INSERT INTO Column (name, column_type, board_id, column_order) VALUES ('initial', 'INITIAL', 1, 0);
INSERT INTO Card (name, description, column_id)
    VALUES ( 'TaskBoard', 'Create a taskboard', 1);

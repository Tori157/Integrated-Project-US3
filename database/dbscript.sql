DROP DATABASE `itb-kk`;

CREATE DATABASE `itb-kk`;
USE `itb-kk`;
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

CREATE TABLE boards
(
    boardId   VARCHAR(10)  PRIMARY KEY,
    name      VARCHAR(120) NOT NULL,
    visibility ENUM('PUBLIC', 'PRIVATE') NOT NULL DEFAULT 'PRIVATE',
    ownerId   VARCHAR(36)  NOT NULL,
    createdOn DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedOn DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_board_user_id ON boards (ownerId);

CREATE TABLE statuses
(
    statusId    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL DEFAULT 'No Status',
    description VARCHAR(200),
    createdOn   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedOn   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    boardId     VARCHAR(10) NOT NULL,
    FOREIGN KEY (boardId) REFERENCES boards (boardId)
);

CREATE INDEX idx_status_board_id ON statuses (boardId);

CREATE TABLE tasks
(
    taskId      BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    assignees   VARCHAR(30),
    statusId    BIGINT       NOT NULL,
    createdOn   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedOn   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    boardId     VARCHAR(10)  NOT NULL,
    FOREIGN KEY (statusId) REFERENCES statuses (statusId),
    FOREIGN KEY (boardId) REFERENCES boards (boardId)
);

-- SET @mock_board_id = 'Bd9g5K1w4J';
-- INSERT INTO boards (boardId, name, visibility, ownerId, createdOn, updatedOn)
-- VALUE (@mock_board_id, 'Test Board', 'PUBLIC', '2b2f94fd-68be-4ff2-8c67-cb35e139f6fb', '2024-04-22 08:50:00', '2024-04-22 08:50:00');

-- INSERT INTO statuses (name , description, createdOn, updatedOn , boardId)
-- VALUES  ('No Status', 'The default status', '2024-04-22 08:55:00', '2024-04-22 08:55:00', @mock_board_id),
-- 		('To Do', null, '2024-04-22 08:55:00', '2024-04-22 08:55:00', @mock_board_id),
-- 		('Doing', 'Being worked on', '2024-04-22 08:55:00', '2024-04-22 08:55:00', @mock_board_id),
-- 		('Done', 'Finished', '2024-04-22 08:55:00', '2024-04-22 08:55:00', @mock_board_id);

-- INSERT INTO tasks (title, description, assignees, statusId, createdOn, updatedOn, boardId)
-- VALUES ('TaskTitle1TaskTitle2TaskTitle3TaskTitle4TaskTitle5TaskTitle6TaskTitle7TaskTitle8TaskTitle9TaskTitle0',
-- 		'Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti1Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti2Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti3Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti4Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti5',
-- 		'Assignees1Assignees2Assignees3', 1, '2024-04-22 09:00:00', '2024-04-22 09:00:00', @mock_board_id),
-- 	   ('Repository',  null, null, 2, '2024-04-22 09:05:00', '2024-04-22 14:00:00', @mock_board_id),
-- 	   ('ดาต้าเบส', 'ສ້າງຖານຂໍ້ມູນ', 'あなた、彼、彼女 (私ではありません)', 3, '2024-04-22 09:10:00', '2024-04-25 00:00:00', @mock_board_id),
-- 	   ('_Infrastructure_', '_Setup containers_', 'ไก่งวง กับ เพนกวิน', 2, '2024-04-22 09:15:00', '2024-04-22 10:00:00', @mock_board_id);
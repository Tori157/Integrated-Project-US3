-- Create database
CREATE DATABASE IF NOT EXISTS `itb-kk`;

USE `itb-kk`;

-- Drop tables if needed (uncomment if necessary)
-- DROP TABLE IF EXISTS tasks;
-- DROP TABLE IF EXISTS statuses;
-- DROP TABLE IF EXISTS tasksV1;

-- Create tables
CREATE TABLE IF NOT EXISTS statuses (
  statusId INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci UNIQUE NOT NULL DEFAULT 'NO_STATUS',
  description VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS tasks (
  taskId INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  description VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  assignees VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  statusId INT,
  createdOn DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedOn DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (statusId) REFERENCES statuses(statusId)
) ENGINE = InnoDB;

-- Insert data into statuses
INSERT INTO statuses (name, description)
SELECT 'No Status', 'A status has not been assigned'
WHERE NOT EXISTS (SELECT 1 FROM statuses WHERE name = 'No Status');

INSERT INTO statuses (name, description)
SELECT 'To Do', 'The task is included in the project'
WHERE NOT EXISTS (SELECT 1 FROM statuses WHERE name = 'To Do');

INSERT INTO statuses (name, description)
SELECT 'In Progress', 'The task is being worked on'
WHERE NOT EXISTS (SELECT 1 FROM statuses WHERE name = 'In Progress');

INSERT INTO statuses (name, description)
SELECT 'Reviewing', 'The task is being reviewed'
WHERE NOT EXISTS (SELECT 1 FROM statuses WHERE name = 'Reviewing');

INSERT INTO statuses (name, description)
SELECT 'Testing', 'The task is being tested'
WHERE NOT EXISTS (SELECT 1 FROM statuses WHERE name = 'Testing');

INSERT INTO statuses (name, description)
SELECT 'Waiting', 'The task is waiting for a resource'
WHERE NOT EXISTS (SELECT 1 FROM statuses WHERE name = 'Waiting');

INSERT INTO statuses (name, description)
SELECT 'Done', 'The task has been completed'
WHERE NOT EXISTS (SELECT 1 FROM statuses WHERE name = 'Done');

-- Insert data into tasks
INSERT INTO tasks (title, description, assignees, statusId, createdOn, updatedOn)
VALUES
('NS01', '', '', 1, STR_TO_DATE('14-5-2024 09:00:00', '%d-%m-%Y %H:%i:%s'), STR_TO_DATE('14-5-2024 09:00:00', '%d-%m-%Y %H:%i:%s')),
('TD01', '', '', 2, STR_TO_DATE('14-5-2024 09:10:00', '%d-%m-%Y %H:%i:%s'), STR_TO_DATE('14-5-2024 09:10:00', '%d-%m-%Y %H:%i:%s')),
('IP01', '', '', 3, STR_TO_DATE('14-5-2024 09:20:00', '%d-%m-%Y %H:%i:%s'), STR_TO_DATE('14-5-2024 09:20:00', '%d-%m-%Y %H:%i:%s')),
('TD02', '', '', 2, STR_TO_DATE('14-5-2024 09:30:00', '%d-%m-%Y %H:%i:%s'), STR_TO_DATE('14-5-2024 09:30:00', '%d-%m-%Y %H:%i:%s')),
('DO01', '', '', 7, STR_TO_DATE('14-5-2024 09:40:00', '%d-%m-%Y %H:%i:%s'), STR_TO_DATE('14-5-2024 09:40:00', '%d-%m-%Y %H:%i:%s')),
('IP02', '', '', 3, STR_TO_DATE('14-5-2024 09:50:00', '%d-%m-%Y %H:%i:%s'), STR_TO_DATE('14-5-2024 09:50:00', '%d-%m-%Y %H:%i:%s'));

-- Select data to verify
SELECT * FROM statuses;
SELECT t.taskId, t.title, t.description, t.assignees, s.name, t.createdOn, t.updatedOn
FROM tasks t
JOIN statuses s ON t.statusId = s.statusId;

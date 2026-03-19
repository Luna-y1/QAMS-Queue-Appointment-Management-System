-- Create fresh database
CREATE DATABASE IF NOT EXISTS qams_db;
USE qams_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    userID       INT AUTO_INCREMENT PRIMARY KEY,
    fullName     VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    role         ENUM('Customer','Staff','Admin') NOT NULL DEFAULT 'Customer',
    createdAt    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Services table
CREATE TABLE IF NOT EXISTS services (
    serviceID         INT AUTO_INCREMENT PRIMARY KEY,
    serviceName       VARCHAR(255) NOT NULL,
    description       VARCHAR(255),
    estimatedDuration INT DEFAULT 15
);

-- Staff table
CREATE TABLE IF NOT EXISTS staff (
    staffID        INT AUTO_INCREMENT PRIMARY KEY,
    fullName       VARCHAR(255) NOT NULL,
    specialization VARCHAR(255),
    contactNumber  VARCHAR(50)
);

-- Appointments table
CREATE TABLE IF NOT EXISTS appointments (
    appointmentID   INT AUTO_INCREMENT PRIMARY KEY,
    patientName     VARCHAR(255) NOT NULL,
    serviceName     VARCHAR(255) NOT NULL,
    staffName       VARCHAR(255),
    appointmentDate DATE NOT NULL,
    appointmentTime TIME NOT NULL,
    status          ENUM('Scheduled','Confirmed','Completed','Cancelled') DEFAULT 'Scheduled',
    createdAt       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Queue tokens table
CREATE TABLE IF NOT EXISTS queue_tokens (
    tokenID     INT AUTO_INCREMENT PRIMARY KEY,
    tokenNumber INT NOT NULL,
    patientName VARCHAR(255) NOT NULL,
    serviceName VARCHAR(255) NOT NULL,
    issueTime   TIME NOT NULL,
    status      ENUM('Waiting','Serving','Completed','Skipped') DEFAULT 'Waiting',
    tokenDate   DATE NOT NULL DEFAULT (CURDATE())
);

-- System settings table
CREATE TABLE IF NOT EXISTS system_settings (
    settingID    INT AUTO_INCREMENT PRIMARY KEY,
    settingKey   VARCHAR(100) NOT NULL UNIQUE,
    settingValue VARCHAR(255) NOT NULL
);

-- Default settings
INSERT IGNORE INTO system_settings (settingKey, settingValue) VALUES
('orgName',          'QueueFlow Medical Center'),
('orgEmail',         'contact@queueflow.com'),
('orgPhone',         '+1 (555) 123-4567'),
('orgAddress',       '123 Healthcare Ave, Medical District'),
('avgServiceTime',   '15'),
('maxQueueSize',     '50'),
('autoAdvance',      'true'),
('priorityQueue',    'false'),
('smsNotifications', 'true'),
('emailReminders',   'true'),
('displayNotifs',    'false');

-- Default services
INSERT IGNORE INTO services (serviceName, description, estimatedDuration) VALUES
('Consultation', 'General doctor consultation', 15),
('Check-up',     'Routine health check-up',     20),
('Follow-up',    'Follow-up appointment',        10),
('Emergency',    'Emergency service',             5),
('Lab Test',     'Laboratory testing',           30);

-- Default users (admin + demo)
INSERT IGNORE INTO users (fullName, email, password, role) VALUES
('Administrator', 'admin@queueflow.com', 'admin123', 'Admin'),
('Demo User',     'demo@queueflow.com',  'demo123',  'Customer');

-- Sample staff
INSERT IGNORE INTO staff (fullName, specialization, contactNumber) VALUES
('Dr. Smith',    'General Practice', '555-0101'),
('Dr. Johnson',  'Cardiology',       '555-0102'),
('Dr. Williams', 'Pediatrics',       '555-0103');
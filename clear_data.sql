-- Disable foreign key checks to allow clearing tables in any order
SET FOREIGN_KEY_CHECKS = 0;

-- Clear all secondary data tables
-- Note: Replace table names if they differ in your database
TRUNCATE TABLE bookings;
TRUNCATE TABLE feedbacks;
TRUNCATE TABLE registrations;
TRUNCATE TABLE events;
TRUNCATE TABLE club_memberships;
TRUNCATE TABLE clubs;
TRUNCATE TABLE venues;

-- Delete all users except for ADMIN users
-- Note: 'role' column values are strings in the database
DELETE FROM users WHERE role != 'ADMIN';

-- Reset auto-increment counters for the cleared tables (optional)
ALTER TABLE bookings AUTO_INCREMENT = 1;
ALTER TABLE feedbacks AUTO_INCREMENT = 1;
ALTER TABLE registrations AUTO_INCREMENT = 1;
ALTER TABLE events AUTO_INCREMENT = 1;
ALTER TABLE club_memberships AUTO_INCREMENT = 1;
ALTER TABLE clubs AUTO_INCREMENT = 1;
ALTER TABLE venues AUTO_INCREMENT = 1;

-- Restore foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Final output: Keep the DB lean
SELECT 'Database data cleared. Only ADMIN users remain.' AS status;

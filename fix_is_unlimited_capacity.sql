-- =============================================================================
-- Database Migration: Add default value to is_unlimited_capacity column
-- =============================================================================
-- This script adds the missing default value to the is_unlimited_capacity column
-- in the venues table, which was causing INSERT statements to fail.
-- =============================================================================

-- Check if the column exists and add/modify it with a default value
ALTER TABLE venues MODIFY COLUMN is_unlimited_capacity BOOLEAN DEFAULT FALSE NOT NULL;

-- =============================================================================
-- Migration Complete
-- =============================================================================

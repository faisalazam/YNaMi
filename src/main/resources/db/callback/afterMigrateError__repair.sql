-- TODO Spring Upgrade: add tests for me

-- This script is used to automatically clean the failed entries from the flyway_schema_history after a failed migration.
-- This will automatically remove any failed entry from the Flyway state history, whenever a migration error occurs.
-- This callback will be in action when the `flyway-callback` spring profile is activated
DELETE FROM `${schema_name}`.`flyway_schema_history` WHERE success=false;
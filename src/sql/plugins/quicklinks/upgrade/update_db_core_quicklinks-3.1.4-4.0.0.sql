-- liquibase formatted sql
-- changeset quicklinks:update_db_core_quicklinks-3.1.4-4.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE core_admin_right SET icon_url='ti ti-link' WHERE id_right='QUICKLINKS_MANAGEMENT';

-- liquibase formatted sql
-- changeset quicklinks:create_core_quicklinks.sql
-- preconditions onFail:MARK_RAN onError:WARN
DROP TABLE IF EXISTS quicklinks_portlet;

--
-- Table structure for table quicklinks_portlet
--
CREATE TABLE quicklinks_portlet (
	id_portlet int default NULL,
	id_quicklinks int default NULL
);

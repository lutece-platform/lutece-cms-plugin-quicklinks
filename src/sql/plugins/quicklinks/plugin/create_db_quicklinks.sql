-- liquibase formatted sql
-- changeset quicklinks:create_db_quicklinks.sql
-- preconditions onFail:MARK_RAN onError:WARN
DROP TABLE IF EXISTS quicklinks_quicklinks;
DROP TABLE IF EXISTS quicklinks_entry;
DROP TABLE IF EXISTS quicklinks_entry_type;
DROP TABLE IF EXISTS quicklinks_entry_select;
DROP TABLE IF EXISTS quicklinks_entry_select_option;
DROP TABLE IF EXISTS quicklinks_entry_url;
DROP TABLE IF EXISTS quicklinks_entry_text;
DROP TABLE IF EXISTS quicklinks_entry_internal_link;
DROP TABLE IF EXISTS quicklinks_action;

--
-- Table structure for table quicklinks_quicklinks
--
CREATE TABLE quicklinks_quicklinks (
	id_quicklinks int default 0 NOT NULL,
	title_quicklinks varchar(255) NOT NULL,
	type_quicklinks smallint NOT NULL,
	role_key varchar(50) default '0' NOT NULL,
	workgroup_key varchar(50) default NULL,
	is_enabled smallint default NULL,
	css_style varchar(50) default NULL,
	PRIMARY KEY (id_quicklinks)
);

--
-- Table structure for table quicklinks_entry
--
CREATE TABLE quicklinks_entry (
	id_entry int default 0 NOT NULL,
	id_quicklinks int default 0 NOT NULL,
	id_type int default 0 NOT NULL,
	id_order int default NULL,
	id_parent smallint NOT NULL,
	PRIMARY KEY (id_entry)
);

CREATE INDEX quicklinks_entry_id_x ON quicklinks_entry (id_quicklinks);
CREATE INDEX quicklinks_entry_id_type_x ON quicklinks_entry (id_type);

--
-- Table structure for table quicklinks_entry_type
--
CREATE TABLE quicklinks_entry_type (
	id_entry_type int default 0 NOT NULL,
	title_key varchar(255),
	class_name varchar(255),
	template_create varchar(255),
	template_modify varchar(255),
	PRIMARY KEY (id_entry_type)
);

--
-- Table structure for table quicklinks_entry_select
--
CREATE TABLE quicklinks_entry_select (
	id_entry int default 0 NOT NULL,
	title long varchar NOT NULL,
	target varchar(255) NOT NULL,
	PRIMARY KEY (id_entry)
);

--
-- Table structure for table quicklinks_entry_select_option
--
CREATE TABLE quicklinks_entry_select_option (
	id_entry int default 0 NOT NULL,
	id_option int default 0 NOT NULL,
	option_title varchar(255) NOT NULL,
	option_url long varchar NOT NULL,
	id_order int default NULL,
	PRIMARY KEY (id_entry,id_option)  
);

--
-- Table structure for table quicklinks_entry_url
--
CREATE TABLE quicklinks_entry_url (
	id_entry int default 0 NOT NULL,
	title long varchar NOT NULL,
	target varchar(255) NOT NULL,
	description long varchar,
	url long varchar,
	image long varbinary,
	image_mime_type varchar(50) default NULL,
	display_properties int default 0 NOT NULL,
	link_properties int default 0 NOT NULL,
	PRIMARY KEY (id_entry)  
);

--
-- Table structure for table quicklinks_entry_text
--
CREATE TABLE quicklinks_entry_text (
	id_entry int default 0 NOT NULL,
	title long varchar NOT NULL,
	description long varchar NOT NULL,
	PRIMARY KEY (id_entry)
);

--
-- Table structure for table quicklinks_entry_internal_link
--
CREATE TABLE quicklinks_entry_internal_link (
	id_entry int default 0 NOT NULL,
	title long varchar NOT NULL,
	content long varchar NOT NULL,
	PRIMARY KEY (id_entry)
);

--
-- Table structure for table quicklinks_action
--
CREATE TABLE quicklinks_action (
	id_action int default 0 NOT NULL,
	name_key varchar(100) default NULL,
	description_key varchar(100) default NULL,
	action_url varchar(255) default NULL,
	icon_url varchar(255) default NULL,
	action_permission varchar(255) default NULL,
	quicklinks_state smallint default NULL,
	PRIMARY KEY (id_action)
);

ALTER TABLE quicklinks_entry ADD CONSTRAINT fk_quicklinks_entry_id_type FOREIGN KEY (id_type)
      REFERENCES quicklinks_entry_type (id_entry_type) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE quicklinks_entry ADD CONSTRAINT fk_quicklinks_entry_id FOREIGN KEY (id_quicklinks)
      REFERENCES quicklinks_quicklinks (id_quicklinks) ON DELETE RESTRICT ON UPDATE RESTRICT;

--ALTER TABLE quicklinks_entry ADD CONSTRAINT fk_quicklinks_entry_select FOREIGN KEY (id_entry)
--      REFERENCES quicklinks_entry_select (id_entry) ON DELETE RESTRICT ON UPDATE RESTRICT;

--ALTER TABLE quicklinks_entry ADD CONSTRAINT fk_quicklinks_entry_url FOREIGN KEY (id_entry)
--      REFERENCES quicklinks_entry_url (id_entry) ON DELETE RESTRICT ON UPDATE RESTRICT;

--ALTER TABLE quicklinks_entry ADD CONSTRAINT fk_quicklinks_entry_text FOREIGN KEY (id_entry)
--      REFERENCES quicklinks_entry_text (id_entry) ON DELETE RESTRICT ON UPDATE RESTRICT;

--ALTER TABLE quicklinks_entry ADD CONSTRAINT fk_quicklinks_entry_internal_link FOREIGN KEY (id_entry)
--      REFERENCES quicklinks_entry_internal_link (id_entry) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE quicklinks_entry_select_option ADD CONSTRAINT fk_quicklinks_entry_select_option FOREIGN KEY (id_entry)
      REFERENCES quicklinks_entry_select (id_entry) ON DELETE RESTRICT ON UPDATE RESTRICT;

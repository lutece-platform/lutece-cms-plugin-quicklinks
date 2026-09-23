-- liquibase formatted sql
-- changeset quicklinks:init_core_quicklinks.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Dumping data for table core_admin_right
--
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url) VALUES 
('QUICKLINKS_MANAGEMENT','quicklinks.adminFeature.quicklinks_management.name',2,'jsp/admin/plugins/quicklinks/ManageQuicklinks.jsp','quicklinks.adminFeature.quicklinks_management.description',0,'quicklinks','APPLICATIONS','ti ti-link',NULL);

--
-- Dumping data for table core_user_right
--
INSERT INTO core_user_right (id_right,id_user) VALUES ('QUICKLINKS_MANAGEMENT',1);
INSERT INTO core_user_right (id_right,id_user) VALUES ('QUICKLINKS_MANAGEMENT',2);

--
-- Dumping data for table core_admin_role
--
INSERT INTO core_admin_role (role_key,role_description) VALUES 
('quicklinks_manager','Gestion des liens rapides');

--
-- Dumping data for table core_user_role
--
INSERT INTO core_user_role (role_key,id_user) VALUES ('quicklinks_manager',1);
INSERT INTO core_user_role (role_key,id_user) VALUES ('quicklinks_manager',2);

--
-- Dumping data for table core_admin_role_resource
--
INSERT INTO core_admin_role_resource (rbac_id,role_key,resource_type,resource_id,permission) VALUES 
(901,'quicklinks_manager','QUICKLINKS_QUICKLINKS_TYPE','*','*');

--
-- Dumping data for table core_portlet_type
--
INSERT INTO core_portlet_type (id_portlet_type,name,url_creation,url_update,home_class,plugin_name,url_docreate,create_script,create_specific,create_specific_form,url_domodify,modify_script,modify_specific,modify_specific_form) VALUES 
('QUICKLINKS_PORTLET','quicklinks.portlet.name','plugins/quicklinks/CreateQuicklinksPortlet.jsp','plugins/quicklinks/ModifyQuicklinksPortlet.jsp','fr.paris.lutece.plugins.quicklinks.business.portlet.QuicklinksPortletHome','quicklinks','plugins/quicklinks/DoCreateQuicklinksPortlet.jsp','','/admin/plugins/quicklinks/list_quicklinks.html','','plugins/quicklinks/DoModifyQuicklinksPortlet.jsp','','/admin/plugins/quicklinks/list_quicklinks.html','');


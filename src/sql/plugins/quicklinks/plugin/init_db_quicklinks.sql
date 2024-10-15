--
-- Init table quicklinks_action
--
INSERT INTO quicklinks_action (id_action, name_key, description_key, action_url, icon_url, action_permission, quicklinks_state) VALUES
	(1, 'quicklinks.action.modify.name', 'quicklinks.action.modify.description', 'jsp/admin/plugins/quicklinks/ModifyQuicklinks.jsp', 'pencil', 'MODIFY', 0),
	(2, 'quicklinks.action.modify.name', 'quicklinks.action.modify.description', 'jsp/admin/plugins/quicklinks/ModifyQuicklinks.jsp', 'pencil', 'MODIFY', 1),
	(9, 'quicklinks.action.disable.name', 'quicklinks.action.disable.description', 'jsp/admin/plugins/quicklinks/ConfirmDisableQuicklinks.jsp', 'x', 'CHANGE_STATE', 1),
	(10, 'quicklinks.action.enable.name', 'quicklinks.action.enable.description', 'jsp/admin/plugins/quicklinks/DoEnableQuicklinks.jsp', 'check', 'CHANGE_STATE', 0),
	(11, 'quicklinks.action.copy.name', 'quicklinks.action.copy.description', 'jsp/admin/plugins/quicklinks/DoCopyQuicklinks.jsp', 'copy', 'COPY', 0),
	(12, 'quicklinks.action.copy.name', 'quicklinks.action.copy.description', 'jsp/admin/plugins/quicklinks/DoCopyQuicklinks.jsp', 'copy', 'COPY', 1),
	(13, 'quicklinks.action.delete.name', 'quicklinks.action.delete.description', 'jsp/admin/plugins/quicklinks/ConfirmRemoveQuicklinks.jsp', 'trash', 'DELETE', 0);

--
-- Init table quicklinks_entry_type
--
INSERT INTO quicklinks_entry_type (id_entry_type,title_key,class_name,template_create,template_modify) VALUES 
(1,'quicklinks.entryType.text.title','fr.paris.lutece.plugins.quicklinks.business.EntryText','admin/plugins/quicklinks/create_entry_text.html','admin/plugins/quicklinks/modify_entry_text.html');
INSERT INTO quicklinks_entry_type (id_entry_type,title_key,class_name,template_create,template_modify) VALUES 
(2,'quicklinks.entryType.url.title','fr.paris.lutece.plugins.quicklinks.business.EntryUrl','admin/plugins/quicklinks/create_entry_url.html','admin/plugins/quicklinks/modify_entry_url.html');
INSERT INTO quicklinks_entry_type (id_entry_type,title_key,class_name,template_create,template_modify) VALUES 
(3,'quicklinks.entryType.select.title','fr.paris.lutece.plugins.quicklinks.business.EntrySelect','admin/plugins/quicklinks/create_entry_select.html','admin/plugins/quicklinks/modify_entry_select.html');
INSERT INTO quicklinks_entry_type (id_entry_type,title_key,class_name,template_create,template_modify) VALUES 
(4,'quicklinks.entryType.internal_link.title','fr.paris.lutece.plugins.quicklinks.business.EntryInternalLink','admin/plugins/quicklinks/create_entry_internal_link.html','admin/plugins/quicklinks/modify_entry_internal_link.html');

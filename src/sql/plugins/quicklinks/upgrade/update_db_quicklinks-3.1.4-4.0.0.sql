-- liquibase formatted sql
-- changeset quicklinks:update_db_quicklinks-3.1.4-4.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE quicklinks_action SET action_url='jsp/admin/plugins/quicklinks/ManageQuicklinks.jsp?view=modifyQuicklinks' WHERE action_url='jsp/admin/plugins/quicklinks/ModifyQuicklinks.jsp';
UPDATE quicklinks_action SET action_url='jsp/admin/plugins/quicklinks/ManageQuicklinks.jsp?view=confirmDisableQuicklinks' WHERE action_url='jsp/admin/plugins/quicklinks/ConfirmDisableQuicklinks.jsp';
UPDATE quicklinks_action SET action_url='jsp/admin/plugins/quicklinks/ManageQuicklinks.jsp?view=confirmEnableQuicklinks' WHERE action_url='jsp/admin/plugins/quicklinks/DoEnableQuicklinks.jsp';
UPDATE quicklinks_action SET action_url='jsp/admin/plugins/quicklinks/ManageQuicklinks.jsp?view=confirmCopyQuicklinks' WHERE action_url='jsp/admin/plugins/quicklinks/DoCopyQuicklinks.jsp';
UPDATE quicklinks_action SET action_url='jsp/admin/plugins/quicklinks/ManageQuicklinks.jsp?view=confirmRemoveQuicklinks' WHERE action_url='jsp/admin/plugins/quicklinks/ConfirmRemoveQuicklinks.jsp';

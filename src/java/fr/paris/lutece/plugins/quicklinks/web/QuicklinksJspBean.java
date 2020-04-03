/*
 * Copyright (c) 2002-2020, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.quicklinks.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.quicklinks.business.Entry;
import fr.paris.lutece.plugins.quicklinks.business.EntryFilter;
import fr.paris.lutece.plugins.quicklinks.business.EntryHome;
import fr.paris.lutece.plugins.quicklinks.business.EntryType;
import fr.paris.lutece.plugins.quicklinks.business.EntryTypeHome;
import fr.paris.lutece.plugins.quicklinks.business.IEntry;
import fr.paris.lutece.plugins.quicklinks.business.Quicklinks;
import fr.paris.lutece.plugins.quicklinks.business.QuicklinksAction;
import fr.paris.lutece.plugins.quicklinks.business.QuicklinksActionHome;
import fr.paris.lutece.plugins.quicklinks.business.QuicklinksHome;
import fr.paris.lutece.plugins.quicklinks.business.QuicklinksType;
import fr.paris.lutece.plugins.quicklinks.business.portlet.QuicklinksPortletHome;
import fr.paris.lutece.plugins.quicklinks.service.QuicklinksResourceIdService;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.role.RoleHome;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage {@link Quicklinks} features ( manage, create, modify, remove)
 */
public class QuicklinksJspBean extends PluginAdminPageJspBean
{
    private static final long serialVersionUID = -5176913689822438398L;

    // Rights
    public static final String RIGHT_MANAGE_QUICKLINKS = "QUICKLINKS_MANAGEMENT";

    // Templates
    private static final String TEMPLATE_MANAGE = "admin/plugins/quicklinks/manage_quicklinks.html";
    private static final String TEMPLATE_CREATE = "admin/plugins/quicklinks/create_quicklinks.html";
    private static final String TEMPLATE_MODIFY = "admin/plugins/quicklinks/modify_quicklinks.html";

    // JSP URL
    private static final String JSP_URL_PREFIX = "jsp/admin/plugins/quicklinks/";
    private static final String JSP_URL_MANAGE = "ManageQuicklinks.jsp";
    private static final String JSP_URL_MODIFY = "ModifyQuicklinks.jsp";
    private static final String JSP_URL_MODIFY_ENTRY = "ModifyEntry.jsp";
    private static final String JSP_URL_DELETE_ENTRY = "DoRemoveEntry.jsp";
    private static final String JSP_URL_DELETE_QUICKLINKS = "DoRemoveQuicklinks.jsp";
    private static final String JSP_URL_DISABLE_QUICKLINKS = "DoDisableQuicklinks.jsp";

    // Properties
    private static final String PROPERTY_STYLES_PER_PAGE = "paginator.style.itemsPerPage";
    private static final String PROPERTY_TYPE_DEFAULT_VALUE = "quicklinks.create.defaultValue.type";
    private static final String PROPERTY_STATE_DEFAULT_VALUE = "quicklinks.create.defaultValue.state";
    private static final String PROPERTY_ENTRY_ORDER_DEFAULT_VALUE = "quicklinks.modify.entry.create.defaultValue.order";

    // Messages (I18n keys)
    private static final String MESSAGE_PAGE_TITLE_MANAGE = "quicklinks.manage_quicklinks.pageTitle";
    private static final String MESSAGE_LABEL_TAG = "quicklinks.manage_quicklinks.labelTag";
    private static final String MESSAGE_PAGE_TITLE_CREATE = "quicklinks.create_quicklinks.pageTitle";
    private static final String MESSAGE_PAGE_TITLE_MODIFY = "quicklinks.modify_quicklinks.pageTitle";
    private static final String MESSAGE_PAGE_TITLE_CREATE_ENTRY = "quicklinks.create_entry.pageTitle";
    private static final String MESSAGE_PAGE_TITLE_MODIFY_ENTRY = "quicklinks.modify_entry.pageTitle";
    private static final String MESSAGE_STATE_ENABLED = "quicklinks.quicklinksState.enabled";
    private static final String MESSAGE_STATE_DISABLED = "quicklinks.quicklinksState.disabled";
    private static final String MESSAGE_CONFIRMATION_REMOVE_ENTRY = "quicklinks.message.confirmRemoveEntry";
    private static final String MESSAGE_CONFIRMATION_REMOVE_QUICKLINKS = "quicklinks.message.confirmRemoveQuicklinks";
    private static final String MESSAGE_CONFIRMATION_DISABLE_QUICKLINKS = "quicklinks.message.confirmDisableQuicklinks";
    private static final String MESSAGE_STOP_CANNOT_DISABLE_QUICKLINKS = "quicklinks.message.stopCannotDisableQuicklinks";
    private static final String MESSAGE_STOP_CANNOT_MODIFY_QUICKLINKS = "quicklinks.message.stopCannotModifyQuicklinks";
    private static final String MESSAGE_STOP_CANNOT_REMOVE_QUICKLINKS = "quicklinks.message.stopCannotRemoveQuicklinks";
    private static final String MESSAGE_COPY = "quicklinks.copy.titleCopy.prefix";

    // Parameters
    private static final String PARAMETER_QUICKLINKS_ID = "quicklinks_id";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_TYPE = "type";
    private static final String PARAMETER_TYPE_ID = "entry_type_id";
    private static final String PARAMETER_NEXT_STEP = "next_step";
    private static final String PARAMETER_WORKGROUP_KEY = "workgroup_key";
    private static final String PARAMETER_ROLE_KEY = "role_key";
    private static final String PARAMETER_STATE = "state";
    private static final String PARAMETER_CSS_STYLE = "css_style";
    private static final String PARAMETER_ENTRY_ID = "entry_id";
    private static final String PARAMETER_APPLY = "apply";
    private static final String PARAMETER_CANCEL = "cancel";

    // Anchors
    private static final String ANCHOR_NAME = "entry_list";

    // Markers
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_QUICKLINKS_LIST = "quicklinks_list";
    private static final String MARK_QUICKLINKS_INCLUDE_TAG = "quicklinks_include_tag";
    private static final String MARK_QUICKLINKS = "quicklinks";
    private static final String MARK_QUICKLINKS_ID = "quicklinks_id";
    private static final String MARK_QUICKLINKS_ACTIONS = "quicklinks_actions";
    private static final String MARK_PLUGIN = "plugin";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_ENTRY = "entry";
    private static final String MARK_ENTRY_ID = "entry_id";
    private static final String MARK_PERMISSION_CREATE = "permission_create_quicklinks";
    private static final String MARK_TYPE_LIST = "type_list";
    private static final String MARK_DEFAULT_VALUE_TYPE = "type_default_value";
    private static final String MARK_ROLE_KEY_LIST = "role_key_list";
    private static final String MARK_DEFAULT_VALUE_ROLE_KEY = "role_key_default_value";
    private static final String MARK_WORKGROUP_KEY_LIST = "workgroup_key_list";
    private static final String MARK_DEFAULT_VALUE_WORKGROUP_KEY = "workgroup_key_default_value";
    private static final String MARK_DEFAULT_VALUE_STATE = "state_default_value";
    private static final String MARK_STATE_LIST = "state_list";
    private static final String MARK_ENTRY_LIST = "entry_list";
    private static final String MARK_ENTRY_TYPE_LIST = "entry_type_list";

    // Miscellaneous
    private static final String UNAUTHORIZED = "Unauthorized";
    private static final int DEFAULT_PAGINATOR_STYLES_PER_PAGE = 10;
    private static final String DEFAULT_VALUE_TYPE = "1";
    private static final String DEFAULT_VALUE_STATE = "0";
    private static final String DEFAULT_VALUE_ENTRY_ORDER = "first";
    private static final int DEFAULT_ENTRY_PARENT_ID = 0;
    private static final String REGEX_ID = "^[\\d]+$";
    private static final String EMPTY_STRING = "";
    private static final String STEP_MODIFY = "modify";
    private int _nItemsPerPage;
    private int _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;

    /**
     * Get the {@link Quicklinks} management page
     * 
     * @param request
     *            The HTTP servlet request
     * @return The HTML template
     */
    public String getManageQuicklinks( HttpServletRequest request )
    {
        HashMap<String, Object> model = new HashMap<>( );
        Collection<Quicklinks> quicklinksList = QuicklinksHome.findAll( getPlugin( ) );
        quicklinksList = AdminWorkgroupService.getAuthorizedCollection( quicklinksList, getUser( ) );
        setPageTitleProperty( MESSAGE_PAGE_TITLE_MANAGE );

        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_STYLES_PER_PAGE, DEFAULT_PAGINATOR_STYLES_PER_PAGE );
        _strCurrentPageIndex = AbstractPaginator.getPageIndex( request, AbstractPaginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = AbstractPaginator.getItemsPerPage( request, AbstractPaginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, _nDefaultItemsPerPage );

        UrlItem url = new UrlItem( JSP_URL_PREFIX + JSP_URL_MANAGE );

        Paginator paginator = new Paginator( (List<Quicklinks>) quicklinksList, _nItemsPerPage, url.getUrl( ), AbstractPaginator.PARAMETER_PAGE_INDEX,
                _strCurrentPageIndex );
        Collection<HashMap<String, Object>> listQuicklinksWithListActions = new ArrayList<>( );

        for ( Quicklinks quicklinks : (List<Quicklinks>) paginator.getPageItems( ) )
        {
            Collection<QuicklinksAction> listActions = QuicklinksActionHome.selectActionsByQuicklinksState( quicklinks.isEnabled( ), getPlugin( ),
                    getLocale( ) );
            HashMap<String, Object> modelQuicklinks = new HashMap<>( );
            modelQuicklinks.put( MARK_QUICKLINKS, quicklinks );

            if ( quicklinks.getType( ).equals( QuicklinksType.INCLUDE ) )
            {
                String strQuicklinksMarker = QuicklinksInclude.getQuicklinksMarkerPrefix( ) + String.valueOf( quicklinks.getId( ) );
                String strlabelTag = I18nService.getLocalizedString( MESSAGE_LABEL_TAG, new String [ ] {
                        strQuicklinksMarker
                }, getLocale( ) );
                modelQuicklinks.put( MARK_QUICKLINKS_INCLUDE_TAG, strlabelTag );
            }

            modelQuicklinks.put( MARK_QUICKLINKS_ACTIONS, RBACService.getAuthorizedActionsCollection( listActions, quicklinks, getUser( ) ) );
            listQuicklinksWithListActions.add( modelQuicklinks );
        }

        model.put( MARK_NB_ITEMS_PER_PAGE, String.valueOf( _nItemsPerPage ) );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_QUICKLINKS_LIST, listQuicklinksWithListActions );
        model.put( MARK_PERMISSION_CREATE,
                RBACService.isAuthorized( Quicklinks.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, QuicklinksResourceIdService.PERMISSION_CREATE, getUser( ) ) );
        model.put( MARK_PLUGIN, getPlugin( ) );

        // Get Actions list
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Get the {@link Quicklinks} creation page
     * 
     * @param request
     *            The HTTP servlet request
     * @return The HTML template
     */
    public String getCreateQuicklinks( HttpServletRequest request ) throws AccessDeniedException
    {
        Locale locale = getLocale( );

        if ( !RBACService.isAuthorized( Quicklinks.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, QuicklinksResourceIdService.PERMISSION_CREATE, getUser( ) ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        HashMap<String, Object> model = new HashMap<>( );
        setPageTitleProperty( MESSAGE_PAGE_TITLE_CREATE );

        ReferenceList listType = new ReferenceList( );

        for ( ReferenceItem item : QuicklinksType.getReferenceList( ) )
        {
            listType.addItem( item.getCode( ), I18nService.getLocalizedString( item.getName( ), locale ) );
        }

        ReferenceList listState = new ReferenceList( );
        listState.addItem( Boolean.toString( true ), I18nService.getLocalizedString( MESSAGE_STATE_ENABLED, locale ) );
        listState.addItem( Boolean.toString( false ), I18nService.getLocalizedString( MESSAGE_STATE_DISABLED, locale ) );

        model.put( MARK_PLUGIN, getPlugin( ) );
        model.put( MARK_TYPE_LIST, listType );
        model.put( MARK_DEFAULT_VALUE_TYPE, AppPropertiesService.getProperty( PROPERTY_TYPE_DEFAULT_VALUE, DEFAULT_VALUE_TYPE ) );
        model.put( MARK_DEFAULT_VALUE_WORKGROUP_KEY, AdminWorkgroupService.ALL_GROUPS );
        model.put( MARK_WORKGROUP_KEY_LIST, AdminWorkgroupService.getUserWorkgroups( getUser( ), getLocale( ) ) );
        model.put( MARK_ROLE_KEY_LIST, RoleHome.getRolesList( ) );
        model.put( MARK_DEFAULT_VALUE_ROLE_KEY, Quicklinks.ROLE_NONE );
        model.put( MARK_STATE_LIST, listState );
        model.put( MARK_DEFAULT_VALUE_STATE, AppPropertiesService.getProperty( PROPERTY_STATE_DEFAULT_VALUE, DEFAULT_VALUE_STATE ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Processes the {@link Quicklinks} creation
     * 
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     */
    public String doCreateQuicklinks( HttpServletRequest request ) throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( Quicklinks.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, QuicklinksResourceIdService.PERMISSION_CREATE, getUser( ) ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strType = request.getParameter( PARAMETER_TYPE );
        String strWorkgroupKey = request.getParameter( PARAMETER_WORKGROUP_KEY );
        String strRoleKey = request.getParameter( PARAMETER_ROLE_KEY );
        String strState = request.getParameter( PARAMETER_STATE );
        String strCssStyle = request.getParameter( PARAMETER_CSS_STYLE );

        // Check mandatory fields
        
        if ( StringUtil.isAnyEmpty( strTitle, strWorkgroupKey, strRoleKey, strState ) || ( strType == null ) || !strType.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        QuicklinksType quicklinksType = QuicklinksType.getByValue( Integer.parseInt( strType ) );

        // Check if quicklinks type exists
        if ( quicklinksType == null )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        // Set quicklinks and create it
        Quicklinks quicklinks = new Quicklinks( );
        quicklinks.setEnabled( Boolean.parseBoolean( strState ) );
        quicklinks.setRoleKey( strRoleKey );
        quicklinks.setTitle( strTitle );
        quicklinks.setType( quicklinksType );
        quicklinks.setWorkgroup( strWorkgroupKey );
        quicklinks.setCssStyle( strCssStyle );

        QuicklinksHome.create( quicklinks, getPlugin( ) );

        return JSP_URL_MANAGE;
    }

    /**
     * Get the {@link Quicklinks} modification page
     * 
     * @param request
     *            The HTTP servlet request
     * @return The HTML template
     */
    public String getModifyQuicklinks( HttpServletRequest request ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin( );
        Locale locale = getLocale( );
        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_MODIFY );
        HashMap<String, Object> model = new HashMap<>( );
        setPageTitleProperty( MESSAGE_PAGE_TITLE_MODIFY );

        // General attributes
        ReferenceList listType = new ReferenceList( );

        for ( ReferenceItem item : QuicklinksType.getReferenceList( ) )
        {
            listType.addItem( item.getCode( ), I18nService.getLocalizedString( item.getName( ), locale ) );
        }

        ReferenceList listState = new ReferenceList( );
        listState.addItem( Boolean.toString( true ), I18nService.getLocalizedString( MESSAGE_STATE_ENABLED, locale ) );
        listState.addItem( Boolean.toString( false ), I18nService.getLocalizedString( MESSAGE_STATE_DISABLED, locale ) );

        model.put( MARK_PLUGIN, plugin );
        model.put( MARK_TYPE_LIST, listType );
        model.put( MARK_QUICKLINKS, quicklinks );
        model.put( MARK_WORKGROUP_KEY_LIST, AdminWorkgroupService.getUserWorkgroups( getUser( ), getLocale( ) ) );
        model.put( MARK_ROLE_KEY_LIST, RoleHome.getRolesList( ) );
        model.put( MARK_STATE_LIST, listState );

        // ### Entry section
        // create entry box
        Collection<EntryType> listEntryType = EntryTypeHome.findAll( plugin );
        model.put( MARK_ENTRY_TYPE_LIST, listEntryType );

        // entry list
        EntryFilter filter = new EntryFilter( );
        filter.setIdQuicklinks( quicklinks.getId( ) );
        filter.setIdParent( EntryHome.ROOT_PARENT_ID );

        Collection<IEntry> listEntry = EntryHome.findByFilter( filter, plugin );

        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_STYLES_PER_PAGE, DEFAULT_PAGINATOR_STYLES_PER_PAGE );
        _strCurrentPageIndex = AbstractPaginator.getPageIndex( request, AbstractPaginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = AbstractPaginator.getItemsPerPage( request, AbstractPaginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, _nDefaultItemsPerPage );

        UrlItem url = new UrlItem( JSP_URL_PREFIX + JSP_URL_MODIFY );
        url.addParameter( PARAMETER_QUICKLINKS_ID, quicklinks.getId( ) );

        Paginator<IEntry> paginator = new Paginator( (List<IEntry>) listEntry, _nItemsPerPage, url.getUrl( ), AbstractPaginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );

        model.put( MARK_NB_ITEMS_PER_PAGE, String.valueOf( _nItemsPerPage ) );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_ENTRY_LIST, paginator.getPageItems( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Processes the {@link Quicklinks} modification
     * 
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     */
    public String doModifyQuicklinks( HttpServletRequest request ) throws AccessDeniedException
    {
        if ( request.getParameter( PARAMETER_CANCEL ) != null )
        {
            return JSP_URL_MANAGE;
        }

        Plugin plugin = getPlugin( );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strType = request.getParameter( PARAMETER_TYPE );
        String strWorkgroupKey = request.getParameter( PARAMETER_WORKGROUP_KEY );
        String strRoleKey = request.getParameter( PARAMETER_ROLE_KEY );
        String strState = request.getParameter( PARAMETER_STATE );
        String strCssStyle = request.getParameter( PARAMETER_CSS_STYLE );

        // Check mandatory fields
        if ( StringUtil.isAnyEmpty( strTitle, strWorkgroupKey, strRoleKey, strState ) || ( strType == null ) || !strType.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        QuicklinksType quicklinksType = QuicklinksType.getByValue( Integer.parseInt( strType ) );

        // Check if quicklinks type exists
        if ( quicklinksType == null )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_MODIFY );

        if ( ( Boolean.parseBoolean( strState ) != quicklinks.isEnabled( ) ) && !RBACService.isAuthorized( Quicklinks.RESOURCE_TYPE,
                String.valueOf( quicklinks.getId( ) ), QuicklinksResourceIdService.PERMISSION_CHANGE_STATE, getUser( ) ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        // Check if portlets are assigned to quicklinks
        int nCountPortlets = QuicklinksPortletHome.getCountPortletByIdQuicklinks( quicklinks.getId( ) );

        if ( ( nCountPortlets > 0 ) && ( ( Boolean.parseBoolean( strState ) != quicklinks.isEnabled( ) ) || ( quicklinksType != quicklinks.getType( ) ) ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_STOP_CANNOT_MODIFY_QUICKLINKS, new String [ ] {
                    String.valueOf( nCountPortlets )
            }, AdminMessage.TYPE_STOP );
        }

        // Set quicklinks
        quicklinks.setEnabled( Boolean.parseBoolean( strState ) );
        quicklinks.setRoleKey( strRoleKey );
        quicklinks.setTitle( strTitle );
        quicklinks.setType( quicklinksType );
        quicklinks.setWorkgroup( strWorkgroupKey );
        quicklinks.setCssStyle( strCssStyle );
        QuicklinksHome.update( quicklinks, plugin );

        UrlItem url = null;

        if ( request.getParameter( PARAMETER_APPLY ) != null )
        {
            url = new UrlItem( JSP_URL_MODIFY );
            url.addParameter( PARAMETER_QUICKLINKS_ID, quicklinks.getId( ) );
        }
        else
        {
            url = new UrlItem( JSP_URL_MANAGE );
        }

        return url.getUrl( );
    }

    /**
     * Processes the {@link Quicklinks} deletion
     * 
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     */
    public String doRemoveQuicklinks( HttpServletRequest request ) throws AccessDeniedException
    {
        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_DELETE );

        // Check if portlets are assigned to quicklinks
        int nCountPortlets = QuicklinksPortletHome.getCountPortletByIdQuicklinks( quicklinks.getId( ) );

        if ( ( nCountPortlets > 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_STOP_CANNOT_REMOVE_QUICKLINKS, new String [ ] {
                    String.valueOf( nCountPortlets )
            }, AdminMessage.TYPE_STOP );
        }

        QuicklinksHome.remove( quicklinks.getId( ), getPlugin( ) );

        UrlItem url = new UrlItem( JSP_URL_MANAGE );

        return url.getUrl( );
    }

    /**
     * Get the {@link Quicklinks} removal message
     * 
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     */
    public String doConfirmRemoveQuicklinks( HttpServletRequest request ) throws AccessDeniedException
    {
        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_DELETE );

        // Check if portlets are assigned to quicklinks
        int nCountPortlets = QuicklinksPortletHome.getCountPortletByIdQuicklinks( quicklinks.getId( ) );

        if ( ( nCountPortlets > 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_STOP_CANNOT_REMOVE_QUICKLINKS, new String [ ] {
                    String.valueOf( nCountPortlets )
            }, AdminMessage.TYPE_STOP );
        }

        HashMap<String, Object> model = new HashMap<>( );
        model.put( MARK_QUICKLINKS_ID, String.valueOf( quicklinks.getId( ) ) );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRMATION_REMOVE_QUICKLINKS, JSP_URL_PREFIX + JSP_URL_DELETE_QUICKLINKS,
                AdminMessage.TYPE_QUESTION, model );
    }

    /**
     * Get the authorized Quicklinks, filtered by workgroup
     *
     * @param request
     *            The {@link HttpServletRequest}
     * @param strPermissionType
     *            The type of permission (see {@link QuicklinksResourceIdService} class)
     * @return The quicklinks or null if user have no access
     */
    private Quicklinks getAuthorizedQuicklinks( HttpServletRequest request, String strPermissionType ) throws AccessDeniedException
    {
        String strIdQuicklinks = request.getParameter( PARAMETER_QUICKLINKS_ID );

        if ( ( strIdQuicklinks == null ) || !strIdQuicklinks.matches( REGEX_ID ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        int nIdQuicklinks = Integer.parseInt( strIdQuicklinks );
        Quicklinks quicklinks = QuicklinksHome.findByPrimaryKey( nIdQuicklinks, getPlugin( ) );

        if ( ( quicklinks == null ) || !AdminWorkgroupService.isAuthorized( quicklinks, getUser( ) )
                || !RBACService.isAuthorized( Quicklinks.RESOURCE_TYPE, String.valueOf( quicklinks.getId( ) ), strPermissionType, getUser( ) ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        return quicklinks;
    }

    /**
     * Move the entry down
     * 
     * @param request
     *            The Http servlet request
     * @return The redirect url
     */
    public String doGoDownEntry( HttpServletRequest request ) throws AccessDeniedException
    {
        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_MODIFY );

        String strIdEntry = request.getParameter( PARAMETER_ENTRY_ID );
        int nIdEntry = Integer.parseInt( strIdEntry );
        EntryHome.goDown( nIdEntry, getPlugin( ) );

        UrlItem url = new UrlItem( JSP_URL_MODIFY );
        url.addParameter( PARAMETER_QUICKLINKS_ID, quicklinks.getId( ) );
        url.setAnchor( ANCHOR_NAME );

        return url.getUrl( );
    }

    /**
     * Move the entry up
     * 
     * @param request
     *            The Http servlet request
     * @return The redirect url
     */
    public String doGoUpEntry( HttpServletRequest request ) throws AccessDeniedException
    {
        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_MODIFY );

        String strIdEntry = request.getParameter( PARAMETER_ENTRY_ID );
        int nIdEntry = Integer.parseInt( strIdEntry );
        EntryHome.goUp( nIdEntry, getPlugin( ) );

        UrlItem url = new UrlItem( JSP_URL_MODIFY );
        url.addParameter( PARAMETER_QUICKLINKS_ID, quicklinks.getId( ) );
        url.setAnchor( ANCHOR_NAME );

        return url.getUrl( );
    }

    /**
     * Move the entry in a parent entry
     * 
     * @param request
     *            The Http servlet request
     * @return The redirect url
     */
    public String doGoInEntry( HttpServletRequest request ) throws AccessDeniedException
    {
        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_MODIFY );

        UrlItem url = new UrlItem( JSP_URL_MODIFY );
        url.addParameter( PARAMETER_QUICKLINKS_ID, quicklinks.getId( ) );
        url.setAnchor( ANCHOR_NAME );

        String strIdEntry = request.getParameter( PARAMETER_ENTRY_ID );
        int nIdEntry = Integer.parseInt( strIdEntry );
        Entry entry = (Entry) EntryHome.findByPrimaryKey( nIdEntry, getPlugin( ) );

        if ( entry == null )
        {
            return url.getUrl( );
        }

        EntryHome.goIn( nIdEntry, getPlugin( ) );

        return url.getUrl( );
    }

    /**
     * Move the entry out a parent entry
     * 
     * @param request
     *            The Http servlet request
     * @return The redirect url
     */
    public String doGoOutEntry( HttpServletRequest request ) throws AccessDeniedException
    {
        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_MODIFY );

        UrlItem url = new UrlItem( JSP_URL_MODIFY );
        url.addParameter( PARAMETER_QUICKLINKS_ID, quicklinks.getId( ) );
        url.setAnchor( ANCHOR_NAME );

        String strIdEntry = request.getParameter( PARAMETER_ENTRY_ID );
        int nIdEntry = Integer.parseInt( strIdEntry );
        EntryHome.goOut( nIdEntry, getPlugin( ) );

        Entry entry = (Entry) EntryHome.findByPrimaryKey( nIdEntry, getPlugin( ) );

        if ( entry == null )
        {
            return url.getUrl( );
        }

        return url.getUrl( );
    }

    /**
     * Get the {@link Entry} creation page
     * 
     * @param request
     *            The HTTP servlet request
     * @return The HTML template
     */
    public String getCreateEntry( HttpServletRequest request ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin( );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strIdType = request.getParameter( PARAMETER_TYPE_ID );
        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_MODIFY );

        if ( ( strIdType == null ) || !strIdType.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nIdType = Integer.parseInt( strIdType );
        EntryType entryType = EntryTypeHome.findByPrimaryKey( nIdType, plugin );

        if ( ( entryType == null ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        HashMap<String, Object> model = new HashMap<>( );
        setPageTitleProperty( MESSAGE_PAGE_TITLE_CREATE_ENTRY );

        IEntry entry = EntryHome.getSpecificEntry( entryType, plugin );
        entry.setIdParent( DEFAULT_ENTRY_PARENT_ID );
        entry.setIdQuicklinks( quicklinks.getId( ) );
        entry.setTitle( strTitle );
        entry.setEntryType( entryType );

        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, getLocale( ) );
        model.put( MARK_PLUGIN, plugin );
        model.put( MARK_ENTRY, entry );
        // Add the specifics markers into model
        entry.getSpecificParameters( request, model, plugin );

        HtmlTemplate template = AppTemplateService.getTemplate( entryType.getTemplateCreate( ), getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Processes the {@link Entry} creation
     * 
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     */
    public String doCreateEntry( HttpServletRequest request ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin( );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strIdType = request.getParameter( PARAMETER_TYPE_ID );
        String strNextStep = request.getParameter( PARAMETER_NEXT_STEP );

        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_MODIFY );

        if ( ( strIdType == null ) || !strIdType.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nIdType = Integer.parseInt( strIdType );
        EntryType entryType = EntryTypeHome.findByPrimaryKey( nIdType, plugin );

        if ( ( entryType == null ) || StringUtils.isEmpty( strTitle ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        IEntry entry = EntryHome.getSpecificEntry( entryType, plugin );
        entry.setIdParent( DEFAULT_ENTRY_PARENT_ID );
        entry.setTitle( strTitle );
        entry.setIdQuicklinks( quicklinks.getId( ) );
        entry.setEntryType( entryType );

        if ( AppPropertiesService.getProperty( PROPERTY_ENTRY_ORDER_DEFAULT_VALUE, DEFAULT_VALUE_ENTRY_ORDER ).equals( DEFAULT_VALUE_ENTRY_ORDER ) )
        {
            entry.setIdOrder( EntryHome.FIRST_ORDER );
        }
        else
        {
            EntryFilter entryFilter = new EntryFilter( );
            entryFilter.setIdQuicklinks( entry.getIdQuicklinks( ) );
            entryFilter.setIdParent( entry.getIdParent( ) );
            entry.setIdOrder( EntryHome.findByFilter( entryFilter, plugin ).size( ) );
        }

        String strErrorMessageSpecificParameters = entry.setSpecificParameters( request );

        if ( strErrorMessageSpecificParameters != null )
        {
            return AdminMessageService.getMessageUrl( request, strErrorMessageSpecificParameters, AdminMessage.TYPE_STOP );
        }

        EntryHome.create( entry, plugin );

        UrlItem url = null;

        if ( ( strNextStep != null ) && strNextStep.equals( STEP_MODIFY ) )
        {
            url = new UrlItem( JSP_URL_MODIFY_ENTRY );
            url.addParameter( PARAMETER_ENTRY_ID, entry.getId( ) );
        }
        else
        {
            url = new UrlItem( JSP_URL_MODIFY );
            url.setAnchor( ANCHOR_NAME );
        }

        url.addParameter( PARAMETER_QUICKLINKS_ID, entry.getIdQuicklinks( ) );

        return url.getUrl( );
    }

    /**
     * Get the {@link Entry} modification page
     * 
     * @param request
     *            The HTTP servlet request
     * @return The HTML template
     */
    public String getModifyEntry( HttpServletRequest request ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin( );
        String strIdEntry = request.getParameter( PARAMETER_ENTRY_ID );

        if ( ( strIdEntry == null ) || !strIdEntry.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nIdEntry = Integer.parseInt( strIdEntry );
        IEntry entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        if ( ( entry == null ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        if ( !RBACService.isAuthorized( Quicklinks.RESOURCE_TYPE, String.valueOf( entry.getIdQuicklinks( ) ), QuicklinksResourceIdService.PERMISSION_MODIFY,
                getUser( ) ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        HashMap<String, Object> model = new HashMap<>( );
        setPageTitleProperty( MESSAGE_PAGE_TITLE_MODIFY_ENTRY );

        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, getLocale( ) );
        model.put( MARK_PLUGIN, plugin );
        model.put( MARK_ENTRY, entry );
        // Add the specifics markers into model
        entry.getSpecificParameters( request, model, plugin );

        HtmlTemplate template = AppTemplateService.getTemplate( entry.getEntryType( ).getTemplateModify( ), getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Processes the {@link Entry} modification
     * 
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     */
    public String doModifyEntry( HttpServletRequest request ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin( );
        String strIdEntry = request.getParameter( PARAMETER_ENTRY_ID );
        String strTitle = request.getParameter( PARAMETER_TITLE );

        if ( ( strIdEntry == null ) || !strIdEntry.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nIdEntry = Integer.parseInt( strIdEntry );
        IEntry entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        if ( ( entry == null ) || ( strTitle == null ) || strTitle.equals( EMPTY_STRING ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        if ( !RBACService.isAuthorized( Quicklinks.RESOURCE_TYPE, String.valueOf( entry.getIdQuicklinks( ) ), QuicklinksResourceIdService.PERMISSION_MODIFY,
                getUser( ) ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        entry.setTitle( strTitle );

        String strErrorMessageSpecificParameters = entry.setSpecificParameters( request );

        if ( strErrorMessageSpecificParameters != null )
        {
            return AdminMessageService.getMessageUrl( request, strErrorMessageSpecificParameters, AdminMessage.TYPE_STOP );
        }

        EntryHome.update( entry, plugin );

        UrlItem url = new UrlItem( JSP_URL_MODIFY );
        url.addParameter( PARAMETER_QUICKLINKS_ID, entry.getIdQuicklinks( ) );
        url.setAnchor( ANCHOR_NAME );

        return url.getUrl( );
    }

    /**
     * Processes the {@link Entry} removal confirmation
     * 
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     */
    public String doConfirmRemoveEntry( HttpServletRequest request ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin( );
        String strIdEntry = request.getParameter( PARAMETER_ENTRY_ID );

        if ( ( strIdEntry == null ) || !strIdEntry.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nIdEntry = Integer.parseInt( strIdEntry );
        IEntry entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        if ( ( entry == null ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        if ( !RBACService.isAuthorized( Quicklinks.RESOURCE_TYPE, String.valueOf( entry.getIdQuicklinks( ) ), QuicklinksResourceIdService.PERMISSION_MODIFY,
                getUser( ) ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        HashMap<String, Object> model = new HashMap<>( );
        model.put( MARK_ENTRY_ID, String.valueOf( entry.getId( ) ) );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRMATION_REMOVE_ENTRY, JSP_URL_PREFIX + JSP_URL_DELETE_ENTRY, AdminMessage.TYPE_QUESTION,
                model );
    }

    /**
     * Processes the {@link Entry} removal
     * 
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     */
    public String doRemoveEntry( HttpServletRequest request ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin( );
        String strIdEntry = request.getParameter( PARAMETER_ENTRY_ID );

        if ( ( strIdEntry == null ) || !strIdEntry.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nIdEntry = Integer.parseInt( strIdEntry );
        IEntry entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        if ( ( entry == null ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        if ( !RBACService.isAuthorized( Quicklinks.RESOURCE_TYPE, String.valueOf( entry.getIdQuicklinks( ) ), QuicklinksResourceIdService.PERMISSION_MODIFY,
                getUser( ) ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        EntryHome.remove( entry.getId( ), plugin );

        UrlItem url = new UrlItem( JSP_URL_MODIFY );
        url.addParameter( PARAMETER_QUICKLINKS_ID, entry.getIdQuicklinks( ) );
        url.setAnchor( ANCHOR_NAME );

        return url.getUrl( );
    }

    /**
     * Processes the {@link Quicklinks} disable confirmation
     * 
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     */
    public String doConfirmDisableQuicklinks( HttpServletRequest request ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin( );
        String strIdQuicklinks = request.getParameter( PARAMETER_QUICKLINKS_ID );

        if ( ( strIdQuicklinks == null ) || !strIdQuicklinks.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nIdQuicklinks = Integer.parseInt( strIdQuicklinks );
        Quicklinks quicklinks = QuicklinksHome.findByPrimaryKey( nIdQuicklinks, plugin );

        if ( ( quicklinks == null ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        if ( !RBACService.isAuthorized( Quicklinks.RESOURCE_TYPE, String.valueOf( quicklinks.getId( ) ), QuicklinksResourceIdService.PERMISSION_CHANGE_STATE,
                getUser( ) ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        HashMap<String, Object> model = new HashMap<>( );
        model.put( MARK_QUICKLINKS_ID, String.valueOf( quicklinks.getId( ) ) );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRMATION_DISABLE_QUICKLINKS, JSP_URL_PREFIX + JSP_URL_DISABLE_QUICKLINKS,
                AdminMessage.TYPE_QUESTION, model );
    }

    /**
     * Processes the {@link Quicklinks} disable
     * 
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     */
    public String doDisableQuicklinks( HttpServletRequest request ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin( );
        String strIdQuicklinks = request.getParameter( PARAMETER_QUICKLINKS_ID );

        if ( ( strIdQuicklinks == null ) || !strIdQuicklinks.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nIdQuicklinks = Integer.parseInt( strIdQuicklinks );
        Quicklinks quicklinks = QuicklinksHome.findByPrimaryKey( nIdQuicklinks, plugin );

        if ( ( quicklinks == null ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        if ( !RBACService.isAuthorized( Quicklinks.RESOURCE_TYPE, String.valueOf( quicklinks.getId( ) ), QuicklinksResourceIdService.PERMISSION_CHANGE_STATE,
                getUser( ) ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        int nCountPortlets = QuicklinksPortletHome.getCountPortletByIdQuicklinks( quicklinks.getId( ) );

        if ( nCountPortlets > 0 )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_STOP_CANNOT_DISABLE_QUICKLINKS, new String [ ] {
                    String.valueOf( nCountPortlets )
            }, AdminMessage.TYPE_STOP );
        }

        quicklinks.setEnabled( false );
        QuicklinksHome.update( quicklinks, plugin );

        UrlItem url = new UrlItem( JSP_URL_MANAGE );

        return url.getUrl( );
    }

    /**
     * Processes the {@link Quicklinks} enable
     * 
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     */
    public String doEnableQuicklinks( HttpServletRequest request ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin( );
        String strIdQuicklinks = request.getParameter( PARAMETER_QUICKLINKS_ID );

        if ( ( strIdQuicklinks == null ) || !strIdQuicklinks.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nIdQuicklinks = Integer.parseInt( strIdQuicklinks );
        Quicklinks quicklinks = QuicklinksHome.findByPrimaryKey( nIdQuicklinks, plugin );

        if ( ( quicklinks == null ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        if ( !RBACService.isAuthorized( Quicklinks.RESOURCE_TYPE, String.valueOf( quicklinks.getId( ) ), QuicklinksResourceIdService.PERMISSION_CHANGE_STATE,
                getUser( ) ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        quicklinks.setEnabled( true );
        QuicklinksHome.update( quicklinks, plugin );

        UrlItem url = new UrlItem( JSP_URL_MANAGE );

        return url.getUrl( );
    }

    /**
     * Processes the {@link Quicklinks} copy
     *
     * @param request
     *            The {@link HttpServletRequest}
     * @return The Url to redirect to
     */
    public String doCopyQuicklinks( HttpServletRequest request ) throws AccessDeniedException
    {
        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_COPY );
        quicklinks.copy( getPlugin( ), I18nService.getLocalizedString( MESSAGE_COPY, getLocale( ) ) + quicklinks.getTitle( ) );

        UrlItem url = new UrlItem( JSP_URL_MANAGE );

        return url.getUrl( );
    }

    /**
     * Processes the {@link Entry} copy
     *
     * @param request
     *            The {@link HttpServletRequest}
     * @return The Url to redirect to
     */
    public String doCopyEntry( HttpServletRequest request ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin( );
        String strIdEntry = request.getParameter( PARAMETER_ENTRY_ID );

        if ( ( strIdEntry == null ) || !strIdEntry.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nIdEntry = Integer.parseInt( strIdEntry );
        IEntry entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        if ( ( entry == null ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        if ( !RBACService.isAuthorized( Quicklinks.RESOURCE_TYPE, String.valueOf( entry.getIdQuicklinks( ) ), QuicklinksResourceIdService.PERMISSION_MODIFY,
                getUser( ) ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        entry.copy( entry.getIdQuicklinks( ), plugin, I18nService.getLocalizedString( MESSAGE_COPY, getLocale( ) ) + entry.getTitle( ) );

        UrlItem url = new UrlItem( JSP_URL_MODIFY );
        url.addParameter( PARAMETER_QUICKLINKS_ID, entry.getIdQuicklinks( ) );
        url.setAnchor( ANCHOR_NAME );

        return url.getUrl( );
    }

    // -------------------------- Private methods --------------------------
}

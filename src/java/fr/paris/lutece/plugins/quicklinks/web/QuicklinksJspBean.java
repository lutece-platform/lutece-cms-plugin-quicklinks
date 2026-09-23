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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.quicklinks.business.Entry;
import fr.paris.lutece.plugins.quicklinks.business.EntryFilter;
import fr.paris.lutece.plugins.quicklinks.business.EntryHome;
import fr.paris.lutece.plugins.quicklinks.business.EntrySelectOption;
import fr.paris.lutece.plugins.quicklinks.business.EntrySelectOptionHome;
import fr.paris.lutece.plugins.quicklinks.business.EntryType;
import fr.paris.lutece.plugins.quicklinks.business.EntryTypeHome;
import fr.paris.lutece.plugins.quicklinks.business.IEntry;
import fr.paris.lutece.plugins.quicklinks.business.Quicklinks;
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
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.util.IPager;
import fr.paris.lutece.portal.web.util.Pager;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage {@link Quicklinks} features ( manage, create, modify, remove)
 */
@RequestScoped
@Named
@Controller( controllerJsp = "ManageQuicklinks.jsp", controllerPath = "jsp/admin/plugins/quicklinks/", right = "QUICKLINKS_MANAGEMENT", securityTokenEnabled = true )
public class QuicklinksJspBean extends MVCAdminJspBean
{
    private static final long serialVersionUID = -5176913689822438398L;

    // Rights
    public static final String RIGHT_MANAGE_QUICKLINKS = "QUICKLINKS_MANAGEMENT";

    // Controller
    static final String CONTROLLER_PATH = "jsp/admin/plugins/quicklinks/";
    static final String CONTROLLER_JSP = "ManageQuicklinks.jsp";

    // Templates
    private static final String TEMPLATE_MANAGE = "admin/plugins/quicklinks/manage_quicklinks.html";
    private static final String TEMPLATE_CREATE = "admin/plugins/quicklinks/create_quicklinks.html";
    private static final String TEMPLATE_MODIFY = "admin/plugins/quicklinks/modify_quicklinks.html";

    // Views
    static final String VIEW_MANAGE_QUICKLINKS = "manageQuicklinks";
    private static final String VIEW_CREATE_QUICKLINKS = "createQuicklinks";
    static final String VIEW_MODIFY_QUICKLINKS = "modifyQuicklinks";
    private static final String VIEW_CONFIRM_REMOVE_QUICKLINKS = "confirmRemoveQuicklinks";
    private static final String VIEW_CONFIRM_DISABLE_QUICKLINKS = "confirmDisableQuicklinks";
    private static final String VIEW_CONFIRM_ENABLE_QUICKLINKS = "confirmEnableQuicklinks";
    private static final String VIEW_CONFIRM_COPY_QUICKLINKS = "confirmCopyQuicklinks";
    private static final String VIEW_CREATE_ENTRY = "createEntry";
    static final String VIEW_MODIFY_ENTRY = "modifyEntry";
    private static final String VIEW_CONFIRM_REMOVE_ENTRY = "confirmRemoveEntry";

    // Actions
    private static final String ACTION_CREATE_QUICKLINKS = "createQuicklinks";
    private static final String ACTION_MODIFY_QUICKLINKS = "modifyQuicklinks";
    private static final String ACTION_REMOVE_QUICKLINKS = "removeQuicklinks";
    private static final String ACTION_DISABLE_QUICKLINKS = "disableQuicklinks";
    private static final String ACTION_ENABLE_QUICKLINKS = "enableQuicklinks";
    private static final String ACTION_COPY_QUICKLINKS = "copyQuicklinks";
    private static final String ACTION_CREATE_ENTRY = "createEntry";
    private static final String ACTION_MODIFY_ENTRY = "modifyEntry";
    private static final String ACTION_REMOVE_ENTRY = "removeEntry";

    // Operations
    private static final String OPERATION_GO_UP = "goUp";
    private static final String OPERATION_GO_DOWN = "goDown";
    private static final String OPERATION_GO_IN = "goIn";
    private static final String OPERATION_GO_OUT = "goOut";
    private static final String OPERATION_COPY = "copy";

    // Properties
    private static final String PROPERTY_ITEMS_PER_PAGE = "paginator.style.itemsPerPage";
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
    private static final String MESSAGE_CONFIRMATION_ENABLE_QUICKLINKS = "quicklinks.message.confirmEnableQuicklinks";
    private static final String MESSAGE_CONFIRMATION_COPY_QUICKLINKS = "quicklinks.message.confirmCopyQuicklinks";
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
    private static final String PARAMETER_OPTION_ID = "option_id";
    private static final String PARAMETER_ENTRY_OPERATION = "entry_operation";
    private static final String PARAMETER_OPTION_OPERATION = "option_operation";
    private static final String PARAMETER_APPLY = "apply";

    // Anchors
    private static final String ANCHOR_ENTRY_LIST = "entry_list";
    private static final String ANCHOR_OPTION_LIST = "option_list";

    // Markers
    private static final String MARK_QUICKLINKS_LIST = "quicklinks_list";
    private static final String MARK_QUICKLINKS_INCLUDE_TAG = "quicklinks_include_tag";
    private static final String MARK_QUICKLINKS = "quicklinks";
    private static final String MARK_QUICKLINKS_ACTIONS = "quicklinks_actions";
    private static final String MARK_PLUGIN = "plugin";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_ENTRY = "entry";
    private static final String MARK_PERMISSION_CREATE = "permission_create_quicklinks";
    private static final String MARK_TYPE_LIST = "type_list";
    private static final String MARK_DEFAULT_VALUE_TYPE = "type_default_value";
    private static final String MARK_ROLE_KEY_LIST = "role_key_list";
    private static final String MARK_DEFAULT_VALUE_ROLE_KEY = "role_key_default_value";
    private static final String MARK_WORKGROUP_KEY_LIST = "workgroup_key_list";
    private static final String MARK_WORKGROUP_LABELS = "workgroup_labels";
    private static final String MARK_ROLE_LABELS = "role_labels";
    private static final String MARK_DEFAULT_VALUE_WORKGROUP_KEY = "workgroup_key_default_value";
    private static final String MARK_DEFAULT_VALUE_STATE = "state_default_value";
    private static final String MARK_STATE_LIST = "state_list";
    private static final String MARK_ENTRY_LIST = "entry_list";
    private static final String MARK_ENTRY_TYPE_LIST = "entry_type_list";

    // Pagers
    private static final String PAGER_QUICKLINKS = "quicklinks.quicklinks";
    private static final String PAGER_ENTRIES = "quicklinks.entries";

    // Miscellaneous
    private static final String UNAUTHORIZED = "Unauthorized";
    private static final String DEFAULT_VALUE_TYPE = "1";
    private static final String DEFAULT_VALUE_STATE = "0";
    private static final String DEFAULT_VALUE_ENTRY_ORDER = "first";
    private static final int DEFAULT_ENTRY_PARENT_ID = 0;
    private static final String REGEX_ID = "^[\\d]+$";
    private static final String STEP_MODIFY = "modify";

    @Inject
    @Pager( name = PAGER_QUICKLINKS, listBookmark = MARK_QUICKLINKS_LIST, defaultItemsPerPage = PROPERTY_ITEMS_PER_PAGE, baseUrl = CONTROLLER_PATH
            + CONTROLLER_JSP )
    private IPager<Quicklinks, Map<String, Object>> _pagerQuicklinks;

    @Inject
    @Pager( name = PAGER_ENTRIES, listBookmark = MARK_ENTRY_LIST, defaultItemsPerPage = PROPERTY_ITEMS_PER_PAGE )
    private IPager<IEntry, IEntry> _pagerEntries;

    /**
     * Get the {@link Quicklinks} management page
     *
     * @param request
     *            The HTTP servlet request
     * @param model
     *            The model
     * @return The HTML template
     */
    @View( value = VIEW_MANAGE_QUICKLINKS, defaultView = true )
    public String getManageQuicklinks( HttpServletRequest request, Models model )
    {
        List<Quicklinks> listQuicklinks = new ArrayList<>(
                AdminWorkgroupService.getAuthorizedCollection( QuicklinksHome.findAll( getPlugin( ) ), (User) getUser( ) ) );

        _pagerQuicklinks.withListItem( listQuicklinks ).populateModels( request, model, this::getQuicklinksRows, getLocale( ) );
        model.put( MARK_PERMISSION_CREATE, isCreationAuthorized( ) );
        model.put( MARK_PLUGIN, getPlugin( ) );
        model.put( MARK_WORKGROUP_LABELS, AdminWorkgroupService.getUserWorkgroups( getUser( ), getLocale( ) ).toMap( ) );
        model.put( MARK_ROLE_LABELS, RoleHome.getRolesList( ).toMap( ) );

        return getPage( MESSAGE_PAGE_TITLE_MANAGE, TEMPLATE_MANAGE, model );
    }

    /**
     * Build the rows of the management page: each quicklinks with its authorized actions and its include tag
     *
     * @param listQuicklinks
     *            The quicklinks of the current page
     * @return The rows
     */
    private List<Map<String, Object>> getQuicklinksRows( List<Quicklinks> listQuicklinks )
    {
        List<Map<String, Object>> listRows = new ArrayList<>( );

        for ( Quicklinks quicklinks : listQuicklinks )
        {
            Map<String, Object> row = new HashMap<>( );
            row.put( MARK_QUICKLINKS, quicklinks );

            if ( quicklinks.getType( ).equals( QuicklinksType.INCLUDE ) )
            {
                String strQuicklinksMarker = QuicklinksInclude.getQuicklinksMarkerPrefix( ) + quicklinks.getId( );
                row.put( MARK_QUICKLINKS_INCLUDE_TAG, I18nService.getLocalizedString( MESSAGE_LABEL_TAG, new String [ ] {
                        strQuicklinksMarker
                }, getLocale( ) ) );
            }

            row.put( MARK_QUICKLINKS_ACTIONS, RBACService.getAuthorizedActionsCollection(
                    QuicklinksActionHome.selectActionsByQuicklinksState( quicklinks.isEnabled( ), getPlugin( ), getLocale( ) ), quicklinks, (User) getUser( ) ) );
            listRows.add( row );
        }

        return listRows;
    }

    /**
     * Get the {@link Quicklinks} creation page
     *
     * @param model
     *            The model
     * @return The HTML template
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @View( VIEW_CREATE_QUICKLINKS )
    public String getCreateQuicklinks( Models model ) throws AccessDeniedException
    {
        if ( !isCreationAuthorized( ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        fillQuicklinksFormModel( model );
        model.put( MARK_DEFAULT_VALUE_TYPE, AppPropertiesService.getProperty( PROPERTY_TYPE_DEFAULT_VALUE, DEFAULT_VALUE_TYPE ) );
        model.put( MARK_DEFAULT_VALUE_WORKGROUP_KEY, AdminWorkgroupService.ALL_GROUPS );
        model.put( MARK_DEFAULT_VALUE_ROLE_KEY, Quicklinks.ROLE_NONE );
        model.put( MARK_DEFAULT_VALUE_STATE, AppPropertiesService.getProperty( PROPERTY_STATE_DEFAULT_VALUE, DEFAULT_VALUE_STATE ) );

        return getPage( MESSAGE_PAGE_TITLE_CREATE, TEMPLATE_CREATE, model );
    }

    /**
     * Processes the {@link Quicklinks} creation
     *
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @Action( ACTION_CREATE_QUICKLINKS )
    public String doCreateQuicklinks( HttpServletRequest request ) throws AccessDeniedException
    {
        if ( !isCreationAuthorized( ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        QuicklinksType quicklinksType = getSubmittedType( request );

        if ( quicklinksType == null )
        {
            return redirectMandatoryFields( request );
        }

        Quicklinks quicklinks = new Quicklinks( );
        populate( quicklinks, quicklinksType, request );
        QuicklinksHome.create( quicklinks, getPlugin( ) );

        return redirectView( request, VIEW_MANAGE_QUICKLINKS );
    }

    /**
     * Get the {@link Quicklinks} modification page
     *
     * @param request
     *            The HTTP servlet request
     * @param model
     *            The model
     * @return The HTML template
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @View( VIEW_MODIFY_QUICKLINKS )
    public String getModifyQuicklinks( HttpServletRequest request, Models model ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin( );
        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_MODIFY );

        fillQuicklinksFormModel( model );
        model.put( MARK_QUICKLINKS, quicklinks );
        model.put( MARK_ENTRY_TYPE_LIST, EntryTypeHome.findAll( plugin ) );

        EntryFilter filter = new EntryFilter( );
        filter.setIdQuicklinks( quicklinks.getId( ) );
        filter.setIdParent( EntryHome.ROOT_PARENT_ID );

        UrlItem url = new UrlItem( CONTROLLER_PATH + getViewUrl( VIEW_MODIFY_QUICKLINKS ) );
        url.addParameter( PARAMETER_QUICKLINKS_ID, quicklinks.getId( ) );
        _pagerEntries.withBaseUrl( url.getUrl( ) ).withListItem( new ArrayList<>( EntryHome.findByFilter( filter, plugin ) ) ).populateModels( request, model,
                getLocale( ) );

        return getPage( MESSAGE_PAGE_TITLE_MODIFY, TEMPLATE_MODIFY, model );
    }

    /**
     * Processes the {@link Quicklinks} modification, or an operation on one of its entries (move, copy)
     *
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @Action( ACTION_MODIFY_QUICKLINKS )
    public String doModifyQuicklinks( HttpServletRequest request ) throws AccessDeniedException
    {
        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_MODIFY );
        String strEntryOperation = request.getParameter( PARAMETER_ENTRY_OPERATION );

        if ( strEntryOperation != null )
        {
            return applyEntryOperation( request, quicklinks, strEntryOperation );
        }

        QuicklinksType quicklinksType = getSubmittedType( request );

        if ( quicklinksType == null )
        {
            return redirectMandatoryFields( request );
        }

        boolean bEnabled = Boolean.parseBoolean( request.getParameter( PARAMETER_STATE ) );

        if ( ( bEnabled != quicklinks.isEnabled( ) ) && !RBACService.isAuthorized( Quicklinks.RESOURCE_TYPE, String.valueOf( quicklinks.getId( ) ),
                QuicklinksResourceIdService.PERMISSION_CHANGE_STATE, (User) getUser( ) ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        int nCountPortlets = QuicklinksPortletHome.getCountPortletByIdQuicklinks( quicklinks.getId( ) );

        if ( ( nCountPortlets > 0 ) && ( ( bEnabled != quicklinks.isEnabled( ) ) || ( quicklinksType != quicklinks.getType( ) ) ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_STOP_CANNOT_MODIFY_QUICKLINKS, new String [ ] {
                    String.valueOf( nCountPortlets )
            }, AdminMessage.TYPE_STOP ) );
        }

        populate( quicklinks, quicklinksType, request );
        QuicklinksHome.update( quicklinks, getPlugin( ) );

        if ( request.getParameter( PARAMETER_APPLY ) != null )
        {
            return redirect( request, VIEW_MODIFY_QUICKLINKS, PARAMETER_QUICKLINKS_ID, quicklinks.getId( ) );
        }

        return redirectView( request, VIEW_MANAGE_QUICKLINKS );
    }

    /**
     * Apply an operation (move up, down, in, out, copy) on an entry of the quicklinks
     *
     * @param request
     *            The HTTP servlet request
     * @param quicklinks
     *            The quicklinks holding the entry
     * @param strOperation
     *            The operation
     * @return The URL to redirect to
     * @throws AccessDeniedException
     *             if the entry does not belong to the quicklinks
     */
    private String applyEntryOperation( HttpServletRequest request, Quicklinks quicklinks, String strOperation ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin( );
        IEntry entry = getAuthorizedEntry( request, QuicklinksResourceIdService.PERMISSION_MODIFY );

        if ( ( entry == null ) || ( entry.getIdQuicklinks( ) != quicklinks.getId( ) ) )
        {
            return redirectMandatoryFields( request );
        }

        switch( strOperation )
        {
            case OPERATION_GO_UP:
                EntryHome.goUp( entry.getId( ), plugin );
                break;
            case OPERATION_GO_DOWN:
                EntryHome.goDown( entry.getId( ), plugin );
                break;
            case OPERATION_GO_IN:
                EntryHome.goIn( entry.getId( ), plugin );
                break;
            case OPERATION_GO_OUT:
                EntryHome.goOut( entry.getId( ), plugin );
                break;
            case OPERATION_COPY:
                entry.copy( entry.getIdQuicklinks( ), plugin, I18nService.getLocalizedString( MESSAGE_COPY, getLocale( ) ) + entry.getTitle( ) );
                break;
            default:
                return redirectMandatoryFields( request );
        }

        return redirectToQuicklinks( request, quicklinks.getId( ) );
    }

    /**
     * Get the {@link Quicklinks} removal confirmation
     *
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @View( value = VIEW_CONFIRM_REMOVE_QUICKLINKS, securityTokenAction = ACTION_REMOVE_QUICKLINKS )
    public String getConfirmRemoveQuicklinks( HttpServletRequest request ) throws AccessDeniedException
    {
        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_DELETE );
        int nCountPortlets = QuicklinksPortletHome.getCountPortletByIdQuicklinks( quicklinks.getId( ) );

        if ( nCountPortlets > 0 )
        {
            return redirectPortletsAssigned( request, MESSAGE_STOP_CANNOT_REMOVE_QUICKLINKS, nCountPortlets );
        }

        return redirectConfirmation( request, MESSAGE_CONFIRMATION_REMOVE_QUICKLINKS, ACTION_REMOVE_QUICKLINKS, PARAMETER_QUICKLINKS_ID, quicklinks.getId( ) );
    }

    /**
     * Processes the {@link Quicklinks} removal
     *
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @Action( ACTION_REMOVE_QUICKLINKS )
    public String doRemoveQuicklinks( HttpServletRequest request ) throws AccessDeniedException
    {
        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_DELETE );
        int nCountPortlets = QuicklinksPortletHome.getCountPortletByIdQuicklinks( quicklinks.getId( ) );

        if ( nCountPortlets > 0 )
        {
            return redirectPortletsAssigned( request, MESSAGE_STOP_CANNOT_REMOVE_QUICKLINKS, nCountPortlets );
        }

        QuicklinksHome.remove( quicklinks.getId( ), getPlugin( ) );

        return redirectView( request, VIEW_MANAGE_QUICKLINKS );
    }

    /**
     * Get the {@link Quicklinks} disable confirmation
     *
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @View( value = VIEW_CONFIRM_DISABLE_QUICKLINKS, securityTokenAction = ACTION_DISABLE_QUICKLINKS )
    public String getConfirmDisableQuicklinks( HttpServletRequest request ) throws AccessDeniedException
    {
        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_CHANGE_STATE );
        int nCountPortlets = QuicklinksPortletHome.getCountPortletByIdQuicklinks( quicklinks.getId( ) );

        if ( nCountPortlets > 0 )
        {
            return redirectPortletsAssigned( request, MESSAGE_STOP_CANNOT_DISABLE_QUICKLINKS, nCountPortlets );
        }

        return redirectConfirmation( request, MESSAGE_CONFIRMATION_DISABLE_QUICKLINKS, ACTION_DISABLE_QUICKLINKS, PARAMETER_QUICKLINKS_ID, quicklinks.getId( ) );
    }

    /**
     * Processes the {@link Quicklinks} disable
     *
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @Action( ACTION_DISABLE_QUICKLINKS )
    public String doDisableQuicklinks( HttpServletRequest request ) throws AccessDeniedException
    {
        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_CHANGE_STATE );
        int nCountPortlets = QuicklinksPortletHome.getCountPortletByIdQuicklinks( quicklinks.getId( ) );

        if ( nCountPortlets > 0 )
        {
            return redirectPortletsAssigned( request, MESSAGE_STOP_CANNOT_DISABLE_QUICKLINKS, nCountPortlets );
        }

        quicklinks.setEnabled( false );
        QuicklinksHome.update( quicklinks, getPlugin( ) );

        return redirectView( request, VIEW_MANAGE_QUICKLINKS );
    }

    /**
     * Get the {@link Quicklinks} enable confirmation
     *
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @View( value = VIEW_CONFIRM_ENABLE_QUICKLINKS, securityTokenAction = ACTION_ENABLE_QUICKLINKS )
    public String getConfirmEnableQuicklinks( HttpServletRequest request ) throws AccessDeniedException
    {
        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_CHANGE_STATE );

        return redirectConfirmation( request, MESSAGE_CONFIRMATION_ENABLE_QUICKLINKS, ACTION_ENABLE_QUICKLINKS, PARAMETER_QUICKLINKS_ID, quicklinks.getId( ) );
    }

    /**
     * Processes the {@link Quicklinks} enable
     *
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @Action( ACTION_ENABLE_QUICKLINKS )
    public String doEnableQuicklinks( HttpServletRequest request ) throws AccessDeniedException
    {
        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_CHANGE_STATE );
        quicklinks.setEnabled( true );
        QuicklinksHome.update( quicklinks, getPlugin( ) );

        return redirectView( request, VIEW_MANAGE_QUICKLINKS );
    }

    /**
     * Get the {@link Quicklinks} copy confirmation
     *
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @View( value = VIEW_CONFIRM_COPY_QUICKLINKS, securityTokenAction = ACTION_COPY_QUICKLINKS )
    public String getConfirmCopyQuicklinks( HttpServletRequest request ) throws AccessDeniedException
    {
        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_COPY );

        return redirectConfirmation( request, MESSAGE_CONFIRMATION_COPY_QUICKLINKS, ACTION_COPY_QUICKLINKS, PARAMETER_QUICKLINKS_ID, quicklinks.getId( ) );
    }

    /**
     * Processes the {@link Quicklinks} copy
     *
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @Action( ACTION_COPY_QUICKLINKS )
    public String doCopyQuicklinks( HttpServletRequest request ) throws AccessDeniedException
    {
        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_COPY );
        quicklinks.copy( getPlugin( ), I18nService.getLocalizedString( MESSAGE_COPY, getLocale( ) ) + quicklinks.getTitle( ) );

        return redirectView( request, VIEW_MANAGE_QUICKLINKS );
    }

    /**
     * Get the {@link Entry} creation page
     *
     * @param request
     *            The HTTP servlet request
     * @param model
     *            The model
     * @return The HTML template
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @View( VIEW_CREATE_ENTRY )
    public String getCreateEntry( HttpServletRequest request, Models model ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin( );
        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_MODIFY );
        EntryType entryType = getSubmittedEntryType( request );

        if ( entryType == null )
        {
            return redirectMandatoryFields( request );
        }

        IEntry entry = EntryHome.getSpecificEntry( entryType, plugin );
        entry.setIdParent( DEFAULT_ENTRY_PARENT_ID );
        entry.setIdQuicklinks( quicklinks.getId( ) );
        entry.setTitle( request.getParameter( PARAMETER_TITLE ) );
        entry.setEntryType( entryType );
        fillEntryModel( request, entry, model );

        return getPage( MESSAGE_PAGE_TITLE_CREATE_ENTRY, entryType.getTemplateCreate( ), model );
    }

    /**
     * Processes the {@link Entry} creation
     *
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @Action( ACTION_CREATE_ENTRY )
    public String doCreateEntry( HttpServletRequest request ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin( );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        Quicklinks quicklinks = getAuthorizedQuicklinks( request, QuicklinksResourceIdService.PERMISSION_MODIFY );
        EntryType entryType = getSubmittedEntryType( request );

        if ( ( entryType == null ) || StringUtils.isEmpty( strTitle ) )
        {
            return redirectMandatoryFields( request );
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
            return redirect( request, AdminMessageService.getMessageUrl( request, strErrorMessageSpecificParameters, AdminMessage.TYPE_STOP ) );
        }

        EntryHome.create( entry, plugin );

        if ( STEP_MODIFY.equals( request.getParameter( PARAMETER_NEXT_STEP ) ) )
        {
            return redirect( request, VIEW_MODIFY_ENTRY, PARAMETER_ENTRY_ID, entry.getId( ) );
        }

        return redirectToQuicklinks( request, entry.getIdQuicklinks( ) );
    }

    /**
     * Get the {@link Entry} modification page
     *
     * @param request
     *            The HTTP servlet request
     * @param model
     *            The model
     * @return The HTML template
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @View( VIEW_MODIFY_ENTRY )
    public String getModifyEntry( HttpServletRequest request, Models model ) throws AccessDeniedException
    {
        IEntry entry = getAuthorizedEntry( request, QuicklinksResourceIdService.PERMISSION_MODIFY );

        if ( entry == null )
        {
            return redirectMandatoryFields( request );
        }

        fillEntryModel( request, entry, model );

        return getPage( MESSAGE_PAGE_TITLE_MODIFY_ENTRY, entry.getEntryType( ).getTemplateModify( ), model );
    }

    /**
     * Processes the {@link Entry} modification, or an operation on one of its options (move, copy)
     *
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @Action( ACTION_MODIFY_ENTRY )
    public String doModifyEntry( HttpServletRequest request ) throws AccessDeniedException
    {
        IEntry entry = getAuthorizedEntry( request, QuicklinksResourceIdService.PERMISSION_MODIFY );

        if ( entry == null )
        {
            return redirectMandatoryFields( request );
        }

        String strOptionOperation = request.getParameter( PARAMETER_OPTION_OPERATION );

        if ( strOptionOperation != null )
        {
            return applyOptionOperation( request, entry, strOptionOperation );
        }

        String strTitle = request.getParameter( PARAMETER_TITLE );

        if ( StringUtils.isEmpty( strTitle ) )
        {
            return redirectMandatoryFields( request );
        }

        entry.setTitle( strTitle );

        String strErrorMessageSpecificParameters = entry.setSpecificParameters( request );

        if ( strErrorMessageSpecificParameters != null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, strErrorMessageSpecificParameters, AdminMessage.TYPE_STOP ) );
        }

        EntryHome.update( entry, getPlugin( ) );

        return redirectToQuicklinks( request, entry.getIdQuicklinks( ) );
    }

    /**
     * Apply an operation (move up, down, copy) on an option of a select entry
     *
     * @param request
     *            The HTTP servlet request
     * @param entry
     *            The entry holding the option
     * @param strOperation
     *            The operation
     * @return The URL to redirect to
     */
    private String applyOptionOperation( HttpServletRequest request, IEntry entry, String strOperation )
    {
        Plugin plugin = getPlugin( );
        String strIdOption = request.getParameter( PARAMETER_OPTION_ID );
        EntrySelectOption option = ( ( strIdOption != null ) && strIdOption.matches( REGEX_ID ) )
                ? EntrySelectOptionHome.findByPrimaryKey( Integer.parseInt( strIdOption ), entry.getId( ), plugin )
                : null;

        if ( option == null )
        {
            return redirectMandatoryFields( request );
        }

        switch( strOperation )
        {
            case OPERATION_GO_UP:
                EntrySelectOptionHome.goUp( option.getId( ), entry.getId( ), plugin );
                break;
            case OPERATION_GO_DOWN:
                EntrySelectOptionHome.goDown( option.getId( ), entry.getId( ), plugin );
                break;
            case OPERATION_COPY:
                option.copy( entry.getId( ), plugin, I18nService.getLocalizedString( MESSAGE_COPY, getLocale( ) ) + option.getTitle( ) );
                break;
            default:
                return redirectMandatoryFields( request );
        }

        UrlItem url = new UrlItem( getViewUrl( VIEW_MODIFY_ENTRY ) );
        url.addParameter( PARAMETER_ENTRY_ID, entry.getId( ) );
        url.setAnchor( ANCHOR_OPTION_LIST );

        return redirect( request, url.getUrl( ) );
    }

    /**
     * Get the {@link Entry} removal confirmation
     *
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @View( value = VIEW_CONFIRM_REMOVE_ENTRY, securityTokenAction = ACTION_REMOVE_ENTRY )
    public String getConfirmRemoveEntry( HttpServletRequest request ) throws AccessDeniedException
    {
        IEntry entry = getAuthorizedEntry( request, QuicklinksResourceIdService.PERMISSION_MODIFY );

        if ( entry == null )
        {
            return redirectMandatoryFields( request );
        }

        return redirectConfirmation( request, MESSAGE_CONFIRMATION_REMOVE_ENTRY, ACTION_REMOVE_ENTRY, PARAMETER_ENTRY_ID, entry.getId( ) );
    }

    /**
     * Processes the {@link Entry} removal
     *
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @Action( ACTION_REMOVE_ENTRY )
    public String doRemoveEntry( HttpServletRequest request ) throws AccessDeniedException
    {
        IEntry entry = getAuthorizedEntry( request, QuicklinksResourceIdService.PERMISSION_MODIFY );

        if ( entry == null )
        {
            return redirectMandatoryFields( request );
        }

        EntryHome.remove( entry.getId( ), getPlugin( ) );

        return redirectToQuicklinks( request, entry.getIdQuicklinks( ) );
    }

    /**
     * Put in the model the lists shared by the creation and modification pages of a {@link Quicklinks}
     *
     * @param model
     *            The model
     */
    private void fillQuicklinksFormModel( Models model )
    {
        Locale locale = getLocale( );
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
        model.put( MARK_WORKGROUP_KEY_LIST, AdminWorkgroupService.getUserWorkgroups( getUser( ), locale ) );
        model.put( MARK_ROLE_KEY_LIST, RoleHome.getRolesList( ) );
        model.put( MARK_STATE_LIST, listState );
    }

    /**
     * Put in the model what the creation and modification pages of an {@link Entry} need, specific markers included
     *
     * @param request
     *            The HTTP servlet request
     * @param entry
     *            The entry
     * @param model
     *            The model
     */
    private void fillEntryModel( HttpServletRequest request, IEntry entry, Models model )
    {
        Map<String, Object> mapSpecific = new HashMap<>( );
        entry.getSpecificParameters( request, mapSpecific, getPlugin( ) );
        mapSpecific.forEach( model::put );

        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, getLocale( ) );
        model.put( MARK_PLUGIN, getPlugin( ) );
        model.put( MARK_ENTRY, entry );
    }

    /**
     * Set the submitted fields on a {@link Quicklinks}
     *
     * @param quicklinks
     *            The quicklinks
     * @param quicklinksType
     *            The validated type
     * @param request
     *            The HTTP servlet request
     */
    private void populate( Quicklinks quicklinks, QuicklinksType quicklinksType, HttpServletRequest request )
    {
        quicklinks.setEnabled( Boolean.parseBoolean( request.getParameter( PARAMETER_STATE ) ) );
        quicklinks.setRoleKey( request.getParameter( PARAMETER_ROLE_KEY ) );
        quicklinks.setTitle( request.getParameter( PARAMETER_TITLE ) );
        quicklinks.setType( quicklinksType );
        quicklinks.setWorkgroup( request.getParameter( PARAMETER_WORKGROUP_KEY ) );
        quicklinks.setCssStyle( request.getParameter( PARAMETER_CSS_STYLE ) );
    }

    /**
     * Get the type of a submitted {@link Quicklinks} form, once its mandatory fields are checked
     *
     * @param request
     *            The HTTP servlet request
     * @return The type, or null when a mandatory field is missing or the type is unknown
     */
    private QuicklinksType getSubmittedType( HttpServletRequest request )
    {
        String strType = request.getParameter( PARAMETER_TYPE );

        if ( StringUtils.isAnyEmpty( request.getParameter( PARAMETER_TITLE ), request.getParameter( PARAMETER_WORKGROUP_KEY ),
                request.getParameter( PARAMETER_ROLE_KEY ), request.getParameter( PARAMETER_STATE ) ) || ( strType == null ) || !strType.matches( REGEX_ID ) )
        {
            return null;
        }

        return QuicklinksType.getByValue( Integer.parseInt( strType ) );
    }

    /**
     * Get the submitted {@link EntryType}
     *
     * @param request
     *            The HTTP servlet request
     * @return The entry type, or null when missing or unknown
     */
    private EntryType getSubmittedEntryType( HttpServletRequest request )
    {
        String strIdType = request.getParameter( PARAMETER_TYPE_ID );

        if ( ( strIdType == null ) || !strIdType.matches( REGEX_ID ) )
        {
            return null;
        }

        return EntryTypeHome.findByPrimaryKey( Integer.parseInt( strIdType ), getPlugin( ) );
    }

    /**
     * Tell whether the user may create a {@link Quicklinks}
     *
     * @return true if authorized
     */
    private boolean isCreationAuthorized( )
    {
        return RBACService.isAuthorized( Quicklinks.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, QuicklinksResourceIdService.PERMISSION_CREATE,
                (User) getUser( ) );
    }

    /**
     * Get the authorized Quicklinks, filtered by workgroup
     *
     * @param request
     *            The {@link HttpServletRequest}
     * @param strPermissionType
     *            The type of permission (see {@link QuicklinksResourceIdService} class)
     * @return The quicklinks
     * @throws AccessDeniedException
     *             if the quicklinks is unknown or the user has no access
     */
    private Quicklinks getAuthorizedQuicklinks( HttpServletRequest request, String strPermissionType ) throws AccessDeniedException
    {
        String strIdQuicklinks = request.getParameter( PARAMETER_QUICKLINKS_ID );

        if ( ( strIdQuicklinks == null ) || !strIdQuicklinks.matches( REGEX_ID ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        Quicklinks quicklinks = QuicklinksHome.findByPrimaryKey( Integer.parseInt( strIdQuicklinks ), getPlugin( ) );

        if ( ( quicklinks == null ) || !isAuthorized( quicklinks, strPermissionType ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        return quicklinks;
    }

    /**
     * Get the authorized {@link IEntry}, filtered by the workgroup and the permissions of its quicklinks
     *
     * @param request
     *            The {@link HttpServletRequest}
     * @param strPermissionType
     *            The type of permission (see {@link QuicklinksResourceIdService} class)
     * @return The entry, or null when the identifier is missing or unknown
     * @throws AccessDeniedException
     *             if the user has no access to the quicklinks of the entry
     */
    private IEntry getAuthorizedEntry( HttpServletRequest request, String strPermissionType ) throws AccessDeniedException
    {
        String strIdEntry = request.getParameter( PARAMETER_ENTRY_ID );

        if ( ( strIdEntry == null ) || !strIdEntry.matches( REGEX_ID ) )
        {
            return null;
        }

        IEntry entry = EntryHome.findByPrimaryKey( Integer.parseInt( strIdEntry ), getPlugin( ) );

        if ( entry == null )
        {
            return null;
        }

        Quicklinks quicklinks = QuicklinksHome.findByPrimaryKey( entry.getIdQuicklinks( ), getPlugin( ) );

        if ( ( quicklinks == null ) || !isAuthorized( quicklinks, strPermissionType ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        return entry;
    }

    /**
     * Tell whether the user may act on a {@link Quicklinks}: workgroup and permission
     *
     * @param quicklinks
     *            The quicklinks
     * @param strPermissionType
     *            The type of permission
     * @return true if authorized
     */
    private boolean isAuthorized( Quicklinks quicklinks, String strPermissionType )
    {
        return AdminWorkgroupService.isAuthorized( quicklinks, (User) getUser( ) )
                && RBACService.isAuthorized( Quicklinks.RESOURCE_TYPE, String.valueOf( quicklinks.getId( ) ), strPermissionType, (User) getUser( ) );
    }

    /**
     * Redirect to the confirmation message of an action
     *
     * @param request
     *            The HTTP servlet request
     * @param strMessageKey
     *            The message key
     * @param strAction
     *            The action to confirm
     * @param strParameter
     *            The name of the identifier parameter
     * @param nId
     *            The identifier
     * @return The redirection result
     */
    private String redirectConfirmation( HttpServletRequest request, String strMessageKey, String strAction, String strParameter, int nId )
    {
        UrlItem url = new UrlItem( getActionUrl( strAction ) );
        url.addParameter( strParameter, nId );

        return redirect( request, AdminMessageService.getMessageUrl( request, strMessageKey, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Redirect to the message telling that portlets still use the quicklinks
     *
     * @param request
     *            The HTTP servlet request
     * @param strMessageKey
     *            The message key
     * @param nCountPortlets
     *            The number of portlets
     * @return The redirection result
     */
    private String redirectPortletsAssigned( HttpServletRequest request, String strMessageKey, int nCountPortlets )
    {
        return redirect( request, AdminMessageService.getMessageUrl( request, strMessageKey, new String [ ] {
                String.valueOf( nCountPortlets )
        }, AdminMessage.TYPE_STOP ) );
    }

    /**
     * Redirect to the mandatory fields message
     *
     * @param request
     *            The HTTP servlet request
     * @return The redirection result
     */
    private String redirectMandatoryFields( HttpServletRequest request )
    {
        return redirect( request, AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP ) );
    }

    /**
     * Redirect to the entry list of a quicklinks
     *
     * @param request
     *            The HTTP servlet request
     * @param nIdQuicklinks
     *            The quicklinks identifier
     * @return The redirection result
     */
    private String redirectToQuicklinks( HttpServletRequest request, int nIdQuicklinks )
    {
        UrlItem url = new UrlItem( getViewUrl( VIEW_MODIFY_QUICKLINKS ) );
        url.addParameter( PARAMETER_QUICKLINKS_ID, nIdQuicklinks );
        url.setAnchor( ANCHOR_ENTRY_LIST );

        return redirect( request, url.getUrl( ) );
    }
}

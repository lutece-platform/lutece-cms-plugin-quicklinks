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

import fr.paris.lutece.plugins.quicklinks.business.Entry;
import fr.paris.lutece.plugins.quicklinks.business.EntryHome;
import fr.paris.lutece.plugins.quicklinks.business.EntrySelectOption;
import fr.paris.lutece.plugins.quicklinks.business.EntrySelectOptionHome;
import fr.paris.lutece.plugins.quicklinks.business.IEntry;
import fr.paris.lutece.plugins.quicklinks.business.Quicklinks;
import fr.paris.lutece.plugins.quicklinks.business.QuicklinksHome;
import fr.paris.lutece.plugins.quicklinks.service.QuicklinksResourceIdService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * This class provides the user interface to manage {@link Quicklinks} features ( manage, create, modify, remove)
 */
public class QuicklinksEntrySelectJspBean extends PluginAdminPageJspBean
{
    private static final long serialVersionUID = -3970588972565885665L;

    // Rights
    public static final String RIGHT_MANAGE_QUICKLINKS = "QUICKLINKS_MANAGEMENT";

    // Templates
    private static final String TEMPLATE_CREATE_SELECT_OPTION = "admin/plugins/quicklinks/create_entry_select_option.html";
    private static final String TEMPLATE_MODIFY_SELECT_OPTION = "admin/plugins/quicklinks/modify_entry_select_option.html";

    // JSP URL
    private static final String JSP_URL_PREFIX = "jsp/admin/plugins/quicklinks/";
    private static final String JSP_URL_MODIFY = "ModifyEntry.jsp";
    private static final String JSP_URL_DELETE_OPTION = "DoRemoveEntrySelectOption.jsp";

    // Properties
    private static final String PROPERTY_OPTION_ORDER_DEFAULT_VALUE = "quicklinks.modify.entry.create.defaultValue.order";

    // Messages (I18n keys)
    private static final String MESSAGE_PAGE_TITLE_CREATE_SELECT_OPTION = "quicklinks.create_entry_select_option.pageTitle";
    private static final String MESSAGE_PAGE_TITLE_MODIFY_SELECT_OPTION = "quicklinks.modify_entry_select_option.pageTitle";
    private static final String MESSAGE_CONFIRMATION_REMOVE_OPTION = "quicklinks.entry_select.message.confirmRemoveEntrySelectOption";
    private static final String MESSAGE_COPY = "quicklinks.copy.titleCopy.prefix";

    // Parameters
    private static final String PARAMETER_QUICKLINKS_ID = "quicklinks_id";
    private static final String PARAMETER_OPTION_ID = "option_id";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_URL = "url";
    private static final String PARAMETER_ENTRY_ID = "entry_id";

    // Anchors
    private static final String ANCHOR_NAME = "option_list";

    // Markers
    private static final String MARK_PLUGIN = "plugin";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_ENTRY = "entry";
    private static final String MARK_OPTION = "entry_select_option";
    private static final String MARK_ENTRY_ID = "entry_id";
    private static final String MARK_OPTION_ID = "option_id";

    // Miscellaneous
    private static final String DEFAULT_VALUE_OPTION_ORDER = "first";
    private static final String REGEX_ID = "^[\\d]+$";
    private static final String EMPTY_STRING = "";
    private static final String UNAUTHORIZED = "Unauthorized";

    /**
     * Get the authorized {@link EntrySelectOption}, filtered by quicklinks workgroup
     *
     * @param request
     *            The {@link HttpServletRequest}
     * @param strPermissionType
     *            The type of permission (see {@link QuicklinksResourceIdService} class)
     * @return The {@link EntrySelectOption} or null if user have no access
     */
    private EntrySelectOption getAuthorizedEntry( HttpServletRequest request, String strPermissionType ) throws AccessDeniedException
    {
        String strIdEntrySelectOption = request.getParameter( PARAMETER_OPTION_ID );
        String strIdEntry = request.getParameter( PARAMETER_ENTRY_ID );

        if ( ( strIdEntrySelectOption == null ) || !strIdEntrySelectOption.matches( REGEX_ID ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        int nIdEntrySelectOption = Integer.parseInt( strIdEntrySelectOption );
        int nIdEntry = Integer.parseInt( strIdEntry );
        EntrySelectOption entrySelectOption = EntrySelectOptionHome.findByPrimaryKey( nIdEntrySelectOption, nIdEntry, getPlugin( ) );
        IEntry entry = EntryHome.findByPrimaryKey( entrySelectOption.getIdEntry( ), getPlugin( ) );
        Quicklinks quicklinks = QuicklinksHome.findByPrimaryKey( entry.getIdQuicklinks( ), getPlugin( ) );

        if ( !AdminWorkgroupService.isAuthorized( quicklinks, getUser( ) )
                || !RBACService.isAuthorized( Quicklinks.RESOURCE_TYPE, String.valueOf( entry.getId( ) ), strPermissionType, getUser( ) ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        return entrySelectOption;
    }

    /**
     * Move the entry down
     * 
     * @param request
     *            The Http servlet request
     * @return The redirect url
     */
    public String doGoDownSelectOption( HttpServletRequest request ) throws AccessDeniedException
    {
        EntrySelectOption entrySelectOption = getAuthorizedEntry( request, QuicklinksResourceIdService.PERMISSION_MODIFY );

        EntrySelectOptionHome.goDown( entrySelectOption.getId( ), entrySelectOption.getIdEntry( ), getPlugin( ) );

        UrlItem url = new UrlItem( JSP_URL_MODIFY );
        url.addParameter( PARAMETER_ENTRY_ID, entrySelectOption.getIdEntry( ) );
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
    public String doGoUpSelectOption( HttpServletRequest request ) throws AccessDeniedException
    {
        EntrySelectOption entrySelectOption = getAuthorizedEntry( request, QuicklinksResourceIdService.PERMISSION_MODIFY );

        EntrySelectOptionHome.goUp( entrySelectOption.getId( ), entrySelectOption.getIdEntry( ), getPlugin( ) );

        UrlItem url = new UrlItem( JSP_URL_MODIFY );
        url.addParameter( PARAMETER_ENTRY_ID, entrySelectOption.getIdEntry( ) );
        url.setAnchor( ANCHOR_NAME );

        return url.getUrl( );
    }

    /**
     * Get the {@link EntrySelectOption} creation page
     * 
     * @param request
     *            The HTTP servlet request
     * @return The HTML template
     */
    public String getCreateSelectOption( HttpServletRequest request ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin( );
        String strIdEntry = request.getParameter( PARAMETER_ENTRY_ID );

        if ( ( strIdEntry == null ) || !strIdEntry.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        IEntry entry = EntryHome.findByPrimaryKey( Integer.parseInt( strIdEntry ), plugin );

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
        setPageTitleProperty( MESSAGE_PAGE_TITLE_CREATE_SELECT_OPTION );

        model.put( MARK_LOCALE, getLocale( ) );
        model.put( MARK_PLUGIN, plugin );
        model.put( MARK_ENTRY, entry );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_SELECT_OPTION, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Processes the {@link Entry} creation
     * 
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     */
    public String doCreateSelectOption( HttpServletRequest request ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin( );
        String strIdEntry = request.getParameter( PARAMETER_ENTRY_ID );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strUrl = request.getParameter( PARAMETER_URL );

        if ( ( strIdEntry == null ) || !strIdEntry.matches( REGEX_ID ) || StringUtils.isEmpty( strTitle ) || StringUtils.isEmpty( strUrl ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        IEntry entry = EntryHome.findByPrimaryKey( Integer.parseInt( strIdEntry ), plugin );

        if ( ( entry == null ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        if ( !RBACService.isAuthorized( Quicklinks.RESOURCE_TYPE, String.valueOf( entry.getIdQuicklinks( ) ), QuicklinksResourceIdService.PERMISSION_MODIFY,
                getUser( ) ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        EntrySelectOption option = new EntrySelectOption( );
        option.setTitle( strTitle );
        option.setUrl( strUrl );
        option.setIdEntry( entry.getId( ) );

        if ( AppPropertiesService.getProperty( PROPERTY_OPTION_ORDER_DEFAULT_VALUE, DEFAULT_VALUE_OPTION_ORDER ).equals( DEFAULT_VALUE_OPTION_ORDER ) )
        {
            option.setIdOrder( EntrySelectOptionHome.FIRST_ORDER );
        }
        else
        {
            option.setIdOrder( EntrySelectOptionHome.findByEntry( entry.getId( ), plugin ).size( ) );
        }

        EntrySelectOptionHome.create( option, plugin );

        UrlItem url = new UrlItem( JSP_URL_MODIFY );
        url.addParameter( PARAMETER_QUICKLINKS_ID, entry.getIdQuicklinks( ) );
        url.addParameter( PARAMETER_ENTRY_ID, entry.getId( ) );
        url.setAnchor( ANCHOR_NAME );

        return url.getUrl( );
    }

    /**
     * Get the {@link Entry} modification page
     * 
     * @param request
     *            The HTTP servlet request
     * @return The HTML template
     */
    public String getModifySelectOption( HttpServletRequest request ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin( );
        EntrySelectOption entrySelectOption = getAuthorizedEntry( request, QuicklinksResourceIdService.PERMISSION_MODIFY );
        IEntry entry = EntryHome.findByPrimaryKey( entrySelectOption.getIdEntry( ), plugin );
        HashMap<String, Object> model = new HashMap<>( );
        setPageTitleProperty( MESSAGE_PAGE_TITLE_MODIFY_SELECT_OPTION );

        model.put( MARK_LOCALE, getLocale( ) );
        model.put( MARK_PLUGIN, plugin );
        model.put( MARK_ENTRY, entry );
        model.put( MARK_OPTION, entrySelectOption );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_SELECT_OPTION, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Processes the {@link Entry} modification
     * 
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     */
    public String doModifySelectOption( HttpServletRequest request ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin( );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strUrl = request.getParameter( PARAMETER_URL );
        EntrySelectOption entrySelectOption = getAuthorizedEntry( request, QuicklinksResourceIdService.PERMISSION_MODIFY );
        IEntry entry = EntryHome.findByPrimaryKey( entrySelectOption.getIdEntry( ), plugin );

        if ( ( strTitle == null ) || strTitle.equals( EMPTY_STRING ) || ( strUrl == null ) || strUrl.equals( EMPTY_STRING ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        entrySelectOption.setTitle( strTitle );
        entrySelectOption.setUrl( strUrl );
        entrySelectOption.setIdEntry( entry.getId( ) );

        EntrySelectOptionHome.update( entrySelectOption, plugin );

        UrlItem url = new UrlItem( JSP_URL_MODIFY );
        url.addParameter( PARAMETER_QUICKLINKS_ID, entry.getIdQuicklinks( ) );
        url.addParameter( PARAMETER_ENTRY_ID, entry.getId( ) );
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
    public String doConfirmRemoveSelectOption( HttpServletRequest request ) throws AccessDeniedException
    {
        EntrySelectOption entrySelectOption = getAuthorizedEntry( request, QuicklinksResourceIdService.PERMISSION_MODIFY );

        HashMap<String, Object> model = new HashMap<>( );
        model.put( MARK_OPTION_ID, String.valueOf( entrySelectOption.getId( ) ) );
        model.put( MARK_ENTRY_ID, String.valueOf( entrySelectOption.getIdEntry( ) ) );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRMATION_REMOVE_OPTION, JSP_URL_PREFIX + JSP_URL_DELETE_OPTION,
                AdminMessage.TYPE_QUESTION, model );
    }

    /**
     * Processes the {@link Entry} removal
     * 
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     */
    public String doRemoveSelectOption( HttpServletRequest request ) throws AccessDeniedException
    {
        EntrySelectOption entrySelectOption = getAuthorizedEntry( request, QuicklinksResourceIdService.PERMISSION_MODIFY );

        EntrySelectOptionHome.remove( entrySelectOption.getId( ), entrySelectOption.getIdEntry( ), getPlugin( ) );

        UrlItem url = new UrlItem( JSP_URL_MODIFY );
        url.addParameter( PARAMETER_ENTRY_ID, entrySelectOption.getIdEntry( ) );
        url.setAnchor( ANCHOR_NAME );

        return url.getUrl( );
    }

    /**
     * Processes the {@link EntrySelectOption} copy
     *
     * @param request
     *            The {@link HttpServletRequest}
     * @return The Url to redirect to
     */
    public String doCopyEntrySelectOption( HttpServletRequest request ) throws AccessDeniedException
    {
        EntrySelectOption entrySelectOption = getAuthorizedEntry( request, QuicklinksResourceIdService.PERMISSION_MODIFY );

        entrySelectOption.copy( entrySelectOption.getIdEntry( ), getPlugin( ),
                I18nService.getLocalizedString( MESSAGE_COPY, getLocale( ) ) + entrySelectOption.getTitle( ) );

        UrlItem url = new UrlItem( JSP_URL_MODIFY );
        url.addParameter( PARAMETER_ENTRY_ID, entrySelectOption.getIdEntry( ) );
        url.setAnchor( ANCHOR_NAME );

        return url.getUrl( );
    }

    // -------------------------- Private methods --------------------------
}

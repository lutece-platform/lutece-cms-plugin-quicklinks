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

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.quicklinks.business.EntryHome;
import fr.paris.lutece.plugins.quicklinks.business.EntrySelectOption;
import fr.paris.lutece.plugins.quicklinks.business.EntrySelectOptionHome;
import fr.paris.lutece.plugins.quicklinks.business.IEntry;
import fr.paris.lutece.plugins.quicklinks.business.Quicklinks;
import fr.paris.lutece.plugins.quicklinks.business.QuicklinksHome;
import fr.paris.lutece.plugins.quicklinks.service.QuicklinksResourceIdService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.url.UrlItem;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * This class provides the user interface to manage the {@link EntrySelectOption} of a select entry ( create, modify, remove)
 */
@RequestScoped
@Named
@Controller( controllerJsp = "ManageEntrySelectOptions.jsp", controllerPath = "jsp/admin/plugins/quicklinks/", right = "QUICKLINKS_MANAGEMENT", securityTokenEnabled = true )
public class QuicklinksEntrySelectJspBean extends MVCAdminJspBean
{
    private static final long serialVersionUID = -3970588972565885665L;

    // Templates
    private static final String TEMPLATE_CREATE_SELECT_OPTION = "admin/plugins/quicklinks/create_entry_select_option.html";
    private static final String TEMPLATE_MODIFY_SELECT_OPTION = "admin/plugins/quicklinks/modify_entry_select_option.html";

    // Views
    private static final String VIEW_CREATE_SELECT_OPTION = "createSelectOption";
    private static final String VIEW_MODIFY_SELECT_OPTION = "modifySelectOption";
    private static final String VIEW_CONFIRM_REMOVE_SELECT_OPTION = "confirmRemoveSelectOption";

    // Actions
    private static final String ACTION_CREATE_SELECT_OPTION = "createSelectOption";
    private static final String ACTION_MODIFY_SELECT_OPTION = "modifySelectOption";
    private static final String ACTION_REMOVE_SELECT_OPTION = "removeSelectOption";

    // Properties
    private static final String PROPERTY_OPTION_ORDER_DEFAULT_VALUE = "quicklinks.modify.entry.create.defaultValue.order";

    // Messages (I18n keys)
    private static final String MESSAGE_PAGE_TITLE_CREATE_SELECT_OPTION = "quicklinks.create_entry_select_option.pageTitle";
    private static final String MESSAGE_PAGE_TITLE_MODIFY_SELECT_OPTION = "quicklinks.modify_entry_select_option.pageTitle";
    private static final String MESSAGE_CONFIRMATION_REMOVE_OPTION = "quicklinks.entry_select.message.confirmRemoveEntrySelectOption";

    // Parameters
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

    // Miscellaneous
    private static final String DEFAULT_VALUE_OPTION_ORDER = "first";
    private static final String REGEX_ID = "^[\\d]+$";
    private static final String UNAUTHORIZED = "Unauthorized";

    /**
     * Get the {@link EntrySelectOption} creation page
     *
     * @param request
     *            The HTTP servlet request
     * @param model
     *            The model
     * @return The HTML template
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @View( VIEW_CREATE_SELECT_OPTION )
    public String getCreateSelectOption( HttpServletRequest request, Models model ) throws AccessDeniedException
    {
        IEntry entry = getAuthorizedEntry( request );

        if ( entry == null )
        {
            return redirectMandatoryFields( request );
        }

        model.put( MARK_LOCALE, getLocale( ) );
        model.put( MARK_PLUGIN, getPlugin( ) );
        model.put( MARK_ENTRY, entry );

        return getPage( MESSAGE_PAGE_TITLE_CREATE_SELECT_OPTION, TEMPLATE_CREATE_SELECT_OPTION, model );
    }

    /**
     * Processes the {@link EntrySelectOption} creation
     *
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @Action( ACTION_CREATE_SELECT_OPTION )
    public String doCreateSelectOption( HttpServletRequest request ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin( );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strUrl = request.getParameter( PARAMETER_URL );
        IEntry entry = getAuthorizedEntry( request );

        if ( ( entry == null ) || StringUtils.isEmpty( strTitle ) || StringUtils.isEmpty( strUrl ) )
        {
            return redirectMandatoryFields( request );
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

        return redirectToEntry( request, entry.getId( ) );
    }

    /**
     * Get the {@link EntrySelectOption} modification page
     *
     * @param request
     *            The HTTP servlet request
     * @param model
     *            The model
     * @return The HTML template
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @View( VIEW_MODIFY_SELECT_OPTION )
    public String getModifySelectOption( HttpServletRequest request, Models model ) throws AccessDeniedException
    {
        IEntry entry = getAuthorizedEntry( request );
        EntrySelectOption option = getOption( request, entry );

        if ( option == null )
        {
            return redirectMandatoryFields( request );
        }

        model.put( MARK_LOCALE, getLocale( ) );
        model.put( MARK_PLUGIN, getPlugin( ) );
        model.put( MARK_ENTRY, entry );
        model.put( MARK_OPTION, option );

        return getPage( MESSAGE_PAGE_TITLE_MODIFY_SELECT_OPTION, TEMPLATE_MODIFY_SELECT_OPTION, model );
    }

    /**
     * Processes the {@link EntrySelectOption} modification
     *
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @Action( ACTION_MODIFY_SELECT_OPTION )
    public String doModifySelectOption( HttpServletRequest request ) throws AccessDeniedException
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strUrl = request.getParameter( PARAMETER_URL );
        IEntry entry = getAuthorizedEntry( request );
        EntrySelectOption option = getOption( request, entry );

        if ( ( option == null ) || StringUtils.isEmpty( strTitle ) || StringUtils.isEmpty( strUrl ) )
        {
            return redirectMandatoryFields( request );
        }

        option.setTitle( strTitle );
        option.setUrl( strUrl );
        EntrySelectOptionHome.update( option, getPlugin( ) );

        return redirectToEntry( request, entry.getId( ) );
    }

    /**
     * Get the {@link EntrySelectOption} removal confirmation
     *
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @View( value = VIEW_CONFIRM_REMOVE_SELECT_OPTION, securityTokenAction = ACTION_REMOVE_SELECT_OPTION )
    public String getConfirmRemoveSelectOption( HttpServletRequest request ) throws AccessDeniedException
    {
        IEntry entry = getAuthorizedEntry( request );
        EntrySelectOption option = getOption( request, entry );

        if ( option == null )
        {
            return redirectMandatoryFields( request );
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_SELECT_OPTION ) );
        url.addParameter( PARAMETER_ENTRY_ID, entry.getId( ) );
        url.addParameter( PARAMETER_OPTION_ID, option.getId( ) );

        return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRMATION_REMOVE_OPTION, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Processes the {@link EntrySelectOption} removal
     *
     * @param request
     *            The HTTP servlet request
     * @return The URL to redirect to
     * @throws AccessDeniedException
     *             if unauthorized
     */
    @Action( ACTION_REMOVE_SELECT_OPTION )
    public String doRemoveSelectOption( HttpServletRequest request ) throws AccessDeniedException
    {
        IEntry entry = getAuthorizedEntry( request );
        EntrySelectOption option = getOption( request, entry );

        if ( option == null )
        {
            return redirectMandatoryFields( request );
        }

        EntrySelectOptionHome.remove( option.getId( ), entry.getId( ), getPlugin( ) );

        return redirectToEntry( request, entry.getId( ) );
    }

    /**
     * Get the entry of the request, once the user is checked against the workgroup and the permissions of its quicklinks
     *
     * @param request
     *            The {@link HttpServletRequest}
     * @return The entry, or null when the identifier is missing or unknown
     * @throws AccessDeniedException
     *             if the user has no access to the quicklinks of the entry
     */
    private IEntry getAuthorizedEntry( HttpServletRequest request ) throws AccessDeniedException
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

        if ( ( quicklinks == null ) || !AdminWorkgroupService.isAuthorized( quicklinks, (User) getUser( ) ) || !RBACService.isAuthorized(
                Quicklinks.RESOURCE_TYPE, String.valueOf( quicklinks.getId( ) ), QuicklinksResourceIdService.PERMISSION_MODIFY, (User) getUser( ) ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        return entry;
    }

    /**
     * Get the option of the request, within the given entry
     *
     * @param request
     *            The {@link HttpServletRequest}
     * @param entry
     *            The entry holding the option
     * @return The option, or null when the entry or the option is missing or unknown
     */
    private EntrySelectOption getOption( HttpServletRequest request, IEntry entry )
    {
        String strIdOption = request.getParameter( PARAMETER_OPTION_ID );

        if ( ( entry == null ) || ( strIdOption == null ) || !strIdOption.matches( REGEX_ID ) )
        {
            return null;
        }

        return EntrySelectOptionHome.findByPrimaryKey( Integer.parseInt( strIdOption ), entry.getId( ), getPlugin( ) );
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
     * Redirect to the option list of a select entry
     *
     * @param request
     *            The HTTP servlet request
     * @param nIdEntry
     *            The entry identifier
     * @return The redirection result
     */
    private String redirectToEntry( HttpServletRequest request, int nIdEntry )
    {
        UrlItem url = new UrlItem( QuicklinksJspBean.CONTROLLER_JSP );
        url.addParameter( MVCUtils.PARAMETER_VIEW, QuicklinksJspBean.VIEW_MODIFY_ENTRY );
        url.addParameter( PARAMETER_ENTRY_ID, nIdEntry );
        url.setAnchor( ANCHOR_NAME );

        return redirect( request, url.getUrl( ) );
    }
}

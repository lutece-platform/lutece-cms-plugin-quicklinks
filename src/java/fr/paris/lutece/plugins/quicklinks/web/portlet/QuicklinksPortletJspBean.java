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
package fr.paris.lutece.plugins.quicklinks.web.portlet;

import java.util.Collection;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.quicklinks.business.Quicklinks;
import fr.paris.lutece.plugins.quicklinks.business.QuicklinksFilter;
import fr.paris.lutece.plugins.quicklinks.business.QuicklinksHome;
import fr.paris.lutece.plugins.quicklinks.business.QuicklinksType;
import fr.paris.lutece.plugins.quicklinks.business.portlet.QuicklinksPortlet;
import fr.paris.lutece.plugins.quicklinks.business.portlet.QuicklinksPortletHome;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.portlet.PortletType;
import fr.paris.lutece.portal.business.portlet.PortletTypeHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.portlet.PortletJspBean;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.portal.service.security.SecurityTokenService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * This class provides the user interface to manage quicklinks Portlet
 */
@RequestScoped
@Named
public class QuicklinksPortletJspBean extends PortletJspBean
{
    private static final long serialVersionUID = -1659013399553752236L;

    ////////////////////////////////////////////////////////////////////////////
    // Constants
    public static final String RIGHT_MANAGE_QUICKLINKS = "QUICKLINKS_MANAGEMENT";
    private static final String MESSAGE_INVALID_TOKEN = "quicklinks.message.invalidToken";
    private static final String MESSAGE_PORTLET_NOT_FOUND = "quicklinks.message.portletNotFound";
    private static final String MESSAGE_PORTLET_TYPE_NOT_FOUND = "quicklinks.message.portletTypeNotFound";
    private static final String ACTION_CREATE_PORTLET = "quicklinks.createPortlet";
    private static final String ACTION_MODIFY_PORTLET = "quicklinks.modifyPortlet";

    // Markers
    private static final String MARK_ID_QUICKLINKS = "quicklinks_id";
    private static final String MARK_QUICKLINKS_LIST = "quicklinks_list";

    // Parameters
    private static final String PARAMETER_ID_QUICKLINKS = "quicklinks_id";

    ////////////////////////////////////////////////////////////////////////////
    // Class attributes

    /**
     * Returns the portlet creation quicklinks
     *
     * @param request
     *            The http request
     * @return The HTML quicklinks
     */
    public String getCreate( HttpServletRequest request )
    {
        HashMap<String, Object> model = new HashMap<>( );
        String strIdPage = request.getParameter( PARAMETER_PAGE_ID );
        String strIdPortletType = request.getParameter( PARAMETER_PORTLET_TYPE_ID );
        PortletType portletType = ( strIdPortletType == null ) ? null : PortletTypeHome.findByPrimaryKey( strIdPortletType );

        if ( ( portletType == null ) || ( portletType.getDoCreateUrl( ) == null ) )
        {
            return I18nService.getLocalizedString( MESSAGE_PORTLET_TYPE_NOT_FOUND, getLocale( ) );
        }

        Plugin plugin = PluginService.getPlugin( portletType.getPluginName( ) );

        // Set Quicklinks filter
        QuicklinksFilter filter = new QuicklinksFilter( );
        filter.setType( QuicklinksType.PORTLET );
        filter.setEnabled( true );

        Collection<Quicklinks> listQuicklinks = QuicklinksHome.findbyFilter( filter, plugin );
        listQuicklinks = AdminWorkgroupService.getAuthorizedCollection( listQuicklinks, (User) getUser( ) );

        ReferenceList referenceListQuicklinks = new ReferenceList( );

        for ( Quicklinks quicklinks : listQuicklinks )
        {
            referenceListQuicklinks.addItem( quicklinks.getId( ), quicklinks.getTitle( ) );
        }

        model.put( MARK_QUICKLINKS_LIST, referenceListQuicklinks );

        model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService( ).getToken( request, ACTION_CREATE_PORTLET ) );

        HtmlTemplate template = getCreateTemplate( strIdPage, strIdPortletType, model );

        return template.getHtml( );
    }

    /**
     * Returns the Download portlet modification quicklinks
     *
     * @param request
     *            The Http request
     * @return The HTML quicklinks
     */
    public String getModify( HttpServletRequest request )
    {
        Quicklinks quicklinks;
        HashMap<String, Object> model = new HashMap<>( );
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        int nPortletId = -1;

        if ( ( strPortletId != null ) && strPortletId.matches( "\\d+" ) )
        {
            nPortletId = Integer.parseInt( strPortletId );
        }

        QuicklinksPortlet portlet = findPortlet( nPortletId );

        if ( portlet == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_PORTLET_NOT_FOUND, AdminMessage.TYPE_STOP );
        }

        Plugin plugin = PluginService.getPlugin( portlet.getPluginName( ) );
        quicklinks = QuicklinksHome.findByPrimaryKey( portlet.getQuicklinksId( ), plugin );

        // Set Quicklinks filter
        QuicklinksFilter filter = new QuicklinksFilter( );
        filter.setType( QuicklinksType.PORTLET );
        filter.setEnabled( true );

        Collection<Quicklinks> listQuicklinks = QuicklinksHome.findbyFilter( filter, plugin );
        listQuicklinks = AdminWorkgroupService.getAuthorizedCollection( listQuicklinks, (User) getUser( ) );

        ReferenceList referenceListQuicklinks = new ReferenceList( );

        for ( Quicklinks quicklinksFromList : listQuicklinks )
        {
            referenceListQuicklinks.addItem( quicklinksFromList.getId( ), quicklinksFromList.getTitle( ) );
        }

        model.put( MARK_QUICKLINKS_LIST, referenceListQuicklinks );
        model.put( MARK_ID_QUICKLINKS, quicklinks.getId( ) );

        model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService( ).getToken( request, ACTION_MODIFY_PORTLET ) );

        HtmlTemplate template = getModifyTemplate( portlet, model );

        return template.getHtml( );
    }

    /**
     * Process portlet's creation
     *
     * @param request
     *            The Http request
     * @return The Jsp management URL of the process result
     */
    public String doCreate( HttpServletRequest request )
    {
        if ( !getSecurityTokenService( ).validate( request, ACTION_CREATE_PORTLET ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_TOKEN, AdminMessage.TYPE_STOP );
        }

        QuicklinksPortlet portlet = new QuicklinksPortlet( );
        String strPageId = request.getParameter( PARAMETER_PAGE_ID );
        String strQuicklinksId = request.getParameter( PARAMETER_ID_QUICKLINKS );
        int nPageId = -1;
        int nQuicklinksId = -1;

        // get portlet common attributes
        String strErrorUrl = setPortletCommonData( request, portlet );

        try
        {
            nPageId = Integer.parseInt( strPageId );
            nQuicklinksId = Integer.parseInt( strQuicklinksId );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        if ( ( strErrorUrl == null ) && ( nQuicklinksId == -1 ) )
        {
            strErrorUrl = AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        portlet.setPageId( nPageId );
        portlet.setQuicklinksId( nQuicklinksId );

        // Creating portlet
        QuicklinksPortletHome.getInstance( ).create( portlet );

        // Displays the page with the new Portlet
        return getPageUrl( nPageId );
    }

    /**
     * Process portlet's modification
     *
     * @param request
     *            The http request
     * @return Management's Url
     */
    public String doModify( HttpServletRequest request )
    {
        if ( !getSecurityTokenService( ).validate( request, ACTION_MODIFY_PORTLET ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_TOKEN, AdminMessage.TYPE_STOP );
        }

        // recovers portlet attributes
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        String strQuicklinksId = request.getParameter( PARAMETER_ID_QUICKLINKS );
        int nPortletId = -1;
        int nQuicklinksId = -1;

        try
        {
            nPortletId = Integer.parseInt( strPortletId );
            nQuicklinksId = Integer.parseInt( strQuicklinksId );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        QuicklinksPortlet portlet = (QuicklinksPortlet) PortletHome.findByPrimaryKey( nPortletId );

        // retrieve portlet common attributes
        String strErrorUrl = setPortletCommonData( request, portlet );

        if ( ( strErrorUrl == null ) && ( nQuicklinksId == -1 ) )
        {
            strErrorUrl = AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        portlet.setQuicklinksId( nQuicklinksId );
        // updates the portlet
        portlet.update( );

        // displays the page with the potlet updated
        return getPageUrl( portlet.getPageId( ) );
    }

    /**
     * Finds the portlet of that identifier, without throwing on a bad one.
     *
     * PortletHome.findByPrimaryKey of the core dereferences the row it loaded without checking it exists, so an
     * unknown identifier raises a NullPointerException there rather than returning null.
     *
     * @param nPortletId
     *            the portlet identifier
     * @return the portlet, or null when the identifier is unknown
     */
    private QuicklinksPortlet findPortlet( int nPortletId )
    {
        if ( nPortletId <= 0 )
        {
            return null;
        }

        try
        {
            return (QuicklinksPortlet) PortletHome.findByPrimaryKey( nPortletId );
        }
        catch( NullPointerException | ClassCastException e )
        {
            AppLogService.debug( "Unknown quicklinks portlet {}", nPortletId );

            return null;
        }
    }
}

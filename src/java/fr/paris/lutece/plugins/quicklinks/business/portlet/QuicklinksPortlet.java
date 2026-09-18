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
package fr.paris.lutece.plugins.quicklinks.business.portlet;

import fr.paris.lutece.plugins.quicklinks.business.EntryFilter;
import fr.paris.lutece.plugins.quicklinks.business.EntryHome;
import fr.paris.lutece.plugins.quicklinks.business.IEntry;
import fr.paris.lutece.plugins.quicklinks.business.Quicklinks;
import fr.paris.lutece.plugins.quicklinks.business.QuicklinksHome;
import fr.paris.lutece.portal.business.portlet.PortletHtmlContent;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

/**
 * This class represents business objects Quicklinks Portlet
 */
public class QuicklinksPortlet extends PortletHtmlContent
{
    private static final String TEMPLATE_PORTLET = "skin/plugins/quicklinks/portlet/quicklinks_portlet.html";
    private static final String MARK_QUICKLINKS = "quicklinks";
    private static final String MARK_ITEMS = "items";
    private static final String MARK_CSS_STYLE = "css_style";

    /////////////////////////////////////////////////////////////////////////////////

    // Constants
    private int _nPortletId;
    private int _nQuicklinksId;
    private int _nStatus;

    /**
     * Returns the html content of the quicklinks portlet.
     *
     * @param request
     *            The HTTP Servlet request
     * @return the html content, empty when the quicklinks is disabled
     */
    @Override
    public String getHtmlContent( HttpServletRequest request )
    {
        Plugin plugin = PluginService.getPlugin( this.getPluginName( ) );
        Locale locale = ( request != null ) ? request.getLocale( ) : I18nService.getDefaultLocale( );

        Quicklinks quicklinks = QuicklinksHome.findByPrimaryKey( getQuicklinksId( ), plugin );

        if ( ( quicklinks == null ) || !quicklinks.isEnabled( ) )
        {
            return "";
        }

        EntryFilter filter = new EntryFilter( );
        filter.setIdQuicklinks( quicklinks.getId( ) );
        filter.setIdParent( EntryHome.ROOT_PARENT_ID );

        List<QuicklinksItem> listItems = new ArrayList<>( );

        for ( IEntry entry : EntryHome.findByFilter( filter, plugin ) )
        {
            listItems.add( buildItem( entry, plugin, locale ) );
        }

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_QUICKLINKS, quicklinks );
        model.put( MARK_CSS_STYLE, quicklinks.getCssStyle( ) );
        model.put( MARK_ITEMS, listItems );

        return AppTemplateService.getTemplate( TEMPLATE_PORTLET, locale, model ).getHtml( );
    }

    /**
     * Builds the view model of one entry and of its children.
     *
     * @param entry
     *            The entry
     * @param plugin
     *            The plugin
     * @param locale
     *            The locale
     * @return the item
     */
    private QuicklinksItem buildItem( IEntry entry, Plugin plugin, Locale locale )
    {
        QuicklinksItem item = new QuicklinksItem( );
        item.setId( entry.getId( ) );
        item.setTitle( entry.getTitle( ) );
        item.setContent( entry.getHtml( plugin, locale ) );

        for ( IEntry child : entry.getChilds( plugin ) )
        {
            item.getChildren( ).add( buildItem( child, plugin, locale ) );
        }

        return item;
    }

    /**
     * Updates the current instance of the quicklinks portlet object
     */
    public void update( )
    {
        QuicklinksPortletHome.getInstance( ).update( this );
    }

    /**
     * Removes the current instance of the the quicklinks portlet object
     */
    public void remove( )
    {
        QuicklinksPortletHome.getInstance( ).remove( this );
    }

    /**
     * Returns the nPortletId
     *
     * @return The nPortletId
     */
    public int getPortletId( )
    {
        return _nPortletId;
    }

    /**
     * Sets the IdPortlet
     *
     * @param nPortletId
     *            The nPortletId
     */
    public void setPortletId( int nPortletId )
    {
        _nPortletId = nPortletId;
    }

    /**
     * Returns the QuicklinksId
     *
     * @return The QuicklinksId
     */
    public int getQuicklinksId( )
    {
        return _nQuicklinksId;
    }

    /**
     * Sets the QuicklinksId
     *
     * @param nQuicklinksId
     *            The nQuicklinksId
     */
    public void setQuicklinksId( int nQuicklinksId )
    {
        _nQuicklinksId = nQuicklinksId;
    }

    /**
     * Returns the Status
     *
     * @return The Status
     */
    @Override
    public int getStatus( )
    {
        return _nStatus;
    }

    /**
     * Sets the Status
     *
     * @param nStatus
     *            The Status
     */
    @Override
    public void setStatus( int nStatus )
    {
        _nStatus = nStatus;
    }

    /**
     * View model of one quicklinks entry: its own html content and its children.
     */
    public static class QuicklinksItem
    {
        private int _nId;
        private String _strTitle;
        private String _strContent;
        private final List<QuicklinksItem> _listChildren = new ArrayList<>( );

        /**
         * @return the entry identifier
         */
        public int getId( )
        {
            return _nId;
        }

        /**
         * @param nId
         *            the entry identifier
         */
        public void setId( int nId )
        {
            _nId = nId;
        }

        /**
         * @return the entry title
         */
        public String getTitle( )
        {
            return _strTitle;
        }

        /**
         * @param strTitle
         *            the entry title
         */
        public void setTitle( String strTitle )
        {
            _strTitle = strTitle;
        }

        /**
         * @return the html the entry renders
         */
        public String getContent( )
        {
            return _strContent;
        }

        /**
         * @param strContent
         *            the html the entry renders
         */
        public void setContent( String strContent )
        {
            _strContent = strContent;
        }

        /**
         * @return the children of this entry
         */
        public List<QuicklinksItem> getChildren( )
        {
            return _listChildren;
        }
    }
}

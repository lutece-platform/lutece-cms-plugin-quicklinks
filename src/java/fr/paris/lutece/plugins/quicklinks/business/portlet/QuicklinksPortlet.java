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

import fr.paris.lutece.plugins.quicklinks.business.Quicklinks;
import fr.paris.lutece.plugins.quicklinks.business.QuicklinksHome;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.util.xml.XmlUtil;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 * This class represents business objects Quicklinks Portlet
 */
public class QuicklinksPortlet extends Portlet
{
    /////////////////////////////////////////////////////////////////////////////////
    // Xml Tags
    private static final String TAG_QUICKLINKS_PORTLET = "quicklinks-portlet";
    private static final String TAG_QUICKLINKS_PORTLET_CONTENT = "quicklinks-portlet-content";

    /////////////////////////////////////////////////////////////////////////////////

    // Constants
    private int _nPortletId;
    private int _nQuicklinksId;
    private int _nStatus;

    /**
     * Returns the Xml code of the quicklinks portlet without XML heading
     *
     * @param request
     *            The HTTP Servlet request
     * @return the Xml code of the quicklinks portlet content
     */
    public String getXml( HttpServletRequest request )
    {
        Plugin plugin = PluginService.getPlugin( this.getPluginName( ) );
        Locale locale;

        if ( request != null )
        {
            locale = request.getLocale( );
        }
        else
        {
            locale = I18nService.getDefaultLocale( );
        }

        StringBuffer strXml = new StringBuffer( );
        XmlUtil.beginElement( strXml, TAG_QUICKLINKS_PORTLET );

        Quicklinks quicklinks = QuicklinksHome.findByPrimaryKey( getQuicklinksId( ), plugin );

        if ( quicklinks.isEnabled( ) )
        {
            XmlUtil.addElement( strXml, TAG_QUICKLINKS_PORTLET_CONTENT, quicklinks.getXml( plugin, locale ).toString( ) );
        }

        XmlUtil.endElement( strXml, TAG_QUICKLINKS_PORTLET );

        return addPortletTags( strXml );
    }

    /**
     * Returns the Xml code of the quicklinks portlet with XML heading
     *
     * @param request
     *            The HTTP Servlet Request
     * @return the Xml code of the Articles List portlet
     */
    public String getXmlDocument( HttpServletRequest request )
    {
        return XmlUtil.getXmlHeader( ) + getXml( request );
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
}

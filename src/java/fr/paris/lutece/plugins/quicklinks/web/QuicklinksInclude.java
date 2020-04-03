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

import fr.paris.lutece.plugins.quicklinks.business.Quicklinks;
import fr.paris.lutece.plugins.quicklinks.business.QuicklinksFilter;
import fr.paris.lutece.plugins.quicklinks.business.QuicklinksHome;
import fr.paris.lutece.plugins.quicklinks.business.QuicklinksType;
import fr.paris.lutece.plugins.quicklinks.service.QuicklinksPlugin;
import fr.paris.lutece.portal.service.content.PageData;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.includes.PageInclude;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import javax.xml.parsers.ParserConfigurationException;

/**
 * This class provides the user interface to manage quicklinks features
 */
public class QuicklinksInclude implements PageInclude
{
    // Properties
    private static final String PROPERTY_QUICKLINKS_MARKER_PREFIX = "quicklinks.include.marker.prefix";

    // Templates
    private static final String TEMPLATE_QUICKLINKS_INCLUDE = "skin/plugins/quicklinks/quicklinks_include.html";

    // Markers
    private static final String MARK_QUICKLINKS = "quicklinks";
    private static final String MARK_PLUGIN = "plugin";
    private static final String MARK_LOCALE = "locale";

    // Default Marker prefix if nothing defined in properties file
    private static final String MARK_DEFAULT_QUICKLINKS_MARKER_PREFIX = "quicklinks_";

    /**
     * Substitute specific Freemarker markers in the page template.
     * 
     * @param rootModel
     *            the HashMap containing markers to substitute
     * @param data
     *            A PageData object containing applications data
     * @param nMode
     *            The current mode
     * @param request
     *            The HTTP request
     */
    public void fillTemplate( Map<String, Object> rootModel, PageData data, int nMode, HttpServletRequest request )
    {
        Plugin plugin = PluginService.getPlugin( QuicklinksPlugin.PLUGIN_NAME );
        Locale locale = getLocale( request );

        QuicklinksFilter filter = getFilter( request );

        Collection<Quicklinks> listQuicklinks = QuicklinksHome.findbyFilter( filter, plugin );

        // For each quicklinks, generate HTML code and put marker with the associated code in rootModel
        for ( Quicklinks quicklinks : listQuicklinks )
        {
            rootModel.put( getQuicklinksMarkerPrefix( ) + String.valueOf( quicklinks.getId( ) ), getTemplateHtmlForQuicklinks( quicklinks, plugin, locale ) );
        }
    }

    /**
     * Get the quicklinks marker prefix to use in template
     * 
     * @return the {@link Quicklinks} marker prefix
     */
    public static String getQuicklinksMarkerPrefix( )
    {
        return AppPropertiesService.getProperty( PROPERTY_QUICKLINKS_MARKER_PREFIX, MARK_DEFAULT_QUICKLINKS_MARKER_PREFIX );
    }

    /**
     * Generate the HTML code for the specified {@link Quicklinks}
     * 
     * @param quicklinks
     *            The {@link Quicklinks}
     * @param plugin
     *            The {@link Plugin}
     * @param locale
     *            The {@link Locale}
     * @return The HTML code
     */
    private String getTemplateHtmlForQuicklinks( Quicklinks quicklinks, Plugin plugin, Locale locale )
    {
        HashMap<String, Object> model = new HashMap<>( );
        model.put( MARK_QUICKLINKS, quicklinks );

        InputSource in = new InputSource( new ByteArrayInputStream( quicklinks.getXml( plugin, locale ).toString( ).getBytes( ) ) );

        try
        {
            model.put( MARK_QUICKLINKS, freemarker.ext.dom.NodeModel.parse( in ) );
        }
        catch( ParserConfigurationException | IOException | SAXException e )
        {
            AppLogService.error( e );
        }

        model.put( MARK_PLUGIN, plugin );
        model.put( MARK_LOCALE, locale );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_QUICKLINKS_INCLUDE, Locale.getDefault( ), model );

        return templateList.getHtml( );
    }

    /**
     * Get the locale from request of the default locale if request is null
     * 
     * @param request
     *            The {@link HttpServletRequest}
     * @return The locale
     */
    private Locale getLocale( HttpServletRequest request )
    {
        Locale locale = null;

        if ( request != null )
        {
            locale = request.getLocale( );
        }
        else
        {
            locale = I18nService.getDefaultLocale( );
        }

        return locale;
    }

    /**
     * Get the {@link QuicklinksFilter}
     * 
     * @param request
     *            The {@link HttpServletRequest}
     * @return The {@link QuicklinksFilter}
     */
    private QuicklinksFilter getFilter( HttpServletRequest request )
    {
        QuicklinksFilter filter = new QuicklinksFilter( );

        filter.setType( QuicklinksType.INCLUDE );
        filter.setEnabled( true );

        if ( SecurityService.isAuthenticationEnable( ) && ( request != null ) )
        {
            LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

            if ( user != null )
            {
                String [ ] rolesUser = SecurityService.getInstance( ).getRolesByUser( user );
                filter.setRoleKeys( rolesUser );
            }
        }

        return filter;
    }
}

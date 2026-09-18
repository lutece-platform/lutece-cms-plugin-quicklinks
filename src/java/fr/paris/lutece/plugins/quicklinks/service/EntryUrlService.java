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
package fr.paris.lutece.plugins.quicklinks.service;

import fr.paris.lutece.plugins.quicklinks.business.EntryHome;
import fr.paris.lutece.plugins.quicklinks.business.EntryUrl;
import fr.paris.lutece.plugins.quicklinks.business.IEntry;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.image.ImageResourceManager;
import fr.paris.lutece.portal.service.image.ImageResourceProvider;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.util.url.UrlItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.ServletContext;

/**
 * Service for Url entry types. Provide ImageResource managemenent
 *
 */
@ApplicationScoped
public class EntryUrlService implements ImageResourceProvider
{
    private static final String IMAGE_RESOURCE_TYPE_ID = "quicklinks_entryurl_image";

    /**
     * Creates a new instance of EntryUrlService
     */
    EntryUrlService( )
    {
        ImageResourceManager.registerProvider( this );
    }

    /**
     * Instantiates the bean when the application context starts, so the image provider is registered before the
     * first request: a CDI bean nothing injects is never created.
     *
     * @param context
     *            the servlet context being initialised
     */
    public void initializedService( @Observes @Initialized( ApplicationScoped.class ) ServletContext context )
    {
    }


    /**
     * Return the Resource id
     * 
     * @param nIdResource
     *            The resource identifier
     * @return The Resource Image
     */
    public ImageResource getImageResource( int nIdResource )
    {
        Plugin plugin = PluginService.getPlugin( QuicklinksPlugin.PLUGIN_NAME );
        IEntry entry = EntryHome.findByPrimaryKey( nIdResource, plugin );

        if ( entry instanceof EntryUrl )
        {
            EntryUrl entryUrl = (EntryUrl) entry;

            return entryUrl.getImageResource( );
        }

        return null;
    }

    /**
     * Return the Resource Type id
     * 
     * @return The Resource Type Id
     */
    public String getResourceTypeId( )
    {
        return IMAGE_RESOURCE_TYPE_ID;
    }

    /**
     * Management of the image associated to the {@link EntryUrl}
     * 
     * @param nEntryUrl
     *            The {@link EntryUrl} identifier
     * @return The url of the resource
     */
    public static String getResourceImageEntryUrl( int nEntryUrl )
    {
        String strResourceType = CDI.current( ).select( EntryUrlService.class ).get( ).getResourceTypeId( );
        UrlItem url = new UrlItem( Parameters.IMAGE_SERVLET );
        url.addParameter( Parameters.RESOURCE_TYPE, strResourceType );
        url.addParameter( Parameters.RESOURCE_ID, Integer.toString( nEntryUrl ) );

        return url.getUrlWithEntity( );
    }
}

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
package fr.paris.lutece.plugins.quicklinks.business;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * The class Entry Text
 *
 */
public class EntryInternalLink extends Entry
{
    private static final String EMPTY_STRING = "";

    // Templates
    private static final String TEMPLATE_DISPLAY = "skin/plugins/quicklinks/entry_internal_link.html";

    // Markers
    private static final String MARK_ENTRY_INTERNAL_LINK = "entry_internal_link";
    private static final String MARK_BASE_URL = "base_url";

    // Properties

    // Parameters
    private static final String PARAMETER_CONTENT = "content";

    // Attributes
    private String _strTitle;
    private String _strContent;

    @Override
    public EntryInternalLink clone( ) throws CloneNotSupportedException
    {
        EntryInternalLink entryInternalLink = new EntryInternalLink( );
        entryInternalLink.setContent( getContent( ) );
        entryInternalLink.setEntryType( getEntryType( ) );
        entryInternalLink.setIdOrder( getIdOrder( ) );
        entryInternalLink.setIdParent( getIdParent( ) );
        entryInternalLink.setIdQuicklinks( getIdQuicklinks( ) );
        entryInternalLink.setTitle( getTitle( ) );

        return entryInternalLink;
    }

    @Override
    public String getHtml( Plugin plugin, Locale locale )
    {
        HashMap<String, Object> model = new HashMap<>( );

        model.put( MARK_ENTRY_INTERNAL_LINK, this );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DISPLAY, locale, model );

        return template.getHtml( );
    }

    @Override
    public String getTitle( )
    {
        return _strTitle;
    }

    @Override
    public void setTitle( String strTitle )
    {
        this._strTitle = strTitle;
    }

    @Override
    public String setSpecificParameters( HttpServletRequest request )
    {
        String strContent = request.getParameter( PARAMETER_CONTENT );

        // Check content
        if ( ( strContent == null ) || strContent.equals( EMPTY_STRING ) )
        {
            return Messages.MANDATORY_FIELDS;
        }

        this.setContent( strContent );

        return null;
    }

    @Override
    public void getSpecificParameters( HttpServletRequest request, Map<String, Object> model, Plugin plugin )
    {
        model.put( MARK_BASE_URL, AppPathService.getBaseUrl( request ) );
    }

    /**
     * @param strContent
     *            the _strContent to set
     */
    public void setContent( String strContent )
    {
        this._strContent = strContent;
    }

    /**
     * @return the _strContent
     */
    public String getContent( )
    {
        return _strContent;
    }

    @Override
    public void createSpecificParameters( Plugin plugin )
    {
        EntryInternalLinkHome.create( this, plugin );
    }

    @Override
    public void loadSpecificParameters( Plugin plugin )
    {
        EntryInternalLinkHome.load( this, plugin );
    }

    @Override
    public void removeSpecificParameters( Plugin plugin )
    {
        EntryInternalLinkHome.remove( getId( ), plugin );
    }

    @Override
    public void updateSpecificParameters( Plugin plugin )
    {
        EntryInternalLinkHome.update( this, plugin );
    }
}

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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 * The class Entry Text
 *
 */
public class EntryText extends Entry
{
    private static final String EMPTY_STRING = "";

    // Templates
    private static final String TEMPLATE_DISPLAY = "skin/plugins/quicklinks/entry_text.html";

    // Parameters
    private static final String PARAMETER_DESCRIPTION = "description";

    // Markers
    private static final String MARK_ENTRY_TEXT = "entry_text";
    private String _strTitle;
    private String _strDescription;

    /**
     * @param strDescription
     *            the _strDescription to set
     */
    public void setDescription( String strDescription )
    {
        _strDescription = strDescription;
    }

    /**
     * @return the _strDescription
     */
    public String getDescription( )
    {
        return _strDescription;
    }

    @Override
    public EntryText clone( ) throws CloneNotSupportedException
    {
        EntryText entryText = new EntryText( );
        entryText.setDescription( getDescription( ) );
        entryText.setEntryType( getEntryType( ) );
        entryText.setIdOrder( getIdOrder( ) );
        entryText.setIdParent( getIdParent( ) );
        entryText.setIdQuicklinks( getIdQuicklinks( ) );
        entryText.setTitle( getTitle( ) );

        return entryText;
    }

    @Override
    public String getHtml( Plugin plugin, Locale locale )
    {
        HashMap<String, Object> model = new HashMap<>( );

        model.put( MARK_ENTRY_TEXT, this );

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
        String strDescription = request.getParameter( PARAMETER_DESCRIPTION );

        if ( ( strDescription == null ) || strDescription.equals( EMPTY_STRING ) )
        {
            return Messages.MANDATORY_FIELDS;
        }

        this.setDescription( strDescription );

        return null;
    }

    @Override
    public void createSpecificParameters( Plugin plugin )
    {
        EntryTextHome.create( this, plugin );
    }

    @Override
    public void loadSpecificParameters( Plugin plugin )
    {
        EntryTextHome.load( this, plugin );
    }

    @Override
    public void removeSpecificParameters( Plugin plugin )
    {
        EntryTextHome.remove( getId( ), plugin );
    }

    @Override
    public void updateSpecificParameters( Plugin plugin )
    {
        EntryTextHome.update( this, plugin );
    }
}

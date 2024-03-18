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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

/**
 * The class Entry Select
 * 
 */
public class EntrySelect extends Entry
{
    private static final int DEFAULT_PAGINATOR_STYLES_PER_PAGE = 10;
    private static final String EMPTY_STRING = "";

    // JSP URL
    private static final String JSP_URL_PREFIX = "jsp/admin/plugins/quicklinks/";
    private static final String JSP_URL_MODIFY = "ModifyEntry.jsp";

    // Templates
    private static final String TEMPLATE_DISPLAY = "skin/plugins/quicklinks/entry_select.html";

    // Markers
    private static final String MARK_ENTRY_SELECT = "entry_select";
    private static final String MARK_OPTION_LIST = "option_list";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";

    // Parameters
    private static final String PARAMETER_TARGET = "target";
    private static final String PROPERTY_STYLES_PER_PAGE = "paginator.style.itemsPerPage";

    // Parameters
    private static final String PARAMETER_ENTRY_ID = "entry_id";

    private int _nItemsPerPage;
    private String _strCurrentPageIndex;

    // Attributes
    private String _strTitle;
    private String _strTarget;

    @Override
    public EntrySelect clone( ) throws CloneNotSupportedException
    {
        EntrySelect entrySelect = new EntrySelect( );
        entrySelect.setEntryType( getEntryType( ) );
        entrySelect.setIdOrder( getIdOrder( ) );
        entrySelect.setIdParent( getIdParent( ) );
        entrySelect.setIdQuicklinks( getIdQuicklinks( ) );
        entrySelect.setTitle( getTitle( ) );
        entrySelect.setTarget( getTarget( ) );

        return entrySelect;
    }

    /**
     * Copy an Entry
     * 
     * @param nIdQuicklinks
     *            The {@link Quicklinks} identifier
     * @param plugin
     *            The {@link Plugin}
     * @param strNewName
     *            The new name
     * @return The {@link IEntry} copy
     */
    @Override
    public IEntry copy( int nIdQuicklinks, Plugin plugin, String strNewName )
    {
        IEntry copy = super.copy( nIdQuicklinks, plugin, strNewName );

        for ( EntrySelectOption entrySelectOption : EntrySelectOptionHome.findByEntry( getId( ), plugin ) )
        {
            entrySelectOption.copy( copy.getId( ), plugin );
        }

        return copy;
    }

    @Override
    public String getHtml( Plugin plugin, Locale locale )
    {
        HashMap<String, Object> model = new HashMap<>( );

        model.put( MARK_ENTRY_SELECT, this );
        model.put( MARK_OPTION_LIST, EntrySelectOptionHome.findByEntry( getId( ), plugin ) );

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

    /**
     * Get the target
     * 
     * @return the target
     */
    public String getTarget( )
    {
        return _strTarget;
    }

    /**
     * Set the target
     * 
     * @param strTarget the target to set
     */
    public void setTarget( String strTarget )
    {
        this._strTarget = strTarget;
    }

    @Override
    public String setSpecificParameters( HttpServletRequest request )
    {
        String strTarget = request.getParameter( PARAMETER_TARGET );
        
        if ( strTarget == null )
        {
            setTarget( EMPTY_STRING );
        }
        else
        {
            setTarget( strTarget );
        }

        return null;
    }

    @Override
    public void getSpecificParameters( HttpServletRequest request, Map<String, Object> model, Plugin plugin )
    {
        Collection<EntrySelectOption> listEntrySelectOption = EntrySelectOptionHome.findByEntry( getId( ), plugin );

        int defaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_STYLES_PER_PAGE, DEFAULT_PAGINATOR_STYLES_PER_PAGE );
        _strCurrentPageIndex = AbstractPaginator.getPageIndex( request, AbstractPaginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = AbstractPaginator.getItemsPerPage( request, AbstractPaginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, defaultItemsPerPage );

        UrlItem url = new UrlItem( JSP_URL_PREFIX + JSP_URL_MODIFY );
        url.addParameter( PARAMETER_ENTRY_ID, request.getParameter( PARAMETER_ENTRY_ID ) );
        url.addParameter( AbstractPaginator.PARAMETER_ITEMS_PER_PAGE, request.getParameter( AbstractPaginator.PARAMETER_ITEMS_PER_PAGE ) );

        Paginator paginator = new Paginator( (List<EntrySelectOption>) listEntrySelectOption, _nItemsPerPage, url.getUrl( ), AbstractPaginator.PARAMETER_PAGE_INDEX,
                _strCurrentPageIndex );

        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, String.valueOf( _nItemsPerPage ) );
        model.put( MARK_OPTION_LIST, paginator.getPageItems( ) );
    }

    @Override
    public void createSpecificParameters( Plugin plugin )
    {
        EntrySelectHome.create( this, plugin );
    }

    @Override
    public void loadSpecificParameters( Plugin plugin )
    {
        EntrySelectHome.load( this, plugin );
    }

    @Override
    public void removeSpecificParameters( Plugin plugin )
    {
        EntrySelectHome.remove( getId( ), plugin );
    }

    @Override
    public void updateSpecificParameters( Plugin plugin )
    {
        EntrySelectHome.update( this, plugin );
    }
}

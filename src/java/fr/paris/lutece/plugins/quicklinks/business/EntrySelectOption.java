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
import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * The class Entry Select option
 *
 */
public class EntrySelectOption
{
    private static final String EMPTY_STRING = "";

    // Attributes
    private int _nId;
    private int _nIdEntry;
    private String _strTitle;
    private String _strUrl;
    private int _nIdOrder;

    /**
     * @return the url
     */
    public String getUrl( )
    {
        return _strUrl;
    }

    /**
     * @param strUrl
     *            the url to set
     */
    public void setUrl( String strUrl )
    {
        this._strUrl = strUrl;
    }

    @Override
    protected EntrySelectOption clone( ) throws CloneNotSupportedException
    {
        EntrySelectOption entrySelectOption = new EntrySelectOption( );
        entrySelectOption.setIdEntry( getIdEntry( ) );
        entrySelectOption.setIdOrder( getIdOrder( ) );
        entrySelectOption.setTitle( getTitle( ) );
        entrySelectOption.setUrl( getUrl( ) );

        return entrySelectOption;
    }

    /**
     * Copy an {@link EntrySelectOption}
     *
     * @param nIdQuicklinks
     *            The {@link Entry} identifier
     * @param plugin
     *            The {@link Plugin}
     * @return The {@link EntrySelectOption} copy
     */
    public EntrySelectOption copy( int nIdEntry, Plugin plugin )
    {
        return copy( nIdEntry, plugin, null );
    }

    /**
     * Copy an {@link EntrySelectOption}
     *
     * @param nIdQuicklinks
     *            The {@link Entry} identifier
     * @param plugin
     *            The {@link Plugin}
     * @param strNewName
     *            The new name
     * @return The {@link EntrySelectOption} copy
     */
    public EntrySelectOption copy( int nIdEntry, Plugin plugin, String strNewName )
    {
        EntrySelectOption copy = null;

        try
        {
            copy = this.clone( );
        }
        catch( CloneNotSupportedException e )
        {
            AppLogService.error( "Object EntrySelectOption does not support clone process." );
        }

        // Update title
        if ( ( strNewName != null ) && !strNewName.equals( EMPTY_STRING ) )
        {
            copy.setTitle( strNewName );
        }

        copy.setIdEntry( nIdEntry );
        copy = EntrySelectOptionHome.create( copy, plugin );

        return copy;
    }

    /**
     * Get the title
     * 
     * @return The title
     */
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * Set the title
     * 
     * @param strTitle
     */
    public void setTitle( String strTitle )
    {
        this._strTitle = strTitle;
    }

    /**
     * @param nIdOrder
     *            the nIdOrder to set
     */
    public void setIdOrder( int nIdOrder )
    {
        this._nIdOrder = nIdOrder;
    }

    /**
     * @return the nIdOrder
     */
    public int getIdOrder( )
    {
        return _nIdOrder;
    }

    /**
     * @param nIdEntry
     *            the _nIdEntry to set
     */
    public void setIdEntry( int nIdEntry )
    {
        this._nIdEntry = nIdEntry;
    }

    /**
     * @return the _nIdEntry
     */
    public int getIdEntry( )
    {
        return _nIdEntry;
    }

    /**
     * @param nId
     *            the _nId to set
     */
    public void setId( int nId )
    {
        this._nId = nId;
    }

    /**
     * @return the _nId
     */
    public int getId( )
    {
        return _nId;
    }
}

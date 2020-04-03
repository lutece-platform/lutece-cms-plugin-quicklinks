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
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppLogService;

public class Entry implements IEntry, Cloneable
{
    private static final String EMPTY_STRING = "";
    private int _nIdQuicklinks;
    private int _nId;
    private int _nIdOrder;
    private EntryType _entryType;
    private int _nIdParent;

    /**
     * @return the _nIdParent
     */
    public int getIdParent( )
    {
        return _nIdParent;
    }

    /**
     * @param nIdParent
     *            the _nIdParent to set
     */
    public void setIdParent( int nIdParent )
    {
        _nIdParent = nIdParent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.quicklinks.business.IEntry#getIdQuicklinks()
     */
    public int getIdQuicklinks( )
    {
        return _nIdQuicklinks;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.quicklinks.business.IEntry#setIdQuicklinks(int)
     */
    public void setIdQuicklinks( int nIdQuicklinks )
    {
        _nIdQuicklinks = nIdQuicklinks;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.quicklinks.business.IEntry#getIdEntry()
     */
    public int getId( )
    {
        return _nId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.quicklinks.business.IEntry#setIdEntry(int)
     */
    public void setId( int nIdEntry )
    {
        _nId = nIdEntry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.quicklinks.business.IEntry#getIdOrder()
     */
    public int getIdOrder( )
    {
        return _nIdOrder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.quicklinks.business.IEntry#setIdOrder(int)
     */
    public void setIdOrder( int nIdOrder )
    {
        _nIdOrder = nIdOrder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.quicklinks.business.IEntry#getType()
     */
    public EntryType getEntryType( )
    {
        return _entryType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.quicklinks.business.IEntry#setType(int)
     */
    public void setEntryType( EntryType entryType )
    {
        _entryType = entryType;
    }

    // ############ Abstract methods ############

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.quicklinks.business.IEntry#getTitle()
     */
    public String getTitle( )
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.quicklinks.business.IEntry#setTitle(java.lang.String)
     */
    public void setTitle( String strTitle )
    {
    }

    /**
     * Clone the entry
     */
    public IEntry clone( ) throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException( );
    }

    /**
     * Copy an Entry
     *
     * @param nIdQuicklinks
     *            The {@link Quicklinks} identifier
     * @param plugin
     *            the {@link Plugin}
     * @return The {@link IEntry} copy
     */
    public IEntry copy( int nIdQuicklinks, Plugin plugin )
    {
        return copy( nIdQuicklinks, plugin, null );
    }

    /**
     * Copy an Entry
     *
     * @param nIdQuicklinks
     *            The {@link Quicklinks} identifier
     * @param plugin
     *            the {@link Plugin}
     * @param strNewName
     *            The new name
     * @return The {@link IEntry} copy
     */
    public IEntry copy( int nIdQuicklinks, Plugin plugin, String strNewName )
    {
        IEntry copy = null;

        try
        {
            copy = this.clone( );
        }
        catch( CloneNotSupportedException e )
        {
            AppLogService.error( "Object Entry does not support clone process." );
        }

        // Update title
        copy.setIdQuicklinks( nIdQuicklinks );

        if ( ( strNewName != null ) && !strNewName.equals( EMPTY_STRING ) )
        {
            copy.setTitle( strNewName );
        }

        copy = EntryHome.create( copy, plugin );

        for ( IEntry entry : EntryHome.findByParentId( this.getId( ), plugin ) )
        {
            entry.setIdParent( copy.getId( ) );
            entry.copy( nIdQuicklinks, plugin );
        }

        return copy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.quicklinks.business.IEntry#getHtml(fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public String getHtml( Plugin plugin, Locale locale )
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.quicklinks.business.IEntry#getChilds(fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public Collection<IEntry> getChilds( Plugin plugin )
    {
        return EntryHome.findByParentId( getId( ), plugin );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.quicklinks.business.IEntry#getParent(fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public IEntry getParent( Plugin plugin )
    {
        return EntryHome.findByPrimaryKey( getIdParent( ), plugin );
    }

    /**
     * Set the specific parameters for the entry
     * 
     * @param request
     *            the {@link HttpServletRequest}
     * @return The i18n message if error occurs, null else
     */
    public String setSpecificParameters( HttpServletRequest request )
    {
        return null;
    }

    /**
     * Get the specific parameters for the entry
     * 
     * @param request
     *            the {@link HttpServletRequest}
     * @param model
     *            The {@link HashMap} model
     * @param plugin
     *            The {@link Plugin}
     */
    public void getSpecificParameters( HttpServletRequest request, Map<String, Object> model, Plugin plugin )
    {
    }

    /**
     * Load the specific parameters
     *
     * @param plugin
     *            The {@link Plugin}
     */
    public void loadSpecificParameters( Plugin plugin )
    {
    }

    /**
     * Create the specific parameters
     *
     * @param plugin
     *            The {@link Plugin}
     */
    public void createSpecificParameters( Plugin plugin )
    {
    }

    /**
     * Remove the specific parameters
     *
     * @param plugin
     *            The {@link Plugin}
     */
    public void removeSpecificParameters( Plugin plugin )
    {
    }

    /**
     * Update the specific parameters
     *
     * @param plugin
     *            The {@link Plugin}
     */
    public void updateSpecificParameters( Plugin plugin )
    {
    }
}

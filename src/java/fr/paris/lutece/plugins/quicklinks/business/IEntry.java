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
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * Interface for Entry
 *
 */
public interface IEntry
{
    /**
     * @return the nIdQuicklinks
     */
    public abstract int getIdQuicklinks( );

    /**
     * @param nIdQuicklinks
     *            the nIdQuicklinks to set
     */
    public abstract void setIdQuicklinks( int nIdQuicklinks );

    /**
     * @return the nId
     */
    public abstract int getId( );

    /**
     * @param nId
     *            the nIdEntry to set
     */
    public abstract void setId( int nId );

    /**
     * @return the nIdOrder
     */
    public abstract int getIdOrder( );

    /**
     * @param nIdOrder
     *            the nIdOrder to set
     */
    public abstract void setIdOrder( int nIdOrder );

    /**
     * @return the _nIdParent
     */
    public abstract int getIdParent( );

    /**
     * @param nIdParent
     *            the _nIdParent to set
     */
    public abstract void setIdParent( int nIdParent );

    /**
     * @param plugin
     *            the {@link Plugin}
     * @return the parent Entry
     */
    public abstract IEntry getParent( Plugin plugin );

    /**
     * Get the child abstract Entry list
     * 
     * @param plugin
     *            The Plugin
     * @return A {@link Collection} of childs
     */
    public abstract Collection<IEntry> getChilds( Plugin plugin );

    /**
     * @return the entryType
     */
    public abstract EntryType getEntryType( );

    /**
     * @param nType
     *            the entryType to set
     */
    public abstract void setEntryType( EntryType entryType );

    /**
     * @return the strTitle
     */
    public abstract String getTitle( );

    /**
     * @param strTitle
     *            the strTitle to set
     */
    public abstract void setTitle( String strTitle );

    /**
     * Get the HTML code of the entry
     * 
     * @param plugin
     *            The {@link Plugin}
     * @param locale
     *            The {@link Locale}
     * @return HTML code of the entry
     */
    public abstract String getHtml( Plugin plugin, Locale locale );

    /**
     * get the specific parameters for the entry
     * 
     * @param request
     *            the {@link HttpServletRequest}
     * @param model
     *            The HashMap to fill
     * @param plugin
     *            The {@link Plugin}
     */
    public abstract void getSpecificParameters( HttpServletRequest request, Map<String, Object> model, Plugin plugin );

    /**
     * Set the specific parameters for the entry
     * 
     * @param request
     *            the {@link HttpServletRequest}
     * @return The i18n message if error occurs, null else
     */
    public abstract String setSpecificParameters( HttpServletRequest request );

    /**
     *
     * @return
     * @throws CloneNotSupportedException
     */
    public IEntry clone( ) throws CloneNotSupportedException;

    /**
     * Copy an Entry
     *
     * @param nIdQuicklinks
     *            The {@link Quicklinks} identifier
     * @param plugin
     *            The {@link Plugin}
     * @return The {@link IEntry} copy
     */
    public IEntry copy( int nIdQuicklinks, Plugin plugin );

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
    public IEntry copy( int nIdQuicklinks, Plugin plugin, String strNewName );

    /**
     * Load the specific parameters
     *
     * @param plugin
     *            The {@link Plugin}
     */
    public abstract void loadSpecificParameters( Plugin plugin );

    /**
     * Create the specific parameters
     *
     * @param plugin
     *            The {@link Plugin}
     */
    public abstract void createSpecificParameters( Plugin plugin );

    /**
     * Remove the specific parameters
     *
     * @param plugin
     *            The {@link Plugin}
     */
    public abstract void removeSpecificParameters( Plugin plugin );

    /**
     * Update the specific parameters
     *
     * @param plugin
     *            The {@link Plugin}
     */
    public abstract void updateSpecificParameters( Plugin plugin );
}

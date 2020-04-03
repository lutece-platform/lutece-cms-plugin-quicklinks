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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 *
 * class EntrySelectOptionHome
 *
 */
public final class EntrySelectOptionHome
{
    public static final int FIRST_ORDER = 0;
    public static final int ROOT_PARENT_ID = 0;
    protected static final int STEP = 1;

    // Static variable pointed at the DAO instance
    private static IEntrySelectOptionDAO _dao = SpringContextService.getBean( "quicklinks.entrySelectOptionDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private EntrySelectOptionHome( )
    {
    }

    /**
     * Insert a new record in the table.
     * 
     * @param option
     *            The Instance of the object {@link Entry}
     * @param plugin
     *            The {@link Plugin} using this data access service
     * @return The {@link EntrySelectOption}
     */
    public static EntrySelectOption create( EntrySelectOption option, Plugin plugin )
    {
        // Move down all orders in new list
        for ( EntrySelectOption optionChangeOrder : findByEntry( option.getIdEntry( ), plugin ) )
        {
            if ( optionChangeOrder.getIdOrder( ) >= option.getIdOrder( ) )
            {
                optionChangeOrder.setIdOrder( optionChangeOrder.getIdOrder( ) + STEP );
                _dao.store( optionChangeOrder, plugin );
            }
        }

        return _dao.insert( option, plugin );
    }

    /**
     * Delete the {@link Entry} specified by identifier
     * 
     * @param nId
     *            The identifier
     * @param nIdEntry
     *            The Entry identifier
     * @param plugin
     *            The {@link Plugin} using this data access service
     */
    public static void remove( int nId, int nIdEntry, Plugin plugin )
    {
        EntrySelectOption optionOld = findByPrimaryKey( nId, nIdEntry, plugin );

        // Move up all orders in old list
        for ( EntrySelectOption optionChangeOrder : findByEntry( optionOld.getIdEntry( ), plugin ) )
        {
            if ( optionChangeOrder.getIdOrder( ) > optionOld.getIdOrder( ) )
            {
                optionChangeOrder.setIdOrder( optionChangeOrder.getIdOrder( ) - STEP );
                _dao.store( optionChangeOrder, plugin );
            }
        }

        _dao.delete( nId, nIdEntry, plugin );
    }

    /**
     * Delete the all the {@link EntrySelectOption} option from an entry
     * 
     * @param nId
     *            The entry identifier
     * @param plugin
     *            The {@link Plugin} using this data access service
     */
    public static void removeByEntryId( int nId, Plugin plugin )
    {
        Collection<EntrySelectOption> listOptions = findByEntry( nId, plugin );

        for ( EntrySelectOption entrySelectOption : listOptions )
        {
            remove( entrySelectOption.getId( ), entrySelectOption.getIdEntry( ), plugin );
        }
    }

    /**
     * Update The {@link Entry}
     * 
     * @param option
     *            The {@link Entry} to update
     * @param plugin
     *            The {@link Plugin} using this data access service
     */
    public static void update( EntrySelectOption option, Plugin plugin )
    {
        if ( option == null )
        {
            return;
        }

        EntrySelectOption optionOld = findByPrimaryKey( option.getId( ), option.getIdEntry( ), plugin );

        if ( optionOld == null )
        {
            return;
        }

        // Move up all orders in old list
        Collection<EntrySelectOption> listOptionMove = findByEntry( optionOld.getIdEntry( ), plugin );

        for ( EntrySelectOption optionChangeOrder : listOptionMove )
        {
            if ( optionChangeOrder.getIdOrder( ) > optionOld.getIdOrder( ) )
            {
                optionChangeOrder.setIdOrder( optionChangeOrder.getIdOrder( ) - STEP );
                _dao.store( optionChangeOrder, plugin );
            }
        }

        // Move down all orders in new list
        for ( EntrySelectOption optionChangeOrder : listOptionMove )
        {
            if ( optionChangeOrder.getIdOrder( ) >= option.getIdOrder( ) )
            {
                optionChangeOrder.setIdOrder( optionChangeOrder.getIdOrder( ) + STEP );
                _dao.store( optionChangeOrder, plugin );
            }
        }

        _dao.store( option, plugin );
    }

    /**
     * Get the max order of a option list entry
     * 
     * @param nIdEntry
     *            The id of the {@link Entry}
     * @param plugin
     *            The {@link Plugin}
     * @return the max order
     */
    private static int countEntry( int nIdEntry, Plugin plugin )
    {
        Collection<EntrySelectOption> listOptions = findByEntry( nIdEntry, plugin );

        return ( listOptions == null ) ? 0 : listOptions.size( );
    }

    /**
     * Move down an {@link IEntry} into the list
     * 
     * @param nId
     *            The id of the {@link IEntry}
     * @param nIdEntry
     *            The Entry identifier
     * @param plugin
     *            The plugin
     */
    public static void goDown( int nId, int nIdEntry, Plugin plugin )
    {
        EntrySelectOption optionDown = findByPrimaryKey( nId, nIdEntry, plugin );

        if ( optionDown == null )
        {
            return;
        }

        int nCountEntry = countEntry( optionDown.getIdEntry( ), plugin );

        if ( optionDown.getIdOrder( ) >= nCountEntry )
        {
            return;
        }

        optionDown.setIdOrder( optionDown.getIdOrder( ) + STEP );

        // Commit
        update( optionDown, plugin );
    }

    /**
     * Move up an {@link IEntry} into the list
     * 
     * @param nId
     *            The id of the {@link IEntry}
     * @param nIdEntry
     *            The Entry identifier
     * @param plugin
     *            The plugin
     */
    public static void goUp( int nId, int nIdEntry, Plugin plugin )
    {
        EntrySelectOption optionUp = findByPrimaryKey( nId, nIdEntry, plugin );

        if ( ( optionUp == null ) || ( optionUp.getIdOrder( ) <= FIRST_ORDER ) )
        {
            return;
        }

        optionUp.setIdOrder( optionUp.getIdOrder( ) - STEP );

        // Commit
        update( optionUp, plugin );
    }

    /**
     * Returns an instance of a EntrySelectOption whose identifier is specified in parameter
     *
     * @param nKey
     *            The EntrySelectOption primary key
     * @param nIdEntry
     *            The Entry identifier
     * @param plugin
     *            the Plugin
     * @return an instance of EntrySelectOption
     */
    public static EntrySelectOption findByPrimaryKey( int nId, int nIdEntry, Plugin plugin )
    {
        return _dao.load( nId, nIdEntry, plugin );
    }

    /**
     * Returns a list of all EntrySelectOption
     *
     * @param plugin
     *            the plugin
     * @return the {@link Collection} of EntrySelectOption
     */
    public static Collection<EntrySelectOption> findAll( Plugin plugin )
    {
        return _dao.select( plugin );
    }

    /**
     * Returns a list of all EntrySelectOption
     *
     * @param nIdEntry
     *            the entry Id
     * @param plugin
     *            the plugin
     * @return the {@link Collection} of EntrySelectOption
     */
    public static Collection<EntrySelectOption> findByEntry( int nIdEntry, Plugin plugin )
    {
        return _dao.selectByEntry( nIdEntry, plugin );
    }
}

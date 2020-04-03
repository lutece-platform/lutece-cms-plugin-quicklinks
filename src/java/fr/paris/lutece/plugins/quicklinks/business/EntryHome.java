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

import java.util.ArrayList;
import java.util.Collection;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;

/**
 *
 * class EntryHome
 *
 */
public final class EntryHome
{
    public static final int FIRST_ORDER = 0;
    public static final int ROOT_PARENT_ID = 0;
    protected static final int STEP = 1;

    // Static variable pointed at the DAO instance
    private static IEntryDAO _dao = SpringContextService.getBean( "quicklinks.entryDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private EntryHome( )
    {
    }

    /**
     * Returns an instance of a {@link Entry} whose identifier is specified in parameter
     *
     * @param nKey
     *            The {@link Entry} primary key
     * @param plugin
     *            the {@link Plugin}
     * @return an instance of {@link Entry}
     */
    public static IEntry findByPrimaryKey( int nKey, Plugin plugin )
    {
        IEntry entry = _dao.load( nKey, plugin );
        EntryType entryType = EntryTypeHome.findByPrimaryKey( entry.getEntryType( ).getId( ), plugin );
        entry.setEntryType( entryType );

        return setSpecificEntry( entry, plugin );
    }

    /**
     * Load the specific entry and return it
     * 
     * @param entryType
     *            The type of {@link Entry}
     * @param plugin
     *            The {@link Plugin}
     * @return The new empty specific entry
     */
    public static IEntry getSpecificEntry( EntryType entryType, Plugin plugin )
    {
        IEntry entrySpecific = null;

        try
        {
            entrySpecific = (IEntry) Class.forName( entryType.getClassName( ) ).newInstance( );
        }
        catch( IllegalAccessException | InstantiationException | ClassNotFoundException e )
        {
            AppLogService.error( e );
            return null;
        }

        entrySpecific.loadSpecificParameters( plugin );

        return entrySpecific;
    }

    /**
     * Insert a new record in the table.
     * 
     * @param entry
     *            The Instance of the object {@link Entry}
     * @param plugin
     *            The {@link Plugin} using this data access service
     */
    public static IEntry create( IEntry entry, Plugin plugin )
    {
        // Move down all orders in new list
        for ( IEntry entryChangeOrder : ( entry.getIdParent( ) == 0 ) ? findByQuicklinksId( entry.getIdQuicklinks( ), plugin )
                : findByParentId( entry.getIdParent( ), plugin ) )
        {
            if ( entryChangeOrder.getIdOrder( ) >= entry.getIdOrder( ) )
            {
                entryChangeOrder.setIdOrder( entryChangeOrder.getIdOrder( ) + STEP );
                _dao.store( entryChangeOrder, plugin );
            }
        }

        _dao.insert( entry, plugin );

        entry.createSpecificParameters( plugin );

        return entry;
    }

    /**
     * Delete the {@link Entry} specified by identifier
     * 
     * @param nId
     *            The identifier
     * @param plugin
     *            The {@link Plugin} using this data access service
     */
    public static void remove( int nId, Plugin plugin )
    {
        IEntry entryOld = findByPrimaryKey( nId, plugin );

        // Move up all orders in old list
        for ( IEntry entryChangeOrder : ( entryOld.getIdParent( ) == 0 ) ? findByQuicklinksId( entryOld.getIdQuicklinks( ), plugin )
                : findByParentId( entryOld.getIdParent( ), plugin ) )
        {
            if ( entryChangeOrder.getIdOrder( ) > entryOld.getIdOrder( ) )
            {
                entryChangeOrder.setIdOrder( entryChangeOrder.getIdOrder( ) - STEP );
                _dao.store( entryChangeOrder, plugin );
            }
        }

        _dao.delete( nId, plugin );

        entryOld.removeSpecificParameters( plugin );
    }

    /**
     * Update The {@link Entry}
     * 
     * @param entry
     *            The {@link Entry} to update
     * @param plugin
     *            The {@link Plugin} using this data access service
     */
    public static void update( IEntry entry, Plugin plugin )
    {
        if ( entry == null )
        {
            return;
        }

        IEntry entryOld = findByPrimaryKey( entry.getId( ), plugin );

        if ( entryOld == null )
        {
            return;
        }

        // Move up all orders in old list
        Collection<IEntry> listEntryMoveUp;

        if ( entryOld.getIdParent( ) == 0 )
        {
            EntryFilter filter = new EntryFilter( );
            filter.setIdQuicklinks( entryOld.getIdQuicklinks( ) );
            filter.setIdParent( entryOld.getIdParent( ) );
            listEntryMoveUp = findByFilter( filter, plugin );
        }
        else
        {
            listEntryMoveUp = findByParentId( entryOld.getIdParent( ), plugin );
        }

        for ( IEntry entryChangeOrder : listEntryMoveUp )
        {
            if ( entryChangeOrder.getIdOrder( ) > entryOld.getIdOrder( ) )
            {
                entryChangeOrder.setIdOrder( entryChangeOrder.getIdOrder( ) - STEP );
                _dao.store( entryChangeOrder, plugin );
            }
        }

        // Move down all orders in new list
        Collection<IEntry> listEntryMoveDown;

        if ( ( entry.getIdParent( ) == 0 ) )
        {
            EntryFilter filter = new EntryFilter( );
            filter.setIdQuicklinks( entry.getIdQuicklinks( ) );
            filter.setIdParent( entry.getIdParent( ) );
            listEntryMoveDown = findByFilter( filter, plugin );
        }
        else
        {
            listEntryMoveDown = findByParentId( entry.getIdParent( ), plugin );
        }

        for ( IEntry entryChangeOrder : listEntryMoveDown )
        {
            if ( entryChangeOrder.getIdOrder( ) >= entry.getIdOrder( ) )
            {
                entryChangeOrder.setIdOrder( entryChangeOrder.getIdOrder( ) + STEP );
                _dao.store( entryChangeOrder, plugin );
            }
        }

        _dao.store( entry, plugin );

        entry.updateSpecificParameters( plugin );
    }

    /**
     * Find All {@link Entry} for a quicklinks
     * 
     * @param nIdQuicklinks
     *            The {@link Quicklinks} identifier
     * @param plugin
     *            The {@link Plugin} using this data access service
     * @return A {@link Collection} of {@link Entry}
     */
    public static Collection<IEntry> findByQuicklinksId( int nIdQuicklinks, Plugin plugin )
    {
        EntryFilter filter = new EntryFilter( );
        filter.setIdQuicklinks( nIdQuicklinks );

        return findByFilter( filter, plugin );
    }

    /**
     * Find All {@link Entry} who have the specified parent id
     * 
     * @param nIdParent
     *            The {@link Quicklinks} identifier
     * @param plugin
     *            The {@link Plugin} using this data access service
     * @return A {@link Collection} of {@link Entry}
     */
    public static Collection<IEntry> findByParentId( int nIdParent, Plugin plugin )
    {
        EntryFilter filter = new EntryFilter( );
        filter.setIdParent( nIdParent );

        return findByFilter( filter, plugin );
    }

    /**
     * Find All {@link Entry} and put them in a {@link ReferenceList}
     * 
     * @param plugin
     *            The {@link Plugin} using this data access service
     * @return A ReferenceList
     */
    public static ReferenceList findReferenceList( Plugin plugin )
    {
        EntryFilter filter = new EntryFilter( );
        ReferenceList referenceList = new ReferenceList( );

        for ( IEntry entry : _dao.findByFilter( filter, plugin ) )
        {
            referenceList.addItem( entry.getId( ), entry.getTitle( ) );
        }

        return referenceList;
    }

    /**
     * Find all {@link Entry} corresponding to {@link EntryFilter}
     * 
     * @param entryFilter
     *            The {@link EntryFilter}
     * @param plugin
     *            The {@link Plugin} using this data access service
     * @return A {@link Collection} of {@link Entry}
     */
    public static Collection<IEntry> findByFilter( EntryFilter entryFilter, Plugin plugin )
    {
        Collection<IEntry> listEntrySpecific = new ArrayList<>( );
        Collection<IEntry> listEntry = _dao.findByFilter( entryFilter, plugin );

        for ( IEntry entry : listEntry )
        {
            EntryType entryType = EntryTypeHome.findByPrimaryKey( entry.getEntryType( ).getId( ), plugin );
            entry.setEntryType( entryType );
            listEntrySpecific.add( setSpecificEntry( entry, plugin ) );
        }

        return listEntrySpecific;
    }

    /**
     * Load the specific entry and return it
     * 
     * @param entry
     *            The generic {@link Entry}
     * @param plugin
     *            The {@link Plugin}
     * @return The specific entry
     */
    private static IEntry setSpecificEntry( IEntry entry, Plugin plugin )
    {
        IEntry entrySpecific = null;

        try
        {
            entrySpecific = (IEntry) Class.forName( entry.getEntryType( ).getClassName( ) ).newInstance( );
        }
        catch( IllegalAccessException | InstantiationException | ClassNotFoundException e )
        {
            // class doesn't exist
            AppLogService.error( e );

            return null;
        }

        entrySpecific.setId( entry.getId( ) );
        entrySpecific.setIdQuicklinks( entry.getIdQuicklinks( ) );
        entrySpecific.setIdOrder( entry.getIdOrder( ) );
        entrySpecific.setIdParent( entry.getIdParent( ) );
        entrySpecific.setEntryType( entry.getEntryType( ) );

        entrySpecific.loadSpecificParameters( plugin );

        return entrySpecific;
    }

    /**
     * Returns an instance of the {@link IEntry} whose identifier is specified in parameter
     *
     * @param nIdQuicklinks
     *            The quicklinks identifier
     * @param nIdParent
     *            The primary key of the parent {@link IEntry}
     * @param nOrder
     *            The order id
     * @param plugin
     *            The current plugin using this method
     * @return An instance of the {@link IEntry} which corresponds to the parent id and order id
     */
    public static IEntry findByOrder( int nIdQuicklinks, int nIdParent, int nOrder, Plugin plugin )
    {
        EntryFilter filter = new EntryFilter( );
        filter.setIdParent( nIdParent );
        filter.setIdOrder( nOrder );
        filter.setIdQuicklinks( nIdQuicklinks );

        Collection<IEntry> listEntry = findByFilter( filter, plugin );

        if ( listEntry.size( ) != 1 )
        {
            return null;
        }

        return listEntry.iterator( ).next( );
    }

    /**
     * Get the max order of a parent {@link IEntry}
     * 
     * @param nIdParent
     *            The id of the parent {@link IEntry}
     * @param nIdQuicklinks
     *            The id of the {@link Quicklinks}
     * @param plugin
     *            The {@link Plugin}
     * @return the max order
     */
    private static int countEntry( int nIdQuicklinks, int nIdParent, Plugin plugin )
    {
        EntryFilter filter = new EntryFilter( );
        filter.setIdParent( nIdParent );
        filter.setIdQuicklinks( nIdQuicklinks );

        Collection<IEntry> listEntry = findByFilter( filter, plugin );

        return listEntry.size( );
    }

    /**
     * Move down an {@link IEntry} into the list
     * 
     * @param nId
     *            The id of the {@link IEntry}
     * @param nIdQuicklinks
     *            The {@link Quicklinks} Id
     * @param plugin
     *            The plugin
     */
    public static void goDown( int nId, Plugin plugin )
    {
        IEntry entryDown = findByPrimaryKey( nId, plugin );

        if ( entryDown == null )
        {
            return;
        }

        int nCountEntry = countEntry( entryDown.getIdQuicklinks( ), entryDown.getIdParent( ), plugin );

        if ( entryDown.getIdOrder( ) >= nCountEntry )
        {
            return;
        }

        entryDown.setIdOrder( entryDown.getIdOrder( ) + STEP );

        // Commit
        update( entryDown, plugin );
    }

    /**
     * Move up an {@link IEntry} into the list
     * 
     * @param nId
     *            The id of the {@link IEntry}
     * @param nIdQuicklinks
     *            The {@link Quicklinks} Id
     * @param plugin
     *            The plugin
     */
    public static void goUp( int nId, Plugin plugin )
    {
        IEntry entryUp = findByPrimaryKey( nId, plugin );

        if ( ( entryUp == null ) || ( entryUp.getIdOrder( ) <= FIRST_ORDER ) )
        {
            return;
        }

        entryUp.setIdOrder( entryUp.getIdOrder( ) - STEP );

        // Commit
        update( entryUp, plugin );
    }

    /**
     * Set the {@link IEntry} into another parent {@link IEntry}
     * 
     * @param nId
     *            The {@link IEntry} to move
     * @param plugin
     *            The plugin
     */
    public static void goIn( int nId, Plugin plugin )
    {
        IEntry entryIn = findByPrimaryKey( nId, plugin );

        if ( entryIn == null )
        {
            return;
        }

        IEntry entryParent = findByOrder( entryIn.getIdQuicklinks( ), entryIn.getIdParent( ), entryIn.getIdOrder( ) + STEP, plugin );

        entryIn.setIdOrder( FIRST_ORDER );
        entryIn.setIdParent( entryParent.getId( ) );
        update( entryIn, plugin );
    }

    /**
     * Set the {@link IEntry} out of another parent {@link IEntry}
     * 
     * @param nId
     *            The {@link IEntry} to move
     * @param plugin
     *            The plugin
     */
    public static void goOut( int nId, Plugin plugin )
    {
        IEntry entryOut = findByPrimaryKey( nId, plugin );

        if ( entryOut == null )
        {
            return;
        }

        IEntry entryParent = findByPrimaryKey( entryOut.getIdParent( ), plugin );

        if ( ( entryParent == null ) )
        {
            return;
        }

        entryOut.setIdOrder( entryParent.getIdOrder( ) );
        entryOut.setIdParent( entryParent.getIdParent( ) );
        update( entryOut, plugin );
    }
}

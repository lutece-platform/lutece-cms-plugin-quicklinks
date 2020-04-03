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
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * Class EntrySelectDAO
 *
 */
public class EntrySelectOptionDAO implements IEntrySelectOptionDAO
{
    private static final String SQL_QUERY_NEW_PK = " SELECT max( id_option ) FROM quicklinks_entry_select_option WHERE id_entry = ? ";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT option_title,option_url,id_order "
            + "FROM quicklinks_entry_select_option WHERE id_option = ? AND id_entry = ?";
    private static final String SQL_QUERY_INSERT = " INSERT INTO quicklinks_entry_select_option ( id_entry, "
            + "id_option, option_title, option_url,id_order ) VALUES ( ?, ?, ?, ?, ? )";
    private static final String SQL_QUERY_DELETE = " DELETE FROM quicklinks_entry_select_option "
            + "WHERE id_option = ? AND id_entry = ? ";
    private static final String SQL_QUERY_UPDATE = " UPDATE quicklinks_entry_select_option SET option_title = ?, "
            + "option_url = ?, id_order = ? WHERE id_option = ? AND id_entry = ? ";
    private static final String SQL_QUERY_FIND_ALL = "SELECT id_entry, id_option, option_title, option_url, id_order "
            + "FROM quicklinks_entry_select_option ORDER BY id_entry, id_order ";
    private static final String SQL_QUERY_FIND_BY_ENTRY_ID = "SELECT id_option, option_title, option_url, id_order "
            + "FROM quicklinks_entry_select_option WHERE id_entry = ? ORDER BY id_order ";

    /**
     * Calculate a new primary key to add a new {@link Entry}
     * 
     * @param plugin The {@link Plugin} using this data access service
     * @return The new key.
     */
    public int newPrimaryKey( int nIdEntry, Plugin plugin )
    {
        int nKey = 1;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin ) )
        {
            daoUtil.setInt( 1, nIdEntry );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                // if the table is empty
                nKey = daoUtil.getInt( 1 ) + 1;
            }
        }
        return nKey;
    }

    /**
     * Load the data of the {@link EntrySelectOption} from the table
     *
     * @param nId      The identifier of the entry select option
     * @param nIdEntry The identifier of the {@link Entry}
     * @param plugin   the plugin
     * @return the instance of the {@link EntrySelectOption}
     */
    public EntrySelectOption load( int nId, int nIdEntry, Plugin plugin )
    {
        EntrySelectOption entrySelectOption = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin ) )
        {
            daoUtil.setInt( 1, nId );
            daoUtil.setInt( 2, nIdEntry );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                entrySelectOption = new EntrySelectOption( );
                entrySelectOption.setId( nId );
                entrySelectOption.setIdEntry( nIdEntry );
                entrySelectOption.setTitle( daoUtil.getString( 1 ) );
                entrySelectOption.setUrl( daoUtil.getString( 2 ) );
                entrySelectOption.setIdOrder( daoUtil.getInt( 3 ) );
            }
        }

        return entrySelectOption;
    }

    /**
     * Delete the {@link EntrySelectOption} whose identifier is specified in
     * parameter
     *
     * @param nId      The identifier of the {@link EntrySelectOption}
     * @param nIdEntry The identifier of the {@link Entry}
     * @param plugin   The {@link Plugin}
     */
    public void delete( int nId, int nIdEntry, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nId );
            daoUtil.setInt( 2, nIdEntry );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Insert the {@link EntrySelectOption}
     *
     * @param option The {@link EntrySelectOption} to insert
     * @param plugin The {@link Plugin}
     * @return The {@link EntrySelectOption}
     */
    public EntrySelectOption insert( EntrySelectOption entrySelectOption, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            int nParam = 1;
            entrySelectOption.setId( newPrimaryKey( entrySelectOption.getIdEntry( ), plugin ) );
            daoUtil.setInt( nParam++, entrySelectOption.getIdEntry( ) );
            daoUtil.setInt( nParam++, entrySelectOption.getId( ) );
            daoUtil.setString( nParam++, entrySelectOption.getTitle( ) );
            daoUtil.setString( nParam++, entrySelectOption.getUrl( ) );
            daoUtil.setInt( nParam++, entrySelectOption.getIdOrder( ) );

            daoUtil.executeUpdate( );
        }

        return entrySelectOption;
    }

    /**
     * Update the {@link EntrySelectOption}
     *
     * @param option The {@link EntrySelectOption} object
     * @param plugin The {@link Plugin}
     */
    public void store( EntrySelectOption entrySelectOption, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nParam = 1;
            daoUtil.setString( nParam++, entrySelectOption.getTitle( ) );
            daoUtil.setString( nParam++, entrySelectOption.getUrl( ) );
            daoUtil.setInt( nParam++, entrySelectOption.getIdOrder( ) );

            daoUtil.setInt( nParam++, entrySelectOption.getId( ) );
            daoUtil.setInt( nParam++, entrySelectOption.getIdEntry( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Get a complete list of {@link EntrySelectOption}
     * 
     * @param plugin The {@link Plugin}
     * @return A {@link Collection} of {@link EntrySelectOption}
     */
    public Collection<EntrySelectOption> select( Plugin plugin )
    {
        Collection<EntrySelectOption> listEntrySelectOption = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_ALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                EntrySelectOption entrySelectOption = new EntrySelectOption( );
                entrySelectOption.setIdEntry( daoUtil.getInt( 1 ) );
                entrySelectOption.setId( daoUtil.getInt( 2 ) );
                entrySelectOption.setTitle( daoUtil.getString( 3 ) );
                entrySelectOption.setUrl( daoUtil.getString( 4 ) );
                entrySelectOption.setIdOrder( daoUtil.getInt( 5 ) );
                listEntrySelectOption.add( entrySelectOption );
            }

        }

        return listEntrySelectOption;
    }

    /**
     * Get a list of {@link EntrySelectOption} specified by entry id
     * 
     * @param nIdEntry The entry id
     * @param plugin   The {@link Plugin}
     * @return A {@link Collection} of {@link EntrySelectOption}
     */
    public Collection<EntrySelectOption> selectByEntry( int nIdEntry, Plugin plugin )
    {
        Collection<EntrySelectOption> listEntrySelectOption = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_ENTRY_ID, plugin ) )
        {
            daoUtil.setInt( 1, nIdEntry );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                EntrySelectOption entrySelectOption = new EntrySelectOption( );
                entrySelectOption.setId( daoUtil.getInt( 1 ) );
                entrySelectOption.setIdEntry( nIdEntry );
                entrySelectOption.setTitle( daoUtil.getString( 2 ) );
                entrySelectOption.setUrl( daoUtil.getString( 3 ) );
                entrySelectOption.setIdOrder( daoUtil.getInt( 4 ) );
                listEntrySelectOption.add( entrySelectOption );
            }
        }
        return listEntrySelectOption;
    }
}

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

/**
 *
 * Class EntryListboxDAO
 *
 */
public class EntrySelectDAO implements IEntrySpecificDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT title,target FROM quicklinks_entry_select WHERE id_entry = ? ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO quicklinks_entry_select ( id_entry,title,target ) VALUES ( ?, ?, ? )";
    private static final String SQL_QUERY_DELETE = " DELETE FROM quicklinks_entry_select WHERE id_entry = ?";
    private static final String SQL_QUERY_UPDATE = " UPDATE quicklinks_entry_select SET title = ?, target = ? WHERE id_entry = ?";

    /**
     * Load the data of the entry type from the table
     *
     * @param entry  The empty entry object
     * @param plugin the plugin
     * @return the instance of the EntryType
     */
    public IEntry load( IEntry entry, Plugin plugin )
    {
        EntrySelect entrySelect = null;

        if ( entry == null )
        {
            return null;
        }

        if ( entry instanceof EntrySelect )
        {
            entrySelect = (EntrySelect) entry;
        }
        else
        {
            return null;
        }

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin ) )
        {
            daoUtil.setInt( 1, entrySelect.getId( ) );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                entrySelect.setTitle( daoUtil.getString( 1 ) );
                entrySelect.setTarget( daoUtil.getString( 2 ) );
            }

        }

        return entrySelect;
    }

    /**
     * Deletes the {@link Entry} whose identifier is specified in parameter
     *
     * @param nEntryId The identifier of the {@link Entry}
     * @param plugin   The {@link Plugin}
     */
    public void delete( int nEntryId, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nEntryId );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Insert the Entry
     *
     * @param entry  The {@link Entry} object
     * @param plugin The {@link Plugin}
     * @return The {@link Entry}
     */
    public IEntry insert( IEntry entry, Plugin plugin )
    {
        EntrySelect entrySelect = null;

        if ( entry == null )
        {
            return null;
        }

        if ( entry instanceof EntrySelect )
        {
            entrySelect = (EntrySelect) entry;
        }
        else
        {
            return null;
        }

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            int nParam = 1;
            daoUtil.setInt( nParam++, entrySelect.getId( ) );
            daoUtil.setString( nParam++, entrySelect.getTitle( ) );
            daoUtil.setString( nParam++, entrySelect.getTarget( ) );

            daoUtil.executeUpdate( );
        }

        return entry;
    }

    /**
     * Update the {@link Entry}
     *
     * @param entry  The {@link Entry} object
     * @param plugin The {@link Plugin}
     */
    public void store( IEntry entry, Plugin plugin )
    {
        EntrySelect entrySelect = null;

        if ( entry == null )
        {
            return;
        }

        if ( entry instanceof EntrySelect )
        {
            entrySelect = (EntrySelect) entry;
        }
        else
        {
            return;
        }

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {

            int nParam = 1;
            daoUtil.setString( nParam++, entrySelect.getTitle( ) );
            daoUtil.setString( nParam++, entrySelect.getTarget( ) );
            daoUtil.setInt( nParam++, entrySelect.getId( ) );
            daoUtil.executeUpdate( );
        }
    }
}

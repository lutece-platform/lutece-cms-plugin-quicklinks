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
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * Class EntryDAO
 *
 */
public class EntryDAO implements IEntryDAO
{
    private static final String SQL_QUERY_NEW_PK = " SELECT max( id_entry ) FROM quicklinks_entry";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_entry,id_quicklinks,id_type,id_order,id_parent "
            + "FROM quicklinks_entry WHERE id_entry = ? ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO quicklinks_entry ( id_entry, id_quicklinks, id_type, "
            + "id_order,id_parent ) VALUES ( ?, ?, ?, ?, ? )";
    private static final String SQL_QUERY_DELETE = " DELETE FROM quicklinks_entry WHERE id_entry = ?";
    private static final String SQL_QUERY_UPDATE = " UPDATE quicklinks_entry SET id_quicklinks = ?, id_type = ?, "
            + "id_order = ?, id_parent = ? WHERE id_entry = ?";
    private static final String SQL_QUERY_SELECT_BY_FILTER_SELECT = " SELECT id_entry, id_quicklinks, id_type, "
            + "id_order, id_parent FROM quicklinks_entry ";
    private static final String SQL_QUERY_SELECT_BY_FILTER_WHERE = " WHERE ";
    private static final String SQL_QUERY_SELECT_BY_FILTER_AND = " AND ";
    private static final String SQL_QUERY_SELECT_BY_FILTER_ID = " id_entry = ? ";
    private static final String SQL_QUERY_SELECT_BY_FILTER_ID_QUICKLINKS = " id_quicklinks = ? ";
    private static final String SQL_QUERY_SELECT_BY_FILTER_TYPE = " id_type = ? ";
    private static final String SQL_QUERY_SELECT_BY_FILTER_ORDER = " id_order = ? ";
    private static final String SQL_QUERY_SELECT_BY_FILTER_PARENT = " id_parent = ? ";
    private static final String SQL_QUERY_SELECT_BY_FILTER_ORDER_BY = " ORDER BY id_order ";
    private static final String EMPTY_STRING = "";
    private static final String PARAM = "Param";

    /**
     * Calculate a new primary key to add a new {@link Entry}
     * 
     * @param plugin The {@link Plugin} using this data access service
     * @return The new key.
     */
    public int newPrimaryKey( Plugin plugin )
    {
        int nKey = 1;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin ) )
        {
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
     * Load the data of the entry type from the table
     *
     * @param entry  The empty entry object
     * @param plugin the plugin
     * @return the instance of the EntryType
     */
    public IEntry load( int nId, Plugin plugin )
    {
        IEntry entry = new Entry( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin ) )
        {
            daoUtil.setInt( 1, nId );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                entry.setId( daoUtil.getInt( 1 ) );
                entry.setIdQuicklinks( daoUtil.getInt( 2 ) );

                EntryType entryType = new EntryType( );
                entryType.setId( daoUtil.getInt( 3 ) );
                entry.setEntryType( entryType );
                entry.setIdOrder( daoUtil.getInt( 4 ) );
                entry.setIdParent( daoUtil.getInt( 5 ) );
            }
        }
        return entry;
    }

    /**
     * Deletes the {@link Entry} whose identifier is specified in parameter
     *
     * @param nId    The identifier of the {@link Entry}
     * @param plugin The {@link Plugin}
     */
    public void delete( int nId, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nId );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Insert the Entry
     *
     * @param entry  The {@link Entry} object
     * @param plugin The {@link Plugin}
     */
    public IEntry insert( IEntry entry, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            entry.setId( newPrimaryKey( plugin ) );

            daoUtil.setInt( 1, entry.getId( ) );
            daoUtil.setInt( 2, entry.getIdQuicklinks( ) );
            daoUtil.setInt( 3, entry.getEntryType( ).getId( ) );
            daoUtil.setInt( 4, entry.getIdOrder( ) );
            daoUtil.setInt( 5, entry.getIdParent( ) );

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
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nParam = 0;
            daoUtil.setInt( ++nParam, entry.getIdQuicklinks( ) );
            daoUtil.setInt( ++nParam, entry.getEntryType( ).getId( ) );
            daoUtil.setInt( ++nParam, entry.getIdOrder( ) );
            daoUtil.setInt( ++nParam, entry.getIdParent( ) );

            daoUtil.setInt( ++nParam, entry.getId( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Find all {@link Entry} specified by filter
     *
     * @param entryFilter The {@link EntryFilter} object
     * @param plugin      the {@link Plugin}
     * @return the instance of the EntryType
     */
    public Collection<IEntry> findByFilter( EntryFilter entryFilter, Plugin plugin )
    {
        Collection<IEntry> listQuicklinks = new ArrayList<>( );
        try ( DAOUtil daoUtil = getDaoFromFilter( SQL_QUERY_SELECT_BY_FILTER_SELECT, entryFilter, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                Entry entry = null;
                entry = new Entry( );
                entry.setId( daoUtil.getInt( 1 ) );
                entry.setIdQuicklinks( daoUtil.getInt( 2 ) );

                EntryType entryType = new EntryType( );
                entryType.setId( daoUtil.getInt( 3 ) );
                entry.setEntryType( entryType );
                entry.setIdOrder( daoUtil.getInt( 4 ) );
                entry.setIdParent( daoUtil.getInt( 5 ) );
                listQuicklinks.add( entry );
            }

        }

        return listQuicklinks;
    }

    /**
     * Return a DAO initialized with the specified filter
     * 
     * @param strQuerySelect the query
     * @param filter         the {@link EntryFilter} object
     * @return the DaoUtil
     */
    private DAOUtil getDaoFromFilter( String strQuerySelect, EntryFilter filter, Plugin plugin )
    {
        String strSQL = strQuerySelect;
        String strWhere = EMPTY_STRING;

        if ( filter.getId( ) != EntryFilter.UNUSED_ATTRIBUTE_VALUE )
        {
            strWhere += ( ( ( !strWhere.equals( EMPTY_STRING ) ) ? SQL_QUERY_SELECT_BY_FILTER_AND : EMPTY_STRING )
                    + SQL_QUERY_SELECT_BY_FILTER_ID );
        }

        if ( filter.getIdQuicklinks( ) != EntryFilter.UNUSED_ATTRIBUTE_VALUE )
        {
            strWhere += ( ( ( !strWhere.equals( EMPTY_STRING ) ) ? SQL_QUERY_SELECT_BY_FILTER_AND : EMPTY_STRING )
                    + SQL_QUERY_SELECT_BY_FILTER_ID_QUICKLINKS );
        }

        if ( filter.getIdType( ) != EntryFilter.UNUSED_ATTRIBUTE_VALUE )
        {
            strWhere += ( ( ( !strWhere.equals( EMPTY_STRING ) ) ? SQL_QUERY_SELECT_BY_FILTER_AND : EMPTY_STRING )
                    + SQL_QUERY_SELECT_BY_FILTER_TYPE );
        }

        if ( filter.getIdOrder( ) != EntryFilter.UNUSED_ATTRIBUTE_VALUE )
        {
            strWhere += ( ( ( !strWhere.equals( EMPTY_STRING ) ) ? SQL_QUERY_SELECT_BY_FILTER_AND : EMPTY_STRING )
                    + SQL_QUERY_SELECT_BY_FILTER_ORDER );
        }

        if ( filter.getIdParent( ) != EntryFilter.UNUSED_ATTRIBUTE_VALUE )
        {
            strWhere += ( ( ( !strWhere.equals( EMPTY_STRING ) ) ? SQL_QUERY_SELECT_BY_FILTER_AND : EMPTY_STRING )
                    + SQL_QUERY_SELECT_BY_FILTER_PARENT );
        }

        if ( !strWhere.equals( EMPTY_STRING ) )
        {
            strSQL += ( SQL_QUERY_SELECT_BY_FILTER_WHERE + strWhere );
        }

        strSQL += SQL_QUERY_SELECT_BY_FILTER_ORDER_BY;

        AppLogService.debug( "Sql query Entry filter : " + strSQL );

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.getId( ) != EntryFilter.UNUSED_ATTRIBUTE_VALUE )
        {
            daoUtil.setInt( nIndex, filter.getId( ) );
            AppLogService.debug( PARAM + nIndex + " (getId) = " + filter.getId( ) );
            nIndex++;
        }

        if ( filter.getIdQuicklinks( ) != EntryFilter.UNUSED_ATTRIBUTE_VALUE )
        {
            daoUtil.setInt( nIndex, filter.getIdQuicklinks( ) );
            AppLogService.debug( PARAM + nIndex + " (getIdQuicklinks) = " + filter.getIdQuicklinks( ) );
            nIndex++;
        }

        if ( filter.getIdType( ) != EntryFilter.UNUSED_ATTRIBUTE_VALUE )
        {
            daoUtil.setInt( nIndex, filter.getIdType( ) );
            AppLogService.debug( PARAM + nIndex + " (getIdType) = " + filter.getIdType( ) );
            nIndex++;
        }

        if ( filter.getIdOrder( ) != EntryFilter.UNUSED_ATTRIBUTE_VALUE )
        {
            daoUtil.setInt( nIndex, filter.getIdOrder( ) );
            AppLogService.debug( PARAM + nIndex + " (getIdOrder) = " + filter.getIdOrder( ) );
            nIndex++;
        }

        if ( filter.getIdParent( ) != EntryFilter.UNUSED_ATTRIBUTE_VALUE )
        {
            daoUtil.setInt( nIndex, filter.getIdParent( ) );
            AppLogService.debug( PARAM + nIndex + " (getIdParent) = " + filter.getIdParent( ) );
        }
        return daoUtil;
    }
}

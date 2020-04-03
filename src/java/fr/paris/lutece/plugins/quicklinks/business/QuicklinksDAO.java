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
 * This class provides Data Access methods for Faq objects
 *
 */
public final class QuicklinksDAO implements IQuicklinksDAO
{
    private static final String SQL_QUERY_NEW_PK = " SELECT max( id_quicklinks ) FROM quicklinks_quicklinks";
    private static final String SQL_QUERY_SELECT = " SELECT id_quicklinks, title_quicklinks, type_quicklinks, "
            + "role_key, workgroup_key, is_enabled, css_style FROM quicklinks_quicklinks WHERE id_quicklinks = ?";
    private static final String SQL_QUERY_INSERT = " INSERT INTO quicklinks_quicklinks ( id_quicklinks, title_quicklinks, "
            + "type_quicklinks, role_key, workgroup_key, is_enabled, css_style ) VALUES ( ?, ?, ?, ?, ?, ?, ? )";
    private static final String SQL_QUERY_DELETE = " DELETE FROM quicklinks_quicklinks WHERE id_quicklinks = ?";
    private static final String SQL_QUERY_UPDATE = " UPDATE quicklinks_quicklinks SET title_quicklinks = ?, type_quicklinks = ?, "
            + "role_key = ?, workgroup_key = ?, is_enabled = ?, css_style = ? WHERE id_quicklinks = ?";
    private static final String SQL_QUERY_SELECTALL = " SELECT id_quicklinks, title_quicklinks, type_quicklinks, role_key, "
            + "workgroup_key, is_enabled, css_style FROM quicklinks_quicklinks ORDER BY title_quicklinks ";
    private static final String SQL_QUERY_SELECT_BY_FILTER_SELECT = " SELECT id_quicklinks, title_quicklinks, type_quicklinks, "
            + "role_key, workgroup_key, is_enabled, css_style FROM quicklinks_quicklinks ";
    private static final String SQL_QUERY_SELECT_BY_FILTER_WHERE = " WHERE ";
    private static final String SQL_QUERY_SELECT_BY_FILTER_AND = " AND ";
    private static final String SQL_QUERY_SELECT_BY_FILTER_OR = " OR ";
    private static final String SQL_QUERY_SELECT_BY_FILTER_OPEN_BRACKET = " ( ";
    private static final String SQL_QUERY_SELECT_BY_FILTER_CLOSE_BRACKET = " ) ";
    private static final String SQL_QUERY_SELECT_BY_FILTER_ROLE_KEY = " role_key = ? ";
    private static final String SQL_QUERY_SELECT_BY_FILTER_WORKGROUP_KEY = " workgroup_key = ? ";
    private static final String SQL_QUERY_SELECT_BY_FILTER_IS_ENABLED = " is_enabled = ? ";
    private static final String SQL_QUERY_SELECT_BY_FILTER_TYPE = " type_quicklinks = ? ";
    private static final String PARAM = "Param";
    
    /**
     * Calculate a new primary key to add a new {@link Quicklinks}
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
     * Insert a new record in the table.
     * 
     * @param quicklinks The Instance of the object {@link Quicklinks}
     * @param plugin     The {@link Plugin} using this data access service
     * @return The new {@link Quicklinks}
     */
    public synchronized Quicklinks insert( Quicklinks quicklinks, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            quicklinks.setId( newPrimaryKey( plugin ) );

            daoUtil.setInt( 1, quicklinks.getId( ) );
            daoUtil.setString( 2, quicklinks.getTitle( ) );
            daoUtil.setInt( 3, quicklinks.getType( ).getValue( ) );
            daoUtil.setString( 4, quicklinks.getRoleKey( ) );
            daoUtil.setString( 5, quicklinks.getWorkgroup( ) );
            daoUtil.setBoolean( 6, quicklinks.isEnabled( ) );
            daoUtil.setString( 7, quicklinks.getCssStyle( ) );

            daoUtil.executeUpdate( );
        }

        return quicklinks;
    }

    /**
     * Delete the {@link Quicklinks} specified by identifier
     * 
     * @param nIdQuicklinks The identifier
     * @param plugin        The {@link Plugin} using this data access service
     */
    public void delete( int nIdQuicklinks, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdQuicklinks );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Update The {@link Quicklinks}
     * 
     * @param quicklinks The {@link Quicklinks} to update
     * @param plugin     The {@link Plugin} using this data access service
     */
    public void store( Quicklinks quicklinks, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nParam = 1;
            daoUtil.setString( nParam++, quicklinks.getTitle( ) );
            daoUtil.setInt( nParam++, quicklinks.getType( ).getValue( ) );
            daoUtil.setString( nParam++, quicklinks.getRoleKey( ) );
            daoUtil.setString( nParam++, quicklinks.getWorkgroup( ) );
            daoUtil.setBoolean( nParam++, quicklinks.isEnabled( ) );
            daoUtil.setString( nParam++, quicklinks.getCssStyle( ) );

            daoUtil.setInt( nParam++, quicklinks.getId( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Load the Quicklinks specified by Identifier
     * 
     * @param nIdQuicklinks The identifier
     * @param plugin        The {@link Plugin} using this data access service
     * @return The {@link Quicklinks}
     */
    public Quicklinks load( int nIdQuicklinks, Plugin plugin )
    {
        Quicklinks quicklinks = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nIdQuicklinks );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                quicklinks = new Quicklinks( );
                quicklinks.setId( daoUtil.getInt( 1 ) );
                quicklinks.setTitle( daoUtil.getString( 2 ) );
                quicklinks.setType( QuicklinksType.getByValue( daoUtil.getInt( 3 ) ) );
                quicklinks.setRoleKey( daoUtil.getString( 4 ) );
                quicklinks.setWorkgroup( daoUtil.getString( 5 ) );
                quicklinks.setEnabled( daoUtil.getBoolean( 6 ) );
                quicklinks.setCssStyle( daoUtil.getString( 7 ) );
            }

        }

        return quicklinks;
    }

    /**
     * Find All {@link Quicklinks}
     * 
     * @param plugin The {@link Plugin} using this data access service
     * @return A {@link Collection} of {@link Quicklinks}
     */
    public Collection<Quicklinks> findAll( Plugin plugin )
    {
        Collection<Quicklinks> list = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                Quicklinks quicklinks = null;
                quicklinks = new Quicklinks( );
                quicklinks.setId( daoUtil.getInt( 1 ) );
                quicklinks.setTitle( daoUtil.getString( 2 ) );
                quicklinks.setType( QuicklinksType.getByValue( daoUtil.getInt( 3 ) ) );
                quicklinks.setRoleKey( daoUtil.getString( 4 ) );
                quicklinks.setWorkgroup( daoUtil.getString( 5 ) );
                quicklinks.setEnabled( daoUtil.getBoolean( 6 ) );
                quicklinks.setCssStyle( daoUtil.getString( 7 ) );
                list.add( quicklinks );
            }

        }

        return list;
    }

    /**
     * Find all {@link Quicklinks} corresponding to {@link QuicklinksFilter}
     * 
     * @param quickLinksFilter The {@link QuicklinksFilter}
     * @param plugin           The {@link Plugin} using this data access service
     * @return A {@link Collection} of {@link Quicklinks}
     */
    public Collection<Quicklinks> findbyFilter( QuicklinksFilter quickLinksFilter, Plugin plugin )
    {
        Collection<Quicklinks> listQuicklinks = new ArrayList<>( );

        try ( DAOUtil daoUtil = getDaoFromFilter( SQL_QUERY_SELECT_BY_FILTER_SELECT, quickLinksFilter, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                Quicklinks quicklinks = null;
                quicklinks = new Quicklinks( );
                quicklinks.setId( daoUtil.getInt( 1 ) );
                quicklinks.setTitle( daoUtil.getString( 2 ) );
                quicklinks.setType( QuicklinksType.getByValue( daoUtil.getInt( 3 ) ) );
                quicklinks.setRoleKey( daoUtil.getString( 4 ) );
                quicklinks.setWorkgroup( daoUtil.getString( 5 ) );
                quicklinks.setEnabled( daoUtil.getBoolean( 6 ) );
                quicklinks.setCssStyle( daoUtil.getString( 7 ) );
                listQuicklinks.add( quicklinks );
            }

        }

        return listQuicklinks;
    }

    /**
     * Return a DAO initialized with the specified filter
     * 
     * @param strQuerySelect the query
     * @param filter         the {@link QuicklinksFilter} object
     * @return the DaoUtil
     */
    private DAOUtil getDaoFromFilter( String strQuerySelect, QuicklinksFilter filter, Plugin plugin )
    {
        String strSQL = strQuerySelect;

        StringBuilder sbWhere = new StringBuilder( );

        if ( ( filter.getRoleKeys( ) != null ) && ( filter.getRoleKeys( ).length > 0 ) )
        {
            StringBuilder sbRoles = new StringBuilder( );
            sbWhere.append( SQL_QUERY_SELECT_BY_FILTER_OPEN_BRACKET );

            for ( int nIndex = 0; nIndex < filter.getRoleKeys( ).length; nIndex++ )
            {
                appendFilter( sbRoles, SQL_QUERY_SELECT_BY_FILTER_ROLE_KEY, SQL_QUERY_SELECT_BY_FILTER_OR );
            }
            sbWhere.append( sbRoles );
            sbWhere.append( SQL_QUERY_SELECT_BY_FILTER_CLOSE_BRACKET );
        }

        if ( filter.getType( ) != null )
        {
            appendFilter( sbWhere, SQL_QUERY_SELECT_BY_FILTER_TYPE, SQL_QUERY_SELECT_BY_FILTER_AND );
        }

        if ( filter.isEnabled( ) != null )
        {
            appendFilter( sbWhere, SQL_QUERY_SELECT_BY_FILTER_IS_ENABLED, SQL_QUERY_SELECT_BY_FILTER_AND );
        }

        if ( filter.getWorkgroup( ) != null )
        {
            appendFilter( sbWhere, SQL_QUERY_SELECT_BY_FILTER_WORKGROUP_KEY, SQL_QUERY_SELECT_BY_FILTER_AND );
        }

        if ( sbWhere.length( ) > 0 )
        {
            strSQL += SQL_QUERY_SELECT_BY_FILTER_WHERE + sbWhere.toString( );
        }

        AppLogService.debug( "Sql query Quicklinks filter : " + strSQL );

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( ( filter.getRoleKeys( ) != null ) && ( filter.getRoleKeys( ).length > 0 ) )
        {
            for ( String strRoleKey : filter.getRoleKeys( ) )
            {
                daoUtil.setString( nIndex, strRoleKey );
                AppLogService.debug( PARAM + nIndex + " (getRoleKey) = " + filter.getRoleKeys( ) );
                nIndex++;
            }
        }

        if ( filter.getType( ) != null )
        {
            daoUtil.setInt( nIndex, filter.getType( ).getValue( ) );
            AppLogService.debug( PARAM + nIndex + " (getType) = " + filter.getType( ) + "(value int : "
                    + filter.getType( ).getValue( ) + ")" );
            nIndex++;
        }

        if ( filter.isEnabled( ) != null )
        {
            daoUtil.setBoolean( nIndex, filter.isEnabled( ) );
            AppLogService.debug( PARAM + nIndex + " (isEnabled) = " + filter.isEnabled( ) );
            nIndex++;
        }

        if ( filter.getWorkgroup( ) != null )
        {
            daoUtil.setString( nIndex, filter.getWorkgroup( ) );
            AppLogService.debug( PARAM + nIndex + " (getWorkgroup) = " + filter.getWorkgroup( ) );
        }

        return daoUtil;
    }

    /**
     * Appends the filter to the buffer
     * 
     * @param buffer        the buffer
     * @param strFilterName the filter
     * @param strCondition  {@link #SQL_QUERY_SELECT_BY_FILTER_AND} or
     *                      {@link #SQL_QUERY_SELECT_BY_FILTER_OR}
     */
    private void appendFilter( StringBuilder buffer, String strFilterName, String strCondition )
    {
        if ( buffer.length( ) != 0 )
        {
            buffer.append( strCondition );
        }
        buffer.append( strFilterName );
    }
}

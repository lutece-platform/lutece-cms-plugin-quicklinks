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
package fr.paris.lutece.plugins.quicklinks.business.portlet;

import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for QuicklinksPortlet objects
 */
public final class QuicklinksPortletDAO implements IQuicklinksPortletDAO
{
    private static final String SQL_QUERY_INSERT = "INSERT INTO quicklinks_portlet ( id_portlet , id_quicklinks ) VALUES ( ? , ? )";
    private static final String SQL_QUERY_SELECT = "SELECT id_portlet , id_quicklinks FROM quicklinks_portlet WHERE id_portlet = ? ";
    private static final String SQL_QUERY_SELECT_COUNT_PORTLET_BY_ID_QUICKLINKS = "SELECT COUNT(id_portlet) "
            + "FROM quicklinks_portlet WHERE id_quicklinks = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE quicklinks_portlet SET id_portlet = ?, id_quicklinks = ? WHERE id_portlet = ? ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM quicklinks_portlet WHERE id_portlet= ? ";

    ///////////////////////////////////////////////////////////////////////////////////////
    // Access methods to data

    /**
     * Insert a new record in the table quicklinks_portlet
     *
     *
     * @param portlet the instance of the Portlet object to insert
     */
    public void insert( Portlet portlet )
    {
        QuicklinksPortlet p = (QuicklinksPortlet) portlet;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT ) )
        {
            daoUtil.setInt( 1, p.getId( ) );
            daoUtil.setInt( 2, p.getQuicklinksId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Deletes records for a portlet identifier in the table quicklinks_portlet
     *
     *
     * @param nPortletId the portlet identifier
     */
    public void delete( int nPortletId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE ) )
        {
            daoUtil.setInt( 1, nPortletId );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Loads the data of Quicklinks Portlet whose identifier is specified in
     * parameter
     *
     *
     * @param nPortletId The Portlet identifier
     * @return theDocumentListPortlet object
     */
    public Portlet load( int nPortletId )
    {
        QuicklinksPortlet portlet = new QuicklinksPortlet( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT ) )
        {
            daoUtil.setInt( 1, nPortletId );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                portlet.setId( daoUtil.getInt( 1 ) );
                portlet.setQuicklinksId( daoUtil.getInt( 2 ) );
            }
        }
        return portlet;
    }

    /**
     * return number of quicklinks portlet who are associate to the id quicklinks
     * 
     * @param nIdQuicklinks the id of the quicklinks
     * @return number of quicklinks portlet who are associate to the id quicklinks
     */
    public int selectCountPortletByIdQuicklinks( int nIdQuicklinks )
    {
        int nCountPortlet = 0;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_COUNT_PORTLET_BY_ID_QUICKLINKS ) )
        {
            daoUtil.setInt( 1, nIdQuicklinks );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                nCountPortlet = daoUtil.getInt( 1 );
            }

        }

        return nCountPortlet;
    }

    /**
     * Update the record in the table
     *
     *
     * @param portlet A portlet
     */
    public void store( Portlet portlet )
    {
        QuicklinksPortlet p = (QuicklinksPortlet) portlet;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE ) )
        {
            daoUtil.setInt( 1, p.getId( ) );
            daoUtil.setInt( 2, p.getQuicklinksId( ) );
            daoUtil.setInt( 3, p.getId( ) );
            daoUtil.executeUpdate( );
        }
    }
}

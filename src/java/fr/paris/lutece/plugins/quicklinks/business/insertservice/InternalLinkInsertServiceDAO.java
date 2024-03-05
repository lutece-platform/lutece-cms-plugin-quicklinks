/*
 * Copyright (c) 2002-2024, City of Paris
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

package fr.paris.lutece.plugins.quicklinks.business.insertservice;

import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class provides Data Access methods for InternalLinkInsertService objects
 */
public class InternalLinkInsertServiceDAO
{
    /**
     * This class implements the Singleton design pattern.
     */
    private static final InternalLinkInsertServiceDAO _dao = new InternalLinkInsertServiceDAO( );

    /**
     * Creates a new InternalLinkInsertServiceDAO object.
     */
    private InternalLinkInsertServiceDAO( )
    {
    }

    /**
     * Returns the unique instance of the singleton.
     *
     * @return the instance
     */
    static InternalLinkInsertServiceDAO getInstance( )
    {
        return _dao;
    }

    /**
     * The collection of page
     *
     * @param strPageName
     *         the name of the page
     * @return The collection of field
     */
    Collection selectPageListbyName( String strPageName )
    {
        Collection<InternalLinkInsertService> list = new ArrayList<>( );
        String strSQL;

        if ( "".equals( strPageName ) )
        {
            strSQL = " SELECT id_page , name ,description FROM core_page";
        }
        else
        {
            strSQL = " SELECT id_page , name ,description FROM core_page WHERE name LIKE'%" + strPageName + "%'";
        }

        try ( DAOUtil daoUtil = new DAOUtil( strSQL ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                InternalLinkInsertService page = new InternalLinkInsertService( );
                page.setIdPage( daoUtil.getInt( 1 ) );
                page.setLabelPage( daoUtil.getString( 2 ) );
                page.setDescriptionPage( daoUtil.getString( 3 ) );
                list.add( page );
            }

            daoUtil.free( );
        }
        return list;
    }
}

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
import jakarta.enterprise.context.ApplicationScoped;

/**
 * This class provides Data Access methods for InternalLinkInsertService objects
 */
@ApplicationScoped
public class InternalLinkInsertServiceDAO
{
    private static final String SQL_QUERY_SELECT_BY_NAME = " SELECT id_page , name ,description FROM core_page WHERE name LIKE ?";
    private static final String SQL_LIKE_WILDCARD = "%";

    /**
     * The collection of page
     *
     * @param strPageName
     *         the name of the page
     * @return The collection of field
     */
    Collection<InternalLinkInsertService> selectPageListbyName( String strPageName )
    {
        Collection<InternalLinkInsertService> list = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_NAME ) )
        {
            daoUtil.setString( 1, SQL_LIKE_WILDCARD + strPageName + SQL_LIKE_WILDCARD );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                InternalLinkInsertService page = new InternalLinkInsertService( );
                page.setIdPage( daoUtil.getInt( 1 ) );
                page.setLabelPage( daoUtil.getString( 2 ) );
                page.setDescriptionPage( daoUtil.getString( 3 ) );
                list.add( page );
            }
        }
        return list;
    }
}

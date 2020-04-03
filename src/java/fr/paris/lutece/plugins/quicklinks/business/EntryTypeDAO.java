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
import fr.paris.lutece.util.sql.DAOUtil;

/**
 *
 * Class EntryTypeDAO
 *
 */
public class EntryTypeDAO implements IEntryTypeDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_entry_type, title_key, class_name, "
            + "template_create, template_modify FROM quicklinks_entry_type WHERE id_entry_type = ? ";
    private static final String SQL_QUERY_SELECT = "SELECT id_entry_type, title_key, class_name, template_create, template_modify"
            + " FROM quicklinks_entry_type ";

    /**
     * Load the data of the entry type from the table
     *
     * @param nIdKey The identifier of the entry type
     * @param plugin the plugin
     * @return the instance of the EntryType
     */
    public EntryType load( int nIdKey, Plugin plugin )
    {
        EntryType entryType = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin ) )
        {
            daoUtil.setInt( 1, nIdKey );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                entryType = new EntryType( );
                entryType.setId( daoUtil.getInt( 1 ) );
                entryType.setTitleI18nKey( daoUtil.getString( 2 ) );
                entryType.setClassName( daoUtil.getString( 3 ) );
                entryType.setTemplateCreate( daoUtil.getString( 4 ) );
                entryType.setTemplateModify( daoUtil.getString( 5 ) );
            }
        }
        return entryType;
    }

    /**
     * Load the data of all entry type returns them in a list
     * 
     * @param plugin the plugin
     * @return the {@link Collection} of entry type
     */
    public Collection<EntryType> select( Plugin plugin )
    {
        Collection<EntryType> listEntryType = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                EntryType entryType = new EntryType( );
                entryType.setId( daoUtil.getInt( 1 ) );
                entryType.setTitleI18nKey( daoUtil.getString( 2 ) );
                entryType.setClassName( daoUtil.getString( 3 ) );
                entryType.setTemplateCreate( daoUtil.getString( 4 ) );
                entryType.setTemplateModify( daoUtil.getString( 5 ) );
                listEntryType.add( entryType );
            }

        }

        return listEntryType;
    }
}

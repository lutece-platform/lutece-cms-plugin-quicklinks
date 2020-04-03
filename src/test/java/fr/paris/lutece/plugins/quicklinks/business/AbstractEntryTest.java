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

import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.sql.DAOUtil;

public abstract class AbstractEntryTest extends LuteceTestCase
{

    private static final String SQL_INSERT = "INSERT INTO quicklinks_entry_type ( id_entry_type, title_key, class_name, template_create, template_modify ) VALUES (?,?,?,?,?)";
    private static final String SQL_DELETE = "DELETE FROM quicklinks_entry_type WHERE id_entry_type = ?";
    
    protected void createEntryType( int id, String titleKey, String className, String templateCreate, String templateModify )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_INSERT ) )
        {
            int nIndex = 0;
            daoUtil.setInt( ++nIndex, id );
            daoUtil.setString( ++nIndex, titleKey );
            daoUtil.setString( ++nIndex, className );
            daoUtil.setString( ++nIndex, templateCreate );
            daoUtil.setString( ++nIndex, templateModify );
            daoUtil.executeUpdate( );
        }
    }
    
    protected void deleteEntryType( int id )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_DELETE ) )
        {
            int nIndex = 0;
            daoUtil.setInt( ++nIndex, id );
            daoUtil.executeUpdate( );
        }
    }
    
    
}

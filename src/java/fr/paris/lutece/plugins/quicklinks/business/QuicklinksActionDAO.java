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
 * QuicklinksActionDAO
 */
public class QuicklinksActionDAO implements IQuicklinksActionDAO
{
    private static final String SQL_QUERY_SELECT_ACTIONS = "SELECT a.name_key, a.description_key, a.action_url, a.icon_url, a.action_permission ,a.quicklinks_state"
            + " FROM quicklinks_action a WHERE a.quicklinks_state = ? ";

    /**
     * Load the list of actions for a all {@link Quicklinks} by {@link Quicklinks}
     * state
     * 
     * @param bIsEnabled true if {@link Quicklinks} is enabled
     * @param plugin     the plugin
     * @return The Collection of actions
     */
    public Collection<QuicklinksAction> selectActionsByQuicklinksState( boolean bIsEnabled, Plugin plugin )
    {
        Collection<QuicklinksAction> listActions = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ACTIONS, plugin ) )
        {
            daoUtil.setBoolean( 1, bIsEnabled );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                QuicklinksAction action = new QuicklinksAction( );
                action.setNameKey( daoUtil.getString( 1 ) );
                action.setDescriptionKey( daoUtil.getString( 2 ) );
                action.setUrl( daoUtil.getString( 3 ) );
                action.setIconUrl( daoUtil.getString( 4 ) );
                action.setPermission( daoUtil.getString( 5 ) );
                action.setQuicklinksState( daoUtil.getInt( 6 ) );
                listActions.add( action );
            }

        }

        return listActions;
    }
}

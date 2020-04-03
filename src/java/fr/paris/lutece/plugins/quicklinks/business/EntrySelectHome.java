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
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 *
 * class EntrySelectHome
 *
 */
public final class EntrySelectHome
{
    // Static variable pointed at the DAO instance
    private static IEntrySpecificDAO _dao = SpringContextService.getBean( "quicklinks.entrySelectDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private EntrySelectHome( )
    {
    }

    /**
     * Returns an instance of a EntrySelect whose identifier is specified in parameter
     *
     * @param entry
     *            The Entry to load
     * @param plugin
     *            the Plugin
     * @return an instance of EntrySelect
     */
    public static IEntry load( IEntry entry, Plugin plugin )
    {
        return _dao.load( entry, plugin );
    }

    /**
     * Insert a new record in the table.
     *
     * @param entry
     *            The Instance of the object {@link EntrySelect}
     * @param plugin
     *            The {@link Plugin} using this data access service
     * @return The {@link EntrySelect}
     */
    public static IEntry create( EntrySelect entry, Plugin plugin )
    {
        return _dao.insert( entry, plugin );
    }

    /**
     * Delete the {@link EntrySelect} specified by identifier
     *
     * @param nId
     *            The identifier
     * @param plugin
     *            The {@link Plugin} using this data access service
     */
    public static void remove( int nId, Plugin plugin )
    {
        EntrySelectOptionHome.removeByEntryId( nId, plugin );
        _dao.delete( nId, plugin );
    }

    /**
     * Update The {@link EntrySelect}
     *
     * @param entry
     *            The {@link EntrySelect} to update
     * @param plugin
     *            The {@link Plugin} using this data access service
     */
    public static void update( EntrySelect entry, Plugin plugin )
    {
        _dao.store( entry, plugin );
    }
}

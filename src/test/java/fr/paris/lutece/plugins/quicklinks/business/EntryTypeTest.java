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

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.portal.service.plugin.Plugin;
import org.junit.jupiter.api.Test;

public class EntryTypeTest extends AbstractEntryTest
{
    private final int _id = 1111111111;
    /**
     * The unit-test container never runs the portal startup, so PluginService has no cache and resolving a
     * plugin there fails. DAOUtil falls back on the portal pool when the plugin is null, which is what every
     * query of this plugin uses anyway.
     */
    private final Plugin _plugin = null;
    
    @Test
    public void testFind( )
    {
        createEntryType( _id, "title", "className", "templateCreate", "templateModify" );
        
        EntryType entryType = EntryTypeHome.findByPrimaryKey( _id, _plugin);
        
        assertEquals( "title", entryType.getTitleI18nKey( ) );
        assertEquals( "className", entryType.getClassName( ) );
        assertEquals( "templateCreate", entryType.getTemplateCreate( ) );
        assertEquals( "templateModify", entryType.getTemplateModify( ) );
        
        deleteEntryType( _id );
    }
    
    @Test
    public void testFindAll( )
    {
        createEntryType( _id, "title", "className", "templateCreate", "templateModify" );
        
        Collection<EntryType> list = EntryTypeHome.findAll( _plugin );
        
        assertTrue( CollectionUtils.isNotEmpty( list ) );
        
        deleteEntryType( _id );
    }
}

/*
 * Copyright (c) 2002-2026, City of Paris
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.SecureRandom;
import java.util.List;

import jakarta.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.quicklinks.business.EntryHome;
import fr.paris.lutece.plugins.quicklinks.business.EntryInternalLink;
import fr.paris.lutece.plugins.quicklinks.business.EntryTypeHome;
import fr.paris.lutece.plugins.quicklinks.business.Quicklinks;
import fr.paris.lutece.plugins.quicklinks.business.QuicklinksHome;
import fr.paris.lutece.plugins.quicklinks.business.QuicklinksType;
import fr.paris.lutece.plugins.quicklinks.service.QuicklinksPlugin;
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.portlet.PortletTemplate;
import fr.paris.lutece.portal.business.portlet.PortletTemplateHome;
import fr.paris.lutece.portal.service.page.IPageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;

/**
 * Renders a quicklinks portlet with every shipped FreeMarker template
 */
public class QuicklinksPortletRenderingTest extends LuteceTestCase
{
    private static final String PORTLET_NAME = "QuicklinksPortletRenderingTest";
    private static final String MARKER_PORTLET = "portlet-quicklinks";
    private static final String CSS_STYLE = "quicklinks-rendering-css";
    // internal link entries : the HSQL test database has no quicklinks_entry_text table, the SQL converter renames it to quicklinks_entry_varchar
    private static final int ENTRY_TYPE_INTERNAL_LINK = 4;
    private static final int UNKNOWN_TEMPLATE_ID = 99999;
    private static final int TEMPLATE_ONE_COLUMN = 2;

    private Plugin _plugin;
    private int _nPageId;
    private Quicklinks _quicklinks;
    private String _strRootEntryText;
    private String _strChildEntryText;
    private QuicklinksPortlet _portlet;

    @Inject
    private IPageService _pageService;

    @BeforeEach
    protected void setUp( ) throws Exception
    {
        super.setUp( );
        _plugin = PluginService.getPlugin( QuicklinksPlugin.PLUGIN_NAME );

        Page page = new Page( );
        page.setParentPageId( PortalService.getRootPageId( ) );
        page.setPageTemplateId( TEMPLATE_ONE_COLUMN );
        page.setName( PORTLET_NAME + "Page" );
        page.setDescription( PORTLET_NAME + "Page" );
        _pageService.createPage( page );
        _nPageId = page.getId( );

        _quicklinks = new Quicklinks( );
        _quicklinks.setTitle( "quicklinks" + new SecureRandom( ).nextLong( ) );
        _quicklinks.setType( QuicklinksType.PORTLET );
        _quicklinks.setWorkgroup( "all" );
        _quicklinks.setRoleKey( "none" );
        _quicklinks.setEnabled( true );
        _quicklinks.setCssStyle( CSS_STYLE );
        QuicklinksHome.create( _quicklinks, _plugin );

        _strRootEntryText = "root" + new SecureRandom( ).nextLong( );
        EntryInternalLink rootEntry = createEntry( _strRootEntryText, EntryHome.ROOT_PARENT_ID );
        _strChildEntryText = "child" + new SecureRandom( ).nextLong( );
        createEntry( _strChildEntryText, rootEntry.getId( ) );

        _portlet = new QuicklinksPortlet( );
        _portlet.setQuicklinksId( _quicklinks.getId( ) );
        _portlet.setPortletTypeId( QuicklinksPortletHome.getInstance( ).getPortletTypeId( ) );
        _portlet.setPluginName( QuicklinksPlugin.PLUGIN_NAME );
        _portlet.setPageId( _nPageId );
        _portlet.setStyleId( 0 );
        _portlet.setColumn( 1 );
        _portlet.setOrder( 1 );
        _portlet.setName( PORTLET_NAME );
        _portlet.setStatus( Portlet.STATUS_PUBLISHED );
        _portlet.setDisplayPortletTitle( 0 );
        _portlet.setDeviceDisplayFlags( Portlet.FLAG_DISPLAY_ON_NORMAL_DEVICE | Portlet.FLAG_DISPLAY_ON_LARGE_DEVICE | Portlet.FLAG_DISPLAY_ON_XLARGE_DEVICE );
        QuicklinksPortletHome.getInstance( ).create( _portlet );
    }

    @AfterEach
    protected void tearDown( ) throws Exception
    {
        if ( _portlet != null )
        {
            QuicklinksPortletHome.getInstance( ).remove( _portlet );
        }
        if ( _quicklinks != null )
        {
            QuicklinksHome.remove( _quicklinks.getId( ), _plugin );
        }
        if ( _nPageId != 0 )
        {
            _pageService.removePage( _nPageId );
        }
        LocalVariables.remove( );
        super.tearDown( );
    }

    /**
     * Every shipped template renders the portlet title, the entries with their nesting, the quicklinks CSS style and the device display classes
     */
    @Test
    public void testRenderEveryShippedTemplate( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        LocalVariables.setLocal( null, request, new MockHttpServletResponse( ) );

        List<PortletTemplate> listTemplates = PortletTemplateHome.findByPortletType( QuicklinksPortletHome.getInstance( ).getPortletTypeId( ) );
        assertEquals( 1, listTemplates.size( ), "the shipped template should be registered in the core for the quicklinks portlet type" );

        for ( PortletTemplate template : listTemplates )
        {
            _portlet.setIdTemplate( template.getId( ) );
            String strContent = _portlet.getHtmlContent( request );

            assertTrue( strContent.contains( MARKER_PORTLET ), "template " + template.getTemplatePath( ) + " should render the portlet wrapper" );
            assertTrue( strContent.contains( PORTLET_NAME ), "template " + template.getTemplatePath( ) + " should render the portlet title" );
            assertTrue( strContent.contains( _strRootEntryText ), "template " + template.getTemplatePath( ) + " should render the root entry" );
            assertTrue( strContent.contains( _strChildEntryText ), "template " + template.getTemplatePath( ) + " should render the child entry" );
            assertTrue( strContent.indexOf( _strRootEntryText ) < strContent.indexOf( _strChildEntryText ),
                    "template " + template.getTemplatePath( ) + " should render the child entry after its parent" );
            assertTrue( strContent.contains( CSS_STYLE ), "template " + template.getTemplatePath( ) + " should render the quicklinks CSS style" );
            assertTrue( strContent.contains( "d-none d-md-block" ), "template " + template.getTemplatePath( ) + " should hide the portlet on small devices" );
        }
    }

    /**
     * The template chosen for the portlet is persisted by the core
     */
    @Test
    public void testChosenTemplateIsPersisted( )
    {
        PortletTemplate template = PortletTemplateHome.findByPortletType( QuicklinksPortletHome.getInstance( ).getPortletTypeId( ) ).get( 0 );

        _portlet.setIdTemplate( template.getId( ) );
        _portlet.update( );

        assertEquals( template.getId( ), PortletHome.findByPrimaryKey( _portlet.getId( ) ).getIdTemplate( ), "the chosen template should be persisted" );
        assertTrue( PortletTemplateHome.isTemplateUsed( template.getId( ) ), "the chosen template should be reported as used" );
    }

    /**
     * An unknown template falls back to the default one and a hidden title is not rendered
     */
    @Test
    public void testFallbackToDefaultTemplate( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        LocalVariables.setLocal( null, request, new MockHttpServletResponse( ) );

        _portlet.setIdTemplate( UNKNOWN_TEMPLATE_ID );
        _portlet.setDisplayPortletTitle( 1 );
        String strContent = _portlet.getHtmlContent( request );

        assertTrue( strContent.contains( MARKER_PORTLET ), "the default template should render the portlet wrapper" );
        assertTrue( strContent.contains( _strRootEntryText ), "the default template should render the root entry" );
        assertFalse( strContent.contains( PORTLET_NAME ), "a hidden portlet title should not be rendered" );
    }

    /**
     * A disabled quicklinks renders nothing
     */
    @Test
    public void testDisabledQuicklinksRendersNothing( )
    {
        _quicklinks.setEnabled( false );
        QuicklinksHome.update( _quicklinks, _plugin );

        assertEquals( "", _portlet.getHtmlContent( new MockHttpServletRequest( ) ), "a disabled quicklinks should not be rendered" );
    }

    /**
     * Creates an internal link entry of the test quicklinks
     *
     * @param strText
     *            the title and the content of the entry
     * @param nIdParent
     *            the parent entry, or the root
     * @return the created entry
     */
    private EntryInternalLink createEntry( String strText, int nIdParent )
    {
        EntryInternalLink entry = new EntryInternalLink( );
        entry.setEntryType( EntryTypeHome.findByPrimaryKey( ENTRY_TYPE_INTERNAL_LINK, _plugin ) );
        entry.setIdQuicklinks( _quicklinks.getId( ) );
        entry.setIdParent( nIdParent );
        entry.setIdOrder( 0 );
        entry.setTitle( strText );
        entry.setContent( strText );
        EntryHome.create( entry, _plugin );

        return entry;
    }
}

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

import java.util.HashMap;
import java.util.Locale;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.rbac.RBACResource;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupResource;
import fr.paris.lutece.portal.service.workgroup.WorkgroupRemovalListenerService;
import fr.paris.lutece.util.xml.XmlUtil;

/**
 *
 * This class represents a Quicklinks object.
 *
 */
public class Quicklinks implements AdminWorkgroupResource, RBACResource, Cloneable
{
    public static final String RESOURCE_TYPE = "QUICKLINKS_QUICKLINKS_TYPE";
    public static final String ROLE_NONE = "none";
    private static final String TAG_QUICKLINKS = "quicklinks";
    private static final String TAG_ENTRY = "entry";
    private static final String TAG_ENTRY_CONTENT = "entry_content";
    private static final String ATTRIBUTE_QUICKLINKS_ID = "id";
    private static final String ATTRIBUTE_QUICKLINKS_TITLE = "title";
    private static final String ATTRIBUTE_QUICKLINKS_TYPE = "type";
    private static final String ATTRIBUTE_QUICKLINKS_ROLE_KEY = "roleKey";
    private static final String ATTRIBUTE_QUICKLINKS_CSS_STYLE = "cssStyle";
    private static final String ATTRIBUTE_ENTRY_ID = "id";
    private static final String ATTRIBUTE_ENTRY_TITLE = "title";
    private static final String ATTRIBUTE_ENTRY_TYPE = "type";
    private static final String ATTRIBUTE_ENTRY_ORDER = "order";
    private static QuicklinksWorkgroupRemovalListener _listenerWorkgroup;
    private int _nId;
    private String _strTitle;
    private QuicklinksType _enumType;
    private boolean _bIsEnabled;
    private String _strRoleKey;
    private String _strWorkgroupKey;
    private String _strCssStyle;

    /**
     * @return the _strCssStyle
     */
    public String getCssStyle( )
    {
        return _strCssStyle;
    }

    /**
     * @param strCssStyle
     *            the _strCssStyle to set
     */
    public void setCssStyle( String strCssStyle )
    {
        _strCssStyle = strCssStyle;
    }

    /**
     * Initialize the {@link Quicklinks}
     */
    public static void init( )
    {
        // Create removal listeners and register them
        if ( _listenerWorkgroup == null )
        {
            _listenerWorkgroup = new QuicklinksWorkgroupRemovalListener( );
            WorkgroupRemovalListenerService.getService( ).registerListener( _listenerWorkgroup );
        }
    }

    /**
     * @return the idQuicklinks
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * @param nId
     *            the idQuicklinks to set
     */
    public void setId( int nId )
    {
        this._nId = nId;
    }

    /**
     * @return the _strTitle
     */
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * @param strTitle
     *            the _strTitle to set
     */
    public void setTitle( String strTitle )
    {
        _strTitle = strTitle;
    }

    /**
     * @return the type
     */
    public QuicklinksType getType( )
    {
        return _enumType;
    }

    /**
     * @param enumType
     *            the type to set
     */
    public void setType( QuicklinksType enumType )
    {
        this._enumType = enumType;
    }

    /**
     * @return the isEnabled
     */
    public boolean isEnabled( )
    {
        return _bIsEnabled;
    }

    /**
     * @param bIsEnabled
     *            the isEnabled to set
     */
    public void setEnabled( boolean bIsEnabled )
    {
        this._bIsEnabled = bIsEnabled;
    }

    /**
     * @return the roleKey
     */
    public String getRoleKey( )
    {
        return _strRoleKey;
    }

    /**
     * @param strRoleKey
     *            the roleKey to set
     */
    public void setRoleKey( String strRoleKey )
    {
        this._strRoleKey = strRoleKey;
    }

    /**
     * @param strWorkgroupKey
     *            the workgroupKey to set
     */
    public void setWorkgroup( String strWorkgroupKey )
    {
        this._strWorkgroupKey = strWorkgroupKey;
    }

    /**
     * @return the workgroupKey
     */
    public String getWorkgroup( )
    {
        return _strWorkgroupKey;
    }

    /**
     * Get The resource Id
     */
    public String getResourceId( )
    {
        return String.valueOf( getId( ) );
    }

    /**
     * Get the resource type code
     */
    public String getResourceTypeCode( )
    {
        return RESOURCE_TYPE;
    }

    /**
     * Get the XML code for {@link Quicklinks}
     * 
     * @param plugin
     *            The {@link Plugin}
     * @param locale
     *            The {@link Locale}
     * @return The HTML code
     */
    public StringBuffer getXml( Plugin plugin, Locale locale )
    {
        StringBuffer strXml = new StringBuffer( );
        HashMap<String, Object> model = new HashMap<>( );
        model.put( ATTRIBUTE_QUICKLINKS_ID, String.valueOf( getId( ) ) );
        model.put( ATTRIBUTE_QUICKLINKS_TITLE, getTitle( ) );
        model.put( ATTRIBUTE_QUICKLINKS_TYPE, getType( ).getI18nKey( ) );
        model.put( ATTRIBUTE_QUICKLINKS_ROLE_KEY, getRoleKey( ) );
        model.put( ATTRIBUTE_QUICKLINKS_CSS_STYLE, getCssStyle( ) );
        XmlUtil.beginElement( strXml, TAG_QUICKLINKS, model );

        EntryFilter filter = new EntryFilter( );
        filter.setIdQuicklinks( getId( ) );
        filter.setIdParent( EntryHome.ROOT_PARENT_ID );

        for ( IEntry entry : EntryHome.findByFilter( filter, plugin ) )
        {
            getEntryXml( strXml, entry, plugin, locale );
        }

        XmlUtil.endElement( strXml, TAG_QUICKLINKS );

        return strXml;
    }

    /**
     * Get the Xml content for the specified entry
     * 
     * @param strXml
     *            The XML buffer
     * @param entry
     *            The {@link Entry}
     * @param plugin
     *            The {@link Plugin}
     * @param locale
     *            The {@link Locale}
     */
    private void getEntryXml( StringBuffer strXml, IEntry entry, Plugin plugin, Locale locale )
    {
        HashMap<String, Object> model = new HashMap<>( );
        model.put( ATTRIBUTE_ENTRY_ID, String.valueOf( entry.getId( ) ) );
        model.put( ATTRIBUTE_ENTRY_TITLE, entry.getTitle( ) );
        model.put( ATTRIBUTE_ENTRY_TYPE, String.valueOf( entry.getEntryType( ).getId( ) ) );
        model.put( ATTRIBUTE_ENTRY_ORDER, String.valueOf( entry.getIdOrder( ) ) );
        XmlUtil.beginElement( strXml, TAG_ENTRY, model );

        for ( IEntry entryChild : entry.getChilds( plugin ) )
        {
            getEntryXml( strXml, entryChild, plugin, locale );
        }

        XmlUtil.addElementHtml( strXml, TAG_ENTRY_CONTENT, entry.getHtml( plugin, locale ) );
        XmlUtil.endElement( strXml, TAG_ENTRY );
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    protected Quicklinks clone( ) throws CloneNotSupportedException
    {
        Quicklinks quicklinks = new Quicklinks( );
        quicklinks.setEnabled( isEnabled( ) );
        quicklinks.setRoleKey( getRoleKey( ) );
        quicklinks.setTitle( getTitle( ) );
        quicklinks.setType( getType( ) );
        quicklinks.setWorkgroup( getWorkgroup( ) );

        return quicklinks;
    }

    /**
     * Copy a {@link Quicklinks}
     *
     * @param plugin
     *            the {@link Plugin}
     * @return The {@link Quicklinks} copy
     */
    public Quicklinks copy( Plugin plugin )
    {
        return copy( plugin, null );
    }

    /**
     * Copy a {@link Quicklinks}
     *
     * @param plugin
     *            the {@link Plugin}
     * @param strNewName
     *            The new name
     * @return The {@link Quicklinks} copy
     */
    public Quicklinks copy( Plugin plugin, String strNewName )
    {
        Quicklinks copy = null;

        try
        {
            copy = this.clone( );
        }
        catch( CloneNotSupportedException e )
        {
            AppLogService.error( "Object Quicklinks does not support clone process." );
        }

        // Update title
        copy.setTitle( strNewName );
        copy = QuicklinksHome.create( copy, plugin );

        EntryFilter filter = new EntryFilter( );
        filter.setIdQuicklinks( getId( ) );
        filter.setIdParent( EntryHome.ROOT_PARENT_ID );

        for ( IEntry entry : EntryHome.findByFilter( filter, plugin ) )
        {
            entry.copy( copy.getId( ), plugin );
        }

        return copy;
    }
}

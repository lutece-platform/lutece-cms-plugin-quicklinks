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

import fr.paris.lutece.portal.business.portlet.IPortletInterfaceDAO;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.portlet.PortletTypeHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods for Quicklinks Portlet objects
 */
public class QuicklinksPortletHome extends PortletHome
{
    /////////////////////////////////////////////////////////////////////////////////
    // Constants
    // Static variable pointed at the DAO instance
    private static IQuicklinksPortletDAO _dao = SpringContextService.getBean( "quicklinks.quicklinksPortletDAO" );

    /* This class implements the Singleton design pattern. */
    private static QuicklinksPortletHome _singleton;

    /**
     * Constructor
     */
    public QuicklinksPortletHome( )
    {
        if ( _singleton == null )
        {
            _singleton = this;
        }
    }

    /**
     * Returns the instance of QuicklinksPortletHome
     *
     * @return the QuicklinksPortletHome instance
     */
    public static PortletHome getInstance( )
    {
        if ( _singleton == null )
        {
            _singleton = new QuicklinksPortletHome( );
        }

        return _singleton;
    }

    /**
     * Returns the identifier of the portlet type
     *
     * @return the portlet type identifier
     */
    public String getPortletTypeId( )
    {
        String strCurrentClassName = this.getClass( ).getName( );
        return PortletTypeHome.getPortletTypeId( strCurrentClassName );
    }

    /**
     * Returns the instance of the portlet DAO singleton
     *
     * @return the instance of the DAO singleton
     */
    public IPortletInterfaceDAO getDAO( )
    {
        return _dao;
    }

    /**
     * return number of quicklinks portlet who are associate to the id quicklinks
     * 
     * @param nIdQuicklinks
     *            the id of the quicklinks
     * @return number of quicklinks portlet who are associate to the id quicklinks
     */
    public static int getCountPortletByIdQuicklinks( int nIdQuicklinks )
    {
        return _dao.selectCountPortletByIdQuicklinks( nIdQuicklinks );
    }
}

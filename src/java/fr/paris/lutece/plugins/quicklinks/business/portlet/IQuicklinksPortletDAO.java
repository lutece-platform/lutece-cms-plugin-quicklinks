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
import fr.paris.lutece.portal.business.portlet.Portlet;

/**
 * IQuicklinksPortletDAO
 */
public interface IQuicklinksPortletDAO extends IPortletInterfaceDAO
{
    /**
     * Deletes records for a portlet identifier in the table quicklinks_portlet
     *
     *
     * @param nPortletId
     *            the portlet identifier
     */
    void delete( int nPortletId );

    /**
     * Insert a new record in the table quicklinks_portlet
     *
     *
     * @param portlet
     *            the instance of the Portlet object to insert
     */
    void insert( Portlet portlet );

    /**
     * Loads the data of Quicklinks Portlet whose identifier is specified in parameter
     *
     *
     * @param nPortletId
     *            The Portlet identifier
     * @return theDocumentListPortlet object
     */
    Portlet load( int nPortletId );

    /**
     * return number of quicklinks portlet who are associate to the id quicklinks
     * 
     * @param nIdQuicklinks
     *            the id of the quicklinks
     * @return number of quicklinks portlet who are associate to the id quicklinks
     */
    int selectCountPortletByIdQuicklinks( int nIdQuicklinks );

    /**
     * Update the record in the table
     *
     *
     * @param portlet
     *            A portlet
     */
    void store( Portlet portlet );
}

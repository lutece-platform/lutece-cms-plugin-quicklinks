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


package fr.paris.lutece.plugins.quicklinks.web;

import fr.paris.lutece.plugins.quicklinks.business.insertservice.InternalLinkInsertService;
import fr.paris.lutece.plugins.quicklinks.business.insertservice.InternalLinkInsertServiceHome;
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.page.IPageService;
import fr.paris.lutece.portal.service.page.PageResourceIdService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.insert.InsertServiceSelectionBean;
import fr.paris.lutece.portal.web.insert.InsertServiceSelectorJspBean;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.text.StringEscapeUtils.escapeEcmaScript;

/**
 * This class provides the user interface of the internal link insert service: search a page, then insert a link to it
 */
@RequestScoped
@Named
@Controller( controllerJsp = "InternalLinkInsertService.jsp", controllerPath = "jsp/admin/plugins/quicklinks/", right = "CORE_LINK_SERVICE_MANAGEMENT", securityTokenEnabled = true )
public class InternalLinkInsertServiceJspBean extends MVCAdminJspBean implements InsertServiceSelectionBean
{
    private static final long serialVersionUID = -5176913689822438398L;

    private static final String REGEX_PAGE_ID = "^[\\d]+$";

    // Views
    private static final String VIEW_SEARCH_PAGE = "searchPage";

    // Actions
    private static final String ACTION_INSERT_LINK = "insertLink";

    // Parameters
    private static final String PARAMETER_PLUGIN_NAME = "plugin_name";
    private static final String PARAMETER_PAGE_NAME = "page_name";
    private static final String PARAMETER_PAGE_ID = "id_page";
    private static final String PARAMETER_PAGE_ID_URL = "page_id";
    private static final String PARAMETER_ALT = "alt";
    private static final String PARAMETER_TARGET = "target";
    private static final String PARAMETER_NAME = "name";
    private static final String PARAMETER_INPUT = "input";
    private static final String PARAMETER_MODE = "mode";

    // Markers
    private static final String MARK_PLUGIN_NAME = "plugin_name";
    private static final String MARK_PAGES_LIST = "pages_list";
    private static final String MARK_URL = "url";
    private static final String MARK_TARGET = "target";
    private static final String MARK_ALT = "alt";
    private static final String MARK_NAME = "name";
    private static final String MARK_INPUT = "input";

    // Templates
    private static final String TEMPLATE_SELECTOR_PAGE = "admin/plugins/quicklinks/internallinkinsertservice_selector.html";
    private static final String TEMPLATE_LINK = "admin/plugins/quicklinks/internallinkinsertservice_link.html";

    // Miscellaneous
    private static final String JSP_DO_INSERT = "jsp/admin/insert/DoInsertIntoElement.jsp";
    private static final int MODE_SESSION = 1;

    /**
     * Return the html form for page selection, as the core insert service screen displays it.
     *
     * @param request
     *         The Http Request
     * @return The html form.
     */
    @Override
    public String getInsertServiceSelectorUI( HttpServletRequest request )
    {
        AdminUser user = AdminUserService.getAdminUser( request );
        IPageService pageService = CDI.current( ).select( IPageService.class ).get( );
        Collection<InternalLinkInsertService> listPagesAuthorized = new ArrayList<>( );

        for ( InternalLinkInsertService page : InternalLinkInsertServiceHome.getPageListbyName( StringUtils.defaultString( request.getParameter( PARAMETER_PAGE_NAME ) ) ) )
        {
            if ( pageService.isAuthorizedAdminPage( page.getIdPage( ), PageResourceIdService.PERMISSION_VIEW, user ) )
            {
                listPagesAuthorized.add( page );
            }
        }

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_PLUGIN_NAME, StringUtils.defaultString( request.getParameter( PARAMETER_PLUGIN_NAME ) ) );
        model.put( MARK_INPUT, request.getParameter( PARAMETER_INPUT ) );
        model.put( MARK_PAGES_LIST, listPagesAuthorized );
        model.put( MARK_URL, AppPathService.getBaseUrl( request ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SELECTOR_PAGE, AdminUserService.getLocale( request ), model );

        return template.getHtml( );
    }

    /**
     * Return the sub categories of the insert service, read by the core insert service list: none.
     *
     * @return An empty list
     */
    public ReferenceList getSubCategories( )
    {
        return new ReferenceList( );
    }

    /**
     * Return the page selection filtered by the searched name
     *
     * @param request
     *         The Http Request
     * @return The html form.
     */
    @View( value = VIEW_SEARCH_PAGE, defaultView = true )
    public String getSearchPage( HttpServletRequest request )
    {
        return getInsertServiceSelectorUI( request );
    }

    /**
     * Insert a link to the selected page into the HTML content. Only the session of the user changes, for the core
     * insert screen to read it back, so the action carries no security token.
     *
     * @param request
     *         The http request
     * @return The redirection result
     */
    @Action( value = ACTION_INSERT_LINK, securityTokenDisabled = true )
    public String doInsertLink( HttpServletRequest request )
    {
        String strPageId = request.getParameter( PARAMETER_PAGE_ID );
        Page page = ( ( strPageId != null ) && strPageId.matches( REGEX_PAGE_ID ) ) ? PageHome.findByPrimaryKey( Integer.parseInt( strPageId ) ) : null;

        if ( page == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP ) );
        }

        String strName = request.getParameter( PARAMETER_NAME );
        UrlItem url = new UrlItem( AppPathService.getPortalUrl( ) );
        url.addParameter( PARAMETER_PAGE_ID_URL, page.getId( ) );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_URL, url.getUrl( ) );
        model.put( MARK_TARGET, request.getParameter( PARAMETER_TARGET ) );
        model.put( MARK_ALT, request.getParameter( PARAMETER_ALT ) );
        model.put( MARK_NAME, StringUtils.isEmpty( strName ) ? page.getName( ) : strName );

        String strInsert = escapeEcmaScript( AppTemplateService.getTemplate( TEMPLATE_LINK, null, model ).getHtml( ) );
        request.getSession( ).setAttribute( InsertServiceSelectorJspBean.SESSION_INSERT, strInsert );

        UrlItem urlDoInsert = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_DO_INSERT );
        urlDoInsert.addParameter( PARAMETER_INPUT, request.getParameter( PARAMETER_INPUT ) );
        urlDoInsert.addParameter( PARAMETER_MODE, MODE_SESSION );

        return redirect( request, urlDoInsert.getUrl( ) );
    }
}

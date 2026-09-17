<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.quicklinks.web.portlet.QuicklinksPortletJspBean"%>
${ quicklinksPortletJspBean.init( pageContext.request, QuicklinksPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ pageContext.response.sendRedirect( quicklinksPortletJspBean.doModify( pageContext.request ) ) }

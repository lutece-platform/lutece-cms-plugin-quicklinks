<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />
<%@page import="fr.paris.lutece.plugins.quicklinks.web.portlet.QuicklinksPortletJspBean"%>
${ quicklinksPortletJspBean.init( pageContext.request,QuicklinksPortletJspBean.RIGHT_MANAGE_ADMIN_SITE) }
${ quicklinksPortletJspBean.getCreate( pageContext.request) }

<%@ include file="../../AdminFooter.jsp" %>
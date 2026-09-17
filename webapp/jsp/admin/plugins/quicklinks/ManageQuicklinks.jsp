<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.quicklinks.web.QuicklinksJspBean"%>

${ quicklinksJspBean.init( pageContext.request , QuicklinksJspBean.RIGHT_MANAGE_QUICKLINKS ) }
${ quicklinksJspBean.getManageQuicklinks( pageContext.request) }

<%@ include file="../../AdminFooter.jsp" %>
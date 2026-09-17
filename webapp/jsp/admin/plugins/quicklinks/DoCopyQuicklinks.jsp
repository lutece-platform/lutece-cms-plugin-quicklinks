<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.quicklinks.web.QuicklinksJspBean"%>


${ quicklinksJspBean.init( pageContext.request,QuicklinksJspBean.RIGHT_MANAGE_QUICKLINKS ) }
${ pageContext.response.sendRedirect( quicklinksJspBean.doCopyQuicklinks( pageContext.request ) ) }

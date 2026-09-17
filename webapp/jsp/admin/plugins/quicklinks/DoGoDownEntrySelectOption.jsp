<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.quicklinks.web.QuicklinksEntrySelectJspBean"%>

${ quicklinksEntrySelectJspBean.init( pageContext.request , QuicklinksEntrySelectJspBean.RIGHT_MANAGE_QUICKLINKS ) }
${ pageContext.response.sendRedirect( quicklinksEntrySelectJspBean.doGoDownSelectOption( pageContext.request ) ) }
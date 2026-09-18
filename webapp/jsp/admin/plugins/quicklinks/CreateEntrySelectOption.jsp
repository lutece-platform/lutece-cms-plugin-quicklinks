<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />
<%@page import="fr.paris.lutece.plugins.quicklinks.web.QuicklinksEntrySelectJspBean"%>
${ quicklinksEntrySelectJspBean.init( pageContext.request, QuicklinksEntrySelectJspBean.RIGHT_MANAGE_QUICKLINKS) }
${ quicklinksEntrySelectJspBean.getCreateSelectOption( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>
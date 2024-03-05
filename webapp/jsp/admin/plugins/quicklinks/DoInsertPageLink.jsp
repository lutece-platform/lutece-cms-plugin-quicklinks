<jsp:include page="../../insert/InsertServiceHeader.jsp" />
<jsp:useBean id="internalLinkInsertService" scope="session" class="fr.paris.lutece.plugins.quicklinks.web.InternalLinkInsertServiceJspBean" />

<% response.sendRedirect( internalLinkInsertService.doInsertUrl( request ) );%>

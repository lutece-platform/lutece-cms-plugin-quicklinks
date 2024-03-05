<jsp:include page="../../insert/InsertServiceHeader.jsp" />
<jsp:useBean id="internalLinkInsertService" scope="session" class="fr.paris.lutece.plugins.quicklinks.web.InternalLinkInsertServiceJspBean" />

<%= internalLinkInsertService.getInsertServiceSelectorUI( request ) %>

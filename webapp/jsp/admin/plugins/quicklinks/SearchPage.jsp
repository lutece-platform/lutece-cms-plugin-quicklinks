<jsp:include page="../../insert/InsertServiceHeader.jsp" />
<%@page import="fr.paris.lutece.plugins.quicklinks.web.InternalLinkInsertServiceJspBean"%>

${ internalLinkInsertServiceJspBean.getInsertServiceSelectorUI( pageContext.request ) }

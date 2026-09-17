<jsp:include page="../../insert/InsertServiceHeader.jsp" />
<%@page import="fr.paris.lutece.plugins.quicklinks.web.InternalLinkInsertServiceJspBean"%>

${ pageContext.response.sendRedirect( internalLinkInsertServiceJspBean.doInsertUrl( pageContext.request ) ) }

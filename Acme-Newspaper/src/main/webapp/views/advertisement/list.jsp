<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


	<display:table pagesize="5" class="displaytag" keepStatus="false"
		name="advertisements" requestURI="${requestURI }" id="row">
	
	
		<!-- Attributes -->
	
		<acme:column code="advertisement.title" path="title" />
		<display:column><a target="_blank" href="${row.targetPage }"><img width="250px" height="100px" alt="banner" src="${row.banner }"></a></display:column>
		
		<security:authorize access="hasRole('ADMIN')">
		
			<display:column>

				<acme:button url="advertisement/admin/delete.do?advertisementId=${row.id}" code="advertisement.delete" />

			</display:column>
			
		</security:authorize>
	
	</display:table>

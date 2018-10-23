<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>


<display:table pagesize="5" class="displaytag" keepStatus="true"
	name="users" id="row" requestURI="${requestURI }">
	

	<!-- Attributes -->
	
	<acme:column code="actor.name" path="name"/>
	<acme:column code="actor.surname" path="surname"/>
	<acme:column code="actor.postalAddress" path="postalAddress"/>
	<acme:column code="actor.phone" path="phone"/>
	<acme:column code="actor.email" path="email"/>

	
	<display:column>
	<acme:button url="actor/display.do?actorId=${row.id}" code="actor.display"/>
	</display:column>
	
	
	<security:authorize access="hasRole('USER')">
		<display:column>
			<jstl:if test="${!principal.following.contains(row) && row.id!=principal.id}">
				<acme:button url="actor/user/follow.do?userId=${row.id}" code="actor.user.follow"/>
			</jstl:if>
		</display:column>
		
		<display:column>
			<jstl:if test="${principal.following.contains(row) && row.id!=principal.id}">
				<acme:button url="actor/user/unfollow.do?userId=${row.id}" code="actor.user.unfollow"/>
			</jstl:if>
		</display:column>
	</security:authorize>
	


</display:table>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>


<form:form action="actor/${authority}/edit.do"
	modelAttribute="${authority}">

	<form:hidden path="id" />
	<form:hidden path="version" />

	<form:hidden path="userAccount" />

	<acme:textbox code="actor.name" path="name" />

	<acme:textbox code="actor.surname" path="surname" />

	<acme:textbox code="actor.email" path="email" />

	<acme:textbox code="actor.phone" path="phone"
		placeholder="+34954645178" />

	<acme:textbox code="actor.postalAddress" path="postalAddress" />

	<security:authorize access="isAuthenticated()">
		<security:authentication property="principal.username" var="user" />
	</security:authorize>
	<jstl:if test="${user == actor.userAccount.username }">
		<acme:submit name="save" code="actor.save" />
		<acme:button url="actor/display-principal.do" code="actor.cancel" />
	</jstl:if>
</form:form>

<jstl:if test="${authority!='admin'}">
	<form:form action="actor/${authority}/edit.do">
		<jstl:if test="${user == actor.userAccount.username }">
			<input type="hidden" name="actorId" value="${actor.id}" />
			<acme:submit name="delete" code="actor.delete" />
		</jstl:if>
	</form:form>
</jstl:if>
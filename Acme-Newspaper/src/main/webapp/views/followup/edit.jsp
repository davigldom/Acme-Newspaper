<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<form:form action="followup/user/edit.do?articleId=${articleId}" modelAttribute="followup" method = "post">

	<form:hidden path="id"/>
	<form:hidden path="version"/>

	
	<acme:textbox
	code="followup.title"
	path="title" />
	
	<acme:textarea
	code="followup.summary"
	path="summary" />
	
	<acme:textarea
	code="followup.body"
	path="body" />
	
	<spring:message code="followup.pictures.placeholder" var="picturesPlaceholder"/>
	<acme:textarea
	code="followup.pictures"
	path="pictures" placeholder="${picturesPlaceholder }" />
	
	<acme:submit
	code="followup.save"
	name="save" />

	<acme:button url="article/display.do?articleId=${articleId }" code="followup.cancel"/>
	
</form:form>
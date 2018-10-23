<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<form:form action="volume/user/create.do" modelAttribute="volume" method = "post">

<%-- 	<form:hidden path="id"/> --%>
<%-- 	<form:hidden path="version"/> --%>

	
	<acme:textbox
	code="volume.title"
	path="title" />
	
	<acme:textarea
	code="volume.description"
	path="description" />
	
	<acme:textbox
	code="volume.year"
	path="year" />
	
	
	
	<acme:submit
	code="volume.save"
	name="save" />

	<acme:button url="volume/user/list.do" code="volume.cancel"/>
	
</form:form>
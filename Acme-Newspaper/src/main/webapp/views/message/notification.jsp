<%--
 * edit.jsp
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<form:form action="message/administrator/notification.do" modelAttribute="notification"> 

	<form:hidden path="id"/>
	<form:hidden path="version"/>
	
	
	<acme:textbox
		code="ourMessage.subject"
		path="subject" />
			
	<form:label path="priorityLevel">
		<spring:message code="ourMessage.priorityLevel" />
	</form:label>
	<form:errors cssClass="error" path="priorityLevel" />
	<br>
	<form:select id="levels" path="priorityLevel">
		<spring:message code="ourMessage.high" var="messageHigh" />
		<spring:message code="ourMessage.med" var="messageMed" />
		<spring:message code="ourMessage.low" var="messageLow" />
		<form:option value="0" label="----" />
		<form:option value="HIGH" label="${messageHigh }" />
		<form:option value="MED" label="${messageMed }" />
		<form:option value="LOW" label="${messageLow }" />
	</form:select>
	<br><br>
			
	<acme:textarea
		code="ourMessage.body"
		path="body" />
	
	
	<acme:submit name="broadcast" code="ourMessage.save"/>
	
	<acme:button url="folder/list.do?folderId=0" code="ourMessage.cancel"/>
	
</form:form>
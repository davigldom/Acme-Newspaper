<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>


<spring:message code="ourMessage.subject" var="subject" />
<b><jstl:out value="${subject }: " /></b>
<jstl:out value="${ourMessage.subject}" />
<br><br>

<spring:message code="ourMessage.moment" var="moment" />
<b><jstl:out value="${moment}: " /></b>
<jstl:out value="${momentFormatted}" />
<br><br>

<spring:message code="ourMessage.sender" var="senderTitle" />
<b><jstl:out value="${senderTitle }: " /></b>
<jstl:out value="${sender.name } ${sender.surname }" />
<br><br>

<spring:message code="ourMessage.recipient" var="recipientTitle" />
<b><jstl:out value="${recipientTitle }: " /></b>
<jstl:out value="${recipient.name } ${recipient.surname }" />
<br><br>

<spring:message code="ourMessage.priorityLevel" var="priorityLevel" />
<b><jstl:out value="${priorityLevel }: " /></b>
<jstl:if test="${ourMessage.priorityLevel==high }">
<spring:message code="ourMessage.high" var="highValue" />
<jstl:out value="${highValue }" />
</jstl:if>
<jstl:if test="${ourMessage.priorityLevel==med}">
<spring:message code="ourMessage.med" var="medValue" />
<jstl:out value="${medValue }" />
</jstl:if>
<jstl:if test="${ourMessage.priorityLevel==low}">
<spring:message code="ourMessage.low" var="lowValue" />
<jstl:out value="${lowValue }" />
</jstl:if>
<br><br>

<div id="messageBody">
	<spring:message code="ourMessage.body" var="body" />
	<b><jstl:out value="${body }: " /></b><br/>
	<jstl:out value="${ourMessage.body}" />
	<br><br>
</div>

<acme:delete url="message/delete.do?messageId=${ourMessage.id}" code="ourMessage.delete" returnMessage="ourMessage.confirm.delete"/>

<acme:button url="folder/list.do?folderId=${rootId }" code="ourMessage.back"/>

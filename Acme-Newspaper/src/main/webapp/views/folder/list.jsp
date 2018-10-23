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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Listing grid -->

<jstl:if test="${rootName!=null}">
<spring:message code="folder.title" var="folderTitle" />
<h2><jstl:out value="${folderTitle } ${rootName }"></jstl:out></h2>
</jstl:if>

<jstl:choose>
	<jstl:when test="${thereAreChildren==true}">
		<display:table pagesize="5" class="displaytag" keepStatus="false"
			name="folders" requestURI="folder/list.do" id="row">
	
			<!-- Attributes -->
			
			<acme:column code="folder.name" path="name" />
	
			<!-- Action links -->
		
			<acme:columnButton url="folder/list.do?folderId=${row.id}"
				code="folder.enter" />
			
			<display:column>
				<jstl:if test="${row.sysFolder==false}">
					<acme:button url="folder/edit.do?folderId=${row.id}"
						code="folder.edit" />
				</jstl:if>
			</display:column>	
		
		</display:table>
		<br><br>
	</jstl:when>
	<jstl:otherwise>
		<spring:message code="folder.error" var="folderError" />
		<div><b><jstl:out value="${folderError }"></jstl:out></b></div>
		<br>
	</jstl:otherwise>
</jstl:choose>
	
	<acme:button url="folder/create.do"
		code="folder.create" />
		
	<jstl:if test="${folderId!=0}">	
		<acme:button url="folder/list.do?folderId=${rootOfRootId }"
			code="folder.backRoot" />
	</jstl:if>
	<br><br>

<jstl:if test="${folderId!=0}">
	<jstl:choose>
		<jstl:when test="${!empty messages }">
			<display:table pagesize="5" class="displaytag" keepStatus="true"
				name="messages" requestURI="folder/list.do" id="rowMessage">
			
				<!-- Attributes -->
				
				<fmt:formatDate value="${rowMessage.moment.time}" type="date" pattern="yyyy-MM-dd HH:mm" var ="formatedMoment"/>
				<acme:columnOut code="message.moment" path="${formatedMoment}" />
				
				<acme:column code="message.subject" path="subject" />
				
				<acme:column code="message.priorityLevel" path="priorityLevel" />
				
				<acme:columnOut code="message.sender" path="${rowMessage.sender.name } ${rowMessage.sender.surname }" />
				
				<acme:columnOut code="message.recipient" path="${rowMessage.recipient.name } ${rowMessage.recipient.surname }" />
				
				<acme:columnButton url="message/display.do?messageId=${rowMessage.id}"
					code="message.display" />
				
				<acme:columnButton url="message/change-folder.do?messageId=${rowMessage.id}"
					code="message.change" />
				
			</display:table>
			<br><br>
		</jstl:when>
		<jstl:otherwise>
			<spring:message code="message.error" var="messageError" />
			<div><b><jstl:out value="${messageError }"></jstl:out></b></div>
			<br>
		</jstl:otherwise>
	</jstl:choose>
</jstl:if>

<acme:button url="message/create.do"
	code="message.create" />
	
<security:authorize access="hasRole('ADMIN')">
	<acme:button url="message/administrator/notification.do"
		code="message.notification" />
	<br/>
</security:authorize>
	
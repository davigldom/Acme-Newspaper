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

<form:form action="folder/edit.do" modelAttribute="folder"> 
	
	<form:hidden path="id"/>
	<form:hidden path="version"/>
		
	<acme:textbox
		code="folder.name"
		path="name" />
		
	<jstl:if test="${folder.id==0}">
		<form:label path="root">
			<spring:message code="folder.root" />:
		</form:label>
		<form:errors cssClass="error" path="root" />
		<br/>
		<form:select id="roots" path="root">
			<form:option value="0" label="----" />		
			<form:options items="${roots}" itemValue="id"
				itemLabel="name" />
		</form:select>
		<br/>
	</jstl:if>
		
	<jstl:if test="${folder.id!=0}">
		<form:label path="root">
			<spring:message code="folder.root" />:
		</form:label>
		<form:errors cssClass="error" path="root" />
		<br/>
		<form:select id="roots" path="root">
			<form:option value="0" label="----" />
			<jstl:forEach var="root" items="${roots}">
				<jstl:if test="${root.id == rootId }">
					<form:option selected="true" value="${root.id }">
						<jstl:out value="${root.name }" />
					</form:option>
				</jstl:if>
				<jstl:if test="${root.id != rootId }">
					<form:option value="${root.id }">
						<jstl:out value="${root.name }" />
					</form:option>
				</jstl:if>
			</jstl:forEach>
		</form:select>
		<br/>
	</jstl:if>
		
	<br/>
		
	<acme:submit
		code="folder.save"
		name="save" />	
		
	<jstl:if test="${folder.id != 0}">
		<acme:delete url="folder/delete.do?folderId=${folder.id}" code="folder.delete" returnMessage="folder.confirm.delete" />
	</jstl:if>
		
	<acme:button url="folder/list.do?folderId=${folder.id}" code="folder.cancel"/>
			
</form:form>

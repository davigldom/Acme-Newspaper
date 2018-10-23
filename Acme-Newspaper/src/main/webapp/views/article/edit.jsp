<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<form:form action="article/user/edit.do?newspaperId=${newspaperId }" modelAttribute="article" method = "post">

	<form:hidden path="id"/>
	<form:hidden path="version"/>

	
	<acme:textbox
	code="article.title"
	path="title" />
	
	<acme:textarea
	code="article.summary"
	path="summary" />
	
	<acme:textarea
	code="article.body"
	path="body" />
	
	<spring:message code="article.pictures.placeholder" var="picturesPlaceholder"/>
	<acme:textarea
	code="article.pictures"
	path="pictures" placeholder="${picturesPlaceholder }" />
	
	<jstl:if test="${article.id != 0 }">
	<acme:checkbox code="article.draft" path="draft"/>
	<br>
	</jstl:if>
	
	<acme:submit
	code="article.save"
	name="save" />

	<acme:button url="article/user/list.do" code="article.cancel"/>
	
</form:form>
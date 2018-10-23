<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<jstl:if test="${isListingCreated == false}">
	<jstl:if test="${!empty articles}">
		<div id="search">
			<form method="get" action="article/search-word.do">
				<spring:message code="article.search" var="searchButton" />
				<input type="text" name="keyword"> <input type="hidden"
					name="newspaperId" value="${newspaperId }"> <input
					type="submit" value="${searchButton }">
			</form>
		</div>
		<br>
		<br>
	</jstl:if>
</jstl:if>


<display:table pagesize="5" class="displaytag" keepStatus="false"
	name="articles" requestURI="${requestURI }" id="row">


	<!-- Attributes -->

	<acme:column code="article.title" path="title" />

	<jstl:choose>
		<jstl:when test="${row.publicationMoment != null }">
			<fmt:formatDate value="${row.publicationMoment.time}" type="date"
				pattern="dd/MM/yyyy" var="formatedPublicationMoment" />
			<acme:columnOut code="article.moment"
				path="${formatedPublicationMoment}" />
		</jstl:when>
		<jstl:otherwise>
			<spring:message code="article.noPublishedYet" var="noPublishedYet" />
			<acme:columnOut code="article.moment" path="${noPublishedYet}" />
		</jstl:otherwise>
	</jstl:choose>

	<spring:message code="article.yes" var="yes" />
	<spring:message code="article.no" var="no" />
	<jstl:choose>
		<jstl:when test="${row.published == true}">
			<acme:columnOut code="article.published" path="${yes}" />
		</jstl:when>
		<jstl:otherwise>
			<acme:columnOut code="article.published" path="${no}" />
		</jstl:otherwise>
	</jstl:choose>

	<jstl:if test="${isListingCreated == false}">
		<acme:columnOut code="article.creator"
			path="${row.creator.name } ${row.creator.surname }" />
	</jstl:if>
	
	<jstl:if test="${isListingCreated == true}">
		<acme:columnButton url="article/display.do?articleId=${row.id}"
			code="article.display" />
	</jstl:if>




	<security:authorize access="hasRole('USER')">
		<display:column>
			<jstl:if test="${row.creator == principal}">
				<jstl:if test="${row.published == false}">
					<jstl:if test="${row.draft == true}">
						<acme:button url="article/user/edit.do?articleId=${row.id}"
							code="article.edit" />
					</jstl:if>
				</jstl:if>
			</jstl:if>
		</display:column>
	</security:authorize>

</display:table>
<br>
<br>

<jstl:if test="${isListingCreated == false}">
	<acme:button url="newspaper/list.do" code="article.back" />
</jstl:if>

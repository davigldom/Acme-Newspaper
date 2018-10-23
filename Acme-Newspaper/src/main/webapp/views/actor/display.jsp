<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<security:authorize access="isAuthenticated()">
	<security:authentication property="principal.username" var="user" />
</security:authorize>
<spring:message code="actor.name" var="name" />
<jstl:out value="${name }: " />
<jstl:out value="${actor.name}" />
<br>

<spring:message code="actor.surname" var="surname" />
<jstl:out value="${surname}: " />
<jstl:out value="${actor.surname}" />
<br>

<spring:message code="actor.email" var="email" />
<jstl:out value="${email }: " />
<jstl:out value="${actor.email}" />
<br>

<spring:message code="actor.phone" var="phone" />
<jstl:out value="${phone }: " />
<jstl:out value="${actor.phone}" />
<br>

<spring:message code="actor.postalAddress" var="postalAddress" />
<jstl:out value="${postalAddress }: " />
<jstl:out value="${actor.postalAddress}" />
<br>

<br />
<br />

<security:authorize access="isAuthenticated()">
	<form:form action="actor/${authority}/edit.do" method="post">
		<input type="hidden" name="actorId" value="${actor.id}" />
		<jstl:if test="${user == actor.userAccount.username }">
			<acme:button url="actor/edit.do?actorId=${actor.id}"
				code="actor.edit" />
			<jstl:if test="${authority ne 'admin' }">
				<acme:submit name="delete" code="actor.delete" />
			</jstl:if>
		</jstl:if>
	</form:form>
</security:authorize>
<br>
<acme:button url="welcome/index.do" code="actor.back" />

<br />
<br />
<%-- <display:table pagesize="5" class="displaytag" keepStatus="true" --%>
<%-- 	name="chirps" requestURI="${requestURI }" id="row"> --%>

<%-- 	<acme:columnOut code="actor.chirp.title" path="${row.title }" /> --%>
<%-- 	<acme:columnOut code="actor.chirp.description" --%>
<%-- 		path="${row.description }" /> --%>

<%-- 	<fmt:formatDate value="${row.moment.time}" type="date" --%>
<%-- 		pattern="dd/MM/yyyy HH:mm" var="formatedPublicationDate" /> --%>
<%-- 	<acme:columnOut code="newspaper.moment" --%>
<%-- 		path="${formatedPublicationDate}" /> --%>
<%-- 	<security:authorize access="hasRole('ADMIN')"> --%>
<%-- 		<display:column> --%>
<%-- 			<acme:button url="chirp/admin/delete.do?chirpId=${row.id }" --%>
<%-- 				code="chirp.delete" /> --%>
<%-- 		</display:column> --%>
<%-- 	</security:authorize> --%>
<%-- </display:table> --%>
<!-- <br /> -->


<jstl:set var="contains" value="false" />
<jstl:forEach var="authority" items="${actor.userAccount.authorities}">
	<jstl:if test="${authority eq 'USER'}">
		<jstl:set var="contains" value="true" />
	</jstl:if>
</jstl:forEach>

<jstl:if test="${contains }">
	<h1>
		<spring:message code="actor.articles" />
	</h1>

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

		<acme:columnButton url="article/display.do?articleId=${row.id}"
			code="article.display" />

		<security:authorize access="hasRole('USER')">
			<display:column>
				<jstl:if
					test="${row.creator.userAccount.username == actor.userAccount.username}">
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



	<div class="background">

		<h1>Chirps</h1>

		<security:authorize access="hasRole('USER')">
			<jstl:if test="${user == actor.userAccount.username }">
				<br />
				<br />
				<acme:button url="chirp/user/create.do" code="actor.chirp.create" />
			</jstl:if>
		</security:authorize>
		<br /> <br />


		<jstl:if test="${empty chirps}">
			<p>
				<spring:message code="chirp.empty" />
			</p>
		</jstl:if>

		<jstl:if test="${!empty chirps}">

			<jstl:forEach items="${chirps}" var="entry">

				<div class="chirp">
					<a id="name"
						href="actor/display.do?actorId=<jstl:out value="${entry.user.id}"/>"><jstl:out
							value="${entry.user.name} ${entry.user.surname }" /> <span
						class="glyphicon glyphicon-user"></span></a>
					<fmt:formatDate value="${entry.moment.time}" type="date"
						pattern="dd/MM/yyyy HH:mm" var="formatedPublicationMoment" />
					<h1 id="moment">
						<jstl:out value="${formatedPublicationMoment}" />
					</h1>
					<h1 id="title">
						<jstl:out value="${entry.title}" />
					</h1>
					<br>
					<h1 id="text">
						<jstl:out value="${entry.description}" />
					</h1>
					<br>
					<security:authorize access="hasRole('ADMIN')">
						<acme:button url="chirp/admin/delete.do?chirpId=${entry.id }"
							code="chirp.delete" />
					</security:authorize>

				</div>
				<br>
			</jstl:forEach>
		</jstl:if>
	</div>
</jstl:if>



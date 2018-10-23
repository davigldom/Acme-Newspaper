<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="background">

	
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
			<fmt:formatDate value="${entry.moment.time}" type="date" pattern="dd/MM/yyyy HH:mm" var ="formatedPublicationMoment"/>
			<h1 id="moment"><jstl:out value="${formatedPublicationMoment}" /></h1>
				<h1 id="title">
					<jstl:out value="${entry.title}" />
				</h1>
				<br>
				<h1 id="text">
					<jstl:out value="${entry.description}" />
				</h1>
				<br>
				<security:authorize access="hasRole('ADMIN')">
					<a href="chirp/admin/delete.do?chirpId=${entry.id }" id="delete" ><span class="glyphicon glyphicon-trash"></span></a>
				</security:authorize>

			</div>
			<br>
		</jstl:forEach>
	</jstl:if>
</div>

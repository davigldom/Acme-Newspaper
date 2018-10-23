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

<jstl:if test="${ad != null }">
	<spring:message code="article.ad" var="advertisement" />
	<b><jstl:out value="${advertisement}: " /></b>
	<br>
	<jstl:out value="${ad.title }" />
	<br>
	<a target="_blank" href="${ad.targetPage}"><img width="50%" height="20%" alt="banner" src="${ad.banner }"></a>
	<br><br><br><br>
</jstl:if>


<spring:message code="article.moment" var="moment" />
<b><jstl:out value="${moment}: " /></b>
<jstl:choose>
	<jstl:when test="${momentFormated != null }">
		<jstl:out value="${momentFormated}" />
	</jstl:when>
	<jstl:otherwise>
		<spring:message code="article.noPublishedYet" var="noPublishedYet" />
		<jstl:out value="${noPublishedYet}" />
	</jstl:otherwise>
</jstl:choose>
<br>
<br>

<jstl:if test="${!empty article.followups }">
	<b><spring:message code="article.followup.show" /></b>:
	<jstl:forEach var="followup" items="${article.followups }">
		<h4>
			<jstl:out value="${followup.title }" />
		</h4>
		<fmt:formatDate value="${followup.publicationMoment.time}" type="date"
			pattern="dd/MM/yyyy HH:mm" var="formatedPublicationMoment" />
		<i>(<jstl:out value="${formatedPublicationMoment}" />)
		</i>
		<br>
		<p>
			<jstl:out value="${followup.summary }" />
		</p>
		<p>
			<jstl:out value="${followup.body }" />
		</p>
		<jstl:forEach var="picture" items="${followup.pictures}">
			<img src="<jstl:out value="${picture}" />" alt="${picture }"
				height="200" />
		</jstl:forEach>
		<br>
	</jstl:forEach>
</jstl:if>


<div id="articleSummary">
	<b><jstl:out value="${article.summary}" /></b>
</div>
<br>

<jstl:if test="${!empty article.pictures }">
	<jstl:forEach var="picture" items="${article.pictures}">
		<img src="<jstl:out value="${picture}" />" alt="${picture }"
			height="200" />
	</jstl:forEach>
	<br>
	<br>
	<!-- </div> -->
</jstl:if>

<div id="articleBody">
	<jstl:out value="${article.body}" />
</div>
<br>

<spring:message code="article.yes" var="yes" />
<spring:message code="article.no" var="no" />
<spring:message code="article.published" var="published" />
<b><jstl:out value="${published }: " /></b>
<jstl:choose>
	<jstl:when test="${article.published == true}">
		<jstl:out value="${yes}" />
	</jstl:when>
	<jstl:otherwise>
		<jstl:out value="${no}" />
	</jstl:otherwise>
</jstl:choose>
<br>
<br>

<spring:message code="article.draft" var="draft" />
<b><jstl:out value="${draft }: " /></b>
<jstl:choose>
	<jstl:when test="${article.draft== true}">
		<jstl:out value="${yes}" />
	</jstl:when>
	<jstl:otherwise>
		<jstl:out value="${no}" />
	</jstl:otherwise>
</jstl:choose>
<br>
<br>

<security:authorize access="hasRole('USER')">
	<jstl:if test="${article.creator == principal}">
		<jstl:if test="${article.published != true}">
			<jstl:if test="${article.draft == true}">
				<acme:button url="article/user/edit.do?articleId=${articleId}"
					code="article.edit" />
			</jstl:if>
		</jstl:if>
	</jstl:if>
</security:authorize>

<security:authorize access="hasRole('USER')">
	<jstl:if test="${article.creator == principal}">
		<jstl:if test="${article.draft == false && article.published==true}">
			<acme:button url="followup/user/edit.do?articleId=${article.id }"
				code="article.followup.create" />
		</jstl:if>
	</jstl:if>
</security:authorize>

<security:authorize access="hasRole('ADMIN')">
	<acme:button url="article/admin/delete.do?articleId=${articleId}"
		code="article.delete" />
</security:authorize>

<acme:button url="/newspaper/display.do?newspaperId=${article.newspaper.id }"
	code="article.back" />

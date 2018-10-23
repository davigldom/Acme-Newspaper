<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<spring:message code="newspaper.description" var="description" />
<b><jstl:out value="${description }: " /></b>
<jstl:out value="${newspaper.description}" />
<br>
<br>

<b><spring:message code="newspaper.price" />:</b>
<jstl:choose>
	<jstl:when test="${newspaper.price != null }">
		<fmt:formatNumber var="priceFormat" type="number" minFractionDigits="2" maxFractionDigits="2" value="${newspaper.price}" />
		<jstl:out value="${priceFormat}" />
	</jstl:when>
	<jstl:otherwise>
		<spring:message code="newspaper.noPrice" var="noPrice" />
		<jstl:out value="${noPrice}" />
	</jstl:otherwise>
</jstl:choose>
<br>
<br>

<spring:message code="newspaper.moment" var="moment" />
<b><jstl:out value="${moment}: " /></b>
<jstl:choose>
	<jstl:when test="${momentFormated != null }">	
		<jstl:out value="${momentFormated}" />
	</jstl:when>
	<jstl:otherwise>
		<spring:message code="newspaper.noPublishedYet" var="noPublishedYet" />
		<jstl:out value="${noPublishedYet}" />
	</jstl:otherwise>
</jstl:choose>
<br>
<br>

<spring:message code="newspaper.picture" var="picture" />
<b><jstl:out value="${picture }: " /></b>
<br>
<jstl:choose>
	<jstl:when test="${newspaper.picture != '' }">
		<img src="<jstl:out value="${newspaper.picture }" />"
			alt="${picture }" height="200" />
	</jstl:when>
	<jstl:otherwise>
		<spring:message code="newspaper.noPicture" var="noPicture" />
		<jstl:out value="${noPicture }" />
	</jstl:otherwise>
</jstl:choose>
<br>
<br>

<b><spring:message code="newspaper.private"/>: </b>
	<jstl:if test="${newspaper.makePrivate == true}">
		<span class="glyphicon glyphicon-ok"></span>
	</jstl:if>
	<jstl:if test="${newspaper.makePrivate == false}">
		<span class="glyphicon glyphicon-remove"></span>
	</jstl:if>
<br>
<br>

<spring:message code="newspaper.open" var="open" />
<spring:message code="newspaper.close" var="close" />
<spring:message code="newspaper.published" var="published" />
<spring:message code="newspaper.noPublished" var="noPublished" />
<spring:message code="newspaper.status" var="status" />
<b><jstl:out value="${status }: " /></b>
<jstl:choose>
	<jstl:when test="${newspaper.status == 'PUBLISHED'}">
		<jstl:out value="${published}" />
	</jstl:when>
	<jstl:when test="${newspaper.status == 'OPEN'}">
		<jstl:out value="${noPublished} - ${open}" />
	</jstl:when>
	<jstl:when test="${newspaper.status == 'CLOSE'}">
		<jstl:out value="${noPublished} - ${close}" />
	</jstl:when>
</jstl:choose>
<br>
<br>

<jstl:if test="${newspaper.makePrivate==true}">
	<security:authorize access="hasRole('CUSTOMER') or hasRole('ADMIN')">
		<jstl:if test="${alreadySubscribed==true}">
			<h4><b><spring:message code="newspaper.tablecontent"/></b></h4>
			<jstl:forEach var="article" items="${articles}">
				<img src="images/line.png" width="100%" height="40" />
				
				<h3><a href="article/display.do?articleId=${article.id }" ><jstl:out value="${article.title}" /></a></h3>
				
				<b><spring:message code="article.creator"/>:</b>
				<a href="actor/display.do?actorId=${article.creator.id }" ><jstl:out value="${article.creator.name} ${article.creator.surname}" /></a>
				<br><br>
				
				<div class="cortar" align="justify" >
					<jstl:out value="${article.summary}" />
				</div>
				<br><br>
			</jstl:forEach>
		</jstl:if>
	</security:authorize>
	<security:authorize access="hasRole('CUSTOMER')">	
		<jstl:if test="${alreadySubscribed==false}">
			<h4><b><spring:message code="newspaper.tablecontent"/></b></h4>	
			<jstl:forEach var="article" items="${articles}">
				<img src="images/line.png" width="100%" height="40" />
				
				<h3><jstl:out value="${article.title}" /></h3>
				
				<b><spring:message code="article.creator"/>:</b>
				<a href="actor/display.do?actorId=${article.creator.id}" ><jstl:out value="${article.creator.name} ${article.creator.surname}" /></a>
				<br><br>
				
				<div class="cortar">
					<jstl:out value="${article.summary}" />
				</div>
				<br><br>
			</jstl:forEach>
			
			<jstl:if test="${newspaper.status == 'PUBLISHED'}">
				<acme:button url="newspaper/customer/subscription/create.do?newspaperId=${newspaper.id}" code="subscription.create"/>
			</jstl:if>
		</jstl:if>
	</security:authorize>

	<security:authorize access="hasRole('USER')">
		<security:authentication property="principal.username" var="username" />
			<jstl:if test="${username == newspaper.publisher.userAccount.username}">
				<h4><b><spring:message code="newspaper.tablecontent"/></b></h4>
				<jstl:forEach var="article" items="${articles}">
					<img src="images/line.png" width="100%" height="40" />
					
					<h3><a href="article/display.do?articleId=${article.id }" ><jstl:out value="${article.title}" /></a></h3>
					
					<b><spring:message code="article.creator"/>:</b>
					<a href="actor/display.do?actorId=${article.creator.id }" ><jstl:out value="${article.creator.name} ${article.creator.surname}" /></a>
					<br><br>
					
					<div class="cortar" align="justify" >
						<jstl:out value="${article.summary}" />
					</div>
					<br><br>
				</jstl:forEach>
			</jstl:if>
			<jstl:if test="${username != newspaper.publisher.userAccount.username}">
				<h4><b><spring:message code="newspaper.tablecontent"/></b></h4>
				<jstl:forEach var="article" items="${articles}">
					<img src="images/line.png" width="100%" height="40" />
					
					<h3><jstl:out value="${article.title}" /></h3>
					
					<b><spring:message code="article.creator"/>:</b>
					<a href="actor/display.do?actorId=${article.creator.id }" ><jstl:out value="${article.creator.name} ${article.creator.surname}" /></a>
					<br><br>
					
					<div class="cortar" align="justify" >
						<jstl:out value="${article.summary}" />
					</div>
					<br><br>
				</jstl:forEach>
			</jstl:if>
	</security:authorize>
	
	<security:authorize access="!isAuthenticated()">
		<h4><b><spring:message code="newspaper.tablecontent"/></b></h4>
		<jstl:forEach var="article" items="${articles}">
			<img src="images/line.png" width="100%" height="40" />
			
			<h3><jstl:out value="${article.title}" /></h3>
			
			<b><spring:message code="article.creator"/>:</b>
			<a href="actor/display.do?actorId=${article.creator.id }" ><jstl:out value="${article.creator.name} ${article.creator.surname}" /></a>
			<br><br>
			
			<div class="cortar" align="justify" >
				<jstl:out value="${article.summary}" />
			</div>
			<br><br>
		</jstl:forEach>
	</security:authorize>

</jstl:if>

<jstl:if test="${newspaper.makePrivate==false}">
	<h4><b><spring:message code="newspaper.tablecontent"/></b></h4>

	<jstl:forEach var="article" items="${articles}">
		
		<img src="images/line.png" width="100%" height="40" />
		
		<h3><a href="article/display.do?articleId=${article.id }" ><jstl:out value="${article.title}" /></a></h3>
		
		<b><spring:message code="article.creator"/>:</b>
		<a href="actor/display.do?actorId=${article.creator.id }" ><jstl:out value="${article.creator.name} ${article.creator.surname}" /></a>
		<br><br>
		
		<div class="cortar" align="justify" >
			<jstl:out value="${article.summary}" />
		</div>
		<br><br>		

	</jstl:forEach>
	
</jstl:if>

<br>
<br>

<security:authorize access="hasRole('USER')">
	<jstl:if test="${newspaper.publisher == principal}">
		<jstl:if test="${newspaper.status != 'PUBLISHED'}">
			<acme:button url="newspaper/user/edit.do?newspaperId=${newspaperId}"
				code="newspaper.edit" />
		</jstl:if>
	</jstl:if>
</security:authorize>

<security:authorize access="hasRole('ADMIN')">
	<acme:button url="newspaper/admin/delete.do?newspaperId=${newspaperId}"
				code="newspaper.delete" />
</security:authorize>

<acme:button url="/newspaper/list.do" code="newspaper.back"/>

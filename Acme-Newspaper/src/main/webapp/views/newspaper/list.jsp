<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<jstl:if test="${isListingCreated == false}">
	<jstl:if test="${!empty newspapers}">
		<div id="search">
			<form method="get" action="newspaper/search-word.do">
				<spring:message code="newspaper.search" var="searchButton" />
				<input type="text" name="keyword"> 
				<input type="submit" value="${searchButton }">
			</form>
		</div>
		<br>
		<br>
	</jstl:if>
	
	<div>
		<ul id="menu">
			<li>		
				<a id="showPublished" style='color:#7CAFB7;' href="javascript:show('published')" ><spring:message code="newspaper.showPublished" /></a>
			</li>
			<security:authorize access="hasRole('USER')">
				<li>
					<a id="showOpened" href="javascript:show('opened')" ><spring:message code="newspaper.showOpened" /></a>
				</li>
			</security:authorize>
		</ul>
	</div>
</jstl:if>

<br>
<div id='published' style='display:block;'>
	<display:table pagesize="5" class="displaytag" keepStatus="false"
		name="newspapers" requestURI="${requestURI }" id="row">
	
	
		<!-- Attributes -->
	
		<acme:column code="newspaper.title" path="title" />
		
		<fmt:formatNumber var="priceFormat" type="number" minFractionDigits="1" maxFractionDigits="2" value="${row.price}" />
		<acme:columnOut code="newspaper.price" path="${priceFormat}"/>
	
		<jstl:choose>
			<jstl:when test="${row.publicationDate != null }">
				<fmt:formatDate value="${row.publicationDate.time}" type="date" pattern="dd/MM/yyyy" var ="formatedPublicationDate"/>
				<acme:columnOut code="newspaper.moment" path="${formatedPublicationDate}" />
			</jstl:when>
			<jstl:otherwise>
				<spring:message code="newspaper.noPublishedYet" var="noPublishedYet" />
				<acme:columnOut code="newspaper.moment" path="${noPublishedYet}" />
			</jstl:otherwise>
		</jstl:choose>
		
		<spring:message code="newspaper.open" var="open" />
		<spring:message code="newspaper.close" var="close" />
		<spring:message code="newspaper.published" var="published" />
		<spring:message code="newspaper.noPublished" var="noPublished" />
		<spring:message code="newspaper.status" var="status" />
		<jstl:choose>
			<jstl:when test="${row.status == 'PUBLISHED'}">
				<acme:columnOut code="newspaper.status" path="${published}" />
			</jstl:when>
			<jstl:when test="${row.status == 'OPEN'}">
				<acme:columnOut code="newspaper.status" path="${noPublished} - ${open}" />
			</jstl:when>
			<jstl:when test="${row.status == 'CLOSE'}">
				<acme:columnOut code="newspaper.status" path="${noPublished} - ${close}" />
			</jstl:when>
		</jstl:choose>
		
		<spring:message code="newspaper.private" var="makePrivate"/>
		<display:column title="${makePrivate}" sortable="true">
			<jstl:if test="${row.makePrivate == true}">
					<span class="glyphicon glyphicon-ok"></span>
			</jstl:if>
			<jstl:if test="${row.makePrivate == false}">
					<span class="glyphicon glyphicon-remove"></span>
			</jstl:if>
		</display:column>
		
		<jstl:if test="${isListingCreated == false}">
			<acme:columnOut code="newspaper.publisher"
				path="${row.publisher.name } ${row.publisher.surname }" />
		</jstl:if>
	
		<acme:columnButton url="newspaper/display.do?newspaperId=${row.id}"
			code="newspaper.display" />
			
		<acme:columnButton url="article/list.do?newspaperId=${row.id}"
			code="newspaper.seeArticles" />
			
		<security:authorize access="hasRole('USER')">
			<display:column>
				<jstl:if test="${row.publisher == principal}">
					<jstl:if test="${row.status != 'PUBLISHED'}">
						<acme:button url="newspaper/user/edit.do?newspaperId=${row.id}"
							code="newspaper.edit" />
					</jstl:if>
				</jstl:if>
			</display:column>
			
			<display:column>
				<jstl:if test="${row.publisher == principal}">
					<jstl:if test="${row.status != 'PUBLISHED'}">
						<acme:button url="newspaper/user/publish.do?newspaperId=${row.id}"
							code="newspaper.publish" />
					</jstl:if>
				</jstl:if>
			</display:column>
			
			<display:column>
				<jstl:if test="${row.publisher == principal}">
					<jstl:if test="${row.status == 'OPEN'}">
						<acme:button url="article/user/create.do?newspaperId=${row.id}"
							code="newspaper.writeArticle" />
					</jstl:if>
				</jstl:if>
			</display:column>
			
			<display:column>
				<jstl:if test="${addButton == true && row.status == 'PUBLISHED' && !volumeNewspapers.contains(row)}">
					<acme:button url="volume/user/add-newspaper.do?newspaperId=${row.id}&volumeId=${volume.id}" code="newspaper.volume.add"/>
				</jstl:if>
				
				<jstl:if test="${addButton == true && row.status == 'PUBLISHED' && volumeNewspapers.contains(row)}">
					<acme:button url="volume/user/remove-newspaper.do?newspaperId=${row.id}&volumeId=${volume.id}" code="newspaper.volume.remove"/>
				</jstl:if>
			</display:column>
			
		</security:authorize>
		
		<security:authorize access="hasRole('AGENT')">
		
			<display:column>

				<acme:button url="advertisement/agent/create.do?newspaperId=${row.id}" code="newspaper.advertisement.create" />

			</display:column>
			
		</security:authorize>
	
	</display:table>
</div>

<div id='opened' style='display:none;'>
	<security:authorize access="hasRole('USER')">
		<jstl:if test="${isListingCreated == false}">
			<jstl:choose>
				<jstl:when test="${!empty newspapersOpened}">
					<display:table pagesize="5" class="displaytag" keepStatus="true"
						name="newspapersOpened" requestURI="${requestURI }" id="row">
					
					
						<!-- Attributes -->
					
						<acme:column code="newspaper.title" path="title" />
						
						<fmt:formatNumber var="priceFormat" type="number" minFractionDigits="1" maxFractionDigits="2" value="${row.price}" />
						<acme:columnOut code="newspaper.price" path="${priceFormat}"/>
					
						<jstl:choose>
							<jstl:when test="${row.publicationDate != null }">
								<fmt:formatDate value="${row.publicationDate.time}" type="date" pattern="dd/MM/yyyy" var ="formatedPublicationDate"/>
								<acme:columnOut code="newspaper.moment" path="${formatedPublicationDate}" />
							</jstl:when>
							<jstl:otherwise>
								<spring:message code="newspaper.noPublishedYet" var="noPublishedYet" />
								<acme:columnOut code="newspaper.moment" path="${noPublishedYet}" />
							</jstl:otherwise>
						</jstl:choose>
						
						<spring:message code="newspaper.open" var="open" />
						<spring:message code="newspaper.close" var="close" />
						<spring:message code="newspaper.published" var="published" />
						<spring:message code="newspaper.noPublished" var="noPublished" />
						<spring:message code="newspaper.status" var="status" />
						<jstl:choose>
							<jstl:when test="${row.status == 'PUBLISHED'}">
								<acme:columnOut code="newspaper.status" path="${published}" />
							</jstl:when>
							<jstl:when test="${row.status == 'OPEN'}">
								<acme:columnOut code="newspaper.status" path="${noPublished} - ${open}" />
							</jstl:when>
							<jstl:when test="${row.status == 'CLOSE'}">
								<acme:columnOut code="newspaper.status" path="${noPublished} - ${close}" />
							</jstl:when>
						</jstl:choose>
					
						<acme:columnOut code="newspaper.publisher"
							path="${row.publisher.name } ${row.publisher.surname }" />
					
						<acme:columnButton url="newspaper/display.do?newspaperId=${row.id}"
							code="newspaper.display" />
							
						<acme:columnButton url="article/list.do?newspaperId=${row.id}"
							code="newspaper.seeArticles" />
							
						<acme:columnButton url="article/user/create.do?newspaperId=${row.id}"
							code="newspaper.writeArticle" />
					
					</display:table>
				</jstl:when>
				<jstl:otherwise>
					<spring:message code="newspaper.noOpen" var="noOpen" />
					<h4><jstl:out value="${noOpen }" /></h4>
				</jstl:otherwise>
			</jstl:choose>
		</jstl:if>
	</security:authorize>
</div>
<br/>
<br/>

<jstl:if test="${backButton==true }">
	<acme:button url="volume/list.do" code="newspaper.volume.back"/>
</jstl:if>


<script type="text/javascript">

	function show(contents){
		
		if (contents == "published"){
			document.getElementById('showPublished').style.color = '#7CAFB7';
			document.getElementById('published').style.display = 'block';
			document.getElementById('showOpened').style.color = 'white';
			document.getElementById('opened').style.display = 'none';
		} else if (contents == "opened") {
			document.getElementById('showOpened').style.color = '#7CAFB7';
			document.getElementById('opened').style.display = 'block';
			document.getElementById('showPublished').style.color = 'white';
			document.getElementById('published').style.display = 'none';
		} 
	}

</script>
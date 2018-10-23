<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<security:authorize access="isAuthenticated()">
	<security:authentication property="principal.username" var="user" />
</security:authorize>

<display:table pagesize="5" class="displaytag" keepStatus="false"
	name="volumes" requestURI="${requestURI }" id="row">

	<acme:column code="volume.title" path="title" />

	<acme:column code="volume.description" path="description" />

	<acme:column code="volume.year" path="year" />

	<acme:columnButton
		url="newspaper/listVolumeNewspapers.do?volumeId=${row.id}"
		code="volume.newspapers" />

	<security:authorize access="hasRole('USER')">
		<jstl:if test="${row.publisher.userAccount.username == user}">
			<acme:columnButton
				url="newspaper/user/list-to-add.do?volumeId=${row.id }"
				code="volume.newspaper.add" />
		</jstl:if>
	</security:authorize>

	<security:authorize access="hasRole('CUSTOMER')">
		<jstl:if test="${notSubscribed==true }">
			<acme:columnButton
				url="volume-subscription/customer/create.do?volumeId=${row.id }"
				code="subscription.create" />
		</jstl:if>
	</security:authorize>


</display:table>
<br />
<br />

<security:authorize access="hasRole('USER')">
	<acme:button url="volume/user/create.do" code="volume.list.create" />
</security:authorize>
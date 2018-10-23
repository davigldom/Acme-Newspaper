<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<display:table pagesize="5" class="displaytag" keepStatus="false"
	name="subscriptions" requestURI="${requestURI}" id="row">


	<!-- Attributes -->

	<acme:column code="subscription.ccnumber" path="creditCardNumber" />
	
	<acme:column code="subscription.newspaper" path="newspaper.title" />

	<acme:columnButton url="newspaper/customer/subscription/unsubscribe.do?subscriptionId=${row.id}"
		code="subscription.delete" />

</display:table>

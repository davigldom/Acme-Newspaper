<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<form:form action="newspaper/customer/subscription/subscribe.do" modelAttribute="subscription" method = "post">

	<form:hidden path="id"/>
	<form:hidden path="version"/>
	
	<form:hidden path="newspaper"/>
	
	<acme:textbox
	code="subscription.ccnumber"
	path="creditCardNumber" />
	
	<acme:textbox
	code="subscription.ccmonth"
	path="expirationMonth" />
	
	<acme:textbox
	code="subscription.ccyear"
	path="expirationYear" />

	<acme:textbox
	code="subscription.ccscode"
	path="securityCode" />
	
	<acme:submit
	code="subscription.save"
	name="save" />

	<acme:button url="newspaper/list.do" code="subscription.cancel"/>
	
</form:form>
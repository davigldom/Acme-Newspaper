<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<form:form action="advertisement/agent/save.do" modelAttribute="advertisement" method = "post">

	<form:hidden path="id"/>
	<form:hidden path="version"/>
	
	<input type="hidden" name="newspaperId" value="${newspaperId }">

	
	<acme:textbox
	code="advertisement.title"
	path="title" />
	
		<acme:textbox
	code="advertisement.banner"
	path="banner" />
	
		<acme:textbox
	code="advertisement.targetPage"
	path="targetPage" />
	
		<acme:textbox
	code="advertisement.ccnumber"
	path="creditCardNumber" />
	
	<acme:textbox
	code="advertisement.ccmonth"
	path="expirationMonth" />
	
	<acme:textbox
	code="advertisement.ccyear"
	path="expirationYear" />

	<acme:textbox
	code="advertisement.ccscode"
	path="securityNumber" />
	
	<acme:submit
	code="advertisement.save"
	name="save" />
	

	<acme:button url="welcome/index.do" code="advertisement.cancel"/>
	
</form:form>
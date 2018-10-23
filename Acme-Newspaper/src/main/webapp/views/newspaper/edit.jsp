<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<form:form action="newspaper/user/edit.do" modelAttribute="newspaper" method = "post">

	<form:hidden path="id"/>
	<form:hidden path="version"/>

	
	<acme:textbox
	code="newspaper.title"
	path="title" />
	
	<acme:textarea
	code="newspaper.description"
	path="description" />
	
	<acme:textbox
	code="newspaper.picture"
	path="picture" />
	
	<spring:message code="newspaper.open" var="open" />
	<spring:message code="newspaper.close" var="close" />
	<spring:message code="newspaper.noPublished" var="noPublished" />
	<jstl:if test="${newspaper.id != 0}">
		<form:label path="status">
			<spring:message code="newspaper.status" />
		</form:label>
		<form:errors cssClass="error" path="status" />
		<br/>
		<form:select path="status">
			<jstl:if test="${newspaper.status == 'CLOSE'}">
				<form:option selected="true" value="CLOSE">
					<jstl:out value="${noPublished } - ${close }" />
				</form:option>
				<form:option value="OPEN">
					<jstl:out value="${noPublished } - ${open }" />
				</form:option>
			</jstl:if>
			<jstl:if test="${newspaper.status == 'OPEN'}">
				<form:option selected="true" value="OPEN">
					<jstl:out value="${noPublished } - ${open }" />
				</form:option>
				<form:option value="CLOSE">
					<jstl:out value="${noPublished } - ${close }" />
				</form:option>
			</jstl:if>
		</form:select>
		<br><br>
	</jstl:if>
	
	<form:label path="makePrivate" >
		<spring:message code="newspaper.private" />
	</form:label>
	<form:checkbox path="makePrivate" id="private" onchange="javascript: toggleSubmit()"/>
	<form:errors path="makePrivate" cssClass="error" />
	<br><br>
	
	<form:label path="price" >
		<spring:message code="newspaper.price" />
	</form:label>
	<form:errors path="price" cssClass="error" />
	<br>
	<spring:message code="newspaper.priceExplanation" />
	<br>
	<form:input path="price" id="price" placeholder="10.00" size="50" disabled="disabled" onload="javascript: toggleSubmit()" />
	<br><br>
	
	<acme:submit
	code="newspaper.save"
	name="save" />

	<acme:button url="newspaper/user/list.do" code="newspaper.cancel"/>
	
	<script type="text/javascript">
		function toggleSubmit() {
			var privated = document.getElementById("private");
			if(privated.checked){
				document.getElementById("price").disabled = false;
			} else{
				document.getElementById("price").disabled = true;
			}
		}
		
		window.onload=toggleSubmit;
		
	</script>
	
</form:form>
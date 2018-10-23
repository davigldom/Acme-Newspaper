<%--
 * index.jsp
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

 <jstl:choose>
 <jstl:when test="${pageContext.response.locale.language=='en'}">
				<b> Usage of cookies</b>
 
<p>We use cookies to improve users' browsing experience. <p>
<i> <b> Cookies used </b> </i>

	<p>JSSESSIONID - Used by the system to keep your session status</p>
	<p>language - Used by the system to know what language the webpage should be in</p>

 </jstl:when>
 <jstl:otherwise>

				<b> Uso de cookies </b>
 
<p>Utilizamos cookies para mejorar la experience del usuario en la página. <p>
<i> <b> Cookies usadas </b> </i>

	<p>JSSESSIONID - Usada por el sistema para guardar el estado de la sesión</p>
	<p>language - Usada por el sistema para saber en que lenguaje debe estar la página</p>

 </jstl:otherwise>
 </jstl:choose>



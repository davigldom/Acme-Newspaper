<%--
 * header.jsp
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>

<div id="banner">
	<a title="Acme-Newspaper Co., Inc." href="welcome/index.do"><img
		src="images/banner.jpg" alt="Acme-Newspaper Co., Inc." height="140px" /></a>
</div>

<div>
	<ul id="jMenu">

		<li><a class="fNiv"><span
				class="glyphicon glyphicon-list-alt"></span> <spring:message
					code="master.page.newspaper" /></a> <!-- 		</span><i class="fas fa-bookmark"></i><span class="fa fa-newspaper"></span> -->

			<ul>
				<li class="arrow"></li>

				<li><a href="volume/list.do"><spring:message
							code="master.page.volume.list" /></a></li>


				<security:authorize
					access="isAnonymous() or hasRole('CUSTOMER') or hasRole('ADMIN') or hasRole('AGENT')">
					<li><a href="newspaper/list.do"><spring:message
								code="master.page.newspaper.listPublished" /></a></li>
				</security:authorize>

				<security:authorize access="hasRole('AGENT')">
					<li><a href="newspaper/agent/list-advertisements.do"><spring:message
								code="master.page.newspaper.advertisement" /></a></li>

					<li><a href="newspaper/agent/list-non-advertisements.do"><spring:message
								code="master.page.newspaper.nonAdvertisement" /></a></li>

				</security:authorize>

				<security:authorize access="hasRole('USER')">
					<li><a href="newspaper/list.do"><spring:message
								code="master.page.newspaper.list" /></a></li>
					<li><a href="newspaper/user/create.do"><spring:message
								code="master.page.newspaper.create" /></a></li>
					<li><a href="newspaper/user/list.do"><spring:message
								code="master.page.newspaper.listCreated" /></a></li>

					<li><a href="volume/user/create.do"><spring:message
								code="master.page.volume.create" /></a></li>
					<li><a href="volume/user/list.do"><spring:message
								code="master.page.volume.user.create" /></a></li>
				</security:authorize>
			</ul></li>

		<security:authorize access="hasRole('CUSTOMER')">
			<li><a class="fNiv"><span
					class="glyphicon glyphicon-asterisk"></span> <spring:message
						code="master.page.volume" /></a>
				<ul>
					<li class="arrow"></li>
					<li><a href="volume/customer/list-subscribed.do"><spring:message
								code="master.page.volume.list.subscribed" /></a></li>
					<li><a href="volume/customer/list-not-subscribed.do"><spring:message
								code="master.page.volume.list.not.subscribed" /></a></li>

				</ul></li>

		</security:authorize>

		<security:authorize access="hasRole('USER')">
			<li><a class="fNiv"><span
					class="glyphicon glyphicon-text-size"></span> <spring:message
						code="master.page.article" /></a>
				<ul>
					<li class="arrow"></li>
					<li><a href="article/user/list.do"><spring:message
								code="master.page.article.listCreated" /></a></li>
				</ul></li>
		</security:authorize>

		<li><a class="fNiv"><span class="glyphicon glyphicon-user"></span>
				<spring:message code="master.page.users" /></a>
			<ul>
				<li class="arrow"></li>
				<li><a href="actor/list.do"> <spring:message
							code="master.page.users.list" /></a></li>
				<security:authorize access="hasRole('USER')">
					<li><a href="actor/user/list-following.do"><spring:message
								code="master.page.users.list.following" /></a></li>
					<li><a href="actor/user/list-followers.do"><spring:message
								code="master.page.users.list.followers" /></a></li>
				</security:authorize>
			</ul></li>

		<security:authorize access="hasRole('USER')">
			<li><a class="fNiv" href="chirp/user/list.do"><span
					class="glyphicon glyphicon-comment"></span> <spring:message
						code="master.page.users.chirp.display" /></a></li>
		</security:authorize>

		<security:authorize access="hasRole('CUSTOMER')">
			<li><a class="fNiv"><span class="glyphicon glyphicon-bell"></span>
					<spring:message code="master.page.subscription" /></a>
				<ul>
					<li class="arrow"></li>
					<li><a href="newspaper/customer/subscription/list.do"><spring:message
								code="master.page.subscription.list" /></a></li>
			
					<li><a href="volume-subscription/customer/list.do"><spring:message
								code="master.page.volume.subscription.list" /></a></li>
				</ul></li>
		</security:authorize>

		<security:authorize access="hasRole('ADMIN')">
			<li><a class="fNiv"><span class="glyphicon glyphicon-wrench"></span>
					<spring:message code="master.page.system.config" /></a>
				<ul>
					<li class="arrow"></li>
					<li><a href="system-config/admin/edit.do"><spring:message
								code="master.page.system.config.edit" /></a></li>
				</ul></li>

			<li><a class="fNiv"><span class="glyphicon glyphicon-wrench"></span>
					<spring:message code="master.page.advertisement" /></a>
				<ul>
					<li class="arrow"></li>
					<li><a href="advertisement/admin/list.do"><spring:message
								code="master.page.advertisement.list" /></a></li>
				</ul></li>

			<li><a class="fNiv"><span
					class="glyphicon glyphicon-th-list"></span> <spring:message
						code="master.page.dashboard" /></a>
				<ul>
					<li class="arrow"></li>
					<li><a href="actor/admin/dashboard.do"><spring:message
								code="master.page.dashboard.display" /></a></li>
				</ul></li>

			<li><a class="fNiv"><span
					class="glyphicon glyphicon-asterisk"></span> <spring:message
						code="master.page.tabooWords" /></a>
				<ul>
					<li class="arrow"></li>
					<li><a href="newspaper/admin/list.do"><spring:message
								code="master.page.newspaper.list" /></a></li>
					<li><a href="article/admin/list.do"><spring:message
								code="master.page.article.list" /></a></li>
					<li><a href="chirp/admin/list.do"><spring:message
								code="master.page.chirp.list" /></a></li>
					<li><a href="advertisement/admin/list-taboo.do"><spring:message
								code="master.page.advertisement.list" /></a></li>

				</ul></li>

		</security:authorize>


		<%-- 		<security:authorize access="hasRole('AGENT')"> --%>
		<%-- 			<li><a class="fNiv" href="advertisement/agent/create.do"><span class="	glyphicon glyphicon-exclamation-sign"></span> <spring:message --%>
		<%-- 						code="master.page.advertisement.create" /></a> --%>
		<!-- 		</li> -->

		<%-- 		</security:authorize> --%>

		<security:authorize access="isAnonymous()">
			<li id="logout"><a class="fNiv" href="security/login.do"><span
					class="glyphicon glyphicon-log-in"></span> <spring:message
						code="master.page.login" /></a></li>
		</security:authorize>

		<security:authorize access="isAuthenticated()">
			<li><a class="fNiv"><span class="glyphicon glyphicon-cog"></span>
					<spring:message code="master.page.profile" /> (<security:authentication
						property="principal.username" />) </a>
				<ul>
					<li class="arrow"></li>
					<li><a href="folder/list.do?folderId=0"><spring:message
								code="master.page.messages" /></a></li>


					<li><a href="actor/display-principal.do"><spring:message
								code="master.page.display" /></a></li>
				</ul></li>

			<li id="logout"><a class="fNiv" href="j_spring_security_logout">
					<span class="glyphicon glyphicon-off"></span> <spring:message
						code="master.page.logout" />
			</a></li>
		</security:authorize>
	</ul>
</div>

<div id="languages">
	<a href="?language=en">en</a> | <a href="?language=es">es</a>
</div>


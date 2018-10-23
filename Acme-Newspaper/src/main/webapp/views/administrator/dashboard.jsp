<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><!-- Added -->
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>


<p><b><spring:message code="administrator.dashboard.getAverageFollowpsPerArticle"/> :</b> <jstl:out value="${getAverageFollowpsPerArticle}"></jstl:out></p><br>
<p><b><spring:message code="administrator.dashboard.getFollowupsPerArticleUpToWeek"/> :</b> <jstl:out value="${getFollowupsPerArticleUpToWeek}"/></p><br>
<p><b><spring:message code="administrator.dashboard.getFollowupsPerArticleUpToTwoWeeks"/> :</b> <jstl:out value="${getFollowupsPerArticleUpToTwoWeeks}"></jstl:out></p><br>
<p><b><spring:message code="administrator.dashboard.getAverageChirpsPerUser"/> :</b> <jstl:out value="${getAverageChirpsPerUser}"></jstl:out></p><br>
<p><b><spring:message code="administrator.dashboard.getStandardDeviationChirpsPerUser"/> :</b> <jstl:out value="${getStandardDeviationChirpsPerUser}"></jstl:out></p><br>
<p><b><spring:message code="administrator.dashboard.getRatioUsersMoreChirpsThan75Percent"/> :</b> <jstl:out value="${getRatioUsersMoreChirpsThan75Percent}"></jstl:out></p><br>
<p><b><spring:message code="administrator.dashboard.getRatioArticlesCreatedByUser"/> :</b> <jstl:out value="${getRatioArticlesCreatedByUser}"></jstl:out></p><br>
<p><b><spring:message code="administrator.dashboard.getAverageArticlesPerUser"/> :</b> <jstl:out value="${getAverageArticlesPerUser}"></jstl:out></p><br>
<p><b><spring:message code="administrator.dashboard.getStandardDeviationArticlesPerUser"/> :</b> <jstl:out value="${getStandardDeviationArticlesPerUser}"></jstl:out></p><br>
<p><b><spring:message code="administrator.dashboard.getAverageArticlesPerNewspaper"/> :</b> <jstl:out value="${getAverageArticlesPerNewspaper}"></jstl:out></p><br>
<p><b><spring:message code="administrator.dashboard.getStandardDeviationArticlesPerNewspaper"/> :</b> <jstl:out value="${getStandardDeviationArticlesPerNewspaper}"></jstl:out></p><br>
<p><b><spring:message code="administrator.dashboard.getRatioNewspaperCreatedByUser"/> :</b> <jstl:out value="${getRatioNewspaperCreatedByUser}"></jstl:out></p><br>
<p><b><spring:message code="administrator.dashboard.getAverageNewspaperPerUser"/> :</b> <jstl:out value="${getAverageNewspaperPerUser}"></jstl:out></p><br>
<p><b><spring:message code="administrator.dashboard.getStandardDeviationNewspaperPerUser"/> :</b> <jstl:out value="${getStandardDeviationNewspaperPerUser}"></jstl:out></p><br>
<p><b><spring:message code="administrator.dashboard.getRatioPublicVsPrivate"/> :</b> <jstl:out value="${getRatioPublicVsPrivate}"></jstl:out></p><br>
<p><b><spring:message code="administrator.dashboard.getAverageArticlesPerPrivateNewspaper"/> :</b> <jstl:out value="${getAverageArticlesPerPrivateNewspaper}"></jstl:out></p><br>
<p><b><spring:message code="administrator.dashboard.getAverageArticlesPerPublicNewspaper"/> :</b> <jstl:out value="${getAverageArticlesPerPublicNewspaper}"></jstl:out></p><br>
<p><b><spring:message code="administrator.dashboard.getRatioSubscribersPrivateVsTotal"/> :</b> <jstl:out value="${getRatioSubscribersPrivateVsTotal}"></jstl:out></p><br>
<p><b><spring:message code="administrator.dashboard.getAvgRatioPrivateVsPublicPerPublisher"/> :</b> <jstl:out value="${getAvgRatioPrivateVsPublicPerPublisher}"></jstl:out></p><br>
<p><b><spring:message code="administrator.dashboard.findRatioNewspapersAdvertisementsVSNoAdvertisements"/> :</b> <jstl:out value="${findRatioNewspapersAdvertisementsVSNoAdvertisements}"></jstl:out></p><br>
<p><b><spring:message code="administrator.dashboard.findRatioAdvertisementsTaboo"/> :</b> <jstl:out value="${findRatioAdvertisementsTaboo}"></jstl:out></p><br>
<p><b><spring:message code="administrator.dashboard.getAverageNewspapersPerVolume"/> :</b> <jstl:out value="${getAverageNewspapersPerVolume}"></jstl:out></p><br>
<p><b><spring:message code="administrator.dashboard.getRatioSubscriptionsNewspaperVsVolume"/> :</b> <jstl:out value="${getRatioSubscriptionsNewspaperVsVolume}"></jstl:out></p><br>
<br>


<p><b><spring:message code="administrator.dashboard.getNewspapersTenPercentMoreArticles"/> :</b></p>
<jstl:forEach var="newspaper" items="${getNewspapersTenPercentMoreArticles}">
		<jstl:out value="${newspaper.title}"/><br>
	</jstl:forEach>
<br>


<p><b><spring:message code="administrator.dashboard.getNewspapersTenPercentFewerArticles"/> :</b></p>
<jstl:forEach var="newspaper2" items="${getNewspapersTenPercentFewerArticles}">
		<jstl:out value="${newspaper2.title}"/><br>
	</jstl:forEach>
<br>


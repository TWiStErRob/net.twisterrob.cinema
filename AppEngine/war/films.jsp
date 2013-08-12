<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
	<title>Film listing</title>
	<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.8.0/jquery.min.js"></script>
	<link rel="stylesheet" type="style/css" href="/stylesheets/global.css" />
</head>
<body>
	<h1>Page title</h1>
	<div id="xxx-container">
		<c:choose>
			<c:when test="${user ne null}">
				<c:set var="greeting" value="${user.nickname}: ${user.authDomain}\\${user.email} (${user.userId}/${user.federatedIdentity})" />
				Hello ${fn:escapeXml(greeting)}! <a href="${url}">log out</a>.
			</c:when>
			<c:otherwise>
				<a href="${url}">log in</a>
			</c:otherwise>
		</c:choose>
		<c:forEach var="view" items="${result}">
			Film #${view.edi}: ${view.seen ? "seen" : "to see" }
		</c:forEach>
	</div>


	<div id="gsonDebugWrapper">
		<script>$(document).ready(function() { $("#gsonDebugToggler").click(function () {$("#gsonDebug").toggle();}).click(); });</script>
		<button id="gsonDebugToggler">Toggle GSON debug info</button>
		<pre id="gsonDebug"><%=new com.google.gson.GsonBuilder() //
					.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ") //
					.registerTypeAdapter(java.util.GregorianCalendar.class,
							new com.twister.cineworld.model.json.data.CalendarTypeConverter()) //
					.setPrettyPrinting() //
					.create() //
					.toJson(request.getAttribute("result"))%>
		</pre>
	</div>
</body>
</html>
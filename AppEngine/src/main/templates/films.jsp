<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<!DOCTYPE html>
<html>
<head>
	<title>Film listing</title>
	<link rel="stylesheet" type="style/css" href="/static/styles/global.css" />
	<link rel="stylesheet" type="text/css" href="/static/styles/libs/jquery/jquery.tablesorter-2.0.5b.css" />
	<script type="text/javascript" src="/static/scripts/libs/jquery/jquery-1.9.1.js"></script>
	<script type="text/javascript" src="/static/scripts/libs/jquery/jquery.tablesorter-2.0.5b.js"></script>
	<script type="text/javascript" src="/static/scripts/libs/jquery/jquery.tablesorter-2.0.5b.pager.js"></script>
</head>
<body>
	<h1>Page title</h1>
	<div id="xxx-container">
		<c:choose>
			<c:when test="${user ne null}">
				<c:set var="greeting" value="${user.nickName}: \${user.authDomain}\\${user.email} (${user.userId}/\${user.federatedIdentity})" />
				Hello ${fn:escapeXml(greeting)}! <a href="${url}">log out</a>.
			</c:when>
			<c:otherwise>
				<a href="${url}">log in</a>
			</c:otherwise>
		</c:choose>
		<table id="viewList" class="tablesorter">
			<thead>
				<tr><th>Edi</th><th>Title</th><th>Seen?</th></tr>
			</thead>
			<tbody>
			<c:forEach var="view" items="${views}">
				<tr><td>${view.film.edi}</td><td>${view.film.title}</td><td>${view.seen}</td></tr>
			</c:forEach>
			</tbody>
			<tfoot>
				<tr><td colspan="3">
					<div id="viewList-pager">
						<form>
							<img src="/static/images/jquery/tablesorter/first.png" class="first">
							<img src="/static/images/jquery/tablesorter/prev.png" class="prev">
							<input type="text" class="pagedisplay">
							<img src="/static/images/jquery/tablesorter/next.png" class="next">
							<img src="/static/images/jquery/tablesorter/last.png" class="last">
							<select class="pagesize">
								<option value="5">5</option>
								<option value="10" selected="selected">10</option>
								<option value="25">25</option>
								<option value="50">50</option>
								<option value="100">100</option>
								<option value="${fn:length(views)}">All</option>
							</select>
						</form>
					</div>
				</td></tr>
			</tfoot>
		</table>
	</div>


	<div id="gsonDebugWrapper">
		<script>$(document).ready(function() { $("#gsonDebugToggler").click(function () {$("#gsonDebug").toggle();}).click(); });</script>
		<button id="gsonDebugToggler">Toggle GSON debug info</button>
		<pre id="gsonDebug"><%
			com.google.gson.GsonBuilder gsonBuilder = new com.google.gson.GsonBuilder() //
					.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ") //
					.registerTypeAdapter(java.util.GregorianCalendar.class, new com.twister.cineworld.model.json.data.CalendarTypeConverter()) //
					.setPrettyPrinting();
			new com.google.gson.graph.GraphAdapterBuilder()
					.addType(com.twister.gapp.cinema.model.User.class)
					.addType(com.twister.gapp.cinema.model.Film.class)
					.addType(com.twister.gapp.cinema.model.View.class)
					.registerOn(gsonBuilder);
			String json = gsonBuilder.create() //
					.toJson(request.getAttribute("views"));
				out.println(json);
		%></pre>
	</div>
</body>
</html>
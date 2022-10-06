<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
	<title>Film listing</title>
	<link rel="stylesheet" type="style/css" href="/static/styles/global.css" />
	<link rel="stylesheet" type="text/css" href="/static/styles/libs/jquery/jquery.tablesorter-2.0.5b.css" />
	<script type="text/javascript" src="/static/scripts/libs/jquery/jquery-1.9.1.js"></script>
	<script type="text/javascript" src="/static/scripts/libs/jquery/jquery.tablesorter-2.0.5b.js"></script>
	<script type="text/javascript" src="/static/scripts/libs/jquery/jquery.tablesorter-2.0.5b.pager.js"></script>
	<script type="text/javascript" src="/static/scripts/libs/moment/moment-2.1.0.js"></script>
	<script>
		$.tablesorter.addParser({
			id: 'isoTime',
			is: function(s) {
				return false; // return false so this parser is not auto detected
			},
			format: function(s) { // format your data for normalization
				// 2013-08-26T23:48:31.750+01:00
				var m = moment(s);
				return m !== undefined && m !== null && m.isValid()? 0 + m : 0;
			},
			type : 'numeric'
		});
		$.tablesorter.addParser({
			id: 'filmStatus',
			is: function(s) {
				return false; // return false so this parser is not auto detected
			},
			format: function(s) { // format your data for normalization
				return s.toLowerCase()
					.replace(/^new$/, 1)
					.replace(/^existing$/, 2);
			},
			type : 'text'
		});
		$(document).ready(function() {
			$("#filmList")
			.tablesorter({
				widthFixed: true,
				sortList: [[3,1],[1,0]],
				headers: {
					4: { sorter:'filmStatus' },
					5: { sorter:'isoTime' },
					6: { sorter:'isoTime' }
				}
			})
			.tablesorterPager({
				container: $("#filmList-pager"),
				positionFixed: false
			});
		});
	</script>
</head>
<body>
	<!-- Header -->
	<c:choose>
		<c:when test="${user ne null}">
			<c:set var="greeting" value="${user.nickName}: \${user.authDomain}\\${user.email} (${user.userId}/\${user.federatedIdentity})" />
			Hello ${fn:escapeXml(greeting)}! <a href="${url}">log out</a>.
		</c:when>
		<c:otherwise>
			<a href="${url}">log in</a>
		</c:otherwise>
	</c:choose>

	<!-- Views -->
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

	<!-- Films -->
	<h1>Films</h1>
	<table id="filmList" class="tablesorter">
		<thead>
			<tr><th>Edi</th><th>Title</th><th>Runtime</th><th>Status</th><th>Created</th><th>Last update</th></tr>
		</thead>
		<tbody>
		<c:forEach var="filmGroup" items="${films}">
			<c:forEach var="film" items="${filmGroup.value}">
				<tr><td>${film.edi}</td><td>${film.title}</td><td>${film.runtime}</td><td>${filmGroup.key}</td><td>${film.created}</td><td>${film.lastUpdated}</td></tr>
			</c:forEach>
		</c:forEach>
		</tbody>
		<tfoot>
			<tr><td colspan="5">
				<div id="filmList-pager">
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
							<c:set var="filmCount" value="0" />
							<c:forEach var="filmGroup" items="${films}">
								<c:set var="filmCount" value="${filmCount + fn:length(filmGroup.value)}" />
							</c:forEach>
							<option value="${filmCount}">All</option>
						</select>
					</form>
				</div>
			</td></tr>
		</tfoot>
	</table>

	<div id="gsonDebugWrapper">
		<script>$(document).ready(function() { $("#gsonDebugToggler").click(function () {$("#gsonDebug").toggle();}).click(); });</script>
		<button id="gsonDebugToggler">Toggle GSON debug info</button>
		<pre id="gsonDebug"><%
			com.google.gson.GsonBuilder gsonBuilder = new com.google.gson.GsonBuilder() //
					.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ") //
					.registerTypeAdapter(java.util.GregorianCalendar.class, new com.twister.cineworld.model.json.data.CalendarTypeConverter()) //
					.setPrettyPrinting();
			new com.google.gson.graph.GraphAdapterBuilder()
					.addType(net.twisterrob.cinema.gapp.model.User.class)
					.addType(net.twisterrob.cinema.gapp.model.Film.class)
					.addType(net.twisterrob.cinema.gapp.model.View.class)
					.registerOn(gsonBuilder);
			String json = gsonBuilder.create() //
					.toJson(request.getAttribute("views"));
				out.println(json);
		%></pre>
	</div>
</body>
</html>
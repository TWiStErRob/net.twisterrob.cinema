<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
	<title>Maintenance</title>
	<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.8.0/jquery.min.js"></script>
	<link rel="stylesheet" type="style/css" href="/stylesheets/global.css" />
</head>
<body>
	<h1>Page title</h1>
	<div id="xxx-container">
		Hello ${hello}!
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
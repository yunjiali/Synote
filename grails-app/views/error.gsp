<html>
<head>
<title>Grails Runtime Exception</title>
<meta name="layout" content="main" />
<style type="text/css">
.message {
	border: 1px solid black;
	padding: 5px;
	background-color: #E9E9E9;
}

.stack {
	border: 1px solid black;
	padding: 5px;
	overflow: auto;
	height: 300px;
}

.snippet {
	padding: 5px;
	background-color: white;
	border: 1px solid black;
	margin: 3px;
	font-family: courier;
}

.title
{
	color:red;
}
</style>
</head>

<body>
<h1><strong class="title"><g:message code="org.synote.error.title" /></strong></h1>
<div></div>
<h2><g:message code="org.synote.error.detail.title" /></h2>

<div class="message"><strong>Error ${request.'javax.servlet.error.status_code'}:</strong>
${request.'javax.servlet.error.message'.encodeAsHTML()}<br />
<strong>Servlet:</strong> ${request.'javax.servlet.error.servlet_name'}<br />
<strong>URI:</strong> ${request.'javax.servlet.error.request_uri'}<br />
<g:if test="${exception}">
	<strong>Exception Message:</strong>
	${exception.message?.encodeAsHTML()}
	<br />
	<strong>Caused by:</strong>
	${exception.cause?.message?.encodeAsHTML()}
	<br />
	<strong>Class:</strong>
	${exception.className}
	<br />
	<strong>At Line:</strong> [${exception.lineNumber}] <br />
	<strong>Code Snippet:</strong>
	<br />
	<div class="snippet"><g:each var="cs"
		in="${exception.codeSnippet}">
		${cs?.encodeAsHTML()}<br />
	</g:each></div>
</g:if></div>
<g:if test="${exception}">
	<h2>Stack Trace</h2>
	<div class="stack"><g:each
		in="${exception.stackTraceLines}">
		${it.encodeAsHTML()}<br />
	</g:each></div>
</g:if>
</body>
</html>
<html>
<head>
<title><g:layoutTitle default="Grails" /></title>
<link rel="stylesheet" href="${resource(dir:'css/admin',file:'main.css')}" />
<link rel="shortcut icon"
	href="${resource(dir:'images',file:'synote_icon.ico')}" type="image/x-icon" />
<g:layoutHead />
<g:urlMappings/>
</head>
<body>
<div id="spinner" class="spinner" style="display: none;"><img
	src="${resource(dir:'images',file:'spinner.gif')}" alt="Spinner" /></div>
<div id="grailsLogo" class="logo"><a href="http://grails.org"><img
	src="${resource(dir:'images',file:'grails_logo.png')}" alt="Grails"
	border="0" /></a></div>
<g:render template="/common/message" />
<g:layoutBody />
</body>
</html>
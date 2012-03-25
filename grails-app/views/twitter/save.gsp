<html>
<head>
<title><g:message
	code="org.synote.integration.twitter.save.title" /></title>
<meta name="layout" content="main" />
</head>
<body>
<div class="nav"><span class="menuButton"><a class="home"
	href="${resource(dir: '')}" title="Go to Home">Home</a></span></div>
<div class="body">
<h1><g:message code="org.synote.integration.twitter.save.title" /></h1>
<g:render template="/common/message" model="[bean: tweetList]" /></div>
</body>
</html>

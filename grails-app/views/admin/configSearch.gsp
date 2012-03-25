<html>
<head>
<title>Search Settings</title>
<meta name="layout" content="admin" />
</head>
<body>
<div class="nav"></div>
<div class="body">
<h1>Search Configuration</h1>
<g:render template="/common/message" /> <g:form method="post"
	controller="admin">
	<div class="dialog">
	<ul>
		<li>Index all resources: Performs a bulk index of all searchable
		class instances in the database</li>
		<li>Unindex all resourecs: Un-indexes searchable class instances
		(removes them from the search index). Use with caution.</li>
	</ul>
	</div>
	<div class="buttons"><span class="button"><g:actionSubmit
		class="login" value="Index all resources" action="searchIndexAll"
		title="index all resources" /></span> <span class="button"><g:actionSubmit
		class="cancel" value="Unindex all resources" action="searchUnindexAll"
		title="unindex all resources"
		onclick="return confirm('Are you sure?');" /></span></div>
</g:form></div>
</body>
</html>


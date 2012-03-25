<html>
<head>
<title>Secret Operations for admin</title>
<meta name="layout" content="main" />
</head>
<body>
<div class="nav"></div>
<div class="body">
	<h1>Secret Operations</h1>
	<g:render template="/common/message" />
	<h2>Convert old transcript to WebVTT</h2>
	<g:link controller="admin" action="convertTranscriptResourceToWebVTTResource">Convert old transcript to WebVTT</g:link><br/>
	<g:link controller="admin" action="removeTranscriptResources">Remove old transcript resources</g:link><br/>
	<g:link controller="admin" action="convertTranscriptResourceToWebVTTResource" id="24939">Remove old transcript resources by id</g:link><br/>
	
	<h2>Add Slides</h2>
	<g:link controller="admin" action="addSlides" id="36513">Add TBL's TED talk slides</g:link>
	
	<h2>Create Sitemaps</h2>
	<g:link controller="admin" action="generateReplaySitemap">Generate Replay Sitemap</g:link><br/>
	<g:link controller="admin" action="generateResourcesSitemap">Generate Resources Sitemap</g:link><br/>
	<g:link controller="admin" action="generateAnnotationsSitemap">Generate Annotations Sitemap</g:link><br/>
	<g:link controller="admin" action="generateUsersSitemap">Generate Users Sitemap</g:link>
	
	<h2>Dumping RDF data</h2>
	<g:link controller="admin" action="dumpRDFToOneFile">Dumping RDF data into one file</g:link>
</div>
</body>
</html>

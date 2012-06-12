<html>
<head>
<title><g:message code="org.synote.resource.compound.multimediaResource.create.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
</head>
<body>
<div class="container">
	<div class="row">
		<div class="span2" id="user_nav_div">
			<g:render template="/common/userNav" model="['active':'recordings']"/>
		</div>
		<div class="span10" id="user_content_div">
			<h2><g:message code="org.synote.resource.compound.multimediaResource.create.title" /></h2>
			<br/>
			<br/>
			<div id="my_synote_content">
			
			<g:link class="user" action="createinet" title="internet">
				<img src="${resource(dir: 'images/skin', file: 'network_64.png')}" alt="internet"/><br />Internet
			</g:link>
			
			<g:link class="user" action="createyt" title="Youtube">
				<img src="${resource(dir: 'images/skin', file: 'youtube_64.png')}" alt="youtube"/><br />YouTube</g:link>
			<g:fileUploadEnabled>
			<g:link class="user" action="createlocal" title="Upload from local disk">
				<img src="${resource(dir: 'images/skin', file: 'harddisk_64.png')}" alt="upload"/><br />Local Disk</g:link>
			</g:fileUploadEnabled>
			<g:viascribeXmlUploadEnabled>
			<g:link class="user" action="createxml" title="Synchronised XML">
				<img src="${resource(dir: 'images/skin', file: 'xml_64.png')}" alt="synchronised xml"/><br />Synchronised XML</g:link>
			</div>
			</g:viascribeXmlUploadEnabled>
		</div>
	</div>
</div>
</body>
</html>

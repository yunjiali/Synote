<html>
<head>
<title><g:message code="org.synote.resource.compound.multimediaResource.createlocal.title" /></title>
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
			<h2><g:message code="org.synote.resource.compound.multimediaResource.createlocal.title" /></h2>
			<hr/>
			<div >
				<iframe style="border:0; min-height:300px;width:100%" src="http://localhost:8888/api/multimediaUpload?nexturl=http%3A%2F%2Flocalhost%3A8888"></iframe>
			</div>
		</div>
	</div>
</div>
</body>
</html>

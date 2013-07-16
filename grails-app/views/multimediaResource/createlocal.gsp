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
			<browser:choice>
				<browser:isMsie>
				<div id="create_warning_div" class="alert alert-error">
  					<strong>Sorry!</strong> Internet Explorer is not compatible with this function, please use Firefox, Google Chrome or Safari instead.
				</div>
				</browser:isMsie>
				<browser:otherwise>
				<div >
					<iframe style="border:0; min-height:300px;width:100%" src="${uploadMultimediaURL}"></iframe>
				</div>
				</browser:otherwise>
			</browser:choice>
		</div>
	</div>
</div>
</body>
</html>

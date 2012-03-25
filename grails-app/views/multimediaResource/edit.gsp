<%@page import="org.synote.resource.compound.MultimediaResource"%>
<%@page import="org.synote.permission.PermissionValue"%>
<html>
<head>
<title><g:message
	code="org.synote.resource.compound.multimediaResource.edit.title" /></title>
<meta name="layout" content="main" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform', file: 'uni-form.css')}" media="screen" charset="utf-8"/>
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform', file: 'default.uni-form.css')}" media="screen" charset="utf-8"/>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form.jquery.min.js')}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form-validation.jquery.min.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js/jquery', file: 'jquery.combobox.js')}"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$(".uniForm").uniform();
		$("input[type=text], input[type=password]").wijtextbox();
		$("#resourceEditForm_submit").button();
		$("#resourceEditForm_delete").button();
		$("#resourceEditForm_cancel").button();
		$(".combowrap select").combobox();
	});
</script>
</head>
<body>
<div class="span-24" id="user_nav">
	<g:render template="/common/userNav"/>
</div>
<div class="span-22 prepend-1 append-1" id="user_content">
	<h1>Edit Recording</h1>
	<g:render template="/common/message" model="[bean: multimediaResource]" />
	<g:form method="post" class="uniForm" action="update">
		<input type="hidden" name="id" value="${multimediaResource?.id}" />
		<div class="ctrlHolder inlineLabels">
			<label for="id">Id:</label>
			<span>${fieldValue(bean: multimediaResource, field: 'id')}</span>
		</div>
		<div class="ctrlHolder inlineLabels">
			<label for="title"><em>*</em>Title:</label>
			<input type="text" id="title" class="textInput medium required" name="title" id="title" value="${fieldValue(bean: multimediaResource, field: 'title')}" />
			<p class="formHint">Please enter the title of the recording</p>
		</div>
		<div class="ctrlHolder inlineLabels">
			<label for="url"><em>*</em>Url:</label>
			<input type="text" id="url" name="url" class="textInput medium required" value="${fieldValue(bean: multimediaResource, field: 'url')}" />
			<p class="formHint">Please enter the url of the recording</p>
		</div>
		<div class="ctrlHolder inlineLabels">
			<label for="perm">Public Permission:</label></td>
				<g:render template="/common/permission"
						model="[canPrivate:true, defaultPerm:multimediaResource.perm]" /></td>
		</div>
		<div class="prepend-top append-bottom">
			<input id="resourceEditForm_submit" type="submit" value="Save" />
			<g:link controller='multimediaResource' action='delete' onclick="return confirm('Are you sure?');" id="${multimediaResource?.id}" elementId="resourceEditForm_delete">Delete</g:link>
			<g:link controller='user' action='listResources' elementId="resourceEditForm_cancel">Cancel</g:link>
		</div>
	</g:form>
</div>
</body>
</html>

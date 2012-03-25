<html>
<head>
<title><g:message code="org.synote.user.group.edit.title" /></title>
<meta name="layout" content="main" />
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform', file: 'uni-form.css')}" media="screen" charset="utf-8"/>
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform', file: 'default.uni-form.css')}" media="screen" charset="utf-8"/>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form.jquery.min.js')}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form-validation.jquery.min.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js/jquery', file: 'jquery.combobox.js')}"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$(".uniForm").uniform();
		$("input[type=text], input[type=password]").wijtextbox();
		//$("input[type='checkbox']").wijcheckbox();
		$("#groupEditForm_submit").button();
		$("#groupEditForm_delete").button();
		$("#groupEditForm_cancel").button();
	});
</script>
</head>
<body>
<div class="span-24" id="user_nav">
	<g:render template="/common/userNav"/>
</div>
<div class="body">
<div class="span-24" id="user_content">
	<h1>Edit Group</h1>
	<g:render template="/common/message" model="[bean: userGroup]" /> 
	<g:form method="post" class="uniForm" action="update">
		<input type="hidden" name="id" value="${userGroup?.id}" />
		<div class="ctrlHolder inlineLabels">
			<label for="id">Id:</label>
			<span>${fieldValue(bean: userGroup, field: 'id')}</span>
		</div>
		<div class="ctrlHolder inlineLabels">	
			<label for="name"><em>*</em>Name:</label>
			<input type="text" class="textInput medium required" id="name" name="name" value="${fieldValue(bean: userGroup, field: 'name')}" />
		</div>
		<div class="ctrlHolder inlineLabels">
			<label for="shared">Shared:</label>
			<g:checkBox name="shared" id="shared" value="${userGroup?.shared}" title="Checked if it is shared" />
		</div>
		<div class="prepend-top append-bottom">
			<input id="groupEditForm_submit" type="submit" value="Save" />
			<g:link controller='userGroup' action='delete' onclick="return confirm('Are you sure?');" id="${userGroup?.id}" elementId="groupEditForm_delete">Delete Group</g:link>
			<g:link controller='user' action='listGroups' elementId="groupEditForm_cancel">Cancel</g:link>
		</div>
	</g:form>
</div>
</body>
</html>

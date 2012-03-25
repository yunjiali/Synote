<html>
<head>
<title><g:message code="org.synote.user.group.create.title" /></title>
<meta name="layout" content="main" />
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform', file: 'uni-form.css')}" media="screen" charset="utf-8"/>
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform', file: 'default.uni-form.css')}" media="screen" charset="utf-8"/>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form.jquery.min.js')}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form-validation.jquery.min.js')}"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$(".uniForm").uniform();
		$("input[type=text], input[type=password]").wijtextbox();
		$("#groupCreateForm_submit").button();
		$("#groupCreateForm_reset").button();
		$("#groupCreateForm_cancel").button();
	});
</script>
</head>
<body>
<div class="span-24" id="user_nav">
	<g:render template="/common/userNav"/>
</div>
<div class="span-22 prepend-1 append-1" id="user_content">
	<h1>Create Group</h1>
	<g:render template="/common/message" model="[bean: multimediaResource]" />
	<g:form method="post" class="uniForm" action="save" controller="userGroup">
		<div class="ctrlHolder inlineLabels">
			<label for="name">Name:</label>
			<input type="text" class="textInput medium required" id="name" name="name" value="${fieldValue(bean: userGroup, field: 'name')}" />
			<p class="formHint">Please enter the name of the group</p>
		</div>
		<div class="ctrlHolder inlineLabels">
			<label for="shared">Shared:</label>
			<g:checkBox name="shared" value="${userGroup?.shared}" title="Checked if it is shared"></g:checkBox>
		</div>
		<g:isAdminLoggedIn>
		<div class="ctrlHolder inlineLabels">
			<label for="ownerUserName">Owner:</label>
			<input type="text" id="ownerUserName" name="ownerUserName"
						value="${ownerUserName}" />
		</div>
		</g:isAdminLoggedIn>
		<div class="prepend-top append-bottom">
			<input id="groupCreateForm_submit" type="submit" value="Create" />
			<input id="groupCreateForm_reset" type="reset" value="Reset" />
			<g:link controller='user' action='listGroups' elementId="groupCreateForm_cancel">Cancel</g:link>
		</div>
	</g:form>
</div>

</body>
</html>

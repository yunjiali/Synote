<html>
<head>
<title><g:message code="org.synote.user.editUserProfile.title" /></title>
<meta name="layout" content="main" />
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform', file: 'uni-form.css')}" media="screen" charset="utf-8"/>
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform', file: 'default.uni-form.css')}" media="screen" charset="utf-8"/>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form.jquery.min.js')}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form-validation.jquery.min.js')}"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$(".uniForm").uniform();
		$("input[type=text], input[type=password]").wijtextbox();
		$("#userProfileEditForm_submit").button();
		$("#userProfileEditForm_cancel").button();
	});
</script>
</head>
<body>
<div class="span-24" id="user_nav">
	<g:render template="/common/userNav"/>
</div>
<div class="span-22 prepend-1 append-1" id="user_content">
	<h1><g:message code="org.synote.user.editUserProfile.title" /></h1>
	<g:render template="/common/message" model="[bean: user]" />
	<g:form method="post" class="uniForm" action="handleEditUserProfile">
		<input type="hidden" name="id" value="${user?.id}" />
		<div class="ctrlHolder inlineLabels">
			<label for="id">Id:</label>
			<span>${fieldValue(bean: user, field: 'id')}</span>
		</div>
		<div class="ctrlHolder inlineLabels">
			<label for="userName">User Name:</label>
			<span>${fieldValue(bean: user, field: 'userName')}</span>
		</div>
		<div class="ctrlHolder inlineLabels">
			<label for="firstName"><em>*</em>First Name:</label>
			<input type="text" id="firstName" class="textInput medium required" name="firstName" value="${fieldValue(bean: user, field: 'firstName')}" />
		</div>
		<div class="ctrlHolder inlineLabels">
			<label for="lastName"><em>*</em>Last Name:</label>
			<input type="text" class="textInput medium required" id="lastName" name="lastName" value="${fieldValue(bean: user, field: 'lastName')}" />
		</div>
		<div class="ctrlHolder inlineLabels">
			<label for="email"><em>*</em>Email:</label>
			<input type="text" class="textInput medium required" id="email" name="email" value="${fieldValue(bean: user, field: 'email')}" />
		</div>
		<div class="prepend-top append-bottom">
			<input id="userProfileEditForm_submit" type="submit" value="Save" />
			<g:link controller='user' action='showUserProfile' elementId="userProfileEditForm_cancel">Cancel</g:link>
		</div>
	</g:form>
</div>
</body>
</html>

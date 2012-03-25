<html>
<head>
<title><g:message code="org.synote.user.changePassword.title" /></title>
<meta name="layout" content="main" />
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform', file: 'uni-form.css')}" media="screen" charset="utf-8"/>
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform', file: 'default.uni-form.css')}" media="screen" charset="utf-8"/>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form.jquery.min.js')}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form-validation.jquery.min.js')}"></script>
<script type="text/javascript">
$(document).ready(function(){
	$(".uniForm").uniform();
	$("input[type=password]").wijtextbox();
	$("#password_submit").button();
	$("#password_cancel").button();
});
</script>
</head>
<body>
<div class="span-24" id="user_nav">
	<g:render template="/common/userNav"/>
</div>
<div class="span-24" id="user_content">
<h1><g:message code="org.synote.user.changePassword.title" /></h1>
<g:render template="/common/message" /> 
<g:form controller="user" action="handleChangePassword" method="post" class="uniForm">
	<div class="ctrlHolder inlineLabels">
		<label for="oldPassword"><em>*</em>Your old password:</label>
		<input type="password" id="oldPassword" class="textInput medium required" name="oldPassword"/>
		<p class="formHint">Please enter your old password</p>
	</div>
	<div class="ctrlHolder inlineLabels">
		<label for="newPassword"><em>*</em>New password:</label>
		<input type="password" id="oldPassword" class="textInput medium required" name="newPassword"/>
		<p class="formHint">Please enter your new password</p>
	</div>
	<div class="ctrlHolder inlineLabels">
		<label for="confirmNewPassword"><em>*</em>Confirm your password:</label>
		<input type="password" id="confirmNewPassword" class="textInput medium required" name="confirmNewPassword"/>
		<p class="formHint">Please enter your password again</p>
	</div>
	<div style="padding:4px 0;">
		<input class="left" id="password_submit" type="button" value="Submit"/>
		<input class="left" id="password_cancel" type="reset" value="Cancel" />
	</div>
</g:form>
</div>
</body>
</html>

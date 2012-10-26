<html>
<head>
<title><g:message code="org.synote.user.changePassword.title" /></title>
<meta name="layout" content="main" />
<script type="text/javascript">
$(document).ready(function(){
	
});
</script>
</head>
<body>
<div class="container">
	<div class="row">
		<div class="span2" id="user_nav_div">
			<g:render template="/common/userNav" model="['active':'user_profile']"/>
		</div>
		<div class="span10" id="user_content_div">
			<h2 class="heading-inline"><g:message code="org.synote.user.changePassword.title" /></h2>
			<hr/>
			<g:render template="/common/message" model="[bean: user]" />
			<div id="error_msg_div"></div>
			<div class="well">
				<g:form method="post" class="form-horizontal" controller="user" action="handleChangePassword">
			      	<div class="control-group">
			      		<label class="control-label" for="oldPassword"><em>*</em>Your old password:</label>
				      	<div class="controls">
							<input type="password" id="oldPassword" class="required" name="oldPassword"/>
							<span class="help-block">Please enter your old password</span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="newPassword"><em>*</em>New password:</label>
						<div class="controls">
							<input type="password" id="newPassword" class="textInput medium required" name="newPassword"/>
							<span class="help-block">Please enter your new password</span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="confirmNewPassword"><em>*</em>Confirm password:</label>
						<div class="controls">
							<input type="password" id="confirmNewPassword" class="textInput medium required" name="confirmNewPassword"/>
							<span class="help-block">Please enter your new password again</span>
						</div>
					</div>
					<div class="form-actions">
						<div class="pull-left">
							<input class="btn btn-primary" type="submit" value="Save" />
							<g:link class="btn" controller='user' action='showUserProfile' elementId="userProfileEditForm_cancel">Cancel</g:link>
						</div>
					</div>
				</g:form>
			</div>
		</div>
	</div>
</div>
</body>
</html>

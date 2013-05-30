<html>
<head>
<title><g:message code="org.synote.user.editUserProfile.title" /></title>
<meta name="layout" content="main" />
<script type="text/javascript" src="${resource(dir:'js',file:"util.js")}"></script>
<script type="text/javascript" src="${resource(dir: 'js/jquery', file: 'jquery.validate-1.9.1.min.js')}"></script>
<script type="text/javascript">
	
</script>
</head>
<body>
<div class="container">
	<div class="row">
		<div class="span2" id="user_nav_div">
			<g:render template="/common/userNav" model="['active':'user_profile']"/>
		</div>
		<div class="span10" id="user_content_div">
			<h2 class="heading-inline"><g:message code="org.synote.user.editUserProfile.title" /></h2>
			<hr/>
			<g:render template="/common/message" model="[bean: user]" />
			<div id="error_msg_div"></div>
			<div class="well">
				<g:form method="post" class="form-horizontal" action="handleEditUserProfile">
					<input type="hidden" name="id" value="${user?.id}" />
			      	<div class="control-group">
			      		<label class="control-label" for="id">Id:</label>
				      	<div class="controls">
							${fieldValue(bean: user, field: 'id')}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="userName">User Name:</label>
						<div class="controls">
							${fieldValue(bean: user, field: 'userName')}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="firstName"><em>*</em>First Name:</label>
						<div class="controls">
							<input type="text" id="firstName" class="required" name="firstName" value="${fieldValue(bean: user, field: 'firstName')}" />
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="lastName"><em>*</em>Last Name:</label>
						<div class="controls">
							<input type="text" class="required" id="lastName" name="lastName" value="${fieldValue(bean: user, field: 'lastName')}" />
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="email"><em>*</em>Email:</label>
						<div class="controls">
							<input type="text" class="required" id="email" name="email" value="${fieldValue(bean: user, field: 'email')}" />
						</div>
					</div>
					<div class="form-actions">
						<div class="pull-left">
							<input class="btn btn-primary" id="userProfileEditForm_submit" type="submit" value="Save" />
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

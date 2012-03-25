<html>
<head>
<title>Reset password</title>
<meta name="layout" content="admin" />
</head>
<body>
<div class="nav"></div>
<div class="body">
<h1>Reset user password</h1>
<g:render template="/common/message" /> <g:form method="post"
	controller="admin">
	<div class="dialog">
	<table>
		<tbody>
			<tr class="prop">
				<td class="name"><label for="userName">Select user name</label></td>
				<td class="value"><g:select id="userName" name="userName"
					from="${userList}" value="${user?.userName}" /></td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="newPassword">New password:</label></td>
				<td class="value"><input type="password" id="newPassword"
					name="newPassword" /></td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="confirmNewPassword">Confirm
				new password:</label></td>
				<td class="value"><input type="password"
					id="confirmNewPassword" name="confirmNewPassword" /></td>
			</tr>
		</tbody>
	</table>
	</div>
	<div class="buttons"><span class="button"><g:actionSubmit
		class="login" value="Confirm" action="handleResetPassword"
		title="Confirm" onclick="return confirm('Are you sure?');" /></span> <span
		class="button"><g:actionSubmit class="cancel" value="Cancel"
		action="cancel" title="Cancel" /></span></div>
</g:form></div>
</body>
</html>

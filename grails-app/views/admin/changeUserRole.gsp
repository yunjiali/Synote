<html>
<head>
<title>Change User Role</title>
<meta name="layout" content="admin" />
</head>
<body>
<div class="nav"></div>
<div class="body">
<h1>Change User Role</h1>
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
				<td class="name"><label for="newPassword">Select Role</label></td>
				<td class="value"><g:render template="/common/role" /></td>
			</tr>
		</tbody>
	</table>
	</div>
	<div class="buttons"><span class="button"><g:actionSubmit
		class="login" value="Confirm" action="handleChangeUserRole"
		title="Confirm" onclick="return confirm('Are you sure?');" /></span> <span
		class="button"><g:actionSubmit class="cancel" value="Cancel"
		action="cancel" title="Cancel" /></span></div>
</g:form></div>
</body>
</html>

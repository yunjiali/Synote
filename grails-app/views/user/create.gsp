<%@ page import="org.synote.user.User"%>
<html>
<head>
<meta name="layout" content="main" />
<title><g:message code="org.synote.user.create.title" /></title>
</head>
<body>
<div class="nav"><span class="menuButton"><a class="home"
	href="${createLink(uri: '/')}">Home</a></span> <span class="menuButton"><g:link
	class="list" action="list">
	<g:message code="default.list.label" args="[entityName]" />
</g:link></span></div>
<div class="body">
<h1><g:message code="org.synote.user.create.title" /></h1>
<g:render template="/common/message" model="[bean: userInstance]" />
<div class="errors"><g:renderErrors bean="${userInstance}"
	as="list" /></div>
<g:form action="save" method="post">
	<div class="dialog">
	<table>
		<tbody>

			<tr class="prop">
				<td valign="top" class="name"><label for="userName"><g:message
					code="user.userName.label" default="User Name" /></label></td>
				<td valign="top"
					class="value ${hasErrors(bean: userInstance, field: 'userName', 'errors')}">
				<g:textField name="userName" value="${userInstance?.userName}" /></td>
			</tr>

			<tr class="prop">
				<td valign="top" class="name"><label for="password"><g:message
					code="user.password.label" default="Password" /></label></td>
				<td valign="top"
					class="value ${hasErrors(bean: userInstance, field: 'password', 'errors')}">
				<g:textField name="password" value="${userInstance?.password}" /></td>
			</tr>

			<tr class="prop">
				<td valign="top" class="name"><label for="firstName"><g:message
					code="user.firstName.label" default="First Name" /></label></td>
				<td valign="top"
					class="value ${hasErrors(bean: userInstance, field: 'firstName', 'errors')}">
				<g:textField name="firstName" value="${userInstance?.firstName}" />
				</td>
			</tr>

			<tr class="prop">
				<td valign="top" class="name"><label for="lastName"><g:message
					code="user.lastName.label" default="Last Name" /></label></td>
				<td valign="top"
					class="value ${hasErrors(bean: userInstance, field: 'lastName', 'errors')}">
				<g:textField name="lastName" value="${userInstance?.lastName}" /></td>
			</tr>

			<tr class="prop">
				<td valign="top" class="name"><label for="email"><g:message
					code="user.email.label" default="Email" /></label></td>
				<td valign="top"
					class="value ${hasErrors(bean: userInstance, field: 'email', 'errors')}">
				<g:textField name="email" value="${userInstance?.email}" /></td>
			</tr>

			<tr class="prop">
				<td valign="top" class="name"><label for="enabled"><g:message
					code="user.enabled.label" default="Enabled" /></label></td>
				<td valign="top"
					class="value ${hasErrors(bean: userInstance, field: 'enabled', 'errors')}">
				<g:checkBox name="enabled" value="${userInstance?.enabled}" /></td>
			</tr>

			<tr class="prop">
				<td valign="top" class="name"><label for="dateCreated"><g:message
					code="user.dateCreated.label" default="Date Created" /></label></td>
				<td valign="top"
					class="value ${hasErrors(bean: userInstance, field: 'dateCreated', 'errors')}">
				<g:datePicker name="dateCreated" precision="day"
					value="${userInstance?.dateCreated}" /></td>
			</tr>

			<tr class="prop">
				<td valign="top" class="name"><label for="lastUpdated"><g:message
					code="user.lastUpdated.label" default="Last Updated" /></label></td>
				<td valign="top"
					class="value ${hasErrors(bean: userInstance, field: 'lastUpdated', 'errors')}">
				<g:datePicker name="lastUpdated" precision="day"
					value="${userInstance?.lastUpdated}" /></td>
			</tr>

			<tr class="prop">
				<td valign="top" class="name"><label for="lastLogin"><g:message
					code="user.lastLogin.label" default="Last Login" /></label></td>
				<td valign="top"
					class="value ${hasErrors(bean: userInstance, field: 'lastLogin', 'errors')}">
				<g:datePicker name="lastLogin" precision="day"
					value="${userInstance?.lastLogin}" /></td>
			</tr>

			<tr class="prop">
				<td valign="top" class="name"><label for="confirmedPassword"><g:message
					code="user.confirmedPassword.label" default="Confirmed Password" /></label>
				</td>
				<td valign="top"
					class="value ${hasErrors(bean: userInstance, field: 'confirmedPassword', 'errors')}">
				<g:textField name="confirmedPassword"
					value="${userInstance?.confirmedPassword}" /></td>
			</tr>

		</tbody>
	</table>
	</div>
	<div class="buttons"><span class="button"><g:submitButton
		name="create" class="save"
		value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
	</div>
</g:form></div>
</body>
</html>

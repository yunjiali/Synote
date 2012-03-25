
<%@ page import="org.synote.user.User"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title><g:message code="org.synote.user.edit.title" /></title>
</head>
<body>
<div class="nav"><span class="menuButton"><a class="home"
	href="${createLink(uri: '/')}">Home</a></span> <span class="menuButton"><g:link
	class="list" action="list">
	<g:message code="default.list.label" args="[entityName]" />
</g:link></span> <span class="menuButton"><g:link class="create" action="create">
	<g:message code="default.new.label" args="[entityName]" />
</g:link></span></div>
<div class="body">
<h1><g:message code="org.synote.user.edit.title" /></h1>
<g:render template="/common/message" model="[bean: userInstance]" /> <g:hasErrors
	bean="${userInstance}">
	<div class="errors"><g:renderErrors bean="${userInstance}"
		as="list" /></div>
</g:hasErrors> <g:form method="post">
	<g:hiddenField name="id" value="${userInstance?.id}" />
	<g:hiddenField name="version" value="${userInstance?.version}" />
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
				<td valign="top" class="name"><label for="authorities"><g:message
					code="user.authorities.label" default="Authorities" /></label></td>
				<td valign="top"
					class="value ${hasErrors(bean: userInstance, field: 'authorities', 'errors')}">

				</td>
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
		</tbody>
	</table>
	</div>
	<div class="buttons"><span class="button"><g:actionSubmit
		class="save" action="update"
		value="${message(code: 'default.button.update.label', default: 'Update')}" /></span>
	<span class="button"><g:actionSubmit class="delete"
		action="delete"
		value="${message(code: 'default.button.delete.label', default: 'Delete')}"
		onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
	</div>
</g:form></div>
</body>
</html>

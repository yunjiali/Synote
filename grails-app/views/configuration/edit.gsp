
<%@ page import="org.synote.config.Configuration"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="admin" />
<g:set var="entityName"
	value="${message(code: 'configuration.label', default: 'Configuration')}" />
<title><g:message code="default.edit.label" args="[entityName]" /></title>
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
<h1><g:message code="default.edit.label" args="[entityName]" /></h1>
<g:if test="${flash.message}">
	<div class="message">
	${flash.message}
	</div>
</g:if> <g:hasErrors bean="${configurationInstance}">
	<div class="errors"><g:renderErrors
		bean="${configurationInstance}" as="list" /></div>
</g:hasErrors> <g:form method="post">
	<g:hiddenField name="id" value="${configurationInstance?.id}" />
	<g:hiddenField name="version" value="${configurationInstance?.version}" />
	<div class="dialog">
	<table>
		<tbody>

			<tr class="prop">
				<td valign="top" class="name"><label for="name"><g:message
					code="configuration.name.label" default="Name" /></label></td>
				<td valign="top"
					class="value ${hasErrors(bean: configurationInstance, field: 'name', 'errors')}">
				<g:textField name="name" value="${configurationInstance?.name}" />
				</td>
			</tr>

			<tr class="prop">
				<td valign="top" class="name"><label for="val"><g:message
					code="configuration.val.label" default="Val" /></label></td>
				<td valign="top"
					class="value ${hasErrors(bean: configurationInstance, field: 'val', 'errors')}">
				<g:textField name="val" value="${configurationInstance?.val}" /></td>
			</tr>

			<tr class="prop">
				<td valign="top" class="name"><label for="dateCreated"><g:message
					code="configuration.dateCreated.label" default="Date Created" /></label></td>
				<td valign="top"
					class="value ${hasErrors(bean: configurationInstance, field: 'dateCreated', 'errors')}">
				<g:datePicker name="dateCreated" precision="day"
					value="${configurationInstance?.dateCreated}" /></td>
			</tr>

			<tr class="prop">
				<td valign="top" class="name"><label for="lastUpdated"><g:message
					code="configuration.lastUpdated.label" default="Last Updated" /></label></td>
				<td valign="top"
					class="value ${hasErrors(bean: configurationInstance, field: 'lastUpdated', 'errors')}">
				<g:datePicker name="lastUpdated" precision="day"
					value="${configurationInstance?.lastUpdated}" /></td>
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

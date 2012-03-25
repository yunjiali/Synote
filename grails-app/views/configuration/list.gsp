
<%@ page import="org.synote.config.Configuration"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="admin" />
<title><g:message
	code="org.synote.config.configuration.list.title" /></title>
</head>
<body>
<div class="nav"><span class="menuButton"><a class="home"
	href="${createLink(uri: '/')}">Home</a></span> <span class="menuButton"><g:link
	class="create" action="create">
	<g:message code="default.new.label" args="[entityName]" />
</g:link></span></div>
<div class="body">
<h1><g:message code="org.synote.config.configuration.list.title" /></h1>
<g:if test="${flash.message}">
	<div class="message">
	${flash.message}
	</div>
</g:if>
<div class="list">
<table>
	<thead>
		<tr>

			<g:sortableColumn property="id"
				title="${message(code: 'configuration.id.label', default: 'Id')}" />

			<g:sortableColumn property="name"
				title="${message(code: 'configuration.name.label', default: 'Name')}" />

			<g:sortableColumn property="val"
				title="${message(code: 'configuration.val.label', default: 'Val')}" />

			<g:sortableColumn property="dateCreated"
				title="${message(code: 'configuration.dateCreated.label', default: 'Date Created')}" />

			<g:sortableColumn property="lastUpdated"
				title="${message(code: 'configuration.lastUpdated.label', default: 'Last Updated')}" />

		</tr>
	</thead>
	<tbody>
		<g:each in="${configurationInstanceList}" status="i"
			var="configurationInstance">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

				<td><g:link action="show" id="${configurationInstance.id}">
					${fieldValue(bean: configurationInstance, field: "id")}
				</g:link></td>

				<td>
				${fieldValue(bean: configurationInstance, field: "name")}
				</td>

				<td>
				${fieldValue(bean: configurationInstance, field: "val")}
				</td>

				<td><g:formatDate date="${configurationInstance.dateCreated}" /></td>

				<td><g:formatDate date="${configurationInstance.lastUpdated}" /></td>

			</tr>
		</g:each>
	</tbody>
</table>
</div>
<div class="paginateButtons"><g:paginate
	total="${configurationInstanceTotal}" /></div>
</div>
</body>
</html>

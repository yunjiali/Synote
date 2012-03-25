<%@ page import="org.synote.user.User"%>
<html>
<head>
<meta name="layout" content="main" />
<title><g:message code="org.synote.user.list.title" /></title>
</head>
<body>
<div class="nav"><span class="menuButton"><a class="home"
	href="${createLink(uri: '/')}">Home</a></span> <g:ifAllGranted
	role="ROLE_ADMIN">
	<span class="menuButton"><g:link class="create" action="create">Create User</g:link></span>
</g:ifAllGranted></div>
<div class="body">
<h1><g:message code="org.synote.user.list.title" /></h1>
<g:render template="/common/message" model="[bean:userInstance]" />
<div class="list">
<table>
	<thead>
		<tr>

			<g:sortableColumn property="id"
				title="${message(code: 'user.id.label', default: 'Id')}" />

			<g:sortableColumn property="userName"
				title="${message(code: 'user.userName.label', default: 'User Name')}" />

			<g:sortableColumn property="firstName"
				title="${message(code: 'user.firstName.label', default: 'First Name')}" />

			<g:sortableColumn property="lastName"
				title="${message(code: 'user.lastName.label', default: 'Last Name')}" />
		</tr>
	</thead>
	<tbody>
		<g:each in="${userInstanceList}" status="i" var="userInstance">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

				<td>
					${fieldValue(bean: userInstance, field: "id")}
				</td>

				<td>
				${fieldValue(bean: userInstance, field: "userName")}
				</td>

				<td>
				${fieldValue(bean: userInstance, field: "firstName")}
				</td>

				<td>
				${fieldValue(bean: userInstance, field: "lastName")}
				</td>

			</tr>
		</g:each>
	</tbody>
</table>
</div>
<div class="paginateButtons"><g:paginate
	total="${userInstanceTotal}" /></div>
</div>
</body>
</html>

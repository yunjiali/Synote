<%@ page import="org.synote.user.User"%>
<%@ page import="org.synote.user.group.UserGroup"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title>${userInstance.firstName+" "+userInstacne.lastName+"' Synote profile"}</title>
</head>
<body>
<g:isLoggedIn>
<div class="span-24" id="user_nav">
	<g:render template="/common/userNav"/>
</div>
</g:isLoggedIn>
<div class="span-24" id="user_content">
	<div class="span-22 prepend-1 append-1" itemscope="itemscope" itemtype="http://schema.org/Table">
	<h1><g:message code="org.synote.user.showUserProfile.title" /></h1>
	<g:render template="/common/message" model="[bean: userInstance]" />
	<table class="ui-widget" itemscope="itemscope" itemtype="http://schema.org/Person" >
		<tr>
		<th class="ui-widget-header">Property</th>
		<th class="ui-widget-header">Value</th>
		</tr>
		<tbody>
			<tr class="ui-widget-content prop">
				<td valign="top" class="name"><g:message code="user.id.label"
					default="Id" /></td>
	
				<td valign="top" class="value">
				${fieldValue(bean: userInstance, field: "id")}
				</td>
	
			</tr>
	
			<tr class="ui-widget-content prop">
				<td valign="top" class="name"><g:message
					code="user.userName.label" default="User Name" /></td>
	
				<td valign="top" class="value" itemprop="name">
				${fieldValue(bean: userInstance, field: "userName")}
				</td>
	
			</tr>
	
			<tr class="ui-widget-content prop">
				<td valign="top" class="name"><g:message
					code="user.firstName.label" default="First Name" /></td>
	
				<td valign="top" class="value" itemprop="givenName">
				${fieldValue(bean: userInstance, field: "firstName")}
				</td>
	
			</tr>
	
			<tr class="ui-widget-content prop">
				<td valign="top" class="name"><g:message
					code="user.lastName.label" default="Last Name" /></td>
	
				<td valign="top" class="value" itemprop="familyName">
				${fieldValue(bean: userInstance, field: "lastName")}
				</td>
	
			</tr>
	
			<tr class="ui-widget-content prop">
				<td valign="top" class="name"><g:message code="user.email.label"
					default="Email" /></td>
	
				<td valign="top" class="value" itemprop="email">
				${fieldValue(bean: userInstance, field: "email")}
				</td>
	
			</tr>
		</tbody>
	</table>
</div>
</div>
</body>
</html>

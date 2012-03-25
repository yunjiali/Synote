
<%@ page import="org.synote.user.User"%>
<%@ page import="org.synote.user.group.UserGroup"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title><g:message code="org.synote.user.showUserProfile.title" /></title>
<script type="text/javascript">
	$(document).ready(function(){
		$("#user_profile_edit").button({
			icons:{
				primary:"ui-icon-pencil"
			}
		});
		$("#user_profile_change_password").button({
			icons:{
				primary:"ui-icon-key"
			}
		});
	});
</script>
</head>
<body>
<div class="span-24" id="user_nav">
	<g:render template="/common/userNav"/>
</div>
<div class="span-24" id="user_content">
	<div class="span-22 prepend-1 append-1">
	<h1><g:message code="org.synote.user.showUserProfile.title" /></h1>
	<g:render template="/common/message" model="[bean: userInstance]" />
	<table class="ui-widget">
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
	
				<td valign="top" class="value">
				${fieldValue(bean: userInstance, field: "userName")}
				</td>
	
			</tr>
	
			<tr class="ui-widget-content prop">
				<td valign="top" class="name"><g:message
					code="user.firstName.label" default="First Name" /></td>
	
				<td valign="top" class="value">
				${fieldValue(bean: userInstance, field: "firstName")}
				</td>
	
			</tr>
	
			<tr class="ui-widget-content prop">
				<td valign="top" class="name"><g:message
					code="user.lastName.label" default="Last Name" /></td>
	
				<td valign="top" class="value">
				${fieldValue(bean: userInstance, field: "lastName")}
				</td>
	
			</tr>
	
			<tr class="ui-widget-content prop">
				<td valign="top" class="name"><g:message code="user.email.label"
					default="Email" /></td>
	
				<td valign="top" class="value">
				${fieldValue(bean: userInstance, field: "email")}
				</td>
	
			</tr>
	
			<tr class="ui-widget-content prop">
				<td valign="top" class="name"><g:message
					code="user.enabled.label" default="Enabled" /></td>
	
				<td valign="top" class="value"><g:formatBoolean
					boolean="${userInstance?.enabled}" /></td>
	
			</tr>
	
			<tr class="ui-widget-content prop">
				<td valign="top" class="name"><g:message
					code="user.dateCreated.label" default="Date Created" /></td>
	
				<td valign="top" class="value"><g:formatDate
					date="${userInstance?.dateCreated}" /></td>
	
			</tr>
	  
			<tr class="ui-widget-content prop">
				<td valign="top" class="name"><g:message
					code="user.groups.label" default="Groups" /></td>
				<td valign="top" style="text-align: left;" class="value">
					<g:each in="${userInstance?.groups}" var="g">
					<span class="group"><g:link controller="userGroup" action="show" id="${g?.id}" class="edit">
							${g?.encodeAsHTML()}
						</g:link></span>
						<br/>
					</g:each>
				</td>
	
			</tr>
	
			<tr class="ui-widget-content prop">
				<td valign="top" class="name"><g:message
					code="user.lastUpdated.label" default="Last Updated" /></td>
	
				<td valign="top" class="value"><g:formatDate
					date="${userInstance?.lastUpdated}" /></td>
	
			</tr>
	
			<tr class="ui-widget-content prop">
				<td valign="top" class="name"><g:message
					code="user.lastLogin.label" default="Last Login" /></td>
	
				<td valign="top" class="value"><g:formatDate
					date="${userInstance?.lastLogin}" /></td>
	
			</tr>
		</tbody>
	</table>
	<div class="prepend-top append-bottom">
		<g:link controller='user' action='editUserProfile' elementId="user_profile_edit">Edit your profile</g:link>
		<g:allowRegistering>
			<g:link controller='user' action='changePassword' elementId="user_profile_change_password">Change password</g:link>
		</g:allowRegistering>
	</div>
</div>
</div>
</body>
</html>

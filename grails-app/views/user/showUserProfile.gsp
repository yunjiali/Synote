
<%@ page import="org.synote.user.User"%>
<%@ page import="org.synote.user.group.UserGroup"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title><g:message code="org.synote.user.showUserProfile.title" /></title>
</head>
<body>
<div class="container">
	<div class="row">
		<div class="span2" id="user_nav_div">
			<g:isLoggedIn>
				<g:render template="/common/userNav" model="['active':'user_profile']"/>
			</g:isLoggedIn>
		</div>
		<div class="span10" itemscope="itemscope" itemtype="http://schema.org/Table">
			<h1>${userInstance.userName}'s Profile</h1>
			<g:render template="/common/message" model="[bean: userInstance]" />
			
			<dl class="dl-horizontal well" itemscope="itemscope" itemtype="http://schema.org/Person">
				<dt><g:message code="user.userName.label" default="User Name" /></dt>
				<dd>${fieldValue(bean: userInstance, field: "userName")}</dd>
				<dt><g:message code="user.firstName.label" default="First Name" /></dt>
				<dd itemprop="givenname">${fieldValue(bean: userInstance, field: "firstName")}</dd>
				<dt><g:message code="user.lastName.label" default="Last Name" /></td>
				<dd itemprop="familyName">${fieldValue(bean: userInstance, field: "lastName")}</dd>
				<dt><g:message code="user.email.label" default="Email" /></dt>
				<dd itemprop="email">${fieldValue(bean: userInstance, field: "email")}</dd>
				
				<dt><g:message code="user.enabled.label" default="Enabled" /></dt>
				<dd><g:formatBoolean boolean="${userInstance?.enabled}" /></dd>
				<dt><g:message code="user.dateCreated.label" default="Date Created" /></dt>
				<dd valign="top" class="value"><g:formatDate date="${userInstance?.dateCreated}" /></dd>
				<dt><g:message code="user.groups.label" default="Groups" /></dt>
				<dd valign="top" style="text-align: left;" class="value">
					<g:if test="${userInstance?.groups?.size() > 0 }">
						<g:each in="${userInstance?.groups}" var="g">
						<span><g:link controller="userGroup" action="show" id="${g?.id}">
								${g?.encodeAsHTML()}
							</g:link></span>
							<br/>
						</g:each>
					</g:if>
					<g:else>
						No groups
					</g:else>
				</dd>
	
				<dt><g:message code="user.lastUpdated.label" default="Last Updated" /></dd>
				<dd><g:formatDate date="${userInstance?.lastUpdated}" /></dt>
				<dt><g:message code="user.lastLogin.label" default="Last Login" /></dd>
				<dd><g:formatDate date="${userInstance?.lastLogin}" /></dt>
			</dl>
			<div class="row">
				<div class="span6 offset1">
					<g:link class="btn" controller='user' action='editUserProfile' elementId="user_profile_edit">
					<i class="icon-pencil"></i>Edit your profile</g:link>
					<g:allowRegistering>
						<g:link class="btn btn-warning" controller='user' action='changePassword' elementId="user_profile_change_password">
						<i class="icon-wrench"></i>Change password</g:link>
					</g:allowRegistering>
				</div>
			</div>
		</div>
	</div>
</div>
</body>
</html>

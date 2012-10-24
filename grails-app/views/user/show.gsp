<%@ page import="org.synote.user.User"%>
<%@ page import="org.synote.user.group.UserGroup"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title>${userInstance.firstName+" "+userInstance.lastName+"' Synote profile"}</title>
</head>
<body>
<div class="container">
	<div class="row">
		<div class="span2" id="user_nav_div">
			<syn:isLoggedIn>
				<g:render template="/common/userNav" model="['active':'user_profile']"/>
			</syn:isLoggedIn>
		</div>
		<div class="span10" itemscope="itemscope" itemtype="http://schema.org/Table">
			<h1><g:message code="org.synote.user.showUserProfile.title" /></h1>
			<g:render template="/common/message" model="[bean: userInstance]" />
			
			<dl itemscope="itemscope" itemtype="http://schema.org/Person">
				<dt><g:message code="user.userName.label" default="User Name" /></dt>
				<dd>${fieldValue(bean: userInstance, field: "userName")}</dd>
				<dt><g:message code="user.firstName.label" default="First Name" /></dt>
				<dd itemprop="givenname">${fieldValue(bean: userInstance, field: "firstName")}</dd>
				<dt><g:message code="user.lastName.label" default="Last Name" /></td>
				<dd itemprop="familyName">${fieldValue(bean: userInstance, field: "lastName")}</dd>
				<dt><g:message code="user.email.label" default="Email" /></dt>
				<dd itemprop="email">${fieldValue(bean: userInstance, field: "email")}</dd>
			</dl>
		</div>
	</div>
</div>
</body>
</html>

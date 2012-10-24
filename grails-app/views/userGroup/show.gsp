<html>
<head>
<title><g:message code="org.synote.user.group.show.title" /></title>
<meta name="layout" content="main" />
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform', file: 'uni-form.css')}" media="screen" charset="utf-8"/>
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform', file: 'default.uni-form.css')}" media="screen" charset="utf-8"/>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form.jquery.min.js')}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form-validation.jquery.min.js')}"></script>
<script type="text/javascript">
	$(document).ready(function(){
		//if button exists, then init
		if($("#user_group_edit"))
		{
			$("#user_group_edit").button({
				icons:{
					primary:"ui-icon-pencil"
				}
			});
		}
		if($("#user_group_join"))
		{
			$("#user_group_join").button({
				icons:{
					primary:"ui-icon-plusthick"
				}
			});
		}
		if($("#user_group_remove"))
		{
			$("#user_group_remove").button({
				icons:{
					primary:"ui-icon-closethick"
				}
			});
		}
	});
</script>
</head>
<body>
<syn:isLoggedIn>
<div class="span-24" id="user_nav">
	<g:render template="/common/userNav"/>
</div>
</syn:isLoggedIn>
<div class="span-24" id="user_content">
	<div class="span-22 prepend-1 append-1">
	<h1>Show Group</h1>
	<g:render template="/common/message" /> 
	<table class="ui-widget">
		<tr>
			<th class="ui-widget-header">Property</th>
			<th class="ui-widget-header">Value</th>
		</tr>
		<tbody>
			<tr class="ui-widget-content prop">
				<td class="name">Id:</td>
				<td class="value">
				${fieldValue(bean: userGroup, field: 'id')}
				</td>
			</tr>
			<tr class="ui-widget-content prop">
				<td class="name">Name:</td>
				<td class="value">
				${fieldValue(bean: userGroup, field: 'name')}
				</td>
			</tr>
			<tr class="ui-widget-content prop">
				<td class="name">Shared:</td>
				<td class="value">
				${fieldValue(bean: userGroup, field: 'shared')}
				</td>
			</tr>
			<tr class="ui-widget-content prop">
				<td class="name">Owner:</td>
				<td class="value">
				${fieldValue(bean: userGroup, field: 'owner')}
				</td>
			</tr>
			<tr class="ui-widget-content prop">
				<td class="name" style="vertical-align: top">Members:</td>
				<td class="value">
					<span>${memberCount}&nbsp;members</span>
					<g:if test="${isMember || isOwnerOrAdmin}">
						<br/>
						<g:each status="index" var="member" in="${userGroup.members.sort {it.user.userName}}">
							${member.user.userName}
							<g:if test="${index < userGroup.members.size() - 1}">, </g:if>
						</g:each>
						<br/>
						<span class="group"><g:link action="editMember" controller="userGroup" id="${userGroup.id}" 
												class="editMember" title="Add member">Edit&nbsp;Member</g:link></span>
					</g:if>
				</td>
			</tr>
			<tr class="ui-widget-content prop">
				<td class="name" style="vertical-align: top">Group Recording:</td>
				<td class="value">
					<span>${recordingCount}&nbsp;recordings</span>
					<g:if test="${isMember || isOwnerOrAdmin}">
						<g:each status="index" var="permission" in="${userGroup.permissions.sort {it.resource.title}}">
							<br/>
							<g:link controller="multimediaResource" action="show" id="${permission.resource?.id}">
								${permission.resource.title} (${permission.perm})
							</g:link>
							<g:if test="${isOwnerOrAdmin}">
								<a href="#">delete</a>
							</g:if>
						</g:each>
						<br/>
						<span class="group"><g:link action="editPermission" controller="userGroup" id="${userGroup.id}" 
							class="editRecording" title="Add member">Edit&nbsp;Recording</g:link></span>
					</g:if>
				</td>
			</tr>
		</tbody>
	</table>
	<div class="prepend-top append-bottom">
		<g:if test="${isOwnerOrAdmin}">
			<g:link controller='userGroup' action='edit' id="${userGroup.id}" elementId="user_group_edit">Edit group</g:link>
		</g:if>
		<g:if test="${canJoinGroup}">
			<g:link controller='userGroup' action='joinGroup' id="${userGroup.id}" elementId="user_group_join">Join this group</g:link>
		</g:if>
		<g:if test="${isMember}">
			<g:link controller='userGroup' action='removeFromGroup' id="${userGroup.id}" elementId="user_group_remove">Remove yourself from this group</g:link>
		</g:if>
	</div>
</div>
</div>
</body>
</html>

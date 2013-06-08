<html>
<head>
<title><g:message code="org.synote.user.group.show.title" /></title>
<meta name="layout" content="main" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<div class="container">
	<div class="row">
		<div class="span12" id="user_content_div">
			<h2 class="heading-inline"><g:message code="org.synote.user.group.show.title" /></h2>
			<hr/>
			<g:render template="/common/message" />
			<syn:isLoggedIn>
			<g:if test="${ isMember == false && isOwnerOrAdmin == false}">
			<div class="pull-right">
				<g:link class="btn btn-primary btn-large" action="joinGroup" title="Join this group" id="${userGroup.id}"><i class="icon-plus icon-white"></i>Join this group</g:link>
			</div>
			</g:if>
			<g:elseif test="${isMember == true && isOwnerOrAdmin == false}">
				<div class="pull-right">
					<g:link class="btn btn-danger btn-large" action="removeFromGroup" title="Remove yourself from this group" id="${userGroup.id}"><i class="icon-remove icon-white"></i>Remove from this group</g:link>
				</div>
			</g:elseif>
			</syn:isLoggedIn>
			<dl>
				<dt>Group Name</dt>
				<dd>${userGroup.name}</dd>
				<dt>Group Owner</dt>
				<dd>${userGroup.owner?.userName}</dd>
				<dt>Public</dt>
				<dd>${userGroup.shared==true?'Yes':'No'}</dd>
				<dt>Description</dt>
				<dd>${userGroup.description?userGroup.description:'<i>no description</i>'}</dd>
				<dt>Date Created</dt>
				<dd>${userGroup.dateCreated.format("dd-mm-yyyy")}</dd>
			</dl>
			<br/>
			<g:if test="${isOwnerOrAdmin == true }">
				<div class="pull-right">
					<g:link class="btn btn-danger" action="delete" title="Delete this group" id="${userGroup.id}" onclick="confirm('Are you sure you want to delete this group?')"><i class="icon-remove icon-white"></i>Delete</g:link>
				</div>
				<div class="pull-right" style="margin-right:10px">
					<g:link class="btn" action="editGroup" controller='user' title="Edit this group" id="${userGroup.id}"><i class="icon-pencil"></i>Edit</g:link>
				</div>
			</g:if>
			<br/>
			<h3>Group Members and Recordings</h3>
			<div class="accordion" id="usergroup_accordion">  
	            <div class="accordion-group">  
	              <div class="accordion-heading">  
	                <a class="accordion-toggle" data-toggle="collapse" data-parent="#usergroup_accordion" href="#members_collapse">  
	                  	Members (${members.size()+1}) 
	                </a>
	              </div>  
	              <div id="members_collapse" class="accordion-body collapse" style="height: 0px; ">  
	                <div class="accordion-inner">
	                	<span>Owner: <b> ${userGroup.owner?.userName}</b></span>
	                	<g:if test="${members?.size() > 0}">
	                		<table class="table table-condensed">
	                			<g:each in="${members}" status="i" var="member">
	                				<tr>
	                					<td>${member.user.userName}</td>
	                					<g:if test="${isOwnerOrAdmin}">
	                						<td><g:link title="remove this user from group" controller='userGroup' action='deleteMember' id="${member.id}" class="btn btn-mini btn-danger" onclick="confirm('Are you sure you want to remove this user from group?')"><i class="icon-remove icon-white"></i>Remove from this group</g:link></td>
	                					</g:if>
	                				</tr>
	                			</g:each> 
	                		</table>
	                	</g:if>
	                </div>  
	              </div>  
	            </div>  
	            <div class="accordion-group">  
	              <div class="accordion-heading">  
	                <a class="accordion-toggle" data-toggle="collapse" data-parent="#usergroup_accordion" href="#recordings_collapse">  
	                 	Recordings (${recordings.size()})
	                </a>  
	              </div>  
	              <div id="recordings_collapse" class="accordion-body collapse">  
	                <div class="accordion-inner"> 
	                	 <g:if test="${recordings?.size() > 0}">
	                		<table class="table table-condensed">
	                			<g:each in="${recordings}" status="i" var="permission">
	                				<g:set var="recording" value="${permission.resource}"/>
	                				<tr>
	                					<td><g:link title="replay this recording" controller="recording" action="replay" id="${recording.id}">${recording.title}</g:link></td>
	                					<g:if test="${isOwnerOrAdmin}">
	                						<td><g:link controller='userGroup' action='deletePermission' id="${permission.id}" class="btn btn-mini btn-danger" onclick="confirm('Are you sure you want to remove this user from group?')"><i class="icon-remove icon-white"></i>Remove from this group</g:link></td>
	                					</g:if>
	                				</tr>
	                			</g:each> 
	                		</table>
	                	</g:if>
	                </div>  
	              </div>  
	            </div>   
          	</div>  <!-- /usergroup_accordion -->
		</div>
	</div>
</div><!-- /container -->
</body>
</html>

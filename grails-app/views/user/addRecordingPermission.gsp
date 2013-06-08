<html>
<head>
<title><g:message code="org.synote.user.addRecordingPermission" /></title>
<meta name="layout" content="main" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<div class="container">
	<div class="row">
		<div class="span2" id="user_nav_div">
			<g:render template="/common/userNav" model="['active':'group']"/>
		</div>
		<div class="span10" id="user_content_div">
			<h2 class="heading-inline"><g:message code="org.synote.user.addRecordingPermission" /></h2>
			<hr/>
			<g:render template="/common/message" />
			<div>
				<g:form method='POST' name='addRecordingPermissionForm'>
					<fieldset>
						<div class="control-group">
							<label for="perm" class="control-label"><b>Privacy and Publishing Settings</b></label>
					      	<div class="controls">
					      		<g:render template="/common/permission" model="[canPrivate:false]" />
					      	</div>
				      	</div>
				    </fieldset>
				</g:form>
			</div>
			<h3>Select a group</h3>
			<div>
				<g:if test="${groupList.rows?.size()>0}">
					<table class="table table-bordered">
						<thead>
							<tr>
								<th>Name</th>
								<th>Members</th>
								<th>Recordings</th>
								<th>Created At</th>
								<th>Public</th>
								<th></th>
							</tr>
						</thead>
						<g:each in="${groupList.rows}" status="i" var="group">
							<tr class="${group.joined==true?'success':(group.shared == false?'error':'')}">
								<td>${group.name}</td>
								<td>${group.member_count}</td>
								<td>${group.recording_count}</td>
								<td>${group.date_created.format("dd-mm-yyyy")}</td>
								<td>${group.shared}</td>
								<td><g:link class="btn btn-success add-btn" controller="user" action="saveRecordingPermission" id="${multimedia.id}" params="[groupId:group.id]" title="add">Add</g:link></td>								
							</tr>
						</g:each>
					</table>
				</g:if>
				<g:else>
					<div style="font-size:120%">
						You haven't created any group yet.
					</div>
				</g:else>
				<div class="row" id="group_pagination">
					<g:render template="/common/pagination" 
						model="['currentPage':groupList.page,'rows':params.rows, 'sidx':params.sidx, 'query':params.text,
							'sord':params.sord,'ctrl':'user', 'act':'listGroups', 'total':groupList.total]"/>
				</div>
			</div>
		</div>
	</div>
</div><!-- /container -->
</body>
</html>
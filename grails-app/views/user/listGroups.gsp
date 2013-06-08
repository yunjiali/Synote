<html>
<head>
<title><g:message code="org.synote.user.listGroups.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
</head>
<body>
<div class="container">
	<div class="row">
		<div class="span2" id="user_nav_div">
			<g:render template="/common/userNav" model="['active':'group']"/>
		</div>
		<div class="span10" id="user_content_div">
			<h2 class="heading-inline"><g:message code="org.synote.user.listGroups.title" /></h2>
			<hr/>
			<g:render template="/common/message"/>
			<ul class="nav nav-tabs" id="grouplist_tab">
				<li class="active"><a href="#group_owner" data-toggle="tab">I am the Owner</a></li>
				<li><a href="#group_joined" data-toggle="tab">I joined</a></li>
			</ul>
			<div class="tab-content">
				<div class="tab-pane active" id="group_owner">
					<div>
						<g:link class="btn btn-primary btn-large" action="createGroup" title="Create a new group"><i class="icon-plus icon-white"></i>Create a new group</g:link>
					</div>
					<div>
						<span id="group_owned_count_span" style="padding:5px" class="pull-right label label-info">${groupListOwner.records} Groups</span>
					</div>
					<br/><br/>
					<g:if test="${groupListOwner.rows?.size()>0}">
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
						<g:each in="${groupListOwner.rows}" status="i" var="group">
							<tr class="${group.joined==true?'success':(group.shared == false?'error':'')}">
								<td>${group.name}</td>
								<td>${group.member_count}</td>
								<td>${group.recording_count}</td>
								<td>${group.date_created.format("dd-mm-yyyy")}</td>
								<td>${group.shared}</td>
								<td><g:link controller="userGroup" action="show" id="${group.id}" title="detail">Detail</g:link></td>
							</tr>
						</g:each>
					</table>
					</g:if>
					<g:else>
						<div style="font-size:120%">
							You haven't created any group yet.
						</div>
					</g:else>
					<div class="row" id="group_owned_pagination">
						<g:render template="/common/pagination" 
							model="['currentPage':groupListOwner.page,'rows':params.rows, 'sidx':params.sidx, 'query':params.text,
								'sord':params.sord,'ctrl':'user', 'act':'listGroups', 'total':groupListOwner.total]"/>
					</div>
				</div> <!-- /group_owner -->
				<div class="tab-pane" id="group_joined">
					<div>
						<span id="group_joined_count_span" style="padding:5px" class="pull-right label label-info">${groupListJoined.records} Groups</span>
					</div>
					<br/><br/>
					<g:if test="${groupListJoined.rows?.size()>0}">
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
						<g:each in="${groupListJoined.rows}" status="i" var="group">
							<tr class="${group.joined==true?'success':(group.shared == false?'error':'')}">
								<td>${group.name}</td>
								<td>${group.member_count}</td>
								<td>${group.recording_count}</td>
								<td>${group.date_created.format("dd-mm-yyyy")}</td>
								<td>${group.shared}</td>
								<td><g:link controller="userGroup" action="show" id="${group.id}" title="detail">Detail</g:link></td>
							</tr>
						</g:each>
					</table>
					</g:if>
					<g:else>
						<div style="font-size:120%">
							You haven't joined any group yet.
						</div>
					</g:else>
					<div class="row" id="group_joined_pagination">
						<g:render template="/common/pagination" 
							model="['currentPage':groupListJoined.page,'rows':params.rows, 'sidx':params.sidx, 'query':params.text,
								'sord':params.sord,'ctrl':'user', 'act':'listGroups', 'total':groupListJoined.total]"/>
					</div>
				</div>
			</div><!-- /tab-content -->
		</div><!-- /user_content_div  -->
	</div>
</div>
</body>
</html>

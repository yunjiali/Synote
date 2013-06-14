<html>
<head>
<title><g:message code="org.synote.user.group.list.title" /></title>
<meta name="layout" content="main" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<div class="container">
	<div class="row">
		<div class="span12" id="user_content_div">
			<h2 class="heading-inline"><g:message code="org.synote.user.group.list.title" /></h2>
			<span id="recording_count_span" style="padding:5px" class="pull-right label label-info">${groupList.records} groups</span>
			<hr/>
			<g:render template="/common/message" />
			<g:if test="${groupList.records>0}">
			<table class="table table-bordered">
				<thead>
					<tr>
						<th>Name</th>
						<th>Owner</th>
						<th>Members</th>
						<th>Recordings</th>
						<th>Created At</th>
						<th>Public</th>
						<th></th>
					</tr>
				</thead>
				<g:each in="${groupList.rows}" status="i" var="group">
					<tr class="${group.shared == false?'error':''}">
						<td>${group.name}</td>
						<td>${group.owner_name}</td>
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
			<div class="nodata">No groups have been found.</div>
			</g:else>
		</div>
	</div>
	<div class="row" id="group_pagination">
		<g:render template="/common/pagination" 
			model="['currentPage':groupList.page,'rows':params.rows, 'sidx':params.sidx, 'query':params.text,
				'sord':params.sord,'ctrl':'user', 'act':'listGroups', 'total':groupList.total]"/>
	</div>
</div><!-- /container -->
</body>
</html>

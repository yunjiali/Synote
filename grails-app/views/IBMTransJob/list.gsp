

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title><g:message
	code="org.synote.integration.ibmhts.list.title" /></title>
</head>
<body>
<h1><g:message code="org.synote.integration.ibmhts.list.title" /></h1>
<div style="font-size:3em">Coming Soon...</div>
<!--  
<g:render template="/common/message" />
<div class="list">
<table>
	<thead>
		<tr>
			<g:sortableColumn property="title" title="Title" />
			<g:sortableColumn property="dateCreated" title="Created Date" />
			<g:sortableColumn property="status" title="Status" />
			<g:sortableColumn property="saved" title="Saved" />
			<g:sortableColumn property="lastUpdated" title="Last Updated" />
				<th></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${IBMTransJobList}" status="i" var="IBMTransJob">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
				${fieldValue(bean:IBMTransJob, field: 'title')}
				</td>
				<td>
				${fieldValue(bean:IBMTransJob, field: 'dateCreated')}
				</td>
				<td>
				${org.synote.integration.ibmhts.IBMTransJobStatus.valueOfInt(Integer.parseInt(fieldValue(bean:IBMTransJob, field: 'status').toString()))}
				</td>
				<td>
				${fieldValue(bean:IBMTransJob, field: 'saved')}
				</td>
				<td>
				${fieldValue(bean:IBMTransJob, field: 'lastUpdated')}
				</td>
				<td><g:link action='show' id="${IBMTransJob.id}"
					title='show detail'>Detail</g:link></td>
			</tr>
		</g:each>
	</tbody>
</table>
</div>
<div class="paginateButtons"><g:paginate
	total="${IBMTransJobCount}" /></div>
</div>
-->
</body>
</html>

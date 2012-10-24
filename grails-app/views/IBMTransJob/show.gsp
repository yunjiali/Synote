<%@page import="org.synote.permission.PermissionValue"%>
<%@page import="org.synote.integration.ibmhts.IBMTransJobStatus"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title><g:message
	code="org.synote.integration.ibmhts.show.title" /></title>
<g:javascript library="prototype" />
</head>
<body>
<div class="nav"><span class="menuButton"><g:link
	class="list" action="list">IBM Transcript Service Job List</g:link></span></div>
<div class="body">
<h1><g:message code="org.synote.integration.ibmhts.list.title" /></h1>
<g:render template="/common/message" model="[bean: iBMTransJob]" />
<div class="dialog">
<table>
	<tbody>
		<tr class="prop">
			<td valign="top" class="name">Title:</td>
			<td valign="top" class="value">
			${fieldValue(bean:iBMTransJob, field:'title')}
			</td>
		</tr>
		<tr class="prop">
			<td valign="top" class="name">Url:</td>
			<td valign="top" class="value">
			${fieldValue(bean:iBMTransJob, field:'url')}
			</td>
		</tr>
		<tr class="prop">
			<td valign="top" class="name">Status:</td>
			<td valign="top" class="value">
			${IBMTransJobStatus.valueOfInt(iBMTransJob.status)}
			</td>
		</tr>
		<tr class="prop">
			<td valign="top" class="name">Created Date:</td>
			<td valign="top" class="value">
			${fieldValue(bean:iBMTransJob, field:'dateCreated')}
			</td>
		</tr>
		<tr class="prop">
			<td valign="top" class="name">Last Update</td>
			<td valign="top" class="value">
			${fieldValue(bean:iBMTransJob, field:'lastUpdated')}
			</td>
		</tr>
		<tr class="prop">
			<td class="name">Transcript</td>
			<g:if test="${iBMTransJob?.saved}">
				<td valign="top" class="value">
					${iBMTransJob?.transcript }
				</td>
			</g:if>
			<g:elseif test="${!iBMTransJob?.saved && iBMTransJob.status == IBMTransJobStatus.DONE.value()}">
				<td class="value" style="Color:red">
					<g:message code="org.synote.integration.ibmhts.show.job.unuploaded" />
				</td>
			</g:elseif>
			<g:elseif test="${!iBMTransJob?.saved && iBMTransJob.status == IBMTransJobStatus.PROCESSING.value()}">
				<td class="value" style="Color:red">
					<g:message code="org.syntoe.integration.ibmhts.show.job.processing" />
				</td>
			</g:elseif>
			<g:elseif test="${!iBMTransJob?.saved && iBMTransJob.status == IBMTransJobStatus.FAILED.value()}">
				<td class="value" style="Color:red">
					<g:message code="org.synote.integration.ibmhts.show.job.failed" />
				</td>
			</g:elseif>
		</tr>
	</tbody>
</table>
</div>
<div class="links">
	<g:if test="${iBMTransJob.status == IBMTransJobStatus.DONE.value() && iBMTransJob.saved}">
		<span class="link"><g:link target="_blank" action="downloadTranscript" class="download"
				title="Download Transcript to your local disk" id="${iBMTransJob?.id}">Download Transcript</g:link>
		</span>
	</g:if>
	<g:if test="${connected}">
		<g:if test="${perm?.val >= PermissionValue.findByName('WRITE').val && iBMTransJob.status == IBMTransJobStatus.DONE.value()}">
			<span class="link"><a target="_blank" href="${editUrl}" class="edit" title="Edit Transcript">Edit Transcript</a></span>
		</g:if> 
		<syn:isOwnerOrAdmin owner="${iBMTransJob.owner.id}">
			<g:if test="${iBMTransJob.status == IBMTransJobStatus.DONE.value()}">
				<span class="link">
					<g:if test="${iBMTransJob.saved}">
						<g:link action="handleUpload" id="${iBMTransJob.id}" onclick="return confirm('Current transcript will be over-written. Are you sure?');"
							class="upload" title="Reupload Transcript">Reupload Transcript</g:link>
					</g:if>
					<g:else>
						<g:link action="handleUpload" id="${iBMTransJob.id}"
							class="upload" title="Upload Transcript to synote">Upload Transcript</g:link>
					</g:else>
				</span>
			</g:if>
			<span class="link">
				<g:link action="delete" onclick="return confirm('Are you sure?');" class="delete"
				title="Delete Job" id="${iBMTransJob?.id}">Delete Job</g:link>
			</span>
		</syn:isOwnerOrAdmin>
	</g:if>
	<g:else>
		<span style="color:red"><g:message code="org.synote.integration.ibmhts.show.downtime" /></span>
	</g:else>
</div>
</div>
</body>
</html>

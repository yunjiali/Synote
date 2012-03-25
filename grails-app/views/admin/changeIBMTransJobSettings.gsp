<html>
<head>
<title>IBM transcript server settings</title>
<meta name="layout" content="main" />
</head>
<body>
<div class="nav"></div>
<div class="body">
<h1>IBM transcript server settings</h1>
<g:render template="/common/message" /> <g:form method="post">
	<div class="dialog">
	<table>
		<tbody>
			<tr class="prop">
				<td class="name"><label for="IBMTransJobEnabled">IBM
				Transcript Service Enabled</label></td>
				<td class="value">${IBMTransJobEnabled}</td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="IBMServerProtocol">IBM
				Transcript Service Protocol</label></td>
				<td class="value"><input type="text"
					name="IBMServerProtocol" value="${IBMServerProtocol}" /></td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="IBMServerName">IBM
				Transcript Server Name</label></td>
				<td class="value"><input type="text"
					name="IBMServerName" value="${IBMServerName}" /></td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="IBMServerPort">IBM
				Transcript Server Port</label></td>
				<td class="value"><input type="text"
					name="IBMServerPort" value="${IBMServerPort}" /></td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="IBMServerAppPath">IBM
				Transcript Server Application Path</label></td>
				<td class="value"><input type="text"
					name="IBMServerAppPath" value="${IBMServerAppPath}" /></td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="IBMServerSourceDir">IBM
				Transcript Server Source Directory</label></td>
				<td class="value"><input type="text"
					name="IBMServerSourceDir" value="${IBMServerSourceDir}" /></td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="stopAddingJobDayOfWeek">IBM
				Transcript Server Stop Adding Job Day Of Week</label></td>
				<td class="value"><input type="text"
					name="stopAddingJobDayOfWeek" value="${stopAddingJobDayOfWeek}" /></td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="stopAddingJobTime">IBM
				Transcript Server Stop Adding Job Time</label></td>
				<td class=
				"value"><input type="text"
					name="stopAddingJobTime" value="${stopAddingJobTime}" /></td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="downtimeDayOfWeek">IBM
				Transcript Server Downtime Day Of Week</label></td>
				<td class="value"><input type="text"
					name="downtimeDayOfWeek" value="${downtimeDayOfWeek}" /></td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="downtimeStartTime">IBM
				Transcript Server Downtime Start Time</label></td>
				<td class="value"><input type="text"
					name="downtimeStartTime" value="${downtimeStartTime}" /></td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="downtimeEndTime">IBM
				Transcript Server Downtime End Time</label></td>
				<td class="value"><input type="text"
					name="downtimeEndTime" value="${downtimeEndTime}" /></td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="downtimeCheckInterval">IBM
				Transcript Server Downtime Check Interval Time</label></td>
				<td class="value"><input type="text"
					name="downtimeCheckInterval" value="${downtimeCheckInterval}" /></td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="autoDowntime">IBM
				Transcript Server auto downtime</label></td>
				<td class="value"><input type="text"
					name="autoDowntime" value="${autoDowntime}" /></td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="allowAddingJobs">Allow Adding Jobs</label></td>
				<td class="value">${allowAddingJobs}</td>
			</tr>
		</tbody>
	</table>
	</div>
	<div class="buttons">
		<span class="button">
			<g:actionSubmit class="save" value="Save" action="saveIBMTransJobSettings" title="save settings" />
			<g:if test="${IBMTransJobEnabled.toLowerCase() == 'true'}">
				<g:actionSubmit class="save" value="Disable Transcribing Service" action="disableIBMTransJob" title="Disable Transcribing Service" onclick="return confirm('Are you sure?');"/>
			</g:if>
			<g:if test="${IBMTransJobEnabled.toLowerCase() == 'false'}">
				<g:actionSubmit class="save" value="Enable Transcribing Service" action="EnableIBMTransJob" title="Enable Transcribing Service" onclick="return confirm('Are you sure?');"/>
			</g:if>
			<g:if test="${connected && !allowAddingJobs && !withinStopAddingJobsTime}">
				<g:actionSubmit class="save" value="Enable adding jobs" action="enableAddingJobs" title="Enable Adding Jobs" onclick="return confirm('Are you sure?');"/>
			</g:if>
			<g:elseif test="${connected && allowAddingJobs }">
				<g:actionSubmit class="save" value="Disable adding jobs" action="disableAddingJobs" title="Disable Adding Jobs" onclick="return confirm('Are you sure?');"/>
			</g:elseif>
			<g:elseif test="${!connected}">
				<i style="color:red">Have not connected to the transcribing server yet. Please check later.</i>
			</g:elseif>
		</span>
	</div>
</g:form></div>
</body>
</html>

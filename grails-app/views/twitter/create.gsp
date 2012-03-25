<html>
<head>
<title><g:message
	code="org.synote.integration.twitter.create.title" /></title>
<meta name="layout" content="main" />
<g:javascript library="prototype" />
<calendar:resources lang="en" theme="blue" />
</head>
<body>
<div style="font-size:3em;">Coming Soon...</div>
<!-- 
<div class="nav"></div>
<div class="body">
<h1><g:message code="org.synote.integration.twitter.create.title" /></h1>
<g:render template="/common/message" model="[bean: multimediaResource]" />
<g:form method="post">
	<div class="dialog">
	<table>
		<tbody>
			<tr class="prop">
				<td class="name">Multimedia Title:</td>
				<td class="value">
				${fieldValue(bean: multimediaResource, field: 'title')}
				</td>
			</tr>
			<tr>
				<td><g:hiddenField name="multimediaResourceId"
					value="${fieldValue(bean: multimediaResource, field: 'id')}" /></td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="title">Twitter Username:</label></td>
				<td class="value"><input type="text" id="twitter_userName"
					name="twitter_userName" /></td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="title">Twitter Hashtag:</label></td>
				<td class="value"><input type="text" id="twitter_hashTag"
					name="twitter_hashTag" /></td>
			</tr>
			<tr class="prop">
				<td valign="top" class="name"><label for="startTime_Rec">Start
				Time of Recording:</label></td>
				<td valign="top" class="value"><g:datePicker
					name="startTime_Rec" value="${new Date()}" /> :<g:select
					name="startTime_RecSec" from="${0..59}" /></td>
			</tr>
			<!--
						<tr class="prop">
											 <td valign="top" class="name">
												 <label for="endTime_Rec">End Time of Recording:</label>
											 </td>
												<td valign="top" class="value">
												<g:datePicker name="endTime_Rec" value="${new Date()}"/>
												:<g:select name="endTime_RecSec" from="${0..59}"/>
											</td>
									 </tr>
							-->
			<tr class="prop">
				<td valign="top" class="name"><label for="startTime_Tweet">Start
				Time of Tweets:</label></td>
				<td valign="top" class="value"><g:datePicker
					name="startTime_Tweet" value="${new Date()}" /> :<g:select
					name="startTime_TweetSec" from="${0..59}" /></td>
			</tr>
			<tr class="prop">
				<td valign="top" class="name"><label for="endTime">End
				Time of Tweets:</label></td>
				<td valign="top" class="value"><g:datePicker
					name="endTime_Tweet" value="${new Date()}" /> :<g:select
					name="endTime_TweetSec" from="${0..59}" /></td>
			</tr>
		</tbody>
	</table>
	</div>
	<div class="buttons"><span class="button"><g:actionSubmit
		class="tweetUpload" value="List Tweets" action="list"
		title="List Tweets" /></span></div>
</g:form></div>
 -->
</body>
</html>

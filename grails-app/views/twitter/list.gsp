<html>
<head>
<title><g:message
	code="org.synote.integration.twitter.list.title" /></title>
<meta name="layout" content="main" />
</head>
<body>
<div class="nav"></div>
<div class="body">
<h1><g:message code="org.synote.integration.twitter.list.title" /></h1>
<g:render template="/common/message" model="[bean: tweetList]" /> <g:form
	method="post">
	<div class="list">
	<table>
		<thead>
			<tr>
				<g:sortableColumn property="number" title="No." />
				<g:sortableColumn property="tweet" title="Tweet" />
				<g:sortableColumn property="time" title="Time" defaultOrder="asc" />
				<g:sortableColumn property="select" title="Select" />
			</tr>
		</thead>
		<tbody>
			<g:each status="i" in="${tweetList}" var='tweet'>
				<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
					<td>
					${(i+1)}
					</td>
					<td>
					${tweet.tweet}
					</td>
					<td><g:formatDate format="EEE, dd MMM yyyy HH:mm:ss"
						date="${tweet.tweetDate}" /></td>
					<td><g:checkBox
						name="checkbox_${i}" value="${true}" checked="true"/></td>
							</tr>
							<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
								<td><g:hiddenField name="tweet_${i}" value="${tweet.tweet}"/></td>
								<td><g:hiddenField name="tdate_${i}" value="${tweet.tweetDate}"/></td>
								<td><g:hiddenField name="mid_${i}" value="${tweet.multimediaResourceId}"/></td>
								<td><g:hiddenField name="rdate_${i}" value="${tweet.recordingDate}"/></td>
							</tr>
						</g:each>
						<tr>
							<g:if test="${tweetList.size()== null}">
								<td><g:hiddenField name="tweetCount" value="0"/></td>
							</g:if>
							<g:else>
								<td><g:hiddenField name="tweetCount" value="${tweetList.size()}"/></td>
							</g:else>
						</tr>
					</tbody>
				</table>
			</div>
			<div class="buttons">
				<span class="button"><g:actionSubmit class="TweetUpload" value="Convert Tweets into Synmarks" action="save" title="Convert Tweets into Synmarks"/></span>
			</div>
		</g:form>
	</div>
</body>
</html>

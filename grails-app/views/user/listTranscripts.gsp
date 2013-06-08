<html>
<head>
<title><g:message code="org.synote.user.listTranscripts.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
</head>
<body>
<div class="container">
	<div class="row">
		<div class="span2" id="user_nav_div">
			<g:render template="/common/userNav" model="['active':'transcripts']"/>
		</div>
		<div class="span10" id="user_content_div">
			<h2 class="heading-inline"><g:message code="org.synote.user.listTranscripts.title" /></h2>
			<span id="recording_count_span" style="padding:5px" class="pull-right label label-info">${multimediaList.records} transcripts</span>
			<br/>
			<g:render template="/common/message"/>
			<div>
				<div id="recording_list_div">
					<g:if test="${multimediaList.rows?.size() == 0}">
						<div class="nodata">You have no recordings with transcript</div>
					</g:if>
					<g:each in="${multimediaList.rows}" var="row">
						<g:render template="/common/recording" model="['row':row,'actionEnabled':false, 'viewTranscriptsEnabled':true,'viewSynmarksEnabled':false]"/>
					</g:each>
				</div>
			</div>
			<div class="row" id="recording_pagination">
				<g:render template="/common/pagination" 
					model="['currentPage':multimediaList.page,'rows':params.rows, 'sidx':params.sidx, 'text':params.text,
						'sord':params.sord,'ctrl':'user', 'act':'listRecordings', 'total':multimediaList.total]"/>
			</div>
		</div>
	</div>
</div>
</body>
</html>

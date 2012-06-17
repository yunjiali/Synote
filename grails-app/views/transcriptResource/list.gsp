<html>
<head>
<title><g:message code="org.synote.resource.compound.TranscriptResource.list.title" /></title>
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
			<h2>Transcript for
				<g:link controller="reocording" action="replay" id="${multimedia.id}">${multimedia.title }</g:link>
			</h2>
			<div class="row">
			<span id="recording_count_span" style="padding:5px" class="pull-right label label-info">${cueList.records} Transcript Blocks</span>
			</div>
			<div>
				<div id="recording_list_div">
					<g:if test="${cueList.rows?.size() == 0}">
						<div class="nodata">There is no transcript for this recording</div>
					</g:if>
					<g:each in="${cueList.rows}" var="row">
						<g:render template="/common/webvttcue" model="['row':row,'multimedia':multimedia]"/>
					</g:each>
				</div>
			</div>
			
			<div class="row" id="recording_pagination">
				<g:render template="/common/pagination" 
					model="['currentPage':cueList.page,'rows':params.rows, 'sidx':params.sidx, 'text':params.text,
						'sord':params.sord,'ctrl':'transcriptResource', 'act':'list', 'id':multimedia.id,'total':cueList.total]"/>
			</div>
			
		</div>
	</div>
</div>
</body>
</html>

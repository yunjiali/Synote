<html>
<head>
<title><g:message code="org.synote.user.listSynmarks.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
</head>
<body>
<div class="container">
	<div class="row">
		<div class="span2" id="user_nav_div">
			<g:render template="/common/userNav" model="['active':'synmarks']"/>
		</div>
		<div class="span10" id="user_content_div">
			<h2 class="heading-inline"><g:message code="org.synote.user.listSynmarks.title" /></h2>
			<span id="recording_count_span" style="padding:5px" class="pull-right label label-info">${synmarksList.records} Synmarks</span>
			<div class="row">
				<div>
					<div class="pull-right span7">
						<g:form class="form-inline pull-right" action="listSynmarks">
							<input type="text" name="text" class="input-medium" placeholder="Search your Synamrks" value="${params.text}">
							<input type="submit" class="btn" value="Submit" />
						</g:form>
					</div>
				</div>
			</div>
			<div>
				<div id="synmark_list_div">
					<g:if test="${synmarksList.rows?.size() == 0}">
						<div class="nodata">You have no recordings</div>
					</g:if>
					<g:each in="${synmarksList.rows}" var="row">
						<g:render template="/common/synmark" model="['row':row,'nerditEnabled':false]"/>
					</g:each>
				</div>
			</div>
			<div class="row" id="recording_pagination">
				<g:render template="/common/pagination" 
					model="['currentPage':synmarksList.page,'rows':params.rows, 'sidx':params.sidx, 'text':params.text,
						'sord':params.sord,'ctrl':'user', 'act':'listSynmarks', 'total':synmarksList.total]"/>
			</div>
		</div>
	</div>
</div>
</body>
</html>

<html>
<head>
<title><g:message code="org.synote.resource.compound.multimediaResource.list.title" /></title>
<meta name="layout" content="main" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<div class="container">
	<div class="row">
		<div class="span10" id="user_content_div">
			<h2 class="heading-inline"><g:message code="org.synote.resource.compound.multimediaResource.list.title" /></h2>
			<span id="recording_count_span" style="padding:5px" class="pull-right label label-info">${multimediaList.records} recordings</span>
			<g:render template="/common/message" />
			<div class="row">
				<div>
					<div class="pull-right span7">
						<g:form class="form-inline pull-right" action="list">
							<label for="sidx">Sorted By:</label>
							<select name="sidx">
								<g:if test="${params.sidx == 'date_created'}">
									<option value="date_created" selected="selected">Date Created</option>
								</g:if>
								<g:else>
									<option value="date_created">Date Created</option>
								</g:else>
								<g:if test="${params.sidx == 'title'}">
									<option value="title" selected="selected">Title</option>
								</g:if>
								<g:else>
									<option value="title">Title</option>
								</g:else>
								<g:if test="${params.sidx =='perm'}">
									<syn:isLoggedIn>
										<option value="user_perm_val" selected="selected">Permission</option>
									</syn:isLoggedIn>
									<syn:isNotLoggedIn>
										<option value="public_perm_val" selected="selected">Permission</option>
									</syn:isNotLoggedIn>
								</g:if>
								<g:else>
									<syn:isLoggedIn>
										<option value="user_perm_val">Permission</option>
									</syn:isLoggedIn>
									<syn:isNotLoggedIn>
										<option value="public_perm_val">Permission</option>
									</syn:isNotLoggedIn>
								</g:else>
							</select>
							<input type="submit" class="btn" value="Submit" />
						</g:form>
					</div>
				</div>
			</div>
			<div>
				<div id="recording_list_div">
					<g:if test="${multimediaList.rows?.size() == 0}">
						<div class="nodata">No recordings have been found.</div>
					</g:if>
					<g:each in="${multimediaList.rows}" var="row">
						<g:render template="/common/recording" model="['row':row,'actionEnabled':true, 'viewTranscriptsEnabled':false,'viewSynmarksEnabled':false]"/>
					</g:each>
				</div>
			</div>
			<div class="row" id="recording_pagination">
				<g:render template="/common/pagination" 
					model="['currentPage':multimediaList.page,'rows':params.rows, 'sidx':params.sidx, 'text':params.text,
						'sord':params.sord,'ctrl':'multimediaResource', 'act':'list', 'total':multimediaList.total]"/>
			</div>
		</div>
		<div class="span2" id="most_views_div">
			<h4 class="pull-right">Most viewed</h4>
			<br/>
			<g:each in="${viewList.rows}" var="preview_row">
				<div style="padding-left:15px">
					<g:render template="/common/recording_preview" model="['preview_row':preview_row]" />
				</div>
			</g:each>
		</div>
	</div>
</div>
</body>
</html>

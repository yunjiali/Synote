<html>
<head>
<title><g:message code="org.synote.user.listRecordings.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
</head>
<body>
<div class="container">
	<div class="row">
		<div class="span2" id="user_nav_div">
			<g:render template="/common/userNav" model="['active':'recordings']"/>
		</div>
		<div class="span10" id="user_content_div">
			<h2 class="heading-inline"><g:message code="org.synote.user.listRecordings.title" /></h2>
			<span id="recording_count_span" style="padding:5px" class="pull-right label label-info">${multimediaList.records} recordings</span>
			<div class="row">
				<div>
					<div class="pull-right span7">
						<g:form class="form-inline pull-right" action="listRecordings">
							<label for="sidx">Sorted By:</label>
							<select name="sidx">
								<g:if test="${params.sidx == 'dateCreated'}">
									<option value="dateCreated" selected="selected">Date Created</option>
								</g:if>
								<g:else>
									<option value="dateCreated">Date Created</option>
								</g:else>
								<g:if test="${params.sidx =='perm'}">
									<option value="perm" selected="selected">Permission</option>
								</g:if>
								<g:else>
									<option value="perm">Permission</option>
								</g:else>
							</select>
							<input type="text" name="text" class="input-medium" placeholder="Search your recordings" value="${params.text}">
							<input type="submit" class="btn" value="Submit" />
						</g:form>
					</div>
				</div>
			</div>
			<div>
				<div id="recording_list_div">
					<g:if test="${multimediaList.rows?.size() == 0}">
						<div class="nodata">You have no recordings</div>
					</g:if>
					<g:each in="${multimediaList.rows}" var="row">
						<g:render template="/common/recording" model="['row':row,'editable':true]"/>
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

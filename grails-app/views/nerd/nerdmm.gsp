<html>
<head>
<title>Analyse multimedia using NERD</title>
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
			<h2 class="heading-inline">NERD Recording</h2>
			<hr/>
			<g:form controller="nerd" action="nerdit" method='GET'>
			<h3>Choose Named Entity Extractor</h3>
			<div>
				<g:render template="/common/nerd_extractor"/>
			</div>
			
			<h3>Select text from this recording</h3>
			<div>
				<table class="table table-bordered table-striped">
					<colgroup>
						<col class="span1"/><!-- checkbox -->
						<col class="span1"/><!-- which field, title, note or tags -->
						<col class="span4"/><!-- text -->
						<col class="span4"/><!-- NE which has been recognised -->
					</colgroup>
					<thead>
						<tr>
							<th></th>
							<th>Field</th>
							<th>Content Preview</th>
							<th>Named Entities</th>
						</tr>
					</thead>
					<tbody>
						<!-- title row -->
						<tr id="tr_${multimedia.id}">
							<td><input id="cbx_${multimedia.id}" name="fields" value="${multimedia.id}" type="checkbox"/></td>
							<td>Title</td>
							<td id="text_${multimedia.id}">${multimedia.title}</td>
							<td>No entity yet</td>
						</tr>
						<!-- recording description row -->
						<g:if test="${multimedia.note?.content?.trim()?.size()>0}">
						<tr id="tr_${multimedia.note.id}">
							<td><input id="cbx_${multimedia.note?.id}" name="fields" value="${multimedia.note?.id}" type="checkbox"/></td>
							<td>Description</td>
							<td id="text_${multimedia.note.id}">${multimedia.note?.content}</td>
							<td>No entity yet</td>
						</tr>
						</g:if>
						<!-- recording tags rows -->
						<g:if test="${multimedia.tags?.size() >0}">
							<g:each in="${multimedia.tags}" var="tag">
								<g:if test="${tag.content?.trim()?.size() >0}">
								<tr id="tr_${tag.id}">
									<td><input id="cbx_${tag.id}" name="fields" value="${tag.id}" type="checkbox"/></td>
									<td>Tag</td>
									<td id="text_${tag.id}">${tag.content}</td>
									<td>No entity yet</td>
								</tr>
								</g:if>
							</g:each>
						</g:if>
					</tbody>
				</table>
			</div>
			<div class="form-actions">
				<div class="pull-left">
	            	<input type="submit" id="nerditForm_submit" class="btn btn-large btn-warning btn-nerd" value="Nerd it"/>
	            	<input class="btn btn-large" id="nerditForm_reset" type="reset" value="Reset"/>
	            </div>
			</div>
			</g:form>		
		</div>
	</div>
</div>
</body>
</html>

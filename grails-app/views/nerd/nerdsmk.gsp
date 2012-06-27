<html>
<head>
<title>Analyse Synmark using NERD</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<script type="text/javascript">
$(document).ready(function(){
	$('#cbx_selectall').change(function () {
	    if ($(this).attr("checked")) {
			$("input[name='fields']").attr("checked","checked");
	        return;
	    }
	    
	    $("input[name='fields']").removeAttr("checked");
	    return
	});
});
</script>
</head>
<body>
<div class="container">
	<div class="row">
		<div class="span2" id="user_nav_div">
			<g:render template="/common/userNav" model="['active':'synmarks']"/>
		</div>
		<div class="span10" id="user_content_div">
			<h2 class="heading-inline">NERD Synmark</h2>
			<hr/>
			<g:form controller="nerd" action="nerditone" method='GET' id="${synmark.id}">
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
							<th><input id="cbx_selectall" type="checkbox" checked="checked" /></th>
							<th>Field</th>
							<th>Content Preview</th>
						</tr>
					</thead>
					<tbody>
						<!-- title row, synmark title could be empty -->
						<g:if test="${synmark.title?.trim()?.size()>0}">
						<tr id="tr_${synmark.id}">
							<td><input id="cbx_${synmark.id}" name="fields" value="${synmark.id}" type="checkbox" checked="checked"/></td>
							<td>Title</td>
							<td id="text_${synmark.id}">${synmark.title}</td>
						</tr>
						</g:if>
						<!-- recording description row -->
						<g:if test="${synmark.note?.content?.trim()?.size()>0}">
						<tr id="tr_${synmark.note.id}">
							<td><input id="cbx_${synmark.note?.id}" name="fields" value="${synmark.note?.id}" type="checkbox" checked="checked"/></td>
							<td>Description</td>
							<td id="text_${synmark.note.id}">${synmark.note?.content}</td>
						</tr>
						</g:if>
						<!-- recording tags rows -->
						<g:if test="${synmark.tags?.size() >0}">
							<g:each in="${synmark.tags}" var="tag">
								<g:if test="${tag.content?.trim()?.size() >0}">
								<tr id="tr_${tag.id}">
									<td><input id="cbx_${tag.id}" name="fields" value="${tag.id}" type="checkbox" checked="checked"/></td>
									<td>Tag</td>
									<td id="text_${tag.id}">${tag.content}</td>
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

<html>
<head>
<title>Analyse Transcript Block NERD</title>
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
			<h2 class="heading-inline">NERD Recording</h2>
			<hr/>
			<g:form controller="nerd" action="nerditone" method='GET'>
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
						<tr id="tr_${cue.id}">
							<td><input id="cbx_${cue.id}" name="fields" value="${cue.id}" type="checkbox"/></td>
							<td>text</td>
							<td id="text_${cue.id}">
								<g:if test="${cue.speaker?.size()>0}">
								<b>${cue.speaker}</b><br/>
								</g:if>
								${cue.text?.encodeAsHTML()}
							</td>
							<td>No entity yet</td>
						</tr>
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

<html>
<head>
<title><g:message code="org.synote.user.createGroup.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<script type="text/javascript" src="${resource(dir: 'js/jquery', file: 'jquery.validate-1.9.1.min.js')}"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$("#createGroupForm").validate(
		{
			rules: {
			    name: "required",
			    description:"required"
			 },
			highlight: function(label) {
				$(label).closest('.control-group').addClass('error');
			}
		});
	});
</script>
</head>
<body>
<div class="container">
	<div class="row">
		<div class="span2" id="user_nav_div">
			<g:render template="/common/userNav" model="['active':'group']"/>
		</div>
		<div class="span10" id="user_content_div">
			<h2 class="heading-inline"><g:message code="org.synote.user.createGroup.title" /></h2>
			<hr/>
			<g:render template="/common/message" model="[bean: userGroup]" />
			<div class="span10">
				<g:form method='POST' name='createGroupForm' class="form-horizontal" controller="user" action="saveGroup">
				  <fieldset>
				    <div class="control-group">
				    	<label for="name" class="control-label"><b><em>*</em>Group Name</b></label>
				      	<div class="controls">
				        	<input type='text' autocomplete="off" class="required" name='name' id='name' value='${params.name}' />
				      	</div>
				    </div>
				    <div class="control-group">
				     	<label for="shared" class="control-label"><b><em>*</em>Access Level</b></label>
				      	<div class="controls">
				      		<label class="checkbox">
					      		<g:if test="${params.shared == true}">
									<input type='checkbox' name='shared' id='shared' checked="checked"/>
								</g:if>
								<g:else>
									<input type='checkbox' name='shared' id='shared'/>
								</g:else>
								This is a public group 
							</label>
							<span class="help-block">Users can search both public or private groups, but you need a passphrase to join the private group.</span>
				      	</div>
				    </div>
				    <div class="control-group">
				     	<label for="description" class="control-label"><b><em>*</em>Group Description</b></label>
				      	<div class="controls">
							<textarea name='description' rows="4" class="required" id='description'>${params.description?.trim()?params.description?.trim():""}</textarea>
				      	</div>
				    </div>
				    <div class="form-actions">
			            <input class="btn btn-large btn-primary" id="createGroupForm_submit" type="submit" value="Create" />
			            <input class="btn btn-large" id="createGroupForm_reset" type="reset" value="Reset"/>
			        </div>
				  </fieldset>
				</g:form>
			</div>
		</div>
	</div>
</div>
</body>
</html>

<html>
<head>
<title><g:message code="org.synote.user.group.edit.title" /></title>
<meta name="layout" content="main" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script type="text/javascript">
	$(document).ready(function(){
		$("#editGroupForm").validate(
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
			<h2 class="heading-inline"><g:message code="org.synote.user.group.edit.title" /></h2>
			<hr/>
			<g:render template="/common/message" />
			<g:form method='POST' name='editGroupForm' class="form-horizontal" controller="user" action="updateGroup">
			  <fieldset>
			  	<input type="hidden" name="id" value="${userGroup.id}"/>
			    <div class="control-group">
			    	<label for="name" class="control-label"><b><em>*</em>Group Name</b></label>
			      	<div class="controls">
			        	<input type='text' autocomplete="off" class="required" name='name' id='name' value='${userGroup.name}' />
			      	</div>
			    </div>
			    <div class="control-group">
			     	<label for="shared" class="control-label"><b><em>*</em>Access Level</b></label>
			      	<div class="controls">
			      		<label class="checkbox">
				      		<g:if test="${userGroup.shared == true}">
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
						<textarea name='description' rows="4" class="required" id='description'>${userGroup.description}</textarea>
			      	</div>
			    </div>
			    <div class="form-actions">
		            <input class="btn btn-large btn-primary" id="editGroupForm_submit" type="submit" value="Update" />
		            <input class="btn btn-large" id="editGroupForm_reset" type="reset" value="Reset"/>
		        </div>
			  </fieldset>
			</g:form>
		</div>
	</div>
</div><!-- /container -->
</body>
</html>

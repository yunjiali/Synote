<%@page import="org.synote.resource.compound.MultimediaResource"%>
<%@page import="org.synote.permission.PermissionValue"%>
<html>
<head>
<title><g:message code="org.synote.resource.compound.multimediaResource.edit.title" /></title>
<meta name="layout" content="main" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script type="text/javascript" src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.9/jquery.validate.min.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$("#multimediaEditForm").validate(
		{
			rules: {
			    title: {
				    required:true,
			    	maxlength:255
			    },
			    url:{
					required:true,
					url:true
				},
				duration:{
					required:true
				},
				isVideo:{
					required:true
				}
			 },
			highlight: function(label) {
				$(label).closest('.control-group').addClass('error');
			},
		});
	});
</script>
</head>
<body>
<!-- set thumbnail url -->
<g:if test="${multimedia.thumbnail?.size() > 0 && multimedia.isVideo==true}">
	<g:set var="thumbnail_src" value="${multimedia.thumbnail}"/>
</g:if>
<g:elseif test="${multimedia.isVideo == false }">
	<g:set var="thumbnail_src" value="${resource(dir: 'images', file: 'audio_default.png')}"/>
</g:elseif>
<g:else>
	<g:set var="thumbnail_src" value="${resource(dir: 'images', file: 'video_default.jpg')}"/>
</g:else>

<!-- set duration -->
<g:if test="${multimedia.duration == -1}">
	<g:set var="duration" value="unknown"/>
</g:if>
<g:else>
	<g:set var="duration" value="${multimedia.duration }"/>
</g:else>
<div class="container">
	<div class="row">
		<div class="span2" id="user_nav_div">
			<g:render template="/common/userNav" model="['active':'recordings']"/>
		</div>
		<div class="span10" id="user_content_div">
			<h2 class="heading-inline"><g:message code="org.synote.resource.compound.multimediaResource.edit.title" /></h2>
			<a class="btn btn-warning pull-right" href="#">Nerd It!</a>
			<hr/>
			<g:render template="/common/message" model="[bean: multimediaResource]" />
			<div>
				<g:form method='POST' name='multimediaEditForm' controller="multimediaResource" action="update">
					<fieldset>
						<div class="span5">
							<input type="hidden" name="id" value="${multimedia.id}" />
							<div class="control-group">
								<label for="title" class="control-label"><b><em>*</em>Title</b></label>
						      	<div class="controls">
						        	<input type='text' autocomplete="off" class="required span4" name='title' id='title' value="${fieldValue(bean: multimedia, field: 'title')}" />
						      	</div>
					      	</div>
					      	<div class="control-group">
								<label for="url" class="control-label"><b><em>*</em>URL</b></label>
						      	<div class="controls">
						        	<input type='text' autocomplete="off" class="required span4" name='url' id='url' value="${fieldValue(bean: multimedia, field: 'url')}" />
						      	</div>
					      	</div>
					      	<div class="control-group">
								<label for="note" class="control-label"><b>Description</b></label>
						      	<div class="controls">
						        	<textarea class="input-xlarge span4" name='note' id='note' rows="8">${multimedia.note?.content}</textarea>
						      	</div>
					      	</div>
					      	<div class="control-group">
								<label for="tags" class="control-label"><b>Tags</b></label>
						      	<div class="controls">
						      		<g:set var="tagStr" value=""/>
						      		<g:each in="${multimedia.tags}" var="t">
						      			<g:set var="tagStr" value="${tagStr+','+t}"/>
						      		</g:each>
						        	<input class="span4" name='tags' id='tags' value="${tagStr?.size() >0?tagStr.substring(1):tagStr}" />
						        	<span class="help-block">Please separate the tags by comma ","</span>
						      	</div>
					      	</div>
					      	<div class="control-group">
								<label for="perm" class="control-label"><b>Privacy and Publishing Settings</b></label>
						      	<div class="controls">
						      		<g:render template="/common/permission"
											model="[canPrivate:true, defaultPerm:multimedia.perm]" />
						      	</div>
					      	</div>
				      	</div>
				      	<!-- Add later -->
				      	<!--  
				      	<div class="span4">
				      		<div class="control-group">
							<label for="realStarttime" class="control-label"><b>Recording Start Time</b></label>
					      	<div class="controls">
					      		
					      	</div>
				      	</div>
				      	<div class="control-group">
							<label for="realStarttime" class="control-label"><b>Recording End Time</b></label>
					      	<div class="controls">
					      	</div>
				      	</div>-->
				      	<div class="span4">
				      		<div class="control-group">
								<label for="duration" class="control-label"><b>Duration</b></label>
						      	<div class="controls">
						      		<div class="input-append">
						      			<span class="span2 uneditable-input" id="duration_span">${duration}</span>
						      			<button class="btn btn-info" type="button" id="duration_button">Get duration</button>
						      		</div>
						      		<input type='hidden' class="required" name='duration' id='duration' value="${multimedia.duration}" />
						      	</div>
					      	</div>
					      	<div class="control-group">
								<label for="isVideo" class="control-label"><b>The recording is a video?</b></label>
						      	<div class="controls">
						      		<div class="input-append">
						      			<span class="span2 uneditable-input" id="isVideo_span">${multimedia.isVideo}</span>
						      			<button class="btn btn-info" type="button" id="isVideo_button">Is it a Video?</button>
						      		</div>
						      	</div>
						      	<input type='hidden' class="required" name='isVideo' id='isVideo' value="${multimedia.isVideo}" />
					      	</div>
					      	<div class="control-group">
								<label for="url" class="control-label"><b>Thumbnail Picture</b></label>
						      	<div class="controls">
						        	<img src="${thumbnail_src}" class="thumbnail-img"/><br/><br/>
						        	<button class="btn btn-info" type="button" id="thumbnail_button">Generate thumbnail</button>
						      	</div>
						      	<input type='hidden' class="required span4" name='thumbnail' id='thumbnail' value="${multimedia.thumbnail}" />
						    </div>
				      	</div>
					</fieldset>
					<div class="form-actions">
						<div class="pull-left">
			            	<input class="btn btn-primary" id="multimediaEditForm_submit" type="submit" value="Save" />
			            	<input class="btn" id="multimediaEditForm_reset" type="reset" value="Reset"/>
			            </div>
			          	<div class="pull-right"><input class="btn btn-danger" id="multimediaEditForm_reset" type="reset" value="Delete"/></div>
			        </div>
				</g:form>
			</div>
		</div>
	</div>
</div>
</body>
</html>

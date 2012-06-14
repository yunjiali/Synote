<html>
<head>
<title><g:message code="org.synote.resource.compound.multimediaResource.createinet.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<g:urlMappings/>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.maskedinput-1.3.min.js")}"></script>
<script type="text/javascript" src="${resource(dir: 'js/jquery', file: 'jquery.form.js')}"></script>
<script type="text/javascript" src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.9/jquery.validate.min.js"></script>
<script type="text/javascript" src="${resource(dir:'js',file:"util.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js',file:"synote-multimedia-service-client.js")}"></script>
<script type="text/javascript">
var mmServiceURL = "${mmServiceURL}";
var client = new SynoteMultimediaServiceClient(mmServiceURL);

function showMsg(msg,type)
{
	var msg_div = $("#error_msg_div");
	if(type == "error")
	{
		msg_div.html("<div class='alert alert-error'><button class='close' data-dismiss='alert'>x</button>"+msg+"</div>");
	}
	else if(type=="warning")
	{
		msg_div.html("<div class='alert'><button class='close' data-dismiss='alert'>x</button>"+msg+"</div>");
	}
	else
	{
		msg_div.html("<div class='alert alert-success'><button class='close' data-dismiss='alert'>x</button>"+msg+"</div>");
	}
}

function resetForm()
{
	$("#ugc_div").hide();
	$("#metadata_div").hide();
	$("#controls_div").hide();
	$("#duration_span").removeAttr("disabled").removeClass("disabled");
	$("#duration_span").closest(".control-group").removeClass("warning");
	$("#isVideo_true").removeAttr("disabled");
	$("#isVideo_false").removeAttr("disabled");

	$("#multimediaCreateForm .resetFields").clearFields();
}

$(document).ready(function(){

	$("#multimediaCreateForm").validate(
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
				required:true,
				digits:true
			},
			isVideo:{
				required:true
			}
		 },
		highlight: function(label) {
			$(label).closest('.control-group').addClass('error');
		},
	});

	$("#duration").mask("?99:99:99");
	
	$("#multimediaCreateForm").submit(function(){
		$("#multimediaCreateForm").ajaxSubmit({
			url:g.createLink({controller:"multimediaResource",action:"saveAjax"}),
			//resetForm:true,
			type:'post',
			dataType:'json',
			beforeSend:function(event)
			{
				$("#form_loading_div").show();
				if($("#duration").val() == "" && $("#duration_span").val() != "")
				{
					$("#duration").val(stringToMilisec$("#duration_span").val());
				}
			},
			success:function(data,textStatus, jqXHR, $form)
			{
				//console.log("status:"+status);
				if(data.success) //status == 200
				{
					$("#multimediaCreateForm_div").remove();
					var mmid = data.success.mmid;
					var edit_recording_url = $('#edit_recording_span a').attr('href')+"/"+mmid; 
					$('#edit_recording_span a').attr('href',edit_recording_url);
					var play_recording_url = $('#play_recording_span a').attr('href')+"/"+mmid;
					$('#play_recording_span a').attr('href',play_recording_url);
					$("#after_save_div").show(200);
					showMsg(data.success.description,null);
				}
				else if(data.error)
				{
					showMsg(data.error.description,"error");
				}
			},
			error:function(jqXHR,textStatus,errorThrown)
			{
				
				var resp =$.parseJSON(jqXHR.responseText);
				showMsg(resp.error.descrption,"error");
				alert("error!");
			},
			complete:function(jqXHR,textStatus)
			{
				$("#form_loading_div").hide();
			}
		});
		//Don't forget return false
		return false;
	});
	
	
	$("#url_submit_btn").click(function(){
		
		var url = $("#url").val();
		if(!isValidURL(url))
		{
			showMsg("The recording URL is not valid","error");
			return;
		}

		resetForm();
		
		$("#url_submit_btn").button('loading');
		$("#form_loading_div").show();
		client.getMetadata(url, function(data, errMsg){
			if(data != null)
			{
				$("#form_loading_div").hide();
				$("#ugc_div").show(200);
				$("#metadata_div").show(200);
				$("#controls_div").show(200);

				/*
				ytParser.getThumbnail(data,function(thumbnail_url,errorMsg){
					if(thumbnail_url != null)
					{
						$("#thumbnail_img").attr("src",thumbnail_url);
						$("#thumbnail").val(thumbnail_url);
					}
					else
					{
						$("#thumbnail_img").closest(".control-group").addClass("error");
						var oldHtml = $("#thumbnail_img").closest(".control-group").html();
						$("#thumbnail_img").closest(".control-group").html(oldHtml+"Cannot get the thumbnail picture for this video.");
					}
				});*/

				//Get duration
				if(data.durationsec !== undefined && data.durationsec != null)
				{
					var duration = parseInt(data.durationsec)*1000;
					$("#duration_span").val(milisecToString(duration));
					$("#duration_span").attr("disabled","disabled").addClass("disabled");
					$("#duration").val(duration);
				}
				else
				{
					$("#duration_span").closest(".control-group").addClass("warning");
					var oldHtml = $("#duration_span").closest(".controls").html();
					$("#duration_span").closest(".controls").html(oldHtml+"<p class='help-block'>Please manually enter the duration of the recording.</p>");
				}

				//Get isVideo
				//We are sure it is a video if the isVideo is true, but we cannot be certain if the isVideo field is false
				if(data.isVideo !== undefined && data.isVideo != null)
				{
					if(data.isVideo === true)
					{
						$("#isVideo_true").attr("checked","checked").attr("disabled","disabled");
						$("#isVideo_false").attr("disabled","disabled");
					}
				}

				//Add title if exists
				if(data.title !== undefined && data.title != null)
				{
					$("#title").val(title);
				}
			}
			else
			{
				showMsg(errMsg,"error");
				$("#form_loading_div").hide();
			}
			$("#url_submit_btn").button('reset');
		});
	});

	$("#multimediaCreateForm_reset").click(function(){
		resetForm();
	})
});
</script>
</head>
<body>
<div class="container">
	<div class="row">
		<div class="span2" id="user_nav_div">
			<g:render template="/common/userNav" model="['active':'recordings']"/>
		</div>
		<div class="span10" id="user_content_div">
			<h2 class="heading-inline"><g:message code="org.synote.resource.compound.multimediaResource.createinet.title" /></h2>
			<hr/>
			<g:render template="/common/message" model="[bean: multimediaResource]" />
			<div id="error_msg_div"></div>
			<div id="after_save_div" style="display:none;">
				<span id="edit_recording_span">
					<g:link controller="multimediaResource" action="edit" title="edit recording">Edit this recording's detail</g:link>
				</span>
				<br/>
				<span id="play_recording_span">
					<g:link controller="recording" action="replay" title="play recording">Play this recording</g:link>
				</span>
				<br/>
				<span>
					<g:link controller="user" action="index" title="My synote">Go to My Synote</g:link>
				</span>
			</div>
			<div id="form_loading_div" style="display:none;">
				<img id="form_loading_img" src="${resource(dir:'images/skin',file:'loading_64.gif')}" alt="loading"/>
			</div>
			<div id="multimediaCreateForm_div">
				<g:form method='POST' name='multimediaCreateForm'>
					<fieldset>
						<input type="hidden" name="rlocation" value="youtube" />
						<div class="control-group span9">
							<label for="url" class="control-label"><b><em>*</em>URL</b></label>
						    <div class="controls">
						    	<div class="input-append">
						       		<input type='text' autocomplete="off" class="required span6" name='url' id='url' />
						       		<button id="url_submit_btn" type="button" class="btn" data-loading-text="loading...">Inspect</button>
						        	<p class="help-block" style="color:#999">Please enter the URL of the recording from internet <br/>e.g. http://example.org/video/1.mp3</p>
						        </div>
						    </div>
					    </div>
						<div class="span5" id="ugc_div" style="display:none;"><!-- div for user generated content -->
					      	<div class="control-group">
								<label for="title" class="control-label"><b><em>*</em>Title</b></label>
						      	<div class="controls">
						        	<input type='text' autocomplete="off" class="required span4 resetFields" name='title' id='title'/>
						      	</div>
					      	</div>
					      	<div class="control-group">
								<label for="note" class="control-label"><b>Description</b></label>
						      	<div class="controls">
						        	<textarea class="input-xlarge span4 resetFields" name='note' id='note' rows="8" id="note"></textarea>
						      	</div>
					      	</div>
					      	<div class="control-group">
								<label for="tags" class="control-label"><b>Tags</b></label>
						      	<div class="controls">
						        	<input class="span4 resetFields" name='tags' id='tags' />
						        	<span class="help-block">Please separate the tags by comma ","</span>
						      	</div>
					      	</div>
					      	<div class="control-group">
								<label for="perm" class="control-label"><b>Privacy and Publishing Settings</b></label>
						      	<div class="controls">
						      		<g:render template="/common/permission" model="[canPrivate:true]" />
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
				      	<div class="span4" id="metadata_div" style="display:none;"> <!-- div for low level metadata -->
				      		<div class="control-group">
								<label for="duration" class="control-label"><b><em>*</em>Duration</b></label>
						      	<div class="controls">
						      		<input type="text" class="span2 resetFields" name="duration_span" id="duration_span"/>
						      		<input type='hidden' class="required resetFields" name='duration' id='duration'/>
						      	</div>
					      	</div>
					      	<div class="control-group">
								<label for="isVideo" class="control-label"><b><em>*</em>Video or Audio?</b></label>
						      	<div class="controls">
						      		<label class="radio">
						      			<input type="radio"	name="isVideo" value="true" id="isVideo_true"/>Video
						      		</label>
						      		<label class="radio">
						      			<input type="radio"	name="isVideo" value="false" id="isVideo_false" />Audio
						      		</label>
						      	</div>
					      	</div>
					      	<input type='hidden' class="span4" name='thumbnail' id='thumbnail' />
				      	</div>
					</fieldset>
					<div class="form-actions" id="controls_div" style="display:none;">
						<div class="pull-left">
			            	<input class="btn btn-primary" id="multimediaCreateForm_submit" type="submit" value="Create" />
			            	<input class="btn" id="multimediaCreateForm_reset" type="reset" value="Reset"/>
			            </div>
			        </div>
				</g:form>
			</div>
		</div>
	</div>
</div>
</body>
</html>

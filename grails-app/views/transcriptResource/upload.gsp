<html>
<head>
<title><g:message code="org.synote.resource.compound.TranscriptResource.list.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<g:urlMappings/>
<script type="text/javascript" src="${resource(dir: 'js/jquery', file: 'jquery.form.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js/jquery', file: 'jquery.validate-1.9.1.min.js')}"></script>
<script type="text/javascript">

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

$(document).ready(function(){

	$("#transcriptUploadingForm").validate(
	{
		rules: {
		    format: {
			    required:true
		    },
		    file:{
				required:true
		    }
		 },
		highlight: function(label) {
			$(label).closest('.control-group').addClass('error');
		},
	});

	
	$("#transcriptUploadingForm").submit(function(){
		$("#transcriptUploadingForm").ajaxSubmit({
			url:g.createLink({controller:"transcriptResource",action:"handleUpload"}),
			//resetForm:true,
			type:'post',
			enctype:"multipart/form-data",
			dataType:'json',
			beforeSend:function(event)
			{
				$("#form_loading_div").show();
				$("#warning_div").remove();
				$("#transcriptUploadingForm_submit").button("loading");
			},
			success:function(data,textStatus, jqXHR, $form)
			{
				//console.log("status:"+status);
				if(data.success) //status == 200
				{
					$("#transcriptUploadingForm_div").remove();
					//var mmid = data.success.mmid;
					//var edit_recording_url = $('#edit_recording_span a').attr('href')+"/"+mmid; 
					//$('#edit_recording_span a').attr('href',edit_recording_url);
					//var play_recording_url = $('#play_recording_span a').attr('href')+"/"+mmid;
					//$('#play_recording_span a').attr('href',play_recording_url);
					//$("#after_save_div").show(200);
					showMsg(data.success.description,null);
					$("#after_save_div").show(200);
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
				$("#transcriptUploadingForm_submit").button("reset");
				
			}
		});
		//Don't forget return false
		return false;
	});
});
</script>
</head>
<body>
<div class="container">
	<div class="row">
		<div class="span2" id="user_nav_div">
			<g:render template="/common/userNav" model="['active':'transcripts']"/>
		</div>
		<div class="span10" id="user_content_div">
			<h2 class="heading-inline"><g:link controller="reocording" action="replay" id="${multimedia.id}">${multimedia.title }</g:link>
			</h2>
			<hr/>
			<div id="warning_div" class="alert">
			  <button type="button" class="close" data-dismiss="alert">×</button>
			  <strong>Warning!</strong> The current transcript will be over-written once the uploading is finished.
			</div>
			<h3>Upload caption file or transcript</h3>
			<br/>
			<div id="error_msg_div"></div>
			<div id="after_save_div" style="display:none;">
				<span id="edit_recording_span">
					<g:link controller="multimediaResource" action="edit" id="${multimedia.id}" title="edit recording">Edit this recording's detail</g:link>
				</span>
				<br/>
				<span id="play_recording_span">
					<g:link controller="recording" id="${multimedia.id}" action="replay" title="play recording">Play this recording</g:link>
				</span>
			</div>
			<div id="transcriptUploadingForm_div">
				<g:form method='POST' name='transcriptUploadingForm'>
					<fieldset>
						<input type="hidden" name="mmid" value="${multimedia.id}" />
						<div class="span9">
							<input type="hidden" name="id" value="" />
							<label for="format" class="control-label"><b><em>*</em>Format:</b></label>
							<div class="control-group">
								<label class="radio">
									<input type="radio" value="srt" checked="checked" name="format" />
											SRT
								</label>
								<label class="radio">
									<input type="radio" value="webvtt" name="format" />
											WebVTT
								</label>
					      	</div>
					      	<label for="file" class="control-label"><b><em>*</em>File:</b></label>
					      	<div class="control-group">
								<input type="file" name="file"/>
					      	</div>
				      	</div>
					</fieldset>
					<div id="form_loading_div" style="display:none;">
						<img id="form_loading_img"	 src="${resource(dir:'images/skin',file:'loading_64.gif')}" alt="loading"/>
					</div>
					<div class="form-actions">
						<div class="pull-left">
			            	<input class="btn btn-primary" id="transcriptUploadingForm_submit" type="submit" value="Upload" data-loading-text="Uploading..." />
			            </div>
			        </div>
				</g:form>
			</div>
		</div>
	</div>
</div>
</body>
</html>

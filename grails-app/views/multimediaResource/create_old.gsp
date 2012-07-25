<%@page import="org.synote.resource.compound.MultimediaResource"%>
<%@page import="org.synote.resource.single.binary.MultimediaUrl"%>
<%@page import="org.synote.permission.PermissionValue"%>
<%@page import="org.synote.api.APIStatusCode" %>
<html>
<head>
<title><g:message
	code="org.synote.resource.compound.multimediaResource.create.title" /></title>
<meta name="layout" content="main" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform', file: 'uni-form.css')}" media="screen" charset="utf-8"/>
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform', file: 'default.uni-form.css')}" media="screen" charset="utf-8"/>
<style type="text/css">
	.uniForm .formHint { font-size: 100%; color: #777; }
	.uniForm ul li label {font-size:1.1em;}
	.ctrlHolder input[type="radio"]{position:relative; bottom:20px;}
	/*vertical align for checkbox, image and text*/
	#step-1 .ctrlHolder span {position:relative;bottom:20px;font-size:1.1em;font-weight:bold;}
</style>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form.jquery.min.js')}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form-validation.jquery.min.js')}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:'jquery.form.js')}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:'jquery.form.wizard-min.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js/jquery', file: 'jquery.combobox.js')}"></script>
<script type="text/javascript">
var form_div_array = ["internet","youtube","local","syn_xml"];

$(document).ready(function(){
	
	//init wizard
	$("form[name='create_form']").formwizard({ 
	 	formPluginEnabled: true,
	 	validationEnabled: false,
	 	focusFirstInput : true,
	 	formOptions :{
	 		beforeSerialize:function($form)
			{
				//Clear the unused form fields in step 1
				var selected_val = $("input[name='rlocation']:checked", ".uniForm").val();
				clearUnusedFormfields(selected_val);

				//And do the validation if possible
			},
			beforeSubmit: function(formData, jqForm, options)
			{
				//prepare to display, clear all the classes and text
				//$('#dialog-modal').wijdialog('destroy');
				$('#after_creating_div').hide();
				$('#msg_div').removeClass();
				$('#msg_icon_span').removeClass();	
				$('#msg_body_span').text("");
				//Yunjia: Maybe we should also clear $('#view_recording_span a') and $('#play_recording_span a'), but I won't do it now
				
				$('#dialog-modal').wijdialog('open');
				//console.log("before submit");
			},
			success: function(data)
			{
				//console.log("success call back");
				$('#progress_div').hide();
				$('#after_creating_div').show();
				var stat = $(data).find('status').text();
				var description = $(data).find('description').text();
				//console.log("status:"+stat);
				//console.log("description:"+description);
				if (parseInt(stat) != ${APIStatusCode.SUCCESS})
				{
					//console.log("error happens");
					$('#resubmit_span').show()
					$('#view_recording_span').hide();
					$('#play_recording_span').hide();
					$('#msg_div').addClass("ui-state-error ui-corner-all");
					$('#msg_icon_span').addClass("ui-icon ui-icon-alert");	
				}
				else
				{
					//console.log("no error");
					var mmid = $(data).find('multimedia').attr('id');
					//modify urls
					var view_recording_url = $('#view_recording_span a').attr('href')+"/"+mmid; 
					$('#view_recording_span a').attr('href',view_recording_url);
					var play_recording_url = $('#play_recording_span a').attr('href')+"/"+mmid;
					$('#play_recording_span a').attr('href',play_recording_url);
					
					$('#resubmit_span').hide();
					$('#view_recording_span').show();
					$('#play_recording_span').show();
					$('#msg_div').addClass("ui-state-highlight ui-corner-all");
					$('#msg_icon_span').addClass("ui-icon ui-icon-info");
				}
				$('#msg_body_span').text(description);
			},
			dataType: 'xml',
			resetForm: true
	 	}
	});

	$("#wizard_next").button();
	$("#wizard_back").button();

	//init form layout
	$("form.uniForm").uniform();
	var initVal = $("input[name='rlocation']:checked", ".uniForm").val();
	selectCheckBox(initVal);
	$("form.uniForm input[type='radio']").change(function(){
		selectCheckBox($(this).val());
	});

	//Yunjia: If we have time later we can add themems to dropdownlist fileupload and checkbox
	$(".textInput").wijtextbox();
	$("#description").wijtextbox();
	$(".combowrap select").combobox();
	
	//init the medal dialog
	 $("#dialog-modal").wijdialog({
		captionButtons:{
			pin:{visible:false},
			refresh:{visible:false},
			toggle:{visible:false},
			close:{visible:false}
		},
        autoOpen: false,
        height: 480,
        width: 640,
        modal: true
     });  

     //init permission help dialog		
     $( "#permission_help_div" ).wijdialog({
		    	captionButtons:{
		 			pin:{visible:false},
		 			refresh:{visible:false},
		 			toggle:{visible:false},
		 			close:{visible:true},
		 			minimize:{visible:false},
		 			maximize:{visible:false}
		 		},
    			height: 300,
    			width: 480,
    			modal: true,
    			title: "Permission Help",
    			autoOpen:false,
    			modal:true
    		});
		
     $("#permission_help_img").click(function(){
    	 //console.log("show permission help dialog");
    		$( "#permission_help_div" ).wijdialog("open");
    		
     });
     
	   
});

function clearUnusedFormfields(cbx_val)
{
	$.each(form_div_array, function(index, value){
		if(value != cbx_val)
		{
			$('#'+value+"_div input").clearFields();
			if(value=="local")
			{
				$("#local_file").replaceWith('<input class="fileUpload" type="file" name="file" id="local_file"/>');
			}
			else if(value=="syn_xml")
			{
				$("#syn_xml_file").replaceWith('<input class="fileUpload" type="file" name="syn_xml_file" id="syn_xml_file"/>');
			}
		}
	});
}

function selectCheckBox(cbx_val)
{
	$.each(form_div_array, function(index, value){
		if(value == cbx_val)
		{
			$('#'+cbx_val+"_div").show("fast");
		}
		else
		{
			$('#'+value+"_div").hide("fast");
		}
	});
};


</script>

</head>
<body>
<h1>Create Recording</h1>
<g:render template="/common/message" model="[bean: multimediaResource]" />
<!-- Yunjia: the div width is not correct it seems a little bit overflow -->
<!-- Yunjia: needs to do validation before submit -->
<div class="span-22 prepend-1 append-1" id="fieldWrapper">
	<g:render template="/common/message" model="[bean: multimediaResource]" />
	<g:form name="create_form" controller="multimediaResource" action="saveAjax" enctype="multipart/form-data" method="post" class="uniForm" style="padding:10px;">
		  	<div id="step-1" class="step">	
				<h2>Step 1: Select a audio or video file from:</h2>
				<div class="ctrlHolder">
					<input value="internet" type="radio" name="rlocation" />
					<img src="${resource(dir: 'images/skin', file: 'internet.png')}" alt="internet"/>
					<span>Internet</span>
					<div id="internet_div" class="inlineLabels" style="margin-top:20px;">
						<label for="internet_url"><em>*</em>Please enter a URL address:</label>
						<input class="textInput medium" type="text" id="internet_url" name="internet_url" size="30"
													value="${fieldValue(bean: multimediaResource?.url, field: 'url')}" />
						<p class="formHint">Please enter a valid url from internet</p>
					</div>
				</div>
				<div class="ctrlHolder">
					<input value="youtube" type="radio" name="rlocation" />
					<img src="${resource(dir: 'images/skin', file: 'youtube.png')}" alt="youtube"/>
					<span>Youtube</span>
					<div id="youtube_div" class="inlineLabels" style="margin-top:20px;">
						<!-- Yunjia: we can add youtube preview in this page -->
						<label for="youtube_url"><em>*</em>Please enter the URL of video from Youtube:</label>
						<input class="textInput medium" type="text" id="youtube_url" name="youtube_url" size="30"
													value="${fieldValue(bean: multimediaResource?.url, field: 'url')}" />
						<p class="formHint">Please enter a url for youtube video</p>
					</div>
				</div>
				<div class="ctrlHolder">
					<input value="local" type="radio" name="rlocation"/>
					<img src="${resource(dir: 'images/skin', file: 'upload_server.png')}" alt="upload"/>
					<span>Local Disk</span>
					<div id="local_div" class="inlineLabels" style="margin-top:20px;">
						<label for="local_file"><em>*</em>Please select a file from your local disk:</label>
						<input class="fileUpload" type="file" name="file" id="local_file"/>
					</div>
				</div>
				<g:viascribeXmlUploadEnabled>
				<div class="ctrlHolder">
					<input value="syn_xml" type="radio" name="rlocation"/>
					<img src="${resource(dir: 'images/skin', file: 'file_xml.png')}" alt="synchronised xml"/>
					<span>Synchronised XML</span>
					<div id="syn_xml_div" class="inlineLabels" style="margin-top:20px;">
						<label for="syn_xml_file"><em>*</em>Please select a synchronised xml file from your local disk:</label>
						<input class="fileUpload" type="file" name="syn_xml_file" id="syn_xml_file"/>
						<br/><br/>
						<label for="syn_xml_url"><em>*</em>Please input the URL of the directory which contains the multimedia files:</label>
						<input class="textInput medium" type="text" id="syn_xml_url" name="syn_xml_url" size="30"
													value="${fieldValue(bean: multimediaResource?.url, field: 'url')}" />
					</div>
				</div> 
				</g:viascribeXmlUploadEnabled>
			</div>
	  		<div id="step-2" class="step">
	  			<h2>Step 2: Recording Metadata</h2>	
		        <div class="ctrlHolder inlineLabels">
		        	<label for="title"><em>*</em>Recording Title:</label>
					<input class="textInput medium required" type="text" id="title" name="title" autocomplete="off" value="${fieldValue(bean: multimediaResource, field: 'title')}" />
					<p class="formHint">Give your recording a title</p>
		        </div>
				<div class="ctrlHolder inlineLabels">
					<label for="description">Recording Description:</label>
					<textarea id="description" name="description"
						value="${params?.description}" rows="5" cols="40" ></textarea>
				</div>
				<div class="ctrlHolder inlineLabels">
					<label for="tags">Recording tags:</label>
					<g:textField class="textInput medium" name="tags" value="${params?.tags}" />
					<p class="formHint">Please separate the tags by comma ","</p>
				</div>    
	        </div>                      
	  		<div id="step-3" class="step">
		    	<h2>Step 3: Add Permission and other</h2>	
		        <div class="ctrlHolder inlineLabels">
					<label for="perm">Select a permission:
						<img style="display:inline;" id="permission_help_img" src="${resource(dir: 'images/skin', file: 'info_16.png')}" alt="Help about permission"/>
					</label>
					<g:render template="/common/permission" model="[canPrivate:true, selectorClass:'selectInput medium']" />
					<p class="formHint">Select the permission for this recording in this group</p>
				</div>
				<div class="ctrlHolder inlineLabels">
		    		<label for="useIBMTrans">Transcribe this recording:</label>
					<g:if test="${isAllowedIPAddress && isIBMTransJobEnabled}">
						<input id="useIBMTrans" type="checkbox" name="useIBMTrans"/>
						<p class="formHint">Select the checkbox to transcribe the recording</p>
					</g:if>
					<g:elseif test="${!isAllowedIPAddress}">
						<span style="color:red;"><g:message code="org.synote.resource.compound.multimediaResource.create.useIBMTrans.notAllowIPAddress"/></span>
						<p class="formHint">Check the box to transcribe this recording</p>
					</g:elseif>
					<g:else>
						<span style="color:red;"><g:message code="org.synote.resource.compound.multimediaResource.create.useIBMTrans.notEnabled"/></span>
						<p class="formHint">Check the box to transcribe this recording</p>
					</g:else>	
		    	</div>      				          
	        </div>
			<div class="span-5 append-1 prepend-16 prepend-top append-bottom right" id="wizard_nav"> 							
				<input id="wizard_back" value="Back" type="reset" />
				<input id="wizard_next" value="Next" type="submit" />
			</div>
	</g:form>
	</div>
	<div id="dialog-modal" title="Create recording dialog">
		<!-- Yunjia: centering the div when displayed in the middle of the dialog -->
		<div id="progress_div" style="text-align:center;">
			<img style="display:block" src="${resource(dir: 'images', file: 'progress.gif')}" alt="progressing"/> Create recording...
		</div>
		<br/>
		<div id="msg">
			<p>
				<!-- Yunjia: Display the message icon and text inline, improve the style. The current style
					is wij default one -->
				<span id="msg_icon_span"></span>
				<span id="msg_body_span"></span>
			</p>
		</div>
		<div id="after_creating_div">
			<h2>Where would you like to go next?</h2>
			<hr/>
			<span id="resubmit_span">
				<a id="resubmit_a" href="#" onclick="$('#dialog-modal').wijdialog('close');" title="Review the information and resubmit">Review the information and re-submit</a>
				-&nbsp;If the recording creation is failed, you can go back to the creating page and re-submit it later
				<br/>
			</span>
			<span id="view_recording_span">
				<g:link controller="multimediaResource" action="show" title="view recording detail">View this recording's Detail</g:link>
				-&nbsp; Go to a page where the detail about this recording is listed<br/>
			</span>
			<span id="play_recording_span">
				<g:link controller="recording" action="replay_old" title="play recording">Play this recording</g:link>
				-&nbsp; Play this recording in synote player<br/>
			</span>
			<span>
				<g:link controller="user" action="index" title="My synote">My Synote</g:link>
				-&nbsp;Check on your recordings, groups and your user profile
			</span>
		</div>
	</div>
	<div id="permission_help_div">
			<strong>PRIVATE:</strong>Only the owner can watch the recording. He or she could edit the transcript, slides and add annotations.<br/>
   	     	<strong>READ:</strong>Everyone can watch the recording, but only owner can edit the transcript,slides and add annotations. <br/>
   	     	<strong>ANNOTATE:</strong>Everyone can watch the recording and add annotations, but only the owner can edit the transcript and slides<br/>
    		<strong>WRITE:</strong>Everyone can watch the recording, add annotations, edit transcript and slides.
    		<br/>
    		<br/>
    		<button style="float:right;" onclick="$('#permission_help_div').wijdialog('close');">Close</button>
	</div>
</body>
</html>

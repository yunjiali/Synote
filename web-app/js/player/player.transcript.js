/*
 * Define Transcript class
 * 
 * transcript_id is the index of cue in webvtt
 * TranscriptLineDiv.id = "transcript_"+cue.index
 * TranscriptLineDiv.transcript_id = cue.index
 * For example:
 * <div id="transcripts_content_div">
		<ol id="transcript_ol" style="list-style:none;">
			<li>
				<div id="transcri>
				</div>
			</li>
		</ol>
	</div>
	
	cueSettings are not implemented yet
 */
var Transcript = Base.extend({
	recording:null,
	outer_container:null,
	inner_container:null,
	transcripts:null, //jquery object
	transcriptsData:null,//a list of transcript data, json object serialised from webvtt file
	selectedTranscript:null, //jquery object
	synchronised:true,
	autoScroll:true,
	scrollOffset:-100,
	editingEnabled:false, //If editing is enabled, the resource should not be synchronised displayed
	newId:0,//Store the largest index number of webvtt transcript, which will be the new id of the next created transcript. 
			//It is used when creating new transcript in synote player
	selectionImage:null,//The selection image to create a synmark
	
	constructor:function(recording, outer_container, inner_container)
	{
		this.recording = recording;
		this.outer_container=outer_container;
		this.inner_container=inner_container;
	},
	initTranscript:function()
	{
		//Start loading transcript
		if(recording.canEdit)
		{
			transcript.initEditing();
		}
		$(document.body).mousedown(function() {
		    if(transcript.selectionImage) { 
		    	//transcript.selectionImage.unbind("click");
		    	transcript.selectionImage.fadeOut(); 
		    }
		});
	},
	sync:function(currentPosition)
	{
		//currentTranscript is a jquery object
		//console.log("sync tr");
		if(this.transcripts != null && this.transcripts.length>0)
		{
			var currentTranscript = this.getTranscript(currentPosition)
			//console.log("selectedTranscript:"+this.selectedTranscript);
			if(currentTranscript != this.selectedTranscript)
			{
				this.setTranscriptSelected(currentTranscript);
			}
		}
	},
	//Get the transcript div from the current position
	getTranscript:function(currentPosition)
	{
		
		if(this.transcripts == null)
		{
			return null;
		}
		
		var ct = null;
		for(var i=0;i<this.transcripts.length;i++)
		{
			var tt = parseInt($(this.transcripts[i]).attr("date-time-st"));
			if(tt>currentPosition)
			{
				break;
			}
			else
				ct= $(this.transcripts[i]);
		}
		//console.log("tr date-time-st:"+ct.attr("date-time-st"));
		return ct;
	},
	//get the webvtt json corresponding to the webvtt.index
	getTranscriptData:function(transcript_id)
	{
		var transcriptData = null;
		$.each(this.transcriptsData,function(i,cue){
			if(cue.index == transcript_id)
			{
				transcriptData = cue;
				return;
			}
		});
		return transcriptData;
	},
	//remove a cue with certain index from the transcriptsData
	removeFromTranscriptsData:function(transcript_id)
	{
		if(transcript.transcriptsData)
		{
			var index = null;
			$.each(transcript.transcriptsData,function(i,cue){
				if(cue.index == transcript_id)
				{
					index = i;
					return;
				}
			});
			if(index !== null)
			{
				transcript.transcriptsData.splice(index,1);
			}
		}
	},
	//Set currentTranscript_div as the selected transcript
	setTranscriptSelected:function(currentTranscript)
	{
		if(currentTranscript != null)
		{
			if(this.selectedTranscript != null)
				this.selectedTranscript.removeClass("transcript-selected");
			this.selectedTranscript = currentTranscript;
			currentTranscript.addClass("transcript-selected");
			if(this.autoScroll)
				this.inner_container.scrollTo(currentTranscript,400, {offset:this.scrollOffset});
		}
	},
	//set the currentTranscript_div as edited transcript
	setTranscriptEdited:function(currentTranscript)
	{
		currentTranscript.addClass("transcript-edited");
	},
	clickTranscript:function(currentTranscript) //Define what things should happen after clicking on a transcript line
	{
		this.setTranscriptSelected(currentTranscript);
		multimedia.setPosition(parseInt(currentTranscript.attr("date-time-st")));
	},
	createTranscriptLine:function(cue) //create the li line for a transcript block
	{
		if(cue == null)
			return false;
		//console.log("cue index:"+cue.index);
		var transcript_line_li = $("<li/>").attr("id","li_"+cue.index);
   		var transcript_line = $("<div/>",{
	   		id:"transcript_"+cue.index,
	   		mouseover:function(){$(this).addClass("transcript-highlight");},
			mouseout:function(){$(this).removeClass("transcript-highlight");},
			click:function(){
				transcript.clickTranscript($(this));
			}
	   	}).attr("date-time-st", cue.start)
	   	.attr("date-time-et",cue.end)
	   	.attr("transcript_id", cue.index).addClass("transcript_line").appendTo(transcript_line_li);
	   	
   		//MicroData: add Video or AudioObject to this transcript first and make the text as the http://schema.org/transcript
   		mdHelper.setMediaObject(transcript_line,recording.isVideo == 'true'?true:false);
   		mdHelper.setItemid(transcript_line,attachFragmentToURI(resourceBaseURI+recording.id,getFragmentString(cue.start,cue.end)));
   		
   		var transcript_line_time = $("<div/>",{
	   		text:milisecToString(cue.start)+" to "+milisecToString(cue.end)
	   	}).addClass("transcript-line-time").appendTo(transcript_line);
	   	
   		//find the speaker from cue, use name=v, type=object
	   	if(cue.cueText)
	   	{
	   		var speaker = transcript.getSpeakerFromCue(cue);
	   		if(speaker != null)
	   		{
	   			var transcript_line_speaker = $("<div/>",{
			   		text:speaker+":"
			   	}).addClass("transcript-line-speaker").appendTo(transcript_line);
	   		}
	   	}
	   	else
	   	{
	   		cue.cueText = "";
	   	}
	   	var transcript_line_text = $.trim(transcript.getAnnotatedTextFromCue(cue.cueText))
	   	var transcript_line_content = $("<div/>",{
   			html:transcript_line_text//only replace the first occurance
   		}).addClass("transcript_line_content").appendTo(transcript_line);
	   	//MicroData: add cueText as microdata transcript
	   	mdHelper.setItemprop(transcript_line_content,"transcript");
	    
	   	if(recording.canCreateSynmark === "true")
	   	{
	   		//Select a text block, users can create a synmark based on the selected text
	   		transcript_line_content.bind("mouseup",{cue:cue},function(e){
	   			var selection = getSelection(); //method in player.textselector.js
	   			//console.log("selected:"+selection);
	   			if(selection && (selection = new String(selection).replace(/^\s+|\s+$/g,'')))
	   			{
	   				if(!transcript.selectionImage) 
	   				{
	   	                //console.log("create image");
	   					transcript.selectionImage = $('<img/>',
	   	                {
		   	                src: "../../images/player/bookmark_add_16.png",
		   	                title: 'Create a synmark for the selected text',
		   	                id: 'transcript_create_synmark_img'
	   	                }).css({"cursor":"pointer","z-index":"9999999","opacity":"0.9"}).addClass("right").hide();
	   	                $(document.body).append(transcript.selectionImage);
	   				}
	   				
	   				//console.log("show image");
	   				transcript.selectionImage.insertBefore($(this));
	   				transcript.selectionImage.bind("click",{cue:e.data.cue,selection:selection},function(event){
	   	                //show synmark creation form
	   					//console.log("image click");
	   		        	if(event.data.cue != null)
	   		        	{
	   	                	$("#synmark_create_errorMsg").hide();
		   	 				synmark.fillSynmarkForm(milisecToString(cue.start),milisecToString(cue.end),"","",$.trim(event.data.selection),"");
		   	 				$("#synmark_create_div").show(400);
		   	 				$("html,body").animate({scrollTop:$("#synmarks_div").offset().top},400);
	   	                }
	   		        }).fadeIn(400);
	   			}
	   		});
	   	}
	   	
	   	return transcript_line_li;
	},
	//Init editing forms and buttons for transcript. The settings such as transcript.editingEnabled will be set in startEditing()
	initEditing:function()
	{
		//Init img buttons
		$("#edit_transcript_help_btn").click(function(){
			var url = g.createLink({controller:"recording",action:"help"});
			window.open(url+"#transcript_editing_help","synote player help");
		});
		
		$("#edit_transcript_delete_btn").click(function(){
			if(transcript.selectedTranscript == null)
			{
				alert('Please select a transcript block.');
				return;
			}
			
			if(confirm("Delete this transcript block?"))
			{
				transcript.deleteTranscript(transcript.selectedTranscript.attr("transcript_id"),function(msg,error){	
					if(error != null)
					{
						transcript.showMsg(msg,error);
					}
					else
					{
						transcript.showMsg(msg);
					}
				});
			}
		});
		
		$("#edit_transcript_add_btn").click(function(){
			
			var newTime = multimedia.getPosition();
			$("#transcript_st").val(milisecToString(newTime));
			$("#transcript_edit_div").show(400);
			$("#transcript_id").val("");
		});
		
		$("#edit_transcript_edit_btn").click(function(){
			
			if(transcript.selectedTranscript == null)
			{
				alert('Please select a transcript block.');
				return;
			}
			
			var transcriptData = transcript.getTranscriptData(transcript.selectedTranscript.attr("transcript_id"));
			if(transcriptData != null)
			{
				$("#transcript_st").val(milisecToString(transcriptData.start));
				$("#transcript_et").val(milisecToString(transcriptData.end));
				
				var speaker = transcript.getSpeakerFromCue(transcriptData);
				if(speaker)
					$("#transcript_speaker").val(speaker);
				
				$("#transcript_content").val(transcript.getAnnotatedTextFromCue(transcriptData.cueText));
				$("#transcript_id").val(transcriptData.index); //This is the id of srt index, not the id of transcript div
				
				$("#transcript_edit_errorMsg").hide();
				$("html,body").animate({scrollTop:$("#transcripts_div").offset().top},200);
				$("#transcript_edit_div").show(200);	

			}
		});
		
		//init transcript editing and creating form
		
		
		$("#transcript_st").mask("?99:99:99");
		$("#transcript_et").mask("?99:99:99");
		
		$("#transcript_st_time").click(function(){
			var currentPosition = multimedia.getPosition();
			//console.log("curpo:"+currentPosition);
			$("#transcript_st").val(milisecToString(currentPosition));
		});
		$("#transcript_st_add").click(function(){
			var oldTime = stringToMilisec($("#transcript_st").val());
			var newTime = oldTime+1000;
			$("#transcript_st").val(milisecToString(newTime));
			
		});
		$("#transcript_st_remove").click(function(){
			var oldTime = stringToMilisec($("#transcript_st").val());
			var newTime = oldTime-1000>0?oldTime-1000:0;
			$("#transcript_st").val(milisecToString(newTime));
		});
		$("#transcript_et_time").click(function(){
			var currentPosition = multimedia.getPosition();
			$("#transcript_et").val(milisecToString(currentPosition));
		});
		$("#transcript_et_add").click(function(){
			var oldTime = stringToMilisec($("#transcript_et").val());
			var newTime = oldTime+1000;
			$("#transcript_et").val(milisecToString(newTime));
		});
		$("#transcript_et_remove").click(function(){
			var oldTime = stringToMilisec($("#transcript_et").val());
			var newTime = oldTime-1000>0?oldTime-1000:0;
			$("#transcript_et").val(milisecToString(newTime));
		});
		
		$("#transcript_submit").click(function(){
			//Yunjia: valid transcript form first
			
			if(!$("#transcript_edit_form").valid())
			{
				transcript.showMsg("There are errors in the form","error");
				return;
			}
			
			if($("#transcript_et").val() == null || $.trim($("#transcript_et").val()).length == 0)
			{
				//console.log("abc");
				alert("End time for the transcript is required!");
				return false;
			}
			if($("#transcript_st").val() == null || $("#transcript_content").val()==null || $.trim($("#transcript_content").val()).length==0)
			{
				return false;
			}
			//If editing transcript
			if($("#transcript_id").val() != null && $.trim($("#transcript_id").val()).length>0)
			{
				var transcript_id = $("#transcript_id").val();
				transcript.updateTranscript(transcript_id,function(msg,error){
					if(error != null)
					{
						transcript.showMsg(msg,error);
					}
					else
					{
						transcript.showMsg(msg);
						transcript.exitEditing();
					}
				});
			}
			else //if creating a new transcript
			{
				transcript.createTranscript(function(msg,error){
					if(error != null)
					{
						transcript.showMsg(msg,error);
					}
					else
					{
						$("#transcript_edit_form").resetForm();
						transcript.showMsg(msg);
						//Do not hide the form, as users may want to add transcript again
						//Reset the start time to the current time
						var newTime = multimedia.getPosition();
						$("#transcript_st").val(milisecToString(newTime));
					}
				});
			}
		});
		$("#transcript_cancel").click(function(){
			$("#transcript_edit_form").resetForm();
			$("#transcript_edit_div").hide(400);
		});
		
		//validation for transcript_edit_form
		$("#transcript_edit_form").validate(
		{
			rules: {
			    transcript_st: {
				    required:true
			    },
			    transcript_et:{
					required:true
				},
				transcript_speaker:{
					required:false,
					maxlength:255
				},
				transcript_content:{
					required:true,
					maxlength:65535 //mysql text field, max length 65535
				}
			 },
			highlight: function(label) {
				$(label).closest('.control-group').addClass('error');
			},
		});
	},
	showMsg:function(msg,type) //display the message, could be error, or success
	{
		var msg_div = $("#transcript_msg_div");
		if(type == "error")
		{
			msg_div.html("<div class='alert alert-error'><button class='close' data-dismiss='alert'>x</button>"+msg+"</div>");
		}
		else
		{
			msg_div.html("<div class='alert alert-success'><button class='close' data-dismiss='alert'>x</button>"+msg+"</div>");
		}
	},
	empty:function()
	{
		//empty the data
		this.transcripts = null; 
		this.transcriptsData = null;
		this.selectedTranscript = null; 
		//empty the inner container
		$("#transcript_ol").empty();
		
	},
	exitEditing:function() //execute code when exit editing mode
	{
		$("#transcript_edit_form").resetForm();
		$("#transcript_edit_div").hide(400);
	},
	//Create a new transcript block according to the values in transcript_edit_form. 
	//Both transcript.transcripts and transcript.transcriptData will be updated
	createTranscript:function(callback)
	{
		//console.log("new");
		var cue = new Object();
		cue.start= stringToMilisec($("#transcript_st").val());
		cue.end = stringToMilisec($("#transcript_et").val());
		cue.cueText="";
		if($("#transcript_speaker").val() !== undefined && $("#transcript_speaker").val() !== "" && $.trim($("#transcript_speaker").val()).length>0)
			cue.cueText = "<v. "+$("#transcript_speaker").val()+">";
		cue.cueText += $("#transcript_content").val();
		cue.cueText += "</v>";
		cue.index = transcript.newId++;
		
		//Generate thumbnail picture
		if(recording.isVideo == 'true' && recording.thumbnail != 'null')
		{
			mmServiceClient.generateThumbnail(recording.url,recording.uuid, cue.start, cue.end, function(thumbnail_url, error){
				//We are not going to print out any error message here
				if(error == null)
				{
					cue.thumbnail = thumbnail_url;
					transcript.createTranscriptAjax(cue, callback)
				}
			});
		}
		else
		{
			transcript.createTranscriptAjax(cue,callback)
		}
	},
	/*The create transcript ajax function shared by both creating thumbnail picture and not creating thumbnail picture*/
	createTranscriptAjax:function(cue, callback)
	{
		var url = g.createLink({controller:"recording",action:"saveTranscriptAjax"});
		$.ajax({
			url:url,
			type:"post",
			data:{cue:JSON.stringify(cue),multimediaId:recording.id},
			dataType:"json",
			success:function(data,textStatus, jqXHR)
			{
				//console.log("status:"+status);
				if(data.success) //status == 200
				{
					//Add it to the data source
					if(data.success.cueId != null)
					{
						cue.id = parseInt(data.success.cueId);
					}
						
					if(transcript.transcriptsData == null)
					{
						//console.log("create new trans");
						$("#transcript_ol").empty();
						transcript.transcriptsData = new Array();
					}
					transcript.transcriptsData.push(cue);
					//find the previous div and insert the newly created div after it
					var transcript_line_li = transcript.createTranscriptLine(cue);
					var previous_div = transcript.getTranscript(cue.start);
					if(previous_div != null) //the newly created transcript can be inserted into somewhere
					{
						var previous_li = previous_div.parent();
						transcript_line_li.insertAfter(previous_li);
					}
					else //the newly created transcript is the first one on the transcript list
					{
						//console.log("add li");
						var transcript_ol = $("#transcript_ol");
						transcript_line_li.prependTo(transcript_ol);
					}
					transcript.setTranscriptEdited(transcript_line_li.children(":first"));
					//renew the transcripts divs
					transcript.transcripts = $(".transcript_line");
					callback(data.success.description,null);
					return;
				}
				else if(data.error)
				{
					//console.log("error save transcript");
					callback(data.error.description,"error");
					return;
				}	
			},
			error:function(jqXHR,textStatus,errorThrown)
			{
				var resp =$.parseJSON(jqXHR.responseText);
				callback(resp.error.description,"error");
				return;
			}
		});
	},
	//Update a transcript block according to the values in transcript_edit_form. Both transcript.transcripts and transcript.transcriptData will be updated
	updateTranscript:function(transcript_id,callback)
	{
		//console.log("replace");
		// this is the cue.index, not the transcript div id
		var url = g.createLink({controller:"recording",action:"saveTranscriptAjax"});
		var cue = transcript.getTranscriptData(transcript_id);
		if(cue == null)
		{
			callback("Cannot find the the transcript block.","error");
			return;
		}
		var newCue = new Object();
		newCue.start = stringToMilisec($("#transcript_st").val());
		newCue.end = stringToMilisec($("#transcript_et").val());
		newCue.cueText = "";
		newCue.index = cue.index;
		newCue.id = cue.id;
		if($("#transcript_speaker").val() !== undefined && $("#transcript_speaker").val() !== "" && $.trim($("#transcript_speaker").val()).length>0)
			newCue.cueText = "<v. "+$("#transcript_speaker").val()+">";
		newCue.cueText += $("#transcript_content").val();
		newCue.cueText += "</v>";
		//if the time has changed a lot, we need a new thumbnail picture
		var oldMiddle = (cue.start+cue.end)/2;
		//recording.thumbnail == null means the synote multimedia service cannot generate the thumbnail picture, so we give up
		//Here is a bug, if the user change the newly created transcript's time greatly, we would not be able to generate the thumbnail picture again, because
		//the cue.id is not available.
		if(cue.id != null && recording.thumbnail != 'null' && recording.isVideo == 'true' && (cue.thumbnail == null || (oldMiddle<newCue.start || oldMiddle > newCue.end))) //need to regenerate the thumbnail
		{
			var cueId = cue.id
			mmServiceClient.generateThumbnail(recording.url,recording.uuid, newCue.start, newCue.end, function(thumbnail_url, error){
				//We are not going to print out any error message here
				if(error == null)
				{
					var saveThumbnailURL = g.createLink({controller:'recording', action:'saveThumbnailAjax'});
					$.ajax({
						url:saveThumbnailURL,
						type:"post",
						data:{url:thumbnail_url,id:cueId},
						dataType:"json",
						success:function(data,textStatus,jqXHR)
						{
							//Do nothing
						},
						error:function(jqXHR,textStatus,errorThrown)
						{
							//Do nothing
						}
					});
				}
			});
		}
		
		$.ajax({
			url:url,
			type:"post",
			data:{cue:JSON.stringify(newCue),multimediaId:recording.id},
			dataType:"json",
			success:function(data,textStatus, jqXHR)
			{
				//console.log("status:"+status);
				if(data.success) //status == 200
				{
					var transcript_line_div_old = $("#transcript_"+transcript_id);
					var transcript_line_li_old = transcript_line_div_old.parent();
					var transcript_line_li_new = transcript.createTranscriptLine(newCue);
					transcript.setTranscriptEdited(transcript_line_li_new.children(":first"));
					//If the time doesn't change, replace the old li with the new one
					if(cue.start == newCue.start)
					{
						transcript_line_li_old.replaceWith(transcript_line_li_new);
					}
					else //find the right place to insert
					{
						
						var previous_div = transcript.getTranscript(newCue.start);
						if(previous_div != null) //the newly created transcript can be inserted into somewhere
						{
							var previous_li = previous_div.parent();
							transcript_line_li_new.insertAfter(previous_li);
						}
						else //the newly created transcript is the first one on the transcript list
						{
							var transcript_ol = $("#transcript_ol");
							transcript_line_li_new.prependTo(transcript_ol);
						}
						transcript.setTranscriptEdited(transcript_line_li_new.children(":first"));
						transcript_line_li_old.remove();
					}
					
					cue=newCue;
					//renew the transcripts divs
					transcript.transcripts = $(".transcript_line");
					callback(data.success.description,null);
					return;
				}
				else if(data.error)
				{
					//console.log("error save transcript");
					callback(data.error.description,"error");
					return;
				}	
			},
			error:function(jqXHR,textStatus,errorThrown)
			{
				var resp =$.parseJSON(jqXHR.responseText);
				callback(resp.error.description,"error");
				return;
			}
		});
	},
	//delete a transcript block.  Both transcript.transcripts and transcript.transcriptData will be updated
	deleteTranscript:function(transcript_id, callback)
	{
		var cue = transcript.getTranscriptData(transcript_id);
		if(cue == null)
		{
			callback("Cannot find the the transcript block.","error");
			return;
		}
		var deleteTranscriptURL = g.createLink({controller:'recording', action:'deleteTranscriptAjax'});
		$.ajax({
			url:deleteTranscriptURL,
			type:"post",
			data:{id:cue.id},
			dataType:"json",
			success:function(data,textStatus,jqXHR)
			{
				if(data.success)
				{
					transcript.removeFromTranscriptsData(transcript_id);
					var transcript_line_div = $("#transcript_"+transcript_id);
					//remove the parent li from transcript_ol
					transcript_line_div.parent().remove();
					transcript.transcripts = $(".transcript_line");
					callback("The transcript block has been successfully deleted.");
					return;
				}
			},
			error:function(jqXHR,textStatus,errorThrown)
			{
				var resp =$.parseJSON(jqXHR.responseText);
				callback(resp.error.description,"error");
				return;
			}
		});
		
		
	},
	//fill the transcript widget with data
	refreshTranscripts:function(data)
	{
		this.empty();//empty all the data and divs first
		this.newId = 0;
		
		if(!data || data.length == 0)
		{
			$("#transcript_ol").html("No transcript");
			return;
		}
		transcript.transcriptsData = data;
		//console.log("cue length:"+data.length);
		var transcript_ol = $("#transcript_ol");
		//sort by start time
		data = $(data).sort(sortCueByStartTime);
		$.each(data,function(i,cue){
			//console.log("cue");
			if(cue.cueText != null && $.trim(cue.cueText).length > 0)
			{
				//console.log("cue is not null");
				var transcript_line_li = transcript.createTranscriptLine(cue);
				transcript_line_li.appendTo(transcript_ol);
				if(parseInt(cue.index) > transcript.newId)
					transcript.newId = parseInt(cue.index);
			}
		});
		
		transcript.newId++;
		transcript.transcripts = $(".transcript_line");	
	},
	refresh:function(callback) //Add a callback function
	{
		var getTranscriptsURL = g.createLink({controller:'recording', action:'getTranscriptsAjax'});
		var transcripts_div = this.outer_container;
		var transcripts_content_div = this.inner_container;
		var multimediaId = this.recording.id;
		//transcripts_content_div.empty();
		
		$.ajax({
			   type: "GET",
			   url: getTranscriptsURL,
			   data:{multimediaId:multimediaId,type:"json"},
			   dataType: "json",
			   //Yunjia: Add a beforeSend function to display the loading message
			   beforeSend:function(jqXHR, settings)
			   {
				   $("#transcript_loading_div").show();
			   },
			   success:function(data)
			   {
				   transcript.refreshTranscripts(data);
			   },
			   complete:function(jqXHR, textStatus)
			   {
				   $("#transcript_loading_div").hide();
				   if(callback != null)
				   		callback(null);
			   }
		});
	},
	//Get the annotated text from cueText, including tags such as <i> <b>
	getAnnotatedTextFromCue:function(cueText)
	{
		return cueText.replace(/<v.+?>/gi,'');
	},
	//Get the plain text from cueText, including tags such as <i> <b>
	getPlainTextFromCue:function(cueText)
	{
		return transcript.getAnnotatedTextFromCue(cueText);
	},
	getSpeakerFromCue:function(cue)
	{
		var textParser = new WebVTTCueTextParser(cue.cueText,errorHandler); //no err handler
	   	var tree = textParser.parse(cue.start,cue.end); //the value tree
	   	var speaker = null;
	   	$.each(tree.children,function(i,child){
	   		if(child.name=="v" && child.type=="object")
	   		{
	   			//console.log("speaker");
	   			speaker = child.value;
	   			return false;
	   		}
	   	});	
	   	return speaker;
	}
});

var sortCueByStartTime = function(a,b)
{
	return a.start > b.start?1:-1;
}
//This function is used for webvtt.parser.js
var errorHandler = function(message,pos)
{
	//console.log("position:"+pos+",error:"+message);
}


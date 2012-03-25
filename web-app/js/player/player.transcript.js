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
		//this.refresh(this.outer_container,this.inner_container);
		//Yunjia: we need to download srt or webvtt. Not every player supports webvtt now.
		$("#edit_transcript_export_img").click(function(){
			var url = g.createLink({controller:"recording",action:"downloadTranscriptAsWebVTT",params:{multimediaId:recording.id}});
			//transcript.downloadTranscriptAsSRT();
			window.open(url,"Download Transcript");
		});
		
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
	//merge two transcriptData together
	mergeTranscriptData:function(prevData,mainData)
	{
		if(prevData != null)
		{
			//cue.start = prevData.start;
			prevData.end = mainData.end;
			prevData.cueText = transcript.getAnnotatedTextFromCue(prevData.cueText)+" "+transcript.getAnnotatedTextFromCue(mainData.cueText);
			var prevSpeaker = transcript.getSpeakerFromCue(prevData);
			var mainSpeaker = transcript.getSpeakerFromCue(mainData);
			prevData.cueText = "<v. "+prevSpeaker?prevSpeaker:(mainSpeaker?mainSpeaker:null)+">"+prevData.cueText;
			this.removeFromTranscriptsData(mainData.index);
			return prevData;
		}
		else
			return null;
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
				this.selectedTranscript.removeClass("transcript_selected");
			this.selectedTranscript = currentTranscript;
			currentTranscript.addClass("transcript_selected");
			if(this.autoScroll)
				this.inner_container.scrollTo(currentTranscript,400, {offset:this.scrollOffset});
		}
	},
	//set the currentTranscript_div as edited transcript
	setTranscriptEdited:function(currentTranscript)
	{
		currentTranscript.addClass("transcript_edited");
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
	   		mouseover:function(){$(this).addClass("transcript_highlight");},
			mouseout:function(){$(this).removeClass("transcript_highlight");},
			click:function(){
				transcript.clickTranscript($(this));
			}
	   	}).attr("date-time-st", cue.start)
	   	.attr("date-time-et",cue.end)
	   	.attr("transcript_id", cue.index).addClass("transcript_line").appendTo(transcript_line_li);
	   	
   		//MicroData: add Video or AudioObject to this transcript first and make the text as the http://schema.org/transcript
   		mdHelper.setMediaObject(transcript_line,factory.isAudio);
   		mdHelper.setItemid(transcript_line,attachFragmentToURI(resourceBaseURI+recording.id,getFragmentString(cue.start,cue.end)));
   		
   		var transcript_line_time = $("<div/>",{
	   		text:milisecToString(cue.start)+" to "+milisecToString(cue.end)
	   	}).addClass("transcript_line_time").appendTo(transcript_line);
	   	
   		//find the speaker from cue, use name=v, type=object
	   	if(cue.cueText)
	   	{
	   		var speaker = transcript.getSpeakerFromCue(cue);
	   		if(speaker != null)
	   		{
	   			var transcript_line_speaker = $("<div/>",{
			   		text:speaker+":"
			   	}).addClass("transcript_line_speaker").appendTo(transcript_line);
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
		$("#edit_transcript_help_img").click(function(){
			var url = g.createLink({controller:"recording",action:"help"});
			window.open(url+"#transcript_editing_help","synote player help");
		});
		$("#edit_transcript_quit_img").click(function(){
			if(confirm("All the changes will be discared?"))
			{
				transcript.exitEditing();
			}
		});
		$("#edit_transcript_save_exit_img").click(function(){
			//pass the cue json objects to server and save it to database
			//I can't put it in a separate method as transcript.exitEditing() must be done when the response is success!
			if(confirm("Are you sure you want to save all the changes? The old transcript will not be available and the saved draft will also" +
					"be removed."))
			{
				var url = g.createLink({controller:"recording",action:"saveTranscriptAjax"});
				$.ajax({
					url:url,
					type:"post",
					data:{transcripts:JSON.stringify(transcript.transcriptsData),multimediaId:recording.id},
					dataType:"text",
					beforeSend:function(x){
						if (x && x.overrideMimeType) {
							x.overrideMimeType("application/json;charset=UTF-8");
				        }
						$("#transcript_edit_errorMsg").hide();
					},
					success:function(responseText)
					{
						var respJson = $.parseJSON(responseText);
						//console.log("status:"+status);
						if(respJson.success) //status == 200
						{
							transcript.showDialog(respJson.success.description);
							transcript.exitEditing();
						}
						else if(respJson.error)
						{
							//console.log("error save transcript");
							var errMsg = respJson.error.description
							if(errMsg && errMsg.length > 50)
								errMsg = errMsg.substring(0,49)+"...";
							$("#transcript_edit_errorMsg").text(errMsg);
							$("#transcript_edit_errorMsg").show();
						}
					}
				})
			}
		});
		$("#edit_transcript_save_draft_img").click(function(){
			transcript.saveTranscriptDraft();
		});
		$("#edit_transcript_clear_img").click(function(){
			if(confirm("Remove all the current transcript?"))
			{
				transcript.empty();
			}
		});
		
		$("#edit_transcript_revert_img").click(function(){
			if(confirm("Are you sure you want to discard any change and restore the transcript from the draft?"))
			{
				var url = g.createLink({controller:"recording",action:"getTranscriptDraftAjax"});
				$.ajax({
					url:url,
					type:"post",
					data:{multimediaId:recording.id},
					dataType:"json",
					success:function(data)
					{
						//var respJson = $.parseJSON(responseText);
						//console.log("status:"+status);
						if(data.error)
						{
							transcript.showDialog(data.error.description);
						}
						else
						{
							transcript.refreshTranscripts(data);
							transcript.showDialog("The transcript has been reverted.");
						}
					}
				});
			}
		});
		$("#edit_transcript_import_img").click(function(){
			if($("#transcript_edit_div").is(":visible"))
			{
				$("#transcript_edit_form").resetForm();
				$("#transcript_edit_div").hide();
			}
			$("#transcript_upload_div").show(400);
			$("#transcript_upload_errorMsg").hide();
		});
		$("#edit_transcript_add_img").click(function(){
			if($("#transcript_upload_div").is(":visible"))
			{
				$("#transcript_upload_form").resetForm();
				$("#transcript_upload_div").hide();
			}
			var newTime = multimedia.getPosition();
			$("#transcript_st").val(milisecToString(newTime));
			$("#transcript_edit_div").show(400);
			$("#transcript_edit_errorMsg").hide();
			$("#transcript_id").val("");
		});
		
		//init transcript editing and creating form
		$("#transcripts_div .uniForm").uniform();
		$("#transcripts_div .uniForm input[type=text]").wijtextbox();
		
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
		
		$("#transcript_submit").button().click(function(){
			//Yunjia: valid transcript form first
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
			//If editing or splitting transcript
			if($("#transcript_id").val() != null && $.trim($("#transcript_id").val()).length>0)
			{
				var split_text = $("#transcript_split_content").val();
				var original_text = $("#transcript_content").val();
				if(split_text !=null && $.trim(split_text).length>0)
				{
					transcript.createSplitTranscript();
					var range = $("#transcript_content").getSelection();
					//remove the selected text from the original transcript_content
					var splitted_content = original_text.substring(0,range.start)+original_text.substring(range.end);
					//reset the transcript_content
					$("#transcript_content").val(splitted_content);
					$("#transcript_split_content").val("");
					//Set the start time equals the end time of the splitted transcript block, so that the transcript_content is ready
					//to be splitted again.
					$("#transcript_st").val($("#transcript_et").val());
					var transcript_id = $("#transcript_id").val();
					transcript.updateTranscript(transcript_id);
					transcript.showDialog("The transcript has been successfully split.");
					//We will not reset tht form unless you click exit
				}
				else
				{
					var transcript_id = $("#transcript_id").val();
					transcript.updateTranscript(transcript_id);
					transcript.showDialog("The transcript has been successfully updated.");
					$("#transcript_edit_form").resetForm();
					$("#transcript_edit_div").hide(400);
				}
			}
			else //if creating a new transcript
			{
				transcript.createTranscript();
				transcript.showDialog("The transcript has been successfully added.");
				$("#transcript_edit_form").resetForm();
				//Do not hide the form, as users may want to add transcript again
				//Reset the start time to the current time
				var newTime = multimedia.getPosition();
				$("#transcript_st").val(milisecToString(newTime));
			}
		});
		$("#transcript_cancel").button().click(function(){
			$("#transcript_edit_form").resetForm();
			$("#transcript_edit_div").hide(400);
			if($("#transcript_split_div").is(':visible'))
			{
				transcript.exitTranscriptSpliting();
			}
		});
		
		//init file upload form
		$("#transcript_file_submit").button().click(function(){
			
		});
		$("#transcript_file_cancel").button().click(function(){
			$("#transcript_edit_div").hide(400);
		});
	},
	showDialog:function(msg) //Show the transcript_edit_dialog with the msg content
	{
		$("#transcript_edit_dialog").text(msg);
		$("#transcript_edit_dialog").wijdialog({
            autoOpen: true,
            height: 180,
            width: 400,
            modal: true,
            buttons: {
                Ok: function () {
                    $(this).wijdialog("close");
                }
            },
            captionButtons: {
                pin: { visible: false },
                refresh: { visible: false },
                toggle: { visible: false },
                minimize: { visible: false },
                maximize: { visible: false }
            }
        });
	},
	//Things should been done when you start editing
	startEditing:function()
	{
		//show the tooltip to tell users there are right click menus availabel
		$("#transcript_edit_wrapper_div").wijtooltip("show");
		setTimeout(function(){$("#transcript_edit_wrapper_div").wijtooltip("hide");},5000);
		initTranscriptClickMenu(".transcript_line"); //method in player.transcript.click.menu.js
		transcript.editingEnabled=true;
		transcript.synchronised=false; //Disable the auto synchronisation display when editing, as the transcript divs are changing
		$("#transcript_sync_cb").prop('disabled',true);
	},
	exitEditing:function() //execute code when exit editing mode
	{
		disableTranscriptClickMenu(".transcript_line");
		$("#transcript_edit_menu_div").hide();
		if($("#transcript_edit_div").is(":visible"))
			$("#transcript_edit_div").hide();
		if($("#transcript_upload_div").is(":visible"))
			$("#transcript_upload_div").hide();
		$("#transcript_edit_enter_div").show(400);
		transcript.editingEnabled = false;
		//refresh to get the saved transcript
		transcript.refresh();
	},
	//init the editing form for transcript spliting
	initTranscriptSpliting:function()
	{
		$("#transcript_split_div").show();
		//clear the text in transcript_split_content
		$("#transcript_split_content").val("");
		//$("#transcript_content").attr("disabled","disabled");
		$("html,body").animate({scrollTop:$("#transcripts_div").offset().top},400);
		$("#transcript_edit_div").show(200);
		
		//init the fieldselector
		$("#transcript_content").bind("keydown keyup mousedown mousemove mouseup", function(event){
			//change the split textarea everytime the transcript_content textarea selection is changed
			//console.log("update");
			var range = $("#transcript_content").getSelection();
			$("#transcript_split_content").val(range.text);
		});
	},
	exitTranscriptSpliting:function()
	{
		//unbind events for transcript_content
		$("#transcript_content").unbind("keydown keyup mousedown mouseup mousemove");
		//$("#transcript_content").removeAttr("disabled");
		$("#transcript_split_div").hide();
	},
	//pass the cue json objects to server and save it as a draft
	saveTranscriptDraft:function() 
	{
		//the json is in cue format, i.e. cue.index, cue.start, cue.end, etc. So on the server side, you can easily change the json to real
		//.vtt file. Then you can download it.
		var url = g.createLink({controller:"recording",action:"saveTranscriptDraftAjax"});
		$.ajax({
			url:url,
			type:"post",
			data:{transcripts:JSON.stringify(transcript.transcriptsData),multimediaId:recording.id},
			dataType:"text",
			beforeSend:function(x){
				if (x && x.overrideMimeType) {
					x.overrideMimeType("application/json;charset=UTF-8");
		        }
				$("#transcript_edit_errorMsg").hide();
			},
			success:function(responseText)
			{
				var respJson = $.parseJSON(responseText);
				//console.log("status:"+status);
				if(respJson.success) //status == 200
				{
					transcript.showDialog(respJson.success.description);
				}
				else if(respJson.error)
				{
					$("#transcript_edit_errorMsg").text(respJson.error.description);
					$("#transcript_edit_errorMsg").show();
				}
			}
		});
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
	//Create a new transcript block according to the values in transcript_edit_form. Both transcript.transcripts and transcript.transcriptData will be updated
	createTranscript:function()
	{
		//console.log("new");
		var cue = new Object();
		cue.start= stringToMilisec($("#transcript_st").val());
		cue.end = stringToMilisec($("#transcript_et").val());
		cue.cueText="";
		if($("#transcript_speaker").val() !== undefined && $("#transcript_speaker").val() !== "" && $.trim($("#transcript_speaker").val()).length>0)
			cue.cueText = "<v. "+$("#transcript_speaker").val()+">";
		cue.cueText += $("#transcript_content").val();
		cue.index = transcript.newId++;
		//Add it to the data source
		if(transcript.transcriptsData == null)
		{
			//console.log("create new trans");
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
	},
	//Update a transcript block according to the values in transcript_edit_form. Both transcript.transcripts and transcript.transcriptData will be updated
	updateTranscript:function(transcript_id)
	{
		//console.log("replace");
		// this is the cue.index, not the transcript div id
		var cue = transcript.getTranscriptData(transcript_id);
		if(cue != null)
		{
			cue.start= stringToMilisec($("#transcript_st").val());
			cue.end = stringToMilisec($("#transcript_et").val());
			cue.cueText="";
			if($("#transcript_speaker").val() !== undefined && $("#transcript_speaker").val() !== "" && $.trim($("#transcript_speaker").val()).length>0)
				cue.cueText = "<v. "+$("#transcript_speaker").val()+">";
			cue.cueText += $("#transcript_content").val();
			var transcript_line_div_old = $("#transcript_"+transcript_id);
			var transcript_line_li_old = transcript_line_div_old.parent();
			var transcript_line_li_new = transcript.createTranscriptLine(cue);
			//console.log("firstchild:"+transcript_line_li_new.children(":first").length);
			transcript.setTranscriptEdited(transcript_line_li_new.children(":first"));
			transcript_line_li_old.replaceWith(transcript_line_li_new);
			
			//renew the transcripts divs
			transcript.transcripts = $(".transcript_line");
		}
		else
		{
			//Yunjia: do something if cue is not found?
		}
	},
	//delete a transcript block.  Both transcript.transcripts and transcript.transcriptData will be updated
	deleteTranscript:function(transcript_id)
	{
		this.removeFromTranscriptsData(transcript_id);
		var transcript_line_div = $("#transcript_"+transcript_id);
		//remove the parent li from transcript_ol
		transcript_line_div.parent().remove();
		transcript.transcripts = $(".transcript_line");
	},
	//create a new transcript block from spliting action. The difference between this method and createTranscript is that
	//this method use transcript_split_content textarea instead of transcript_content textarea to create a new transcript block
	createSplitTranscript:function()
	{
		//console.log("new split");
		var cue = new Object();
		cue.start= stringToMilisec($("#transcript_st").val());
		cue.end = stringToMilisec($("#transcript_et").val());
		if($("#transcript_speaker").val() != null && $.trim($("#transcript_speaker").val()).length>0)
			cue.cueText = "<v. "+$("#transcript_speaker").val()+">";
		cue.cueText += $("#transcript_split_content").val();
		cue.index = transcript.newId++;
		//Add it to the data source
		if(transcript.transcriptsData == null)
			transcript.transcriptsData = new Array();
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
			var transcript_ol = $("#transcript_ol");
			transcript_line_li.prependTo(transcript_ol);
		}
		transcript.setTranscriptEdited(transcript_line_li.children(":first"));
		//renew the transcripts divs
		transcript.transcripts = $(".transcript_line");
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
	refresh:function()
	{
		var getTranscriptsURL = g.createLink({controller:'recording', action:'getTranscriptsAjax'});
		var transcripts_div = this.outer_container;
		var transcripts_content_div = this.inner_container;
		var multimediaId = this.recording.id;
		//transcripts_content_div.empty();
		
		//Yunjia: it's difficult to caculate the height of transcript to be honest, so I will use fixed height for transcript_content_div
		var transcript_content_div_height = window.screen.height - multimedia.height; 
		//(window.screen.height- multimedia.height-223)>0?window.screen.height- multimedia.height-223:windows.screen.height;
		//console.log("window:"+ window.screen.height);
		//console.log("player:"+ multimedia.height);
		transcripts_content_div.height(transcript_content_div_height);
		
		$.ajax({
			   type: "GET",
			   url: getTranscriptsURL,
			   data:{multimediaId:multimediaId,type:"json"},
			   dataType: "json",
			   //Yunjia: Add a beforeSend function to display the loading message
			   success:function(data)
			   {
				   transcript.refreshTranscripts(data);
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

//This function is used for webvtt.parser.js
var errorHandler = function(message,pos)
{
	//console.log("position:"+pos+",error:"+message);
}
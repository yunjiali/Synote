function initTranscriptClickMenu(selector)
{
	//yunjia: have to put control in it. Only owners can edit and delete the synmark
	$.contextMenu({
        selector: selector, 
        trigger: 'right', 
        //ignoreRightClick: true,
        items: {
        	play: {name: "Play", icon: "play", callback: transcriptMenuPlay},
            edit: {name: "Edit", icon: "edit", callback: transcriptMenuEdit},
            "merge":  {name: "Merge with Previous", icon:"merge", callback:transcriptMenuMerge},
            split:{name:"Split",icon:"split", callback:transcriptMenuSplit},
            "delete": {name: "Delete", icon: "delete", callback:transcriptMenuDelete },
            sep1: "---------",
            quit: {name: "Quit", icon: "quit", callback: $.noop}
        }
    });
}

//functions to initialise the editing form
function disableTranscriptClickMenu(selector)
{
	$.contextMenu('destroy', selector);
}

function transcriptMenuPlay(key,opt)
{
	var transcript_line_div = (opt.$trigger).attr("id");
	transcript.clickTranscript($("#"+transcript_id_div));
}

function transcriptMenuEdit(key,opt)
{
	var transcript_id = (opt.$trigger).attr("transcript_id");
	var transcriptData = transcript.getTranscriptData(transcript_id); //transcript data is the cue
	//Open the edit widget
	if(transcriptData!=null)
	{
		$("#transcript_st").val(milisecToString(transcriptData.start));
		$("#transcript_et").val(milisecToString(transcriptData.end));
		
		var speaker = transcript.getSpeakerFromCue(transcriptData);
		if(speaker)
			$("#transcript_speaker").val(speaker);
		
		$("#transcript_content").val(transcript.getAnnotatedTextFromCue(transcriptData.cueText));
		$("#transcript_id").val(transcriptData.index); //This is the id of srt index, not the id of transcript div
		
		$("#transcript_edit_errorMsg").hide();
		$("html,body").animate({scrollTop:$("#transcripts_div").offset().top},400);
		$("#transcript_edit_div").show(200);	
	}
}

function transcriptMenuMerge(key,opt)
{
	var transcript_id = (opt.$trigger).attr("transcript_id");
	var transcript_line_li = $("#transcript_"+transcript_id).parent();
	var transcriptData = transcript.getTranscriptData(transcript_id);
	if(transcript_line_li.attr("id") == $("#transcript_ol").children(":first").attr("id"))
	{
		transcript.showDialog("This is the first text block! No previous text block is available.");
		return
	}
	
	var pre_transcript_line_li = transcript_line_li.prev();
	//console.log("prev:"+pre_transcript_line_li);
	if(pre_transcript_line_li != null)
	{
		var pre_transcriptData = transcript.getTranscriptData(pre_transcript_line_li.children(":first").attr("transcript_id"));
		var newTranscriptData = transcript.mergeTranscriptData(pre_transcriptData,transcriptData);
		//create a new transcript line, replace the previous transcript line and then remove 
		var new_transcript_line_li = transcript.createTranscriptLine(newTranscriptData);
		pre_transcript_line_li.replaceWith(new_transcript_line_li);
		transcript.deleteTranscript(transcript_id);
		//transcript.showDialog("The transcript blocks has been marged successfully.");
	}
}

function transcriptMenuSplit(key,opt)
{
	var transcript_id = (opt.$trigger).attr("transcript_id");
	var transcriptData = transcript.getTranscriptData(transcript_id);
	//Open the edit widget
	if(transcriptData!=null)
	{
		$("#transcript_st").val(milisecToString(transcriptData.start));
		$("#transcript_et").val(milisecToString(transcriptData.end));
		
		var speaker = transcript.getSpeakerFromCue(transcriptData);
		if(speaker)
			$("#transcript_speaker").val(speaker);
		
		$("#transcript_content").val(transcript.getAnnotatedTextFromCue(transcriptData.cueText));
		$("#transcript_id").val(transcriptData.index); //This is the id of srt index, not the id of transcript div
		
		$("#transcript_edit_errorMsg").hide();
		
		transcript.initTranscriptSpliting();
	}
}

function transcriptMenuDelete(key,opt)
{
	if(confirm("Are you sure to delete this transcript block?"))
	{
		var transcript_id = (opt.$trigger).attr("transcript_id"); //the srt.index, not the id of transcript_div
		transcript.deleteTranscript(transcript_id);
	}
	
}
$(document).ready(function(){
	//If the initial screen size is small, move transcript div into tabs
	if($(window).width() <=767)
	{
		moveTranscriptToTabs();
	}
});

$(window).bind('resize',function() {
	 	if($(window).width()<=767){
	 		if($("#col_right_div #transcripts_div").length == 0 && $("#col_left_div #transcripts_div").length > 0) //if transcripts_div is in the left column, move it to the right column
	 		{
	 			moveTranscriptToTabs();
	 		}
	 	}
	 	if($(window).width() >767)
	 	{
	 		if($("#col_right_div #transcripts_div").length> 0 && $("#col_left_div #transcripts_div").length == 0) // if transcripts_div is in the tab list with synmarks and slides
	 		{
	 			detachTranscriptFromTabs();
	 		}
	 	}
});

function moveTranscriptToTabs()
{
	var transcripts_div = $("#col_left_div #transcripts_div").detach();
	transcripts_div.addClass("tab-pane").addClass("span-transcripts");
	$("#tab_content_div").append(transcripts_div);
}

function detachTranscriptFromTabs()
{
	var transcripts_div = $("#tab_content_div #transcripts_div").detach();
	transcripts_div.removeClass("tab-pane").removeClass("span-transcripts");
	$("#col_left_div").append(transcripts_div);
}
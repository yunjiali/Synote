$(document).ready(function(){
	//If the initial screen size is small, move transcript div into tabs
	if($(window).width() <=767)
	{
		moveLeftSpanToTabs();
	}
});

$(window).bind('resize',function() {
	 	if($(window).width()<=767){
	 		if($("#col_right_div .span-left").length == 0 && $("#col_left_div .span-left").length > 0) //if transcripts_div is in the left column, move it to the right column
	 		{
	 			moveLeftSpanToTabs();
	 		}
	 	}
	 	if($(window).width() >767)
	 	{
	 		if($("#col_right_div .span-left").length> 0 && $("#col_left_div .span-left").length == 0) // if transcripts_div is in the tab list with synmarks and slides
	 		{
	 			detachLeftSpanFromTabs();
	 		}
	 	}
});

function moveLeftSpanToTabs()
{
	var transcripts_div = $("#col_left_div .span-left").detach();
	//transcripts_div.addClass("tab-pane").addClass("span-left");
	$("#tab_content_div").append(transcripts_div);
}

function detachLeftSpanFromTabs()
{
	var transcripts_div = $("#tab_content_div .span-left").detach();
	//transcripts_div.removeClass("tab-pane").removeClass("span-transcripts");
	$("#col_left_div").append(transcripts_div);
}
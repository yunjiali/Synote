/*
 * This file is for starting and playing functions for Synote Player
 */
var factory;
var multimedia;
var synmark;
var transcript;
var presentation;
var timer;

function initSynotePlayer(recording)
{
	var multimediaId = recording.id
	//default settings of screen: search bar is hidden
	$("#search_bar_div").hide();
	$("#player_help_btn").button({
		icons:{
			primary:"ui-icon-info"
		}
	}).click(function(){
		var url = g.createLink({controller:"recording",action:"help"});
		window.open(url,"Synote Player Help");
	});
	$("#settings_btn").button({
		icons:{
			primary:"ui-icon-gear",
			secondary:"ui-icon-carat-1-s"
		}
	});
	$("#settings_menu_ul").wijmenu({
		orientation:'vertical',
		trigger:"#settings_btn",
		triggerEvent:"click",
		position:{my:"left top",at:"left bottom"},
		animation:{animated:"slide",option:{direction:"left"}}
	});
	
	//alert("1");
	factory = new MultimediaFactory(recording);
	//alert("1.1");
	multimedia = factory.getMultimediaPlayer(recording,$("#recording_content_div"),$("#multimedia_player_div"));
	if(multimedia != null)
	{
		ctrler = new MediaFragmentController();
		//MicroData:Change MediaObject type to VideoObject
		mdHelper.setMediaObject($("#recording_content_div"),factory.isAudio);
		if(factory.encodingFormat)
		{
			var new_meta = mdHelper.createMetaTag();
			mdHelper.setItemprop(new_meta,"encodingFormat");
			mdHelper.setContent(new_meta,factory.encodingFormat);
			$("#recording_content_div").prepend(new_meta);
		}
		multimedia.initPlayer(factory.isAudio);
	}
	else
	{
		var message_p = $("<p/>");
		message_p.text("Cannot find an appropriate player! Please check the if the url is valid.");
		$("multimedia_player_error_div").append(message_p).show();
	}

	transcript = new Transcript(recording,$("transcripts_div"),$("#transcripts_content_div"));
	transcript.initTranscript();
	synmark = new Synmark(recording,$("#synmarks_div"),$("#synmark_list_div"));
	synmark.initSynmark();
	presentation = new Presentation(recording,$("#slides_div"),$("#image_container_div"));
	presentation.initPresentation();
	//console.log("player:"+ $("#recording_content_div").outerHeight());
	//presentation.refresh();
	synmark.refresh();
	//synmark.refresh();
	transcript.refresh();
	presentation.refresh();
	
	timer = new SynoteTimer();
	timer.run();
}


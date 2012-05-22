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
	
	//$("#player_help_btn").click(function(){
	//	var url = g.createLink({controller:"recording",action:"help"});
	//	window.open(url,"Synote Player Help");
	//});
	
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
		var message_div = $("<div/>").addClass("alert alert-error");
		message_div.html("<button class='close' data-dismiss='alert'>x</button> Cannot find an appropriate player on this device! Please check the if the url is valid.");
		$("multimedia_player_error_div").append(message_div);
	}

	//transcript = new Transcript(recording,$("transcripts_div"),$("#transcripts_content_div"));
	//transcript.initTranscript();
	//synmark = new Synmark(recording,$("#synmarks_div"),$("#synmark_list_div"));
	//synmark.initSynmark();
	//presentation = new Presentation(recording,$("#slides_div"),$("#image_container_div"));
	//presentation.initPresentation();
	//synmark.refresh();
	//transcript.refresh();
	//presentation.refresh();
	
	//timer = new SynoteTimer();
	//timer.run();
}


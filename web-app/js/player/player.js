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
	
	$("#description_show_btn").click(function(){
		if($(this).text() == "more")
		{
			$("#tags_description_div").removeClass("description-brief").addClass("description-full");
			$(this).text("less");
		}
		else if($(this).text() == "less")
		{
			$("#tags_description_div").removeClass("description-full").addClass("description-brief");
			$(this).text("more");
		}
	});
	
    multimedia = new MediaElementJSPlayer(recording,$("#recording_content_div"),$("#multimedia_player_div"));
    multimedia.initPlayer(function(msg,error){
    	if(error!=null)
    	{
    		multimedia.showMsg(msg,error);
    	}
    	else
    	{
    		ctrler = new MediaFragmentController();
    		//if(multimedia.autoStart)
    		//	ctrler.start_playback();
    		//MicroData:Change MediaObject type to VideoObject
    		mdHelper.setMediaObject($("#recording_content_div"),recording.isVideo == 'true'?true:false);
    		
    		//Init the control buttons on slides bar
    		$("#nav_play_btn").bind('click',{},function(){
    			multimedia.play();
    		});
    		$("#nav_pause_btn").bind('click',{},function(){
    			multimedia.pause();
    		});
    		$("#nav_stop_btn").bind('click',{},function(){
    			multimedia.stop();
    		});
    		$("#nav_rewind_btn").bind('click',{},function(){
    			multimedia.rewind();
    		});
    		$("#nav_forward_btn").bind('click',{},function(){
    			multimedia.forward();
    		});
    		
    		if(!isiPad())
    		{	
    			if(multimedia.autoStart === true)
    			{
    				console.log("start play.");
    				setTimeout('ctrler.start_playback()', 500);
    			}
    		}
    	}
    });

	transcript = new Transcript(recording,$("transcripts_div"),$("#transcripts_content_div"));
	transcript.initTranscript();
	synmark = new Synmark(recording,$("#synmarks_div"),$("#synmark_list_div"));
	synmark.initSynmark();
	presentation = new Presentation(recording,$("#slides_div"),$("#image_container_div"));
	presentation.initPresentation();
	synmark.refresh();
	transcript.refresh();
	presentation.refresh();
	
	timer = new SynoteTimer();
	timer.run();
}


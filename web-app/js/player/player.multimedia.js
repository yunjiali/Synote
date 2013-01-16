/*
 * 
 * The base class for multimedia player. Take the recording's json format and the container elements as constructor input
 * outer_container is the container including the control buttons, inner container is for the player only.
 * PLEASE NOTE THAT BOTH outer_container AND inner_container SHOUDL BE JQUERY OBJECT!
 * 
 * ALL POSITION AND DURATION TIME ARE IN MILLISECONDS!
 */

//Yunjia: when buffering, add modal buffering to the whole screen
var MultimediaBase = Base.extend({
	recording:null,
	outer_container:null,
	inner_container:null,
	//Default height and width
	height_default:320,
	width_default:480,
	
	//bigger player height and width
	height_large:480,
	width_large:640,
	
	//audio only player height and width
	height:0,
	width:0,
	
	autoStart:true,
	player:null,
	
	jumpToStartPosition:true, //indicate if the player should start playing at the startPosition
	
	audio_list:new Array('wma','m4a','mp3','wav','mpeg'),
	video_list:new Array('mp4','m4v','mov','wmv','flv','ogg','webm'),
	
	constructor:function(recording, outer_container, inner_container)
	{
		//console.log("constructor");
		
		this.recording = recording;
		this.outer_container = outer_container;
		this.inner_container = inner_container;
		
		this.height = this.height_default;
		this.width = this.width_default;
	},
	
	getInnerContainer:function()
	{
		return inner_container;
	},
		
	initPlayer:function(){ //startPosition is in miliseconds
		
		//in the base class init player's control panels
		if(recording.isVideo != 'true')
			$("#mf_info_div").removeClass("mf-info-video").addClass("mf-info-audio");
		
		//init control buttons
		$("#control_goto_tb").mask("99:99:99");
		$("#control_goto").click(function()
		{
			var pos = stringToMilisec($("#control_goto_tb").val());
			//console.log("pos:"+pos);
			multimedia.setPosition(pos);
		});
		
		$("#control_goto_tb").keyup(function(event){
		//Yunjia: here is a bug for IE (and Safari maybe). Keyup event is not captured by IE, so when you click "enter", you just open
			//the settings dropdownmenu
			var keycode = (event.which)?event.which:event.keyCode;
			if(keycode == 13){
					//console.log("13");
					event.preventDefault();
					$("#control_goto").click();
			}
		});
		
		if($.isEmptyObject(mf_json.hash) && $.isEmptyObject(mf_json.query)) //the fragment is not valid, so we will ignore it.
		{	
			$("#control_mf").click(function(){
				//Player from somewhere
				alert("No media fragment is defined.");
			});
		}
		else
		{
			var st = mf_json.hash.t[0].start?mf_json.hash.t[0].start:0;
			var et = mf_json.hash.t[0].end?mf_json.hash.t[0].end:-1;
			var old_text = $("#control_mf").text();
			
			if(st === 0 || st === undefined || st==="0" || st==="00.000" || st==="00:00.000" ||st === "00:00:00.000")
			{
				old_text+=" the beginning";
			}
			else
			{
				old_text+=" "+st;
			}
			
			if(et === -1 || et === null)
			{
				old_text+=" to the end";
			}
			else
			{
				old_text+=" to "+et;
			}
			$("#control_mf").text(old_text);
			$("#control_mf").show();
			$("#control_mf").click(function(){
				//Player from somewhere
				ctrler.start_playback();
			});
		}
		
	},
	refresh:function(){},
	resize:function(width,height){},
	play:function(){},
	playFrom:function(st){}, //start playing from time st, st is in miliseconds
	pause:function(){},
	stop:function(){},
	rewind:function(){},
	forward:function(){},
	getPosition:function(){}, //in miliseconds
	setPosition:function(position){}, //in miliseconds
	getDuration:function(){},
	setDurationSpan:function()
	{
		var time_duration_span = $("#time_duration_span");
		//console.log("get dur:"+multimedia.getDuration());
		var dur = multimedia.getDuration();
		if(dur >0)
		{
			time_duration_span.text(milisecToString(dur));
			//mdHelper.setItemprop(time_duration_span,"duration");
		}
	},
	setCurrentTimeSpan:function()
	{
		var time_current_span = $("#time_current_position");
		var currentPosition = multimedia.getPosition();
		if(currentPosition >0)
			time_current_span.text(milisecToString(currentPosition));
	},
	initListeners:function(){},
	showMsg:function(msg,error)
	{
		var msg_div = $("#multimedia_player_error_div");
		if(error!=null)
		{
			msg_div.html("<div class='alert alert-error'><button class='close' data-dismiss='alert'>x</button>"+msg+"</div>");
		}
		else
		{
			msg_div.html("<div class='alert alert-success'><button class='close' data-dismiss='alert'>x</button>"+msg+"</div>");
		}
	}
})

/*
 * ############################################################################
 * MediaElement player
 * ############################################################################
 */
var MediaElementJSPlayer = MultimediaBase.extend({
	constructor:function(recording, outer_container, inner_container)
	{
		this.base(recording, outer_container, inner_container);
	},
	initPlayer:function(callback)
	{
		this.base();
		var player_tag = multimedia.inner_container.find("#multimedia_player");
		player_tag.children("source").attr("src",recording.url);
		//find out the media type by checking the file format
		
		if(isYouTubeURL(recording.url))
		{
			$("#multimedia_player").children("source").attr("type","video/x-youtube");
		}
		else if(isDailyMotionURL(recording.url))
		{
			$("#multimedia_player").children("source").attr("type","video/dailymotion");
		}
		else
		{
			var jqURL = $.url(recording.url);
			var file = jqURL.attr('file').toLowerCase();
			
			var parts = file.split('.')
			//if no file extension
			if(parts.length>1)
			{
				var file_extension = parts[parts.length-1].toLowerCase();
				if(file_extension)
				{
					if($.inArray(file_extension,this.video_list)!=-1)
					{
						$("#multimedia_player").children("source").attr("type","video/"+file_extension);
					}
					else if($.inArray(file_extension,this.audio_list)!=-1)
					{
						$("#multimedia_player").children("source").attr("type","audio/"+file_extension);
					}
					else
					{
						//do nothing
					}
				}
			}
		}
		if(recording.hasCC == 'true')
		{
			multimedia.refresh("en","None",callback);
		}
		else
		{
			multimedia.refresh(null,"None", callback);
		}
		this.initListeners();
		//console.log("state:"+this.player.getState());
	},
	refresh:function(lang,startLang,callback){
		//Yunjia: Later, when we have multiple transcript, we will embed multiple tracks
		var player_tag = multimedia.inner_container.find("#multimedia_player");
		var opts = {
				audioWidth: this.width_default,
				//enablePluginDebug: true,
	            //plugins: ['flash','silverlight'],
				startLanguage:startLang,
				success:function(mediaElement,domObject)
				{
					multimedia.player = mediaElement;
					mediaElement.addEventListener('timeupdate',function(e){
						multimedia.setCurrentTimeSpan();
					},false);
					callback("success",null);
					return;
				},
				error:function(){
					callback("Cannot initialise the player.","error");
					return;
				}
		}
		
		if(lang != null)
		{
			var transcript_track = $("<track/>").attr("kind","subtitles").attr("src","../downloadTranscript?type=webvtt&multimediaId="+recording.id).attr("srclang",lang);
			transcript_track.appendTo(player_tag);
		}
		else //remove the track
		{
			player_tag.children("track[kind='subtitles']").remove();
		}
		
		
		player_tag.mediaelementplayer(opts);
	},
	resize:function(width,height){}, 
	play:function()
	{
		//console.log("1 play:::");
		var p = multimedia.player;
		if(p)
			p.play();
		else
			console.log("player is null");
	},
	playFrom:function(st)
	{
		if(multimedia)
		{
			//console.log(st);
			multimedia.play();
			multimedia.setPosition(st);
		}
		else
			console.log("player is null");
	},
	pause:function()
	{
		//console.log("2 pause:::");
		var p = multimedia.player;
		if(p)
			p.pause();
		else
			console.log("player is null");
	},
	stop:function() //We decide the stop function should be go back to the front and pause
	{
		//console.log("3 stop:::");
		var p = multimedia.player;
		if(p)
			p.stop();
		else
			console.log("player is null");
	},
	rewind:function()
	{
		var p = multimedia.player;
		var currentPos = multimedia.getPosition();
		var pace = parseInt($("#control_pace_div :selected").val(),10);
		var pos = currentPos-pace*1000;
		multimedia.setPosition(pos>0?pos:0);
		
	},//need pace
	forward:function()
	{
		var p = multimedia.player;
		var currentPos = multimedia.getPosition();
		var pace = parseInt($("#control_pace_div :selected").val(),10);
		//console.log("seelcted:"+$("#control_pace_div :selected").val());
		//console.log("pace:"+pace);
		multimedia.setPosition(currentPos+pace*1000);
	},//need pace
	getPosition:function()
	{
		var p = multimedia.player;
		if(p)
			return parseInt(p.currentTime*1000);
		else
		{
			return null;
			console.log("player is null");
		}
	},
	setPosition:function(position) 
	{
		var p = multimedia.player;
		var position = position?position:0;
		if(p)
		{
			if(multimedia.getPosition() <=0)
			{
				setTimeout("multimedia.setPosition("+position+")",100);
			}
			else
				p.setCurrentTime(position/1000);
		}
		else
			console.log("jw player is null");
	},
	getDuration:function()
	{
		var p = multimedia.player;
		if(p)
			return p.duration*1000;
		else
		{
			return null;
			console.log("jw player is null");
		}
	},
	initListeners:function()
	{
		//console.log("init listeners...")
		$("#control_play").bind('click',{},this.play);
		$("#control_pause").bind('click',{},this.pause);
		$("#control_stop").bind('click',{},this.stop);
		$("#control_rewind").bind('click',{},this.rewind);
		$("#control_forward").bind('click',{},this.forward);
	}
});

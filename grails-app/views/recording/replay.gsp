<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="org.synote.player.client.TimeFormat" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional with HTML5 microdata//EN" "xhtml1-transitional-with-html5-microdata.dtd">
<html lang="en">
<head>
	<title>Synote Player</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	<meta name="author" content="Yunjia Li"/>
	<g:urlMappings/>
	<link rel="stylesheet" type="text/css" href="${resource(dir: 'bootstrap', file: 'css/bootstrap.min.css')}" />
	<link rel="stylesheet" href="${resource(dir: 'css', file: 'player.css')}" />
	<style type="text/css">
		body {
	        padding-top: 60px;
	    }
	</style>
	<link rel="stylesheet" type="text/css" href="${resource(dir: 'bootstrap', file: 'css/bootstrap-responsive.min.css')}" />
	<link rel="stylesheet" type="text/css" href="${resource(dir: 'bootstrap', file: 'css/bootstrap-tagmanager.css')}" />
	<link rel="stylesheet" type="text/css" href="${resource(dir: 'css', file: 'main.css')}" />
	<link rel="stylesheet" type="text/css" href="${resource(dir: 'smfplayer', file: 'mediaelementplayer.min.css')}" />
	<link rel="stylesheet" type="text/css" href="${resource(dir: 'smfplayer', file: 'smfplayer.css')}"  />
	<link rel="shortcut icon" href="${resource(dir: 'images', file: 'synote_icon.ico')}" type="image/x-icon" />
	
	<script type="text/javascript" src="${resource(dir: 'js/jquery', file: 'jquery-1.7.1.min.js')}"></script>
	<script type="text/javascript" src="${resource(dir: 'bootstrap', file: 'js/bootstrap.min.js')}"></script>
	<script type="text/javascript" src="${resource(dir: 'bootstrap', file: 'js/bootstrap-tagmanager.js')}"></script>
	<script type="text/javascript" src="${resource(dir: 'js', file: 'player/player.responsive.js')}"></script>
	<script type="text/javascript" src="${resource(dir:'js',file:"Base.js")}"></script>
	<script type="text/javascript" src="${resource(dir: 'js/jquery', file: 'jquery.form.js')}"></script>
	<script type="text/javascript" src="${resource(dir: 'js/jquery', file: 'jquery.validate-1.9.1.min.js')}"></script>
	<!-- Other jquery libraries  -->
	<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.maskedinput-1.3.min.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.url.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.timers.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.field_selection.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.scrollTo-1.4.2-min.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.okShortcut.min.js")}"></script>
	<!-- Player settings -->
	<script type="text/javascript" src="${resource(dir:'js/jquery',file:"microdataHelper.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/tiny_mce',file:"jquery.tinymce.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/player',file:"webvtt.parser.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js',file:"util.js")}"></script>
	<!-- For player -->
	<script type="text/javascript" src="${resource(dir:'smfplayer',file:"mediaelement-and-player.min.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'smfplayer',file:"smfplayer.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/player',file:"player.transcript.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/player',file:"player.synmark.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/player',file:"player.presentation.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/player',file:"player.textselector.js")}"></script>
	<!--  -->
	<script id="scriptInit" type="text/javascript">
		//In case I forget to remove console.log in IE
		var alertFallback = true;
		if (typeof console === "undefined" || typeof console.log === "undefined") {
		     console = {};
		     if (alertFallback) {
		         console.log = function(msg) {
		              //Do nothing
		         };
		     } else {
		         console.log = function() {};
		     }
		};
	</script>
	<script type="text/javascript">
	var recording = null;
	var user = null;
	var mdHelper = new MicrodataHelper(true); //helper to help embed microdata
	var userBaseURI = "${userBaseURI}";
	var resourceBaseURI = "${resourceBaseURI}";
	var player;
	var transcript,
		synmark,
		presentation;
	//Check if media fragment is defined in window.location
	var uglyURI = decodeURIComponent(window.location);
	var prettyURI = uglyURI;
	if(uglyURI.indexOf("#!") != -1)
		prettyURI = uglyURI.replace("#!","#");
	var currentURL = $.url(prettyURI);
	var recordingURLStr = "${recording.url.url}";
	if(currentURL.attr('fragment')) //if current url has fragment
	{
		if(recordingURLStr.indexOf('#') == -1)
			recordingURLStr+='#';
			
		recordingURLStr +=currentURL.attr('fragment');
		//console.log(recordingURLStr);
	}
	
	recording = new Object();
	recording.id = "${recording.id}";
	//Removed recording.title = "{recording.title?.encodeAsHTML()}";
	recording.url = recordingURLStr;//Yunjia: We have to think about it, if the url has fragment but the server cannot regonise it..., what should we do then
	recording.canEdit = "${canEdit}".toLowerCase();
	recording.canCreateSynmark = "${canCreateSynmark}".toLowerCase();
	recording.isVideo = "${recording.isVideo}".toLowerCase();
	recording.uuid = "${recording.uuid}";
	recording.thumbnail = "${recording.thumbnail}";
	recording.hasCC = "${hasCC}".toLowerCase();

	user = new Object();
	if("${user}".length>0)
	{
		//console.log("1");
		user.id = "${user?.id}";
		user.user_name = "${user?.userName}";
	}
	else //No user logged in
	{
		//console.log("2");
		user.id = -1;
		user.user_name = "Guest User";
	}

	var SynotePlayer = function()
	{
		this.options = {
			mfURI:recording.url, 
			autoStart:true, 
			height: 320, 
			width:480,
			audioWidth:480,
			audioHeight:320,
			isVideo: recording.isVideo === "true"?true:false,
			success:function(mediaElement,domObject,p){
				mediaElement.addEventListener('timeupdate', function(e){
		            var currentPosition = mediaElement.currentTime*1000;

		            $("#time_current_position").text(milisecToString(currentPosition));
		            if(synmark.synchronised == true)
						synmark.sync(currentPosition);
					if(transcript.synchronised == true)
						transcript.sync(currentPosition);
					if(presentation.synchronised == true)
						presentation.sync(currentPosition);
		        });
			}
		};
		this.init=function(player_div, callback)
		{	
			var p = player_div.smfplayer(this.options);
			if(p === undefined)
			{
				return callback("The video/audio resource cannot be played in Synote Player.",null);
			}

			//add subtitles if available, currently only support en
			if(recording.hasCC)
			{
				var transcript_track = $("<track/>").attr("kind","subtitles").attr("src","../downloadTranscript?type=webvtt&multimediaId="+recording.id).attr("srclang","en");
				transcript_track.appendTo(player_div.find("video,audio"));
			}
			
			transcript = new Transcript(recording,$("transcripts_div"),$("#transcripts_content_div"));
			transcript.initTranscript();
			synmark = new Synmark(recording,$("#synmarks_div"),$("#synmark_list_div"));
			synmark.initSynmark();
			presentation = new Presentation(recording,$("#slides_div"),$("#image_container_div"));
			presentation.initPresentation();
			synmark.refresh();
			transcript.refresh();
			presentation.refresh();
			return callback(null, p);
		};
	}
	
	$(document).ready(function(){
		var synotePlayer = new SynotePlayer();
		synotePlayer.init($("#multimedia_player_div"), function(err, p){
			player = p;
			if(err != null)
			{
				$("#multimedia_player_error_div").html('<button type="button" class="close" data-dismiss="alert alert-error">&times;</button>'+err);
				return;
			}
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

			if(recording.isVideo != 'true')
				$("#mf_info_div").removeClass("mf-info-video").addClass("mf-info-audio");
			
			//init control buttons
			$("#control_goto_tb").mask("99:99:99");
			$("#control_goto").click(function()
			{
				var pos = stringToMilisec($("#control_goto_tb").val());
				//console.log("pos:"+pos);
				player.setPosition(pos);
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

			$("#control_play, #nav_play_btn").bind('click',{},function(){
				player.play();
			});
			$("#control_pause,#nav_pause_btn").bind('click',{},function(){
				player.pause();
			});
			$("#control_rewind,#nav_rewind_btn").bind('click',{},function(){
				var currentPos = player.getPosition();
				var pace = parseInt($("#control_pace_div :selected").val(),10);
				var pos = currentPos-pace*1000;
				player.setPosition(pos>0?pos:0);
			});
			$("#control_forward,#nav_forward_btn").bind('click',{},function(){
				var currentPos = player.getPosition();
				var pace = parseInt($("#control_pace_div :selected").val(),10);
				player.setPosition(currentPos+pace*1000);
			});

			var mf_json = player.getMFJson();
			if($.isEmptyObject(mf_json.hash) && $.isEmptyObject(mf_json.query)) //the fragment is not valid, so we will ignore it.
			{	
				$("#control_mf").click(function(){
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
					player.playmf();
				});
			}
		});
	});
	</script>
</head>
<body itemscope="itemscope" itemtype="http://schema.org/WebPage" itemref="bottomMainFooter">
<meta itemprop="author" content="Yunjia Li"/>
	<!-- Top Navigation bar -->
	<div class="navbar navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </a>
          <a class="brand" href="#">Synote Player</a>
				<syn:isLoggedIn>
				<div class="btn-group pull-right">
					<g:link class="btn btn-success" controller="user" action="showUserProfile" title="Show user profile">
						<i class="icon-user icon-white"></i>
						<syn:loggedInUsername />
					</g:link>
					<a href="#" class="btn dropdown-toggle btn-success" data-toggle="dropdown">
						<span class="caret"></span>
					</a>
					<ul class="dropdown-menu">
				    	<li><g:link controller="user" action="showUserProfile" title="Show user profile">My Profile</g:link></li>
				    	<li><g:link controller="user" action="index" title="Edit my profile">My Synote</g:link></li>
				    	<syn:isAdminLoggedIn>
						<li><g:link controller="admin" action="index" title="Administration">Administration</g:link></li>
						</syn:isAdminLoggedIn>
				    	<li class="divider"></li>
				    	<li>
		    				<g:link controller="multimediaResource" action="create" title="create a recording">
		    				Create a recording </g:link>
			    		</li>
			    		<li>
			    			<g:link controller="userGroup" action="create" title="create a group">
			    			 Create a group </g:link>
			    		</li>
				        <li class="divider"></li>
				        <li><g:link controller="logout" action="index" title="Log out">Log out</g:link></li>
           			</ul>
				</div>
				</syn:isLoggedIn>
				<syn:isNotLoggedIn>
				<div class="btn-group pull-right">
					<g:link controller="login" action="auth" title="Log in" elementId="main_login_a" class="btn btn-primary">
							Login</g:link>
					<syn:allowRegistering>
						<g:link controller="register" action="index" title="Register" class="btn btn-success hidden-phone">
							 Register</g:link>
					</syn:allowRegistering>
				</div>
				</syn:isNotLoggedIn>
				<!--  <div>
					<ul class="nav pull-right btn-group">
				    	<li><button id="control_play" class="btn" title="play"><i class="icon-play"></i></button></li>
						<li><button id="control_pause" class="btn" title="pause"><i class="icon-pause"></i></button></li>
						<li><button id="control_stop" class="btn visible-desktop" title="stop"><i class="icon-stop"></i></button></li>
						<li><button id="control_rewind" class="btn" title="rewind"><i class="icon-backward"></i></button></li>
						<li><button id="control_forward" class="btn" title="rewind"><i class="icon-forward"></i></button></li>
						<li><button class="btn visible-desktop" id="control_goto" title="Go to a certain time"><i class="icon-arrow-right"></i></button></li>
				    </ul>
				</div>-->
				<div class="btn-group nav pull-right">
					<button id="nav_play_btn" class="btn" title="play"><i class="icon-play"></i></button>
					<button id="nav_pause_btn" class="btn" title="pause"><i class="icon-pause"></i></button>
					<button id="nav_rewind_btn" class="btn" title="rewind"><i class="icon-backward"></i></button>
					<button id="nav_forward_btn" class="btn" title="rewind"><i class="icon-forward"></i></button>
				</div>
				<div class="nav-collapse pull-left">
				   <ul class="nav">
					    <li>
					    	<a href="${resource(dir: '/')}" title="home">
					    	Home</a>
					    </li>
						<!-- Recordings -->   
					    <li><g:link controller="multimediaResource" action="list" title="Multimedia recordings">
					    	Browse</g:link>
					    </li>
					    
						<!-- Groups -->    
					    <li><g:link controller="userGroup" action="list" title="Groups list">
							Groups</g:link>
						</li> 
						<li><g:link action="help" target="_blank" title="help">
							Help</g:link>
						</li>
					</ul>
				</div>
			</div>
		</div>
    </div>
	
	<div class="container" id="content">
		<!-- Recording title -->
		<div id="multimedia_title_div">
			<div>
				<h2 id="recording_title_h2" itemprop="name">${recording.title}</h2>
				<div id="recording_owner_div" itemprop="creator" itemscope="itemscope" itemtype="http://schema.org/Person" itemid="${syn.getUserURI([userId:recording.owner?.id.toString()])}">
					<meta itemprop="familyName" content="${recording.owner?.firstName}"/>
					<meta itemprop="givenName" content="${recording.owner?.lastName}"/>
					<meta itemprop="email" content="${recording.owner?.email}"/>
					<span class="owner-info">by <g:link controller="user" action="show" id="${recording.owner?.id}" elementId="recording_owner_a" itemprop="name">
						${recording.owner?.userName}</g:link> |</span>
	  				<span class="datetime-info" itemprop="dateCreated">Created at <syn:printSQLTime datetime="${recording.dateCreated}"/> |</span>
	  				<span class="datetime-info">${views} Views</span>
				</div>
			</div>
		</div>
		<!-- Player and Description-->
		
		<div class="container">
		
		<div class="row">
			<div id="col_left_div" class="player-fixed-width">
				<div id="mf_info_div" class="mf-info-video">
					<div class="pull-left">
						<button class="btn btn-success" id="control_mf" title="Play this fragment" style="display:none;"><i class="icon-play-circle icon-white"></i>Play from</button>
					</div>
					<div id="control_time_div" class="pull-right">
						<span id="time_current_position">00:00:00</span> / <span id="time_duration_span">${TimeFormat.getInstance().toString(recording.duration)}</span>
					</div>
				</div>
				<div id="multimedia_player_error_div">
					<!-- attach error messages as another span class="error" here -->
				</div>
				<g:if test="${recording.isVideo}">
				<div id="recording_content_div" itemscope="itemscope" itemtype="http://schema.org/AuidoObject" itemref="recording_title_div recording_owner_div created_time_span"> <!-- player -->
				</g:if>
				<g:else>
				<div id="recording_content_div" itemscope="itemscope" itemtype="http://schema.org/VideoObject" itemref="recording_title_div recording_owner_div created_time_span"> <!-- player -->
				</g:else>
					<meta itemprop="contentURL" content="${recording.url.url}"/>
					<meta itemprop="dateModified" content="${new SimpleDateFormat("dd/MM/yyyy").format(recording.lastUpdated)}"/>
					<div id="multimedia_player_div"></div>
				</div><!-- end player -->
				<div id="recording_control_div" class="hidden-phone">
					<div style="display:inline;">
						<button id="control_play" title="Play" class="btn"><i class="icon-play"></i></button>
						<button id="control_pause" title="Pause" class="btn"><i class="icon-pause"></i></button>
						<button id="control_rewind" title="Rewind" class="btn"><i class="icon-backward"></i></button>
						<button id="control_forward" title="Forward" class="btn"><i class="icon-forward"></i></button>
					</div>	
					<div id="control_pace_div" style="display:inline;">
						Pace:
						<select name="control_pace_select" class="span1" style="margin-top:9px;" id="control_pace_select">
							<option value="1">1s</option>
							<option value="5">5s</option>
							<option value="10" selected="selected">10s</option>
							<option value="20">20s</option>
						</select>
					</div>
					<div class="input-append pull-right" style="display:inline;margin-top:9px;">
						<input type="text" size="10" class="span1" name="control_goto_tb" id="control_goto_tb" value="00:00:00"/>
						<button id="control_goto" class="btn" title="Go to a certain time"><i class="icon-arrow-right"></i></button>
					</div>
				</div>
				<!-- Transcript -->
				<div id="transcripts_div" class="tab-pane span-left">
					<div>
						<h3 class="heading-inline">Subtitle</h3>
						<div class="pull-right btn-toolbar" style="display:inline">
							<g:if test="${canEdit}">
							<div class="btn-group" id="transcript_edit_enter_div">
								<button class="btn" title="Add a new transcript block" id="edit_transcript_add_btn">
									<img src="${resource(dir:'images/player',file:"edit_transcript_add_22.png")}"  id="edit_transcript_add_img" alt="Add new transcript block"/>
								</button>
							</div>
							</g:if>
							<div class="btn-group">
								<a class="btn dropdown-toggle" data-toggle="dropdown" href="#" title="Export current transcript as .vtt file">
									<img src="${resource(dir:'images/player',file:"edit_transcript_export_22.png")}" alt="Export current transcript as .vtt file" id="edit_transcript_export_img" title="Export current transcript as .vtt file"/>
									<span class="caret"></span>
								</a>
								<ul class="dropdown-menu">
									<li>
										<g:link controller="recording" action="downloadTranscript" params='[multimediaId:"${recording.id}",type:"text"]' target="_blank" title="download transcript as plain text format">Plain text</g:link>
									</li>
									<li>
										<g:link ontroller="recording" action="downloadTranscript" params='[multimediaId:"${recording.id}",type:"srt"]' target="_blank" title="download transcript as srt format">SRT Format</g:link>
									</li>
									<li>
										<g:link ontroller="recording" action="downloadTranscript" params='[multimediaId:"${recording.id}",type:"webvtt"]' target="_blank" title="download transcript as webvtt format">WebVTT Format</g:link>
									</li>
								</ul>
							</div>
						</div>
					</div>
					<div id="transcript_msg_div"></div><!-- displaying info, error messages -->
					<!-- transcript editing form -->
					<g:if test="${canEdit}">
					<div id="transcript_edit_div" class="well" style="display:none;">
						<form id="transcript_edit_form" method="post" class="form-vertical">
							<fieldset>
								<input type="hidden" name="transcript_id" id="transcript_id"/> <!-- The id of srt.index -->
								<div class="control-group">
									<label for="transcript_st" class="control-label"><b><em>*</em>Start:</b></label>
									<div class="input-append">
										<input type='text' size="10" class="required" name='transcript_st' id='transcript_st'/>
										<button class="btn" id="transcript_st_time" title="Get current time" type="button"><i class="icon-time"></i></button>
										<button class="btn" id="transcript_st_add" title="add one second" type="button"><i class="icon-plus"></i></button>
										<button class="btn" id="transcript_st_remove" title="minus one second" type="button"><i class="icon-minus"></i></button>
									</div>
								</div>
								<div class="control-group">
									<label for="transcript_et" class="control-label"><b><em>*</em>End:</b></label>
									<div class="input-append">
										<input type='text' size="10" class="required" name='transcript_et' id='transcript_et'/>
										<button class="btn" id="transcript_et_time" title="Get current time" type="button"><i class="icon-time"></i></button>
										<button class="btn" id="transcript_et_add" title="add one second" type="button"><i class="icon-plus"></i></button>
										<button class="btn" id="transcript_et_remove" title="minus one second" type="button"><i class="icon-minus"></i></button>
									</div>
								</div>
								<div class="control-group">
									<label for="transcript_speaker" class="control-label"><b>Speaker</b></label>
									<input type='text' size="255" autocomplete="off" name='transcript_speaker' id='transcript_speaker' value='' />
								</div>
								<div class="control-group">
									<label for="transcript_content" class="control-label"><b><em>*</em>Transcript:</b></label>
									<textarea class="required" name='transcript_content' id='transcript_content' value='' rows="5" style="width:100%;"></textarea>
								</div>
								<div class="form-actions">
									<input class="btn btn-primary" id="transcript_submit" type="button" value="Submit"/><!-- This is not a submit button, because nothing will be submitted to the server -->
									<input class="btn" id="transcript_cancel" type="reset" value="Cancel" />
								</div>
							</fieldset>
						</form>
					</div>
					</g:if>
					<div id="transcript_loading_div" style="display:none;"><img id="transcript_loading_img" src="${resource(dir:'images/skin',file:'loading_64.gif')}" alt="loading"/></div>
					<div id="transcripts_inner_div">
						<div id="transcripts_content_div">
							<ol id="transcript_ol"></ol>
						</div>
					</div>
				</div><!-- end transcript -->
			</div>
		
			<!-- synmarks and slide -->
			<div id="col_right_div" class="span-fluid-right tabbable">
				<div class="container-fluid">
					<div class="row-fluid">
						<!-- description and tags -->
						<div id="tags_description_div" class="span12 hidden-phone description-brief"><!-- description -->	
							<div>
								<b>Tags</b><br/>
								<g:if test="${recording.tags?.size() >0}">
						  		<g:each var="tag" in="${recording.tags}">
						  			<span class="badge badge-tag"><i class="icon-tag tag-item icon-white"></i>${tag?.content}</span>
						  		</g:each>
						  		</g:if>
						  		<g:else>
						  			No tags
						  		</g:else>
						  	</div>
						  	<div id="description_div">
								<b>Description</b>
								<g:if test="${recording.note?.content?.size() >0}">
								<p>${recording.note?.content}</p>	
								</g:if>
								<g:else>
									<br/>No description
								</g:else>
							</div>				
						</div><!-- end description -->
						<div id="description_show_div" class="span12 hidden-phone">
							<button id="description_show_btn" class="btn btn-mini">more</button>
						</div>
						<!-- Synmarks -->
						<ul class="nav nav-tabs" id="tab_right">
							<li class="active dropdown"><a href="#synmarks_div" data-toggle="tab">Synmarks <span id="synmark_count_span"></span></a></li>
							<li><a href="#slides_div" data-toggle="tab">Slides <span id="slides_count_span"></span></a></li>
							<li class="visible-phone"><a href="#transcripts_div" data-toggle="tab">Transcripts</a></li>
						</ul>
						<div class="tab-content" id="tab_content_div">
							<div id="synmarks_div" class="tab-pane active span-middle">
								<h3 class="hiding">Synmarks</h3>
								<div class="pull-right btn-toolbar" style="display:inline">
									<g:if test="${canCreateSynmark}">
									<div class="btn-group" id="synmark_edit_enter_div">
										<button class="btn" title="Add a new synmark block" id="add_synmark_btn">
											<img src="${resource(dir:'images/player',file:"bookmark_add_22.png")}"  id="add_synmark_img" title="Add a new synmark"/>
										</button>
									</div>
									</g:if>
									<div class="btn-group">
										<a class="btn dropdown-toggle" data-toggle="dropdown" href="#" >
											<img src="${resource(dir:'images/player',file:"bookmark_show_22.png")}"  id="show_synmark_img" title="Synmark menu"/>
											<span class="caret"></span>
										</a>
										<ul class="dropdown-menu">
											<g:if test="${canCreateSynmark}">
											<li>
												<a href="#">All Synmarks</a>
											</li>
											<li>
												<a href="#">My Synmarks</a>
											</li>
											<li class="divider"></li>
											</g:if>
											<li>
												<g:link controller="recording" action="exportSynmarks">Export Synmarks</g:link>
											</li>
										</ul>
									</div>
								</div>
								<div id="synmark_msg_div"></div><!-- displaying info, error messages -->
								<!-- synmark editing form -->
								<g:if test="${canCreateSynmark}">
								<div id="synmark_create_div" class="well" style="display:none;">
									<form id="synmark_form" method="post" class="form-vertical">
										<fieldset>
											<input type="hidden" name="synmark_id" id="synmark_id"/> 
											<input type="hidden" name="synmark_thumbnail" id="synmark_thumbnail"/> 
											<div class="control-group">
												<label for="synmark_st" class="control-label"><b><em>*</em>Start:</b></label>
												<div class="controls">
													<div class="input-append">
														<input type='text' size="10" class="required span6" name='synmark_st' id='synmark_st' style="display:inline;"/>
														<button class="btn" id="synmark_st_time" title="Get current time" type="button"><i class="icon-time"></i></button>
														<button class="btn" id="synmark_st_add" title="add one second" type="button"><i class="icon-plus"></i></button>
														<button class="btn" id="synmark_st_remove" title="minus one second" type="button"><i class="icon-minus"></i></button>
													</div>
												</div>
											</div>
											<div class="control-group">
												<label for="synmark_et" class="control-label"><b><em>*</em>End:</b></label>
												<div class="controls">
													<div class="input-append">
														<input type='text' size="10" class="required span6" name='synmark_et' id='synmark_et' style="display:inline;"/>
														<button class="btn" id="synmark_et_time" title="Get current time" type="button"><i class="icon-time"></i></button>
														<button class="btn" id="synmark_et_add" title="add one second" type="button"><i class="icon-plus"></i></button>
														<button class="btn" id="synmark_et_remove" title="minus one second" type="button"><i class="icon-minus"></i></button>
													</div>
												</div>
											</div>
											<div class="control-group">
											    <label for="synmark_title">Title</label>
												<input type='text' class="span6" autocomplete="off" name='synmark_title' id='synmark_title' value='' />
											</div>
											<div class="control-group">
											    <label for="synmark_tags">Tags:</label>
												<input type='text' autocomplete="off" placeholder="tags" class="tm-input tm-input-success" data-provide="typeahead" data-items="10" data-source="${typeaheadStr}" name='synmark_tags_visible' id='synmark_tags_visible' value='' />
												<p class="help-block">Please separate tags by comma ','</p>
											</div>
											<div class="control-group">
												<label for="synmark_note" class="control-label"><b>Note:</b></label>
												<textarea class="tinymce" name='synmark_note' id='synmark_note' value='' rows="10" style="width:100%"></textarea>
											</div>
											<div class="form-actions">
												<input class="btn btn-primary" id="synmark_submit" type="submit" value="Submit"/><!-- This is not a submit button, because nothing will be submitted to the server -->
												<input class="btn" id="synmark_cancel" type="reset" value="Cancel" />
											</div>
										</fieldset>
									</form>
								</div>
								</g:if>
								<div id="synmark_loading_div" style="display:none;"><img id="synmark_loading_img" src="${resource(dir:'images/skin',file:'loading_64.gif')}" alt="loading"/></div>
								<div id="synmarks_inner_div">
									<div id="synmark_list_div"></div>
								</div>
							</div>
							<!-- Slides  -->
							<div id="slides_div" class="tab-pane span-right">
								<h3 class="hiding">Slides</h3>
								<div class="pull-right btn-toolbar" style="display:inline">
									<g:if test="${canEdit}">
									<div class="btn-group" id="slides_edit_div">
										<button class="btn" title="Add a new transcript block" id="slides_add_btn">
											<img src="${resource(dir:'images/player',file:"slides_add_22.png")}"  id="slides_add_img" alt="Add new slide"/>
										</button>
										<button class="btn" title="Edit the selected transcript block" id="slides_edit_btn">	
											<img src="${resource(dir:'images/player',file:"slides_edit_22.png")}"  id="slides_edit_img" alt="Edit slides"/>
										</button>
										<button class="btn" title="Delete the selected transcript block" id="slides_delete_btn">	
											<img src="${resource(dir:'images/player',file:"edit_transcript_clear_22.png")}"  id="slides_delete_img" alt="Remove all slide"/>
										</button>
									</div>
									</g:if>
								</div>
								<div id="slides_msg_div"></div><!-- displaying info, error messages -->
								<!-- slide editing form -->
								<g:if test="${canEdit}">
								<div id="presentation_edit_div" class="well" style="display:none;">
									<form id="presentation_edit_form" method="post" class="form-vertical">
										<fieldset>
											<input type="hidden" name="slide_id" id="slide_id"/> 
											<input type="hidden" name="old_index" id="old_index"/> 
											<div class="control-group">
												<label for="slide_st" class="control-label"><b><em>*</em>Start:</b></label>
												<div class="input-append">
													<input type='text' size="10" class="required span6" name='slide_st' id='slide_st' style="display:inline;"/>
													<button class="btn" id="slide_st_time" title="Get current time" type="button"><i class="icon-time"></i></button>
													<button class="btn" id="slide_st_add" title="add one second" type="button"><i class="icon-plus"></i></button>
													<button class="btn" id="slide_st_remove" title="minus one second" type="button"><i class="icon-minus"></i></button>
												</div>
											</div>
											<div class="control-group">
												<label for="slide_et" class="control-label"><b><em>*</em>End:</b></label>
												<div class="input-append">
													<input type='text' size="10" class="required span6" name='slide_et' id='slide_et' style="display:inline;"/>
													<button class="btn" id="slide_et_time" title="Get current time" type="button"><i class="icon-time"></i></button>
													<button class="btn" id="slide_et_add" title="add one second" type="button"><i class="icon-plus"></i></button>
													<button class="btn" id="slide_et_remove" title="minus one second" type="button"><i class="icon-minus"></i></button>
												</div>
											</div>
											<div class="control-group">
												<label for="slide_index"><em>*</em><b>Slide index:</b></label>
												<select name='slide_index' id='slide_index'></select>
											</div>
											<div class="control-group">
												<label for="slide_url"><em>*</em><b>Slide url:</b></label>
												<input type='text' class="required" name='slide_url' id='slide_url'/>
											</div>
											<div class="form-actions">
												<input class="btn btn-primary" id="presentation_submit" type="submit" value="Submit"/><!-- This is not a submit button, because nothing will be submitted to the server -->
												<input class="btn" id="presentation_cancel" type="reset" value="Cancel" />
											</div>
										</fieldset>
									</form>
								</div>
								</g:if>
								<div id="slides_loading_div" style="display:none;"><img id="slides_loading_img" src="${resource(dir:'images/skin',file:'loading_64.gif')}" alt="loading"/></div>
								<div id="image_container_div">
								</div>	
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- container for content-->
	<div id="share_url_dialog" class="modal hide fade">
		<div class='modal-header'>
		    <button type='button' class='close' data-dismiss='modal'>×</button>
		    <h4 id="share_url_title_h4"></h4>
		</div>
		<div class='modal-body'>
		    
		</div>
		<div class='modal-footer'>
		    <a href='#' class='btn' data-dismiss='modal'>Close</a>
		    <!--  
		    <a href='#' class='btn btn-primary'>Copy to Clipboard</a>-->
		</div>
	</div>
	<g:render template="/common/footer"/>
</body>
</html>

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
		#synmarks_div
		{
			background-color:#666666;
		}
		#slides_div
		{
			background-color:#999999;
		}
		body {
	        padding-top: 60px;
	    }
	</style>
	<link rel="stylesheet" type="text/css" href="${resource(dir: 'bootstrap', file: 'css/bootstrap-responsive.min.css')}" />
	<link rel="stylesheet" type="text/css" href="${resource(dir: 'css', file: 'main.css')}" />
	<link rel="shortcut icon" href="${resource(dir: 'images', file: 'synote_icon.ico')}" type="image/x-icon" />
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
	<script type="text/javascript" src="${resource(dir: 'bootstrap', file: 'js/bootstrap.min.js')}"></script>
	<script type="text/javascript" src="${resource(dir: 'js', file: 'player/player.responsive.js')}"></script>
	<script type="text/javascript" src="${resource(dir:'js',file:"Base.js")}"></script>
	<!-- Other jquery libraries  -->
	<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.maskedinput-1.3.min.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.contextMenu.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.url.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.timers.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.field_selection.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.scrollTo-1.4.2-min.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.okShortcut.min.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/player',file:"mediafragments.js")}"></script>
	<!-- Player settings -->
	<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.media.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/jquery',file:"microdataHelper.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/tiny_mce',file:"jquery.tinymce.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/player',file:"webvtt.parser.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/player',file:"player.util.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/player',file:"player.settings.js")}"></script>
	<!-- For player -->
	<script type="text/javascript" src="${resource(dir:'js/jwplayer',file:"jwplayer.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/wmvplayer',file:"silverlight.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/wmvplayer',file:"wmvplayer.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js',file:"swfobject.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.media.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/player',file:"player.multimedia.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/player',file:"player.multimedia_factory.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/player',file:"player.mediafragment.controller.js")}"></script>
	<script type="text/javascript" src="${resource(dir:'js/player',file:"player.js")}"></script>
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
	var appPath = "${grailsApplication.metadata['app.name']}";
	var mf_json = null; //pasrse the media fragment as json object
	var ctrler = null; //multimedia controller, including play media fragment
	var user = null;
	var mdHelper = null //helper to help embed microdata
	var userBaseURI = "${userBaseURI}";
	var resourceBaseURI = "${resourceBaseURI}";
	$(document).ready(function(){
		//Deal with the media fragment first
		
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
		mf_json = MediaFragments.parseMediaFragmentsUri(recordingURLStr);
		//console.log(mf_json);
		
		recording = new Object();
		recording.id = "${recording.id}";
		recording.title = "${recording.title?.encodeAsHTML()}";
		recording.url = "${recording.url.url}";//Yunjia: We have to think about it, if the url has fragment but the server cannot regonise it..., what should we do then
		recording.canEdit = "${canEdit}".toLowerCase();
		recording.canCreateSynmark = "${canCreateSynmark}".toLowerCase();

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
		mdHelper= new MicrodataHelper(true);
		initSynotePlayer(recording);
		//initShortCutKeys();
		
		//Start playing from media fragment is exisiting
		if(!$.isEmptyObject(mf_json.hash) || !$.isEmptyObject(mf_json.query))
		{
			//ctrler.start_playback();
		}
		
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
				<g:isLoggedIn>
				<div class="btn-group pull-right">
					<a class="btn btn-success" href="#">
						<i class="icon-user icon-white"></i>
						<g:loggedInUsername />
					</a>
					<a href="#" class="btn dropdown-toggle btn-success" data-toggle="dropdown">
						<span class="caret"></span>
					</a>
					<ul class="dropdown-menu">
				    	<li><g:link controller="user" action="showUserProfile" title="Show user profile">My Profile</g:link></li>
				    	<li><g:link controller="user" action="index" title="Edit my profile">My Synote</g:link></li>
				    	<g:isAdminLoggedIn>
						<li><g:link controller="admin" action="index" title="Administration">Administration</g:link></li>
						</g:isAdminLoggedIn>
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
				</g:isLoggedIn>
				<g:isNotLoggedIn>
				<div class="btn-group pull-right">
					<g:link controller="login" action="auth" title="Log in" elementId="main_login_a" title="Login to make annotations" class="btn btn-primary">
							Login</g:link>
					<g:allowRegistering>
						<g:link controller="register" action="index" title="Register" class="btn btn-success hidden-phone">
							 Register</g:link>
					</g:allowRegistering>
				</div>
				</g:isNotLoggedIn>
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
					<button id="nav_stop_btn" class="btn visible-desktop" title="stop"><i class="icon-stop"></i></button>
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
				<div id="recording_owner_div" itemprop="creator" itemscope="itemscope" itemtype="http://schema.org/Person" itemid="${g.getUserURI(recording.owner?.id.toString())}">
					<meta itemprop="familyName" content="${recording.owner?.firstName}"/>
					<meta itemprop="givenName" content="${recording.owner?.lastName}"/>
					<meta itemprop="email" content="${recording.owner?.email}"/>
					<span class="owner-info">by <g:link controller="user" action="show" id="${recording.owner?.id}" elementId="recording_owner_a" itemprop="name">
						${recording.owner?.userName}</g:link> |</span>
	  				<span class="datetime-info" itemprop="dateCreated">Created at <g:printSQLTime datetime="${recording.dateCreated}"/> |</span>
	  				<span class="datetime-info">${views} Views</span>
				</div>
			</div>
		</div>
		<!-- Player and Description-->
		<div class="container">
		<div class="row">
			<div id="col_left_div" class="player-fixed-width">
				<div style="margin-bottom:30px;">
					<div class="pull-left">
						<button style="" class="btn btn-success" id="control_mf" title="Play this fragment"><i class="icon-play-circle icon-white"></i>Play from</button>
					</div>
					<div id="control_time_div" class="pull-right">
						<span id="time_current_position">00:00</span> / <span id="time_duration_span">${TimeFormat.getInstance().toString(recording.duration)}</span>
					</div>
				</div>
				<div id="multimedia_player_error_div">
					<!-- attach error messages as another span class="error" here -->
				</div>
				<div id="recording_content_div" itemscope="itemscope" itemtype="http://schema.org/AuidoObject" 
				itemref="recording_title_div recording_owner_div created_time_span"> <!-- player -->
					<meta itemprop="contentURL" content="${recording.url.url}"/>
					<meta itemprop="dateModified" content="${new SimpleDateFormat("dd/MM/yyyy").format(recording.lastUpdated)}"/>
					
					<div id="multimedia_player_div">
					</div>
				</div><!-- end player -->
				<div id="recording_control_div" class="hidden-phone">
					<div style="display:inline;">
						<button id="control_play" title="Play" class="btn"><i class="icon-play"></i></button>
						<button id="control_pause" title="Pause" class="btn"><i class="icon-pause"></i></button>
						<button id="control_stop" title="Stop" class="btn"><i class="icon-stop"></i></button>
						<button id="control_rewind" title="Rewind" class="btn"><i class="icon-backward"></i></button>
						<button id="control_forward" title="Forward" class="btn"><i class="icon-forward"></i></button>
					</div>	
					<div id="control_pace_div" style="display:inline;">
						Pace:
						<select name="control_pace_select" class="span1" style="margin-top:9px;" id="control_pace_select">
							<option value="1">1s</option>
							<option value="5">5s</option>
							<option value="10">10s</option>
							<option value="20">20s</option>
						</select>
					</div>
					<div class="input-append pull-right" style="display:inline;margin-top:9px;">
						<input type="text" size="10" class="span1" name="control_goto_tb" id="control_goto_tb" value="00:00:00"/>
						<button id="control_goto" class="btn" title="Go to a certain time"><i class="icon-arrow-right"></i></button>
					</div>
				</div>
				<!-- Transcript -->
				<div id="transcripts_div" style="height:500px;" class="tab-pane span-left">
					<div>
						<h3 class="heading-inline">Transcript</h3>
						<div class="pull-right btn-toolbar" style="display:inline">
							<g:if test="${canEdit}">
							<div class="btn-group" id="transcript_edit_enter_div">
								<button class="btn" title="Add a new transcript block" id="edit_transcript_add_btn">
									<img src="${resource(dir:'images/player',file:"edit_transcript_add_22.png")}"  id="edit_transcript_add_img" title="Add new transcript block"/>
								</button>
								<button class="btn" title="Edit the selected transcript block" id="edit_transcript_enter_btn">	
									<img src="${resource(dir:'images/player',file:"edit_transcript_22.png")}"  id="edit_transcript_enter_img" title="Edit Transcript"/>
								</button>
								<button class="btn" title="Delete the selected transcript block" id="edit_transcript_clear_btn">	
									<img src="${resource(dir:'images/player',file:"edit_transcript_clear_22.png")}"  id="edit_transcript_clear_img" title="Remove all the transcripts"/>
								</button>
								<button class="btn" title="Transcript editing help" id="edit_transcript_help_btn">	
									<img src="${resource(dir:'images/player',file:"edit_transcript_help_22.png")}"  id="edit_transcript_help_img" title="Transcript editing help"/>
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
										<a href="#" title="download transcript as plain text format">Plain text</a>
									</li>
									<li>
										<a href="#" title="download transcript as srt format">SRT Format</a>
									</li>
									<li>
										<a href="#" title="download transcript as webvtt format">WebVTT Format</a>
									</li>
								</ul>
							</div>
						</div>
					</div>
					<div id="transcripts_inner_div">
						<div id="transcripts_content_div">
							<ol id="transcript_ol" style="list-style:none;"></ol>
						</div>
					</div>
				</div><!-- end transcript -->
			</div>
			
		
			<!-- synmarks and transcript -->
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
							<li class="active"><a href="#synmarks_div" data-toggle="tab">Synmarks</a></li>
							<li><a href="#slides_div" data-toggle="tab">Slides</a></li>
							<li class="visible-phone"><a href="#transcripts_div" data-toggle="tab">Transcripts</a></li>
						</ul>
						<div class="tab-content" id="tab_content_div">
							<div id="synmarks_div" class="tab-pane active span-middle" style="height:600px;">
								<h3>Synmarks</h3>
								<div id="synmarks_inner_div">
								<!-- Yunjia: Add tooltip to explain what is synmark, maybe add a picture to explain -->
								<!-- Yunjia: if I remove the hr all synmarks won't display correctly. I don't know why -->
									<div id="synmark_list_div"></div>
								</div>
							</div>
							<!-- Slides  -->
							<div id="slides_div" class="tab-pane span-right" style="height:600px;">
								<h3>Slides</h3>
								<div id="image_container_div">
								</div>	
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	</div><!-- container for content-->
	<g:render template="/common/footer"/>
</body>
</html>

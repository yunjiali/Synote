<%@ page import="java.text.SimpleDateFormat"%>
<html>
<head>
<title>${recording?.title}</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="fragment" content="!"/>
<meta name="layout" content="main" />
<link rel="stylesheet" href="${resource(dir: 'css', file: 'player.css')}" />
<link rel="stylesheet" href="${resource(dir: 'css/jquery', file: 'jCarousel.css')}" />
<link rel="stylesheet" href="${resource(dir: 'css/jquery', file: 'jquery.contextMenu.css')}" />
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform', file: 'uni-form.css')}" media="screen" charset="utf-8"/>
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform', file: 'default.uni-form.css')}" media="screen" charset="utf-8"/>

<script type="text/javascript" src="${resource(dir:'js',file:"Base.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:'jquery.combobox.js')}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:'jquery.form.js')}"></script>

<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.meerkat.1.3.min.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.maskedinput-1.3.min.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.jcarousel.min.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.contextMenu.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.url.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.timers.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.field_selection.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.scrollTo-1.4.2-min.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.okShortcut.min.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form.jquery.min.js')}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form-validation.jquery.min.js')}"></script>

<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.media.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:"microdataHelper.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/tiny_mce',file:"jquery.tinymce.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jwplayer',file:"jwplayer.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/wmvplayer',file:"silverlight.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/wmvplayer',file:"wmvplayer.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js',file:"swfobject.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"webvtt.parser.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"player.util.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"player.settings.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"player.timer.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"mediafragments.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"player.multimedia.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"player.multimedia_factory.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"player.mediafragment.controller.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"player.synmark.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"player.transcript.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"player.presentation.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"player.synmark.click.menu.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"player.transcript.click.menu.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"player.textselector.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"player.shortcutkey.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"player.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"edit.js")}"></script>

<script type="text/javascript">
	var recording = null;
	var appPath = "${grailsApplication.metadata['app.name']}";
	var mf_json = null; //pasrse the media fragment as json object
	var ctrler = null; //multimedia controller, including play media fragment
	var user = null;
	var position = 0;
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
		recording.title = "${recording.title}";
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
			//show login tooltips
			$("#main_login_a").wijtooltip({ showcallout: true, calloutFilled: false, position: { my: 'right bottom', at: 'left bottom' },
				triggers:"custom",closeBehavior:"sticky", content: "In Read Only version you can only watch the recording. Please login to make annotations.",
                height: 300, width: 400
            });
            $("#main_login_a").wijtooltip("show");
		}
		mdHelper= new MicrodataHelper(true);
		initSynotePlayer(recording);
		initShortCutKeys();

		if(recording.canEdit)
		{
			initEditMode(recording);
		}
		//Start playing from media fragment is exisiting
		if(!$.isEmptyObject(mf_json.hash) || !$.isEmptyObject(mf_json.query))
		{
			ctrler.start_playback();
		}
	})
</script>
</head>
<body>
	<!-- Yunjia: all the elements should have paddings -->
	<div id="multimedia_title_div" class="span-24 append-bottom">
		<div class="span-16">
			<div id="recording_title_div" style="padding:0 15px;" itemprop="name">${recording.title}</div>
			<div style="display:inline;" style="padding:0 15px;">
				<div id="recording_owner_div" itemprop="creator" itemscope="itemscope" itemtype="http://schema.org/Person" itemid="${g.getUserURI(recording.owner?.id.toString())}">
					by <g:link controller="user" action="show" id="${recording.owner?.id}" elementId="recording_owner_a" itemprop="name">
						${recording.owner?.userName}</g:link>
					<meta itemprop="familyName" content="${recording.owner?.firstName}"/>
					<meta itemprop="givenName" content="${recording.owner?.lastName}"/>
					<meta itemprop="email" content="${recording.owner?.email}"/>		
				</div>
				<!-- Yunjia: Add a link to the username, when user clicks, they can see other recordings from this user -->
				<!--  Yunjia: Use 18 days ago, 1 year ago, etc. We need javascript library to do it -->
				<div id="created_time_div">created <span id="created_time_span" itemprop="dateCreated">${new SimpleDateFormat("dd/MM/yyyy").format(recording.dateCreated)}</span></div>
			</div>
		</div>
		<div id="player_settings_div" class="right span-8 last" style="z-index:999">
			<button id="player_help_btn" class="right">Help</button>
			<button id="settings_btn" class="right">Settings</button>
			<!-- Yunjia: settings=> synchronised, autoscroll, auotscroll offset -->
			<ul id="settings_menu_ul" style="z-index:9999">
                <li><a href="#">Player Size</a>
                	<ul>
                		<!-- Yunjia: Disable this panel if it's audio -->
                		<li><span>
                            <input type="radio" name="player_size" id="player_size_radio" value="full" /><label for="player_size">Full Screen</label></span>
                        </li>
                		<li><span>
                            <input type="radio" name="player_size" id="player_size_radio" value="large"/><label for="player_size">640*480</label></span>
                        </li>
                        <li><span>
                            <input type="radio" name="player_size" id="player_size_radio" value="medium" checked="checked"/><label for="player_size">480*320</label></span>
                        </li>
                        <li><span>
                            <input type="radio" name="player_size" id="player_size_radio" value="small"/><label for="player_size">No Video</label></span>
                        </li>
                	</ul>
                </li>
                <li><a href="#">Synmarks</a>
                	<ul>
                		<li><span>
                            <input type="checkbox" name="synmark_auto_sync" id="synmark_sync_cb" checked="checked"/><label for="synmark_auto_sync">
                            Highlight Synmarks Automatically</label></span>
                        </li>
                        <li><span>
                            <input type="checkbox" name="synmark_auto_scroll" id="synmark_scroll_cb" checked="checked"/><label for="synmark_auto_scroll">
                            Scroll Synmark Panel Automatically</label></span>
                        </li>
                        <li></li>
                        <li><span>
                            <input type="checkbox" name="synmark_mine_only" id="synmark_mine_cb"/><label for="synmark_mine_only">
                            Display My Synmarks Only</label></span>
                        </li>
                        <!-- Yufnjia: this is not provided unless some asked
                        <li><span>
                            <input type="checkbox" name="synmark_tags_mine_only" id="synmark_tags_mine_cb"/><label for="synmark_tags_mine_only">
                           	Only Suggesting Tags I Used</label></span>
                        </li>
                         -->
                	</ul>
                </li>
                <li><a href="#">Transcripts</a>
                	<ul>
                		<li><span>
                            <input type="checkbox" name="transcript_auto_sync" id="transcript_sync_cb" checked="checked"/><label for="transcript_auto_sync">
                            Highlight Transcript Automatically</label></span>
                        </li>
                        <li><span>
                            <input type="checkbox" name="transcript_auto_scroll" id="transcript_scroll_cb" checked="checked"/><label for="transcript_auto_scroll">
                            Scroll Transcript Panel Automatically</label></span>
                        </li>
                	</ul>
                </li>
                <li><a href="#">Slides</a>
                	<ul>
                		<li><span>
                            <input type="checkbox" name="presentation_auto_sync" id="presentation_sync_cb" checked="checked"/><label for="presentation_auto_sync">
                            Highlight Presentation Automatically</label></span>
                        </li>
                        <li><span>
                            <input type="checkbox" name="presentation_auto_scroll" id="presentation_scroll_cb" checked="checked"/><label for="presentation_auto_scroll">
                            Scroll Presentation Panel Automatically</label></span>
                        </li>
                	</ul>
                </li>
                <li>
                	<a id="print_a" class="right" href="${createLink(controller:'recording',action:'print',id:recording.id)}" target="_blank">Print Friendly Version</a>
                </li>
            </ul>
            <!-- <button id="player_share_btn" class="right">Share</button> -->
           	<div class="span-8 right" style="padding-top:10px;">
           		<!-- facebook -->
				<div id="fb-root"></div>
				<script>(function(d, s, id) {
				  var js, fjs = d.getElementsByTagName(s)[0];
				  if (d.getElementById(id)) return;
				  js = d.createElement(s); js.id = id;
				  js.src = "//connect.facebook.net/en_GB/all.js#xfbml=1";
				  fjs.parentNode.insertBefore(js, fjs);
				}(document, 'script', 'facebook-jssdk'));</script>
				<div style="margin-left:10px;" class="fb-like right" data-send="false" data-layout="box_count" data-width="50" data-show-faces="false"></div>
           		<!--  twitter -->
	           	<a href="https://twitter.com/share" class="twitter-share-button right" data-count="vertical" data-hashtags="synote" data-related="synote">Tweet</a>
				<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src="//platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>
				<!-- Google plus -->
				<div class="right" style="margin-right:10px;">
					<script type="text/javascript" src="https://apis.google.com/js/plusone.js">
					  {lang: 'en-GB', parsetags: 'explicit'}
					</script>
					<div class="g-plusone" data-size="tall"></div>
					<script type="text/javascript">gapi.plusone.go();</script>
				</div>
			</div>
		</div>
	</div>
	<div class="span-24" id="player_configuration_panel_div" style="display:none;">
		<div id="player_configuration_share_div"></div>
		<div id="player_configuration_settings_div"></div>
	</div>
	<!-- Reserve it for larger video resolution -->
	<div id="recording_content_div_large"></div>
	<div class="span-13">
	<div id="recording_content_div" class="span-13" itemscope="itemscope" itemtype="http://schema.org/AuidoObject" 
			itemref="recording_title_div recording_owner_div created_time_span">
		<meta itemprop="contentURL" content="${recording.url.url}"/>
		<meta itemprop="dateModified" content="${new SimpleDateFormat("dd/MM/yyyy").format(recording.lastUpdated)}"/>
		<div style="padding-bottom:40px;">
			<div style="float:left;">
				<button style="display:none;" id="control_mf" title="Play this fragment">Play from</button>
			</div>
			<div id="control_time_div" style="text-align:right;float:right;">
				<span id="time_current_position">00:00</span> / <span id="time_duration_span">00:00</span>
			</div>
		</div>
		<div id="multimedia_player_error_div" class="error" style="display:none">
		</div>
		<div id="multimedia_player_div">
		</div>	
		<div id="recording_control_div">
			<button id="control_play" title="Play">Play</button>
			<button id="control_pause" title="Pause">Pause</button>
			<button id="control_stop" title="Stop">Stop</button>
			<button id="control_rewind" title="Rewind">Rewind</button>
			<button id="control_forward" title="Forward">Forward</button>
			<div id="control_pace_div" class="combowrap" style="display:inline-block;">
				Pace:
				<select name="control_pace_select" id="control_pace_select">
					<option value="1">1s</option>
					<option value="5">5s</option>
					<option value="10">10s</option>
					<option value="20">20s</option>
				</select>
			</div>
			<input type="text" size="10" name="control_goto_tb" id="control_goto_tb" value="00:00:00" style="font-size:1.1em;padding:0.4em;"/>
			<button id="control_goto" title="Go to a certain time">Goto</button>
			
		</div>
	</div>
	<div id="transcripts_div" class="span-13">
		<div id="transcripts_inner_div">
			<div class="span-13" id="transcript_widget_title">
				<div class="span-3"><h2 style="margin:0 !important;">Transcript</h2></div>
				<div class="span-9 append-1 last right">
					<div id="transcript_edit_enter_div">
						<g:if test="${canEdit}">
						<img class="transcript_menu_img right" src="${resource(dir:'images/player',file:"edit_transcript_22.png")}" alt="Edit Transcript" id="edit_transcript_enter_img" title="Edit Transcript"/>
						</g:if>
						<img class="transcript_menu_img right" src="${resource(dir:'images/player',file:"edit_transcript_export_22.png")}" alt="Export current transcript as .vtt file" id="edit_transcript_export_img" title="Export current transcript as .vtt file"/>
					</div>
					<g:if test="${canEdit}">
					<div id="transcript_edit_menu_div" style="display:none">
						<img class="transcript_menu_img left" src="${resource(dir:'images/player',file:"edit_transcript_add_22.png")}" alt="Add new transcript block" id="edit_transcript_add_img" title="Add new transcript block"/>
						<img class="transcript_menu_img left" src="${resource(dir:'images/player',file:"edit_transcript_import_22.png")}" alt="Import transcript from a file" id="edit_transcript_import_img" title="Import transcript from a file"/>
						<img class="transcript_menu_img left" src="${resource(dir:'images/player',file:"edit_transcript_clear_22.png")}" alt="Remove all the transcripts" id="edit_transcript_clear_img" title="Remove all the transcripts"/>
						<img class="transcript_menu_img left" src="${resource(dir:'images/player',file:"edit_transcript_revert_22.png")}" alt="Load transcript from saved draft" id="edit_transcript_revert_img" title="Load transcript from saved draft"/>
						<img class="transcript_menu_img left" src="${resource(dir:'images/player',file:"edit_transcript_save_draft_22.png")}" alt="Save current transcript as draft" id="edit_transcript_save_draft_img" title="Save current transcript as draft"/>					
						<img class="transcript_menu_img right" src="${resource(dir:'images/player',file:"edit_transcript_help_22.png")}" alt="Transcript editing help" id="edit_transcript_help_img" title="Transcript editing help"/>
						<img class="transcript_menu_img right" src="${resource(dir:'images/player',file:"edit_transcript_quit_22.png")}" alt="Quit transcript editing without saving" id="edit_transcript_quit_img" title="Quit transcript editing without saving"/>
						<img class="transcript_menu_img right" src="${resource(dir:'images/player',file:"edit_transcript_save_exit_22.png")}" alt="Save trancript and exit" id="edit_transcript_save_exit_img" title="Save trancript and exit"/>
					</div>
					</g:if>
				</div>
			</div>
			<g:if test="${canEdit}">
			<div class="span-12" id="transcript_edit_wrapper_div">
				<!-- Yunjia: The dialog doesn't look very nice -->
				<div id="transcript_edit_dialog"></div>
				<div class="span-12" id="transcript_upload_div" style="display:none">
				<form id="transcript_upload_form" method="post" enctype="multipart/form-data" class="uniForm">
					<div class="errorMsg" id="transcript_upload_errorMsg" style="display:none; margin:5px 0;padding:5px 5px;"></div>
					<div class="ctrlHolder inlineLabels">
					    <label for="transcript_upload_file"><em>*</em>Please select a file:</label>
						<input type='file' class="textInput long required" name='transcript_upload_file' id='transcript_upload_file' />
						<p class="formHint">Supported transcript formats are .txt, .srt and .xml files</p>
					</div>
					<div style="padding:4px 0;">
						<input class="left" id="transcript_file_submit" type="submit" value="Submit"/>
						<input class="left" id="transcript_file_cancel" type="reset" value="Exit" />
					</div>
				</form>
				</div>
				<div class="span-12" id="transcript_edit_div" style="display:none">
				<form id="transcript_edit_form" method="post" class="uniForm">
					<div class="errorMsg" id="transcript_edit_errorMsg" style="display:none; margin:5px 0;padding:5px 5px;"></div>
					<div class="ctrlHolder inlineLabels">
					    <label for="transcript_st"><em>*</em>Start:</label>
						<input type='text' size="10" class="textInput small required" name='transcript_st' id='transcript_st'/>
						<img style="cursor:pointer;" id="transcript_st_time" src="${resource(dir:'images/player',file:'time_24.png')}" alt="Get current time" title="Get current time"/>
						<img style="cursor:pointer;" id="transcript_st_add" src="${resource(dir:'images/player',file:'add_24.png')}" alt="add one second" title="add one second"/>
						<img style="cursor:pointer;" id="transcript_st_remove" src="${resource(dir:'images/player',file:'remove_24.png')}" alt="minus one second" title="minus one second"/>
					</div>
					
					<div class="ctrlHolder inlineLabels">
					    <label for="transcript_et"><em>*</em>End:</label>
						<input type='text' size="10" class="textInput small required" name='transcript_et' id='transcript_et'/>
						<img style="cursor:pointer;" id="transcript_et_time" src="${resource(dir:'images/player',file:'time_24.png')}" alt="Get current time" title="Get current time"/>
						<img style="cursor:pointer;" id="transcript_et_add" src="${resource(dir:'images/player',file:'add_24.png')}" alt="add one second" title="add one second"/>
						<img style="cursor:pointer;" id="transcript_et_remove" src="${resource(dir:'images/player',file:'remove_24.png')}" alt="minus one second" title="minus one second"/>
					</div> 
		
					<div class="ctrlHolder inlineLabels">
					    <label for="transcript_speaker">Speaker</label>
						<input type='text' size="255" autocomplete="off" class="textInput medium" name='transcript_speaker' id='transcript_speaker' value='' />
					</div>
					<div class="ctrlHolder inlineLabels" id="transcript_split_div" style="display:none;">
					    <label for="transcript_split_content">Selected Text:</label>
						<textarea name='transcript_split_content' style="height:5em !important" id='transcript_split_content' value='' ></textarea>
						<p class="formHint">Please select the text you want to separate from the textarea below</p>
					</div>
					<div class="ctrlHolder inlineLabels">
					    <label for="transcript_content">Transcript:</label>
						<textarea name='transcript_content' style="height:5em !important" id='transcript_content' value='' ></textarea>
					</div>
					<div style="padding:4px 0;">
						<input type="hidden" name="transcript_id" id="transcript_id"/> <!-- The id of srt.index -->
						<input class="left" id="transcript_submit" type="button" value="Submit"/><!-- This is not a submit button, because nothing will be submitted to the server -->
						<input class="left" id="transcript_cancel" type="reset" value="Exit" />
					</div>
				</form>
				</div>
			</div>
			</g:if>
			<hr/>
			<div id="transcripts_content_div">
				<ol id="transcript_ol" style="list-style:none;"></ol>
			</div>
		</div>
	</div>
	</div>
	<div id="synmarks_div" class="span-11 last append-bottom">
		<div id="synmarks_inner_div">
		<!-- Yunjia: Add tooltip to explain what is synmark, maybe add a picture to explain -->
		<div class="span-10" id="symnmark_widget_title">
			<div class="span-4"><h2 style="margin:0 !important;">Synmarks</h2></div>
			<div class="span-4 last right">
				<img src="${resource(dir:'images/player',file:"document_export_22.png")}" alt="Export Synmarks as csv file" id="export_synmark_img" title="Export Synmarks as csv file"/>
				<g:if test="${canCreateSynmark}">
				<img src="${resource(dir:'images/player',file:"bookmark_add_22.png")}" alt="Add Synmark" id="add_synmark_img" title="Add Synmark"/>
				</g:if>
			</div>
		</div>
		<g:if test="${canCreateSynmark}">
		<div class="span-10" id="synmark_create_div" style="display:none;">
		<!-- Yunjia: The dialog doesn't look very nice -->
		<div id="synmark_dialog"></div>
		<form id="synmark_form" method="post" action="" class="uniForm">
			<div class="errorMsg" id="synmark_create_errorMsg" style="display:none; margin:5px 0;padding:5px 5px;"></div>
			<div class="ctrlHolder inlineLabels">
			    <label for="synmark_st"><em>*</em>Start:</label>
				<input type='text' size="10" class="textInput small required" name='synmark_st' id='synmark_st' value="" />
				<img style="cursor:pointer;" id="synmark_st_time" src="${resource(dir:'images/player',file:'time_24.png')}" alt="Get current time" title="Get current time"/>
				<img style="cursor:pointer;" id="synmark_st_add" src="${resource(dir:'images/player',file:'add_24.png')}" alt="add one second" title="add one second"/>
				<img style="cursor:pointer;" id="synmark_st_remove" src="${resource(dir:'images/player',file:'remove_24.png')}" alt="minus one second" title="minus one second"/>
			</div>
			
			<div class="ctrlHolder inlineLabels">
			    <label for="synmark_et">End:</label>
				<input type='text' size="10" class="textInput small" name='synmark_et' id='synmark_et' value="" />
				<img style="cursor:pointer;" id="synmark_et_time" src="${resource(dir:'images/player',file:'time_24.png')}" alt="Get current time" title="Get current time"/>
				<img style="cursor:pointer;" id="synmark_et_add" src="${resource(dir:'images/player',file:'add_24.png')}" alt="add one second" title="add one second"/>
				<img style="cursor:pointer;" id="synmark_et_remove" src="${resource(dir:'images/player',file:'remove_24.png')}" alt="minus one second" title="minus one second"/>
			</div> 

			<div class="ctrlHolder inlineLabels">
			    <label for="synmark_title">Title</label>
				<input type='text' size="255" autocomplete="off" class="textInput long" name='synmark_title' id='synmark_title' value='' />
			</div>
			<div class="ctrlHolder inlineLabels">
			    <label for="synmark_tags">Tags:</label>
				<input type='text' size="255" autocomplete="off" class="textInput long" name='synmark_tags' id='synmark_tags' value='' />
				<p class="formHint">Please separate tags by comma ','</p>
			</div>
			
			<div class="ctrlHolder">
			    <label for="synmark_note">Note:</label>
			    <textarea id="synmark_note" name="synmark_note" class="tinymce" cols="25" rows="25"></textarea>
				<!--  <textarea name='synmark_note' style="height:5em !important" id='synmark_note' value='' ></textarea>-->
			</div>
			<!-- 
			<div class="ctrlHolder inlineLabels">
			    <label for="synmark_next">Next</label>
				<input type='text' autocomplete="off" class="textInput medium required" name='synmark_next' id='synmark_next' value='' />
			</div>
			 -->
			<div style="padding:4px 0;">
				<input type="hidden" name="synmark_id" id="synmark_id" value=""/>
				<input class="left" id="synmark_submit" type="submit" value="Submit"/>
				<input class="left" id="synmark_cancel" type="reset" value="Exit" />
			</div>
		</form>
		</div>
		</g:if>
		<!-- Yunjia: if I remove the hr all synmarks won't display correctly. I don't know why -->
		<hr/>
		<div id="synmark_list_div"></div>
		<div id="synmark_url_dialog" title="The URL of this Synmark is:"></div>
	</div>
	</div>
	<div id="slides_div">
		<div id="slides_bar_div">
			<button id="slides_play_btn" class="left slides_btn" title="play"></button>
			<button id="slides_pause_btn" class="left slides_btn" title="pause"></button>
			<button id="slides_stop_btn" class="left slides_btn" title="stop"></button>
			<button id="slides_rewind_btn" class="left slides_btn" title="rewind"></button>
			<button id="slides_forward_btn" class="left slides_btn" title="foward"></button>
			<button id="slides_collapse_expand_btn" class="slides_div_collapse right slides_btn"></button>
			<span id="slides_count_span"></span>
			<g:if test="${canEdit}">
				<button id="slides_delete_btn" class="right slides_btn" title="delete slide"></button>
				<button id="slides_edit_btn" class="right slides_btn" title="edit slide"></button>
				<button id="slides_add_btn" class="right slides_btn" ttle="add slide"></button>
			</g:if>
		</div>
		<div id="presentation_edit_dialog" style="display:none">
			<form id="presentation_edit_form" method="post" class="uniForm">
				<div class="errorMsg" id="slide_edit_errorMsg" style="display:none; margin:5px 0;padding:5px 5px;"></div>
				<div class="ctrlHolder inlineLabels">
					<label for="slide_st"><em>*</em>Start:</label>
					<input type='text' size="10" class="textInput small required" name='slide_st' id='slide_st'/>
					<img style="cursor:pointer;" id="slide_st_time" src="${resource(dir:'images/player',file:'time_24.png')}" alt="Get current time" title="Get current time"/>
					<img style="cursor:pointer;" id="slide_st_add" src="${resource(dir:'images/player',file:'add_24.png')}" alt="add one second" title="add one second"/>
					<img style="cursor:pointer;" id="slide_st_remove" src="${resource(dir:'images/player',file:'remove_24.png')}" alt="minus one second" title="minus one second"/>
				</div>
				<div class="ctrlHolder inlineLabels">
					<label for="slide_et">End:</label>
					<input type='text' size="10" class="textInput small" name='slide_et' id='slide_et'/>
					<img style="cursor:pointer;" id="slide_et_time" src="${resource(dir:'images/player',file:'time_24.png')}" alt="Get current time" title="Get current time"/>
					<img style="cursor:pointer;" id="slide_et_add" src="${resource(dir:'images/player',file:'add_24.png')}" alt="add one second" title="add one second"/>
					<img style="cursor:pointer;" id="slide_et_remove" src="${resource(dir:'images/player',file:'remove_24.png')}" alt="minus one second" title="minus one second"/>
				</div>
				<div class="ctrlHolder inlineLabels">
					<label for="slide_index"><em>*</em>Slide index:</label>
					<div class="combowrap">
						<select name='slide_index' id='slide_index'></select>
					</div>
				</div>
				<div class="ctrlHolder inlineLabels">
					<label for="slide_url"><em>*</em>Slide url:</label>
					<input type='text' class="textInput medium required" name='slide_url' id='slide_url'/>
				</div>
				<div style="padding:4px 0;">
					<input type="hidden" name="slide_id" id="slide_id"/> <!-- The id of srt.index -->
					<input type="hidden" name="old_index" id="old_index"/>
					<input class="left" id="presentation_submit" type="submit" value="Submit"/><!-- This is not a submit button, because nothing will be submitted to the server -->
					<input class="left" id="presentation_cancel" type="reset" value="Cancel" />
				</div>
			</form>
		</div>
		<div id="presentation_dialog"></div>
		<div id="image_container_div">
		</div>	
	</div>
</body>
</html>

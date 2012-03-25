<%@ page import="java.text.SimpleDateFormat"%>
<html>
<head>
<title><g:message code="org.synote.player.server.recording.replay.title"/></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<link rel="stylesheet" href="${resource(dir: 'css', file: 'player.css')}" />
<link rel="stylesheet" href="${resource(dir: 'css/jquery', file: 'jCarousel.css')}" />
<link rel="stylesheet" href="${resource(dir: 'css/jquery', file: 'jquery.contextMenu.css')}" />
<script type="text/javascript" src="${resource(dir: 'js/jquery', file: 'jquery.combobox.js')}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.meerkat.1.3.min.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.jcarousel.min.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.contextMenu.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.url.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jwplayer',file:"jwplayer.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/wmvplayer',file:"wmvplayer.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/wmvplayer',file:"silverlight.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js',file:"swfobject.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"player.util.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"player.settings.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"Base.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"player.multimedia.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"player.multimedia_factory.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"player.synmark.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"player.transcript.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"player.presentation.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"player.click.menu.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/player',file:"edit.js")}"></script>

<script type="text/javascript">
	$(document).ready(function(){
		var recording = new Object();
		recording.id = "${recording.id}";
		recording.title = "${recording.title}";
		recording.url = "${recording.url.url}";

		var user = new Object();
		user.id = "${user.id}";
		user.user_name = "${user.userName}";
		initEditMode(recording);
	})
</script>
</head>
<body>
	<!-- Yunjia: all the elements should have paddings -->
	<div id="multimedia_title_div" class="span-24 append-bottom">
		<div class="span-16">
			<div id="recording_title_div" style="padding:0 15px;">${recording.title}</div>
			<div style="display:inline;" style="padding:0 15px;">
				<div id="recording_owner_div">by <a>${recording.owner.userName}</a></div>
				<!-- Yunjia: Add a link to the username, when user clicks, they can see other recordings from this user -->
				<!--  Yunjia: Use 18 days ago, 1 year ago, etc. We need javascript library to do it -->
				<div id="created_time_div">created <span id="created_time_span">${new SimpleDateFormat("dd/MM/yyyy").format(recording.dateCreated)}</span></div>
			</div>
		</div>
		<div id="player_settings_div" class="right span-8 last">		
		</div>
	</div>
	<!-- Transcript takes the whole page space -->
	<div id="transcripts_div" class="span-24">
		<div id="transcripts_inner_div">
			<div id="transcripts_content_div"></div>
		</div>
	</div>
	<!-- The rest, such as recording, synmark, images will just show in a floating bar -->
	<div id="edit_accordion" class="span-13">
		<h2><a href="#">Player</a></h2>
		<div id="recording_content_div" class="span-13">
			<div id="multimedia_player_div">
				This will be replaced by the SWF
			</div>	
			<div id="recording_control_div">
				<button id="control_play" title="Play">Play</button>
				<button id="control_pause" title="Pause">Pause</button>
				<button id="control_stop" title="Stop">Stop</button>
				<button id="control_rewind" title="Rewind">Rewind</button>
				<button id="control_forward" title="Forward">Forward</button>
				<div id="control_pace_div" class="combowrap" style="display:inline-block;">
					Pace:
					<select id="control_pace_select">
						<option value="1">1s</option>
						<option value="5">5s</option>
						<option value="10">10s</option>
						<option value="20">20s</option>
					</select>
				</div>
				<div id="control_time_div">
					<span id="time_current_position">00:00:00</span> / <span id="time_duration_span">00:25:22</span>
				</div>
			</div>
		</div>
		<h2><a href="#">Synmarks</a></h2>
		<div id="synmarks_div" class="span-13">
			<!-- Yunjia: Add tooltip to explain what is synmark, maybe add a picture to explain -->
			<div class="span-10" id="symnmark_widget_title">
				<div class="span-4 last right">
					<button id="synmark_create_button" style="float:right;">Create</button>
				</div>
			</div>
			<!-- Yunjia: if I remove the hr all synmarks won't display correctly. I don't know why -->
			<div id="synmark_list_div"></div>
		</div>
		<h2><a href="#">Slides</a></h2>
		<div id="slides_div">
			<div id="slides_bar_div">
				<span id="slides_count_span"></span>
				<button id="slides_collapse_expand_btn" class="slides_div_collapse"></button>
			</div>
		</div>
	</div>
</body>
</html>

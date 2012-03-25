<html>
<head>
<title><g:message code="org.synote.home.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<link rel="stylesheet" type="text/css" href="${resource(dir:'css/jquery',file:"jquery.slides.css")}" media="screen, projection" />
<link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:"calendar.css")}" media="screen, projection" />
<script type="text/javascript" src="${resource(dir:'js/jquery',file:"easySlider1.7.js")}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.zrssfeed.min.js")}"></script>
<script type="text/javascript">
$(document).ready(function(){
	$("#slider").easySlider({
		auto: false, 
		continuous: true,
		numeric: true
	});
	$("#try_button").button();
	$("#lastest_a").button();
	
	//Get latest recordings
	//var multimedia_list_url = g.createLink({controller:'multimediaResource',action:'listMultimediaAjax'});
	/*$.ajax({
				type: "GET",
				url: multimedia_list_url,
				data: "_search=false&page=1&rows=5&sidx=id&sord=desc",
				dataType: "json",
				success:function(data)
				{
				   	var latest_recordings_div = $("#latest_recordings_div");
				   	$.each(data.rows,function(i,entry){
				   		var recording_div = $("<div/>");
				   		var title_span = $("<span/>");
				   		var title_a = $("<a/>",{
				   			title: entry.title,
				   			href: g.createLink({controller:'recording',action:'replay'})+"/"+entry.id,
				   			target:"_blank",
				   			text: entry.title
				   		}).appendTo(title_span);
				   		title_span.appendTo(recording_div);
				   		var creator_span = $("<span/>",{
				   			html:"&nbsp;&nbsp;<i>by "+entry.owner_name+"</i>"
				   		}).appendTo(recording_div);
				   		recording_div.appendTo(latest_recordings_div);
				   		//if(i!=data.rows.length-1)
				   		//{
				   		//	$("<br/>").appendTo(latest_recordings_div);
				   		//}
				   	});
				   	$("<br/>").appendTo(latest_recordings_div);
				   	var more_recordings_div=$("<div/>").addClass("right").appendTo(latest_recordings_div);
				   	var more_recordings_a = $("<a/>",{
				   		target:"_self",
				   		href:g.createLink({controller:"multimediaResource",action:"list"}),
				   		text:"More Recordings..."
				   	}).appendTo(more_recordings_div);
			  	}
	});*/
	
	//Get the RSS feed from Synote blog
	$("#blog_feed_div").rssfeed("http://blog.lsl.ecs.soton.ac.uk/synote/feed/",{
		limit:7,
		content:false,
		titletag:"h3"
	})
});
</script>
<style type="text/css" media="screen">
   /*I didn't use the original zrssfeed css file*/
   .rssFeed {
		font-family: Arial, Helvetica, sans-serif;
		font-size:90%;
	}
	.rssFeed a {
		color: #444;
		text-decoration: none;
	}
	.rssFeed a:hover {
		color: #000;
		text-decoration: underline;
	}
	
	.rssHeader { padding: 0.2em 0; }
	
	.rssBody { border: 0; }
	.rssBody ul { list-style: none; }
	.rssBody ul, .rssRow, .rssRow h4, .rssRow p {
		margin: 0;
		padding: 0;
	}
	
	.rssRow h4 { font-size: 1.1em; }
	.rssRow div {
		font-size: 90%;
		color: #666;
		margin: 0.2em 0 0.4em 0;
	}
	
	.rssRow .rssMedia {
		padding: 0.5em;
		font-size: 1em;
	}
   
</style>
</head>
<body>
	<h1 class="hiding"><g:message code="org.synote.home.title" /></h1>
	<div class="span-24" style="height:280px;text-align:middle;">
		<h2 class="hiding">Easy guide to working with your research media and annotations</h2>
		<div id="slider">
			<ul>		
				<li>
					<div class="span-22 append-1 prepend-1">
						<div class="span-11 append-1">
							<h3>Step 1</h3>
							<p>Register or Login using a username and password.</p>
							<p>Search for a recording you have made or for someone else's recording by title, tag or keywords.</p>
							<p>Refine your search by going to My Synote, select My Resources,  My Groups or My Tags.</p>
						</div>
						<div class="span-10 right last">
							<img src="${resource(dir:'images',file:"Step1small.jpg")}" alt="Step 1" />	
						</div>
					</div>
				</li>
				<li><div class="span-22 append-1 prepend-1">
						<div class="span-11 append-1">
							<h3>Step 2</h3>
							<p>Just want to listen or annotate a recording? Select the 'Recordings' button to view a list of public recordings and 'Play' , view 'Details' or 'Print' information about the recording.</p>
							<p>You may be able to annotate or change the transcript - use 'Edit Transcript' or 'Add Synmark' if available.</p>
							<p>Right-hand mouse button will reveal more options for editing.</p>
						</div>
						<div class="span-10 right last">
							<img src="${resource(dir:'images',file:"Step2small.jpg")}" alt="Step 1" />	
						</div>
					</div></li>
				<li><div class="span-22 append-1 prepend-1">
						<div class="span-11 append-1">
							<h3>Step 3</h3>
							<p>To make your own recording  - select 'Create' then 'Create a recording'.</p>
							<p>Choose how you wish to upload your recording, add details and set permissions.</p>
							<p>The 'i' button provides more information.</p>
							<p>Submit your recording and if you go to 'Play you will then be able to access the recording as mentioned in Step 2 and Step 4.</p>
						</div>
						<div class="span-10 right last">
							<img src="${resource(dir:'images',file:"Step3small.jpg")}" alt="Step 1" />	
						</div>
					</div></li>
				<li><div class="span-22 append-1 prepend-1">
						<div class="span-11 append-1">
							<h3>Step 4</h3>
							<p>Make a transcript for a recording by selecting the 'Edit Transcript' then 'Add ...'</p>
							<p>Type or import text. You can play, edit, merge, split or delete transcripts by using the right-hand mouse features.</p>
							<p>View your recording by returning to the 'Recordings' menu button.</p>
							<p>Change the look and feel of the player by using the 'Settings' button.</p>
						</div>
						<div class="span-10 right last">
							<img src="${resource(dir:'images',file:"Step4small.jpg")}" alt="Step 1" />	
						</div>
					</div></li>
				<li><div class="span-22 append-1 prepend-1">
						<div class="span-11 append-1">
							<h3>Step 5</h3>
							<p>Add synchronised annotations or notes (synmarks) at selected points in the recording by selecting the 'create' button in the Synmark panel.  You can edit and delete your synmarks.</p>
							<p>Use the 'Title' and 'Tags' to aid coding and searches.</p>
							<p>Add a note - sections can be colour coded or underlined and spell checked.</p>
						</div>
						<div class="span-10 right last">
							<img src="${resource(dir:'images',file:"Step5small.jpg")}" alt="Step 1" />	
						</div>
					</div></li>
			</ul>
			<span>view demo</span>
		</div>		
	</div>
	<div class="span-24 prepend-top">
		<div class="span-12">
			<div id="synote_intro" class="panelBox">
				<!-- Yunjia: Use jquery ui css framework to style the header, such as: -->
				<!-- class="ui-help-reset ui-widget-header ui-corner-all" style="padding:.5em;" -->
				<div class="titleHeader">
					<h2>
						What is Synote
					</h2>
				</div>
				<hr/>
				<div style="height:360px;" class="mainBody">
					<div class="span-12">
						<div class="span-2">
							<img src="${resource(dir:'images',file:"sync_48.png")}" title="synchronised"/>
						</div>
						<div class="span-9 append-1 last">
							<h3>Synchronised</h3>
							<p>Listen to recordings whilst viewing the transcript and slides and making synchronised notes.</p>
						</div>
					</div>
					<div class="span-12">
						<div class="span-2">
							<img src="${resource(dir:'images',file:"media_fragment_48.png")}" title="media fragment"/>
						</div>
						<div class="span-9 append-1 last">
							<h3>Media Fragments</h3>
							<p>Annotate only a certain part of audio-visual resources from all over the Web and share them online. </p>
						</div>
					</div>
					<div class="span-12">
						<div class="span-2">
							<img src="${resource(dir:'images',file:"linkeddata_48.png")}" title="linked data"/>
						</div>
						<div class="span-9 append-1 last">
							<h3>Linked Data</h3>
							<p>Automatically publish media fragments and annotations using Linked Data and they are searchable by Google.
							<g:link controller="user" action="help" fragment="linkeddata">Learn more...</g:link></p>
						</div>
					</div>
					<div class="span-12">
						<div class="span-2">
							<img src="${resource(dir:'images',file:"speech_recognition_48.png")}" title="speech recognition"/>
						</div>
						<div class="span-9 append-1 last">
							<h3>Speech Recognition</h3>
							<p>Transcripts can be automatically uploaded via speech recognition if linked to this type of service.</p>
						</div>
					</div>	
					<!--
						<li>
							Synote can be linked to Twitter comments that appear as notes synchronised with a discussion

						</li>
						<li>
							Synote transcripts can be automatically uploaded via speech recognition if linked to this type of service.
						</li>
						<li>
						</li>
						<li>
							See <g:link controller="user" action="help" title="help">Help page</g:link> and <g:link controller="user" action="help" title="help" fragment="demo" title="demo">Demo</g:link> more information.
						</li>
					</ul>-->
					<a id="try_button" href="register" style="float:right;">Create an Account Now>></a>
				</div>
			</div>
		</div>
		<div class="span-12 last">
			<div id="demo_video" class="panelBox">
				<div class="titleHeader">
					<h2>
						Latest Recordings
					</h2>
				</div>
				<hr/>
				<div id="latest_recordings_div" style="height:360px;padding-left:2em;overflow:hidden;" class="mainBody">
					<g:getLatestRecordings rows="4"/>
					<div class="right">
						<g:link controller="multimediaResource" action="list" elementId="lastest_a">More Recordings>></g:link>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="span-24 prepend-top">
		<div class="span-9">
			<script charset="utf-8" src="http://widgets.twimg.com/j/2/widget.js"></script>
			<script>
			new TWTR.Widget({
			  version: 2,
			  type: 'search',
			  search: 'from:afterglowlee OR from:EADraffan OR @synote OR #synote',
			  interval: 30000,
			  title: 'Synote on Twitter',
			  subject: '',
			  width: 'auto',
			  height: 300,
			  theme: {
			    shell: {
			      background: '#8ec1da',
			      color: '#ffffff'
			    },
			    tweets: {
			      background: '#ffffff',
			      color: '#444444',
			      links: '#1985b5'
			    }
			  },
			  features: {
			    scrollbar: false,
			    loop: true,
			    live: true,
			    behavior: 'default'
			  }
			}).render().start();
			</script>
			
		</div>
		<div class="span-9">
			<div id="synote_blog" class="panelBox">
				<div class="titleHeader">
					<h2>
						Blogs
					</h2>
				</div>
				<hr/>
				<div id="blog_feed_div" class="mainBody">
				</div>
			</div>
		</div>
		<div class="span-6 last">
			<div id="synote_stat" class="panelBox">
				<div class="titleHeader">
					<h2>
						Synote Facts	
					</h2>
				</div>
				<hr/>
				<div style="font-size:1.1em;" class="mainBody">
					We now have:
					<!-- Yunjia: Write an api for counting users, recordings and synmarks later -->
					<ul>
						<li>
							<g:getUserCount/> Users
						</li>
						<li>
							<g:getRecordingCount/> Recordings
						</li>
						<li>
							<g:getSynmarkCount/> Synmarks
						</li>
						<li>
							<g:getGroupCount/> Groups
						</li>
					</ul>
					<div>
						<!-- facebook -->
						<div id="fb-root"></div>
						<script>(function(d, s, id) {
						  var js, fjs = d.getElementsByTagName(s)[0];
						  if (d.getElementById(id)) return;
						  js = d.createElement(s); js.id = id;
						  js.src = "//connect.facebook.net/en_GB/all.js#xfbml=1";
						  fjs.parentNode.insertBefore(js, fjs);
						}(document, 'script', 'facebook-jssdk'));</script>
						<div class="fb-like" data-send="false" data-width="200" data-show-faces="false"></div>
						<!-- Google plus -->
						<div style="margin-right:10px;">
							<script type="text/javascript" src="https://apis.google.com/js/plusone.js">
							  {lang: 'en-GB', parsetags: 'explicit'}
							</script>
							<div class="g-plusone" data-annotation="inline"></div>
							<script type="text/javascript">gapi.plusone.go();</script>
						</div>
						<!--  twitter -->
			           	<a href="https://twitter.com/synote" class="twitter-follow-button" data-show-count="false" data-size="large">Follow @synote</a>
						<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src="//platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>

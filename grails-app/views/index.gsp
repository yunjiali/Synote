<html>
<head>
<title><g:message code="org.synote.home.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<link rel="stylesheet" type="text/css" href="${resource(dir:'css/jquery',file:"jquery.slides.css")}" media="screen, projection" />
<link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:"calendar.css")}" media="screen, projection" />
<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jquery.zrssfeed.min.js")}"></script>
<script type="text/javascript">
$(document).ready(function(){
	
	//start the carousel
	$('.carousel').carousel();
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
	<div class="row">
		<div class="span5">
			<h2>What is Synote</h2>
			<p>
				Make a transcript for a recording by selecting the 'Edit Transcript' then 'Add ...'
				Type or import text. You can play, edit, merge, split or delete transcripts by using the right-hand mouse features.
				your recording by returning to the 'Recordings' menu button.
				Change the look and feel of the player by using the 'Settings' button.
			</p>
		</div>
		<div class="span6 offset1">
			<div id="slider" class="carousel">
			    <!-- Carousel items -->
			    <div class="carousel-inner">
				    <div class="carousel-inner">
						<div class="item">
							<img src="${resource(dir:'images',file:'Step1small.jpg')}" alt="Step 1" />	
							<div class="carousel-caption">
								<h4>Safe and Secure</h4>
							<div>256-bit SSL data encryption</div>
								<div>firewall protected servers</div>
						</div>
					</div>
						<div class="item">
							<img src="${resource(dir:'images',file:'Step2small.jpg')}" alt="Step 2" />
							<div class="carousel-caption">
								<h4>Easy and Flexible</h4>
								<div>intuitive and user-friendly interface</div>
									<div>working on desktop and mobile devices</div>
								</div>
							</div>
							<div class="item">
								<img src="${resource(dir:'images',file:'Step3small.jpg')}" alt="Step 3" />	
								<div class="carousel-caption">
									<h4>Powerful and Universal</h4>
									<div>advanced reports and charts</div>
									<div>external services integration</div>
								</div>
							</div>
						</div>
				    </div>
				    <!-- Carousel nav -->
				    <a class="carousel-control left" href="#slider" data-slide="prev">&lsaquo;</a>
				    <a class="carousel-control right" href="#slider" data-slide="next">&rsaquo;</a>
				</div>
			</div>
		</div>
</body>
</html>

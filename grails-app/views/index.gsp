<html>
<head>
<title><g:message code="org.synote.home.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
</head>
<body>
	<h1 class="hiding"><g:message code="org.synote.home.title" /></h1>
	<!-- Synote introduction and slide show -->
	<div class="row">
		<div class="span5">
			<h2>What is Synote</h2>
			<p>
				Synote makes multimedia resources such as video and audio easier to access, search, manage, and exploit. Learners, teachers and other users can create notes, bookmarks, tags, links, images and text captions synchronised to any part of a recording, such as a lecture.
			</p>
			<p>Imagine how difficult it would be to use a textbook if it had no contents page, index or page numbers. Synote actually provides the way to find or associate notes with a particular part of a recording,i.e. the media fragments.
			</p>
			<div>
				<g:link controller="user" action="help" fragment="step" title="5 steps quick start" class="btn btn-info">
					 5 Steps quick start</g:link>	 
				<g:link controller="register" action="index" title="Register" class="btn btn-success">
					 Get a free account</g:link>
			</div>
		</div>
		<div class="span6 offset1">
			<div id="slider" class="carousel">
			    <!-- Carousel items -->
			    <div class="carousel-inner">
				    <div class="carousel-inner">
						<div class="item">
							<img src="${resource(dir:'images',file:'test/web_design.jpg')}" alt="Step 1" />	
							<div class="carousel-caption">
								<h4>Share your annotations on the Web</h4>
							</div>
						</div>
						<div class="item">
							<img src="${resource(dir:'images',file:'test/varioggetti.jpg')}" alt="Step 2" />
							<div class="carousel-caption">
								<h4>Working on desktop and mobile devices</h4>
							</div>
						</div>
						<div class="item">
							<img src="${resource(dir:'images',file:'test/seo.jpg')}" alt="Step 3" />	
							<div class="carousel-caption">
								<h4>Search engine optimisation for media fragments</h4>
							</div>
						</div>
				    </div>
				    <!-- Carousel nav -->
				    <a class="carousel-control left" href="#slider" data-slide="prev">&lsaquo;</a>
				    <a class="carousel-control right" href="#slider" data-slide="next">&rsaquo;</a>
				</div>
			</div>
			<script type="text/javascript">
				//start the carousel
				$('.carousel').carousel({
					interval: 2000
				});
			</script>
		</div>
	</div>
	<div class="row block">
		<div class="span12">
			<div class="row">
				<div class="span1">
					<img src="${resource(dir:'images',file:"multimedia_48.png")}" title="annotation multimedia online"/>
				</div>
				<div class="span3">
					<h3>Multimedia Online</h3>
					<p>Annotate most auido and video resources online as long as they are accessible through URLs, including YouTube video.</p>
				</div>
				<div class="span1">
					<img src="${resource(dir:'images',file:"sync_48.png")}" title="synchronised"/>
				</div>
				<div class="span3">
					<h3>Synchronised</h3>
					<p>Listen to recordings whilst viewing the transcript and slides and making synchronised notes.</p>
				</div>
				<div class="span1">
					<img src="${resource(dir:'images',file:"media_fragment_48.png")}" title="media fragment"/>
				</div>
				<div class="span3">
					<h3>Media Fragments</h3>
					<p>Annotate only a certain part of audio-visual resources from all over the Web and share them online. </p>
				</div>
			</div>
			<div class="row">
				<div class="span1">
					<img src="${resource(dir:'images',file:"linkeddata_48.png")}" title="linked data"/>
				</div>
				<div class="span3">
					<h3>Linked Data</h3>
					<p>Automatically publish media fragments and annotations using Linked Data and they are searchable by Google.
					<g:link controller="user" action="help" fragment="linkeddata">Learn more...</g:link></p>
				</div>
				<div class="span1">
					<img src="${resource(dir:'images',file:"speech_recognition_48.png")}" title="speech recognition"/>
				</div>
				<div class="span3">
					<h3>Speech Recognition</h3>
					<p>Transcripts can be automatically uploaded via speech recognition if linked to this type of service.</p>
				</div>
				<div class="span1">
					<img src="${resource(dir:'images',file:"pda_48.png")}" title="mobile access"/>
				</div>
				<div class="span3">
					<h3>Mobile Access</h3>
					<p>Check your annotations and transcript on mobile devices, such as iPhone, iPad and Android tablet.
						<a href="http://users.ecs.soton.ac.uk/wj1e11/synotemobile/" target="_blank">Go to the demo</a></p>
				</div>
			</div>
		</div>
	</div>
</body>
</html>

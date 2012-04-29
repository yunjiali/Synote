<%@ page import="org.synote.user.User"%>
<%@ page import="org.synote.user.group.UserGroup"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title><g:message code="org.synote.user.help.title" /></title>
</head>
<body>
<h1 id="help">Synote Help</h1>
<div id="help_content">
	<h2>Contents</h2>
	<ul>
		<li><a href="#step">Step by Step to Synote Researcher</a></li>
		<li><a href="#demo">Demo Video</a>
		<li><a href="#googleindex">Let Google Index Your Media Fragment</a></li>
		<li><a href="#linkeddata">Publishing Media Fragments and Annotations as Linked Data</a></li>
	</ul>
</div>
<h2 id="step">Step by Step to Synote Researcher</h2>
<hr/>
This is an easy guide to working with your research media and annotations
	<h3 id="step1">Step 1</h3>
	<ul>
		<li><b>Register</b> as a first time user or</li>
		<li><b>Login</b> using a suitable username and password or</li>
		<li>Search for a recording you have made or for someone else’s recording by title, tag or keywords or browse through <b>Recordings</b>.</li> 
		<li>Refine your search by going to <b>My Synote</b>, select <b>My Resources</b>,  <b>My Groups</b> or <b>My Tags</b>.</li>
	</ul>
	<img src="${resource(dir:'images',file:"Step1small.jpg")}" alt="Step 1" />	
	<h3 id="step2">Step 2</h3>
	<ul>
		<li>Just want to listen or annotate a recording? Select the <b>Recordings</b> button to view a list of public recordings and <b'Play'</b> , 
			view <b>'Details'</b> or <b>'Print'</b> information about the recording. </li>
		<li>You may be able to annotate or change the transcript – use <b>'Edit Transcript'</b> or <b>'Add Synmark'</b> if available. </li>
		<li><b>Right-hand mouse button</b> will reveal more options for editing. </li> 
	</ul>
	<img src="${resource(dir:'images',file:"Step2small.jpg")}" alt="Step 2" />	
	<h3 id="step3">Step 3</h3>
	<ul>
		<li>To make your own recording  - select <b>'Create'</b> then <b>'Create a recording'</b>.</li>
		<li>Choose how you wish to upload your recording, add details and set permissions. </li>
		<li><b>The 'i' button provides more information.</b></li> 
	</ul>
	<img src="${resource(dir:'images',file:"Step3small.jpg")}" alt="Step 3" />	
	<h3 id="step4">Step 4</h3>
	<ul>
		<li>Make a transcript for a recording by selecting the <b>'Edit Transcript'</b> then <b>'Add …'</b>  </li>
		<li>Type or import text. You can play, edit, merge, split or delete transcripts by using the right-hand mouse features.</li>
		<li>View your recording by returning to the <b>'Recordings'</b> menu button </li>
		<li>Change the look and feel of the player by using the <b>'Settings'</b> button. </li>
	</ul>
	<img src="${resource(dir:'images',file:"Step4small.jpg")}" alt="Step 4" />	
	<h3 id="step5">Step 5</h3>
	<ul>
		<li>Add synchronised annotations or notes (synmarks) at selected points in the recording by selecting the <b>'create'</b> button in the Synmark panel.  You can edit and delete your synmarks. </li>
		<li>Use the <b>'Title'</b> and <b>'Tags'</b> to aid coding and searches. </li>
		<li>Add a note – sections can be colour coded or underlined and spell checked.</li>
		<li>Synote allows you to listen to lectures or recordings whilst viewing the transcript and slides and making notes.  It synchronises these notes so key points can be stored for revision or use later.</li>
		<li>Synote can be used for research with the ability to code interviews, add annotations that are synchronised with a discussion and highlight important behaviours. </li>
		<li>Synote can be linked to Twitter comments that appear as notes synchronised with a discussion,</li>
		<li>Synote transcripts can be automatically uploaded via speech recognition if linked to this type of service. </li>
	</ul>
	<img src="${resource(dir:'images',file:"Step5small.jpg")}" alt="Step 5" />	
	<a href="#help" title="back to top">Back to top</a>
<h2 id="demo">Demo Video</h2>
<hr/>
<div>
	<p>This is the demo video of Synote.</p>
	<iframe width="480" height="360" src="http://www.youtube.com/embed/yMZF_wWEv2o" frameborder="0" allowfullscreen="true"></iframe>
</div>
<h2 id="googleindex">Let Google Index Your Media Fragment</h2>
<hr/>
<div>
	<p>Media fragment is important on the Web as it describes the inside content of multimedia resource. Major search engines, however, are not able to index media fragments and the related annotations. We analysed this problem from both multimedia application and search engine's point of views. The key problem is that media fragments and annotations do not have their own page on the Web and they share the same page as the parent resource. Our solution is using Google Ajax Application Crawler and server-side programmes to create snapshot pages for each media fragment and related annotations. In this way, media fragments will have their own links in the search results and on following the links, users still visit the same replay page as before. We have tested the implementation and the result shows that the media fragments could be indexed by Google</p>
	<object width="480" height="360"><param name="movie" value="http://www.youtube.com/v/_MfwHhX9DNg?version=3&amp;hl=en_GB"></param><param name="allowFullScreen" value="true"></param><param name="allowscriptaccess" value="always"></param><embed src="http://www.youtube.com/v/_MfwHhX9DNg?version=3&amp;hl=en_GB" type="application/x-shockwave-flash" width="480" height="360" allowscriptaccess="always" allowfullscreen="true"></embed></object>	
</div>
<a href="#help" title="back to top">Back to top</a>
<h2 id="linkeddata">Publishing Media Fragments and Annotations as Linked Data</h2>
<hr/>
<div>
	<p>
		While end users could easily share and tag the multimedia resources online, the searching and reusing of the inside content of multimedia is still difficult. Many applications in Web 2.0 have generated many external annotations linked to media fragments. This video use Synote as the target application to discuss how media fragments could be published together with external annotations following linked data principles. We also implement a model to let Google index media fragments which improves the online presence of media fragments.We hope that more media fragments could be published later to benefit the indexing of multimedia resources.
	</p>
	<object width="480" height="360"><param name="movie" value="http://www.youtube.com/v/AZeXETmatZk?version=3&amp;hl=en_GB"></param><param name="allowFullScreen" value="true"></param><param name="allowscriptaccess" value="always"></param><embed src="http://www.youtube.com/v/AZeXETmatZk?version=3&amp;hl=en_GB" type="application/x-shockwave-flash" width="480" height="360" allowscriptaccess="always" allowfullscreen="true"></embed></object>
</div>
<a href="#help" title="back to top">Back to top</a>
</body>
</html>

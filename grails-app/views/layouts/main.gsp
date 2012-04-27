<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional with HTML5 microdata//EN" "xhtml1-transitional-with-html5-microdata.dtd">
<html lang="en">
<head>
	<title><g:layoutTitle default="Synote" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="viewpoint" cotnent="width=device-width, initial-scale=1.0"/>
	<meta name="author" content="Yunjia Li"/>
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
	<script type="text/javascript" src="${resource(dir: 'bootstrap', file: 'js/bootstrap.min.js')}"></script>
	<link rel="stylesheet" type="text/css" href="${resource(dir: 'bootstrap', file: 'css/bootstrap.min.css')}" />
	<link rel="stylesheet" type="text/css" href="${resource(dir: 'css', file: 'main.css')}" />
	<link rel="shortcut icon" href="${resource(dir: 'images', file: 'synote_icon.ico')}" type="image/x-icon" />
	<g:urlMappings/>
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
	<g:layoutHead />
</head>
<body itemscope="itemscope" itemtype="http://schema.org/WebPage" itemref="bottomMainFooter">
<meta itemprop="author" content="Yunjia Li">
	<!-- Top Navigation bar -->
	<div class="navbar" style="margin-bottom:0px !important;" itemscope="itemscope" itemtype="WPHeader">
		<div class="navbar-inner">
			<div class="container" style="width:940px;">
				<!-- top menu -->
				<div class="nav-collapse">
					<g:isNotLoggedIn>
					<div class="btn-group pull-right">
						<g:link controller="login" action="auth" title="Log in" elementId="main_login_a" class="btn btn-primary">
								Login</g:link>
						<g:allowRegistering>
							<g:link controller="register" action="index" title="Register" class="btn btn-success">
								 Register</g:link>
						</g:allowRegistering>
					</div>
					</g:isNotLoggedIn>
					<ul class="nav">   
					    <li>
					    	<a href="${resource(dir: '/')}" title="home">
					    	<i class="icon-home icon-white"></i>Home </a>
					    </li>
						<!-- Recordings -->   
					    <li><g:link controller="multimediaResource" action="list" title="Multimedia recordings">
					    	<i class="icon-film"></i>Recordings</g:link>
					    </li>
					    
						<!-- Groups -->    
					    <li><g:link controller="userGroup" action="list" title="Groups list">
							<i class="icon-magnet"/></i>Groups</g:link>
						</li>
						<!-- Synote Guide --> 
						<li><g:link controller="user" action="help" target="_blank" title="help">
							<i class="icon-info-sign"/></i>Help</g:link>
						</li>
					</ul>
				</div>
			</div>
		</div>
	</div>
	<!-- The search bar -->
	<div id="search_bar_div">
		<div class="container">
			<div class="row">
				<div class="span2" style="text-align:center;">
					<img itemprop="primaryImageOfPage" src="${resource(dir: 'images', file: 'synote_logo_small.png')}" alt="Synote"/>
				</div>
				<div class="span10">
					<g:render template="/common/search" />
				</div>
			</div>
		</div>
	</div>
	<div id="main_content" class="container" itemprop="maincontentOfPage" itemscope="itemscope" itemtype="http://schema.org/WebPageElement">
		<g:layoutBody />
	</div>
	<div itemscope="itemscope" itemtype="WPFooter" class="span-24 prepend-top" style="margin-bottom:24px;">
			<ul id="bottomMainFooter">
				<li>
					<a href="http://www.soton.ac.uk" target="_blank"
						title="University of Southampton">&copy; <span itemprop="copyrightYear">2012</span> <span itemprop="copyrightHolder">University of Southampton</span></a>
				</li>
				<li>
					<g:link controller="user" action="contact"
						target="_blank" title="Contact Us">Contact Us</g:link>
				</li>
				<li>
					<g:link
				action="termsAndConditions" controller="user" title="Legal"
				target="_blank">Legal</g:link>
				</li>
				<li>
					<g:link
				action="accessibility" controller="user" title="Accessibility"
				target="_blank">Accessibility</g:link>
				</li>
				<li>
					<a
				href="http://www.synote.ecs.soton.ac.uk" target="_blank"
				title="About synote">About Synote</a> 
				</li>
				<li>
					<a
				href="http://blog.lsl.ecs.soton.ac.uk/synote/" target="_blank"
				title="Synote News">Synote News</a>
				</li>
			</ul>
	</div>
</body>
</html>

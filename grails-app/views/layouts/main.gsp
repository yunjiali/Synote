<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional with HTML5 microdata//EN" "xhtml1-transitional-with-html5-microdata.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:foaf="http://xmlns.com/foaf/0.1/" 
	xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#">
<head>
	<title><g:layoutTitle default="Synote" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.16/jquery-ui.min.js"></script>
	<script src="http://cdn.wijmo.com/external/jquery.bgiframe-2.1.3-pre.js" type="text/javascript"></script>
	<script src="http://cdn.wijmo.com/external/jquery.mousewheel.min.js" type="text/javascript"></script>
	<script src="http://cdn.wijmo.com/jquery.wijmo-open.1.4.1.min.js" type="text/javascript"></script>
	<script type="text/javascript" src="${resource(dir:'js',file:"synote.js")}"></script>
	<link rel="stylesheet" type="text/css" href="${resource(dir:'css/blueprint',file:"print.css")}" media="print"/>
	<link rel="stylesheet" type="text/css" href="${resource(dir:'css/blueprint',file:"screen.css")}" media="screen, projection" />
	<!--[if lt IE 8]>
	<link rel="stylesheet" href="${resource(dir:'css/blueprint',file:"ie.css")}" media="screen, projection" />
	<![endif]-->
	<link rel="stylesheet" type="text/css" href="${resource(dir: 'css', file: 'main.css')}" />
	<link rel="stylesheet" type="text/css" href="http://cdn.wijmo.com/jquery.wijmo-open.1.4.1.css"  />
	<link rel="stylesheet" type="text/css" href="http://cdn.wijmo.com/themes/aristo/jquery-wijmo.css" />
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
		$(document).ready(function(){
			$("#topMainMenu").wijmenu({
				trigger: ".wijmo-wijmenu-item",
				triggerEvent: "click"
			});
			$("#bottomMainFooter").wijmenu({
				trigger: ".wijmo-wijmenu-item",
				triggerEvent: "click"
			});
		});
	</script>
	<g:layoutHead />
</head>
<body itemscope="itemscope" itemtype="http://schema.org/WebPage" itemref="bottomMainFooter">
<meta itemprop="author" content="Yunjia Li">
<div id="container_wrapper">
	<div id="container" class="container">
		<!-- Top Navigation bar -->
		<div class="span-24">
			<!-- top menu -->
			<div itemscope="itemscope" itemtype="WPHeader">
				<ul id="topMainMenu">   
			    <li>
			    	<a href="${resource(dir: '/')}" title="home">
			    		<img src="${resource(dir: 'images/skin', file: 'house.png')}"  /> Home </a>
			    </li>
			
				<!-- Recordings -->   
			    <li><g:link controller="multimediaResource" action="list" title="Multimedia recordings">
			    	<img  src="${resource(dir: 'images/skin', file: 'video_16.png')}"  /> Recordings </g:link>
			    </li>
			    
				<!-- Groups -->    
			    <li><g:link controller="userGroup" action="list" title="Groups list">
					<img src="${resource(dir: 'images/skin', file: 'user_group_16.png')}"/> Groups </g:link></li>
				<!-- Synote Guide --> 
				<li><g:link controller="user" action="help" target="_blank" title="help">
					<img  src="${resource(dir: 'images/skin', file: 'info_16.png')}" /> Help </g:link>
				</li>
				<!-- User profile -->
				<g:isLoggedIn>
				<li class="right">
					<g:link controller="logout" action="index" title="Log out">
					<img  src="${resource(dir: 'images/skin', file: 'logout_16.png')}" /> Log Out </g:link>
				</li>
				<li class="right">
					<g:link controller="user" action="index" title="Edit your profile">
					<img  src="${resource(dir: 'images/skin', file: 'settings_16.png')}"/> My Synote </g:link>
				</li>
				<g:isAdminLoggedIn>
				<li class="right">
					<g:link controller="admin" action="index" title="Administration">
					<img src="${resource(dir: 'images/skin', file: 'settings_16.png')}"  /> Administration </g:link>
				</li>
				</g:isAdminLoggedIn>
				<li class="right"><a title="create" style="font-weight:normal !important;">
			    	<img  src="${resource(dir: 'images/skin', file: 'document_pencil_16.png')}"  /> create </a>
			    	<ul>
			    		<li>
			    			<g:link controller="multimediaResource" action="create" title="recording diretory">
			    			<img  src="${resource(dir: 'images/skin', file: 'video_16.png')}"  />
			    			 Create a recording </g:link>
			    		</li>
			    		<li>
			    			<g:link controller="userGroup" action="create" title="recording diretory">
			    			<img  src="${resource(dir: 'images/skin', file: 'user_group_new_16.png')}"  />
			    			 Create a group </g:link>
			    		</li>
			    		
			    	</ul>
			    </li>
				<li class="right">
					<span><img  src="${resource(dir: 'images/skin', file: 'user_16.png')}"/>
					Hi, <g:loggedInUsername /> </span>
				</li>
				</g:isLoggedIn>
				<g:isNotLoggedIn>
				<g:allowRegistering>
				<li class="right">
					<g:link controller="register" action="index" title="Register">
					<img  src="${resource(dir: 'images/skin', file: 'pencil_16.png')}"/> Register </g:link>
				</li>
				</g:allowRegistering>
				<g:if test="${!hideLoginLink}">
				<li class="right">
					<g:link controller="login" action="auth" title="Log in" elementId="main_login_a">
					<img  src="${resource(dir: 'images/skin', file: 'key_16.png')}"/> Login </g:link>
				</li>
				</g:if>	
				</g:isNotLoggedIn>	
				</ul>
			</div>
		</div>
		<!-- The search bar -->
		<div id="search_bar_div" class="span-24" style="padding:10px 0; background:whiteSmoke; border-bottom: 1px solid #e5e5e5;'">
			<div class="prepend-1 span-4">
				<img itemprop="primaryImageOfPage" src="${resource(dir: 'images', file: 'synote_logo_small.png')}" alt="Synote"/>
			</div>
			<div class="span-19 last" style="vertical-align:middle;">
				<g:render template="/common/search" />
			</div>
		</div>
		<div id="main_content" class="span-24 prepend-top append-bottom" itemprop="maincontentOfPage" itemscope="itemscope" itemtype="http://schema.org/WebPageElement">
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
	</div>
</div>
</body>
</html>

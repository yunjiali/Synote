<html>
<head>
<title><g:message code="org.synote.user.index.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
</head>
<body>
<div class="span-24" id="user_nav">
	<g:render template="/common/userNav"/>
</div>
<div class="span-24" id="user_content">
	<h1><g:message code="org.synote.user.index.title" /></h1>
	<div id="my_synote_content">
	
	<g:allowRegistering>
		<g:link class="user" action="showUserProfile" title="Show user profile"><img src="${resource(dir: 'images/mysynote', file: 'view-pim-contacts.png')}" /><br />User Profile</g:link>
			
		<!-- <g:link class="user" action="changePassword" title="Change password"><img src="${resource(dir: 'images/mysynote', file: 'edit-rename.png')}" /><br />Change password</g:link>
		-->
	</g:allowRegistering>
	<g:link class="user" action="listGroups" title="My Groups"><img src="${resource(dir: 'images/mysynote', file: 'user-group-properties.png')}" /><br />My Groups</g:link>
	
	<g:link class="user" action="listResources" title="My Resources"><img src="${resource(dir: 'images/mysynote', file: 'my_resources_64.png')}" /><br />My Resources</g:link>
	
	<g:link class="user" action="listTags" title="My Tags"><img src="${resource(dir: 'images/mysynote', file: 'tag_64.png')}" /><br />My Tags</g:link>
	<g:twitterEnabled>
	<g:link class="user" controller="twitter" action="index" title="Upload Tweets"><img src="${resource(dir: 'images/mysynote', file: 'twitter.png')}" /><br />Upload Tweets</g:link>
	</g:twitterEnabled>
	<g:ibmhtsEnabled>
	<g:link class="user" controller="IBMTransJob" action="list" title="IBM Transcript Service"><img src="${resource(dir: 'images/mysynote', file: 'view-media-playlist-4.png')}" /><br />Transcript Jobs</g:link>
	</g:ibmhtsEnabled>
	<!-- <g:link class="user" controller="logout" action="index" title="Log out"><img src="${resource(dir: 'images/mysynote', file: 'application-exit-2.png')}" /><br />Log out</g:link>-->
	
	</div>
</div>
</body>
</html>

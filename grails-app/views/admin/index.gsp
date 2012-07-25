<html>
<head>
<title>Welcome to Grails</title>
<meta name="layout" content="admin" />
<style type="text/css" media="screen">
#nav {
	margin-top: 20px;
	margin-left: 30px;
	width: 228px;
	float: left;
}

.homePagePanel * {
	margin: 0px;
}

.homePagePanel .panelBody ul {
	list-style-type: none;
	margin-bottom: 10px;
}

.homePagePanel .panelBody h1 {
	text-transform: uppercase;
	font-size: 1.1em;
	margin-bottom: 10px;
}

.homePagePanel .panelBody {
	background: url(images/leftnav_midstretch.png) repeat-y top;
	margin: 0px;
	padding: 15px;
}

.homePagePanel .panelBtm {
	background: url(images/leftnav_btm.png) no-repeat top;
	height: 20px;
	margin: 0px;
}

.homePagePanel .panelTop {
	background: url(images/leftnav_top.png) no-repeat top;
	height: 11px;
	margin: 0px;
}

h2 {
	margin-top: 15px;
	margin-bottom: 15px;
	font-size: 1.2em;
}

#pageBody {
	margin-left: 280px;
	margin-right: 20px;
}
</style>
</head>
<body>
<div id="nav">
<div class="homePagePanel">
<div class="panelTop"></div>
<div class="panelBody">
<h1>Application Status</h1>
<ul>
	<li>App version: <g:meta name="app.version"></g:meta></li>
	<li>Grails version: <g:meta name="app.grails.version"></g:meta></li>
	<li>JVM version: ${System.getProperty('java.version')}
	</li>
	<li>Controllers: ${grailsApplication.controllerClasses.size()}
	</li>
	<li>Domains: ${grailsApplication.domainClasses.size()}
	</li>
	<li>Services: ${grailsApplication.serviceClasses.size()}
	</li>
	<li>Tag Libraries: ${grailsApplication.tagLibClasses.size()}
	</li>
</ul>
<h1>Installed Plugins</h1>
<ul>
	<g:set var="pluginManager"
		value="${applicationContext.getBean('pluginManager')}"></g:set>

	<g:each var="plugin" in="${pluginManager.allPlugins}">
		<li>
		${plugin.name} - ${plugin.version}
		</li>
	</g:each>

</ul>
</div>
<div class="panelBtm"></div>
</div>


</div>
<div id="pageBody">
<h1>Administration tools</h1>
<g:render template="/common/message" /> <g:link class="user"
	controller="user" action="list" title="Edit basic user information">List users</g:link><br />
<g:link class="user" controller="admin" action="changePermission"
	title="Change Permissions">Change Permission</g:link><br />
<g:link class="user" controller="admin" action="resetPassword"
	title="Reset a user's password">Reset password</g:link><br />
<g:link class="user" controller="admin"
	action="changeIBMTransJobSettings"
	title="Change IBM transcript server settings">IBM transcript server settings</g:link><br />
<g:link class="user" controller="admin" action="configSearch"
	title="Config search options">Search settings</g:link><br />
<g:link class="user" controller="admin" action="changeUserRole"
	title="User role management">User Role management</g:link><br />
<g:link class="user" controller="configuration" action="index"
	title="Change application configuration">Change Application Configuration</g:link><br />
<g:link class="user" controller="admin" action="setTermsAndConditions"
	title="Set terms and conditions">Edit Terms and Conditions</g:link><br />
	<g:link class="user" controller="admin" action="setContactPage"
	title="Set Contact Page">Edit Contact Page</g:link><br />
<!--<g:link class="user" controller="admin" action="testEmail">Test email</g:link>-->
<h2>When you firstly start the system, you need to:</h2>
<ol>
	<li>Enable transcribing service?</li>
	<li>Enable Entermedia and file upload?</li>
	<li>Enable captcha?</li>
	<li>Write your own terms and conditions?</li>
	<li>Write your own Contact page? Change the Contact link?</li>
	<li>Enable Viascribe?</li>
	<li>Enable twitter?</li>
	<li>Enable Forget Password?</li>
	<li>Change the linkeddata baseURI and servercontext in configuration</li>
	<li>Change the synote-multimedia-service url in configuration</li>
	<li>Enable NERD, set the NERD key if necessary</li>
</ol>
<h2>When the system restarts, you need to:</h2>
<ol>
	<li>Enable IBM transcribing service</li>
</ol>
</div>
<h2>Advancded functions</h2>
</body>
</html>
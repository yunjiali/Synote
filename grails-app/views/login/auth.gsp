<head>
<title><g:message code="org.synote.user.login.title" /></title>
<meta name="layout" content="main" />
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform', file: 'uni-form.css')}" media="screen" charset="utf-8"/>
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform', file: 'default.uni-form.css')}" media="screen" charset="utf-8"/>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form.jquery.min.js')}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form-validation.jquery.min.js')}"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$("#loginForm").uniform();
		$(".textInput").wijtextbox();
		$("#loginForm_submit").button();
		$("#loginForm_div").wijexpander({allowExpand:false});
		$("#read_only_help_span [title]").wijtooltip();
		document.forms['loginForm'].elements['j_username'].focus();
	});
</script>
</head>

<body>
<g:render template="/common/message" />
<h1><g:message code="org.synote.user.login.title" /></h1>
<div class="span-16 prepend-4 append-4 prepend-top append-bottom">
	<div id="loginForm_div">
		<h2>
			Login
		</h2>
		<div>
		<form action='${postUrl}' method='POST' id='loginForm' class="uniForm">
			<div class="ctrlHolder inlineLabels">
			    <label for="j_username"><em>*</em>User Name:</label>
				<input type='text' autocomplete="off" class="textInput medium required" name='j_username' id='j_username' value='${request.remoteUser}' />
				<p class="formHint">Please enter the user name you choose when registering in Synote</p>
			</div>
			<div class="ctrlHolder inlineLabels">
				<label for="j_password"><em>*</em>Password</label>
				<input type='password' class="textInput medium required" name='j_password' id='j_password' />
				<p class="formHint">Please enter the password for the user name</p>
			</div>
			<div class="prepend-top append-bottom">
				<input id="loginForm_submit" type="submit" value="Login" />
			</div>
			<g:forgetPasswordEnabled>
			<div id="forget_password_div">
				<a href="#">Forgotten Password?</a> (not implemented yet)
			</div>
			</g:forgetPasswordEnabled>
			<div class="append-bottom">
				Do not have an account? 
				<g:link style="color:#3366cc; font-weight:bold;" controller="register" action="index">Register Now!</g:link>
				Or try <g:link style="color:#3366cc; font-weight:bold;"
					controller="recording" action="replay" id="${params.multimediaId}" params="[isGuest:true]"
					title="${message(code: 'org.synote.user.login.auth.guestReplay', default: 'Read Only version')}">
					<g:message code="org.synote.user.login.auth.guestReplay" />
				</g:link></b>
				<span id="read_only_help_span"><img style="display:inline;"  src="${resource(dir: 'images/skin', file: 'info_16.png')}" 
				title="In Read Only version you can only watch the recording. You cannot make any annotations." alt="Explain what is read only"/>
				</span>
			</div>	 
		</form>
		</div>
	</div>
</div>
</body>

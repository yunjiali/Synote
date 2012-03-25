<head>
<meta name="layout" content="main" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>User Registration</title>
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform', file: 'uni-form.css')}" media="screen" charset="utf-8"/>
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform', file: 'default.uni-form.css')}" media="screen" charset="utf-8"/>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form.jquery.min.js')}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form-validation.jquery.min.js')}"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$(".uniForm").uniform();
		$("input[type=text], input[type=password]").wijtextbox();
		$("#registrationForm_submit").button();
		$("#registerForm").wijexpander({allowExpand:false});
		$("#read_only_help_span [title]").wijtooltip();
	});
</script>
</head>

<body>

<g:render template="/common/message" model="[bean: user]" /> 

<div class="span-16 prepend-4 append-4 prepend-top append-bottom">
	<div id="registerForm">
		<h1>User Registration</h1>
		<g:form method="post" class="uniForm" controller="register" action="save">

			<div class="ctrlHolder inlineLabels">
			    <label for="userName"><em>*</em>User Name:</label>
				<input type='text' autocomplete="off" class="textInput medium required" name='userName' id='userName' value='${fieldValue(bean: user, field: 'userName')}' />
				<p class="formHint">Please enter the username you would like to use for Synote</p>
			</div>
			
			<div class="ctrlHolder inlineLabels">
			    <label for="password"><em>*</em>Password:</label>
				<input type='password' autocomplete="off" class="textInput medium required" name='password' id='password' value='${fieldValue(bean: user, field: 'password')}' />
			</div>

			<div class="ctrlHolder inlineLabels">
			    <label for="confirm"><em>*</em>Confirm Password:</label>
				<input type='password' autocomplete="off" class="textInput medium required" name='confirm' id='confirm' value='${params.confirm}' />
			</div>
			
			
			<div class="ctrlHolder inlineLabels">
			    <label for="firstName">First Name:</label>
				<input type='text' autocomplete="off" class="textInput medium required" name='firstName' id='firstName' value='${fieldValue(bean: user, field: 'firstName')}' />
			</div>

			<div class="ctrlHolder inlineLabels">
			    <label for="lastName">Last Name:</label>
				<input type='text' autocomplete="off" class="textInput medium required" name='lastName' id='lastName' value='${fieldValue(bean: user, field: 'lastName')}' />
			</div>

			<div class="ctrlHolder inlineLabels">
			    <label for="email"><em>*</em>Email:</label>
				<input type='text' autocomplete="off" class="textInput medium required" name='email' id='email' value='${fieldValue(bean: user, field: 'email')}' />
			</div>
			<g:captchaEnabled>
			<div class="ctrlHolder inlineLabels">
			    <label for="captcha"><em>*</em>Enter Code:</label>
				<input type='text' autocomplete="off" class="textInput medium required" name='captcha' id='captcha' />
				<p class="formHint"><img src="${createLink(controller:'captcha', action:'index')}" align="absmiddle" /></p>
			</div>
			</g:captchaEnabled>
			<g:checkBox name="termsAndConditions"
				value="agree" checked="false" />&nbsp;&nbsp;I have read and agreed
			the <g:link controller="user" action="termsAndConditions">Synote Terms and Conditions.</g:link>
			
			<div class="prepend-top append-bottom">
				<input id="registrationForm_submit" type="submit" value="Register" />
			</div>
		</g:form>

	</div>
</div>
</body>

<head>
<title><g:message code="org.synote.user.login.title" /></title>
<meta name="layout" content="main" />
<script type="text/javascript" src="${resource(dir: 'js/jquery', file: 'jquery.validate-1.9.1.min.js')}"></script>
<script type="text/javascript">
	$(document).ready(function(){
		document.forms['loginForm'].elements['j_username'].focus();
		$("#loginForm").validate(
		{
			highlight: function(label) {
			    $(label).closest('.control-group').addClass('error');
			},
		});
	});
</script>
</head>
<body>
<h1 class="hiding"><g:message code="org.synote.user.login.title" /></h1>
<div class="container">
	<div class="row">
		<div class="span6">
			<h2>Sign in to Synote</h2>
			<p><b>With a Synote account you can:</b></p>
			<ul>
				<li>Create Recordings</li>
				<li>Set your recordings public or private</li>
				<li>Edit transcript and presentation slides for you own recordings</li>
				<li>Make annotations on your and your friends' recordings</li>
				<li>Create groups and add recordings that shared with the members in the group</li>
			</ul>
			<p><b>Do not have an account?</b></p>
			<g:link controller="register" action="index" title="Register" class="btn btn-success">
					 Get a free account</g:link><br/><br/>
			<p><b>Or you can still enjoy Synote:</b></p>
			<ul>
				<li>View public recordings list</li>
				<li>Watch recordings in Synote player, but you cannot make annotations.</li>
			</ul>
		</div>
		<!-- login form -->
		<div class="span4 offset1 well">
			<h3>Login</h3>
			<hr/>
			<g:render template="/common/message" />
			<form action='${postUrl}' method='POST' name='loginForm'>
			  <fieldset>
			    <div class="control-group">
			     	<label for="j_username" class="control-label"><b><em>*</em>User Name</b></label>
			      	<div class="controls">
			        	<input type='text' autocomplete="off" class="required" name='j_username' id='j_username' value='${request.remoteUser}' 
			        		placeholder="Synote user name"/>
			      	</div>
			    </div>
			    <div class="control-group">
			     	<label for="j_password" class="control-label"><b><em>*</em>Password</b></label>
			      	<div class="controls">
						<input type='password' name='j_password' class="required" id='j_password' placeholder="password"/>
			      	</div>
			    </div>
			    <div class="control-group">
			    	<label class="checkbox" for="_spring_security_remember_me">
			    		<input type="checkbox" name="_acegi_security_remember_me" id="_acegi_security_remember_me" />Keep me signed in
			    	</label>
			    </div>
			    <syn:forgetPasswordEnabled>
				<div id="forget_password_div">
					<a href="#">Forgotten Password?</a> (not implemented yet)
				</div>
				</syn:forgetPasswordEnabled>
				
			    <div class="form-actions">
		            <input class="btn-large btn-primary" id="loginForm_submit" type="submit" value="Login" />
		        </div>
			  </fieldset>
			</form>
		</div>
	</div>
</div>
</body>

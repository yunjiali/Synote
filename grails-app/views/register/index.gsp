<head>
<meta name="layout" content="main" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>User Registration</title>
<script type="text/javascript" src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.9/jquery.validate.min.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$("#registrationForm").validate(
		{
			rules: {
			    password: "required",
			    confirm: {
			      equalTo: "#password"
			    }
			 },
			highlight: function(label) {
				$(label).closest('.control-group').addClass('error');
			}
		});
	});
</script>
</head>

<body>
<h1 class="hiding">User Registration</h1>
<div class="container">
	<div class="row">
		<div class="span6 offset2 well">
			<h3>Create a Synote account</h3>
			<hr/>
			<g:render template="/common/message" model="[bean: user]" />
			<div>
				<g:form method='POST' name='registrationForm' class="form-horizontal" controller="register" action="save">
				  <fieldset>
				    <div class="control-group">
				    	<label for="userName" class="control-label"><b><em>*</em>User Name</b></label>
				      	<div class="controls">
				        	<input type='text' autocomplete="off" class="required" name='userName' id='userName' value='${fieldValue(bean: user, field: 'userName')}' />
							<p class="help-block">The username you would like to use in Synote</p>
				      	</div>
				    </div>
				    <div class="control-group">
				     	<label for="password" class="control-label"><b><em>*</em>Password</b></label>
				      	<div class="controls">
							<input type='password' name='password' class="required" id='password'/>
				      	</div>
				    </div>
				    <div class="control-group">
				     	<label for="confirm" class="control-label"><b><em>*</em>Confirm Password</b></label>
				      	<div class="controls">
							<input type='password' name='confirm' class="required" id='confirm'/>
				      	</div>
				    </div>
					<div class="control-group">
					    <label for="firstName" class="control-label"><b><em>*</em>First Name</b></label>
					    <div class="controls">
					    	<input type='text' autocomplete="off" class="required" name='firstName' id='firstName' value='${fieldValue(bean: user, field: 'firstName')}' />	
					    </div>
					</div>
					<div class="control-group">
					    <label for="lastName" class="control-label"><b><em>*</em>Last Name</b></label>
					    <div class="controls">
					    	<input type='text' autocomplete="off" class="required" name='lastName' id='lastName' value='${fieldValue(bean: user, field: 'lastName')}' />
					    </div>
					</div>
					<div class="control-group">
					    <label for="email" class="control-label"><b><em>*</em>Email</b></label>
					    <div class="controls">
					    	<div class="input-prepend">
					    		<span class="add-on"><i class="icon-envelope"></i></span><input type='text' class="required email" name='email' id='email' value='${fieldValue(bean: user, field: 'email')}' />
					    	</div>
					    </div>
					</div>
					<syn:captchaEnabled>
					<div class="control-group">
					    <label for="captcha" class="control-label"><b><em>*</em>Enter Code:</b></label>
					    <div class="controls">
					    	<input type='text' autocomplete="off" class="required" name='captcha' id='captcha' />
							<p class="help-block"><img src="${createLink(controller:'captcha', action:'index')}" align="absmiddle" /></p>
					    </div>
					</div>
					</syn:captchaEnabled>
					<div class="control-group">
			    		<label class="checkbox" for="termsAndConditions">
			    			<input type="checkbox" name="termsAndConditions" id="termsAndConditions" class="required"/>
							I have read and agreed the <g:link controller="user" action="termsAndConditions">Synote Terms and Conditions.</g:link>
			    		</label>
			   		</div>
				    <div class="form-actions">
			            <input class="btn-large btn-primary" id="registrationForm_submit" type="submit" value="Register" />
			            <input class="btn-large" id="registrationForm_reset" type="reset" value="Reset"/>
			        </div>
				  </fieldset>
				</g:form>
			</div>
		</div>
	</div>
</div>
</body>

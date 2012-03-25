package org.synote.user

import org.synote.user.User
import org.synote.user.UserRole
import org.synote.user.SynoteAuthenticationProvider
import org.synote.config.ConfigurationService

import org.springframework.security.providers.UsernamePasswordAuthenticationToken as AuthToken
import org.springframework.security.context.SecurityContextHolder as SCH

/**
 * Registration controller.
 */

class RegisterController {
	
	def beforeInterceptor = [action: this.&allowRegistering]
	
	def authenticateService
	def synoteAuthenticationProvider
	def emailerService
	def configurationService

	static Map allowedMethods = [save: 'POST']

	private allowRegistering()
	{
		boolean allowRegistering = Boolean.parseBoolean(configurationService.getConfigValue("org.synote.user.register.enable"))
		if(!allowRegistering)
			redirect(controller:'login', action:'auth')
		
		return
	}
	 /**
	 * User Registration Top page.
	 */
	                          
	def index = {

		// skip if already logged in
		if (authenticateService.isLoggedIn()) {
			redirect(controller:"user", action:"index")
			return
		}

		if (session.id) {
			def person = new User()
			person.properties = params
			return [user: person]
		}

		redirect uri: '/'
	}

	/**
	 * Person save action.
	 */
	//TODO: rewrite and test this programme, the username and password are not correct
	def save = {

		// skip if already logged in
		if (authenticateService.isLoggedIn()) {
			redirect(controller:"user", action:"index")
			return
		}

		def person = new User()
		person.properties = params

		def config = authenticateService.securityConfig
		def defaultRole = config.security.defaultRole

		if(!params.termsAndConditions)
		{
			flash.error = "You have to agree the Synote terms and Conditions before you register."
			render view: 'index', model: [user: person]
			return
		}
		//check if the registration username has already existed
		def p = User.findByUserName(params.userName)
		if(p != null)
		{
			person.password = ''
			flash.error = 'User name ${params.userName} has already been taken. Please choose another user name.'
			render view: 'index', model: [user: person]
			return
		}
		def role = UserRole.findByAuthority(defaultRole)
		if (!role) {
			person.password = ''
			flash.error = 'Default Role not found.'
			render view: 'index', model: [user: person]
			return
		}
		
		if(Boolean.parseBoolean(configurationService.getConfigValue("org.synote.user.register.captcha.enabled")) == true)
		{
			if (params.captcha.toUpperCase() != session.captcha) {
				person.password = ''
				flash.error = 'Access code did not match.'
				render view: 'index', model: [user: person]
				return
			}
		}

		if (params.password != params.confirm) {
			person.password = ''
			flash.error = 'The passwords you entered do not match.'
			render view: 'index', model: [user: person]
			return
		}

		def pass = authenticateService.encodePassword(person.userName.toLowerCase()+person.password)
		person.password = pass
		person.enabled = true
		person.lastLogin = new Date()
		if (person.save()) {
			role.addToPeople(person)
			if (config.security.useMail) 
			{
				String emailContent = """You have signed up for an account at:

				 ${request.scheme}://${request.serverName}:${request.serverPort}${request.contextPath}
				
				 Here are the details of your account:
				 -------------------------------------
				 LoginName: ${person.username}
				 Email: ${person.email}
				 Full Name: ${person.userRealName}
				 Password: ${params.passwd}
				"""

				def email = [
					to: [person.email], // 'to' expects a List, NOT a single email address
					subject: "[${request.contextPath}] Account Signed Up",
					text: emailContent // 'text' is the email body
				]
				emailerService.sendEmails([email])
			}

			person.save(flush: true)

			def auth = new AuthToken(person.userName, params.password)
			def authtoken = synoteAuthenticationProvider.authenticate(auth)
			SCH.context.authentication = authtoken
			
			flash.message="User ${person.userName} was successfully registered."
			redirect(controller:"user", action:"index")
			return
		}
		else {
			person.password = ''
			flash.error="Error occurs while saving the registration, please try again..."
			render view: 'index', model: [person: person]
			return
		}
	}
}

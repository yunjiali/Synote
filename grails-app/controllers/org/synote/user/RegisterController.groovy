package org.synote.user

import org.synote.user.User
import org.synote.user.UserRole
import org.synote.user.UserRolePeople
import org.synote.config.ConfigurationService

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken as AuthToken
import org.springframework.security.core.context.SecurityContextHolder as SCH
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils as SSU


/**
 * Registration controller.
 */

class RegisterController {
	
	def beforeInterceptor = [action: this.&allowRegistering]
	
	def springSecurityService
	//def synoteAuthenticationProvider
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
		if (springSecurityService.isLoggedIn()) {
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
		if (springSecurityService.isLoggedIn()) {
			redirect(controller:"user", action:"index")
			return
		}

		def person = new User()
		person.properties = params

		def config = SSU.getSecurityConfig()
		//currently there is only onedefautrole
		def defaultRole = SSU.getSecurityConfig().ui.register.defaultRoleNames[0]

		if(!params.termsAndConditions)
		{
			flash.error = "You have to agree the Synote terms and Conditions before you register."
			render view: 'index', model: [user: person]
			return
		}
		//check if the registration username has already existed
		//println 'User name ${params.userName} has already been taken. Please choose another user name1.'
		//println "User name ${params.userName} has already been taken. Please choose another user name2."
		def p = User.findByUserName(params.userName)
		if(p != null)
		{
			person.password = ''
			flash.error = "User name ${params.userName} has already been taken. Please choose another user name."
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

		def pass = springSecurityService.encodePassword(person.userName.toLowerCase()+person.password)
		person.password = pass
		person.enabled = true
		person.lastLogin = new Date()
		if (person.save()) {
			UserRolePeople.create person,role
			//Don't use email
			//if (.security.useMail) 
			//{
			//	String emailContent = """You have signed up for an account at:

			//	 ${request.scheme}://${request.serverName}:${request.serverPort}${request.contextPath}
			//	
			//	 Here are the details of your account:
			//	 -------------------------------------
			//	 LoginName: ${person.username}
			//	 Email: ${person.email}
			//	 Full Name: ${person.userRealName}
			//	 Password: ${params.passwd}
			//	"""

			//	def email = [
			//		to: [person.email], // 'to' expects a List, NOT a single email address
			//		subject: "[${request.contextPath}] Account Signed Up",
			//		text: emailContent // 'text' is the email body
			//	]
			//	emailerService.sendEmails([email])
			//}

			person.save(flush: true)

			//def auth = new AuthToken(person.userName, params.password)
			//def authtoken = synoteAuthenticationProvider.authenticate(auth)
			//SCH.context.authentication = authtoken
			
			flash.message="User ${person.userName} was successfully registered. Please Login."
			redirect(controller:"login", action:"auth")
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

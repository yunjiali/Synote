package org.synote.user

import org.codehaus.groovy.grails.plugins.springsecurity.RedirectUtils
import org.grails.plugins.springsecurity.service.AuthenticateService

import org.springframework.security.AuthenticationTrustResolverImpl
import org.springframework.security.DisabledException
import org.springframework.security.context.SecurityContextHolder as SCH
import org.springframework.security.ui.AbstractProcessingFilter
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter
import org.synote.user.exception.UserAuthenticationException

/**
 * Login Controller (Example).
 */
class LoginController {

	/**
	 * Dependency injection for the authentication service.
	 */
	def authenticateService

	/**
	 * Dependency injection for OpenIDConsumer.
	 */
	def openIDConsumer

	/**
	 * Dependency injection for OpenIDAuthenticationProcessingFilter.
	 */
	def openIDAuthenticationProcessingFilter
	
	def securityService

	private final authenticationTrustResolver = new AuthenticationTrustResolverImpl()

	def index = {
		if (isLoggedIn()) {
			redirect uri: '/'
		}
		else {
			redirect action: auth, params: params
		}
	}

	/**
	 * Show the login page.
	 */
	def auth = {

		nocache response

		if (isLoggedIn()) {
			redirect uri: '/'
			return
		}

		String view
		String postUrl
		def config = authenticateService.securityConfig.security
		if (config.useOpenId) {
			view = 'openIdAuth'
			postUrl = "${request.contextPath}/login/openIdAuthenticate"
		}
		else if (config.useFacebook) {
			view = 'facebookAuth'
			postUrl = "${request.contextPath}${config.facebook.filterProcessesUrl}"
		}
		else {
			view = 'auth'
			//old login method
			postUrl = "${request.contextPath}${config.filterProcessesUrl}"
			
			//new login method
			//postUrl = "${request.contextPath}/login/handleLogin"
		}
		//if(!flash.message && !flash.error)
		//	flash.message = "Please login..."
		render view: view, model: [postUrl: postUrl],params:params
		return
	}

	/**
	 * Form submit action to start an OpenID authentication.
	 */
	def openIdAuthenticate = {
		String openID = params['j_username']
		try {
			String returnToURL = RedirectUtils.buildRedirectUrl(
					request, response, openIDAuthenticationProcessingFilter.filterProcessesUrl)
			String redirectUrl = openIDConsumer.beginConsumption(request, openID, returnToURL)
			redirect url: redirectUrl
		}
		catch (org.springframework.security.ui.openid.OpenIDConsumerException e) {
			log.error "Consumer error: $e.message", e
			redirect url: openIDAuthenticationProcessingFilter.authenticationFailureUrl
		}
	}

	// Login page (function|json) for Ajax access.
	def authAjax = {
		nocache(response)
		def g = new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()
		def redirectUrl = g.createLink(controller:'login',action:'auth');
		//redirect to login page
		render """
		<script type='text/javascript'>
		(function() {
			window.location = '${redirectUrl}';
		})();
		</script>
		"""
	}

	/**
	 * The Ajax success redirect url.
	 */
	def ajaxSuccess = {
		nocache(response)
		render '{success: true}'
	}

	/**
	 * Show denied page.
	 */
	def denied = {
		if (isLoggedIn() && authenticationTrustResolver.isRememberMe(SCH.context?.authentication)) {
			// have cookie but the page is guarded with IS_AUTHENTICATED_FULLY
			redirect action: full, params: params
		}
	}

	/**
	 * Login page for users with a remember-me cookie but accessing a IS_AUTHENTICATED_FULLY page.
	 */
	def full = {
		render view: 'auth', params: params,
			model: [hasCookie: authenticationTrustResolver.isRememberMe(SCH.context?.authentication)]
	}

	// Denial page (data|view|json) for Ajax access.
	def deniedAjax = {
		//this is example:
		render "{error: 'access denied'}"
	}

	/**
	 * login failed
	 */
	def authfail = {

		def username = session[AuthenticationProcessingFilter.SPRING_SECURITY_LAST_USERNAME_KEY]
		def msg = ''
		def exception = session[AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY]
		if (exception) {
			if (exception instanceof DisabledException) {
				msg = "This user is disabled."
			}
			else {
				msg = "Wrong username or password."
			}
		}

		if (isAjax()) {
			render "{error: '${msg}'}"
		}
		else {
			flash.error = msg
			redirect action: auth, params: params
		}
	}
	
	/**
	 * login success
	 */
	def authSuccess = {
		
		//Get authorities for the login user
		
		if(session.requestedController && session.requestedAction)
		{
			redirect(controller:session.requestedController, action: session.requestedAction, params:session.requestedParams)
			
			session.requestedcontroller = null
			session.requestedAction = null
			session.requestedParams = null
		}
		else
			redirect (uri:'/')
		return
	}
	
	def autoCreateUser = {
		
		log.debug("start to create new user")

		def config = authenticateService.securityConfig.security
		
		def ldapAuth = session[SynoteLdapAuthenticationProcessingFilter.LDAP_LAST_AUTH] 
		session.removeAttribute(SynoteLdapAuthenticationProcessingFilter.LDAP_LAST_AUTH) 
		def authorities = session[AutoCreateLdapUserDetailsMapper.LDAP_AUTOCREATE_CURRENT_AUTHORITIES] 
		session.removeAttribute(AutoCreateLdapUserDetailsMapper.LDAP_AUTOCREATE_CURRENT_AUTHORITIES)
		def ctx = session[AutoCreateLdapUserDetailsMapper.LDAP_AUTOCREATE_CURRENT_CTX]
		session.removeAttribute(AutoCreateLdapUserDetailsMapper.LDAP_AUTOCREATE_CURRENT_CTX)

		log.debug("Get authentication and authorities")
		if(ldapAuth == null || authorities == null || ctx == null)
		{
			log.debug("ldapAuth, authorities or(and) ctx are null")
			flash.error = "Cannot create new user. Wrong username or password."
			redirect uri:"${config.loginFormUrl}"
			return
		}
		try
		{
			securityService.createUserFromLdap(ldapAuth,authorities,ctx)
		}
		catch(UserAuthenticationException uex)
		{
			flash.error = uex.getMessage()
			log.error(uex.getMessage())
			redirect uri:"${config.loginFormUrl}"
			return
		}
		
		// redirect to originally-requested URL if there's a saved request 
		def savedRequest = session[AbstractProcessingFilter.SPRING_SECURITY_SAVED_REQUEST_KEY] 
		if (savedRequest) { 
			redirect url: savedRequest.fullRequestUrl 
		} 
		else { 
			redirect uri: '/' 
		} 
		return
	}

	/**
	 * Check if logged in.
	 */
	private boolean isLoggedIn() {
		return authenticateService.isLoggedIn()
	}

	private boolean isAjax() {
		return authenticateService.isAjax(request)
	}

	/** cache controls */
	private void nocache(response) {
		response.setHeader('Cache-Control', 'no-cache') // HTTP 1.1
		response.addDateHeader('Expires', 0)
		response.setDateHeader('max-age', 0)
		response.setIntHeader ('Expires', -1) //prevents caching at the proxy server
		response.addHeader('cache-Control', 'private') //IE5.x only
	}
}

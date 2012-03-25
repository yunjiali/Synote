package org.synote.user;

import javax.servlet.http.HttpServletRequest 
import javax.servlet.http.HttpServletResponse 

import org.apache.log4j.Logger;

import org.codehaus.groovy.grails.plugins.springsecurity.GrailsAuthenticationProcessingFilter 
import org.springframework.security.Authentication 
import org.springframework.security.AuthenticationException 
import org.springframework.security.userdetails.UsernameNotFoundException 

class SynoteLdapAuthenticationProcessingFilter extends GrailsAuthenticationProcessingFilter {
	
	/** Session key for the most recent successful LDAP authentication. */ 
	static final String LDAP_LAST_AUTH = 'LDAP_LAST_AUTH' 
	private Logger log = Logger.getLogger(getClass());
	
	@Override 
	protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) { 
		
		log.debug("start sending redirect...")
		Authentication authentication = findAuthentication(request) 
		if (authentication) { 
			log.debug("Get username:"+authentication.getName())
			request.session[LDAP_LAST_AUTH] = authentication 
			url = '/login/autoCreateUser' 
		} 
		log.debug ("redirect url:"+url)
		super.sendRedirect request, response, url 
	} 
	
	private Authentication findAuthentication(HttpServletRequest request) { 
		AuthenticationException exception = request.session[SPRING_SECURITY_LAST_EXCEPTION_KEY] 
		
		log.debug("Throw out exception:"+exception.getClass().getName())
		if (!(exception instanceof UsernameNotFoundException)) { 
			return null 
		} 
		
		return exception.authentication 
	} 
}

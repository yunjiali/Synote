package org.synote.user;

import org.springframework.security.*
import org.springframework.security.providers.*
import org.springframework.security.userdetails.*
import org.springframework.security.context.*
import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUserImpl
import org.synote.user.User
import org.synote.user.SecurityService
import org.apache.log4j.Logger;

/**
 * 
 * @author Yunjia Li
 * synote authentication provider. The old synote programme user a different userName and password way to
 * authenticate users
 */
class SynoteAuthenticationProvider implements AuthenticationProvider{
	
	def authenticateService
	private Logger log = Logger.getLogger(getClass());
	
	Authentication authenticate(Authentication synoteAuth) {
		
		User.withTransaction { status ->
			User user = User.findByUserName(synoteAuth.getPrincipal())
			//log.debug("psw:"+synoteAuth.getPrincipal().toLowerCase()+synoteAuth.getCredentials())
			if(user) {
				//if(synoteAuth instanceof SynoteAPIAuthentication)
				// then do some that for api authentication if necessary
				if (user?.password == authenticateService.encodePassword(synoteAuth.getPrincipal().toLowerCase()+synoteAuth.getCredentials())) 
				{
					GrantedAuthorityImpl[] authorities = 
						user.authorities.collect 
						{
							new GrantedAuthorityImpl(it.authority)
						}
					def userDetails = new GrailsUserImpl(user.userName, user.password, true, true, true, true, authorities, user)
					def token = new UsernamePasswordAuthenticationToken(userDetails, user.password, userDetails.authorities)
					token.details = synoteAuth.details
					
					return token
				}else 
					throw new BadCredentialsException("Log in failed - identity could not be verified");
					//return null
			}else {
				throw new BadCredentialsException("Log in failed - identity could not be verified");
			}
		}
	}
	
	boolean supports(Class authentication) {
		return true
	}
}

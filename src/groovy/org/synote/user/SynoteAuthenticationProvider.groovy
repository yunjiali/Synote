package org.synote.user;

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.BadCredentialsException

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
	
	def springSecurityService
	def userDetailsService
	private Logger log = Logger.getLogger(getClass());
	
	@Override
	Authentication authenticate(Authentication synoteAuth) {
		
		User.withTransaction { status ->
			User user = User.findByUserName(synoteAuth.getPrincipal())
			//log.debug("psw:"+synoteAuth.getPrincipal().toLowerCase()+synoteAuth.getCredentials())
			if(user) {
				//if(synoteAuth instanceof SynoteAPIAuthentication)
				// then do some that for api authentication if necessary
				if (user?.password == springSecurityService.encodePassword(synoteAuth.getPrincipal().toLowerCase()+synoteAuth.getCredentials())) 
				{
					def userDetails = userDetailsService.loadUserByUsername(user.userName)
					def token = new UsernamePasswordAuthenticationToken(userDetails, user.password, userDetails.getAuthorities())
					//token.details = synoteAuth.details
					
					return token
				}else 
					throw new BadCredentialsException("Log in failed - identity could not be verified");
					//return null
			}else {
				throw new BadCredentialsException("Log in failed - identity could not be verified");
			}
		}
	}
	
	@Override
	public boolean supports(Class<? extends Object> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class)
    }

}

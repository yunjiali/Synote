package org.synote.user

import org.springframework.security.*
import org.springframework.security.providers.*
import org.springframework.security.userdetails.*
import org.springframework.security.context.*
import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUserImpl
import org.synote.user.User
import org.synote.user.UserAPIKey
import org.synote.user.SecurityService
import org.apache.log4j.Logger
import org.synote.api.exception.SynoteAPIException
import org.synote.api.APIStatusCode

/**
*
* @author Yunjia Li
* synote api authentication provider.
*/
class SynoteAPIAuthenticationProvider implements AuthenticationProvider
{
	def authenticateService
	private Logger log = Logger.getLogger(getClass());
	
	Authentication authenticate(Authentication synoteAuth) {
		
		User.withTransaction { status ->
			UserAPIKey userAPIKey = UserAPIKey.findByUserKey(synoteAuth.getCredentials())
			//log.debug("psw:"+synoteAuth.getPrincipal().toLowerCase()+synoteAuth.getCredentials())
			if(userAPIKey) {
				User user = userAPIKey.user
				
				GrantedAuthorityImpl[] authorities =
					user.authorities.collect
					{
						new GrantedAuthorityImpl(it.authority)
					}
				def userDetails = new GrailsUserImpl(user.userName, user.password, true, true, true, true, authorities, user)
				def token = new UsernamePasswordAuthenticationToken(userDetails, user.password, userDetails.authorities)
				token.details = synoteAuth.details
				
				return token
				
			}else if(synoteAuth?.getCredentials() != null){
				throw new SynoteAPIException("The user api key is not valid!", APIStatusCode.USER_API_KEY_INVALID)
			}
			else
			{
				return null
			}
		}
	}
	
	boolean supports(Class authentication) {
		return true
	}
}

package org.synote.user

import org.springframework.security.ui.*
import org.springframework.security.context.*
import org.springframework.beans.factory.*
import org.springframework.context.*
import javax.servlet.*
import javax.servlet.http.*
import org.apache.log4j.Logger

import org.synote.api.exception.SynoteAPIException
import org.synote.api.APIStatusCode

/**
 * 
 * @author Yunjia Li
 * This filter is used to authenticate restful api request via apikeys
 */
class SynoteAPITokenFilter extends SpringSecurityFilter implements InitializingBean{

	def synoteAPIAuthenticationProvider
	private Logger log = Logger.getLogger(getClass())
	
	void doFilterHttp(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
	
			//if (SecurityContextHolder.getContext().getAuthentication() == null) {
	
				def userAPIKey = request.getParameter("apiKey")
				if (userAPIKey) {
	
					def apiAuth = new SynoteAPIAuthentication(
								credentials:userAPIKey,
								authenticated:true
								)
					try
					{
						def synoteAuth = synoteAPIAuthenticationProvider.authenticate(apiAuth)
						log.debug("User has already logged in? "+SecurityContextHolder.context.authentication)
						if (synoteAuth && SecurityContextHolder.context.authentication == null) {
							log.debug("Successfully Authenticated ${userAPIKey} and add it to session.")
							// Store to SecurityContextHolder
							SecurityContextHolder.context.authentication = synoteAuth
						}
					}
					catch(Exception ex)
					{
						log.debug("Failed Authenticated ${userAPIKey}.")
						request.session[APIStatusCode.API_AUTH_EXCEPTION] = ex
					}
				}
				else
				{
					log.debug("user api key has not been provided!")
					//We allow request as anonymous user
					//request.session[APIStatusCode.API_AUTH_EXCEPTION] = new SynoteAPIException("user api key has not been provided!",APIStatusCode.USER_API_KEY_INVALID)
				}
			//}
			//else
			//{
			//	log.debug("User has logged in.");
			//}
			chain.doFilter(request, response)
		}
	
		int getOrder() {
			return FilterChainOrder.REMEMBER_ME_FILTER
		}
		
		void afterPropertiesSet() {
			return
		}
}

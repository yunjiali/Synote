package org.synote.user

import org.springframework.security.ui.*
import org.springframework.security.context.*
import org.springframework.beans.factory.*
import org.springframework.context.*
import javax.servlet.*
import javax.servlet.http.*

import org.apache.log4j.Logger
import org.synote.api.APIStatusCode
import org.synote.api.exception.SynoteAPIException

class SynoteAPILoginFilter  extends SpringSecurityFilter implements InitializingBean{
	def synoteAuthenticationProvider
	private Logger log = Logger.getLogger(getClass())
	
	void doFilterHttp(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
	
			//if (SecurityContextHolder.getContext().getAuthentication() == null) {
	
				def userName = request.getParameter("userName")
				def psw = request.getParameter("psw")
				if (userName && psw) {
	
					def apiAuth = new SynoteAPIAuthentication(
								principal:userName,
								credentials:psw,
								authenticated:true
								)
	
					try
					{
						def synoteAuth = synoteAuthenticationProvider.authenticate(apiAuth)
						if (synoteAuth) {
							log.debug("Successfully Authenticated ${userName}.")
							// Store to SecurityContextHolder
							SecurityContextHolder.context.authentication = synoteAuth
						}
					}
					catch(Exception ex)
					{
						log.debug("Failed Authenticated ${userName}.")
						request.session[APIStatusCode.API_AUTH_EXCEPTION] = ex
					}
				}
				
				else
				{
					log.debug("Authentication information username or password is not provided!")
					request.session[APIStatusCode.API_AUTH_EXCEPTION] = new SynoteAPIException("Authentication information username or password is not provided!", APIStatusCode.AUTHENTICATION_FAILED)
				}
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

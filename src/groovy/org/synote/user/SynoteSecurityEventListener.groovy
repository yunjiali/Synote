package org.synote.user;

import org.springframework.context.*
import org.springframework.security.event.authentication.*
import org.springframework.security.event.authorization.AbstractAuthorizationEvent
import org.synote.user.User
import org.synote.user.SecurityService

class SynoteSecurityEventListener implements ApplicationListener{
	
	def securityService
	/**
	 * {@inheritDoc}
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(
	 *     org.springframework.context.ApplicationEvent)
	 */
	void onApplicationEvent(final ApplicationEvent e) 
	{
		if (e instanceof AbstractAuthenticationEvent) 
		{
			if (e instanceof InteractiveAuthenticationSuccessEvent) {
				// handle InteractiveAuthenticationSuccessEvent
			}
			else if (e instanceof AbstractAuthenticationFailureEvent) {
				// handle AbstractAuthenticationFailureEvent
			}
			else if (e instanceof AuthenticationSuccessEvent) {
				def user = User.findByUserName(e.authentication.principal.username)
				if(user)
				{
					securityService.saveLoginDate(user)
				}
			}
			else if (e instanceof AuthenticationSwitchUserEvent) {
				// handle AuthenticationSwitchUserEvent
			}
			else {
				// handle other authentication event
			}
		}
		else if (e instanceof AbstractAuthorizationEvent) {
			// handle authorization event
		}
	}
}

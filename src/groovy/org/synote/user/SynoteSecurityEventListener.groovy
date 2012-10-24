package org.synote.user;

import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.*
import org.springframework.security.core.Authentication

import org.synote.user.User
import org.synote.user.SecurityService

class SynoteSecurityEventListener implements ApplicationListener<AbstractAuthenticationEvent>{
	
	def securityService
	/**
	 * {@inheritDoc}
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(
	 *     org.springframework.context.ApplicationEvent)
	 */
	void onApplicationEvent(AbstractAuthenticationEvent event) 
	{
		User.withTransaction { status ->
			event.authentication.with {
				if (event instanceof AuthenticationSuccessEvent) {
					def user = User.findByUserName(event.authentication.principal.username)
					if(user)
					{
						securityService.saveLoginDate(user)
					}
				}
			}
		 }
	}
}

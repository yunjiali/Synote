// Place your Spring DSL code here
//import org.synote.user.SynoteAuthenticationProvider
import org.synote.user.SynoteSecurityEventListener
import org.synote.search.GoogleCrawlFilter
import org.synote.user.SecurityService
//import org.springframework.ldap.core.support.BaseLdapPathContextSource
//import org.springframework.ldap.core.ContextSource
//import org.springframework.security.ldap.SpringSecurityContextSource;
//import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
//import org.synote.user.SynoteLdapAuthenticator

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils as SSU
 
//import org.synote.user.SynoteLdapAuthenticationProcessingFilter 
//import org.synote.user.AutoCreateLdapUserDetailsMapper 

import org.synote.user.SynoteSecurityEventListener

beans = {
	
	def conf = SSU.getSecurityConfig()//AuthorizeTools.securityConfig.security 
	
//	ldapAuthenticator(SynoteLdapAuthenticator, ref('contextSource')) 
//	{
//		userSearch = ref('ldapUserSearch')
//	}
	
	synoteAuthenticationProvider(org.synote.user.SynoteAuthenticationProvider) {
		springSecurityService = ref("springSecurityService")
		userDetailsService = ref("userDetailsService")
	}
	
	googleCrawlFilter(GoogleCrawlFilter)
	{
		//declare nothing
	}
	
	securityEventListener(SynoteSecurityEventListener) 
	{
		securityService = ref("securityService")
	}
}
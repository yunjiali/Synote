// Place your Spring DSL code here
//import org.synote.user.SynoteAuthenticationProvider
import org.synote.user.SynoteSecurityEventListener
import org.synote.search.GoogleCrawlFilter
import org.synote.user.SecurityService

import org.synote.search.resource.converter.*

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
	
	
	multimediaConverter(MultimediaConverter)
	synmarkConverter(SynmarkConverter)
	webVTTCueConverter(WebVTTCueConverter)
	permissionValueConverter(PermissionValueConverter)
}
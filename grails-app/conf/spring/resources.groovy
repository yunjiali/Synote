// Place your Spring DSL code here
import org.synote.user.SynoteAuthenticationProvider
import org.synote.user.SynoteSecurityEventListener
import org.synote.user.SynoteAPITokenFilter
import org.synote.user.SynoteAPILoginFilter
import org.synote.search.GoogleCrawlFilter
import org.synote.user.SynoteAPIAuthenticationProvider
import org.synote.user.SecurityService
//import org.springframework.ldap.core.support.BaseLdapPathContextSource
//import org.springframework.ldap.core.ContextSource
//import org.springframework.security.ldap.SpringSecurityContextSource;
//import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
//import org.synote.user.SynoteLdapAuthenticator

import org.codehaus.groovy.grails.plugins.springsecurity.AuthorizeTools 
//import org.synote.user.SynoteLdapAuthenticationProcessingFilter 
//import org.synote.user.AutoCreateLdapUserDetailsMapper 

beans = {
	
	def conf = AuthorizeTools.securityConfig.security 
	
//	ldapAuthenticator(SynoteLdapAuthenticator, ref('contextSource')) 
//	{
//		userSearch = ref('ldapUserSearch')
//	}
	
	synoteAuthenticationProvider(SynoteAuthenticationProvider) {
		authenticateService = ref("authenticateService")
	}
	
	synoteAPIAuthenticationProvider(SynoteAPIAuthenticationProvider) {
		authenticateService = ref("authenticateService")
	}
	
	synoteAPITokenFilter(SynoteAPITokenFilter) {
		//userDetailsService = ref("userDetailsService")
		synoteAPIAuthenticationProvider = ref("synoteAPIAuthenticationProvider")
		//customProvider = ref("customAppTokenAppTokenAuthenticationProvider")
	}
	
	synoteAPILoginFilter(SynoteAPILoginFilter) {
		//userDetailsService = ref("userDetailsService")
		synoteAuthenticationProvider = ref("synoteAuthenticationProvider")
		//customProvider = ref("customAppTokenAppTokenAuthenticationProvider")
	}
	
	googleCrawlFilter(GoogleCrawlFilter)
	{
		//declare nothing
	}
	
	synoteSecurityEventListener(SynoteSecurityEventListener) 
	{
		securityService = ref("securityService")
	}
	
//	authenticationProcessingFilter(SynoteLdapAuthenticationProcessingFilter) { 
//		authenticationManager = ref('authenticationManager') 
//		rememberMeServices = ref('rememberMeServices') 
//		authenticateService = ref('authenticateService') 
//		authenticationFailureUrl = conf.authenticationFailureUrl 
//		ajaxAuthenticationFailureUrl = conf.ajaxAuthenticationFailureUrl 
//		defaultTargetUrl = conf.defaultTargetUrl 
//		alwaysUseDefaultTargetUrl = conf.alwaysUseDefaultTargetUrl 
//		filterProcessesUrl = conf.filterProcessesUrl 
//	} 
	
//	ldapUserDetailsMapper(AutoCreateLdapUserDetailsMapper) { 
//		userDetailsService = ref('userDetailsService') 
//		passwordAttributeName = conf.ldapPasswordAttributeName 
//		usePassword = conf.ldapUsePassword 
//		retrieveDatabaseRoles = conf.ldapRetrieveDatabaseRoles 
//	} 
}
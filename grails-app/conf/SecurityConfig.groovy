security {

	// see DefaultSecurityConfig.groovy for all settable/overridable properties
	
	active = true

	loginUserDomainClass = "org.synote.user.User"
	userName='userName'
	password = 'password'
	authorityDomainClass = "org.synote.user.UserRole"
	defaultRole='ROLE_NORMAL'
	
	
	//requestMapClass = "org.synote.user.admin.Requestmap"
	useRequestMapDomainClass = false
	useControllerAnnotations = true
	
	//useLdap = true
	//ldapRetrieveDatabaseRoles = true
	//ldapRetrieveGroupRoles = false
	//ldapServer = 'ldaps://nlbldap.soton.ac.uk:636/'
	//ldapSearchBase='ou=user,dc=soton,dc=ac,dc=uk'
	//ldapSearchFilter='(sAMAccountName={0})'
	//ldapUsePassword=false
	
	providerNames=['synoteAuthenticationProvider',
	 //              'ldapAuthProvider',
				   'synoteAPIAuthenticationProvider',
	               'anonymousAuthenticationProvider', 
	               'rememberMeAuthenticationProvider']
	//Add filter for api
	filterInvocationDefinitionSourceMap = [
		'/api/login':'httpSessionContextIntegrationFilter,logoutFilter,authenticationProcessingFilter,securityContextHolderAwareRequestFilter,rememberMeProcessingFilter,synoteAPILoginFilter,anonymousProcessingFilter,exceptionTranslationFilter,filterInvocationInterceptor',
		'/api/error' : 'JOINED_FILTERS',
		'/api/logout' : 'JOINED_FILTERS',
		'/api/**': 'httpSessionContextIntegrationFilter,logoutFilter,authenticationProcessingFilter,securityContextHolderAwareRequestFilter,rememberMeProcessingFilter,synoteAPITokenFilter,anonymousProcessingFilter,exceptionTranslationFilter,filterInvocationInterceptor',
		'/recording/replay/**':'httpSessionContextIntegrationFilter,logoutFilter,authenticationProcessingFilter,securityContextHolderAwareRequestFilter,rememberMeProcessingFilter,googleCrawlFilter,anonymousProcessingFilter,exceptionTranslationFilter,filterInvocationInterceptor',
		'/**': 'JOINED_FILTERS',
	]
	
	defaultTargetUrl='/login/authSuccess'
	alwaysUseDefaultTargetUrl=true
	afterLogoutUrl='/login/index'
}

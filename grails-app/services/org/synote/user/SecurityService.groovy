package org.synote.user

import org.springframework.security.providers.UsernamePasswordAuthenticationToken as AuthToken
import org.springframework.security.context.SecurityContextHolder as SCH
import org.springframework.security.GrantedAuthority
import org.springframework.security.GrantedAuthorityImpl
import org.springframework.security.Authentication
import org.springframework.security.ui.AbstractProcessingFilter 
import org.springframework.util.Assert
import org.springframework.ldap.core.DirContextOperations

import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUserImpl
import org.codehaus.groovy.grails.plugins.springsecurity.AuthorizeTools
import org.synote.utils.RegExService
import org.synote.utils.UtilsService
import org.synote.user.User
import org.synote.user.group.UserGroupMember
import org.synote.user.UserRole
import org.synote.user.exception.UserAuthenticationException
import org.synote.user.SynoteLdapAuthenticationProcessingFilter
import org.synote.user.AutoCreateLdapUserDetailsMapper


class SecurityService {

	def authenticateService
	def regExService
	def utilsService
	
	private Logger log = Logger.getLogger(getClass());
	
	boolean transactional = true
	
	def getPswInputString(userName, password)
	{
		return userName.toLowerCase()+ password
	}
	
	def isAdmin(user)
	{
		if(user.userName.equals("admin"))
			return true
		else
			return false
	}
	
	def isLoggedIn()
    {
		return isAuthenticated()
	}
	
	def isNotLoggedIn()
	{
		return getLoggedUser() == null
	}
	
	def isNormalLoggedIn()
    {	
		return AuthorizeTools.ifAllGranted("ROLE_NORMAL")
	}
	
	def isAdminLoggedIn()
	{
		return AuthorizeTools.ifAllGranted("ROLE_ADMIN")
	}
	
	def getGuestUser()
	{
		UserRole guestRole = UserRole.findByAuthority("ROLE_GUEST")
		User user
		if(guestRole.people.size() >0)
		{
			return guestRole.people.toArray()[0]
		}
		else
			return null
	}
	
	def getLoggedUser()
	{
		if(isLoggedIn())
			return authenticateService.userDomain()
		else
			return null
	}
	
	def isOwnerOrAdmin(owner)
	{
		if(isAdminLoggedIn())
			return true
			
		def cUser = getLoggedUser() //current user
		
		if(owner?.class == String)
			owner = owner.toLong()

		def oUser = User.get(owner)

		return cUser?.id.equals(oUser?.id)
	}
	
	def saveLoginDate(User user)
	{
		user.lastLogin = new Date()
		if(user.merge(flush:true))
		{
			return true
		}
		else
		{
			return false
		}
	}
	
	def encodePassword(String userName, String psw)
	{
		return authenticateService.passwordEncoder(userName.toLowerCase()+psw)
	}
	
	//TODO:Change the ipaddress pattern in securityConfig.groovy
	def isAllowedIPAddress(String ipAddress)
	{
		if(isAllowedIPv4Address(ipAddress) && isAllowedIPv6Address(ipAddress))
		{
			return true
		}
		else
		{
			return false
		}
		//return true
	}
	
	def createUserFromLdap(ldapAuth,authorities,ctx)
	{
		Assert.isInstanceOf(DirContextOperations.class, ctx,
				"ContextSource is not valid");
		//User.withTransaction{status->
			
			log.debug "Create user ${ldapAuth.name} from ldap"
			//TODO:Use database configuration to map attributes in User to the attributes in ldap
			def person = User.findByUserName(ldapAuth.name)
			if(person)
			{
				throw new UserAuthenticationException("Cannot create new user. The user ${ldapAuth.name} has already been taken.")
			}
			def user = new User(
							userName: ldapAuth.name,
							email: ctx.getObjectAttribute("mail"),
							enabled: true, 
							password: 'notused',
							confirmedPassword:'notused',
							firstName:ctx.getObjectAttribute("givenName"),
							lastName:ctx.getObjectAttribute("sn"),
							lastLogin:new Date())
			def roles = convertLdapGroupFromCtx(ctx)
			log.debug(roles)
			GrantedAuthorityImpl[] newAuthorities = roles.collect{
				new GrantedAuthorityImpl(it.authority)
			}
			roles.each {role->
				role.addToPeople(user)
			}
			
			if(!user.validate())
			{
				user.errors.allErrors.each{
				
					log.debug(it)
				}
			}
			log.debug("User error:"+user.errors)
			if(!user.save(flush: true))
			{
				throw new UserAuthenticationException("Cannot create new user ${ldapAuth.name}")
				//user.save(flush:true)
			}
			// manually authenticate now since they're a valid LDAP user and now 
			// have a corresponding database user too 
			def userDetails = new GrailsUserImpl(user.userName, user.password, 
					true, true, true, true, newAuthorities, user) 
			SCH.context.authentication = new AuthToken( 
					userDetails, user.password, newAuthorities) 
			
			log.debug("Grant user ${ldapAuth.name} with roles:"+user.authorities)
			//return user
		//}
	}
	
	def isAllowedIPv4Address(String ipAddress)
	{
		return regExService.isAllowedIPv4Address(ipAddress)
	}
	
	def isAllowedIPv6Address(String ipAddress)
	{
		return true
	}
	
	private boolean isAuthenticated() {
		def authPrincipal = SCH?.context?.authentication?.principal
		return authPrincipal != null && authPrincipal != 'anonymousUser'
	}
	
	/*
	 * convert groups from ldap server to user roles. ctx is the contextsource
	 * returned by acegi ldap authentication
	 * 
	 */
	private convertLdapGroupFromCtx(DirContextOperations ctx)
	{
		return convertLdapGroupFromStrings(new String()[])
	}
	
	/*
	 * convert groups from ldap server to user roles. groups is a String array
	 * which representing the Dn of groups in ldap
	 */
	private convertLdapGroupFromStrings(String[] groups)
	{
		def roles = []
		//currently, we allocate ROLE_NORMAL to every new created ldap user
		UserRole role = UserRole.findByAuthority("ROLE_NORMAL")
		roles << role
		return roles
	}
}

package org.synote.user

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken as AuthToken
import org.springframework.security.core.context.SecurityContextHolder as SCH
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils as SSU

import org.apache.log4j.Logger
import org.synote.utils.RegExService
import org.synote.utils.UtilsService
import org.synote.user.User
import org.synote.user.group.UserGroupMember
import org.synote.user.UserRole
import org.synote.user.exception.UserAuthenticationException


class SecurityService {

	def springSecurityService
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
		return springSecurityService.isLoggedIn()
	}
	
	def isNotLoggedIn()
	{
		return getLoggedUser() == null
	}
	
	def isNormalLoggedIn()
    {	
		return SSU.ifAllGranted("ROLE_NORMAL")
	}
	
	def isAdminLoggedIn()
	{
		return SSU.ifAllGranted("ROLE_ADMIN")
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
			return springSecurityService.currentUser
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
		return springSecurityService.passwordEncoder(userName.toLowerCase()+psw)
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
	
	def isAllowedIPv4Address(String ipAddress)
	{
		return regExService.isAllowedIPv4Address(ipAddress)
	}
	
	def isAllowedIPv6Address(String ipAddress)
	{
		return true
	}
	
	//private boolean isAuthenticated() {
	//	def authPrincipal = SCH?.context?.authentication?.principal
	//	return authPrincipal != null && authPrincipal != 'anonymousUser'
	//}
}

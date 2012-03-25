package org.synote.user;

import grails.test.*
import org.springframework.security.*
import org.springframework.security.providers.*
import org.springframework.security.userdetails.*
import org.springframework.security.context.*
import org.springframework.security.context.SecurityContextHolder as SCH
import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUserImpl
import org.synote.user.User

/**
 * 
 * @author Rabbit
 * Parent class for integration tests. Providing login authentication, logout
 * and other methods.
 */
class SynoteAuthenticationBaseTests extends GrailsUnitTestCase
{
	def grailsApplication
	
	protected void setUp() {
		super.setUp()
		
	}
	
	protected void tearDown() {
		super.tearDown()
		SCH.context.authentication = null
	}
	
	protected Authentication authenticate(User user, String credentials) {
		
		def authorities = user.authorities.collect { new GrantedAuthorityImpl(it.authority) }
		
		def userDetails = new GrailsUserImpl(user.userName, user.password, user.enabled, user.enabled, user.enabled, user.enabled, authorities as GrantedAuthority[], user)
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, user.password, userDetails.authorities)
		
		SCH.context.authentication = authentication
		
		return authentication
	}
	
	protected Authentication sampleUserLogin()
	{
		String testUserName = grailsApplication.config.synote.test.account.userName
		String testPsw = grailsApplication.config.synote.test.account.password
		String testRole = grailsApplication.config.synote.test.account.role
		String credentials = testUserName.toLowerCase()+testPsw
		def user = User.findByUserName(testUserName)    // or create a new one if one doesn't exist
		return authenticate(user, credentials)
	}
	
	
	
	protected Authentication adminLogin()
	{
		String adminUserName = grailsApplication.config.synote.test.admin.userName
		String adminPsw = grailsApplication.config.synote.test.admin.password
		String adminRole = grailsApplication.config.synote.test.admin.role
		String credentials = adminUserName.toLowerCase()+adminPsw
		def user = User.findByUserName(adminUserName)    // or create a new one if one doesn't exist
		return authenticate(user, credentials)
	}
	
	protected void userLogout()
	{
		SCH.context.authentication = null
	}
}

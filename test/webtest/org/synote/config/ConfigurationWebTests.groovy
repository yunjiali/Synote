package org.synote.config

import org.synote.utils.MessageService
import org.synote.user.SynoteAuthBaseWebTest
import org.synote.config.ConfigurationService

/*
 * Test the configuration controllers and pages
 */
class ConfigurationWebTests extends SynoteAuthBaseWebTest {
	
	def messageService
	def grailsApplication
	def configurationService
	
	void suite()
	{
		testListConfig()
	}
	
	/*
	 * Test list configuration page. This page should only been seen by administrators
	 */
	void testListConfig()
	{
		String userName = grailsApplication.config.synote.test.account.userName
		String password = grailsApplication.config.synote.test.account.password
		
		String adminName = grailsApplication.config.synote.test.admin.userName
		String adminPsw = grailsApplication.config.synote.test.account.password
		
		String loginPageTitle = messageService.getMessage("org.synote.user.login.title")
		String accessDeniedPageTitle = messageService.getMessage("org.synote.user.login.denied.title")
		String configurationListPageTitle = messageService.getMessage("org.synote.config.configuration.list.title")
		
		webtest("No user logged in")
		{
			invoke("configuration/index")
			verifyTitle(text:loginPageTitle)
		}
		
		webtest("normal user login")
		{
			login(userName,password)
		}
		
		webtest("Normal user access")
		{
			invoke("configuration/index")
			verifyTitle(text:accessDeniedPageTitle)
		}
		
		webtest("normal user logout")
		{
			logoutUrl()
		}
		
		webtest("admin login")
		{
			login(adminName, password)
		}
		
		webtest("Normal user access")
		{
			invoke("configuration/index")
			verifyTitle(configurationListPageTitle)
		}
		
		webtest("admin logout")
		{
			logoutUrl()
		}
	}
}
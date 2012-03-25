package org.synote.user

import org.synote.utils.MessageService
import org.synote.user.SynoteAuthBaseWebTest


/*
 * Test login and logout and some pages under user controller
 */
class UserWebTests extends SynoteAuthBaseWebTest
{
	def authenticateService
	def messageService
	def grailsApplication
	
	void suite()
	{
		testUserLoginLogout()
		testListUsers()
		testShowEditDeleteUsers()
	}
	
	
	/*
	 * test user login and logout function
	 */
	void testUserLoginLogout() 
	{
		webtest("Test login")
		{
			String userName = grailsApplication.config.synote.test.account.userName
			String password = grailsApplication.config.synote.test.account.password
			
			String loginPageTitle = messageService.getMessage("org.synote.user.login.title")
			String homePageTitle = messageService.getMessage("org.synote.home.title")
			//Login
			invoke(url:'login')
			verifyTitle(text:loginPageTitle)
			//set login inpuloginPageTitlet
			setInputField(name:'j_username',userName)
			setInputField(name:'j_password', password)
			clickButton(label:'Login')
			//Go to home page after login
			verifyTitle(text:homePageTitle)
			//Logout
			clickLink(label:'Log Out')
			//should be in login page again
			verifyTitle(text:loginPageTitle)
			//test finished
		}
    }
	
	/*
	 * Test if everybody can see this page
	 * 
	 * Expect result: Any body can see this page
	 */
	void testListUsers()
	{
		webtest("Test List Users")
		{
			invoke(url:"user/list")
			verifyTitle(text:messageService.getMessage("org.synote.user.list.title"))
		}
	}
	
	/*
	 * Test if only admin can see show,edit,delete user pages
	 * 
	 * Expect result: Only admin can see this page. User not logged in or normal users should be directed to login page
	 */
	void testShowEditUsers()
	{
		String testUserId = grailsApplication.config.synote.test.account.userId
		String userName = grailsApplication.config.synote.test.account.userName
		String password = grailsApplication.config.synote.test.account.password
		
		String adminName = grailsApplication.config.synote.test.admin.userName
		String adminPsw = grailsApplication.config.synote.test.admin.password
		
		String loginPageTitle = messageService.getMessage("org.synote.user.login.title")
		String homePageTitle = messageService.getMessage("org.synote.home.title")
		String editPageTitle=messageService.getMessage("org.synote.user.edit.title")
		String showPageTitle = messageService.getMessage("org.synote.user.show.title")
		String deniedPageTitle = messageService.getMessage("org.synote.user.login.denied.title")

		webtest("Test Show Edit Users when not logged in")
		{
			invoke(url:"user/show/${testUserId}")
			verifyTitle(text:loginPageTitle)
			
			invoke(url:"user/edit/${testUserId}")
			verifyTitle(text:loginPageTitle)
		}
		
		webtest("Test Show Edit Users when test user logged in")
		{
			
			//Login
			invoke(url:'login')
			verifyTitle(text:loginPageTitle)
			//set login inpuloginPageTitlet
			setInputField(name:'j_username',userName)
			setInputField(name:'j_password', password)
			clickButton(label:'Login')
			//Go to home page after login
			verifyTitle(text:homePageTitle)
			
			invoke(url:"user/show/${testUserId}")
			verifyTitle(text:deniedPageTitle)
			
			invoke(url:"user/edit/${testUserId}")
			verifyTitle(text:deniedPageTitle)
			
			clickLink(label:'Log Out')
			verifyTitle(text:loginPageTitle)
		}
		
		webtest("Test Show Edit Users when admin logged in")
		{
			invoke(url:'login')
			verifyTitle(text:loginPageTitle)
			//set login inpuloginPageTitlet
			setInputField(name:'j_username',adminName)
			setInputField(name:'j_password', adminPsw)
			clickButton(label:'Login')
			//Go to home page after login
			verifyTitle(text:homePageTitle)
			
			invoke(url:"user/show/${testUserId}")
			verifyTitle(text:showPageTitle)
			
			invoke(url:"user/edit/${testUserId}")
			verifyTitle(text:editPageTitle)
		}
	}
}
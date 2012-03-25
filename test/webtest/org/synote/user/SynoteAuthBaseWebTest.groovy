package org.synote.user

import org.synote.utils.MessageService
import org.synote.user.User
import org.synote.user.UserRole

class SynoteAuthBaseWebTest extends grails.util.WebTest 
{
	def messageService
	def grailsApplication
	
	def login(String userName, String password)
	{
		ant.group(description:"correct login")
		{
			invoke(url:'login')
			verifyTitle(text:messageService.getMessage("org.synote.user.login.title"))
			
			setInputField(name:'j_username',userName)
			setInputField(name:'j_password', password)
			clickButton(label:'Login')
			
			verifyTitle(text:messageService.getMessage("org.synote.home.title"))
		}
	}
	
	def logoutClickLink()
	{
		ant.group(description:'correct logout')
		{
			clickLink(label:'Log Out')
			verifyTitle(text:messageService.getMessage("org.synote.user.login.title"))
		}
	}
	
	def logoutUrl()
	{
		ant.group(description:'logout via url')
		{
			invoke("logout")
			verifyTitle(text:messageService.getMessage("org.synote.user.login.title"))
		}
	}
	
	protected User getTempUser(String tempUserName)
	{
		def tempUser = User.buildLazy(userName:tempUserName)
		def normal = UserRole.findByAuthority("ROLE_NORMAL")
		def role = tempUser.authorities.find{it.id==normal.id}
		
		if(!role)
			tempUser.addToAuthorities(normal)
		
		return tempUser
	}
	
}

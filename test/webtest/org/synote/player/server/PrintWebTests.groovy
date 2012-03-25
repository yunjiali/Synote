package org.synote.player.server

import org.synote.utils.MessageService
import org.synote.user.SynoteAuthBaseWebTest
import org.synote.resource.compound.MultimediaResource
import org.synote.user.User

class PrintWebTests extends SynoteAuthBaseWebTest {
	
	def messageService
	def grailsApplication
	
	void suite()
	{
		testPrint()
	}
	
	/*
	 * Test print functions
	 */
	void testPrint() {
		
		String userName = grailsApplication.config.synote.test.account.userName
		String password = grailsApplication.config.synote.test.account.password
		
		String printPageTitle = messageService.getMessage("org.synote.player.server.recording.print.title")
		String handlePrintPageTitle = messageService.getMessage("org.synote.player.server.recording.handlePrint.title")
		
		def multimediaResource = MultimediaResource.buildLazy()
		
		webtest("Login")
		{	
			login(userName,password)
		}
		
		webtest("print")
		{
			invoke("recording/print/${multimediaResource.id}")
			verifyTitle(text:printPageTitle)
		}
		
		webtest("print preview")
		{
			clickButton(label:"Preview")
			verifyTitle(text:handlePrintPageTitle)
		}
		
		webtest("logout")
		{
			logoutUrl()
		}
    }

}
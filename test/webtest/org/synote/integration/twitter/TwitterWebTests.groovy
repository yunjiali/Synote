package org.synote.integration.twitter

import org.synote.utils.MessageService
import org.synote.user.SynoteAuthBaseWebTest
import org.synote.resource.compound.MultimediaResource
import org.synote.user.User


class TwitterWebTests extends SynoteAuthBaseWebTest {

	def messageService
	def grailsApplication

	void suite()
	{
		testTwitter()
	}
	
	void testTwitter() {
		
		String userName = grailsApplication.config.synote.test.account.userName
		String password = grailsApplication.config.synote.test.account.password
		String twitterUserName = "afterglowlee"
		
		String multimediaShowPageTitle = messageService.getMessage("org.synote.resource.compound.multimediaResource.show.title")
		String twitterCreatePageTitle = messageService.getMessage("org.synote.integration.twitter.create.title")
		String twitterListPageTitle = messageService.getMessage("org.synote.integration.twitter.list.title")
		String twitterSavePageTitle = messageService.getMessage("org.synote.integration.twitter.save.title")

		def multimediaResource = MultimediaResource.buildLazy()

		webtest("login")
		{
			login(userName,password)
		}		
		
		webtest("upload tweets")
		{
			invoke("multimediaResource/show/${multimediaResource.id}")
			verifyTitle(text:multimediaShowPageTitle)
			
			clickLink "Upload Tweets"
			verifyTitle(text:twitterCreatePageTitle)
			
			setInputField(name:'twitter_userName',twitterUserName)
			
			clickButton(label:"List Tweets")
			verifyText("No tweets present in the list")
		}
		
		webtest("logout")
		{
			logoutUrl()
		}
    }

}
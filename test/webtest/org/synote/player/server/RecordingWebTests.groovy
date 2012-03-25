package org.synote.player.server

import org.synote.utils.MessageService
import org.synote.user.SynoteAuthBaseWebTest
import org.synote.resource.compound.MultimediaResource
import org.synote.user.User

class RecordingWebTests extends SynoteAuthBaseWebTest {

	def messageService
	def grailsApplication
	
	void suite()
	{
		testGuestReplay()
	}
	
	void testGuestReplay()
	{
		String userName = grailsApplication.config.synote.test.account.userName
		String password = grailsApplication.config.synote.test.account.password
		
		def owner = User.findByUserName(userName)
		
		def multimediaResource = MultimediaResource.buildLazy(owner:owner)
		
		String loginPageTitle = messageService.getMessage("org.synote.user.login.title")
		String loginPageGuestReplay = messageService.getMessage("org.synote.user.login.auth.guestReplay")
		String synotePlayerPageTitle = messageService.getMessage("org.synote.player.server.recording.replay.title")
		
		webtest("Guest replay. Replay without logging in.")
		{
			invoke("recording/replay/${multimediaResource.id}")
			verifyTitle(text:loginPageTitle)
			
			clickLink loginPageGuestReplay
			verifyTitle(text:synotePlayerPageTitle)
		}
    }

}
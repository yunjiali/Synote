package org.synote.user

import grails.test.*
import org.synote.resource.compound.MultimediaResource

class SecurityServiceTests extends SynoteAuthenticationBaseTests {
	
	def securityService
	protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testGetLoggedUser() {
		def synoteAuth = sampleUserLogin()
		assertTrue securityService.isLoggedIn()
		
		def user = securityService.getLoggedUser()
		assertNotNull "There should be some user logged in.", user
		userLogout()
    }
	
	void testIsOwnerOrAdmin1()
	{
		def synoteAuth = sampleUserLogin()
		assertTrue securityService.isLoggedIn()
		
		def multimediaResource = MultimediaResource.findByTitle("Keyboard cat")
		assertTrue securityService.isOwnerOrAdmin(multimediaResource?.owner?.id)
		
		assertTrue securityService.isOwnerOrAdmin(multimediaResource?.owner?.id.toString())
		
		userLogout()
		
		def adminAuth = adminLogin()
		assertTrue securityService.isAdminLoggedIn()
		assertTrue securityService.isOwnerOrAdmin(multimediaResource?.owner?.id)
	}
}

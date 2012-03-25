package org.synote.permission

import grails.test.*
import org.synote.user.User
import org.synote.resource.Resource
import org.synote.resource.compound.MultimediaResource
import org.synote.user.SynoteAuthenticationBaseTests

class PermServiceTests extends SynoteAuthenticationBaseTests {
	
	def permService
	def securityService
	
	protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
		userLogout()
    }

    void testMultimediaResourcePermAnonymous() {
		
		//Get Synote Guide recording
		def multimediaResource = MultimediaResource.findByTitle("Synote Guide")
		def pv = permService.getPerm(multimediaResource)
		
		assertEquals 200,pv.val
    }
	
	void testMultimediaResourcePermOwner() {
		def multimediaResource = MultimediaResource.findByTitle("Keyboard cat")
		def pvAnonymous = permService.getPerm(multimediaResource)
		assertEquals 200,pvAnonymous.val

		//Owner Login
		def synoteAuth = sampleUserLogin()
		assertTrue securityService.isLoggedIn()
		
		def pvOwner = permService.getPerm(multimediaResource)
		assertEquals 300,pvOwner.val
		
		userLogout()
	}
	
	void testMultimediaResourcePermAdmin()
	{
		def multimediaResource = MultimediaResource.findByTitle("Keyboard cat")
		def adminAuth = adminLogin()
		assertTrue securityService.isAdminLoggedIn()
		
		def pvAdmin = permService.getPerm(multimediaResource)
		assertEquals 300,pvAdmin.val
		
		userLogout()
	}
	
	/*
	 * Resources like synmark resources, multimediaResource and transcriptResource
	 * don't have permissions in old synote. In new synote, we define the permission of
	 * this kind of resource can inherit the permission of the resource it annotates (usually a multimedia
	 * resource) if the permission of this resource is NULL
	 */
	void testPermissionHierarchy()
	{
		
	}
}

package org.synote.resource.single.binary

import grails.test.*
import org.synote.user.User
import org.synote.resource.compound.MultimediaResource

class MultimediaUrlTests extends GrailsUnitTestCase {
	
	def user
	
	protected void setUp() {
        super.setUp()
        mockDomain(User)
        mockDomain(MultimediaUrl)
		mockDomain(MultimediaResource)
        user = new User(userName:"urlTest")
    }

    protected void tearDown() {
        super.tearDown()
    }
	
	/*
	 * Test the url field for MultimediaUrl domain class cannot be null
	 */
    void testUrlNull() {
		
		def multimediaResource = new MultimediaResource(owner:user)
		def multimediaUrl = new MultimediaUrl(multimedia:multimediaResource,title:"MultimediaUrl", perm:null, url:null, owner:user)
		multimediaUrl.validate()
		
		assertEquals 1,multimediaUrl.errors.size()
		assertFalse "Url should not be null", multimediaUrl.validate()
    }
}

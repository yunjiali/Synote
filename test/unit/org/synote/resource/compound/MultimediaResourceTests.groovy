package org.synote.resource.compound

import grails.test.*
import org.synote.user.User
import org.synote.resource.single.binary.MultimediaUrl
import org.synote.resource.single.text.MultimediaTextNote

class MultimediaResourceTests extends GrailsUnitTestCase {
	def user
	
    protected void setUp() {
        super.setUp()
        mockDomain(User)
        mockDomain(MultimediaUrl)
        mockDomain(MultimediaResource)
		mockDomain(MultimediaTextNote)
        user = new User(userName:"urlTest")
    }

    protected void tearDown() {
        super.tearDown()
    }
	
	/*
	 * Test the multimediaResource can not be saved when the multimediaUrl
	 * is null
	 */
    void testSaveWithNullMultimediaUrl() {
		
		def multimediaResource = new MultimediaResource(owner:user,title:"SaveWithInvalidUrl", perm:null)
		def multimediaTextNote = new MultimediaTextNote(owner:user,content:"a note")
		
		multimediaResource.note = multimediaTextNote
	
		assertNull "url field cannot be null",multimediaResource.save()
		
    }
	
	/*
	 * Test the multiemdiaResource can be saved when multimediaTextNote is null
	 */
	void testSaveWithNullMultimediaTextNote()
	{
		def multimediaResource = new MultimediaResource(owner:user,title:"SaveWithInvalidUrl", perm:null)
		def multimediaUrl = new MultimediaUrl(multimedia:multimediaResource,title:"MultimediaUrl", perm:null, url:null, owner:user)
		multimediaResource.url = multimediaUrl
		
		assertNotNull "Note column can be null",multimediaResource.save()
	}
}

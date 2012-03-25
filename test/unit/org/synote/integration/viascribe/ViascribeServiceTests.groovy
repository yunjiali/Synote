package org.synote.integration.viascribe

import grails.test.*
import grails.converters.*
import org.synote.resource.compound.MultimediaResource
import org.springframework.core.io.ClassPathResource
import org.synote.permission.PermissionValue
import org.synote.user.User
import org.synote.annotation.ResourceAnnotation
import org.synote.annotation.synpoint.Synpoint
import org.synote.resource.single.text.SynmarkTag
import org.synote.resource.single.text.SynmarkTextNote
import org.synote.resource.single.binary.PresentationSlide
import org.synote.resource.single.binary.MultimediaUrl
import org.synote.resource.single.text.TranscriptTextResource
import org.synote.resource.compound.*
import org.synote.annotation.*
import org.synote.annotation.synpoint.Synpoint

class ViascribeServiceTests extends GrailsUnitTestCase{
	
	private String EXAMPLE_VIASCRIBE_FILE_PATH = "scripts/test/viascribe/viascribeTest.xml"
	private String EXAMPLE_VSXML_FILE_PATH = "scripts/test/vsxml/vsxmlTest.vsxml"
	private String url = "http://users.ecs.soton.ac.uk/mw/recordings/Mike%20Wald/correctsynotehelpmikenew_2008_10_08_5"
	private String title = "Viascribe Test"
	private PermissionValue perm
	private User user
	
	def viascribeService
	
	protected void setUp() {
        super.setUp()
        mockDomain(MultimediaResource)
		mockDomain(PermissionValue)
		mockDomain(User)
		mockDomain(Synpoint)
		mockDomain(SynmarkTag)
		mockDomain(SynmarkTextNote)
		mockDomain(PresentationSlide)
		mockDomain(MultimediaUrl)
		mockDomain(TranscriptTextResource)
		mockDomain(TranscriptResource)
		mockDomain(SynmarkResource)
		mockDomain(PresentationResource)
		mockDomain(Annotation)
		mockDomain(ResourceAnnotation)
		mockDomain(Synpoint)
		viascribeService = new ViascribeService()
		perm = new PermissionValue(name:"ANNOTATE",val:200)
		user = new User(userName:"viascribeTest")
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testViascribeUpload() 
    {	
		def file = new ClassPathResource(EXAMPLE_VIASCRIBE_FILE_PATH).getFile()
		assert file.exists()
		
		def xml = new XmlSlurper().parseText(file.getText())
		def multimediaResource = viascribeService.upload(user, title, xml, url, perm)
		
		assertNotNull "multimediaResource instance should not be null",multimediaResource
		assertEquals title,multimediaResource.title
		assertNotNull multimediaResource?.url?.url
    }
	
	void testVSXMLTranscriptUpload()
	{
		def file = new ClassPathResource(EXAMPLE_VSXML_FILE_PATH).getFile()
		assert file.exists()
		
		def xml = new XmlSlurper().parseText(file.getText())
		def multimediaResource = viascribeService.uploadMultimedia(user, title, xml, url, perm)
		
		assertNotNull "multimediaResource instance should not be null",multimediaResource
		
		def annotation = viascribeService.uploadTranscript(user,multimediaResource,"TranscriptResource",xml)
		assertNotNull "The annotation should not be null.",annotation
		assertTrue annotation.synpoints.size() > 0
		
		def transcriptResource = annotation.source
		assertNotNull "transcript resource should already been uploaded", transcriptResource
		assertLength(1, transcriptResource.transcripts)
		assertTrue transcriptResource.transcripts.toList().getAt(0)?.content.indexOf("learning society") != -1
	}
}

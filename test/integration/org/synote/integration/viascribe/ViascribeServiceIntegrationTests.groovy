package org.synote.integration.viascribe

import grails.test.*
import grails.converters.*
import org.synote.resource.compound.MultimediaResource
import org.springframework.core.io.ClassPathResource
import org.synote.permission.PermissionValue
import org.synote.user.User
import org.synote.user.SynoteAuthenticationBaseTests
import org.synote.user.SecurityService
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

class ViascribeServiceIntegrationTests extends SynoteAuthenticationBaseTests{
	
	private String EXAMPLE_VIASCRIBE_FILE_PATH = "scripts/test/viascribe/viascribeTest.xml"
	private String EXAMPLE_VSXML_FILE_PATH = "scripts/test/vsxml/vsxmlTest.vsxml"
	private String url = "http://users.ecs.soton.ac.uk/mw/recordings/Mike%20Wald/correctsynotehelpmikenew_2008_10_08_5/"
	private String title = "Viascribe Integration Test"
	private PermissionValue perm
	
	def viascribeService
	def multimediaResource
	def securityService
	
	protected void setUp() {
		super.setUp()
		perm = PermissionValue.findByName("ANNOTATE")
	}
	
	protected void tearDown() {
		super.tearDown()
		//This will remove all the resource created by this test from database
		if(multimediaResource)
		{
			multimediaResource.delete()
		}
	}
	
	void testViascribeUpload() 
	{	
		def synoteAuth = sampleUserLogin()
		assertTrue securityService.isLoggedIn()
		
		def user = securityService.getLoggedUser()
		assertNotNull "There should be some user logged in.", user
		
		def file = new ClassPathResource(EXAMPLE_VIASCRIBE_FILE_PATH).getFile()
		assert file.exists()
		
		def xml = new XmlSlurper().parseText(file.getText())
		
		//test uploadMultimedia method
		def multimediaResource = viascribeService.uploadMultimedia(user, title, xml, url, perm)
		
		assertNotNull "multimediaResource instance should not be null",multimediaResource
		assertEquals title,multimediaResource.title
		assertNotNull multimediaResource?.url?.url
		
		//test uploadTranscript method
		def tAnnotation = viascribeService.uploadTranscript(user, multimediaResource, 'Transcript', xml)
		assertNotNull "transcript annotation should not be null.",tAnnotation
		
		//test uploadPresentation method
		def pAnnotation = viascribeService.uploadPresentation(user, multimediaResource, 'Presentation', xml, url)
		assertNotNull "presentation annotation should not be null.", pAnnotation
		
		//test uploadSlide
		if (xml.slides.slide.size()!= 0)
		{
			//Create Synamark with Slide's text data
			viascribeService.uploadSynmark(user,multimediaResource,xml)
			def synmarkList = ResourceAnnotation.findByTargetAndClass(multimediaResource, "org.synote.resource.compound.SynmarkResource")
			assertNotEquals 0,synmarkList.size()
		}
	}
}


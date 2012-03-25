package org.synote.resource

import grails.test.*
import org.synote.resource.compound.*
import org.synote.resource.single.SingleResource
import org.synote.resource.single.binary.*
import org.synote.resource.single.text.*

class ResourceTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testResourceHierarchy() {
		
		mockDomain(org.synote.resource.single.binary.MultimediaUrl)
		mockDomain(org.synote.resource.single.text.SynmarkTextNote)
		mockDomain(org.synote.resource.compound.TranscriptResource)
		
		def multimediaUrl = new MultimediaUrl()
		assertTrue multimediaUrl instanceof Resource
		assertTrue multimediaUrl instanceof SingleResource
		assertTrue multimediaUrl instanceof BinaryResource
		assertTrue multimediaUrl instanceof UrlResource
		
		def synmarkTextNote = new SynmarkTextNote()
		assertTrue synmarkTextNote instanceof Resource
		assertTrue synmarkTextNote instanceof SingleResource
		assertTrue synmarkTextNote instanceof TextResource
		assertTrue synmarkTextNote instanceof TextNoteResource
		
		def transcriptResource = new TranscriptResource()
		assertTrue transcriptResource instanceof Resource
		assertTrue transcriptResource instanceof CompoundResource
    }
}

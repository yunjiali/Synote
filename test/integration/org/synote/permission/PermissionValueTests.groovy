package org.synote.permission

import grails.test.*

class PermissionValueTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testPermValueHierarchy() 
    {
    	def pvPrivate = PermissionValue.findByName("PRIVATE")
		def pvRead = PermissionValue.findByName("READ")
		def pvAnnotate = PermissionValue.findByName("ANNOTATE")
		def pvWrite = PermissionValue.findByName("WRITE")
		
		assertTrue (pvPrivate.val < pvRead.val && pvPrivate.val < pvAnnotate.val && pvPrivate.val < pvWrite.val)
		assertTrue (pvRead.val<pvAnnotate.val && pvRead.val<pvWrite.val)
		assertTrue (pvAnnotate.val<pvWrite.val)
    }
}

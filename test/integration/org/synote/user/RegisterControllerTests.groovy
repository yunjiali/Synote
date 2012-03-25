package org.synote.user

import grails.test.*

/*
 * Test register controller
 */
class RegisterControllerTests extends GrailsUnitTestCase {
	
	def grailsApplication
	def authenticateService
	def synoteAuthenticationProvider
	def emailerService
	
	User user
	RegisterController rc
	String testUserName
	String testPsw
	
	protected void setUp() {
        super.setUp()
		rc = new RegisterController()
		testUserName = grailsApplication.config.synote.test.create.user.userName
		testPsw = grailsApplication.config.synote.test.create.user.password
		rc.authenticateService = authenticateService
		rc.synoteAuthenticationProvider = synoteAuthenticationProvider
		rc.emailerService = emailerService
    }

    protected void tearDown() {
        super.tearDown()
		user = User.findByUserName(testUserName)
        if(user)
        	user.delete()
    }
	
	//Test Captcha doesn't match
	void testCaptchaInvalid()
	{
		rc.session.captcha = "ABCDEF"
		String captchaStr = "A"//Random captcha
		rc.params.userName = testUserName
		rc.params.password = testPsw
		rc.params.confirm = testPsw
		rc.params.firstName = "synote"
		rc.params.lastName ="test"
		rc.params.email = "synotetest@synote.org"
		rc.params.captcha = captchaStr
		rc.params.termsAndConditions = 1
		
		rc.save()
		
		def message=rc.flash.error
		assert message.startsWith("Access code did not match")
	}

    void testRegistration() 
    {
		//Init captcha, use session['captcha'] to get the string
		rc.session.captcha = "ABCDEF"
		String captchaStr = "ABCDEF"
		rc.params.userName = testUserName
		rc.params.password = testPsw
		rc.params.confirm = testPsw
		rc.params.firstName = "synote"
		rc.params.lastName ="test"
		rc.params.email = "synotetest@synote.org"
		rc.params.captcha = captchaStr
		rc.params.termsAndConditions = 1
		
		rc.save()
		
		user = User.findByUserName(testUserName)
		def message=rc.flash.error
		assertNotNull("User ${testUserName} has been saved", user)
		assertNotNull("User ${testUserName} has logged in", authenticateService.userDomain())
		assertEquals "/user/index",rc.response.redirectedUrl
    }
}

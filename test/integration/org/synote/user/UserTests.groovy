package org.synote.user

import java.util.UUID
import grails.test.*

class UserTests extends GrailsUnitTestCase {
	
	def authenticateService
	def grailsApplication
	String testUserName
	String testPsw
	
	protected void setUp() {
        super.setUp()
		testUserName = grailsApplication.config.synote.test.create.user.userName
		testPsw = grailsApplication.config.synote.test.create.user.password
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testUserNameConstrains() 
    {
		def passwd = authenticateService.passwordEncoder(testPsw)
		
		//Invalide user: first character in userName is not a letter
		def user1 = new User(userName:"1test1",
				password:passwd,
				confirmedPassword:passwd,
				firstName:"synote",
				lastName:"test1",
				enabled:true,
				email: "test1@synote.org",
				lastLogin:new Date())
		assertFalse "userName starts with a number",user1.validate()
		
		//StringBuilder sb = new StringBuilder()
		//sb.append("test1test1234567890testtest1234567890testtest1234567890testtest1234567890testtest1234567890testtest")
		//sb.append("1234567890testtest1234567890testtest1234567890testtest1234567890testtest1234567890testtest1234567890testtest1234567890testtest1234567890testtest1234567890testte")
		//sb.append("st1234567890testtest1234567890testtest1234567890testtest1234567890testtest1234567890testtest1234567890testtest1234567890testtest1234567890testtest1234567890testtest1234567890testtest1234567890testtest123456")
		//sb.append("7890testtest1234567890testtest1234567890testtest1234567890testtest1234567890testtest1234567890testtest1234567890testtest1234567890testtest1234567890test")
		user1 = new User(userName:"test*test",
				password:passwd,
				confirmPassword:passwd,
				firstName:"synote",
				lastName:"test1",
				enabled:true,
				email: "test1@synote.org",
				lastLogin:new Date())
		assertFalse "userName with invalide characters",user1.validate()
		
		//println "\nErrors:"
		//println user1.errors?:"no errors found"
		//Valid user
		def tempUser = User.findByUserName(testUserName)
		if(tempUser)
			testUserName = testUserName+"10101"
		user1 = new User(userName:testUserName,
								password:passwd,
								confirmedPassword:passwd,
								firstName:"synote",
								lastName:"test",
								enabled:true,
								email: "test@synote.org",
								lastLogin:new Date())
		
		assertTrue "valid user", user1.validate()
		
		
		//assertFalse user1.errors["userName"]
    }
}

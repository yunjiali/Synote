package org.synote.config

import grails.test.*

class ConfigurationServiceTests extends GrailsUnitTestCase {
    
	def configurationService
	
	protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testUserRegisterConfig() 
	{
		String enable  = configurationService.getConfigValue("org.synote.user.register.enable")
		assertNotNull enable
    }
	
	void testIBMHTSConfig()
	{
		String protocol  = configurationService.getConfigValue("org.synote.integration.ibmhts.server.protocol")
		String name = configurationService.getConfigValue("org.synote.integration.ibmhts.server.name")
		String port = configurationService.getConfigValue("org.synote.integration.ibmhts.server.port")
		String appPath = configurationService.getConfigValue("org.synote.integration.ibmhts.server.appPath")
		String apiLogin = configurationService.getConfigValue("org.synote.integration.ibmhts.server.api.login")
		String apiLogout = configurationService.getConfigValue("org.synote.integration.ibmhts.server.api.logout")
		String apiAddJob = configurationService.getConfigValue("org.synote.integration.ibmhts.server.api.addJob")
		String apiGetJobs = configurationService.getConfigValue("org.synote.integration.ibmhts.server.api.getJobs")
		String apiGetJob = configurationService.getConfigValue("org.synote.integration.ibmhts.server.api.getJob")
		String apiGetTranscript = configurationService.getConfigValue("org.synote.integration.ibmhts.server.api.getTranscript")
		String apiRemoveJob = configurationService.getConfigValue("org.synote.integration.ibmhts.server.api.removeJob")
		String apiSourceDir = configurationService.getConfigValue("org.synote.integration.ibmhts.server.sourceDir")
		String username = configurationService.getConfigValue("org.synote.integration.ibmhts.server.username")
		String password = configurationService.getConfigValue("org.synote.integration.ibmhts.server.password")
		String enabled = configurationService.getConfigValue("org.synote.integration.ibmhts.enabled")
		String stopAddingJobDayOfWeek = configurationService.getConfigValue("org.synote.integration.ibmhts.server.stopAddingJob.dayOfWeek")
		String stopAddingJobTime = configurationService.getConfigValue("org.synote.integration.ibmhts.server.stopAddingJob.time")
		String downtimeDayOfWeek = configurationService.getConfigValue("org.synote.integration.ibmhts.server.downtime.dayOfWeek")
		String downtimeStartTime = configurationService.getConfigValue("org.synote.integration.ibmhts.server.downtime.startTime")
		String downtimeEndtime = configurationService.getConfigValue("org.synote.integration.ibmhts.server.downtime.endTime")
		String downtimeCheckInterval = configurationService.getConfigValue("org.synote.integration.ibmhts.server.downtime.checkInterval")
		String maxJob = configurationService.getConfigValue("org.synote.integration.ibmhts.user.maxJob")
		String downtimeAuto = configurationService.getConfigValue("org.synote.integration.ibmhts.server.downtime.auto")
		
		assertNotNull protocol  
		assertNotNull name 
		assertNotNull port 
		assertNotNull appPath 
		assertNotNull apiLogin
		assertNotNull apiLogout 
		assertNotNull apiAddJob 
		assertNotNull apiGetJobs 
		assertNotNull apiGetJob 
		assertNotNull apiGetTranscript 
		assertNotNull apiRemoveJob 
		assertNotNull apiSourceDir
		assertNotNull username 
		assertNotNull password 
		assertNotNull enabled 
		assertNotNull stopAddingJobDayOfWeek 
		assertNotNull stopAddingJobTime
		assertNotNull downtimeDayOfWeek 
		assertNotNull downtimeStartTime 
		assertNotNull downtimeEndtime
		assertNotNull downtimeCheckInterval
		assertNotNull maxJob
		assertNotNull downtimeAuto
	}
}

import org.synote.config.ConfigurationService
import org.synote.utils.DataSourceUtils
import grails.util.GrailsUtil

class BootStrap {

	def configurationService
	
    def init = { servletContext ->
    	
		log.info("BootStrap init...")
		
		//Change Datasoruce settings to avoid Broken pipeline prblem
		DataSourceUtils.tune(servletContext)
		
		//Disable IBM HTS service
		if(!GrailsUtil.environment.toLowerCase() != "test")
		{
			configurationService.setConfigValue("org.synote.integration.ibmhts.enabled","false")
			log.info("Set ibm hts disabled...")
		}
		//Stop search mirroring and using manual indexing
		//searchableService.stopMirroring()
	}
    
	def destroy = {
     
	}
} 
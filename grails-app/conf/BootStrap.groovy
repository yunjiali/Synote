import org.synote.config.ConfigurationService
import org.synote.linkeddata.LinkedDataService
import org.synote.utils.DataSourceUtils
import grails.util.GrailsUtil

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH

import com.hp.hpl.jena.sdb.Store
import com.hp.hpl.jena.sdb.StoreDesc
import com.hp.hpl.jena.sdb.util.StoreUtils
import com.hp.hpl.jena.sdb.SDBFactory
import com.hp.hpl.jena.rdf.model.Model


class BootStrap {

	def configurationService
	def linkedDataService
	
    def init = { servletContext ->
    	
		log.info("BootStrap init...")
		
		//Check triple store
		log.info "jena:"+CH.config.jena.enabled
		if(CH.config.jena.enabled)
		{
			//if in production mode, load another file
			Store store = SDBFactory.connectStore(linkedDataService.getAssemblerPath()) ;
			log.info("get triple store")
			if(CH.config.jena.sdb.checkFormattedOnStartUp)
			{
				if(!StoreUtils.isFormatted(store))
				{
					log.info("format store")
					store.getTableFormatter().create();
					//init the prefix mapping, so that we don't need to write them again
					linkedDataService.initPrefixMapping(store)
				}
			}
			
			if(CH.config.jena.sdb.emptyOnStartUp == true)
			{
				store.getTableFormatter().truncate()
				linkedDataService.initPrefixMapping(store)
			}
		}
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
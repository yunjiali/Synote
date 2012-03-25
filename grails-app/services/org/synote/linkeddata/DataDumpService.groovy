package org.synote.linkeddata

import org.synote.linkeddata.LinkedDataService
import org.synote.linkeddata.exception.RDFGenerationException
import org.synote.resource.Resource
import org.synote.user.User
import org.synote.user.UserRole
import java.io.FileOutputStream
import java.io.File
import org.apache.commons.logging.LogFactory

class DataDumpService {

	def linkedDataService
	def grailsApplication
	private static final log = LogFactory.getLog(this)
	
    static transactional = true

    /*
     * Dump all the rdf data into one file. We'd prefer to use a new thread to process this request
     */
	def dumpRDFToOneFile() 
	{
		log.info("Start dumping the rdf into one file...")
		def appVersion = grailsApplication.metadata['app.version']
		def appName = grailsApplication.metadata['app.name']
		def catalinaBase = System.properties.getProperty('catalina.base')
		def basePath = "${catalinaBase}/.grails/${appName}-${appVersion}/rdf/"
		if (!catalinaBase) 
			catalinaBase = '.'   // just in case
		//A list of ids
		def resourcesList = []
		def annotationsList = []
		def usersList = []
		
		def resources = Resource.list()
		resources.each{res->
			def rData = linkedDataService.getRedirectDataFromResource(res)
			if(rData!=null)
			{
				if(rData.recording?.perm?.val>0)
				{
					//dump the resource data
					if(!resourcesList.contains(res.id))
					{
						resourcesList << res.id
						try
						{
							File f = new File(basePath+"resources/${res.id}")
							if(!f.exists())
							{
								f.mkdirs()
								if(!f.createNewFile())
								{
									f.delete()
									f.createNewFile()
								}
							}
							def outputStream = new FileOutputStream(f)
							linkedDataService.buildResourceData(outputStream,res)
							outputStream.close()
						}
						catch(RDFGenerationException rdfEx)
						{
							log.debug("Error when generating RDF for resource ${res.id}")
							//Do nothing
						}
					}
					
					//dump annotation data
					if(rData.annotation && !annotationsList.contains(rData.annotation?.id))
					{
						def annotation = rData.annotation
						annotationsList << annotation.id
						try
						{
							File f = new File(basePath+"annotations/${annotation.id}")
							if(!f.exists())
							{
								f.mkdirs()
								if(!f.createNewFile())
								{
									f.delete()
									f.createNewFile()
								}
							}
							def outputStream = new FileOutputStream(f)
							linkedDataService.buildAnnotationData(outputStream,annotation)
							outputStream.close()
						}
						catch(RDFGenerationException rdfEx)
						{
							log.debug("Error when generating RDF for annotation ${annotation.id}")
							//Do nothing
						}
					}
				}
			}
		}
		
		//dump user data
		def normal_role = UserRole.findByAuthority("ROLE_NORMAL")
		def users = normal_role.people
		users.each{user->
			try
			{
				File f = new File(basePath+"users/${user.id}")
				if(!f.exists())
				{
					f.mkdirs()
					if(!f.createNewFile())
					{
						f.delete()
						f.createNewFile()
					}
				}
				def outputStream = new FileOutputStream(f)
				linkedDataService.buildUserData(outputStream,user)
				outputStream.close()
			}
			catch(RDFGenerationException rdfEx)
			{
				log.debug("Error when generating RDF for user ${user.id}")
				//Do nothing
			}	
		}
		log.info("Finish dumping data.")
    }
}

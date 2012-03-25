package org.synote.config

class ConfigurationService {

    boolean transactional = true

    def getConfigValue(String name) 
    {
    	def config = Configuration.findByName(name.trim())
		
		if(!config)
			return null
		
		return config?.val
	}
	
	def setConfigValue(String name, String value)
	{
		if(name)
		{
			def config = Configuration.findByName(name)
			if(config)
			{
				if(!value)
					value=""
					
				config.val=value
				if(!config.save(flush:true))
				{
					//TODO: throw some exceptions?	
				}	
			}	
		}
	}
}

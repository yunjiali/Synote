package org.synote.utils

import java.util.Locale

class MessageService {

    boolean transactional = true
    def grailsAttributes
	def messageSource
	
	public String getMessage(String code, Locale locale)
	{
		getMessage(code, null, locale)
	}
	
	public String getMessage(String code, Object[] args, Locale locale)
	{
		//println "locale:"+org.springframework.web.servlet.support.RequestContextUtils.getLocale(request)
		if(!messageSource)
		{
			messageSource = grailsAttributes.getApplicationContext().getBean("messageSource")
		}
		
		return messageSource.getMessage(code, args, locale)
	}
	public String getMessage(String code)
	{
		getMessage(code,null)
	}
}

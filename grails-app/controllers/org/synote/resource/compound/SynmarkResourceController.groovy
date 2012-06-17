package org.synote.resource.compound

import org.codehaus.groovy.grails.plugins.springsecurity.Secured
import org.synote.resource.ResourceService
import org.synote.permission.PermService

class SynmarkResourceController {

	def resourceService
	def permService
	
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
    def list = {
		
		def mmid = params.id
		if(!mmid)
		{
			flash.error = "Cannot find the reocording id."
			redirect(controller:'user',action:'index')
			return
		}
		def multimedia = MultimediaResource.get(mmid.toLong())
		if(!multimedia)
		{
			flash.error = "Cannot find the reocording id."
			redirect(controller:'user',action:'index')
			return
		}
		
		def perm = permService.getPerm(multimedia)
		if(perm?.val <=0)
		{
			flash.error = "Permission Denied!"
			redirect(controller:'user',action:'index')
			return
		}
		try
		{
			def synmarksList = resourceService.getSynmarksAsJSON(multimedia,params)
			return [synmarksList:synmarksList, params:params, multimedia:multimedia]
		}
		catch(org.hibernate.QueryException qex) //In case the query params not found
		{
			flash.error = qex.getMessage()
			redirect(action:'index')
			return
		}
	}
}

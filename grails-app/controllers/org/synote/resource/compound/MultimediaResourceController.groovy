package org.synote.resource.compound

import grails.converters.*
import org.xml.sax.SAXParseException
import org.synote.resource.Resource
import org.synote.resource.ResourceService
import org.synote.resource.single.text.MultimediaTag
import org.synote.resource.single.text.MultimediaTextNote
import org.synote.resource.single.binary.MultimediaUrl
import org.synote.resource.single.text.*
import org.synote.annotation.synpoint.Synpoint
import org.synote.annotation.ResourceAnnotation
import org.synote.permission.*
import org.synote.permission.exception.ResourcePermissionException
import org.synote.user.User
import org.synote.user.SecurityService
import org.synote.user.group.UserGroup
import org.synote.user.group.UserGroupMember
import org.synote.integration.viascribe.ViascribeService;
import org.synote.integration.viascribe.exception.ViascribeException
import org.synote.utils.RegExService
import org.synote.utils.DatabaseService
import org.synote.utils.MessageService
import org.synote.integration.ibmhts.IBMTransJobService
import org.synote.integration.ibmhts.IBMTransJob
import org.synote.integration.ibmhts.exception.IBMTransJobException
import org.synote.resource.exception.ResourceException
import org.synote.annotation.exception.AnnotationException
import org.synote.exception.SynoteException
import org.synote.integration.entermedia.EnterMediaService
import org.synote.api.APIStatusCode

import groovy.xml.MarkupBuilder


class MultimediaResourceController {

	def beforeInterceptor = [action: this.&auth, except: ['index', 'list', 'listMultimediaAjax','show']]
	//static Map allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']
	
	def securityService
	def permService
	def viascribeService
	def IBMTransJobService
	def regExService
	def databaseService
	def messageService
	def enterMediaService
	def resourceService
	
	private auth()
	{
		if(!securityService.isLoggedIn())
		{
			session.requestedController = controllerName
			session.requestedAction = actionName
			session.requestedParams = params
			
			flash.info = "Please login..."
			redirect(controller: 'login', action: 'auth')
			
			return false
		}
	}
	
	private void createSynmark(multimediaResource) throws ResourceException, AnnotationException
	{
		//create synmark
		def description = params.description
		def tags = params.tags
		
		if(!description && !tags)
			return
		
		def owner = securityService.getLoggedUser()
		
		if(owner)
		{
				
				def synmark = new SynmarkResource
				( owner: owner
				, title: params.title
				, note: new SynmarkTextNote(owner: owner, content: description))
				
				if(tags)
				{
					def synmarkTags = []
					synmarkTags = tags.split(',')
					synmarkTags.each {content ->
						if (content?.trim())
						{
							synmark.addToTags(new SynmarkTag(owner: owner, content: content.trim()))
						}
					}
				}
				
				if (!synmark.save(flush:true))
				{
					log.error "Error create Synmark ${synmark.properties.toString()}"
					throw new ResourceException("Cannot create Synmark")
				}
					
				//synmark.index()
			
				def annotation = new ResourceAnnotation(owner: owner, source: synmark, target: multimediaResource)
				
				def start = 0
				
				annotation.addToSynpoints(new Synpoint(targetStart: start))
				
				if (!annotation.save())
				{
					log.error "Error adding annotation to synmark id=${synmark.id}"
					throw new AnnotationException("Cannot create annotation")
				}		
		}
	}
	
	private getGroupList()
	{
		def owner = securityService.getLoggedUser()
		def groupList = UserGroup.findAllByOwnerOrShared(owner,true)
		
		return groupList as JSON
	}
	
	private String saveAjaxResponse(msg, stat, mmr)
	{
		log.debug "Start creating saveAjax call response..."
		def writer = new StringWriter()
		def xml = new MarkupBuilder(writer)
		xml.synote(action:"ajaxSave")
			{
				status(stat)
				description(msg)
				if(mmr != null)
				{
					multimedia(id:mmr.id)
					{
						title(mmr.title)
					}
				}
			}
		
		return writer.toString();
	}
	
	private saveResourcePermission(Resource multimediaResource) throws ResourcePermissionException
	{
		def userGroup = UserGroup.get(params.groupId)
		
		if (!userGroup)
		{
			throw new ResourcePermissionException("Group not found")
		}
		
		if (!securityService.isOwnerOrAdmin(userGroup.owner.id) && userGroup.shared == false)
		{
			throw new ResourcePermissionException("Permission denied - cannot add permission to the group")
		}
		
		def perm = PermissionValue.findByVal(params.groupPermission)
		def permission = new ResourcePermission(group: userGroup, resource: multimediaResource, perm: perm)
		
		if(permission.hasErrors() || !permission.save())
		{
			throw new ResourcePermissionException("Cannot save resource permission! You may have already give permission to this group. Please delete the permission and try again.")
		}
	}
	
	def index = {
		redirect(action: 'list', params: params)
		return
	}
	
	def list = {
		
		try
		{
			def multimediaList = resourceService.getMultimediaAsJSON(params) as Map
			def viewList = resourceService.getMostViewedMultimedia(5) as Map
			return [multimediaList:multimediaList, viewList:viewList, params:params]
		}
		catch(org.hibernate.QueryException qex) //In case the query params not found
		{
			flash.error = qex.getMessage()
			params.sidx = ''
			redirect(action:'list',params:params)
			return
		}
	}
	
	def show = {
		def multimediaResource = MultimediaResource.get(params.id)
		
		if (!multimediaResource)
		{
			flash.error = "Multimedia with id ${params.id} is not found"
			redirect(action: list)
			return
		}
		
		//User user = securityService.getLoggedUser()
		
		if (permService.getPerm(multimediaResource).val <= 0)//!multimediaResource.perm && !securityService.isOwnerOrAdmin(multimediaResource.owner.id))
		{
			flash.error = "Permission denied - cannot show multimedia with id ${params.id}"
			redirect(action: list)
			return
		}
		
		def resourcePermissionList = []
		
		if(securityService.isOwnerOrAdmin(multimediaResource.owner.id))
		{
			resourcePermissionList = ResourcePermission.findAllByResource(multimediaResource)
		}
		
		boolean isAllowedIPAddress = securityService.isAllowedIPAddress(request.remoteAddr)
		
		return [multimediaResource: multimediaResource, resourcePermissionList:resourcePermissionList, isAllowedIPAddress:isAllowedIPAddress,
				isIBMTransJobEnabled:IBMTransJobService.getConnected() && IBMTransJobService.getAllowAddingJobs()]
	}
	
	def create = {
		
		def multimediaResource = new MultimediaResource(params)
		boolean isAllowedIPAddress = securityService.isAllowedIPAddress(request.remoteAddr)
		return [multimediaResource: multimediaResource, isAllowedIPAddress:isAllowedIPAddress, 
				isIBMTransJobEnabled:IBMTransJobService.getConnected() && IBMTransJobService.getAllowAddingJobs()]
	}
	
	/**
	 * Ajax, add group permission
	 */
	def addGroupPermission = {
		//println "render"
		def groupList = getGroupList()
		render groupList
	}
	
	/**
	 * Ajax, delete group permission
	 */
	def deleteGroupPermission = {
		if(params.resourcePermissionId && params.id)
		{
			//println "resourcePermissionId:"+params.resourcePermissionId
			//println "id:"+params.id
			def multimediaResource = MultimediaResource.get(params.id)
			if(!multimediaResource)
			{
				flash.error = "Cannot find multimedia with id ${params.id}"
				redirect(action:"list")
				return
			}
			
			if(!securityService.isOwnerOrAdmin(multimediaResource?.owner?.id))
			{
				flash.error = "Permission denied. You are not allowed to do this operation."
				redirect(action:"show", id:params.id)
				return
			}
			def resourcePermission = ResourcePermission.get(params.resourcePermissionId)
			if(!resourcePermission)
			{
				flash.error = "Cannot delete the group permission"
				redirect(action:"show", id:params.id)
				return
			}
			else
			{
				resourcePermission.delete()
				flash.message="Permission has been deleted."
				redirect(action:"show", id:params.id)
				return
			}
		}
	}
	
	def save = {
		
		//MultimediaResource is the subclass of Resource, so the constraints don't work
		//We have to validate here manually
		
		def multimediaResource = new MultimediaResource()
		
		def title = params.title
		if (!title)
		{
			flash.error = "Title of the multimedia cannot be empty"
			render (view:'create', model:[multimediaResource: multimediaResource])
			return
		}
		multimediaResource.title = params?.title
		
		def url = params.url
		if (!url)
		{
			flash.error = "URL of the multimedia cannot be empty"
			render (view:'create', model:[multimediaResource: multimediaResource])
			return
		}
		
		url = url.replaceAll(" ", "%20")
		if(!regExService.isUrl(url))
		{
			flash.error = "The format of URL is not valid"
			render (view:'create', model:[multimediaResource: multimediaResource])
			return
		}
		def user = securityService.getLoggedUser()
		def multimediaUrl = new MultimediaUrl(url:url,owner:user)
		multimediaResource.url = multimediaUrl
		
		multimediaResource.owner = user
		
		def pv
		if(params?.perm)
			pv = PermissionValue.findByVal(params?.perm)

		else
			pv = PermissionValue.findByName("PRIVATE")

		multimediaResource.perm = pv
		Resource.withTransaction{status->
			try
			{
				if(multimediaResource.hasErrors() || !multimediaResource.save())
				{
					log.error("Cannot save multimedia resource.")
					throw new ResourceException("Cannot save new recording ${multimediaResource.title}.")
				}
				
				//multimediaResource.index()
				
				createSynmark(multimediaResource)
				
				if(params.groupId !=null && params.groupPermission != null)
				{
					saveResourcePermission(multimediaResource)
				}
				//println "UserLoggin:"+IBMTransJobService.getUserID()
				if(params.useIBMTrans && securityService.isAllowedIPAddress(request.remoteAddr) && IBMTransJobService.getIBMTransJobEnabled()== "true" && IBMTransJobService.getConnected() && IBMTransJobService.getAllowAddingJobs())
				{
					IBMTransJobService.addJob(multimediaResource, title, url.trim())
				}
				
				flash.message = "Multimedia ${multimediaResource.title} was successfully created"
				redirect(action: show, id: multimediaResource.id)
				return
			}
			catch(IBMTransJobException ibmex)
			{
				status.setRollbackOnly()
				flash.error= ibmex.getMessage()
				ibmex.printStackTrace()
				throw ibmex
			}
			catch(SynoteException syex)
			{
				status.setRollbackOnly()
				flash.error= syex.getMessage()
			}
			catch(java.net.ConnectException conex)
			{
				status.setRollbackOnly()
				IBMTransJobService.handleConnectException(conex)
				flash.error = conex.getMessage()
			}
			catch(java.io.IOException ioe)
			{
				status.setRollbackOnly()
				ioe.printStackTrace()
				log.error "java.io.IOException:"+ioe.getMessage()
				flash.error="The transcribing service couldn't return a valid response. Please double check if the media url is valid."
				return
			}
			catch(Exception e)
			{
				status.setRollbackOnly()
				flash.error= e.getMessage()
				e.printStackTrace()
				throw e
			}
			finally
			{
				render (view:'create', model:[multimediaResource:multimediaResource,isAllowedIPAddress:securityService.isAllowedIPAddress(request.remoteAddr),
					isIBMTransJobEnabled:IBMTransJobService.getConnected() && IBMTransJobService.getAllowAddingJobs()])
				return
			}
		}
		
	}
	
	/*Ajax call to create recording
	 *It's not part of the api, api needs login code
	 *<xml>
	 *<synote action="saveAjax">
	 *	<status>0</status>
	 *	<description>OK message</description>
	 *	<multimedia id="1234">
	 *		<title>test title</title>
	 *	</multimedia>
	 *</synote>
	 */
	def saveAjax = {
		
		//MultimediaResource is the subclass of Resource, so the constraints don't work
		//We have to validate here manually
		log.debug "Start saveAjax call..."
		String msg = ""
		def stat = APIStatusCode.SUCCESS
		
		def multimediaResource = new MultimediaResource()
		
		def rlocation = params.rlocation
		if(!rlocation)
		{
			msg = "Error: Please select the video or audio file source"
			stat = APIStatusCode.MM_CREATION_ERROR
			def xmlStr = saveAjaxResponse(msg,stat,null)
			render(text:xmlStr,contentType:"text/xml", encoding:"UTF-8")
			return
		}
		
		def title = params.title
		if (!title)
		{
			msg = "Error: Title of the multimedia cannot be empty"
			stat = APIStatusCode.MM_CREATION_ERROR
			def xmlStr = saveAjaxResponse(msg,stat,null)
			render(text:xmlStr,contentType:"text/xml", encoding:"UTF-8")
			return
		}
		multimediaResource.title = params?.title
		
		def url
		String url_error = ""
		
		if(rlocation == "internet")
		{
			url = params.internet_url
			url_error = "URL of the resource cannot be empty"
		}
		else if(rlocation == "youtube")
		{
			url = params.youtube_url
			url_error = "URL of the youtube video cannot be empty"
		}
		else if(rlocation == "local")
		{
			log.debug "start uploading files"
			def f = request.getFile("file")
			def filename = f.getOriginalFilename()
			File dest = new File(java.util.UUID.randomUUID().toString()+filename)
			f.transferTo(dest)
			
			//println "filename path:"+dest.getPath()
			//println "length:"+dest.length()
			def uploaded_url = enterMediaService.uploadFile(dest, title)
			if(!uploaded_url)
			{
				dest.delete()
				stat = APIStatusCode.MM_CREATION_ERROR
				msg = "Error: Cannot upload the file from local disk. Please try again later."
				def xmlStr = saveAjaxResponse(msg,stat,null)
				render(text:xmlStr,contentType:"text/xml", encoding:"UTF-8")
				return
			}
			url = uploaded_url
			dest.delete();
		}
		else if(rlocation=="syn_xml")
		{
			/*Yunjia: needs to be tested, maybe the "forward" function is not working*/
			forward(action:"saveViascribeAjax", params:params)
			return
		}
		
		
		if (!url)
		{
			msg = "Error:"+url_error
			stat = APIStatusCode.MM_CREATION_ERROR
			def xmlStr = saveAjaxResponse(msg,stat,null)
			render(text:xmlStr,contentType:"text/xml", encoding:"UTF-8")
			return
		}
		
		url = url.trim().replaceAll(" ", "%20")
		if(!regExService.isUrl(url))
		{
			msg = "Error: The format of URL is not valid"
			stat = APIStatusCode.MM_CREATION_ERROR
			def xmlStr = saveAjaxResponse(msg,stat,null)
			render(text:xmlStr,contentType:"text/xml", encoding:"UTF-8")
			return
		}
		def user = securityService.getLoggedUser()
		def multimediaUrl = new MultimediaUrl(url:url,owner:user)
		multimediaResource.url = multimediaUrl
		
		multimediaResource.owner = user
		
		def pv
		if(params?.perm)
			pv = PermissionValue.findByVal(params?.perm)

		else
			pv = PermissionValue.findByName("PRIVATE")

		multimediaResource.perm = pv
		Resource.withTransaction{status->
			try
			{
				if(multimediaResource.hasErrors() || !multimediaResource.save())
				{
					log.error("Cannot save multimedia resource.")
					msg = "Cannot save new recording ${multimediaResource.title}."
					stat = APIStatusCode.MM_CREATION_ERROR
					def xmlStr = saveAjaxResponse(msg,stat,null)
					render(text:xmlStr,contentType:"text/xml", encoding:"UTF-8")
					return
				}
				
				//multimediaResource.index()
				
				createSynmark(multimediaResource)
				
				//Yunjia: here is a bug, you cannot add multimple user group permissions
				//if(params.groupId !=null && params.groupPermission != null)
				//{
				//	saveResourcePermission(multimediaResource)
				//}
				//println "UserLoggin:"+IBMTransJobService.getUserID()
				if(params.useIBMTrans && securityService.isAllowedIPAddress(request.remoteAddr) && IBMTransJobService.getIBMTransJobEnabled()== "true" && IBMTransJobService.getConnected() && IBMTransJobService.getAllowAddingJobs())
				{
					IBMTransJobService.addJob(multimediaResource, title, url.trim())
				}
				
				msg = "Recording '${multimediaResource.title}' was successfully created"
				def xmlStr = saveAjaxResponse(msg,stat,multimediaResource)
				render(text:xmlStr,contentType:"text/xml", encoding:"UTF-8")
				return
			}
			catch(IBMTransJobException ibmex)
			{
				status.setRollbackOnly()
				msg = ibmex.getMessage()
				stat = APIStatusCode.MM_CREATION_ERROR
				ibmex.printStackTrace()
				def xmlStr = saveAjaxResponse(msg,stat,null)
				render(text:xmlStr,contentType:"text/xml", encoding:"UTF-8")
				return
			}
			catch(SynoteException syex)
			{
				status.setRollbackOnly()
				msg = syex.getMessage()
				stat = APIStatusCode.MM_CREATION_ERROR
				def xmlStr = saveAjaxResponse(msg,stat,null)
				render(text:xmlStr,contentType:"text/xml", encoding:"UTF-8")
				return
			}
			catch(java.net.ConnectException conex)
			{
				status.setRollbackOnly()
				IBMTransJobService.handleConnectException(conex)
				msg = conex.getMessage()
				stat = APIStatusCode.MM_CREATION_ERROR
				def xmlStr = saveAjaxResponse(msg,stat,null)
				render(text:xmlStr,contentType:"text/xml", encoding:"UTF-8")
				return
			}
			catch(java.io.IOException ioe)
			{
				status.setRollbackOnly()
				ioe.printStackTrace()
				log.error "java.io.IOException:"+ioe.getMessage()
				msg = "The transcribing service couldn't return a valid response. Please double check if the media url is valid."
				stat = APIStatusCode.MM_CREATION_ERROR
				def xmlStr = saveAjaxResponse(msg,stat,null)
				render(text:xmlStr,contentType:"text/xml", encoding:"UTF-8")
				return
			}
			catch(Exception e)
			{
				status.setRollbackOnly()
				msg = e.getMessage()
				stat = APIStatusCode.MM_CREATION_ERROR
				e.printStackTrace()
				def xmlStr = saveAjaxResponse(msg,stat,null)
				render(text:xmlStr,contentType:"text/xml", encoding:"UTF-8")
				return
			}
		}
		
	}
	
	def edit = {
		
		def multimediaResource = MultimediaResource.get(params.id)

		if (!multimediaResource)
		{
			flash.error = "Multimedia with id ${params.id} not found"
			redirect(action: list)
			return
		}
		if (!securityService.isOwnerOrAdmin(multimediaResource.owner.id))
		{
			flash.error = "Permission denied - cannot edit multimedia with id ${params.id}"
			redirect(action: list)
			return
		}
		
		return [multimedia: multimediaResource, ownerUserName: multimediaResource?.owner?.userName]
	}
	
	def update = {
		
		def multimediaResource = MultimediaResource.get(params.id?.toLong())
		
		if (!multimediaResource)
		{
			flash.error = "Multimedia with id ${params.id} not found"
			redirect(controller:'user',action:'listSynmarks')
			return
		}
		
		if (!securityService.isOwnerOrAdmin(multimediaResource.owner.id))
		{
			flash.error = "Permission denied - cannot update multimedia with id ${params.id}"
			redirect(controller:'user',action:'listSynmarks')
			return
		}
		
		def owner = multimediaResource.owner
		
		//MultimediaTextNote and multimediaTags will also been included.
		//Try multimediaResource.properties = params to see if it's still work
		multimediaResource.saveUrl(params.url)
		multimediaResource.title = params.title
		multimediaResource.perm = PermissionValue.findByVal(params.perm?.toString())
		
		//multimedia description and tags
		if(multimediaResource.note == null)
		{
			multimediaResource.note = new MultimediaTextNote(owner:owner, content:params.note)
		}
		else
		{
			multimediaResource.saveNote(params.note)	
		}
			
		//thumbnail pictures, duration and isVideo
		multimediaResource.thumbnail = params.thumbnail
		multimediaResource.isVideo = Boolean.valueOf(params.isVideo)
		multimediaResource.duration = Integer.valueOf(params.duration)
		
		Resource.withTransaction{status->
			
			def tags = params.tags?.split(",")
			if(multimediaResource.tags == null)
			{
				tags.each{t->
					if(t.trim()?.size() >0)
						multimediaResource.addToTags(new MultimediaTag(owner: owner,content:tag.trim()))
				}
			}
			else if(tags?.size()>0)//tags is an array of string
			{
				def removeTags = []
				multimediaResource.tags.each {mt ->
					!tags.find {t ->
						mt.content?.trim() && mt.content?.equalsIgnoreCase(t)
					}
				}.each {mt ->
					removeTags << mt
				}
				
				removeTags.each{rt->
					multimediaResource.removeFromTags(rt)
					rt.delete()
				}
				tags.findAll {t ->
					t.trim() && !multimediaResource.tags?.find {mt ->
						mt.content.equalsIgnoreCase(t)
					}
				}.each {t ->
					multimediaResource.addToTags(new MultimediaTag(owner: owner, content: t.trim()))
				}
			}
			
			try
			{
		
				if(multimediaResource.hasErrors() || !multimediaResource.save())
				{
					//multimediaResource.errors.allErrors.each {
				     //   println it
				    //}
					log.error "Cannot update multimedia ${multimediaResource.title}"
					throw new ResourceException("Cannot update multimediaResource ${multimediaResource.title}")
				}
				
				//multimediaResource.reindex()
				
				if(params.groupId != null && params.groupPermission != null)
				{
					saveResourcePermission(multimediaResource)
				}
				
				flash.message = "Multimedia ${multimediaResource.title} was successfuly updated"
				redirect(action:'edit',id: multimediaResource.id)
				return
			}
			catch(SynoteException syex)
			{
				status.setRollbackOnly()
				flash.error= syex.getMessage()
			}
			finally
			{
				render(view: 'edit', model: [multimedia: multimediaResource, ownerUserName: params.ownerUserName, params:params])
				return
			}
		}
	}
	
	def delete = {
		
		def multimediaResource = MultimediaResource.get(params.id)
		
		if (!multimediaResource)
		{
			flash.error = "Multimedia with id ${params.id} not found"
			redirect(controller:'user',action:'listSynmarks')
			return
		}
		
		if (!securityService.isOwnerOrAdmin(multimediaResource.owner.id))
		{
			flash.error = "Permission denied - cannot delete multimedia with id ${params.id}"
			redirect(controller:'user',action:'listSynmarks')
			return
		}
		
		def title = multimediaResource.title
		//multimediaResource.unindex()
		
		multimediaResource.delete()
		flash.message = "Multimedia ${title} was successfully deleted"
		//redirect to user resource list page
		redirect(controller:'user',action:'listSynmarks')
		return
	}
	
	/*deprecated  */
	def uploadViascribe ={
		//Do nothing
	}
	
	def saveViascribeAjax = {
		def user = securityService.getLoggedUser()
		
		String msg
		def stat = APIStatusCode.SUCCESS
		
		def title = params.title
		if (!title)
		{
			msg = "Title of Recording cannot be empty"
			stat = APIStatusCode.MM_CREATION_ERROR
			def xmlStr = saveAjaxResponse(msg,stat,multimediaResource)
			render(text:xmlStr,contentType:"text/xml", encoding:"UTF-8")
			return
		}
		
		def url = params.syn_xml_url
		if (!url)
		{
			msg = "URL of multimedia directory cannot be empty"
			stat = APIStatusCode.MM_CREATION_ERROR
			def xmlStr = saveAjaxResponse(msg,stat,multimediaResource)
			render(text:xmlStr,contentType:"text/xml", encoding:"UTF-8")
			return
		}
		
		url = url.trim().replaceAll(" ", "%20")
		//Hope this regular expression will not bring more problem...
		if(!regExService.isUrl(url))
		{
			msg = "The format of URL is not valid"
			stat = APIStatusCode.MM_CREATION_ERROR
			return false
		}
		
		//def filePath = params.file
		//println "filePath:"+filePath
		def file = request.getFile("syn_xml_file")
		if (file.empty)
		{
			msg = "ViaScribe XML file cannot be empty"
			stat = APIStatusCode.MM_CREATION_ERROR
			def xmlStr = saveAjaxResponse(msg,stat,multimediaResource)
			render(text:xmlStr,contentType:"text/xml", encoding:"UTF-8")
			return
		}
		
		//println "perm:"+params.perm
		def perm = null
		if(params.perm)
			perm = PermissionValue.findByVal(params.perm)
		
		Resource.withTransaction{status->
			try
			{
			
				def xml = new XmlSlurper().parseText(file.inputStream.text)
				def multimedia = viascribeService.upload(user, title, xml, url,perm)
				
				createSynmark(multimedia)
				
				if(params.groupId !=null && params.groupPermission != null)
				{
					saveResourcePermission(multimedia)
				}
				msg = "Synchronized xml data was successfully uploaded"
				stat = APIStatusCode.MM_CREATION_ERROR
				def xmlStr = saveAjaxResponse(msg,stat,multimedia)
				render(text:xmlStr,contentType:"text/xml", encoding:"UTF-8")
				return
			}
			catch (SAXParseException saxEx)
			{
				status.setRollbackOnly()
				log.error saxEx.getMessage()
				msg = "The format of Viascript XML file is not well formatted. Please check the content of the uploading file."
				stat = APIStatusCode.MM_CREATION_ERROR
				def xmlStr = saveAjaxResponse(msg,stat,null)
				render(text:xmlStr,contentType:"text/xml", encoding:"UTF-8")
				return
				
			}
			catch(SynoteException syex)
			{
				status.setRollbackOnly()
				log.error syex.getMessage()
				msg= syex.getMessage()
				stat = APIStatusCode.MM_CREATION_ERROR
				def xmlStr = saveAjaxResponse(msg,stat,null)
				render(text:xmlStr,contentType:"text/xml", encoding:"UTF-8")
				return
			}
		}
	}
	
	def saveViascribe = {
		
		def user = securityService.getLoggedUser()
		
		def title = params.title
		if (!title)
		{
			flash.error = "Title of Recording cannot be empty"
			redirect(action: 'uploadViascribe', params: params)
			return false
		}
		
		def url = params.syn_xml_url
		if (!url)
		{
			flash.error = "URL of multimedia directory cannot be empty"
			redirect(action: 'uploadViascribe', params: params)
			return false
		}
		
		url = url.trim().replaceAll(" ", "%20")
		//Hope this regular expression will not bring more problem...
		if(!regExService.isUrl(url))
		{
			flash.error = "The format of URL is not valid"
			redirect(action: 'uploadViascribe', params: params)
			return false
		}
		
		//def filePath = params.file
		//println "filePath:"+filePath
		def file = request.getFile("syn_xml_file")
		if (file.empty)
		{
			flash.error = "ViaScribe XML file cannot be empty"
			redirect(action: 'uploadViascribe', params: params)
			return false
		}
		
		//println "perm:"+params.perm
		def perm = null
		if(params.perm)
			perm = PermissionValue.findByVal(params.perm)
		
		Resource.withTransaction{status->
			try
			{
			
				def xml = new XmlSlurper().parseText(file.inputStream.text)
				def multimedia = viascribeService.upload(user, title, xml, url,perm)
				
				createSynmark(multimedia)
				
				if(params.groupId !=null && params.groupPermission != null)
				{
					saveResourcePermission(multimedia)
				}
				flash.message = "Synchronized xml data was successfully uploaded"
				redirect(action: 'show', id: multimedia.id)
				return
			
			}
			catch (SAXParseException saxEx)
			{
				status.setRollbackOnly()
				log.error saxEx.getMessage()
				flash.error = "The format of Viascript XML file is not well formatted. Please check the content of the uploading file."
				redirect(action:'uploadViascribe', params:params)
				return
				
			}
			catch(SynoteException syex)
			{
				status.setRollbackOnly()
				log.error syex.getMessage()
				flash.error= syex.getMessage()
				redirect(action:'uploadViascribe', params:params)
				return
			}
		}
	}
	
	def generateTranscript =
	{
		def multimediaResource = MultimediaResource.get(params.id)
		if(!multimediaResource)
		{
			flash.error = "Multimedia with id ${params.id} not found"
			redirect(action: list)
			return
		}
		
		def owner = securityService.getLoggedUser()
		if(!securityService.isOwnerOrAdmin(multimediaResource.owner.id))
		{
			flash.error = "You are not entitled to generate transcript for this recording."
			redirect(action: list)
			return
		}
		
		def jobList = IBMTransJob.findAllByResourceAndOwner(multimediaResource,owner)
		jobList.each { job->
			
			try
			{
				log.debug "I am going to delete the job ${job.jobId} as the same own has submitted another job for this resource"
				IBMTransJobService.removeJob(job.jobId)
				job.delete()
			}
			catch(java.net.ConnectException conex)
			{
				IBMTransJobService.handleConnectException(conex)
				flash.error = conex.getMessage()
				redirect(action:'show', id: multimediaResource.id)
				return
			}
			catch(Exception ex)
			{
				flash.error = ex.getMessage()
				redirect(action:'show', id: multimediaResource.id)
				return
			}
		}
		
		try
		{
			IBMTransJobService.addJob(multimediaResource, multimediaResource.title, multimediaResource.url.toString().trim())
			flash.message = "A new transcribing job has been successfully created."
		}
		catch(IBMTransJobException ibmex)
		{
			log.error ibmex.getMessage()+"::"+ibmex.getLocalizedMessage()
			ibmex.printStackTrace()
			flash.error= ibmex.getMessage()
		}
		catch(java.net.ConnectException conex)
		{
			IBMTransJobService.handleConnectException(conex)
			flash.error="Cannot connect to Transcribing service"
		}
		catch(java.io.IOException ioe)
		{
			ioe.printStackTrace()
			log.error "java.io.IOException:"+ioe.getMessage()
			flash.error="The transcribing service couldn't return a valid response. Please double check if the media url is valid."
		}
		catch(Exception ex)
		{
			log.error ex.getMessag()+"::"+ex.getLocalizedMessage()
			ex.getMessage()
			ex.printStackTrace()
			flash.error = ex.getMessage()
		}
		finally
		{
			redirect(action:'show',id:multimediaResource.id)
			return	
		}
	}

}

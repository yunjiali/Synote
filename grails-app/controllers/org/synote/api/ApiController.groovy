package org.synote.api

import grails.converters.*
import org.springframework.security.core.context.SecurityContextHolder as SCH
import org.springframework.security.authentication.BadCredentialsException
import org.synote.user.SecurityService
import org.synote.user.UserAPIKey
import org.synote.user.User
import org.synote.api.exception.SynoteAPIException
import java.util.UUID

import org.synote.resource.compound.*
import org.synote.player.server.PlayerService
import org.synote.resource.ResourceService
import org.synote.permission.PermService
import org.synote.utils.DatabaseService
import org.synote.permission.PermissionValue
import org.synote.annotation.ResourceAnnotation
import org.synote.resource.single.text.TagResource

import org.synote.player.client.MultimediaData
import org.synote.player.client.PlayerException
import org.synote.player.client.PresentationData
import org.synote.player.client.PresentationSlideData
import org.synote.player.client.ClientProfileEntry
import org.synote.player.client.SynmarkData
import org.synote.player.client.TranscriptData
import org.synote.player.client.TranscriptDataSimple
import org.synote.player.client.TranscriptItemData
import org.synote.player.client.TranscriptSynpoint
import org.synote.player.client.UserData

/**
 * 
 * @author Yunjia Li
 * The restful api for synote player.
 * The format parameter in every request is used to define which return format the request needs. 
 * Current support return format is xml, but we are planning to support more, such as json, RDF, etc.
 * 
 */
class ApiController {

	static allowedMethods = [login:'GET',getRecording:'GET',getSynmarkList:'GET',getMultimedia:'GET',getPresentatinList:'GET',
		getTranscriptList:'GET', getMultimediaList:'GET', signout:'GET']
	
	//def beforeInterceptor = [action:this.&auth, except:['error']]
	//def afterInterceptor = [action:this.&signout, except:['listAllTags']]
	def securityService
	def springSecurityService
	def playerService
	def permService
	def databaseService
	def resourceService
	
	/*
	 * Deprecated
	 */
	def auth(){
		def exception = session[APIStatusCode.API_AUTH_EXCEPTION]
		String format = request.getParameter("format")
		log.debug("session id:::::"+session.id)
		if(exception)
		{
			if(exception instanceof SynoteAPIException)
			{
				log.debug("SynoteAPIException:"+exception.getMessage())
				//println "api exception"
				redirect(action:'error', params:[stat:exception.getStatusCode(),desc:exception.getMessage(),format:format])
			}
			else if(exception instanceof BadCredentialsException)
			{
				log.debug("BadCredentialsException:"+exception.getMessage())
				//println "bad creditials"
				redirect(action:'error', params:[stat:APIStatusCode.AUTHENTICATION_FAILED,desc:exception.getMessage(),format:format])
			}
		}
		
		//if(!securityService.isLoggedIn())
		//{
		//	redirect(action:error, params:[stat:APIStatusCode.INTERNAL_ERROR,desc:"Internal error. Please try again.",format:format])
		//}
			
	}
	
	/*
	 * Deprecated
	 */
	/**
	 * sign out the user and clear the session
	 * 
	 * PARAMS:
	 * apiKey: the user api key, if not provided, the request will be treated as anonymous user
	 */
	def signout = {
		
		//TODO: we need to consider automatic session expire
		int stat = APIStatusCode.SUCCESS
		String desc = "Logout sucessful"
		String apiKey = request.getParameter("apiKey")
		
		if(!apiKey)
		{
			stat = APIStatusCode.API_KEY_NOT_FOUND
			desc = "The apiKey is missing in the request!"
		}
		else
		{
		
			//def user = securityService.getLoggedUser()
			def userAPIKey = UserAPIKey.findByUserKey(apiKey)
			if(!userAPIKey)
			{
				stat = APIStatusCode.API_KEY_NOT_FOUND
				desc = "Cannot find apiKey ${apiKey}"
			}
			else
			{
				userAPIKey.delete()
				//destroy the session
				SCH?.context?.authentication = null
				session.invalidate()
			}
		}
		
		String format = request.getParameter("format")
		//Default type is xml
		if(format && format.toLowerCase() == "json")
		{
			//We don't support json yet
		}
		else if(format && format.toLowerCase() == "rdf")
		{
			//We don't support RDF yet
		}
		else
		{
			render (contentType:"text/xml", encoding:"UTF-8")
			{
				synote(action:"signout")
				{
					status(stat)
					description(desc)
				}
			}
		}
		
		return
		
	}
	/*
	 * Deprecated
	 */
    /**
     * PARAMS:
     * userName:the login name
     * psw: the password
     * RETURN: an user api key which should be used in the following api request. It's not necessary to attach apikey when request resources,
     * but only read only resources will be returned.
     * 
     */
	def login = {
		
		int stat = APIStatusCode.SUCCESS
		String desc = "Authentication sucessful"
		String uuid = ""
		//Add record to userAPIKey table
		
		uuid = UUID.randomUUID().toString()
		def user = securityService.getLoggedUser()
		def userAPIKey = new UserAPIKey(user:user, userKey:uuid)
		if(!userAPIKey.save())
		{
			stat = APIStatusCode.INTERNAL_ERROR
			desc = "INTERNAL ERROR"
		}
		
		String format = request.getParameter("format")
		//Default type is xml
		if(format && format.toLowerCase() == "json")
		{
			//We don't support json yet	
		}
		else if(format && format.toLowerCase() == "rdf")
		{
			//We don't support RDF yet
		}
		else
		{
			render (contentType:"text/xml", encoding:"UTF-8")
			{
				synote(action:"login")
				{
					status(stat)
					description(desc)
					if(stat == APIStatusCode.SUCCESS)
					{
						user_api_key(uuid)	
					}		
				}
			}
		}
		return
	}
	
	/**
	 * Not implemented yet
	 * Get all the resources, including multimedia, transcript, synmarks and presentations related to a recording. The parameter mmid
	 * must be provided to find the multimedia resource. NOT IMPLEMENTED YET.
	 * 
	 * PARAMS:
	 * apiKey (optional): the user api key, if not provided, the request will be treated as anonymous user
	 * mmid: multimedia resource id
	 * 
	 */
	def getRecording = {
		
		String format = request.getParameter("format")
		def mmid = request.getParameter("mmid")
		def mmr = checkMultimediaResource(mmid, format)
		
		//see playerservice!
	}
	
	/**
	* Get get the list of multimedia.
	*
	* PARAMS:
	* apiKey (optional): the user api key, if not provided, the request will be treated as anonymous user
	* max: The total number of records you want to return. Default value is returning all the resources (Integer.MAX_VALUE)
	* offset: the offset the first record starts according to the sort field. Default value is 0. 
	* sort: The name of the field, which returned multimedia list is sorted by. Default value is 'id'. Other valid values include: 'title',
	* 'owner_name', 'public_perm_val' (the public permission for the resource) and 'user_perm_value' (the permission for the current user).
	* order: increasing or decreasing order for the sort field, either 'asc' or 'desc'. Default value is 'asc'.
	* 
	* Currently, the permission has 4 possible values:
	* PRIVATE: 0
	* READ: 100
	* ANNOTATE: 200
	* WRITE: 300
	*/
	def getMultimediaList = {
		try
		{
			def multimediaList = resourceService.getMultimediaAsJSON(params) as Map
			def viewList = resourceService.getMostViewedMultimedia(5) as Map
			//println multimediaList.total
			render resourceService.getMultimediaAsJSON(params) as JSON
			return
		}
		catch(org.hibernate.QueryException qex) //In case the query params not found
		{
			def desc = qex.getMessage()
			redirect(action:'error',params:[stat:APIStatusCode.INTERNAL_ERROR,desc:desc])
		}
	}
	
	/**
	* Get the multimedia resource and related attributes. The parameter mmid must be provided to find the multimedia resource.
	*
	* PARAMS:
	* multimediaId: multimedia resource id
	*/
	def getMultimedia = 
	{
		String format = request.getParameter("format")
		def mmid = request.getParameter("multimediaId")
		def mmr = checkMultimediaResource(mmid, format)
		if(!mmr)
			return
		
		def perm = permService.getPerm(mmr)
		if(perm.val >= PermissionValue.findByName("READ")?.val)
		{
			String tagsStr = ""
			if(mmr.tags != null && mmr.tags?.size() >0)
			{
				mmr.tags.each{tag->
					tagsStr+=tag?.content+","	
				}
				tagsStr = tagsStr.substring(0,tagsStr.size()-1)
			}
			
			int stat = APIStatusCode.SUCCESS
			String desc = "Successfully get multimedia resource."
			if(format && format.toLowerCase() == "xml")
			{
				render (contentType:"text/xml", encoding:"UTF-8")
				{
					synote(action:"getMultimedia")
					{
						status(stat)
						description(desc)
						multimedia(id:mmr.id)
						{
							owner(id:mmr.owner?.id, mmr.owner?.userName)
							title(mmr.title)
							url(mmr.url?.url)
							permission(perm.val.toString())
							canEdit(playerService.canEdit(mmr))
							canDelete(playerService.canDelete(mmr))
							canCreateTranscript(playerService.canCreateTranscript(mmr))
							canCreatePresentation(playerService.canCreatePresentation(mmr))
							canCreateSynmark(playerService.canCreateSynmark(mmr))
						}
					}
				}
				return
			}
			else if(format && format.toLowerCase() == "rdf")
			{
				//We don't support RDF yet
			}
			else
			{
				render (contentType:"text/json", encoding:"UTF-8")
				{
					'synote'(
						'action':'getMultimedia',
						'status':stat,
						'description':desc,
						'multimedia':[
							'id':mmr.id,
							'ownerid':mmr.owner?.id,
							'ownername':mmr.owner?.userName,
							'title':mmr.title,
							'url':mmr.url?.url,
							'tags':tagsStr,
							'note':mmr.note?.content
						]
					)
				}
			}
		}
		else
		{
			redirect(action:'error', params:[stat:APIStatusCode.MM_PERMISSION_DENIED,desc:"Permission denied. You are not entitled to see this recording",format:format])
		}
	}
	
	/**
	* Get all the synmarks related to a multimedia resource
	*
	* PARAMS:
	* multimediaId: multimedia resource id
	* 
	* Comments:
	* The start and end time for synmark is in 1/1000 second
	*/
	def getSynmarkList = {
		
		chain(controller:'recording',action:'getSynmarksAjax',params:params)	
	}
	
	/**
	 * Get a single synmark as json
	 * 
	 * PARAMS:
	 * synmarkId: synmark resource id
	 */
	def getSynmark = {
		def synmarkId = params.synmarkId
		if(!synmarkId)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SYNMARK_ID_MISSING, description:"Cannot find synmark id!")
			}
			return
		}
		def synmark = SynmarkResource.get(synmarkId.toLong())
		if(!synmark)
		{
			redirect(action:'error', params:[stat:APIStatusCode.SYNMARK_NOT_FOUND,desc:"Cannot find the synmark resource with id=${synmarkId}!"])
			return
		}
		
		def annotation = ResourceAnnotation.findBySource(synmark)
		def multimedia = annotation?.target
		if(!multimedia)
		{
			redirect(action:'error', params:[stat:APIStatusCode.MM_NOT_FOUND,desc:"Cannot find the multimedia resource!"])
			return
		}
		def perm = permService.getPerm(multimedia)
		if(perm?.val <=0)
		{
			redirect(action:'error', params:[stat:APIStatusCode.MM_PERMISSION_DENIED,desc:"Permission Denied!"])
			return
		}
		
		SynmarkData synmarkData = playerService.createSynmarkData(annotation)
		render synmark as JSON//encodeAsJSON()
		return
	}
	/**
	* Get all the presentation slides related to a multimedia resource
	*
	* PARAMS:
	* apiKey (optional): the user api key, if not provided, the request will be treated as anonymous user
	* multimediaId: multimedia resource id
	* 
	* Comments:
	* The start and end time for synpoints is in 1/1000 second
	*/
	def getPresentationList = {
		chain(controller:'recording',action:'getPresentationsAjax',params:params)
	}
	
	/**
	* Get all the transcripts related to a multimedia resource
	*
	* PARAMS:
	* apiKey (optional): the user api key, if not provided, the request will be treated as anonymous user
	* mmid: multimedia resource id
	*
	* Comments:
	* The start and end time for synpoints is in 1/1000 second
	*/
	def getTranscriptList = {
		chain(controller:'recording',action:'getTranscriptsAjax',params:params)
	}
	
	/**
	 * Return error json
	 */
	def error = {
		int stat = Integer.parseInt(params['stat'])
		//println "stat:"+stat
		String desc = params['desc']
		//String format = params['format']
		
		render (contentType:"text/json", encoding:"UTF-8")
		{
			'synote'(
					'status':stat,
					'description':desc
			)
			
		}
		return
	}
	
	private MultimediaResource checkMultimediaResource(String mmid,String format)
	{
		if(!mmid)
		{
			redirect(action:'error', params:[stat:APIStatusCode.MMID_MISSING,desc:"Parameter 'mmid' is not found.",format:format])
			return null
		}
		
		//get the multimedia resource first
		if(!MultimediaResource.exists(mmid))
		{
			redirect(action:'error', params:[stat:APIStatusCode.MM_NOT_FOUND,desc:"Cannot find multimedia resource with id ${mmid}.",format:format])
			return null
		}
		else
		{
			def mmr = MultimediaResource.get(mmid)
			return mmr
		}
	}
}

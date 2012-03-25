package org.synote.api

import grails.converters.*
import org.springframework.security.context.SecurityContextHolder as SCH
import org.springframework.security.BadCredentialsException
import org.synote.user.SecurityService
import org.synote.user.UserAPIKey
import org.synote.user.User
import org.synote.api.exception.SynoteAPIException
import java.util.UUID

import org.synote.resource.compound.*
import org.synote.player.server.PlayerService
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
	
	def beforeInterceptor = [action:this.&auth, except:['error']]
	//def afterInterceptor = [action:this.&signout, except:['listAllTags']]
	def securityService
	def authenticationService
	def playerService
	def permService
	def databaseService
	
	def auth(){
		def exception = session[APIStatusCode.API_AUTH_EXCEPTION]
		String format = request.getParameter("format")
		log.debug("session id:::::"+session.id)
		if(exception)
		{
			if(exception instanceof SynoteAPIException)
			{
				log.debug("SynoteAPIException:"+exception.getMessage())
				println "api exception"
				redirect(action:'error', params:[stat:exception.getStatusCode(),desc:exception.getMessage(),format:format])
			}
			else if(exception instanceof BadCredentialsException)
			{
				log.debug("BadCredentialsException:"+exception.getMessage())
				println "bad creditials"
				redirect(action:'error', params:[stat:APIStatusCode.AUTHENTICATION_FAILED,desc:exception.getMessage(),format:format])
			}
		}
		
		//if(!securityService.isLoggedIn())
		//{
		//	redirect(action:error, params:[stat:APIStatusCode.INTERNAL_ERROR,desc:"Internal error. Please try again.",format:format])
		//}
			
	}
	
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
		
		//The the number of record returned is not specified, we return all the records
		if(!params.max)
			params.max=Integer.MAX_VALUE
			
		String format = request.getParameter("format")
			
		def multimediaResourceList = databaseService.listMultimedia(params)
		int cnt = databaseService.countMultimediaList(params)
		
		int stat = APIStatusCode.SUCCESS
		String desc = "Successfully get multimedia list."
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
				synote(action:"getMultimediaList")
				{
					status(stat)
					description(desc)
					multimediaList(total:cnt)
					{
						multimediaResourceList.each{mr->
							multimedia(id:mr.id)
							{
								owner(id:mr.owner_id, mr.owner_name)
								title(mr.title)
								public_perm_val(mr.public_perm_val)
								if(securityService.isLoggedIn())
								{
									user_perm_val(mr.user_perm_val)
								}
							}	
						}	
					}
				}
			}
		}
		return
	}
	
	/**
	* Get the multimedia resource and related attributes. The parameter mmid must be provided to find the multimedia resource.
	*
	* PARAMS:
	* apiKey (optional): the user api key, if not provided, the request will be treated as anonymous user
	* mmid: multimedia resource id
	*/
	def getMultimedia = 
	{
		String format = request.getParameter("format")
		def mmid = request.getParameter("mmid")
		def mmr = checkMultimediaResource(mmid, format)
		if(!mmr)
			return
		
		def perm = permService.getPerm(mmr)
		if(perm.val >= PermissionValue.findByName("READ")?.val)
		{
			int stat = APIStatusCode.SUCCESS
			String desc = "Successfully get multimedia resource."
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
	* apiKey (optional): the user api key, if not provided, the request will be treated as anonymous user
	* mmid: multimedia resource id
	* 
	* Comments:
	* The start and end time for synmark is in 1/1000 second
	*/
	def getSynmarkList = {
		
		String format = request.getParameter("format")
		def mmid = request.getParameter("mmid")
		def mmr = checkMultimediaResource(mmid, format)
		if(!mmr)
			return
			
		def annotations = ResourceAnnotation.findAllByTarget(mmr)
		
		def synmarks = playerService.getSynmarks(mmr.id.toString())

		int stat = APIStatusCode.SUCCESS
		String desc = "Successfully get synmark list."
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
				synote(action:"getSynmarkList")
				{
					status(stat)
					description(desc)
					synmarkList()
					{
						synmarks.sort {it.getStart()}.each {smk ->
							synmark(id:smk.id)
							{
								owner(id:smk.owner?.getId(), smk.owner?.getFirstName() + " "+smk.owner?.getLastName())
								title(smk.getTitle())
								note(smk.getNote())
								tags()
								{
									smk.getTags().each{t->
										tag(t)
									}
								}
								nextSynmark(smk.getNext())
								start(smk.getStart())
								end(smk.getEnd())
								canEdit(smk.canEdit())
								canDelete(smk.canDelete())	
							}
						}
					}
				}
			}
		}
		return
	}
	/**
	* Get all the presentation slides related to a multimedia resource
	*
	* PARAMS:
	* apiKey (optional): the user api key, if not provided, the request will be treated as anonymous user
	* mmid: multimedia resource id
	* 
	* Comments:
	* The start and end time for synpoints is in 1/1000 second
	*/
	def getPresentationList = {
		String format = request.getParameter("format")
		def mmid = request.getParameter("mmid")
		def mmr = checkMultimediaResource(mmid, format)
		if(!mmr)
			return
		
		def annotations = ResourceAnnotation.findAllByTarget(mmr)
		
		def presentations = playerService.getPresentations(mmr.id.toString())
		int stat = APIStatusCode.SUCCESS
		String desc = "Successfully get presentations."
		
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
				synote(action:"getPresentations")
				{
					status(stat)
					description(desc)
					presentationList()
					{
						presentations.each{p->
							presentation(id:p.getId())
							{
								owner(id:p.owner?.getId(), p.owner?.getFirstName() + " "+p.owner?.getLastName())
								slides()
								{
									p.slides.each{s->
										slide(id:s.getId())
										{
											start(s.getStart())
											url(s.getUrl())
										}
									}
								}
								canEdit(p.canEdit())
								canDelete(p.canDelete())
							}
						}
					}
				}
			}
		}
		return
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
		String format = request.getParameter("format")
		def mmid = request.getParameter("mmid")
		def mmr = checkMultimediaResource(mmid, format)
		if(!mmr)
			return
			
		def annotations = ResourceAnnotation.findAllByTarget(mmr)
		
		def transcripts = playerService.getTranscripts(mmr.id.toString())
		int stat = APIStatusCode.SUCCESS
		String desc = "Successfully get presentations."
		
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
				synote(action:"getTranscripts")
				{
					status(stat)
					description(desc)
					transcriptList()
					{
						transcripts.each{t->
							transcript(id:t.getId()
								)
							{
								owner(id:t.owner?.getId(), t.owner?.getFirstName() + " "+t.owner?.getLastName())
								content(t.getText())
								canEdit(t.canEdit())
								canDelete(t.canDelete())
								synpoints()
								{
									t.getSynpoints().each{s->
										synpoint(startIndex:s.getStartIndex(), endIndex:s.getEndIndex(), startTime:s.getStartTime(), endTime:s.getEndTime())	
									}
								}
							}	
						}
					}
				}
			}
		}
		return
	}
	
	/**
	 * Return error xml or json
	 */
	def error = {
		int stat = Integer.parseInt(params['stat'])
		println "stat:"+stat
		String desc = params['desc']
		String format = params['format']
		
		if(format && format.toLowerCase() == "json")
		{
			//We don't support json yet
			return
		}
		else
		{
			render (contentType:"text/xml", encoding:"UTF-8")
			{
				synote()
				{
					status(stat)
					description(desc)
				}
				
			}
			return
		}
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

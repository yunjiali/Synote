package org.synote.player.server

import grails.converters.*
import org.synote.player.client.TimeFormat
import org.xml.sax.SAXParseException
import java.util.regex.Matcher
import java.util.regex.Pattern
import org.synote.user.User
import org.synote.resource.compound.*
import org.synote.user.SecurityService
import org.synote.permission.PermService
import org.synote.permission.PermissionValue
import org.synote.player.server.PlayerService
import org.synote.utils.RegExService
import org.synote.integration.viascribe.ViascribeService
import org.synote.resource.compound.WebVTTService
import org.synote.utils.UtilsService
import org.synote.user.profile.UserProfile
import org.synote.user.profile.ProfileEntry
import org.synote.annotation.ResourceAnnotation
import org.synote.utils.FileService
import org.synote.api.APIStatusCode
import org.synote.annotation.Annotation
import org.synote.resource.Resource
import org.synote.analysis.Views
import org.synote.linkeddata.LinkedDataService

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

import org.synote.player.client.WebVTTCueData
import org.synote.player.client.WebVTTData

import org.codehaus.groovy.grails.plugins.springsecurity.Secured

class RecordingController {

	//def beforeInterceptor = [action: this.&auth, except: ['help','print', 'handlePrint','getSynmarksAjax','getPresentationsAjax','getTranscriptsAjax',
	//	'downloadTranscriptAsSRTAjax']]
	//Yunjia:Add perm check for all the get, save and update programmes
	
	def securityService
	def printService
	def viascribeService
	def playerService
	def permService
	def regExService
	def fileService
	def webVTTService
	def linkedDataService
	def utilsService
	
	/*private auth()
	{
		if(!securityService.isLoggedIn())
		{
			if(params.isGuest?.toBoolean() == true && actionName == "replay")
			{
				return true	
			}
			session.requestedController = controllerName
			session.requestedAction = actionName
			session.requestedParams = params
			
			flash.message = "User login required to write or annotate."
			redirect(controller: 'login', action: 'auth', params:[multimediaId:params.id])
			
			return false
		}
	}*/
	
	def help = {
		//Do nothing	
	}
	//The replay page is only a representation of the multimedia resource, so the params.id must be a multimedia resource
	//the media fragment must already be given in the url
	def replay = {
		def recording = null
		def resource = Resource.findById(params.id?.toString())
		if(!resource || !resource.instanceOf(MultimediaResource))
		{
			flash.error = "Cannot find resource with ID ${params.id}"
			redirect(controller:'multimediaResource',action: 'list')
			return
		}
		recording = resource
		
		/*else
		{
			//find the multimedia
			if(resource.instanceOf(SynmarkResource))
			{
				def annotation = Annotation.findBySource(resource)
				recording = annotation?.target
				//redirect to replay with media fragment
				if(recording != null)
				{
					params.id = recording.id
					//redirect(action:"replay",params:params, model:[chain_resource:resource], fragment:)
				}
				else
				{
					flash.error = "Cannot find the recording corresponding to resource ${resource.id}"
					//Yunjia: should redirect to error page instead of list page
					redirect(controller:'multimediaResource',action: 'list')
					return
				}
			}
		} */
		
		if (recording)
		{
			def perm = permService.getPerm(recording)
			if(perm?.val <=0)
			{
				flash.error = "Access denied! You don't have permission to access this recording"
				//Yunjia: should redirect to error page instead of list page
				redirect(controller:'multimediaResource',action: 'list')
				return
			}
			
			def user = securityService.getLoggedUser()
			
			boolean canEdit = false
			boolean canCreateSynmark = false
			boolean isGuest = true
			if(user)
			{
				if(playerService.canEdit(recording))
					canEdit = true
				if(playerService.canCreateSynmark(recording))
					canCreateSynmark = true
			}
			//Yunjia: add ?type=synmark&id=synmarkid, then use the synmarkid to get the params.position
			
			//def mf_uri
			//if(!params.position)
			//{
				//We should put the position as part of the media fragment! Attach it at the end of the uri, ignore the original
				//hash part
				//mf_uri = recording.url.url+hash
				//if in the original url there is hash we need to
				//1. check if the position is not null, we attach the position fragment in front of other has fragments.
				//2. if the position is null, mf_uri = recording.url
				//see TC0055-UA http://www.w3.org/2008/WebVideo/Fragments/WD-media-fragments-impl/ only the last occurances counts
				//params.position = position
			//}
			//else if(!regExService.isPositionInteger(params.position)) //The position is not an integer
			//{
			//	params.position = 0
			//}
			
			def new_view = new Views(user:user,resource:recording)
			//println "#####################"
			//new_view.errors.each{err->
			//	println err
			//}
			if(!new_view.save())
			{
					//Do nothing currently	
			}
				/*if(!recording.equals(resource)) //The recording is opened by clicking through other resources such as synmark or transcript
				{
					def another_view = new Views(user:user,resource:resource)
					if(!another_view.save())
					{
						//Do nothing currently	
					}
				}*/
			def views = Views.countByResource(recording)
			return [recording: recording, user:user, canCreateSynmark:canCreateSynmark,canEdit:canEdit, userBaseURI:linkedDataService.getUserBaseURI(),
				resourceBaseURI:linkedDataService.getResourceBaseURI(), views:views]
		}
		else
		{
			flash.error = "Cannot find recording with ID ${params.id}"
			redirect(controller:'multimediaResource',action: 'list')
			return
		}
	}
	
	def getSynmarksAjax = {
		
		def multimediaId = params.multimediaId
		if(!multimediaId)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MMID_MISSING, description:"Cannot find multimedia id!")		
			}
			return
		}
		def multimedia = MultimediaResource.get(multimediaId.toLong())
		if(!multimedia)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MM_NOT_FOUND, description:"Cannot find the multimedia resource with id=${multimediaId}!")
			}
			return
		}
		
		SynmarkData[] synmarkList = playerService.getSynmarks(multimediaId)
		if(synmarkList == null || synmarkList?.size() == 0)
		{
			render ""
			return
		}
		render synmarkList.encodeAsJSON()
		return
	}
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def saveSynmarkAjax = {
		def multimediaId = params.multimedia_id
		if(!multimediaId)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MMID_MISSING, description:"Cannot find multimedia")
			}
			return
		}
		def multimedia = MultimediaResource.get(multimediaId.toLong())
		if(!multimedia)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MM_NOT_FOUND, description:"Cannot find the multimedia resource with id=${multimediaId}!")
			}
			return
		}
		if(!playerService.canCreateSynmark(multimedia))
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MM_PERMISSION_DENIED, description:"You cannot create synmark for multimedia resource with id=${multimediaId}!")
			}
			return
		}
		
		if(!params.synmark_st)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SYNMARK_ST_MISSING, description:"The start time for synmark is missing!")
			}
			return
		}
		
		//Yunjia: validate synmark_st and synmark_et first
		
		int start = TimeFormat.getInstance().parse(params.synmark_st.trim())
		Integer end = (params.synmark_et?.trim()?.size() > 0) ? TimeFormat.getInstance().parse(params.synmark_et?.trim()) : null;
		if (end != null && end!=0 &&end <= start)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SYNMARK_ET_INVALID, description:"The end time should not be smaller than the start time!")
			}
			return
		}
		
		String title = params.synmark_title.trim()
		String note = params.synmark_note.trim()
		String[] tags = params.synmark_tags.split(",")
		String nextSynmark = (params.synmark_next?.trim()?.size() > 0) ? params.synmark_next?.trim() : null
		
		SynmarkData synmarkData = new SynmarkData(start,end,title,note,tags,nextSynmark)
		try
		{
			playerService.createSynmark(multimediaId,synmarkData);
		}
		catch(PlayerException playerEx)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SYNMARK_UKNOWN_ERROR, description:playerEx.getMessage())
			}
			return
		}
		catch(Exception ex)
		{
			ex.printStackTrace()
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SYNMARK_UKNOWN_ERROR, description:ex.getMessage())
			}
			return
		}
		
		render(contentType:"text/json"){
				success(stat:APIStatusCode.SUCCESS, description:"Synmark has been successfully created.")
		}
		return
	}
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def updateSynmarkAjax = {
		
		if(!params.synmark_id)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SYNMARK_ID_MISSING, description:"Synmark id is missing!")
			}
			return
		}
		def synmarkId = params.synmark_id
		def synmark = SynmarkResource.get(synmarkId.toLong())
		if(!synmark)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SYNMARK_NOT_FOUND, description:"Cannot find the Synmark with id=${synmarkId}!")
			}
			return
		}
		def user = securityService.getLoggedUser()
		//println "user:"+user.id
		//println "owner:"+synmark.owner?.id
		if(user.id != synmark.owner.id)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SYNMARK_PERMISSION_DENIED, description:"You cannot update this synmark!")
			}
			return
		}
		
		if(!params.synmark_st)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SYNMARK_ST_MISSING, description:"The start time for synmark is missing!")
			}
			return
		}
		
		//Yunjia: validate synmark_st and synmark_et first
		
		int start = TimeFormat.getInstance().parse(params.synmark_st.trim())
		Integer end = (params.synmark_et?.trim()?.size() > 0) ? TimeFormat.getInstance().parse(params.synmark_et?.trim()) : null;
		if (end != null && end != 0 && end <= start)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SYNMARK_ET_INVALID, description:"The end time should not be smaller than the start time!")
			}
			return
		}
		
		String title = params.synmark_title.trim()
		String note = params.synmark_note.trim()
		String[] tags = params.synmark_tags.split(",")
		String nextSynmark = (params.synmark_next?.trim()?.size() > 0) ? params.synmark_next?.trim() : null
		
		SynmarkData synmarkData = new SynmarkData(start,end,title,note,tags,nextSynmark)
		
		try
		{
			playerService.editSynmark(synmarkId,synmarkData);
		}
		catch(PlayerException playerEx)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SYNMARK_UKNOWN_ERROR, description:playerEx.getMessage())
			}
			return
		}
		catch(Exception ex)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SYNMARK_UKNOWN_ERROR, description:ex.getMessage())
			}
			return
		}
		
		render(contentType:"text/json"){
				success(stat:APIStatusCode.SUCCESS, description:"Synmark has been successfully updated.")
		}
		return
	}
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def deleteSynmarkAjax = {
		if(!params.synmark_id)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SYNMARK_ID_MISSING, description:"Synmark id is missing!")
			}
			return
		}
		def synmarkId = params.synmark_id
		def synmark = SynmarkResource.get(synmarkId.toLong())
		if(!synmark)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SYNMARK_NOT_FOUND, description:"Cannot find the Synmark with id=${synmarkId}!")
			}
			return
		}
		def user = securityService.getLoggedUser()
		if(user.id != synmark.owner.id)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SYNMARK_PERMISSION_DENIED, description:"You cannot delete this synmark!")
			}
			return
		}
		try
		{
			playerService.deleteSynmark(synmarkId);
		}
		catch(PlayerException playerEx)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SYNMARK_UKNOWN_ERROR, description:playerEx.getMessage())
			}
			return
		}
		catch(Exception ex)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SYNMARK_UKNOWN_ERROR, description:ex.getMessage())
			}
			return
		}
		
		render(contentType:"text/json"){
				success(stat:APIStatusCode.SUCCESS, description:"Synmark has been successfully deleted.")
		}
		return
	}
	
	def getPresentationsAjax = {
		def multimediaId = params.multimediaId
		if(!multimediaId)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MMID_MISSING, description:"Cannot find multimedia id!")
			}
			return
		}
		def multimedia = MultimediaResource.get(multimediaId.toLong())
		if(!multimedia)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MM_NOT_FOUND, description:"Cannot find the multimedia resource with id=${multimediaId}!")
			}
			return
		}
		
		PresentationData[] slidesList = playerService.getPresentations(multimediaId)
		if(slidesList == null || slidesList?.size() == 0)
		{
			render ""
			return	
		}
		render slidesList.encodeAsJSON()
		return
	}
	/*
	 * params: multimedia_id, presentation_id nullable, new slide_index
	 */
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def saveSlideAjax = {
		
		def multimediaId = params.multimedia_id
		if(!multimediaId)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MMID_MISSING, description:"MultimediaId is missing")
			}
			return
		}
		def multimedia = MultimediaResource.get(multimediaId.toLong())
		if(!multimedia)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MM_NOT_FOUND, description:"Cannot find the multimedia resource with id=${multimediaId}!")
			}
			return
		}
		def presentation = null
		//if presentation_id doesn't exist, it means this is new presentation, but we need to check the annotation to see if there is no
		//presentation resource annotates this recording
		if(params.presentation_id)
		{
			def presentationId = params.presentation_id
			presentation = PresentationResource.get(presentationId.toLong())
			if(!presentation)
			{
				render(contentType:"text/json"){
					error(stat:APIStatusCode.PRESENTATION_NOT_FOUND, description:"Cannot find the presentation resource with id=${presentationId}!")
				}
				return
			}
		}
		else
		{
			def annotations = ResourceAnnotation.findAllByTarget(multimedia)
			def annotation = annotations.find{it.source?.instanceOf(PresentationResource)}
			if(annotation)
			{
				render(contentType:"text/json"){
					error(stat:APIStatusCode.PRESENTATION_ID_MISSING, description:"PresentationId is missing")
				}
				return
			}	
		}
		
		if(!playerService.canEdit(multimedia))
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MM_PERMISSION_DENIED, description:"You cannot edit presentation.")
			}
			return
		}
		
		if(!params.slide_index)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SLIDE_INDEX_MISSING, description:"Slide index is missing")
			}
			return
		}
		//slide_index starts from 1 not 0
		def slide_index = Integer.parseInt(params.slide_index)-1
		if(slide_index <0)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SLIDE_INDEX_INVALID, description:"Slide index is invalid.")
			}
			return
		}
		
		if(!params.slide_st)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SLIDE_ST_MISSING, description:"Slide start time is missing")
			}
			return
		}
		int start = TimeFormat.getInstance().parse(params.slide_st.trim())
		int end = null
		if(params.slide_et)
		{
			end = TimeFormat.getInstance().parse(params.slide_et.trim())
			if(start>end)
			{
				render(contentType:"text/json"){
					error(stat:APIStatusCode.SLIDE_ET_INVALID, description:"Start time must be smaller than end time.")
				}
				return
			}
		}
		if(!params.slide_url)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SLIDE_URL_MISSING, description:"Slide url is missing")
			}
			return
		}
		def slideData = new PresentationSlideData(null,start,end,params.slide_url)
		try
		{
			playerService.createSlide(multimedia, presentation, slide_index, slideData);
		}
		catch(PlayerException playerEx)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SLIDE_UNKNOWN_ERROR, description:playerEx.getMessage())
			}
			return
		}
		catch(Exception ex)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SLIDE_UNKNOWN_ERROR, description:ex.getMessage())
			}
			return
		}
		
		render(contentType:"text/json"){
				success(stat:APIStatusCode.SUCCESS, description:"Slide has been successfully added.")
		}
		return
	}
	
	/*
	 * params: multimedia_id, presentation_id, new_index, old_index
	 */
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def updateSlideAjax = {
		def multimediaId = params.multimedia_id
		if(!multimediaId)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MMID_MISSING, description:"MultimediaId is missing")
			}
			return
		}
		def multimedia = MultimediaResource.get(multimediaId.toLong())
		if(!multimedia)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MM_NOT_FOUND, description:"Cannot find the multimedia resource with id=${multimediaId}!")
			}
			return
		}
		
		//if presentation_id doesn't exist, it means this is new presentation, but we need to check the annotation to see if there is no
		//presentation resource annotates this recording
		if(!params.presentation_id)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.PRESENTATION_ID_MISSING, description:"PresentationId is missing")
			}
			return
		}
		def presentation = PresentationResource.get(params.presentation_id.toLong())
		if(!presentation)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.PRESENTATION_NOT_FOUND, description:"Cannot find the presentation resource with id=${presentationId}!")
			}
			return
		}
		
		if(!playerService.canEdit(multimedia))
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MM_PERMISSION_DENIED, description:"You cannot edit presentation.")
			}
			return
		}
		
		if(!params.slide_id)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SLIDE_ID_MISSING, description:"Slide id is missing")
			}
			return
		}
		
		if(!params.slide_index) //this is the new index
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SLIDE_INDEX_MISSING, description:"Slide index is missing")
			}
			return
		}
		//slide_index starts from 1 not 0
		def slide_index = Integer.parseInt(params.slide_index)-1
		if(slide_index <0)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SLIDE_INDEX_INVALID, description:"Slide index is invalid.")
			}
			return
		}
		
		if(!params.old_index)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SLIDE_OLD_INDEX_MISSING, description:"Old slide index is missing.")
			}
			return
		}
		def old_index = Integer.parseInt(params.old_index)-1
		
		if(!params.slide_st)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SLIDE_ST_MISSING, description:"Slide start time is missing")
			}
			return
		}
		int start = TimeFormat.getInstance().parse(params.slide_st.trim())
		int end = null
		if(params.slide_et)
		{
			end = TimeFormat.getInstance().parse(params.slide_et.trim())
			if(start>end)
			{
				render(contentType:"text/json"){
					error(stat:APIStatusCode.SLIDE_ET_INVALID, description:"Start time must be smaller than end time.")
				}
				return
			}
		}
		if(!params.slide_url)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SLIDE_URL_MISSING, description:"Slide url is missing")
			}
			return
		}
		def slideData = new PresentationSlideData(params.slide_id,start,end,params.slide_url)
		try
		{
			playerService.editSlide(multimedia, presentation, old_index, slide_index, slideData);
		}
		catch(PlayerException playerEx)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SLIDE_UNKNOWN_ERROR, description:playerEx.getMessage())
			}
			return
		}
		//catch(Exception ex)
		//{
		//	render(contentType:"text/json"){
		//		error(stat:APIStatusCode.SLIDE_UNKNOWN_ERROR, description:ex.getMessage())
		//	}
		//	return
		//}
		
		render(contentType:"text/json"){
				success(stat:APIStatusCode.SUCCESS, description:"Slide has been successfully updated.")
		}
		return
	}
	
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def deleteSlideAjax = {
		//multimedia id, presentation id, index
		def multimediaId = params.multimedia_id
		if(!multimediaId)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MMID_MISSING, description:"MultimediaId is missing")
			}
			return
		}
		def presentationId = params.presentation_id
		if(!presentationId)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.PRESENTATION_ID_MISSING, description:"PresentationId is missing")
			}
			return
		}
		def multimedia = MultimediaResource.get(multimediaId.toLong())
		if(!multimedia)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MM_NOT_FOUND, description:"Cannot find the multimedia resource with id=${multimediaId}!")
			}
			return
		}
		
		def presentation = PresentationResource.get(presentationId.toLong())
		if(!presentation)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.PRESENTATION_NOT_FOUND, description:"Cannot find the presentation resource with id=${presentationId}!")
			}
			return
		}
		if(!playerService.canEdit(multimedia))
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MM_PERMISSION_DENIED, description:"You cannot edit presentation.")
			}
			return
		}
		
		if(!params.slide_index)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SLIDE_INDEX_MISSING, description:"Slide index is missing")
			}
			return
		}
		def slide_index = Integer.parseInt(params.slide_index)-1
		try
		{
			playerService.deleteSlide(multimedia, presentation, slide_index);
		}
		catch(PlayerException playerEx)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SLIDE_UNKNOWN_ERROR, description:playerEx.getMessage())
			}
			return
		}
		catch(Exception ex)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.SLIDE_UNKNOWN_ERROR, description:ex.getMessage())
			}
			return
		}
		
		render(contentType:"text/json"){
				success(stat:APIStatusCode.SUCCESS, description:"Slide has been successfully deleted.")
		}
		return
	}
	
	
	/*
	 * Database => TranscriptData => TranscriptDataSimple=>SRT String => depends on type params, return SRT String or SRT JSON Object or TT
	 * SRT JSON objects are serialised from TranscriptItemSRT class
	 * The differences between SRT String and SRT JSON Objects are that start and endtime in SRT String are in hh:mm:ss,999 format,
	 * but in SRT JSON object, they are in miliseconds
	 */
	def getTranscriptsAjax={
		def multimediaId = params.multimediaId
		if(!multimediaId)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MMID_MISSING, description:"Cannot find multimedia id!")
			}
			return
		}
		def multimedia = MultimediaResource.get(multimediaId.toLong())
		if(!multimedia)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MM_NOT_FOUND, description:"Cannot find the multimedia resource with id=${multimediaId}!")
			}
			return
		}
		
		//Yunjia: we plan to return only the .srt or TimedText format for transcript.Both formats are easy to display
		//compared with the current solution. The default one is .srt
		def type = params.type
		if(!type)
			type = "srt"
			
		WebVTTData[] transList = webVTTService.getTranscripts(multimedia)
		if(transList == null || transList?.size() == 0)
		{
			render ""
			return
		}
		
		/*String responseStr 
		if(type == "srt")
		{
			log.debug "convert srt file."
			responseStr = playerService.convertToSRT(transList[0])
			render responseStr
			return
		}
		if(type=="json")
		{
			log.debug "convert to webvtt json"
			def srtList = playerService.convertToSRTObject(transList[0])
			render srtList.encodeAsJSON()
			return	
		}
		else
		{
			log.debug "convert tt file."
			responseStr = playerService.convertToTT(transList[0])
			render responseStr
			return
		}*/
		if(transList?.size() == 1)
		{
			def cuesList = transList[0].getCues()
			render cuesList.encodeAsJSON()
			return
		}
		else //Multi language is possible, but I won't consider now
		{
			def cuesList = transList[0].getCues()
			render cuesList.encodeAsJSON()
			return
		}
		 
	}
	
	def print = {
		//TODO: write webtest
		def recording = MultimediaResource.get(params.id)
		
		if (recording)
		{
			User user = securityService.getLoggedUser();
			if(permService.getPerm(recording)?.val < PermissionValue.findByName("READ")?.val)
			{
				flash.error = "Access denied! You don't have permission to access this multimedia"
				redirect(controller:'multimediaResource',action: 'list')
				return
			}
			
			def owners = []
			
			ResourceAnnotation.findAllByTarget(recording).each {annotation ->
				if (annotation.source instanceof SynmarkResource && !owners.contains(annotation.owner))
				owners << annotation.owner
			}
			
			return [recording: recording, owners: owners.sort {it.userName}]
		}
		else
		{
			flash.error = "Cannot find recording with ID ${params.id}"
			redirect(controller:'multimediaResource',action: 'list')
			return
		}
	}
	
	def handlePrint = {
		def recording = MultimediaResource.get(params.id)
		
		if (!recording)
		{
			flash.error = "Cannot find recording with ID ${params.id}"
			redirect(controller:'multimediaResource',action: 'list')
			return false
		}
		
		def perm = permService.getPerm(recording)
		if(perm?.val <=0)
		{
			flash.error = "Access denied! You don't have permission to access this recording"
			//Yunjia: should redirect to error page instead of list page
			redirect(controller:'multimediaResource',action: 'list')
			return
		}
		
		def from = null
		def to = null
		
		if (params.part)
		{
			try
			{
				from = params.from ? TimeFormat.getInstance().parse(params.from) : null
			}
			catch (NumberFormatException ex)
			{
				flash.error = "Check if From time is valid"
				redirect(action: 'print', params: params)
				return false
			}
			
			try
			{
				to = params.to ? TimeFormat.getInstance().parse(params.to) + 999 : null
			}
			catch (NumberFormatException ex)
			{
				flash.error = "Check if To time is valid"
				redirect(action: 'print', params: params)
				return false
			}
		}
		
		def synmarkedUsers = null
		if (params.synmarked)
		{
			synmarkedUsers = []
			params.each {param ->
				if (param.key.startsWith('synmarked-user-') && param.value)
				synmarkedUsers << param.key.substring(15, param.key.size()).toLong()
			}
		}
		
		def synmarksUsers = []
		if (params.synmarks)
		{
			params.each {param ->
				if (param.key.startsWith('synmarks-user-') && param.value)
				synmarksUsers << param.key.substring(14, param.key.size()).toLong()
			}
		}
		
		def synpoints = printService.getSynpoints(
		recording, from, to, synmarkedUsers, params.transcript == 'on', params.presentation == 'on', synmarksUsers)

		def ends = printService.getEnds(synpoints, to)
		
		def settings = [id: params.synmarkId, timing: params.synmarkTiming, title: params.synmarkTitle, note: params.synmarkNote,
		tags: params.synmarkTags, owner: params.synmarkOwner, next: params.synmarkNext]
		
		return [recording: recording, synpoints: synpoints, ends: ends, slideHeight: params.slideHeight, settings: settings]
	}
	
	/*
	 *Provide snapshot page for google search. Return all the trasncripts, synmarks and images related to the time interval
	 */
	def snapshot = {
		def recording = MultimediaResource.get(params.id?.toLong())
		
		if (!recording)
		{
			flash.error = "Cannot find recording with ID ${params.id}"
			redirect(controller:'multimediaResource',action: 'list')
			return false
		}
		
		def perm = permService.getPerm(recording)
		if(perm?.val <=0)
		{
			flash.error = "Access denied! You don't have permission to access this recording"
			redirect(controller:'multimediaResource',action: 'list')
			return
		}
		
		
		//after get the fragment information after _escaped_fragment_ param
		if(!params._escaped_fragment_ )
		{
			response.sendError(404)
			response.contentType="text/plain"
			response.outputStream.flush()
			return
		}
		
		def fragStr = params._escaped_fragment_
		def components = utilsService.parseTimeFragment(fragStr)
		
		def synpoints = printService.getAllSynpoints(recording, components.start, components.end)
		def ends = printService.getEnds(synpoints, components.to)
		
		//println "ends:"+synpoints?.size()
		boolean isVideo=true
		if(!utilsService.isVideo(recording.url?.url))
		{
			isVideo = false
		}
		
		String encodingFormat = utilsService.getEncodingFormat(recording.url?.url)
		
		def settings = [id: false, timing: true, title: params.true, note: true, tags: true, owner: true, next: false]
		return [recording: recording, synpoints: synpoints, ends: ends, slideHeight: "100%", settings: settings,
			canCreateSynmark:false,canEdit:false, userBaseURI:linkedDataService.getUserBaseURI(), resourceBaseURI:linkedDataService.getUserBaseURI(), 
			isVideo:isVideo, encodingFormat:encodingFormat]
	}
	
	//Retrieve the transcript from database and make .srt file and send it back to the server
	//Not used
	def downloadTranscriptAsSRT = {
		//Yunjia: check if the multimedia is private
		def multimediaId = params.multimediaId
		if(!multimediaId)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MMID_MISSING, description:"Cannot find multimedia id!")
			}
			return
		}
		def multimedia = MultimediaResource.get(multimediaId.toLong())
		if(!multimedia)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MM_NOT_FOUND, description:"Cannot find the multimedia resource with id=${multimediaId}!")
			}
			return
		}
		
		TranscriptData[] transList = playerService.getTranscripts(multimediaId)
		if(transList == null || transList?.size() == 0)
		{
			render ""
			return
		}
		
		String responseStr = playerService.convertToSRT(transList[0])
			
		response.setHeader("Content-disposition", "attachment;filename=transcript.srt")
		render(contentType:"text/plain", text:responseStr)
		return 		
		
	}
	
	//Retrieve the transcript from database and make .webvtt file and send it back to the server
	def downloadTranscriptAsWebVTT = {
		//Yunjia: check if the multimedia is private
		def multimediaId = params.multimediaId
		if(!multimediaId)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MMID_MISSING, description:"Cannot find multimedia id!")
			}
			return
		}
		def multimedia = MultimediaResource.get(multimediaId.toLong())
		if(!multimedia)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MM_NOT_FOUND, description:"Cannot find the multimedia resource with id=${multimediaId}!")
			}
			return
		}
		//Yunjia: if there are multiple transcripts...
		WebVTTData[] transcripts = webVTTService.getTranscripts(multimedia)
		//If there are multiple transcripts, we need to provide an id I think
		if(transcripts.size()==1) 
		{
			String responseStr = webVTTService.convertToWebVTT(transcripts[0])
				
			response.setHeader("Content-disposition", "attachment;filename=transcript.vtt")
			render(contentType:"text/vtt", text:responseStr)
			return
		}
		else if(transcripts.size() >1)
		{
			if(!params.transcriptId)
			{
				render(contentType:"text/json"){
					error(stat:APIStatusCode.TRANSCRIPT_ID_MISSING, 
						description:"There are multiple transcripts for this recording, please provide the id of the transcript you want to download")
				}
				return
			}
		}
		else //annnotations equals zero
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.TRANSCRIPT_NOT_FOUND, description:"Cannot find transcript for multimedia resource with id=${multimediaId}!")
			}
			return
		}
	}
	
	//Get the transcript draft
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def getTranscriptDraftAjax = {
		def multimediaId = params.multimediaId
		if(!multimediaId)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MMID_MISSING, description:"Cannot find multimedia id!")
			}
			return
		}
		def multimedia = MultimediaResource.get(multimediaId.toLong())
		if(!multimedia)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MM_NOT_FOUND, description:"Cannot find the multimedia resource with id=${multimediaId}!")
			}
			return
		}
		if(!playerService.canEdit(multimedia))
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.TRANSCRIPT_PERMISSION_DENIED, description:"You are not entitled to edit the transcript.")
			}
			return
		}
		def user = securityService.getLoggedUser()
		def file = webVTTService.getWebVTTDraft(user,multimediaId)
		//file doesn't exist
		if(!file)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.TRANSCRIPT_DRAFT_NOT_FOUND, description:"Draft is not found for this transcript!")
			}
			return
		}
		
		String vttStr = file.getText("utf-8")
		try
		{
			def cueList = webVTTService.convertToWebVTTObjectFromString(vttStr)
			//println "srtList size:"+srtList.encodeAsJSON()
			render cueList.encodeAsJSON()
			return
		}
		catch(PlayerException ex)
		{
			render(contentType:"text/json"){
				error(stat:ex.getStatusCode(), description:ex.getMessage())
			}
			return
		}
	}
	//the draft will be saved at temp/userid/transcript/multimediaId.vtt
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def saveTranscriptDraftAjax = {
		
		def multimediaId = params.multimediaId
		if(!multimediaId)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MMID_MISSING, description:"Cannot find multimedia id!")
			}
			return
		}
		def multimedia = MultimediaResource.get(multimediaId.toLong())
		if(!multimedia)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MM_NOT_FOUND, description:"Cannot find the multimedia resource with id=${multimediaId}!")
			}
			return
		}
		if(!playerService.canEdit(multimedia))
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.TRANSCRIPT_PERMISSION_DENIED, description:"You are not entitled to edit the transcript.")
			}
			return
		}
		
		if(!params.transcripts)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.TRANSCRIPT_TRANSCRIPTS_MISSING, description:"Cannot find updated transcripts")
			}
			return
		}
		
		def transcriptsJSON = JSON.parse(params.transcripts)
		if(webVTTService.validateWebVTTJSON(transcriptsJSON))
		{
			def webVTTStr = webVTTService.convertJSONToWebVTT(transcriptsJSON)
			def user = securityService.getLoggedUser()
			
			webVTTService.createWebVTTDraft(webVTTStr,user,multimediaId)
			
			render(contentType:"text/json"){
				success(stat:APIStatusCode.SUCCESS, description:"The transcript draft has been successfully saved.")
			}
			return
		}
		else
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.TRANSCRIPT_WEBVTT_JSON_INVALID, description:"The transcripts are not valid.")
			}
			return
		}
	}
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def saveTranscriptAjax = {
		def multimediaId = params.multimediaId
		if(!multimediaId)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MMID_MISSING, description:"Cannot find multimedia id!")
			}
			return
		}
		def multimedia = MultimediaResource.get(multimediaId.toLong())
		if(!multimedia)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MM_NOT_FOUND, description:"Cannot find the multimedia resource with id=${multimediaId}!")
			}
			return
		}
		
		if(!playerService.canEdit(multimedia))
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.TRANSCRIPT_PERMISSION_DENIED, description:"You are not entitled to edit the transcript.")
			}
			return
		}
		
		def user = securityService.getLoggedUser()
		
		if(!params.transcripts)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.TRANSCRIPT_TRANSCRIPTS_MISSING, description:"Cannot find updated transcripts")
			}
			return
		}
		def webVTTResource = webVTTService.getWebVTTResource(multimediaId,"")
		try
		{
			//if incoming transcript is empty and the transcriptResource exists in the database,
			//it means this transcript resource needs to be removed
			if(params.transcripts=="null" && webVTTResource)
			{
				//Delete the transcript
				webVTTService.deleteWebVTTResource(webVTTResource.id)
				render(contentType:"text/json"){
					success(stat:APIStatusCode.SUCCESS, description:"The transcript has been successfully deleted.")
				}
				return
			}
			
			def transcriptsJSON = JSON.parse(params.transcripts)
			if(transcriptsJSON.size() == 0 && webVTTResource) // there is transcript originally, but now there isn't, so we need to delete all of them
			{
				webVTTService.deleteWebVTTResource(webVTTResource.id)
				render(contentType:"text/json"){
					success(stat:APIStatusCode.SUCCESS, description:"The transcript has been successfully deleted.")
				}
				return
			}
			
			if(webVTTService.validateWebVTTJSON(transcriptsJSON))
			{	
				if(!webVTTResource)
				{
					//Create new Transcript
					webVTTService.createTranscriptFromJSON(multimedia,transcriptsJSON)
				}
				else
				{
					//update transcript
					webVTTService.editTranscriptFromJSON(multimedia,webVTTResource.id,transcriptsJSON)
					
				}
				//remove the saved draft if any
				webVTTService.deleteWebVTTDraft(user,multimediaId)
				
				render(contentType:"text/json"){
					success(stat:APIStatusCode.SUCCESS, description:"The transcript has been successfully saved.")
				}
				return
			}
			else
			{
				render(contentType:"text/json"){
					error(stat:APIStatusCode.TRANSCRIPT_WEBVTT_JSON_INVALID, description:"The transcripts are not valid.")
				}
				return
			}
		}
		catch(PlayerException ex)
		{
			render(contentType:"text/json"){
				error(stat:ex.getStatusCode(), description:ex.getMessage())
			}
			return
		}
		catch(Exception e)
		{
			render(contentType:"text/json"){
				error(stat:e.hashCode(),description:e.getMessage())
			}
			println "##########################"
			println e.cause.toString()
			e.printStackTrace()
			return
		}
	}
	
}

package org.synote.integration.nerd

import grails.plugins.springsecurity.Secured
import org.synote.resource.compound.*
import org.synote.resource.Resource
import org.synote.resource.single.text.TagResource
import org.synote.resource.single.text.TextNoteResource
import org.synote.resource.single.text.WebVTTCue
import org.synote.annotation.ResourceAnnotation
import org.synote.api.APIStatusCode
import org.synote.linkeddata.LinkedDataService
import org.synote.permission.PermService
import org.synote.resource.compound.WebVTTService
import org.synote.resource.ResourceService
import org.synote.user.SecurityService
import grails.converters.*
import org.synote.linkeddata.LinkedDataService
import org.synote.config.ConfigurationService
import org.synote.player.client.WebVTTData
import org.synote.player.client.PlayerException
import org.synote.annotation.synpoint.Synpoint


import java.util.UUID

import fr.eurecom.nerd.client.*
import fr.eurecom.nerd.client.type.*
import fr.eurecom.nerd.client.schema.*

class NerdController {
	
	def beforeInterceptor = [action: this.&checkNerdEnabled]
	
	def nerdService
	def permService
	def webVTTService
	def linkedDataService
	def resourceService
	def securityService
	def configurationService
	
	
	private checkNerdEnabled()
	{
		//def enabled = configurationService.getConfigValue("org.synote.integration.nerd.enabed")
		//if(enabled.toBoolean())
		//{
		//	return true
		//}
		//else
		//	return false
		return true
	}
	/*
	 * Extract named entity using nerd
	 * params: extractor, language (default "en"), text
	 */
	def extractAjax = {
		def text =""
		
		final String NERD_KEY = configurationService.getConfigValue("org.synote.integration.nerd.key")
		
		if(!params.resourceId)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.NERD_ID_MISSING, description:"Resource id is missing.")
			}
			return
		}
		
		def resource = Resource.get(params.resourceId?.toLong())
		if(!resource)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.NERD_RESOURCE_NOT_FOUND, description:"Cannot find the resource with id ${params.id}.")
			}
			return
		}
		
		if(!params.text)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.NERD_TEXT_NOT_FOUND, description:"Extraction text is missing.")
			}
			return
		}
		
		def synpoint = resourceService.getSynpointByResource(resource)
		def multimedia = resourceService.getMultimediaByResource(resource)
		if(!multimedia)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MM_NOT_FOUND, description:"Cannot find the multimedia resource.")
			}
			return
		}
		text =params.text
		
		if(!params.extractor)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.NERD_EXTRACTOR_NOT_FOUND, description:"Cannot find the extractor.")
			}
			return
		}
		
		def nerdExtractor = nerdService.getNerdExtractor(params.extractor)
		if(!nerdExtractor)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.NERD_EXTRACTOR_NOT_FOUND, description:"Cannot find the extractor with name ${params.extractor}.")
			}
			return
		}
		
		if(!text || text?.trim()?.size() ==0)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.NERD_TEXT_NOT_FOUND, description:"Extraction text cannot be empty.")
			}
			return	
		}
		
		//default language is English. We will add more language support later.
		if(!params.lang)
			params.lang = "en"
		try
		{
			
			//old uri:
			//NERD nerd = new NERD("http://semantics.eurecom.fr/nerdtest/api/",NERD_KEY)

			NERD nerd = new NERD(NERD_KEY)
			def result= nerd.annotateJSON(nerdExtractor,
                                   DocumentType.PLAINTEXT,
                                   text,
								   GranularityType.OEN,
								   30L); 	
			
			def jsObj = JSON.parse(result)
			
			//save json to triple store
			//println "before"
			def entities = nerdService.getEntityFromJSON(result)
			linkedDataService.saveNERDToTripleStroe(entities,multimedia,resource,synpoint,params.extractor)
			//println "afters"
			render JSON.parse(result) as JSON
			return
		}
		catch(Exception ex)
		{
			throw ex
			render(contentType:"text/json"){
				error(stat:APIStatusCode.NERD_EXTRACTOR_INTERAL_ERROR, description:"Connection failure to the extractor.")
			}
			return
		}
		
	}
	
	/*
	* Extract named entities from srt file using nerd
	*/
	def extractSRTAjax = {
		def text =""
		
		final String NERD_KEY = configurationService.getConfigValue("org.synote.integration.nerd.key")
		
		if(!params.resourceId)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.NERD_ID_MISSING, description:"Resource id is missing.")
			}
			return
		}
		
		def vtt = WebVTTResource.get(params.resourceId?.toLong())
		if(!vtt)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.NERD_RESOURCE_NOT_FOUND, description:"Cannot find the resource with id ${params.id}.")
			}
			return
		}
		
		def anno = ResourceAnnotation.findBySource(vtt)
		def multimedia = anno.target
		
		if(!anno)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.RESOURCEANNOTATION_NOT_FOUND, description:"Cannot find the annotation.")
				
			}
		}
		def transcript = webVTTService.getTranscriptFromAnnotation(anno, vtt)
		
		text = webVTTService.convertToSRT(transcript)
		
		if(!params.extractor)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.NERD_EXTRACTOR_NOT_FOUND, description:"Cannot find the extractor.")
			}
			return
		}
		
		def nerdExtractor = nerdService.getNerdExtractor(params.extractor)
		if(!nerdExtractor)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.NERD_EXTRACTOR_NOT_FOUND, description:"Cannot find the extractor with name ${params.extractor}.")
			}
			return
		}
		
		if(!text || text?.trim()?.size() ==0)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.NERD_TEXT_NOT_FOUND, description:"Extraction text cannot be empty.")
			}
			return
		}
		
		//TODO: add nerd language type
		if(!params.lang)
			params.lang = "en"
		
		try
		{
			
			NERD nerd = new NERD(NERD_KEY)
			def result= nerd.annotateJSON(nerdExtractor,
								   DocumentType.TIMEDTEXT,
								   text,
								   GranularityType.OEN,
								   30L);
			def jsObj = JSON.parse(result)
		   
			//save json to triple store
			//println "before"
			def entities = nerdService.getEntityFromJSON(result)
			//extractions.each{ex->
			//	println "startNPT:"+ex.getStartNPT()
			//	println "endNPT:"+ex.getEndNPT()
			//}
			//def synpoint = resourceService.getSynpointByResource(resource)
			//def multimedia = resourceService.getMultimediaByResource(resource)
		   
			def synpoints = Synpoint.findAllByAnnotation(anno)
			
			int countintriple = 0
			synpoints.each{syn->
				//TODO: if two synpoints have exactly the same start and end time
				def ents = entities.findAll{ ent->
					(((int)(ent.getStartNPT()*1000)) -syn.targetStart).abs() <=100 &&
					(((int)(ent.getEndNPT()*1000)) - syn.targetEnd).abs() <=100
				}
				//println "exts size:"+exts?.size()
				if(ents?.size() >0)
				{
					countintriple+=ents.size()
					def cue = WebVTTCue.findByCueIndexAndWebVTTFile(syn.sourceStart,vtt)
					linkedDataService.saveNERDToTripleStroe(ents,multimedia,cue,syn,params.extractor)
				}
			}
			   
			//println "££££££££££finalcounts:${countintriple}££££££££££"
			render JSON.parse(result) as JSON
			return
		}
		catch(Exception ex)
		{
			println ex.class
			println ex.getMessage()
			ex.printStackTrace()
			throw ex
			render(contentType:"text/json"){
				error(stat:APIStatusCode.NERD_EXTRACTOR_INTERAL_ERROR, description:"Connection failure to the extractor.")
			}
			return
		}
		
	}
	
	/*
	 * Save the review (accept or reject the named entity)
	 */
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def saveReviewAjax = {
		def user = securityService.getLoggedUser()
		if(!user)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.AUTHENTICATION_FAILED, description:"User login required.")
			}
			return
		}
		if(params.rating == null)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.LINKEDDATA_RATING_NOT_FOUND, description:"Rating data is missing.")
			}
			return
		}
		
		def rating = Integer.parseInt(params.rating)
		if(rating == null || rating<0 || rating >1)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.LINKEDDATA_RATING_INVALID, description:"Rating data is invalid.")
			}
			return
		}
		
		if(!params.idex)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.NERD_EXTRACTIONID_NOT_FOUND, description:"Extraction id not found.")
			}
			return
		}
		
		try
		{
			linkedDataService.saveReviewToTripleStore(rating, params.idex, user.id)
			render(contentType:"text/json"){
				success(stat:APIStatusCode.SUCCESS, description:"Review saved")
			}
			return
			
		}
		catch(Exception ex)
		{
			throw ex	
		}
	}
	
	/*
	 * DEPRECATED
	 * This method nerd tag, description, note as separate documents, which may not be efficient
	 * params: 
	 * resourceId: the id of the transcript
	 * extractor: the name of the extractor        
	 */
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])	
	def nerdit = {
		//if there is an id, we will use params.id
		//if no id, we are looking for params.fields
		if(!params.id && !params.fields)
		{
			flash.error = "Cannot find the resource."
			redirect(controller:'user',action:'index')
			return	
		}
		
		if(!params.extractor || params.list('extractor')?.size() ==0)
		{
			flash.error = "No extractor is selected."
			redirect(controller:'user',action:'index')
			return
		}
		
		def idList = []
		if(params.id)
		{
			idList << params.id.toLong()	
		}
		else if(params.fields)
		{
			
			params.list('fields').each{ f->	
				idList << f.toLong()
			}
			
		}
		
		if(idList.size() == 0)
		{
			flash.error = "No resource is indicated."
			redirect(controller:'user',action:'index')
			return
		}
		
		def resourceList = []
		def extractors = []
		
		for(int n=0;n<idList.size();n++)
		{
			def i = idList.get(n)
			def resource = Resource.get(i)
			if(!resource)
			{
				flash.error = "Cannot find the resource with id ${i}."
				redirect(controller:'user',action:'index')
				return
			}
			
			def textField = nerdService.getTextFromResource(resource)
			
			extractors = params.list('extractor')
			
			def item = [
					id: i,
					field:textField.field,
					text:textField.text,
				]
			
			resourceList << item
		}
		
		def results = [rows:resourceList,extractors:extractors] as Map
		return [resourceList:results]
	}
	
	/*
	 * This method is very similar to nerdit, but we treat the input as just one document
	 * It saves time because we don't need to send multiple documents related to the same media fragment,
	 * but the NIF start and end character won't be useful anymore because we have mixed up the documents
	 * 
	 * params.id is a MultimediaResource, SynmarkResource or WebVTTCue, which you can find a synpoint for
	 */
	def nerditone = {
		if(!params.id)
		{
			flash.error = "Cannot find the resource."
			redirect(controller:'user',action:'index')
			return
		}
		
		if(!params.fields)
		{
			flash.error = "No field is selected."
			redirect(controller:'user',action:'index')
			return
		}
		
		if(!params.extractor || params.list('extractor')?.size() ==0)
		{
			flash.error = "No extractor is selected."
			redirect(controller:'user',action:'index')
			return
		}
		
		def idList = []
		params.list('fields').each{ f->
			idList << f.toLong()
		}
		
		if(idList.size() == 0)
		{
			flash.error = "No resource is indicated."
			redirect(controller:'user',action:'index')
			return
		}
		
		//def resourceList = []
		def extractors = params.list('extractor')
		StringBuilder strBuilder = new StringBuilder()
		//String eol = System.getProperty("line.separator")
		for(int n=0;n<idList.size();n++)
		{
			def i = idList.get(n)
			def resource = Resource.get(i)
			if(!resource)
			{
				flash.error = "Cannot find the resource with id ${i}."
				redirect(controller:'user',action:'index')
				return
			}
			
			def textField = nerdService.getTextFromResource(resource)
			
			strBuilder.append(textField.text)
			strBuilder.append(",")
		}
		
		def results = [text:strBuilder.toString(),extractors:extractors] as Map
		return [textResource:results, resourceId:params.id]
	}
	
	def nerdmm = { 
		def multimediaResource 	= MultimediaResource.get(params.id)
		
		if(!multimediaResource)
		{
			flash.error = "Cannot find the recording"
			redirect(action:'index', controller:'user')
			return	
		}
		
		def perm = permService.getPerm(multimediaResource)
		if(perm?.val <=0)
		{
			flash.error = "Access denied! You don't have permission to access this recording"
			redirect(controller:'user',action: 'index')
			return
		}
		
		return [multimedia:multimediaResource]
	}
	
	/*
	 * Nerd synmark
	 */
	def nerdsmk={
		def synmarkResource = SynmarkResource.get(params.id)
		
		if(!synmarkResource)
		{
			flash.error = "Cannot find the synmark."
			redirect(action:'index', controller:'user')
			return
		}
		
		
		def anno = ResourceAnnotation.findBySource(synmarkResource)
		if(!anno || !anno.target)
		{
			flash.error = "Cannot find the annotation."
			redirect(action:'index', controller:'user')
			return
		}
		def perm = permService.getPerm(anno.target)
		if(perm?.val <=0)
		{
			flash.error = "Access denied! You don't have permission to access this synmark."
			redirect(controller:'user',action: 'index')
			return
		}
		
		return [synmark:synmarkResource]
	}
	
	/*
	 * Nerd WebVTT Cue
	 */
	def nerdcue = {
		def cue = WebVTTCue.get(params.id)
		if(!cue)
		{
			flash.error = "Cannot find the transcript block."
			redirect(action:'index', controller:'user')
			return
		}
		
		def anno = ResourceAnnotation.findBySource(cue.webVTTFile)
		
		if(!anno || !anno.target)
		{
			flash.error = "Cannot find the annotation."
			redirect(action:'index', controller:'user')
			return
		}
		def perm = permService.getPerm(anno.target)
		if(perm?.val <=0)
		{
			flash.error = "Access denied! You don't have permission to access this transcript block."
			redirect(controller:'user',action: 'index')
			return
		}
		
		def c = [
			id:cue.id,
			//owner_name:r.owner.userName, Don't need owner_name, it's you!
			text:webVTTService.getCueText(cue.content),
			speaker: webVTTService.getSpeaker(cue.content),
			settings: cue.getCueSettings(),
			//start: cue.getStart() !=null?TimeFormat.getInstance().toString(cue.getStart()):"unknown",
			//end:cue.getEnd() !=null?TimeFormat.getInstance().toString(cue.getEnd()):"unknown",
			//thumbnail:cue.getThumbnail()
		]
		return [cue:c]
	}
	
	/*
	 * Nerd the subtitle as srt file without asking users to choose extractors
	 * displaying the result similar to nerditone
	 */
	def nerditsub = {
		def multimedia = MultimediaResource.get(params.id)
		if(!multimedia)
		{
			flash.error = "Cannot find the recording."
			redirect(action:'index', controller:'user')
			return
		}
		
		def perm = permService.getPerm(multimedia)
		if(perm?.val <=0)
		{
			flash.error = "Access denied! You don't have permission to access this transcript block."
			redirect(controller:'user',action: 'index')
			return
		}
		
		WebVTTData[] transcripts= webVTTService.getTranscripts(multimedia)
	   
	    if(!transcripts)
	    {
			flash.error = "No transcript is found for this recording."
			redirect(controller:'user',action: 'index')
			return
	    }
		def transcript = transcripts[0]
	   
	    def srtStr = webVTTService.convertToSRT(transcript)
	
		def results = [text:srtStr,extractors:["combined"]] as Map
	
		return [textResource:results,resourceId:transcript.id, multimedia:multimedia]
		
	}
	
	/*
	 * list all the named entities about a resource
	 */
	@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
	def listne = {
		
		def user = securityService.getLoggedUser()
		if(!user)
		{
			flash.error = "User login required"
			redirect(action:'auth', controller:'login')
			return
		}
		
		if(!params.id)
		{
			flash.error = "Cannot find resource id."
			redirect(action:'index', controller:'user')
			return
		}
		
		//Here it must be a compouned resource,i.e. TranscriptResource, Synmark or MultimediaResource
		def resource = CompoundResource.get(params.id.toLong())
		if(!resource)
		{
			flash.error = "Cannot find the resource with id ${params.id}"
			redirect(action:'index', controller:'user')
			return
		}
		
		def jsObj = linkedDataService.getNEAsJSON(resource,user)
		def nes = [:]
		jsObj.each { i, data -> 
			if(i == "results")
			{
				ExtractorType.values().each{val->
					String extractor_name = nerdService.getNerdExtractorName(val)
					def bs = data.bindings.findAll{it.extr.value == extractor_name}
					nes.put(extractor_name, bs)	
				}
			}
		}
		
		//nes.opencalais.each{z->
		//	println "##################:"+z.rating?.value
		//}
		
		return [nes:nes,res:resource]
	}
}

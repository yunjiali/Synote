package org.synote.search.resource

import org.apache.lucene.search.BooleanQuery
import org.compass.core.engine.SearchEngineQueryParseException
import org.synote.search.resource.*
import org.synote.search.resource.exception.ResourceSearchException
import org.codehaus.groovy.grails.web.errors.GrailsWrappedRuntimeException

import org.synote.user.SecurityService
import org.synote.user.User
import org.synote.search.resource.ResourceSearchService
import org.synote.resource.compound.*
import org.synote.annotation.ResourceAnnotation
import org.synote.annotation.synpoint.Synpoint

import org.synote.search.resource.analysis.QueryRecord
import org.synote.search.resource.analysis.ResultRecord


class ResSearchController {

	def securityService
	def resourceSearchService
//	def emailService
	
	def index = {
		if (!params.query?.trim()) {
			flash.message = "Query cannot be empty!"
			redirect (action:"help")
			return
		}
		//println "max:"+params.max
		//println "offset:"+params.offset
		
		if(!params.max)
			params.max = ResourceSearchResult.MAX_RESULTS
		else
			params.max = Integer.parseInt(params.max)
		
		if(!params.offset)
			params.offset = 0
		else
			params.offset = Integer.parseInt(params.offset)
		
		
		try
		{
			def results = []
			def resourceSearchResult = new ResourceSearchResult(results:results, offset:params.offset, max:params.max)
			
			User user = securityService.getLoggedUser()
			
			if(!params.advanced)
				params.advanced = "false"
			
			//def search = resourceSearchService.buildQuery((Boolean.valueOf(params.advanced)).booleanValue(),params)
			
			resourceSearchService.searchResource(params,user,resourceSearchResult)
			
			resourceSearchResult.results = resourceSearchResult.results.sort{-it.score}
			
			return [resourceSearchResult:resourceSearchResult, params:params, parseException:null]
		}
		catch (SearchEngineQueryParseException seqpex) {
			flash.message = "Query syntax error!"
			redirect (action:"help")
			return
		}
		catch (ResourceSearchException rsex)
		{
			flash.message = rsex.message
			redirect (action:"help")
			return
		}
		catch (BooleanQuery.TooManyClauses bqex) {
			flash.message = "Too many results matched your query!"
			redirect (action:"help")
			return
		}
	}
	
	def locateMultimedia = {
		if(!params.id)
		{
			flash.error ="Resource id is missing!"
			log.error "Resource id is missing!"
			redirect (action:"help")
			return
		}
		MultimediaResource multimediaResource = MultimediaResource.get(params.id)
		
		redirect(controller:"recording",action:"replay", id:params.id)
	}
	
	//used to locate the position where to start playing the recording for synmark
	def locateSynmark = {
		
		if(!params.id)
		{
			flash.error ="Resource id is missing!"
			log.error "Resource id is missing!"
			redirect (action:"help")
			return
		}
		
		def synmarkResource = SynmarkResource.get(params.id)
		if(!synmarkResource)
		{
			flash.error = "Cannot find resource with id ${params.id}"
			log.error "Cannot find resource with id ${params.id}"
			redirect (action:"help")
			return
		}
		
		//if(synmarkResource instanceof SynmarkResource)
		//{
			def ra = ResourceAnnotation.findBySource(synmarkResource)
			if(!ra)
			{
				flash.error = "The annotation for synmark with id ${params.id} has broken!"
				log.error = "The annotation for synmark with id ${params.id} has broken!"
				redirect (action:"help")
				return
			}
			saveSelectedRecord(params,synmarkResource)
			
			def multimediaResource = ra.target
			def synpoints = ra.synpoints.toArray()
			if(!synpoints || synpoints.size() ==0)
			{
				flash.error = "The annotation for synmark with id ${params.id} has broken!"
				log.error = "The annotation for synmark with id ${params.id} has broken!"
				redirect (action:"help")
				return
			}
			if(synpoints.size() == 1)
			{
				def synpoint = synpoints[0]
				redirect(controller:"recording", action:"replay", params:[id:multimediaResource.id, position:synpoint.targetStart])
				return
			}
			else //I don't think it's possible but...
			{
				flash.error = "It's insane! More than one synmark"
				log.error = "It's insane! More than one synmark"
				redirect (action:"help")
				return
			}
		//}
		//else
		//{
		//	flash.error = "Resource with id ${params.id} is not a synmark resource."
		//	log.error "Resource with id ${params.id} is not a synmark resource."
		//	redirect (action:"help")
		//	return
		//}
	}
	
	def locateTranscript = {
		
		if(!params.id)
		{
			flash.error = "Transcript id is missing"
			log.error "Resource id is missing!"
			redirect(action:"help")
			return
		}
		
		if(!params.content)
		{
			flash.error = "Cannot find the content in transcript."
			log.error "Cannot find the content in transcript."
			redirect(action:"help")
			return
		}
		
		def transcriptResource = TranscriptResource.get(params.id)
		if(!transcriptResource)
		{
			flash.error = "No Transcript Resource with id ${params.id} is found!"
			log.error "No Transcript Resource with id ${params.id} is found!"
			redirect(action:"help")
			return
		}
		def ra = ResourceAnnotation.findBySource(transcriptResource)
		if(!ra)
		{
			flash.error = "The annotation for transcript with id ${params.id} has broken!"
			log.error "The annotation for transcript with id ${params.id} has broken!"
			redirect (action:"help")
			return
		}
		
		saveSelectedRecord(params,transcriptResource)
		
		def multimediaResource = ra.target
		//remove the <b></b>tag in content
		String text = resourceSearchService.removeHighlight(params.content)
		def index = transcriptResource.transcript.content.indexOf(text)
		
		//firstly find if there is any SourceStart is exactly index
		def sq = Synpoint.createCriteria()
		def synpoints = sq.list {
			eq("annotation.id", ra.id)
			ge("sourceStart",index)
			maxResults(1)
			order("sourceStart","asc")
		}
		
		if(synpoints.size()==0)
		{
			flash.error = "Cannot find the transcript clip!"
			log.error "Cannot find the transcript clip!"
			redirect(action:"help")
			return
		}
		def synpoint = synpoints[0]
		redirect(controller:"recording", action:"replay", params:[id:multimediaResource.id, position:synpoint.targetStart])
		
	}
	
	def advancedSearch = {
		//Currently, I think wildcard search is problematic. When you try INFO*, there will be
		//many empty transcript and synmark results. I am working on it.
		
		//iBMTransJobService.testVar()
		//Do nothing
	}
	
	def handleAdvancedSearch = {
		if( !params.all&&
		!params.exact&&
		!params.oneormore1&&
		!params.oneormore2&&
		!params.oneormore3)
		{
			flash.error ="You must at least input one word or phrase to search."
			render(view:'advancedSearch')
			return
		}
		
		if(!params.multimedia &&
		!params.synmark &&
		!params.transcript)
		{
			flash.error = "You must at least specify a resource"
			render(view:'advancedSearch')
			return
		}
		
		
		params.advanced = "true"
		params.query = resourceSearchService.queryStringBuilder(params)
		//println "query:"+params.query
		chain(action:"index", params:params)
		return
	}
	
	/*
	 * Currently, we can only search the title of multimedia. There's no other field
	 * to search in multimedia
	 */
	def selectMultimediaFields = {
		//Do nothing
	}
	
	/*
	 * Searchable fields are tags, note, title
	 */
	def selectSynmarkFields = {
		//println "checked:"+params.checked
		def synmarkSearchFields = SynmarkResource.getSearchableFields()
		render synmarkSearchFields
		return
	}
	
	/*
	 * Only the content of the transcript is searchable
	 */
	def selectTranscriptFields = {
		
	}
	
	def help = {
		// Do nothing.
	}
	
	private saveSelectedRecord(params,resource)
	{
		if(params.qr)
		{
			QueryRecord qr = QueryRecord.get(params.qr)
			ResultRecord rr = ResultRecord.findByResourceAndQuery(resource,qr)
			if(rr)
			{
				rr.selected = true
				if(!rr.save())
				{
					log.error("Cannot save result record ${rr.id}")
					return
				}
			}
			else
			{
				log.error("Cannot find result recrod for resource ${multimediaResource.id} and query ${qr.id}")
				return
			}
		}
		else
		{
			log.error "Cannot fine query record while locate multimedia ${params.id}"
			return
		}
	}
}

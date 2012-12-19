package org.synote.search.resource

import org.apache.lucene.search.BooleanQuery
import org.compass.core.engine.SearchEngineQueryParseException
import org.synote.search.resource.exception.ResourceSearchException
import org.codehaus.groovy.grails.web.errors.GrailsWrappedRuntimeException

import org.synote.resource.compound.MultimediaResource
import org.synote.resource.compound.SynmarkResource
import org.synote.resource.single.text.WebVTTCue

import org.synote.user.SecurityService
import org.synote.user.User
import org.synote.search.resource.ResourceSearchService
import org.synote.resource.ResourceService
import org.synote.resource.compound.*
import org.synote.annotation.ResourceAnnotation
import org.synote.annotation.synpoint.Synpoint
import org.synote.player.client.WebVTTCueData
import org.synote.analysis.Views

import org.synote.search.resource.analysis.QueryRecord
import org.synote.search.resource.analysis.ResultRecord


class ResSearchController {

	def securityService
	def resourceSearchService
	def searchableService
	def resourceService
	
	private static String MAX_RESULTS = 10
	
	def index = {
		if (!params.query?.trim()) {
			flash.message = "Query cannot be empty!"
			redirect (action:"help")
			return
		}
		
		def searchParams = [:]
		//println "max:"+params.max
		//println "offset:"+params.offset
		if(!params.type)
			params.type = "all"
		if(!params.rows)
			params.rows = MAX_RESULTS
			
		def maxRows = Integer.valueOf(params.rows)
		if(!params.page)
			params.page ="1"
			
		def currentPage = Integer.valueOf(params.page) ?: 1
		def rowOffset = currentPage == 1 ? 0 : (currentPage - 1) * maxRows
		
		searchParams.offset = rowOffset
		searchParams.max = maxRows
		
		def user=securityService.getLoggedUser()

		try
		{
			def searchResult
			if(params.type == 'all')
			{
				searchResult = searchableService.search(params.query,searchParams)
			}
			else if(params.type == 'multimedia')
			{
				searchResult = MultimediaResource.search(params.query,searchParams)
			}
			else if(params.type == 'synmark')
			{
				searchResult = SynmarkResource.search(params.query,searchParams)
			}
			else if(params.type == 'transcript')
			{
				searchResult = WebVTTCue.search(params.query,searchParams)
			}
			else
			{
				searchResult = searchableService.search(params.query,searchParams)
			}
			
			//for(int i=0;i<searchResult.results.size();i++)
			//{
			//	println "class:"+searchResult.results[i].class
			//	println "score:"+searchResult.scores[i]	
			//}
			def totalRows = searchResult.total
			def numberOfPages = Math.ceil(totalRows/maxRows)
			def results = []
			
			//TODO: ADD search log to database
			searchResult.results.each{result->
				if(result.instanceOf(MultimediaResource))
				{
					def multimedia = MultimediaResource.get(result.id)
					results << resourceService.buildMultimediaJSON(multimedia, securityService.isLoggedIn(), multimedia.perm, multimedia.perm)
				}
				else if(result.instanceOf(SynmarkResource))
				{
					def sr = SynmarkResource.get(result.id)
					def annotation = ResourceAnnotation.findBySource(sr)
					def mr = annotation?.target
					if(mr && annotation)
					{
						if(annotation.synpoints?.size()>0)
						{
							def synpoint = annotation.synpoints.toArray()[0]
							
							results << resourceService.buildSynmarkJSON(sr,mr,synpoint)
						}
						else
						{
							def syn = SynmarkResource.get(result.id)
							if(syn != null)
								syn.delete()	
						}
					}
					else
					{
						def syn = SynmarkResource.get(result.id)
						if(syn != null)
							syn.delete()	
					}
				}
				else if(result.instanceOf(WebVTTCue))
				{
					def cue = WebVTTCue.get(result.id)
					def vtt = cue.webVTTFile
					def annotation = ResourceAnnotation.findBySource(vtt)
					def mr = annotation?.target
					if(mr && annotation)
					{
						def synpoint = annotation.synpoints.find{cue.cueIndex==it.sourceStart}
						if(synpoint)
						{
							def cueData = new WebVTTCueData
								(cue.id.toString(), cue.cueIndex, synpoint.targetStart, synpoint.targetEnd, cue.content,cue.cueSettings,cue.thumbnail)
							results << resourceService.buildCueJSON(cueData, mr)
							
						}
						else
						{
							vtt.delete()
							throw new ResourceSearchException("Error occurs during the search. Please try it again.")	
						}
					}
					else
					{
						vtt.delete()
						throw new ResourceSearchException("Error occurs during the search. Please try it again.")	
					}
				}
				else //there shouldn't be any else I think
				{
					//Do nothing
				}	
			}
			
			def searchResultList = [rows:results, page:currentPage, records:totalRows, total:numberOfPages]
			return [searchResultList:searchResultList, params:params]
		}
		catch (SearchEngineQueryParseException seqpex) {
			flash.error = "Query syntax error!"
			render (view:"index")
			return
		}
		catch (ResourceSearchException rsex)
		{
			flash.error = rsex.message
			render (view:"index")
			return
		}
		catch (BooleanQuery.TooManyClauses bqex) {
			flash.error = "Too many results matched your query!"
			render(view:"help")
			return
		}
	}
}

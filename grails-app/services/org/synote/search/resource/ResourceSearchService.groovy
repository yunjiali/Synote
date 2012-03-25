package org.synote.search.resource

import org.synote.search.resource.*
import org.synote.search.resource.exception.ResourceSearchException
import org.apache.log4j.Logger
import java.util.LinkedHashMap
import org.compass.core.CompassHighlighter

import org.synote.permission.PermService
import org.synote.user.User
import org.synote.user.SecurityService
import org.synote.utils.RegExService
import org.synote.resource.compound.*
import org.synote.resource.Resource
import org.synote.permission.PermissionValue
import org.synote.annotation.ResourceAnnotation
import org.synote.config.ConfigurationService

import org.synote.search.resource.analysis.QueryRecord
import org.synote.search.resource.analysis.ResultRecord
import org.synote.search.resource.analysis.PageSelectionRecord

import org.springframework.web.context.request.RequestContextHolder as RCH

class ResourceSearchService {

	boolean transactional = true
	
	def searchableService
	def permService
	def securityService
	def regExService
	def configurationService
	
	private Logger log = Logger.getLogger(getClass());
	
	/*
	 * Sturcture of search result:
	 * "results"= many "resultItem"
	 * "resultItem" = "the id of multimedia resource" + "Recording" + "Score"
	 * "Recording" = only one "MultimediaResource"+many "SynmarkResource" (a list) + only one "TranscriptResoruce"
	 *
	 * According to the function of search, wherever the hit(s) of the search, the result link will open the synote
	 * player and play from a certain position. In the search, a hit may lie in the title of multimediaResource, in any field in SynmarkResource
	 * and anywhere in TranscriptResource. One multimediaResoruce can have many SynmarkResources.
	 *
	 * The "Score" field will add or the score for each Resource together to provide the ordering
	 * of the search result set.
	 */
	
	
	//Test to add a synmark search result when:
	//That kind of resource means something like "SynmarkResoruce"
	//1. resultItem is null
	//2. resultItem is not null, but that kind of resource list is null
	//3. resultItem is not null, that kind of resource list is null
	
	//The search result can return an instance of a domain class (Resource class)
	//in this case. But some of the fields, which have "belongs to" relationship with
	//another domain class, are empty. For example, the owner property of resource
	//sometimes is null. But result.id is still the same as resource.id. So we still
	//have to use Resource.get(result.id) to find the "solid" resource.
	def searchResource(params,user,resourceSearchResult)
	{
		def results = resourceSearchResult.results
		if(!results)
		{
			//Do nothing
		}
		
		boolean sameQuery = false
		QueryRecord qr = null
		if(!params.qr)
		 	qr= new QueryRecord()
		else
		{
			qr = QueryRecord.get(params.qr)
			sameQuery = true
		}
		
		if(!sameQuery)
		{
			qr.sessionId = RCH.currentRequestAttributes().getSessionId()
			qr.user=securityService.getLoggedUser()
			
			params.query = formalizeQuery(params.query)
			qr.queryString = params.query
			if(params.advanced.toLowerCase() != "false")
				qr.advanced = true
			if(params.res == "2")
				qr.mineOnly = true	
		}
		
		//Create PageSelectionRecord
		int offset = params.offset
		int max = params.max
		PageSelectionRecord psr = new PageSelectionRecord()
		psr.sessionId = qr.sessionId
		psr.user = qr.user
		psr.offset = offset
		psr.max = max
		qr.addToPages(psr)
			
		def searchResult = getOriginalSearchResult(params,qr, sameQuery)
		
		if(searchResult.results)
		{
			qr.resultNum = searchResult.results.size()
			
			int j=0
			
			for(i in 0..<searchResult.results.size())
			{
				def result = searchResult.results[i]
				def resource = Resource.get(result?.id)

				//The resource is null sometimes
				if(!resource)
				{
					//TODO: we need to print out more result information
					log.warn("Null resource, find id ${result?.id}:")
					continue
				}
				
				def highlight = searchResult.highlights[i]
				
				highlight = removeEmptyHighlightItems(highlight)
				if(highlight.size() == 0)
					continue
				
				
				def targetResource
				def perm
				
				//Search mine only
				if(params.res == "2")
				{
					try
					{
						if(resource.owner.id != user.id)
						{
							continue
						}
					}
					catch(NullPointerException npex)
					{
						resource.reindex()
						continue
					}
				}
				
				//TODO: implement a better solution to evluate score
				float score = ScoreCaculator.getScoreFromResource(searchResult.scores[i], resource)
				//println "score:"+score
				//Get multimedia resource
				if(resource instanceof MultimediaResource)
				{
					targetResource = resource
				}
				else
				{
					targetResource = getTargetResource(resource)
					//Sometimes there might be inconsistancy in database and the
					//returned resource can be null. In this case, we need to ignore
					//this resource
					if(!targetResource)
					{
						continue
					}
				}
				
				perm = permService.getPerm(targetResource)
				if(perm?.val >= PermissionValue.findByName("READ")?.val)
				{
					if(!sameQuery)
					{
						qr.allowedNum++
						ResultRecord rr = new ResultRecord()
						rr.sessionId = qr.sessionId
						rr.user = qr.user
						rr.resource = resource
						rr.resourceClass= resource.getClass().getName()
						rr.content = highlight.toString()
						qr.addToResults(rr)
					}
					
					def resultItem = results.find {it.id == targetResource.id}
					String alias = resource.getResourceAlias()
					//Create a new result item
					if(resultItem == null)
					{
						//println "1.empty item"
						def recording = [:]
						def highlights = [:]
						def resourceList = []
						def highlightList = []
						
						resourceList << resource
						highlightList << highlight
						//insert result
						recording.put(alias,resourceList)
						//insert highlight
						highlights.put(alias,highlightList)
						//use multimediaResource id as the id for search result
						long id = targetResource.id
						ResourceResultItem item = new ResourceResultItem(id:id,recording:recording,highlights:highlights,score:score)
						results << item
					}
					//Add the synmark into the resultItem
					else
					{
						//println "2.synmark empty"
						//insert result and highlight
						//synmarkList and highlightList should be all null or all not null
						def resourceList = resultItem.recording.getAt(alias)
						def highlightList = resultItem.highlights.getAt(alias)
						
						//There is no this kind of resource in the resultItem
						if(resourceList == null || highlightList == null)
						{
							resourceList = []
							resourceList << resource
							resultItem.recording.put(alias,resourceList)
							
							highlightList = []
							highlightList << highlight
							resultItem.highlights.put(alias, highlightList)
						}
						//There is(are) this kind of resources(s) in the resultItem already
						else
						{
							//println "3.multi synmark not empty"
							resourceList << resource
							highlightList << highlight
						}
						
						//add score
						resultItem.score += score
					}
				}
			}
		}
		if(!sameQuery)
			qr.recordingNum = results.size()
			
		if(!qr.save(flush:true))
		{
			//Do Nothing
			log.error("Query result cannot be saved!");	
		}
		//Save query record to params. We don't save qr to session because one user may start several search in one session.
		qr.refresh()
		params.qr = qr.id
	}
	
	/*
	 * Remove the highlight,return a text without <b></b> tag. But there might be a problem
	 * that if there is <b> tag in the text itself...... we may remove some useful information
	 */
	def removeHighlight (String highlightStr)
	{
		String text = highlightStr
		text = text.replaceAll("<b>", "")
		text = text.replaceAll("</b>", "")
		return text
	}
	
	private getTargetResource(resource)
	{
		//I don't know if resource can be null...probably not
		if(resource)
		{
			def resourceAnnotation
			try
			{
				resourceAnnotation = ResourceAnnotation.findBySource(resource)
			}
			catch(org.springframework.dao.InvalidDataAccessApiUsageException ex)
			{
				log.debug("Cannot find resourceAnnotation for resource with id ${resource.id}")
				resource.reindex()
				resourceAnnotation = null
			}
			if(resourceAnnotation)
			{
				return resourceAnnotation.target
			}
			else
			{
				return null
			}
		}
		else
		{
			return null
		}
	}
	
	/*
	 * If you just want to search a certain field in a domain class,
	 * you need to specify it in params
	 */
	private initContentHighlighter(params)
	{
		//		As far as I know, there are three possible types for a searchable property:
		//1. Simple type or a type that can be convert to a string directly
		//2. user defined type, such as TextNoteResource in this example
		//3. In "one to many" relationship, the type of the property is java.util.HashSet
		//There should be a propertyToString(String propName) method defined in
		//this kind of domain class in order to get the string that to be highlighted
		def contentHighlighter = { highlighter, index, sr ->
			if (!sr.highlights) {
				sr.highlights = []
			}
			
			def highlighterMap = [:]
			
			try
			{
				//println "class:"+sr.results[index].getClass()
				highlighter.setMaxNumFragments(5)
				sr.results[index].propertiesToString(params).each {item->
					//println "key:"+sr.results[index].getClass()+"."+item.key
					//println "value:"+item.value
					highlighterMap.put(item.key, highlighter.fragmentsWithSeparator(item.key, item.value))
				}
				sr.highlights[index]= highlighterMap
			}
			catch(MissingMethodException mme)
			{
				throw new ResourceSearchException("preopertyToString() method is missing in the searchable class ["+sr.results[index].getClass()+"]")
			}
		}
		
		return contentHighlighter
	}
	
	//Sometimes we may only search one kind of resource such as synmark or transcript
	//User in the search page can choose which resource to search. This information
	//Will pass to this method
	
	/*
	 * Depricated NOW!
	 */
	private String[] getResourceAliases(params)
	{
		def aliases = []
		if(params?.multimedia)
		{
			aliases << MultimediaResource.getResourceAlias()
		}
		if(params?.synmark)
		{
			aliases << SynmarkResource.getResourceAlias()
		}
		if(params?.transcript)
		{
			aliases << TranscriptResource.getResourceAlias()
		}
		
		//If user doesn't specify any resource to search, we search all of them
		//if(!params?.multimedia && !params?.synmark && !params?.transcript)
		//{
		//	println "allll"
		//	aliases << MultimediaResource.getResourceAlias()
		//	aliases << SynmarkResource.getResourceAlias()
		//	aliases << TranscriptResource.getResourceAlias()
		//params.multimedia = "on"
		//params.synmark = "on"
		//params.transcript = "on"
		//}
		String[] aliasesStr = aliases.toArray()
		return aliasesStr
	}
	
	/*
	 * Possible params:
	 * all: all these words
	 * exact: this exact phrase
	 * oneormore1..3:one or more fo these words (I may add ajax support for more words later)
	 * unwanted: any of these unwanted words
	 */
	public queryStringBuilder(params)
	{
		
		StringBuilder sb = new StringBuilder()
		if(params.all && params.all.trim().size()>0)
		{
			//println "all:"+params.all
			sb.append("(")
			sb.append(params.all)
			sb.append(") ")
		}
		
		if(params.exact && params.exact.trim().size()>0)
		{
			//println "exact:"+params.exact
			//sb.append("AND ")
			sb.append("+")
			sb.append("\"")
			sb.append(params.exact)
			sb.append("\" ")
		}
		
		boolean oneormore = false
		
		if(params.oneormore1 && params.oneormore1.trim().size()>0)
		{
			sb.append("(")
			oneormore = true
			sb.append(params.oneormore1)
			//println "oneormore1:"+params.oneormore1
		}
		if(params.oneormore2 && params.oneormore2.trim().size()>0)
		{
			if(!oneormore)
			{
				sb.append("(")
				oneormore = true
			}
			else
			{
				sb.append(" OR ")
			}
			sb.append(params.oneormore2)
			//println "oneormore2:"+params.oneormore2
		}
		if(params.oneormore3 && params.oneormore3.trim().size()>0)
		{
			if(!oneormore)
			{
				sb.append("(")
				oneormore = true
			}
			else
			{
				sb.append(" OR ")
			}
			sb.append(params.oneormore3)
			//println "oneormore3:"+params.oneormore3
		}
		
		if(oneormore)
		{
			sb.append(")")
		}
		
		if(params?.unwanted && params?.unwanted.trim().size() > 0)
		{
			sb.append(" -")
			sb.append("\"")
			sb.append(params?.unwanted)
			sb.append("\"")
		}
		return sb.toString()
	}
	
	/*
	 *multimedia: multimedia resource
	 *synmark: synmark resource
	 *transcript: transcript resource
	 */
	public buildQuery(boolean advanced, params)
	{
		def search
		
		
		
		//currently, advanced and !advanced are the same
		search = {
			queryString(params.query)
		}
		
		return search
	}
	
	/*
	 *OK, there is a problem about compass search. When your search term ends
	 *with * character, the highlighter refuses to highlight the result text.
	 *But there is no problem for ?. If * or ? appears in the middle or beginning
	 *of the search term, it's also fine. So we here have to trim the final *
	 */
	private String formalizeQuery (String query)
	{
		String formalizedQuery = query
		if(regExService.isUnacceptedSearchTerm(query))
		{
			throw new ResourceSearchException("Query should be constructed with words and should not be ended with *.")
		}
		return formalizedQuery
	}
	
	/*
	 * There is something weired:
	 * If we use searchableService.search() to search all the Resources (both SynmarkResource and TranscriptResource)
	 * i.e. using setAliases() to specify all the resource aliases, the returned results
	 * will miss some TranscriptResource instances if there are synmark resources annotate
	 * the same multimedia resource that this transcript resource annotates.
	 *
	 * So that's why I decide to search the three resources separately and finally combine the results together.
	 * That is what this method for.
	 *
	 *
	 * What about suggested query? I didn't implement it.
	 */
	private getOriginalSearchResult(params,qr, sameQuery)
	{
		def searchResult = [:]
		def results = []
		def highlights = []
		def scores = []
		
		boolean advanced = (Boolean.valueOf(params.advanced)).booleanValue()
		if(!advanced)
		{
			def multiResult = MultimediaResource.search(buildQuery(advanced,params),[withHighlighter:initContentHighlighter(params),escape:true])
			def synmarkResult = SynmarkResource.search(buildQuery(advanced,params),[withHighlighter:initContentHighlighter(params),escape:true])
			def transcriptResult = TranscriptResource.search(buildQuery(advanced,params),[withHighlighter:initContentHighlighter(params),escape:true])
			
			if(multiResult.results.size() > 0)
			{
				results.addAll(multiResult.results)
				highlights.addAll(multiResult.highlights)
				scores.addAll(multiResult.scores)
				if(!sameQuery)
				{
					qr.resultNum += multiResult.results.size()
					qr.hits += multiResult.total
				}
			}
			
			if(synmarkResult.results.size() >0)
			{
				/*println "synm:"+synmarkResult.results.size()
				 synmarkResult.highlights.eachWithIndex { highlight,index->
				 println "highlightsyn$index:"+highlight
				 def synmark = synmarkResult.results[index]
				 println "title:"+synmark.title
				 println "tags"+synmark.tags
				 println "note:"+synmark.note
				 }*/
				results.addAll(synmarkResult.results)
				highlights.addAll(synmarkResult.highlights)
				scores.addAll(synmarkResult.scores)
				if(!sameQuery)
				{
					qr.resultNum += synmarkResult.results.size()
					qr.hits += synmarkResult.total
				}
			}
			
			if(transcriptResult.results.size() >0)
			{
				results.addAll(transcriptResult.results)
				highlights.addAll(transcriptResult.highlights)
				scores.addAll(transcriptResult.scores)
				if(!sameQuery)
				{
					qr.resultNum += transcriptResult.results.size()
					qr.hits += transcriptResult.total
				}
			}
		}
		else //advanced search
		{
			
			if(params?.multimedia)
			{
				def multiResult = MultimediaResource.search(buildQuery(advanced,params),[withHighlighter:initContentHighlighter(params),escape:true])
				if(multiResult.results.size() > 0)
				{
					results.addAll(multiResult.results)
					highlights.addAll(multiResult.highlights)
					scores.addAll(multiResult.scores)
					if(!sameQuery)
					{
						qr.resultNum += multiResult.results.size()
						qr.hits += multiResult.total
					}
				}
			}
			
			if(params?.synmark)
			{
				def synmarkResult = SynmarkResource.search(buildQuery(advanced,params),[withHighlighter:initContentHighlighter(params),escape:true])
				if(synmarkResult.results.size() >0)
				{
					results.addAll(synmarkResult.results)
					highlights.addAll(synmarkResult.highlights)
					scores.addAll(synmarkResult.scores)
					if(!sameQuery)
					{
						qr.resultNum += synmarkResult.results.size()
						qr.hits += synmarkResult.total
					}
				}
			}
			
			if(params?.transcript)
			{
				def transcriptResult = TranscriptResource.search(buildQuery(advanced,params),[withHighlighter:initContentHighlighter(params),escape:true])
				if(transcriptResult.results.size() >0)
				{
					results.addAll(transcriptResult.results)
					highlights.addAll(transcriptResult.highlights)
					scores.addAll(transcriptResult.scores)
					if(!sameQuery)
					{
						qr.resultNum += transcriptResult.results.size()
						qr.hits += transcriptResult.total
					}
				}
			}
		}
		
		searchResult.put('results',results)
		searchResult.put('highlights',highlights)
		searchResult.put('scores',scores)
		return searchResult
	}
	
	/*
	 * The search plugin sometimes return results with no hits of the
	 * searching term, this programme will remove this kind of items from
	 * the highligh map
	 */
	private removeEmptyHighlightItems(highlight)
	{
		def oldHighlightList = highlight.entrySet().toList()
		
		for(h in 0..<highlight.size())
		{
			if(!oldHighlightList.get(h).value)
			{
				def key = oldHighlightList.get(h).key
				highlight.remove(key)
			}
		}
		
		return highlight
	}
	//Due to a bug when editing transcript, some recordings have more than one
	//transcript. They annotate the same multimedia. This programme is going to select
	//the transcript with the lowest id. Hopefully this function can be removed later.
	def removeDuplicatedTranscript(transcriptResult)
	{
		//Do nothing
	}
	
	/**
	 * Perform a bulk index of every searchable object in the database
	 * Resource.index() throws MissingMethodException, that's wired!
	 */
	def indexResources = {
		//Thread.start {
		//	searchableService.index()
		//}
		//Do nothing
	}
	
	/**
	 * Unindex all resources
	 */
	def unindexResources = {
		searchableService.unindex()
	}
	
	/*
	 * Reindex all resources
	 */
	def reindexResources = {
		//Thread.start{
		//	searchableService.reindex()
		//}
		//Do nothing
	}
}

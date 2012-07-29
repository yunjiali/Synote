package org.synote.resource

import org.synote.utils.DatabaseService
import org.synote.user.SecurityService
import org.synote.utils.UtilsService
import org.synote.config.ConfigurationService
import org.synote.player.client.TimeFormat
import org.synote.resource.compound.*
import org.synote.resource.single.text.TagResource
import org.synote.resource.single.text.SynmarkTag
import org.synote.resource.single.text.MultimediaTag
import org.synote.resource.single.text.SynmarkTextNote
import org.synote.resource.single.text.MultimediaTextNote
import org.synote.resource.single.text.WebVTTCue
import org.synote.resource.single.binary.PresentationSlide
import org.synote.annotation.ResourceAnnotation
import org.synote.permission.PermissionValue
import org.synote.linkeddata.LinkedDataService
import org.synote.analysis.Views
import grails.converters.*
import groovy.json.*

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

class ResourceService {

	def databaseService
	def securityService
	def utilsService
	def linkedDataService
	def configurationService
	
    static transactional = true

	/*
	 * List multimedia as json, the format is designed speficially for jqGrid
	 */
    def getMultimediaAsJSON(jqGridParams)
	{
		if(!jqGridParams.sidx)
			jqGridParams.sidx = 'date_created'
		if(!jqGridParams.sord)
			jqGridParams.sord = 'desc'
		if(!jqGridParams.rows)
			jqGridParams.rows ="10"
		def maxRows = Integer.valueOf(jqGridParams.rows)
		if(!jqGridParams.page)
			jqGridParams.page ="1"
		def currentPage = Integer.valueOf(jqGridParams.page) ?: 1
		def rowOffset = currentPage == 1 ? 0 : (currentPage - 1) * maxRows
		
		boolean isLoggedIn = securityService.isLoggedIn()
		
		if(jqGridParams.sidx == "perm_val")
		{
			if(isLoggedIn)
			{
				jqGridParams.sidx="user_perm_val"
			}
			else
				jqGridParams.sidx="public_perm_val"
		}
		
		def gParams = utilsService.mapJQGridParamsToGrails(jqGridParams)
		
		def multimediaResourceList = databaseService.listMultimedia(gParams)
		int count = databaseService.countMultimediaList(gParams)
				
		def numberOfPages = Math.ceil(count / maxRows)
		
		
		def results = []
		
		multimediaResourceList?.collect{ r->
			def multimedia = MultimediaResource.findById(r.id)
			def tags = []
			multimedia.tags.each{
				tags<<it.content
			}
			def views = Views.countByResource(multimedia)
			def metrics = getMultimediaResourceMetrics(multimedia)
			
			def item = [
				id:r.id, 
				title:r.title,
				url:MultimediaResource.findById(r.id).url?.url,
				owner_name:r.owner_name,
				perm_name:isLoggedIn?PermissionValue.findByVal(r.user_perm_val).name:r.public_perm_name,
				perm_val:isLoggedIn?r.user_perm_val:r.public_perm_val,
				date_created:utilsService.convertSQLTimeStampToFormattedTimeString(r.date_created,"dd.MM.yyyy HH:mm"),
				last_updated:utilsService.convertSQLTimeStampToFormattedTimeString(r.last_updated,"dd.MM.yyyy HH:mm"),
				thumbnail:r.thumbnail,
				duration:r.duration,
				isVideo:r.is_video,
				note:multimedia.note?.content,
				tags:tags,
				cc:metrics.cc,
				slides_count:metrics.slides_count,
				synmarks_count: metrics.synmarks_count,
				views:views
			]
			
			results << item
		}
		def jqGridData = [rows:results, page:currentPage, records:count, total:numberOfPages]
		return jqGridData
    }
	
	/*
	 * Only list my multimedia resources
	 */
	def getMyMultimediaAsJSON(params)
	{
		//Default: created data desc
		def sortIndex = params.sidx ?: 'dateCreated'
		def sortOrder  = params.sord ?: 'desc'
		if(!params.rows)
			params.rows ="10"
			
		def maxRows = Integer.valueOf(params.rows)
		if(!params.page)
			params.page ="1"
		def currentPage = Integer.valueOf(params.page) ?: 1
		def rowOffset = currentPage == 1 ? 0 : (currentPage - 1) * maxRows
		def user=securityService.getLoggedUser()
		
		def multimediaList = MultimediaResource.createCriteria().list(max:maxRows, offset:rowOffset){
			eq('owner',user)
			if(params.text?.trim()?.length()>0)
			{
				ilike("title","%${params.text}%")
			}
			order(sortIndex,sortOrder).ignoreCase()
		}
		def totalRows = multimediaList.totalCount
		def numberOfPages = Math.ceil(totalRows/maxRows)
		def results = []
		
		multimediaList?.collect{ r->
			def views = Views.countByResource(r)
			def metrics = getMultimediaResourceMetrics(r)
			def tags = []
			r.tags.each{
				tags<<it.content	
			}
			results << [
				id:r.id, 
				//owner_name:r.owner.userName, Don't need owner_name, it's you!
				title:r.title,
				url:r.url?.url,
				perm_name:r.perm?.name,
				perm_val:r.perm?.val,
				date_created:utilsService.convertSQLTimeStampToFormattedTimeString(r.dateCreated,"dd.MM.yyyy HH:mm"),
				last_updated:utilsService.convertSQLTimeStampToFormattedTimeString(r.lastUpdated,"dd.MM.yyyy HH:mm"),
				thumbnail:r.thumbnail,
				duration:r.duration,
				isVideo:r.isVideo,
				note:r.note?.content,
				tags:tags,
				cc:metrics.cc,
				slides_count:metrics.slides_count,
				synmarks_count: metrics.synmarks_count,
				views:views
			]
		}
		def jqGridData = [rows:results, page:currentPage, records:totalRows, total:numberOfPages]
		return jqGridData
	}
	
	/*
	 * given the mmid, get all the synmarks related to it
	 */
	def getSynmarksAsJSON(multimedia,params)
	{
		def sortIndex = params.sidx ?: 'start' //sort by start
		def sortOrder  = params.sord ?: 'asc' //asc
		if(!params.rows)
			params.rows ="10"
			
		def maxRows = Integer.valueOf(params.rows)
		if(!params.page)
			params.page ="1"
		def currentPage = Integer.valueOf(params.page) ?: 1
		def rowOffset = currentPage == 1 ? 0 : (currentPage - 1) * maxRows
		
		//Select synpoint first to save time
		def synpointList = []
		
		//Yunjia:Create a synmark view, all the fiedls are string
		def annotations = ResourceAnnotation.findAllByTarget(multimedia)
		
		annotations.each {annotation ->
			if (annotation.source.instanceOf(SynmarkResource))
				synpointList << annotation.synpoints.toArray()[0]
		}
		def totalRows = synpointList.size()
		def synpoints = synpointList.sort{it.targetStart}.subList(rowOffset, Math.min(rowOffset+maxRows, synpointList.size()))
		def numberOfPages = Math.ceil(totalRows/maxRows)
		def results = []
		
		synpoints?.collect{ synpoint->
			
			//println "sr:"+sr.id
			def annotation = ResourceAnnotation.findById(synpoint.annotation?.id)
			def sr = SynmarkResource.findById(annotation?.source?.id)
			//println "sr:"+sr.id
			def mr = annotation?.target
			//println "mr:"+mr?.id
			if(annotation && mr && sr)
			{
				def start = synpoint.targetStart
				def end = synpoint.targetEnd
				def tags = null
				if(sr.tags?.size() > 0)
				{
					tags = []
					sr.tags.each{t -> 
						tags << t.content
					}
				}
				else
					tags = null
				
				def item = [
					id:sr.id,
					//owner_name:r.owner.userName, Don't need owner_name, it's you!
					title:sr.title,
					tags:tags,
					note:sr.note?.content?.trim()?.size()>256?sr.note?.content?.substring(0,256)+"...":sr.note?.content,
					rtitle:mr.title,
					rid:mr.id,
					risVideo:mr.isVideo,
					mf: synpoint?linkedDataService.getFragmentStringFromSynpoint(synpoint):null,//get media fragment
					start: start !=null?TimeFormat.getInstance().toString(start):"unknown",
					end:end !=null?TimeFormat.getInstance().toString(end):"unknown",
					thumbnail:sr.thumbnail,
					date_created:utilsService.convertSQLTimeStampToFormattedTimeString(sr.dateCreated,"dd.MM.yyyy HH:mm"),
					last_updated:utilsService.convertSQLTimeStampToFormattedTimeString(sr.lastUpdated,"dd.MM.yyyy HH:mm")
				]
				
				results << item
			}
			else //The synmark doesn't annotate any multimedia resource, so we need to delete it
			{
				sr.delete()
			}
		}
		//println "size:"+synmarkList.size()
		def jqGridData = [rows:results, page:currentPage, records:totalRows, total:numberOfPages]
		return jqGridData
	}
	
	def getMySynmarksAsJSON(params)
	{
		def sortIndex = params.sidx ?: 'date_created'
		def sortOrder  = params.sord ?: 'desc'
		if(!params.rows)
			params.rows ="10"
			
		def maxRows = Integer.valueOf(params.rows)
		if(!params.page)
			params.page ="1"
		def currentPage = Integer.valueOf(params.page) ?: 1
		def rowOffset = currentPage == 1 ? 0 : (currentPage - 1) * maxRows
		def user=securityService.getLoggedUser()
		def synmarkList
		def totalRows
		def gParams = utilsService.mapJQGridParamsToGrails(params)
		//Yunjia:Create a synmark view, all the fiedls are string
		if(params.text?.trim()?.length()>0)
		{
			
			//Yunjia: Add Synmark notes later
			synmarkList = databaseService.searchMySynmarks(gParams, user, params.text)
			totalRows = databaseService.searchMySynmarksCount(gParams,user, params.text)
			
		}
		else
		{
			synmarkList = databaseService.getMySynmarks(gParams, user)
			totalRows = databaseService.getMySynmarksCount(gParams,user)
		}
		
		def numberOfPages = Math.ceil(totalRows/maxRows)
		def results = [] 
		
		synmarkList?.collect{ s->
			def sr = SynmarkResource.findById(s.id)
			//println "sr:"+sr.id
			def annotation = ResourceAnnotation.findBySource(sr)
			//println "an:"+annotation?.id
			def mr = annotation?.target
			//println "mr:"+mr?.id
			if(annotation && mr)
			{
				def synpoint = null
				def start = null
				def end = null
				if(annotation.synpoints?.size()>0)
				{
					synpoint = annotation.synpoints.toArray()[0]
					start = annotation.synpoints.toArray()[0].targetStart
					end = annotation.synpoints.toArray()[0].targetEnd
				} 
				
				def item = [
					id:s.id,
					//owner_name:r.owner.userName, Don't need owner_name, it's you!
					title:s.title,
					tags:s.tags?.size() > 0?s.tags.split(","):null,
					note:sr.note?.content?.trim()?.size()>256?sr.note?.content?.substring(0,256)+"...":sr.note?.content,
					rtitle:mr.title,
					rid:mr.id,
					risVideo:mr.isVideo,
					mf: synpoint?linkedDataService.getFragmentStringFromSynpoint(synpoint):null,//get media fragment
					start: start !=null?TimeFormat.getInstance().toString(start):"unknown",
					end:end !=null?TimeFormat.getInstance().toString(end):"unknown",
					thumbnail:s.thumbnail,
					date_created:utilsService.convertSQLTimeStampToFormattedTimeString(s.date_created,"dd.MM.yyyy HH:mm"),
					last_updated:utilsService.convertSQLTimeStampToFormattedTimeString(s.last_updated,"dd.MM.yyyy HH:mm")
				]
				
				results << item
			}
			else //The synmark doesn't annotate any multimedia resource, so we need to delete it
			{
				sr.delete()
			}
		}
		//println "size:"+synmarkList.size()
		def jqGridData = [rows:results, page:currentPage, records:totalRows, total:numberOfPages]
		return jqGridData
	}
	
	def getMostViewedMultimedia(max)
	{
		def viewList = Views.executeQuery(
			"select v.resource.id,count(v.id) from Views v, MultimediaResource mr where mr.id=v.resource.id and mr.perm.val > 0 group by v.resource order by count(v.id) desc",[],[max:max])
		
		def results = []
		viewList.each{ v->
			
			def r = MultimediaResource.findById(v[0].toLong())
			
			if(r)
			{
				def metrics = getMultimediaResourceMetrics(r)
			
				results << [
					id:r.id,
					//owner_name:r.owner.userName, Don't need owner_name, it's you!
					title:r.title,
					url:r.url?.url,
					owner_name:r.owner.userName,
					perm_name:r.perm?.name,
					perm_val:r.perm?.val,
					date_created:utilsService.convertSQLTimeStampToFormattedTimeString(r.dateCreated,"dd.MM.yyyy HH:mm"),
					last_updated:utilsService.convertSQLTimeStampToFormattedTimeString(r.lastUpdated,"dd.MM.yyyy HH:mm"),,
					thumbnail:r.thumbnail,
					duration:r.duration,
					isVideo:r.isVideo,
					cc:metrics.cc,
					slides_count:metrics.slides_count,
					synmarks_count: metrics.synmarks_count,
					views:v[1]
				]
			}	
		}
		
		return [rows:results,records:max]
	}
	
	/*
	 * Get the multimedia resources with transcripts
	 */
	def getMyTranscriptsAsJSON(params)
	{
		def sortIndex = params.sidx ?: 'dateCreated'
		def sortOrder  = params.sord ?: 'desc'
		if(!params.rows)
			params.rows ="10"
			
		def maxRows = Integer.valueOf(params.rows)
		if(!params.page)
			params.page ="1"
		def currentPage = Integer.valueOf(params.page) ?: 1
		def rowOffset = currentPage == 1 ? 0 : (currentPage - 1) * maxRows
		def user=securityService.getLoggedUser()
		//Yunjia:Create a synmark view, all the fiedls are string
	
		def transcriptList = WebVTTResource.createCriteria().list(max:maxRows, offset:rowOffset){
			eq('owner',user)
			order(sortIndex,sortOrder).ignoreCase()
		}
		def totalRows = transcriptList.totalCount
		def numberOfPages = Math.ceil(totalRows/maxRows)
		def results = []
		
		transcriptList?.collect{ vtt->
			def vttr = WebVTTResource.findById(vtt.id)
			//println "sr:"+sr.id
			def annotation = ResourceAnnotation.findBySource(vttr)
			//println "an:"+annotation?.id
			def mr = annotation?.target
			//println "mr:"+mr?.id
			if(annotation && mr)
			{	
				def views = Views.countByResource(mr)
				def metrics = getMultimediaResourceMetrics(mr)
				def tags = []
				mr.tags.each{
					tags<<it.content
				}
				def item = [
					id:mr.id, 
					//owner_name:r.owner.userName, Don't need owner_name, it's you!
					title:mr.title,
					url:mr.url?.url,
					perm_name:mr.perm?.name,
					perm_val:mr.perm?.val,
					date_created:utilsService.convertSQLTimeStampToFormattedTimeString(mr.dateCreated,"dd.MM.yyyy HH:mm"),
					last_updated:utilsService.convertSQLTimeStampToFormattedTimeString(mr.lastUpdated,"dd.MM.yyyy HH:mm"),
					thumbnail:mr.thumbnail,
					duration:mr.duration,
					isVideo:mr.isVideo,
					note:mr.note?.content,
					tags:tags,
					cc:metrics.cc,
					slides_count:metrics.slides_count,
					synmarks_count: metrics.synmarks_count,
					views:views
				]
				
				results << item
			}
			else //The synmark doesn't annotate any multimedia resource, so we need to delete it
			{
				vttr.delete()
			}
		}
		//println "size:"+synmarkList.size()
		def jqGridData = [rows:results, page:currentPage, records:totalRows, total:numberOfPages]
		return jqGridData
	}
	
	def getTagsAsArray(user)
	{
		//haven't implemented yet
	}
	
	/*
	 * Get the statistical data about the annotations of a certain multimedia resource
	 * r: mutliemdia resource
	 */
	def getMultimediaResourceMetrics(r)
	{
		boolean cc = false
		int slides_count = 0;
		int synmarks_count = 0;
		def annotations = ResourceAnnotation.findAllByTarget(r)
		annotations.each{anno->
			if(anno.source.instanceOf(SynmarkResource))
				synmarks_count++
			else if(anno.source.instanceOf(TranscriptResource))
				cc = true
			else if(anno.source.instanceOf(PresentationResource))
				slides_count = anno.source.slides?.size()
		}
		
		return [cc:cc,slides_count:slides_count,synmarks_count:synmarks_count]
	}
	
	/*
	 * Given a resource, get the corresponding synpoint. If no synpoint or not only one synpoint is corresponded,
	 * return null
	 */
	def getSynpointByResource(Resource r)
	{
		if(r.instanceOf(MultimediaResource)||r.instanceOf(MultimediaTag) || 
			r.instanceOf(MultimediaTextNote) || r.instanceOf(WebVTTResource) || r.instanceOf(PresentationResource))	
		{
			return null	
		}
		else if(r.instanceOf(SynmarkResource))
		{
			def anno = ResourceAnnotation.findBySource(r)
			if(!anno)
				return null
			
			return anno.synpoints[0]	
		}
		else if(r.instanceOf(SynmarkTag) || r.instanceOf(SynmarkTextNote))
		{
			def synmark = r.synmark
			def anno = ResourceAnnotation.findBySource(synmark)
			if(!anno)
				return null
			
			return anno.synpoints[0]
		}
		else if(r.instanceOf(WebVTTCue))
		{
			def vtt = r.webVTTFile
			def anno = ResourceAnnotation.findBySource(vtt)
			if(!anno)
				return null
			
			def synpoint = anno.synpoints.find{it.sourceStart == r.cueIndex}
			return synpoint
		}
		else if(r.instanceOf(PresentationSlide))
		{
			def presentation = r.presentation
			def anno = ResourceAnnotation.findBySource(presentation)
			if(!anno)
				return null
			
			def synpoint = anno.synpoints.find{it.sourceStart == r.index}
			return synpoint
		}
		else
			return null
	}
	
	/*
	* Given a resource, get the corresponding multimedia it annotates or related to
	*/
   def getMultimediaByResource(Resource r)
   {
	   if(r.instanceOf(MultimediaResource))
	   {
		   return r
	   }
	   else if(r.instanceOf(MultimediaTag) || r.instanceOf(MultimediaTextNote)) 
	   {
		   return r.multimedia
	   }
	   else if(r.instanceOf(SynmarkResource) || r.instanceOf(WebVTTResource) || r.instanceOf(PresentationResource))
	   {
		   def anno = ResourceAnnotation.findBySource(r)
		   if(!anno)
			   return null
		   
		   return anno.target
	   }
	   else if(r.instanceOf(SynmarkTag) || r.instanceOf(SynmarkTextNote))
	   {
		   def synmark = r.synmark
		   def anno = ResourceAnnotation.findBySource(synmark)
		   if(!anno)
			   return null
		   
		   return anno.target
	   }
	   else if(r.instanceOf(WebVTTCue))
	   {
		   def vtt = r.webVTTFile
		   def anno = ResourceAnnotation.findBySource(vtt)
		   if(!anno)
			   return null
		   
		   return anno.target
	   }
	   else if(r.instanceOf(PresentationSlide))
	   {
		   def presentation = r.presentation
		   def anno = ResourceAnnotation.findBySource(presentation)
		   if(!anno)
			   return null
		   
		   return anno.target
	   }
	   else
		   return null
   }
	
	/*
	 * Generating thumbnail pictures using synote-multimedia-service 
	 */
	def generateThumbnail(url, uuid, start,end)
	{
		def videourl = url?.encodeAsURL()
		def synoteMultimediaServiceURL = configurationService.getConfigValue("org.synote.resource.service.server.url")
		if(!synoteMultimediaServiceURL)
			return null
		def generateThumbnailURL = configurationService.getConfigValue("org.synote.service.generateThumbnail.path")
		def requestURL = synoteMultimediaServiceURL+generateThumbnailURL	
		
		def http = new HTTPBuilder(synoteMultimediaServiceURL)
		def thumbnail_url = null
		try
		{
			def query = [videourl:videourl, id:uuid]
			if(start != null)
				query.put("start",start)
			if(end != null)
				query.put("end",end)
			http.get(path:generateThumbnailURL, query:query){ resp,json->
			
				//println "------------------"
				//println json
				thumbnail_url = json.getAt("thumbnail_url")
				return thumbnail_url
			}
		}catch(Exception ex)
		{
			//do nothing
			log.debug(ex.getMessage())	
		}
		finally
		{
			return thumbnail_url	
		}
	}
	
	/*
	 * Get closed captioning in srt format from YouTube
	 * videoid: the id of YouTube Video
	 * lang: language code, default is en
	 */
	def getSRTfromYouTube(videoid,lang)
	{
		if(videoid == null)
			return null
			
		String l = lang
		if(lang == null)
			l= "en"
		String fmt = "srt"
		
		def http = new HTTPBuilder("http://www.youtube.com")
		def srt = null
		try
		{
			def queryTrack = [v:videoid, type:'list']
			
			//send the first request to get the track name for the subtitle
			http.get(path:"/api/timedtext", contentType:XML, query:queryTrack){ resp1,reader1->
				String s1 = reader1.text
				if(s1?.size() > 0)
				{
					//parse the xml
					def transcript_list = new XmlSlurper().parseText(s1)
					def trackName = transcript_list.track.@name.text()
					//send another request
					def querySRT = [v:videoid, format:fmt, lang:l, name:trackName]
					http.get(path:"/api/timedtext", contentType:TEXT, query:querySRT){ resp2,reader2->
							
						String s2 = reader2.text
						if(s2?.size() > 0)
							srt = s2
						
						return srt
					}
				}
			}
		}
		catch(Exception ex)
		{
			//do nothing
			println ex.getMessage()
			log.debug(ex.getMessage())
		}
		finally
		{
			return srt
		}
	}
}

package org.synote.resource

/*
 * This service connect to synote-multimedia-service and generates thumbnail pictures, durations, etc 
 */

import org.codehaus.groovy.grails.web.json.JSONObject
import grails.converters.JSON
import org.synote.resource.compound.WebVTTResource
import org.synote.resource.compound.SynmarkResource
import org.synote.resource.single.text.WebVTTCue
import org.synote.annotation.synpoint.Synpoint
import org.synote.annotation.ResourceAnnotation
import org.synote.config.ConfigurationService

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

class SynotemmService {

	def configurationService
	
    static transactional = true

	/*
	 * Generate thumbnail picture for each synpoint about a webvtt resource
	 */
    def generateWebVTTThumbnails(WebVTTResource vtt, multimedia)
	{
		def synoteMultimediaServiceURL = configurationService.getConfigValue("org.synote.resource.service.server.url")
		def thumbnailGenerationURL =synoteMultimediaServiceURL + 
			configurationService.getConfigValue("org.synote.resource.service.generateThumbnail.path")
		//println "url:"+thumbnailGenerationURL
		//println "webvtt:"+vtt?.id
		def anno = ResourceAnnotation.findBySource(vtt)
		if(anno == null || anno.target == null)
		{
			//Should throw an exception?
			return	
		}

		//println "here1"
		anno.synpoints.each{synpoint ->
			def start = synpoint.targetStart
			def end = synpoint.targetEnd
			def url = URLEncoder.encode(multimedia.url?.url,"utf-8")
			def uuid = multimedia.uuid
			
			def http = new HTTPBuilder(thumbnailGenerationURL)
			//println "here3"
			http.request(GET,groovyx.net.http.ContentType.JSON,{req->
				uri.query = [start:start, end:end, id:uuid, videourl:url]
				headers.Accept = "application/json"
				response.success = { resp, json->
					//println "resp:"+resp
					//println "thumbnailurl:"+json.thumbnail_url
					def thumbnail_url = ""
					def cue = WebVTTCue.findByWebVTTFileAndCueIndex(vtt,synpoint.sourceStart)
					if(cue)
					{
						if(json.thumbnail_url)
						{
							cue.thumbnail = json.thumbnail_url
							cue.save()
						}
						else
						{
							log.error("Cannot generate thumbnail pictures:"+json?.code+" message:"+json?.message)	
						}
					}
					else
					{
						log.error("Cannot find cue for webvtt resource "+vtt.id)		
					}
				}
				//println "here 4"
			})
		}
		//println "here2"
		return
    }
	
	def generateSynmarkThumbnail(SynmarkResource synmark, videourl)
	{
		//to be implemented
	}
	
	def generateCueThumbnail(WebVTTCue cue,videourl)
	{
		
	}
	
	def saveThumbnails(results)
	{
			
	}
}

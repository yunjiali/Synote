package org.synote.resource.compound

import org.synote.resource.compound.MultimediaResource
import org.synote.resource.Resource
import org.synote.resource.single.text.WebVTTCue
import org.synote.resource.compound.WebVTTResource
import org.synote.resource.compound.WebVTTService
import org.synote.player.client.WebVTTData
import org.synote.api.APIStatusCode
import org.synote.permission.PermService
import org.synote.annotation.ResourceAnnotation
import org.synote.annotation.Annotation



class WebVTTResourceController {
	
	def webVTTService
	def permService
    
	/*
	 * return the webvtt file based on id. if the id is WebVTTResoruce,return the whole webvtt file
	 * if the id is WebVTTCue, just return the cue with WebVTT file title
	 */
	def getWebVTT = 
	{
		if(!params.id)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.TRANSCRIPT_ID_MISSING,
					description:"There are multiple transcripts for this recording, please provide the id of the transcript you want to download")
			}
			return
		}
		def transcript = Resource.get(params.id)
		if(!transcript || (!transcript.instanceOf(WebVTTResource) && !transcript.instanceOf(WebVTTCue)))
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.TRANSCRIPT_NOT_FOUND, description:"Cannot find transcript with id!")
			}
			return
		}
		
		if(transcript.instanceOf(WebVTTResource))
		{
			def annotation = ResourceAnnotation.findBySource(transcript)
			if(annotation)
			{
				def multimedia = annotation.target
				if(!multimedia)
				{
					render(contentType:"text/json"){
						error(stat:APIStatusCode.MM_NOT_FOUND, description:"Cannot find the multimedia resource!")
					}
					return
				}
				def perm = permService.getPerm(multimedia)
				if(perm?.val <=0)
				{
					render(contentType:"text/json"){
						error(stat:APIStatusCode.TRANSCRIPT_PERMISSION_DENIED, description:"Permission denied!")
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
						error(stat:APIStatusCode.TRANSCRIPT_NOT_FOUND, description:"Cannot find transcript for multimedia resource with id=${multimedia.id}!")
					}
					return
				}
			}
			else
			{
				render(contentType:"text/json"){
					error(stat:APIStatusCode.RESOURCEANNOTATION_NOT_FOUND, description:"Cannot find the anntoation.")
				}
				return
			}
		}
		else //instanceof webvttcue
		{
			def annotation = ResourceAnnotation.findBySource(transcript.webVTTFile)
			if(annotation)
			{
				def multimedia = annotation.target
				if(!multimedia)
				{
					render(contentType:"text/json"){
						error(stat:APIStatusCode.MM_NOT_FOUND, description:"Cannot find the multimedia resource!")
					}
					return
				}
				def perm = permService.getPerm(multimedia)
				if(perm?.val <=0)
				{
					render(contentType:"text/json"){
						error(stat:APIStatusCode.TRANSCRIPT_PERMISSION_DENIED, description:"Permission denied!")
					}
					return
				}
				
				String responseStr = webVTTService.getWebVTTCueAsString(transcript)
				response.setHeader("Content-disposition", "attachment;filename=transcript.vtt")
				render(contentType:"text/vtt", text:responseStr)
				return
			}
			else
			{
				render(contentType:"text/json"){
					error(stat:APIStatusCode.RESOURCEANNOTATION_NOT_FOUND, description:"Cannot find the anntoation.")
				}
				return
			}
		}
	}
}

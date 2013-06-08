package org.synote.resource.compound

import grails.plugins.springsecurity.Secured
import org.synote.resource.ResourceService
import org.synote.linkeddata.LinkedDataService
import org.synote.resource.compound.WebVTTResource
import org.synote.permission.PermService
import org.synote.player.client.WebVTTCueData
import org.synote.player.client.WebVTTData
import org.synote.player.client.TimeFormat
import org.synote.api.APIStatusCode

import org.synote.player.client.PlayerException

class TranscriptResourceController {

	def resourceService
	def permService
	def webVTTService
	def linkedDataService
    /*
	* List all my multimedia resource with transcript as JSON, not listing transcript
	*/
   //@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
   def list = {
	   if(!params.id)
	   {
		   flash.error = "Cannot find the recording."
		   redirect(controller:'user',action:'index')
		   return
	   }
	   
	   def multimedia = MultimediaResource.get(params.id?.toLong())
	   if(!multimedia)
	   {
		   flash.error = "Cannot find the reocording id."
		   redirect(controller:'user',action:'index')
		   return
	   }
	   
	   def perm = permService.getPerm(multimedia)
	   if(perm?.val <=0)
	   {
		   flash.error = "Permission Denied!"
		   redirect(controller:'user',action:'index')
		   return
	   }
	   
	   //different from synmarks and multimedia resource, we directly use webVTTService
	   WebVTTData[] transcripts= webVTTService.getTranscripts(multimedia)
	   
	   if(!WebVTTData)
	   {
		   flash.error = "Cannot find the transcript for the recording."
		   redirect(controller:'user',action:'index')
		   return
	   }
   	
	   def transcript = transcripts[0]
	   WebVTTCueData[] cues = transcript.getCues()
	  
	   if(!params.rows)
	   		params.rows ="30"
	   
	   def maxRows = Integer.valueOf(params.rows)
	   if(!params.page)
		   params.page ="1"
	   def currentPage = Integer.valueOf(params.page) ?: 1
	   def rowOffset = currentPage == 1 ? 0 : (currentPage - 1) * maxRows
	   
	   def totalRows = transcript.cues?.size()
	   def numberOfPages = Math.ceil(totalRows/maxRows)
	   def cueList = cues.sort{it.getStart()}.toList().subList(rowOffset, Math.min(rowOffset+maxRows, totalRows))
	   
	   def results = []
	   
	   cueList.each{cue -> 
		   results << resourceService.buildCueJSON(cue,multimedia)
	   }
	   def jqGridData = [rows:results, page:currentPage, records:totalRows, total:numberOfPages]
	   return [cueList:jqGridData, params:params,multimedia:multimedia,transcript:transcript]
   }
   
   @Secured(['ROLE_ADMIN','ROLE_NORMAL'])
   def upload = {
	   if(!params.id)
	   {
		   flash.error = "Cannot find the recording."
		   redirect(controller:'user',action:'index')
		   return
	   }
	   
	   def multimedia = MultimediaResource.get(params.id?.toLong())
	   if(!multimedia)
	   {
		   flash.error = "Cannot find the reocording id."
		   redirect(controller:'user',action:'index')
		   return
	   }
	   
	   def perm = permService.getPerm(multimedia)
	   if(perm?.val <=0)
	   {
		   flash.error = "Permission Denied!"
		   redirect(controller:'user',action:'index')
		   return
	   }
	   
	   return [multimedia:multimedia]
   }
   
   def handleUpload= {
	   
	   //println "handle upload"
	   if(!params.mmid|| !params.file || !params.format)
	   {
		   def msg = "Paramterms are missing."
		   render(contentType:"text/json"){
			   error(stat:APIStatusCode.PARAMS_MISSING, description:msg)
		   }
		   return
	   }
	   
	   def multimedia = MultimediaResource.findById(params.mmid)
	   if(!multimedia)
	   {
		   	def msg = "Cannot find the reocording with id ${params.id}."
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MM_NOT_FOUND, description:msg)
			}
			return
	   }
	   
	   def perm = permService.getPerm(multimedia)
	   if(perm?.val <=0)
	   {
		   	def msg = "Permission Denied"
			render(contentType:"text/json"){
				error(stat:APIStatusCode.MM_PERMISSION_DENIED, description:msg)
			}
			return
	   }
	   
	   if(params.format != "srt" && params.format !="webvtt" && params.format !="dragonidx")
	   {
		   def msg = "Format ${params.format} is invalid."
		   render(contentType:"text/json"){
			   error(stat:APIStatusCode.PARAMS_INVALID, description:msg)
		   }
		   return
	   }
	   
	   try
	   {
		   String text = request.getFile('file').inputStream?.text
		   def currentVTT = webVTTService.getWebVTTResource(String.valueOf(multimedia.id), "")
		   if(params.format == "srt")
		   {
			   //check if srt file is valid
			   webVTTService.createWebVTTResourceFromSRT(multimedia, text)
		   }
		   else if(params.format == "webvtt")
		   {
			   //check if webvtt file is valid
			   webVTTService.createWebVTTResourceFromVTT(multimedia, text)
		   }
		   else if(params.format == "dragonidx")
		   {
			   //check if webvtt file is valid
			   println "dragonidx"
			   webVTTService.createWebVTTResourceFromDragonIdx(multimedia, text)
		   }
		   
		   //delete the old transcript
		   if(currentVTT != null)
		   {
			   log.debug("Delete old transcript")
			   currentVTT.delete()   
		   }
		   
		   def msg= "Transcript has been successful uploaded."
		   render(contentType:"text/json"){
			   success(stat:APIStatusCode.SUCCESS, description:msg)
		   }
		   println "returned"
		   return
	   }
	   catch(PlayerException pex)
	   {
		   def msg = pex.getMessage()
		   render(contentType:"text/json"){
			   error(stat:APIStatusCode.INTERNAL_ERROR, description:msg)
		   }
		   return
	   }
	   catch(Exception ex)
	   {
			ex.printStackTrace()   
	   }
   }
}

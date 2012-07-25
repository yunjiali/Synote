package org.synote.resource.compound

import org.codehaus.groovy.grails.plugins.springsecurity.Secured
import org.synote.resource.ResourceService
import org.synote.linkeddata.LinkedDataService
import org.synote.resource.compound.WebVTTResource
import org.synote.permission.PermService
import org.synote.player.client.WebVTTCueData
import org.synote.player.client.WebVTTData
import org.synote.player.client.TimeFormat


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
		   def item = [
			   id:cue.id,
			   //owner_name:r.owner.userName, Don't need owner_name, it's you!
			   text:webVTTService.getCueText(cue.getCueText()),
			   speaker: webVTTService.getSpeaker(cue.getCueText()),
			   settings: cue.getCueSettings(),
			   mf: linkedDataService.getFragmentString(cue.getStart(), cue.getEnd()),//get media fragment
			   start: cue.getStart() !=null?TimeFormat.getInstance().toString(cue.getStart()):"unknown",
			   end:cue.getEnd() !=null?TimeFormat.getInstance().toString(cue.getEnd()):"unknown",
			   thumbnail:cue.getThumbnail()
		   ]
		   
		   results << item
	   }
	   def jqGridData = [rows:results, page:currentPage, records:totalRows, total:numberOfPages]
	   return [cueList:jqGridData, params:params, multimedia:multimedia,transcript:transcript]
   }
}

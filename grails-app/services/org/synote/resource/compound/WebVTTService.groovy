package org.synote.resource.compound

import org.synote.player.client.TimeFormat
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
import org.synote.player.client.TimeFormat
import org.synote.player.client.TranscriptItemSRT
import org.synote.player.client.WebVTTCueData
import org.synote.player.client.WebVTTData

import org.synote.player.server.PlayerService

import org.synote.resource.compound.WebVTTResource
import org.synote.resource.single.text.WebVTTCue

import org.synote.resource.Resource
import org.synote.resource.single.text.*
import org.synote.resource.single.binary.*
import org.synote.user.User
import org.synote.user.SecurityService
import org.synote.annotation.Annotation
import org.synote.annotation.ResourceAnnotation
import org.synote.annotation.synpoint.Synpoint
import org.synote.user.profile.UserProfile
import org.synote.user.profile.ProfileEntry
import org.synote.permission.PermService

import org.synote.permission.PermissionValue
import org.synote.permission.Permission
import org.synote.permission.ResourcePermission
import org.synote.integration.viascribe.exception.ViascribeException
import org.synote.api.APIStatusCode
import org.apache.commons.logging.LogFactory

class WebVTTService {

    static transactional = true
	private static final log = LogFactory.getLog(this)
	
	def playerService
	def securityService
    /*
	* validate the vtt json objects
	*/
   def validateWebVTTJSON(transcriptsJSON)
   {
	   //not implemented
	   return true
   }
   
   /*
    * validate the cue json object
    */
   def validateWebVTTCueJSON(cueJSON)
   {
		//not implemented
	   return true   
   }
   /*
   * Get WebVTTData of transcript
   */
  WebVTTData[] getTranscripts(multimedia)
  {
	  log.debug("getTranscripts")
	  def annotations = ResourceAnnotation.findAllByTarget(multimedia)
	  
	  def transcripts = []
	  
	  annotations.each {annotation ->
		  if (annotation.source.instanceOf(WebVTTResource) && playerService.canRead(annotation))
		  {
			  def webVTTResource = annotation.source
			  def cues = webVTTResource.cues
			  def cuesData = []
			  
			  annotation.synpoints.sort{synpoint -> synpoint.sourceStart}.each{synpoint->
				  def cue = cues.find{it.cueIndex==synpoint.sourceStart}
				  if(cue)
				  {
					  cuesData << new WebVTTCueData
						  (cue.id.toString(), cue.cueIndex, synpoint.targetStart, synpoint.targetEnd, cue.content,cue.cueSettings,cue.thumbnail)
				  }
			  }
			  
			  transcripts << new WebVTTData( webVTTResource.id.toString() , playerService.getOwner(webVTTResource)
					  , playerService.canEdit(webVTTResource), playerService.canDelete(webVTTResource), (WebVTTCueData[]) cuesData, webVTTResource.fileHeader)
		  }
	  }
	  
	  return transcripts
  }
	  /*
	  * Convert WebVTTResource to WebVTT file
	  */
	 public String convertToWebVTT(WebVTTData vtt)
	 {
		 StringBuilder builder = new StringBuilder()
		 //attach the file header
		 builder.append(vtt.fileHeader)
		 builder.append("\r\n\r\n")
		 
		 def cues =  vtt.cues
		 int i =1
		 cues.sort{it.start}.each{cue->
			 if(cue.cueText != null && cue.cueText?.trim()?.size() != 0)
			 {
				 builder.append(String.valueOf(i++))
				 builder.append("\r\n")
				 builder.append(TimeFormat.getInstance().toWebVTTTimeString(cue.start))
				 builder.append(" --> ")
				 builder.append(TimeFormat.getInstance().toWebVTTTimeString(cue.end))
				 if(!cue.cueSettings.equals(null))
				 {
					 builder.append(" "+cue.cueSettings)
				 }
				 builder.append("\r\n")
				 builder.append(cue.cueText)
				 builder.append("\r\n\r\n")
			 }
		 }
		 return builder.toString()
	 }
 	/*
 	 * Convert WebVTTResource to .srt file
 	 */
 	def convertToSRT(WebVTTData vtt)
	{ 
		StringBuilder builder = new StringBuilder()
		//attach the file header
		
		def cues =  vtt.cues
		int i =1
		cues.sort{it.start}.each{cue->
			if(cue.cueText != null && cue.cueText?.trim()?.size() != 0)
			{
				builder.append(String.valueOf(i++))
				builder.append("\r\n")
				builder.append(TimeFormat.getInstance().toSRTTimeString(cue.start))
				builder.append(" --> ")
				builder.append(TimeFormat.getInstance().toSRTTimeString(cue.end))
				/* No cue settings is needed here
				if(!cue.cueSettings.equals(null))
				{
					builder.append(" "+cue.cueSettings)
				}*/
				builder.append("\r\n")
				builder.append(cue.cueText)
				builder.append("\r\n\r\n")
			}
		}
		return builder.toString()
	}
 
	/*
	 * Convert WebVTT to plain text
	 */
	def convertToText(WebVTTData vtt)
	{
		StringBuilder builder = new StringBuilder()
		//attach the file header
		
		def cues =  vtt.cues
		cues.sort{it.start}.each{cue->
			if(cue.cueText != null && cue.cueText?.trim()?.size() != 0)
			{
				builder.append(cue.cueText)
				builder.append("\r\n\r\n")
			}
		}
		return builder.toString()
	}
	
	/*
	 * save SRT string as new WebVTTResource resource. Create the annotations and save it
	 * make sure the original webvttresource and resource annotation related to it is deleted before calling this method
	 */
	def createWebVTTResourceFromSRT(MultimediaResource multimedia,String srt) throws PlayerException
	{
		def user = securityService.getLoggedUser()
		if(!user)
		{
			throw new PlayerException(APIStatusCode.AUTHENTICATION_FAILED,"User authentication failed.");
		}
		def owner = multimedia.owner
		
		def webVTTResource = new WebVTTResource(owner:owner,fileHeader:"WebVTT", title:"WebVTTResource")
		def anno = new ResourceAnnotation(owner:owner, source:webVTTResource, target: multimedia)
		
		def items = playerService.convertToSRTObjectFromString(srt)
		items.each{item ->
			if(item.getText()?.trim().size() >0)
			{
				def cue = new WebVTTCue(
					content: item.getText()?.trim(),
					owner: owner,
					title:"WebVTTCue",
					cueIndex:item.getIndex(),
					cueSettings:"")
				webVTTResource.addToCues(cue)
				
				def sourceStart = item.getIndex()
				def targetStart = item.getStart()
				def targetEnd = item.getEnd()
				anno.addToSynpoints(new Synpoint(sourceStart: sourceStart,
						targetStart: targetStart,
						targetEnd: targetEnd))
				
				WebVTTResource.withTransaction {status->
					
					if(webVTTResource.hasErrors() || !webVTTResource.save(flush:true))
					{
						 throw new PlayerException(APIStatusCode.TRANSCRIPT_WEBVTT_INVALID ,"Webvtt cannot be saved. Error:"+ webVTTResource.errors.toString())
					}
					
					if(anno.hasErrors() || !anno.save(flush:true))
					{
						throw new PlayerException(APIStatusCode.RESOURCEANNOTATION_CREATEION_ERROR ,"Cannot create annotation. Error:"+ new_anno.errors.toString())
					}
				}
			}
		}
	}
   /*
    * Get WebVTTCue
    */
 	def getWebVTTCueAsString(WebVTTCue cue)
	{
		StringBuilder builder = new StringBuilder()
		//attach the file header
		builder.append(cue.webVTTFile?.fileHeader)
		builder.append("\r\n\r\n")
		
		if(cue.content != null && cue.content?.trim()?.size() != 0)
		{
			builder.append(String.valueOf(cue.cueIndex))
			def annotation = ResourceAnnotation.findBySource(cue.webVTTFile)
			
			def synpoint = annotation?.synpoints?.find{it.sourceStart == cue.cueIndex}
			if(!synpoint)
			{
				return builder.toString()
			}
			builder.append("\r\n")
			builder.append(TimeFormat.getInstance().toWebVTTTimeString(synpoint.targetStart))
			builder.append(" --> ")
			builder.append(TimeFormat.getInstance().toWebVTTTimeString(synpoint.targetEnd))
			if(!cue.cueSettings.equals(null))
			{
				builder.append(" "+cue.cueSettings)
			}
			builder.append("\r\n")
			builder.append(cue.content)
			builder.append("\r\n\r\n")
		}
		return builder.toString()
	}	
   /*
	* Convert vtt json (WebVTTCueData) objects to vtt string
	*/
   def convertJSONToWebVTT(transcriptsJSON)
   {
	   
	   StringBuilder builder = new StringBuilder()
	   builder.append("WebVTT")
	   builder.append("\r\n\r\n");
	   int i = 1
	   //sort it by start time first
	   transcriptsJSON.sort{it.start}.each{cue->
		   if(cue.cueText != null && cue.cueText?.trim()?.size() != 0)
		   {
			   builder.append(i++)
			   builder.append("\r\n")
			   builder.append(TimeFormat.getInstance().toWebVTTTimeString(cue.start))
			   builder.append(" --> ")
			   builder.append(TimeFormat.getInstance().toWebVTTTimeString(cue.end))
			   if(!cue.cueSettings.equals(null))
			   {
				   builder.append(" "+cue.cueSettings)   
			   }
			   builder.append("\r\n")
			   builder.append(cue.cueText)
			   builder.append("\r\n\r\n")
		   }	   
	   }
	   return builder.toString();
   }
   /*
    * convert vtt string to WebVTTCueData[]
    * There is a javascript parser for webvtt, not java parser yet. so I have to write a simple one myself
    * this piece of code is based on srt format, but webvtt is more flexible. The index in cue can be any character
    * and there are also settings at the back of times
    */
   def convertToWebVTTObjectFromString(String vttStr)
   {
	   def cueList = []
	   String[] cueItems = vttStr.split("\\r\\n\\r\\n");
	   if(cueItems.length > 0)
	   {
		   //starts from 1, because 0 is the file heading "WebVTT"
		   //if(cueItems[0] something)
		   //println "###############"
		   //println cueItems[0]
		   //println cueItems[1]
		   for(int i=1;i<cueItems.length;i++)
		   {
			   //split a srt block into 3 parts
			   //first the index
			   //second the time
			   //third the text
			   if(cueItems[i].length() == 0)
			   {
				   continue
			   }
			   String[] cueContent =cueItems[i].split("\\r\\n",3);
			   
			   if(cueContent.length == 3)
			   {
				   int seqCount = Integer.parseInt(cueContent[0]) //the index for srt
				   int startTime=0;
				   int endTime=0;
				   String cueSettings=""
				   
				   //Check the srt time
				   String[] times = cueContent[1].split("-->");
				   if(times.length == 2)
				   {
					   startTime = TimeFormat.getInstance().getWebVTTFormatTime(times[0]);
					   //endTime+cueSettings, we need to separate them
					   String[] etAndSettings = times[1].trim().split("\\s",2)
					   if(etAndSettings.size() == 2)
					   {
						   //println "split 1"
						   //println "0:"+etAndSettings[0]
						   //println "1:"+etAndSettings[1]
						   if(etAndSettings[0] && etAndSettings[0].size() >0) //endtime and cueSettings both are existing
						   {
							   endTime = TimeFormat.getInstance().getWebVTTFormatTime(etAndSettings[0])
							   cueSettings = etAndSettings[1]
						   }
						   //println "st:"+startTime
						   //println "et:"+endTime   
					   }
					   else if(etAndSettings.size() == 1) //only endtime
					   {
						   endTime = TimeFormat.getInstance().getWebVTTFormatTime(etAndSettings[0].trim())
					   }
					   else
					   {
						   throw new PlayerException(APIStatusCode.TRANSCRIPT_DRAFT_INVALID,"Saved draft format error: The string ${times[1]} at index "+ String.valueOf(i)+" is bad formatted.");
					   }
					   if(endTime == -1 || startTime == -1 || startTime > endTime)
					   {
						   //println "1"
						   throw new PlayerException(APIStatusCode.TRANSCRIPT_DRAFT_INVALID,"Saved draft format error: The time format at index "+ String.valueOf(i)+" is bad formatted.");
					   }
				   }
				   else
				   {
					   throw new PlayerException(APIStatusCode.TRANSCRIPT_DRAFT_INVALID, "Saved draft format error: The time format at index "+ String.valueOf(i)+" is bad formatted.");
				   }
				   
				   String text = cueContent[2]
				   WebVTTCueData cue = new WebVTTCueData(seqCount, startTime, endTime, text,cueSettings,null)
				   cueList << cue
			   }
			   else
			   {
				   throw new PlayerException(APIStatusCode.TRANSCRIPT_DRAFT_INVALID,"Saved draft file format error: The content at index "+ String.valueOf(i)+" is bad formatted.");
			   }
		   }
		   
		   return cueList;
	   }
	   else
	   {
		   //Logger.debug("5 return empty transcript");
		   //createEmptyTranscript();
		   throw new PlayerException(APIStatusCode.TRANSCRIPT_DRAFT_INVALID,"Saved draft file format error: The .srt file is not valid.");
	   }
   }
   
   /*
    * Create a new cue from cueJSON (WebVTTCueData)
    * 
    * This method is used for Synote Player only.
    * 
    * No matter who edits the recording, the owner is the multimedia's owner
    */
   def createCueFromJSON(multimedia,webVTTResource, cueJSON)
   {
	   def user = securityService.getLoggedUser()
	   def anno = null
	   def owner = multimedia.owner
	   def i = 1;
	   def cueId= null
	   WebVTTResource.withTransaction {status->
		   
		   if(cueJSON?.cueText?.trim().size()>0)
		   {
			   if(!webVTTResource)
			   {
			   		webVTTResource = new WebVTTResource(owner:owner,fileHeader:"WebVTT", title:"WebVTTResource")
					anno = new ResourceAnnotation(owner:owner, source:webVTTResource, target: multimedia)
			   }
			   else
			   {
					anno = ResourceAnnotation.findBySource(webVTTResource)
					def maxCue = webVTTResource.cues.max{it.cueIndex}
					i = maxCue.cueIndex+1   
			   }
		  
		  
			   def cue = new WebVTTCue(
				   content: cueJSON.cueText?.trim(),
				   owner: owner,
				   title:"WebVTTCue",
				   cueIndex:i,
				   cueSettings:"")
			   webVTTResource.addToCues(cue)
			   
			   def sourceStart = i
			   def targetStart = cueJSON.start
			   def targetEnd = cueJSON.end
			   anno.addToSynpoints(new Synpoint(sourceStart: sourceStart,
					   targetStart: targetStart,
					   targetEnd: targetEnd))
			   
			   if(webVTTResource.hasErrors() || !webVTTResource.save(flush:true))
			   {
					throw new PlayerException(APIStatusCode.TRANSCRIPT_WEBVTT_INVALID ,"Webvtt cannot be saved. Error:"+ webVTTResource.errors.toString())
			   }
			   
			   //println "cueId:"+cue.id
			   cueId = cue.id
			   
			   if(anno.hasErrors() || !anno.save(flush:true))
			   {
				   throw new PlayerException(APIStatusCode.RESOURCEANNOTATION_CREATEION_ERROR ,"Cannot create annotation. Error:"+ new_anno.errors.toString())
			   }
		   }
	   }
   }
   
   /*
    * Edit the cue from cueJSON 
    */
   def editCueFromJSON(multimedia,webVTTResource, cueJSON)
   {
	   
	   def cueIndex = cueJSON.index
	   def cue = WebVTTCue.findByWebVTTFileAndCueIndex(webVTTResource, cueIndex)
	   if(!cue)
	   {
		   throw new PlayerException(APIStatusCode.TRANSCRIPT_WEBVTTCUE_NOT_FOUND ,"Cannot find WebVTT cue with index ${cueIndex}")
	   }
	   
	   def user = securityService.getLoggedUser()
	   def owner = multimedia.owner
	  
	   //Cue settings are not considered here yet
	   if(cue.content != cueJSON.cueText)
	   {
			cue.content = cueJSON.cueText 
			if(cue.hasErrors() || !cue.save(flush:true))
			{
				throw new PlayerException(APIStatusCode.TRANSCRIPT_WEBVTTCUE_INVALID ,"Webvtt cue cannot be saved. Error:"+ cue.errors.toString())
			}
	   }
	   
	   def annotation = ResourceAnnotation.findBySourceAndTarget(webVTTResource, multimedia)
	   if(!annotation)
	   {
		   throw new PlayerException(APIStatusCode.RESOURCEANNOTATION_NOT_FOUND ,"Cannot find ResourceAnnotation about WebVTTResource ${webVTTResource} and multimedia ${multimedia.id}")
	   }
	   def synpoint = Synpoint.findByAnnotationAndSourceStart(annotation,cueIndex)
	   if(!synpoint)
	   {
		   throw new PlayerException(APIStatusCode.TRANSCRIPT_WEBVTTCUE_SYNPOINT_NOT_FOUND ,"Cannot find Synchronisation point for cue with index ${cueIndex}")
	   }
	   
	   if(cueJSON.start != synpoint.targetStart || cueJSON.end != synpoint.targetEnd)
	   {
		   synpoint.targetStart = cueJSON.start
		   synpoint.targetEnd = cueJSON.end   
		   if(synpoint.hasErrors() || !synpoint.save(flush:true))
		   {
			   throw new PlayerException(APIStatusCode.TRANSCRIPT_WEBVTTCUE_INVALID ,"Webvtt cue cannot be saved. Error:"+ synpoint.errors.toString())
		   }
	   }
   }

   //transcriptId is used to get a specific transcript if there are multiple transcripts
   def getWebVTTResource(String multimediaId, String transcriptId)
   {
	   def multimedia = MultimediaResource.get(multimediaId.toLong())
	   def annotations = ResourceAnnotation.findAllByTarget(multimedia)
	   def webVTTResource = null
	   for(Annotation a in annotations)
	   {
		   if(a.source.instanceOf(WebVTTResource))
		   {
			   webVTTResource = a.source
			   break;
		   }
	   }
	   return webVTTResource
   }
   
   def deleteWebVTTResource(vttId)
   {
	   def webVTTResource = WebVTTResource.findById(vttId)
	   webVTTResource.delete(flush:true)
	   //Yunjia:implement thoroughly later
   }
   
   //#########################Get functions to extract information from cue############################
   /*
   * Add these functions later:
   */
   public String getVerticalText(String cueSettings)
   {
	   return "";
   }
   public String getLinePosition(String cueSettings)
   {
	   return "";
   }
   public String getTextPosition(String cueSettings)
   {
	   return "";
   }
   public String getTextSize(String cueSettings)
   {
	   return "";
   }
   public String getTextAlignment(String cueSettings)
   {
	   return "";
   }
   public String getSpeaker(String cueText)
   {
	  //parse the cueText and get the speaker string
	   return "";
   }
}

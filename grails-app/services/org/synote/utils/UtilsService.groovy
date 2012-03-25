package org.synote.utils

import org.synote.player.client.TimeFormat
import java.sql.Timestamp
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Calendar
import java.net.URI

/*
 * Handle utils programmes that used across the server
 */
class UtilsService {

    static transactional = true
	def static AUDIO_FORMAT_LIST = ["mp3","aac","m4a","ogg","wav","wma","mid","midi"] 	
		
    /*
	* Map jqgrid params to grails' params
	*/
   def mapJQGridParamsToGrails(jqGridParams)
   {
	   def gParams = [:]
	   
	   gParams.sort = jqGridParams.sidx ?: 'id'
	   gParams.order  = jqGridParams.sord ?: 'asc'
	   def maxRows = Integer.valueOf(jqGridParams.rows)
	   gParams.max= maxRows
	   def currentPage = Integer.valueOf(jqGridParams.page) ?: 1
	   gParams.offset = currentPage == 1 ? 0 : (currentPage - 1) * maxRows
	   
	   return gParams
   }
   
   /*
    * The programme to help you attach media fragment information at the back of a uri
    * ATTENTION: the mfStr must be a media fragment string, which is key=value format
    */
   def attachFragmentToURI(uriStr, mfStr)
   {
	   if(mfStr && mfStr?.trim().size()>0)
	   {
		   if(uriStr.indexOf("#")!=-1)
		   {
			   return uriStr+"&"+mfStr
		   }
		   else
		   {
				return uriStr+"#"+mfStr   
		   }
	   }
	   else
	   		return uriStr
   }
   
   /*
   * The programme to help you attach media fragment information at the back of a uri for Google Ajax Crawl
   * ATTENTION: the mfStr must be a media fragment string, which is key=value format
   */
  def attachFragmentToURIForGoogleAjaxCrawl(uriStr, mfStr)
  {
	  if(mfStr && mfStr?.trim().size()>0)
	  {
		  if(uriStr.indexOf("#")!=-1)
		  {
			  if(uriStr.indexOf("#!")!=-1)
			  		return uriStr+"&"+mfStr
			  else
			  {
			  		String uglyURI = uriStr.replaceFirst("#","#!")
				  	return uglyURI+"&"+mfStr
			  }
		  }
		  else
		  {
			   return uriStr+"#!"+mfStr
		  }
	  }
	  else
			  return uriStr
  }
   
   /*
    * parse media fragment string. No official library has been developed yet, I just write my own one for test reason
    * return json object
    */
   def parseMediaFragment(String frag)
   {
	   //Do nothing
   }
   
   /*
    * only parse the time fragment, the expected incoming format is "t=00:00:30.123,00:00:33.222"
    */
   def parseTimeFragment(String timeFrag)
   {
		if(!timeFrag)
		{
			return [start:0,end:Integer.MAX_VALUE-1]	
		}
		
		if(!timeFrag.startsWith("t="))
		{
			return [start:0,end:Integer.MAX_VALUE-1]
		}
		
		timeFrag = timeFrag.replaceFirst("t=","")
		
		def timeArray = timeFrag.split(",")
		if(timeArray?.size()>2)
		{
			return [start:0,end:Integer.MAX_VALUE-1]
		}
		def start = 0
		def end = Integer.MAX_VALUE-1
		
		if(timeArray.size() == 2)
		{
			
			if(timeArray[1]?.trim()?.size() > 0)
			{
				end = TimeFormat.getInstance().getWebVTTFormatTime(timeArray[1])
			}
		}
		
		if(timeArray[0]?.trim()?.size() > 0)
		{
			start = TimeFormat.getInstance().getWebVTTFormatTime(timeArray[0])
		}
		//println start+"-->"+end
		return [start:start,end:end]
   }
   
   def convertSQLTimeStampToFormattedTimeString(Timestamp time, String format)
   {
	   if(time == null || format == null)
	   		return null
	   def cal = Calendar.getInstance()
	   cal.setTimeInMillis(time.getTime())
	   SimpleDateFormat formatter = new SimpleDateFormat(format)
	   return formatter.format(cal.getTime())
   }
   
   /*
    * decide if a url points to an actual media file or it is a embedded player
    */
   def isMediaFile(String urlStr)
   {
		try
		{
			def uri= new URI(urlStr)
			if(uri.getRawPath().indexOf(".") == -1)
			{
				return false
			}
			else
				return true
		}
		catch(Exception ex) //urlStr is null, or urlStr is not a valid URI
		{
			return false	
		}
   }
   
   /*
   * decide if a url points to a video or audio file
   */
   def isVideo(String urlStr)
   {
	   try
	   {
		   def uri= new URI(urlStr)
		   if(uri.getRawPath().indexOf(".") == -1)
		   {
			   return true
		   }
		   else
		   {
			   int i = uri.getRawPath().lastIndexOf(".")
			   if(i == -1)
			   		return true
			   
			   String fileExt = uri.getRawPath().substring(i+1)//"." character should not be included
			   //println fileExt
			   if(AUDIO_FORMAT_LIST.contains(fileExt?.trim().toLowerCase()))
			   {
					return false   
			   }
			   else
			   		return true
		   }
	   }
	   catch(Exception ex) //urlStr is null, or urlStr is not a valid URI
	   {
		   return true
	   }
   }
   
   /*
    * Get the encoding format for the file mp3,mp4,mov,etc. Return a string.
    */
   def getEncodingFormat(String urlStr)
   {
	   try
	   {
		   def uri= new URI(urlStr)
		   if(uri.getRawPath().indexOf(".") == -1)
		   {
			   return null
		   }
		   else
		   {
			   int i = uri.getRawPath().lastIndexOf(".")
			   if(i == -1)
			   		return null
			   
			   String fileExt = uri.getRawPath().substring(i+1)//"." character should not be included
			   //println fileExt
			   return fileExt
		   }
	   }
	   catch(Exception ex) //urlStr is null, or urlStr is not a valid URI
	   {
		   return null
	   }
   }
}

package org.synote.taglib

import org.synote.user.SecurityService
import org.synote.permission.PermService
import org.synote.config.ConfigurationService
import org.synote.user.User
import org.synote.resource.compound.MultimediaResource
import org.synote.resource.compound.SynmarkResource
import org.synote.resource.single.text.*
import org.synote.user.group.UserGroup
import org.synote.integration.ibmhts.IBMTransJobService
import org.synote.linkeddata.LinkedDataService
import org.synote.utils.UtilsService
import org.synote.utils.DatabaseService
import java.util.GregorianCalendar
import org.synote.analysis.Views

class SynoteTagLib {
	static namespace = "syn"
	
	def securityService
	def permService
	def configurationService
	def iBMTransJobService
	def linkedDataService
	def utilsService
	def databaseService
	
	String[] monthLongName = ["January","February","March", "April", "May", "June", "July","August", "September", "October", "November","December"]
	String[] monthShortName = ["Jan","Feb","Mar", "Apr", "May", "Jun", "Jul","Aug", "Sep", "Oct", "Nov","Dec"]
	
	def isLoggedIn = {attrs, body ->
		if(securityService.isLoggedIn())
			out << body()
	}
	
	def isNotLoggedIn = {attrs,body->
		if(securityService.isNotLoggedIn())	
			out << body()
	}
	
	def isNormalLoggedIn = {attrs, body ->
		if (securityService.isNormalLoggedIn())
		out << body()
	}
	
	def isAdminLoggedIn = {attrs, body ->
		if (securityService.isAdminLoggedIn())
		out << body()
	}
	
	def isAdminNotLoggedIn = {attrs, body ->
		if (securityService.isLoggedIn() && !securityService.isAdminLoggedIn())
		out << body()
	}
	
	def isOwnerOrAdmin = {attrs, body ->
		def owner = attrs.owner
		if (owner)
		{
			if (securityService.isOwnerOrAdmin(owner))
				out << body()
		}
	}
	
	def isNotOwnerOrAdmin = {attrs, body ->
		def owner = attrs.owner
		if (owner)
		{
			if (!securityService.isOwnerOrAdmin(owner))
			out << body()
		}
	}
	
	def getLoggedId = {
		out << securityService.getLoggedUser()?.id
	}
	
	def loggedInUsername = {
		out << securityService.getLoggedUser()?.userName	
	}
	
	def formatOwner = {attrs ->
		def owner = attrs.owner
		
		out << owner.firstName
		if (owner.lastName.size() > 0)
		{
			out << " "
			out << owner.lastName.charAt(0)
			out << "."
		}
	}
	
	def printPerm = {attrs ->
		def resource = attrs.resource
		def entity = attrs.entity
		
		out << permService.getPerm(resource, entity)?.toString()?.getAt(0)
	}
	
	def formatTime = {attrs ->
		out << formatTimePrivate(attrs.startTime, attrs.endTime)
	}
	
	def printTime = { attrs ->
		try
		{
			int time = Integer.parseInt(attrs.time.toString())
			out << formatTimePrivate(time)
		}
		catch(NumberFormatException nex)
		{
			out << attrs.time	
		}	
	}
	
	def printEndTime = {attrs ->
		def synpoint = attrs.synpoint
		def synpoints = attrs.synpoints
		def index = synpoints.indexOf(synpoint)
		
		attrs.ends.each {time ->
			if (synpoint.targetStart < time && (index + 1 == synpoints.size() || synpoints[index + 1].targetStart >= time))
			out << formatTimePrivate(time, null)
		}
	}
	
	def allowRegistering = { attrs, body ->
		def allowRegistering = Boolean.parseBoolean(configurationService.getConfigValue("org.synote.user.register.enable"))
		if(allowRegistering)
			out << body()
	}
	
	def twitterEnabled = {attrs,body->
		def twitterEnabled = Boolean.parseBoolean(configurationService.getConfigValue("org.synote.integration.twitter.enabled"))
		if(twitterEnabled)
			out << body()
	}
	
	def forgetPasswordEnabled = {attrs,body->
		def forgetPasswordEnabled = Boolean.parseBoolean(configurationService.getConfigValue("org.synote.user.forgetPassword.enabled"))
		if(forgetPasswordEnabled)
			out << body()
	}
	
	def ibmhtsEnabled = {attrs,body->
		//Check the enable field as well as the ipaddress
		boolean isAllowedIPAddress = securityService.isAllowedIPAddress(request.remoteAddr)
		def ibmhtsEnabled = Boolean.parseBoolean(configurationService.getConfigValue("org.synote.integration.ibmhts.enabled"))
		if(ibmhtsEnabled && isAllowedIPAddress)
			out << body()
	}
	
	def ibmhtsAddingJobEnabled = {attrs,body->
		boolean ibmhtsAddingJobEnabled = IBMTransJobService.getAllowAddingJobs()
		if(ibmhtsAddingJobEnabled)
		{
			out << body()	
		}
	}
	
	def fileUploadEnabled = {attrs,body->
		def fileUploadEnabled = Boolean.parseBoolean(configurationService.getConfigValue("org.synote.resource.fileUpload.enabled"))
		if(fileUploadEnabled)
			out << body()
	}
	
	def viascribeXmlUploadEnabled = {attrs,body->
		def viascribeXmlUploadEnabled = Boolean.parseBoolean(configurationService.getConfigValue("org.synote.integration.viascribe.xmlUpload.enabled"))
		if(viascribeXmlUploadEnabled)
			out << body()
	}
	
	def captchaEnabled = {attrs,body->
		def captchaEnabled = Boolean.parseBoolean(configurationService.getConfigValue("org.synote.user.register.captcha.enabled"))
		if(captchaEnabled)
			out << body()
	}
	
	//Get the page content from the configuration name, used for termsAndConditions page or contact page, etc
	def getContentFromConfig = {attrs,body->
		def tcString = configurationService.getConfigValue(attrs.name?.toString())
		if(tcString)
			out<<tcString
	}
	
	//Get the number of Users
	def getUserCount = {
		def userCount = User.count()
		out << String.valueOf(userCount)	
	}
	
	//Get the number of Users
	def getRecordingCount = {
		def recordingCount = MultimediaResource.count()
		out << String.valueOf(recordingCount)
	}
	
	//Get the number of Users
	def getSynmarkCount = {
		def synmarkCount = SynmarkResource.count()
		out << String.valueOf(synmarkCount)
	}
	//Get the number of Users
	def getGroupCount = {
		def groupCount = UserGroup.count()
		out << String.valueOf(groupCount)
	}
	
	//Get latest Recordings
	def getLatestRecordings = { attrs ->
		if(!attrs.rows)
			attrs.rows = 5
		
		def g = new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()
			
		def options =[max:attrs.rows,sort:"date_created",order:"desc"] 
		def mList = databaseService.listMultimedia(options)
		def cal = GregorianCalendar.getInstance()
		mList.each{m->
			def replayPath = g.createLink(controller:'recording',action:'replay',id:m.id)
			def res = MultimediaResource.get(m.id)
			cal.setTimeInMillis(m.date_created.getTime())
			//<p class="calendar">7 <em>Feb</em></p>
			out << "<div class='span1'>"
			out << "<p class='calendar'>"+cal.get(GregorianCalendar.DAY_OF_MONTH)+"<em>"+monthShortName[cal.get(GregorianCalendar.MONTH)]+"</em></p></br>"
			out << "</div>"
			out << "<div class='span2'>"
			def displayTitle = m.title?.size() > 70?m.title.substring(0,70):m.title
			out << "<a style='text-decoration:none;' href='${replayPath}' target='_blank' title='replay ${m.title}'>"+displayTitle+"</a><br/>"
			out << "<span>By<i> "+m.owner_name+"</i></span><br/>"
			out << "<span>"+Views.countByResource(res)+" Views</span>"
			out << "</div>"	
		}
	}
	
	//Display SQL time stamp on the webpage with a certain format
	def printSQLTime = { attrs ->
		if(!attrs.format)
		{
			attrs.format = "dd.MM.yyyy HH:mm"
		}
		if(attrs.datetime)
		{
			out << utilsService.convertSQLTimeStampToFormattedTimeString(attrs.datetime,attrs.format);
		}
	}
	
	def printFullTextFromResource = {attrs->
		if(attrs.resource)
		{
			def resource = attrs.resource
			StringBuilder builder = new StringBuilder()
			if(resource.instanceOf(MultimediaResource) || resource.instanceOf(SynmarkResource))
			{
				builder.append("<b>Title</b>: <br/>")
				builder.append(resource.title)
				
				if(resource.tags)
				{
					builder.append("<br/><b>Tags</b>: <br/>")
					resource.tags.each{tag->
						int i=0
						if(i>0)
							builder.append(",")
						builder.append(tag.content)
						i++
					}
				}
				
				if(resource.note)
				{
					builder.append("<br/><b>Note</b>: <br/>")
					builder.append(resource.note?.content)
				}
			}
			else if(resource.instanceOf(TagResource)||resource.instanceOf(TextNoteResource) || resource.instanceOf(WebVTTCue))
			{
				builder.append(resource.content)
			}
			out << builder.toString()
		}
	}
	
	private String formatTimePrivate(Integer start, Integer end)
	{
		def result = '['
		
		if (start)
		result += formatTimePrivate(start)
		else
		result += formatTimePrivate(0)
		
		if (end)
		{
			result += ' - '
			result += formatTimePrivate(end)
		}
		
		result += ']'
		
		return result
	}
	
	private String formatTimePrivate(int time)
	{
		int seconds = time / 1000;
		
		int hours = seconds / 3600;
		seconds -= hours * 3600;
		
		int minutes = seconds / 60;
		seconds -= minutes * 60;
		
		if (hours == 0 && minutes == 0)
		return "00:00:" + formatNumber(seconds);
		else if (hours == 0)
		return "00:"+formatNumber(minutes) + ":" + formatNumber(seconds);
		else
		return hours + ":" + formatNumber(minutes) + ":" + formatNumber(seconds);
	}
	
	private String formatNumber(int number)
	{
		return (number < 10) ? "0" + Integer.toString(number) : Integer.toString(number);
	}
	
	//###########################################linked data tags#######################################
	def getUserURI = {attrs->
		//println "userId:"+userId
		if(attrs.userId)
		{
			out << linkedDataService.getUserBaseURI()+attrs.userId
		}
	}
	
	def getUserDataURI = {attrs->
		if(attrs.userId)
		{
			out << linkedDataService.getUserDataBaseURI()+attrs.userId
		}
	}
	
	def getResourceURI = {attrs->
		if(attrs.resourceId)
		{
			out << linkedDataService.getResourceBaseURI()+attrs.resourceId
		}
	}
	
	def getResourceURIWithFragment = {attrs->
		String resourceURI = ""
		if(attrs.resourceId)
		{
			resourceURI = linkedDataService.getResourceBaseURI()+attrs.resourceId
		}
		out << utilsService.attachFragmentToURI(resourceURI,linkedDataService.getFragmentStringFromSynpoint(attrs.synpoint))
	}
	
	def getResourceDataURI = {attrs->
		if(attrs.resourceId)
		{
			out << linkedDataService.getResourceDataBaseURI()+attrs.resourceId
		}
	}
}

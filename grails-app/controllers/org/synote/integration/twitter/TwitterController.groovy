package org.synote.integration.twitter

import org.xml.sax.SAXParseException
import grails.converters.*
import groovy.util.XmlSlurper
import java.net.*
import java.util.Date
import java.util.Calendar
import java.util.Locale
import java.text.SimpleDateFormat

import org.synote.integration.twitter.Twitter
import org.synote.user.SecurityService
import org.synote.permission.PermService
import org.synote.permission.PermissionValue
import org.synote.integration.viascribe.ViascribeService
import org.synote.resource.compound.SynmarkResource
import org.synote.resource.compound.MultimediaResource
import org.synote.resource.single.text.SynmarkTag
import org.synote.resource.single.text.SynmarkTextNote
import org.synote.annotation.ResourceAnnotation
import org.synote.annotation.synpoint.Synpoint
import org.synote.user.User

class TwitterController 
{
	def beforeInterceptor = [action: this.&auth]
	
	def securityService
	def permService
	def viascribeService
	
	private auth()
	{
		if(!securityService.isLoggedIn())
		{
			session.requestedController = controllerName
			session.requestedAction = actionName
			session.requestedParams = params
			
			flash.message = "User login required"
			redirect(controller: 'login', action: 'auth')
			
			return false
		}
		
	}
	
	private rightDate (date)
	{
		def b = Integer.parseInt(date)
		if (b >= 0 && b  <= 9){
			return '0' + date
		}
		else return date
	}
	
	private rightTime(date)
	{
		def dateString = "dd MM yyyy HH:mm:ss"
		def format = new SimpleDateFormat(dateString)
		
		def b = "25 10 2009 02:00:00"
		def bTime = format.parse(b)
		
		if (date < bTime )
		{
			def cal = Calendar.getInstance()
			cal.setTime(date)
			cal.add(Calendar.HOUR, -1)
			return cal.getTime()
		}
		else return date
	}
	
	private rightTweetTime(date)
	{
		def dateString = "dd MM yyyy HH:mm:ss"
		def format = new SimpleDateFormat(dateString)
		
		def b = "25 10 2009 02:00:00"
		def bTime = format.parse(b)
		
		if (date < bTime )
		{
			def cal = Calendar.getInstance()
			cal.setTime(date)
			cal.add(Calendar.HOUR, 1)
			return cal.getTime()
		}
		else return date
	}
	
	private createSynmark (recordingDate, tweetDate, tweetText, multimediaResource)
	{
		def recTime = recordingDate
		def tweet = tweetText
		def tweetTime = tweetDate
		
		def user = securityService.getLoggedUser()
		
		def dateString = "EEE MMM dd HH:mm:ss zzz yyyy"
		def format = new SimpleDateFormat(dateString, Locale.UK)
		
		def tDate = format.parse(tweetTime)
		def rDate = format.parse(recTime)
		
		def time = tDate.getTime() - rDate.getTime()
		
		def time1 = time.intValue()
		
		def synmark = new SynmarkResource
		( owner: user
		, note: new SynmarkTextNote(owner:user, content:tweet)
		)
		
		if (!synmark.save()){
			flash.error = "Cannot save Synmark annotation"
			render (view:'save')
		}
		if(!synmark){
			return
		}
		
		synmark.index()
		
		if (time1 >= 0)
		{
			def annotation = new ResourceAnnotation(owner:user, source:synmark, target:multimediaResource)
			
			annotation.addToSynpoints(new Synpoint(targetStart:time1))
			
			if (!annotation.save()){
				flash.error = "Cannot create Synmark annotation."
				render (view:'save')
			}
			else {
				flash.message = "Synmark annotation saved."
				render (view:'save')
			}
		}
		else
		{
			def start = 0
			def annotation = new ResourceAnnotation(owner:user, source:synmark, target:multimediaResource)
			
			annotation.addToSynpoints(new Synpoint(targetStart:start))
			
			if (!annotation.save()){
				flash.error = "Cannot create Synmark annotation."
				render (view:'save')
			}
			else {
				flash.message = "Synmark annotation saved."
				render (view:'save')
			}
		}
		
		return
	}
	
	def index = {
		redirect(action: create, params: params)
	}
	
	def create = {
		
		def multimediaResource = MultimediaResource.get(params.id)
		if (!multimediaResource)
		{
			flash.error = "Multimedia with id ${params.id} not found"
			redirect(controller: 'multimediaResource', action: 'list')
			return
		}
		
		User user = securityService.getLoggedUser()
		
		if (user && permService.getPerm(multimediaResource)?.val < PermissionValue.findByName("READ")?.val)
		{
			flash.error = "Permission denied - cannot show multimedia with id ${params.id}"
			redirect(controller: 'multimediaResource', action: 'list')
			return
		}
		
		return [multimediaResource: multimediaResource]
	}
	
	def list = {
		
		def multimediaResource = MultimediaResource.get(params.multimediaResourceId)
		def twitter_userName = params.twitter_userName
		def twitter_hashTag = params.twitter_hashTag.replace("#", "%23")
		
		if (!twitter_userName && !twitter_hashTag)
		{
			flash.error = "Enter either Twitter Username or Twitter hashtag."
			redirect (action:'create', id:multimediaResource.id)
			return
		}
		
		def recStDay = rightDate(params["startTime_Rec_day"])
		def recStMonth = rightDate(params["startTime_Rec_month"])
		def recStYear = params["startTime_Rec_year"]
		def recStHour = params["startTime_Rec_hour"]
		def recStMinute = params["startTime_Rec_minute"]
		def recStSecond = rightDate(params["startTime_RecSec"])
		
		/*	def recEtDay = rightDate(params["endTime_Rec_day"])
		 def recEtMonth = rightDate(params["endTime_Rec_month"])
		 def recEtYear = params["endTime_Rec_year"]
		 def recEtHour = params["endTime_Rec_hour"]
		 def recEtMinute = params["endTime_Rec_minute"]
		 def recEtSecond = rightDate(params["endTime_RecSec"])
		 */
		def tweetStDay = rightDate(params["startTime_Tweet_day"])
		def tweetStMonth = rightDate(params["startTime_Tweet_month"])
		def tweetStYear = params["startTime_Tweet_year"]
		def tweetStHour = params["startTime_Tweet_hour"]
		def tweetStMinute = params["startTime_Tweet_minute"]
		def tweetStSecond = rightDate(params["startTime_TweetSec"])
		
		def tweetEtDay = rightDate(params["endTime_Tweet_day"])
		def tweetEtMonth = rightDate(params["endTime_Tweet_month"])
		def tweetEtYear = params["endTime_Tweet_year"]
		def tweetEtHour = params["endTime_Tweet_hour"]
		def tweetEtMinute = params["endTime_Tweet_minute"]
		def tweetEtSecond = rightDate(params["endTime_TweetSec"])
		
		def recTime = "$recStDay" + ' ' + "$recStMonth" + ' ' + "$recStYear" + ' ' + "$recStHour" + ':' + "$recStMinute" + ':' + "$recStSecond"
		
		def tweetDate = "$tweetStYear" + '-' + "$tweetStMonth" + '-' + "$tweetStDay"
		def startTime_Tweet = "$tweetStDay" + ' ' + "$tweetStMonth" + ' ' + "$tweetStYear" + ' ' + "$tweetStHour" + ':' + "$tweetStMinute" + ':' + "$tweetStSecond"
		def endTime_Tweet = "$tweetEtDay" + ' ' + "$tweetEtMonth" + ' ' + "$tweetEtYear" + ' ' + "$tweetEtHour" + ':' + "$tweetEtMinute" + ':' + "$tweetEtSecond"
		
		def twitterUrl = "http://twitter.com/statuses/user_timeline.xml?count=200&screen_name=" + "$twitter_userName"
		def twitterUrl2 = "http://search.twitter.com/search.atom?q=" + "$twitter_hashTag"
		def twitterURI = "http://twitter.com/" + "$twitter_userName"
		
		if (!twitter_hashTag)
		{
			def twitterConnection = new URL(twitterUrl)
			URLConnection tc = twitterConnection.openConnection()
			
			if(tc.responseCode != 200) {
				flash.error = "Twitter username or date is incorrect. Unable to get the tweets. Please enter correct Twitter username and recent date."
				redirect (action:'create', id:multimediaResource.id)
				return
			}
			else {
				String tweet = tc.content.text
				def tweetList = []
				
				def tXml = new XmlSlurper().parseText(tweet)
				tXml.'status'.each(){result ->
					def text = result.'text'.text()
					def date = result.'created_at'.text()

					println "date:"+date
					def tweetDateString = "EEE MMM dd HH:mm:ss ZZZZ yyyy"
					def format = new SimpleDateFormat(tweetDateString,Locale.UK)
					def tDate = format.parse(date)
					
					def synoteDateString = "dd MM yyyy HH:mm:ss"
					def format2 = new SimpleDateFormat(synoteDateString)
					
					def sTime = format2.parse(startTime_Tweet)
					def eTime = format2.parse(endTime_Tweet)
					def rTime = format2.parse(recTime)
					
					def startTime = rightTime(sTime)
					def endTime = rightTime(eTime)
					if (tDate >= startTime && tDate <= endTime)
					{
						def tweetTime = rightTweetTime(tDate)
						
						def tweet1 = new Twitter (tweet: text, tweetDate:tweetTime, recordingDate: rTime, multimediaResourceId: multimediaResource.id)
						tweetList << tweet1
					}
				}
				
				if (tweetList.size == 0)
				{
					flash.error = "No tweets present in the list. Please select the correct date and time to get the right tweets."
					redirect (action:'create', id:multimediaResource.id)
					return
				}
				else
				{
					render (view:'list', model:[tweetList:tweetList])
					return
				}
			}
		}
		else
		{
			
			def twitterConnection = new URL(twitterUrl2)
			URLConnection tc = twitterConnection.openConnection()
			
			if(tc.responseCode != 200) {
				flash.error = "Twitter hashtag or date is incorrect. Unable to get the tweets. Please enter correct Twitter hashtag and recent date."
				redirect (action:'create', id:multimediaResource.id)
				return
			}
			else {
				String tweet = tc.content.text
				def tweetList = []
				
				def tXml = new XmlSlurper().parseText(tweet)
				tXml.'entry'.each(){result ->
					def date = result.'published'.text().replaceAll(/[A-Z]/, " ")
					def text = result.'title'.text() + "\ncreated by: " + result.'author'.'name'.text()
					def usr = result.'author'.'uri'.text()
					
					def tweetDateString = "yyyy-MM-dd HH:mm:ss"
					def format = new SimpleDateFormat(tweetDateString)
					def tDate = format.parse(date)
					
					def synoteDateString = "dd MM yyyy HH:mm:ss"
					def format2 = new SimpleDateFormat(synoteDateString)
					
					def sTime = format2.parse(startTime_Tweet)
					def eTime = format2.parse(endTime_Tweet)
					def rTime = format2.parse(recTime)
					
					def startTime = rightTime(sTime)
					def endTime = rightTime(eTime)
					if (!twitter_userName)
					{
						if (tDate >= startTime && tDate <= endTime)
						{
							def tweetTime = rightTweetTime(tDate)
							
							def tweet1 = new Twitter (tweet: text, tweetDate: tweetTime, recordingDate: rTime, multimediaResourceId: multimediaResource.id)
							tweetList << tweet1
						}
					}
					else
					{
						if (tDate >= startTime && tDate <= endTime && usr == twitterURI)
						{
							def tweetTime = rightTweetTime(tDate)
							
							def tweet1 = new Twitter (tweet: text, tweetDate: tweetTime, recordingDate: rTime, multimediaResourceId: multimediaResource.id)
							tweetList << tweet1
						}
					}
					
				}
				
				if (tweetList.size == 0)
				{
					flash.error = "No tweets present in the list. Please select the correct date and time to get the right tweets."
					redirect (action:'create', id:multimediaResource.id)
					return
				}
				else
				{
					render (view:'list', model:[tweetList:tweetList])
					return
				}
			}
		}
	}
	
	def save={
		try{
			def tweetCount = Integer.parseInt(params.tweetCount)
			
			if(tweetCount == 0)
			{
				flash.error = "No tweets present to convert into Synmark. Please select the correct date and time to get the right tweets."
				render (view:'save')
				return
			}
			else
			{
				int i = 0
				while ( i < tweetCount)
				{
					def check = 'checkbox_' + "$i"
					if (params."$check")
					{
						def a = 'tdate_' + "$i"
						def b = 'tweet_' + "$i"
						def recordingDate = (params.rdate_0)
						def tweetDate = (params."$a")
						def tweet = (params."$b")
						def multimediaResource = MultimediaResource.get(params.mid_0)
						
						createSynmark(recordingDate,tweetDate,tweet,multimediaResource)
					}
					i++
				}
				return
			}
		}
		catch (NumberFormatException e)
		{
			flash.error = "No tweets present to convert into Synmark. Please select the correct date and time to get the right tweets."
			render (view:'list')
			return
		}
	}
}

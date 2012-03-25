package org.synote.integration.ibmhts

import java.net.URL
import groovy.util.XmlSlurper
import java.io.File
import org.codehaus.groovy.grails.commons.*
import java.net.ConnectException
import java.net.UnknownHostException
import java.io.IOException
import groovy.xml.*
import java.util.Calendar
import java.util.Locale
import java.text.SimpleDateFormat

import org.synote.user.SecurityService
import org.synote.integration.viascribe.ViascribeService
import org.synote.utils.RegExService
import org.synote.resource.Resource
import org.synote.integration.ibmhts.exception.IBMTransJobException
import org.synote.integration.ibmhts.IBMTransJob
import org.synote.integration.ibmhts.IBMTransJobStatus
import org.synote.config.ConfigurationService

import org.synote.integration.ibmhts.jobs.*

class IBMTransJobService {

	def securityService
	def viascribeService
	def regExService
	def quartzScheduler
	def configurationService
	
	boolean transactional = true
	
	//A flag to show if IBM HTS has been connected. Default is false
	boolean connected = false
	
	//A flag to show if it is allowed to add job into IBM HTS. Default is false. This value can only be set to true manually after
	//the service on IBM HTS is restarted sucessfully manually.
	boolean allowAddingJobs = false
	
	//The current login userID
	String userID
	
	def getIBMServerUrl() throws IBMTransJobException
	{
		String urlStr = getIBMServerProtocol()+getIBMServerName()+":"+getIBMServerPort()+getIBMServerAppPath()
		if(!regExService.isUrl(urlStr))
		{
			throw new IBMTransJobException("New application url ${urlStr} is not valid!")	
		}
		else
		{
			return urlStr	
		}
	}
	
	def getIBMServerProtocol()
	{
		return configurationService.getConfigValue("org.synote.integration.ibmhts.server.protocol")
	}
	
	def setIBMServerProtocol(String protocol)
	{
		configurationService.setConfigValue("org.synote.integration.ibmhts.server.protocol", protocol)	
	}
	
	def getIBMServerName()
	{
		return configurationService.getConfigValue("org.synote.integration.ibmhts.server.name")
	}
	
	def setIBMServerName(String name)
	{
		configurationService.setConfigValue("org.synote.integration.ibmhts.server.name", name)
	}
	
	def getIBMServerPort()
	{
		return configurationService.getConfigValue("org.synote.integration.ibmhts.server.port")
	}
	
	def setIBMServerPort(String port)
	{
		configurationService.setConfigValue("org.synote.integration.ibmhts.server.port", port)
	}
	
	def getIBMServerAppPath()
	{
		return configurationService.getConfigValue("org.synote.integration.ibmhts.server.appPath")
	}
	
	def setIBMServerAppPath(String appPath)
	{
		configurationService.setConfigValue("org.synote.integration.ibmhts.server.appPath", appPath)	
	}
	
	def getUsername()
	{
		return configurationService.getConfigValue("org.synote.integration.ibmhts.server.username")
	}
	
	def getPassword()
	{
		return configurationService.getConfigValue("org.synote.integration.ibmhts.server.password")
	}
	
	/*
	 * Return a String
	 */
	def getIBMTransJobEnabled()
	{
		return configurationService.getConfigValue("org.synote.integration.ibmhts.enabled")
	}
	
	def setIBMTransJobEnabled(String enabled)
	{
		configurationService.setConfigValue("org.synote.integration.ibmhts.enabled", enabled)
		boolean enable = Boolean.valueOf(enabled)
		if(enable)
		{
			//TODO: write these params into database
			//Start checking job
			log.info "IBM HTS service has been enabled."
			
			long interval = Long.parseLong(getDowntimeCheckInterval())
			log.info "downtime interval check set to ${interval}"
			//CheckDowntimeJob.scheduleJob()
			CheckDowntimeJob.schedule(interval,-1)
			//CheckDowntimeJob.triggerNow()
			log.info "Check down time job has been triggered."
		}
		else
		{
			//Stop checking job
			log.info("IBM HTS service has been disabled.")
			//CheckDowntimeJob.removeJob()
			quartzScheduler.pauseJob(CheckDowntimeJob.class.getName(), "IBMHTS")
			setConnected(false)
			setAllowAddingJobs(false)
		}
	}
	
	def setConnected(boolean hasConnected)
	{
		log.debug "Set connected to:"+hasConnected
		connected = hasConnected
	}
	
	def getConnected()
	{
		return connected
	}
	
	def getSourceDir()
	{
		return configurationService.getConfigValue("org.synote.integration.ibmhts.server.sourceDir")
	}
	
	def setSourceDir(String sourceDir)
	{
		configurationService.setConfigValue("org.synote.integration.ibmhts.server.sourceDir", sourceDir)
	}
	
	def getUserID()
	{
		return userID
	}
	
	def setUserID(String id)
	{
		userID = id
	}
	
	def getJobURL()
	{
		return getIBMServerUrl()+configurationService.getConfigValue("org.synote.integration.ibmhts.server.api.getJob")
	}
	
	//TODO: try to use getTranscript API finish this task.
	def getTransURL()
	{
		String transcriptionURL = getIBMServerUrl()+configurationService.getConfigValue("org.synote.integration.ibmhts.server.api.getTranscript")
		
		return transcriptionURL
	}
	
	/*
	 * When the time is within the stopAddingJob time, it will be set to false.
	 * Now, the only way to re-active allow adding jobs are set it manually to true through admin's UI, because we have to
	 * restart the HTS engine manually and we don't know when the service can come back after reboot.
	 */
	def setAllowAddingJobs(boolean allowed)
	{
		log.debug "setAllowAddingJobs to:"+allowed
		allowAddingJobs = allowed
	}
	
	def getAllowAddingJobs()
	{
		return allowAddingJobs
	}
	
	//return interval as a string
	def getDowntimeCheckInterval()
	{
		return configurationService.getConfigValue("org.synote.integration.ibmhts.server.downtime.checkInterval")
	}
	
	def setDowntimeCheckInterval(String interval)
	{
		configurationService.setConfigValue("org.synote.integration.ibmhts.server.downtime.checkInterval", interval)
	}
	
	def addJobURL()
	{
		String addURL = getIBMServerUrl()+configurationService.getConfigValue("org.synote.integration.ibmhts.server.api.addJob")
		return addURL
	}
	
	def getJobsURL()
	{
		String jobsURL = getIBMServerUrl()+configurationService.getConfigValue("org.synote.integration.ibmhts.server.api.getJobs")
		
		return jobsURL
	}
	/*
	 * Log into IBM system via api
	 */
	def loginIBM() throws IBMTransJobException,ConnectException,UnknownHostException
	{
		String username = getUsername()
		
		String password = getPassword()
		
		def loginURL = new URL(getIBMServerUrl()+configurationService.getConfigValue("org.synote.integration.ibmhts.server.api.login")+"username=${username}&password=${password}")
		//println "loginURL:"+loginURL.toString()
		String returnText = loginURL.getText()
		log.debug "login return:"+returnText
		
		def api = new XmlSlurper().parseText(returnText)
		
		if(api.success.text() == "true")
		{
			setUserID(api.retval.userid.text())
			setConnected(true)
			//TODO: if the service can started automatically, then we can set this to true
			//setAllowAddingJobs(true)
			log.info "Logged in: ${getUserID()}"
			return true
		}
		else
		{
			setConnected(false)
			setAllowAddingJobs(false)
			log.error "\n\nError loging in."
			String errorMsg = api.description.text()
			throw new IBMTransJobException(errorMsg)
		}
	}
	
	def logoutIBM() throws IBMTransJobException, ConnectException,UnknownHostException
	{
		if(!getConnected())
		{
			log.debug "Haven't logged in. Do not need to logout'"
			return true
		}
		def logoutURL = new URL(getIBMServerUrl()+configurationService.getConfigValue("org.synote.integration.ibmhts.server.api.logout")+"userid=${getUserID()}")
		String returnText = logoutURL.getText()
		log.debug "logout return:"+returnText
		def api = new XmlSlurper().parseText(returnText)
		
		if(api.success.text() == "true")
		{
			setConnected(false)
			setAllowAddingJobs(false)
			
			log.info "Successfully logged out"
			return true
		}
		else
		{
			log.error "Error loging out"
			String errorMsg = api.description.text()
			throw new IBMTransJobException(errorMsg)
		}
	}
	
	def removeJob(String currentJobID) throws ConnectException,UnknownHostException
	{
		def currentJobRemoveUrl = new URL(getIBMServerUrl()+configurationService.getConfigValue("org.synote.integration.ibmhts.server.api.removeJob")+"userid=${getUserID()}&jobid=${currentJobID}")
		String text = currentJobRemoveUrl.getText()
		log.debug "remove job:"+text
		
		def api = new XmlSlurper().parseText(text)

		//TODO:Parse the response, and return false if not successful
		return true
	}
	
	def getVSXMLTranscript(IBMTransJob jobDB) throws ConnectException, IOException,UnknownHostException
	{
		String sourceVSXMLFile
		
		sourceVSXMLFile = "${getIBMServerUrl()}${getSourceDir()}/${jobDB.jobId}/1.vsxml"
		def sourceVSXMLUrl = new URL(sourceVSXMLFile)
		//		change encoding
		def builder = new StreamingMarkupBuilder()
		builder.encoding="UTF-8"
		def xml =  new XmlSlurper().parseText(sourceVSXMLUrl.getText("UTF-16"))
		log.info "Get vsxml for job ${jobDB.jobId}."
		def vsxmlTranscript = builder.bind{
			mkp.xmlDeclaration()
			mkp.yield xml
		}
		
		return vsxmlTranscript
	}
	
	
	def getPlainTextTranscript(IBMTransJob jobDB)
	{
		//TODO: The returned result has too be decoded using sun.misc.BASE64Decoder
		String getTransUrl = "${getTransURL()}userid=${getUserID()}&jobid=${jobDB.jobId}&format=TXT"
		def sourceTXTUrl = new URL(getTransUrl)
	}
	
	
	def getPlainTextTranscript(String vsxmlTranscript, IBMTransJob jobDB)
	{
		def xml = new XmlSlurper().parseText(vsxmlTranscript)
		String transcript = xml.VSTextData.text().replace('<BRN>', '\r\n').substring(1)
		log.info "Get plain text for job ${jobDB.jobId}."
		return transcript
	}
	
	def saveTranscript(IBMTransJob jobDB) throws FileNotFoundException,IOException, ConnectException,UnknownHostException
	{
		String vsxmlTranscript = getVSXMLTranscript(jobDB)
		String plainText= getPlainTextTranscript(vsxmlTranscript, jobDB)
		
		jobDB.vsxmlTranscript = vsxmlTranscript
		jobDB.transcript = plainText
		/* If a job asked to be saved change its saved status to true */
		jobDB.saved = true
		
		jobDB.save()
		log.info "The transcript for ${jobDB.jobId} has been saved."
	}
	
	def addJob(Resource resource, String title, String url) throws ConnectException, IBMTransJobException,UnknownHostException, java.io.IOException
	{
		//		Check validation first
		/*
		 Retrieve the jobname identifier and the URL of the multimedia
		 file from the user
		 */
		def owner = securityService.getLoggedUser()
		
		def jobList = IBMTransJob.findAllByOwnerAndStatus(owner,IBMTransJobStatus.PROCESSING.value())
		int maxJob = Integer.parseInt(configurationService.getConfigValue("org.synote.integration.ibmhts.user.maxJob"))
		if(jobList.size() >=maxJob)
		{
			throw new IBMTransJobException("Each user can have at most ${maxJob} processing jobs. Please submit new jobs after other jobs have been finished.")	
		}
		
		if(!regExService.isIBMTransJobUrl(url))
		{
			throw new IBMTransJobException("IBM Transcript Service cannot transcribe the file with url ${url}")
		}
		String jobTITLEparameter = title
		String jobURLparameter = url
		
		
		String joburl
		int jobstatus
		String jobEditUrl = getIBMServerUrl()
		
		String addURL = addJobURL()
		log.debug "addJoburl:"+addURL
		
		/*	Adding the job to the EC system */
		def serverUrl = new URL("${addURL}userid=${userID}&url=${jobURLparameter}")
		def serverText = serverUrl.getText()
		log.debug "add job response:"+serverText
		def api = new XmlSlurper().parseText(serverText)
		
		/*
		 If the job was succesfully added to the EC system, create a copy
		 in the DB of the current job
		 */
		if(api.success.text() == "true")
		{
			/*
			 Send getJob request to the HTTP API. The information of the
			 current job added will be stored in the DB
			 */
			String jobid = api.retval.jobid.text()
			String getjobURL = getJobURL()
			
			def jobUrl = new URL("${getjobURL}userid=${userID}&jobid=${jobid}")
			String getJobText = jobUrl.getText()
			def getjobapi = new XmlSlurper().parseText(getJobText)
			log.debug "getJob response:"+getJobText
			
			joburl = getjobapi.retval.'job'.'@url'.text()
			log.debug "jobstatus:"+getjobapi.retval.'job'.'@status'.text()
			println "jobstatus:"+getjobapi.retval.'job'.'@status'.text()
			jobstatus = IBMTransJobStatus.valueOfString(getjobapi.retval.'job'.'@status'.text()).value()
			
			jobEditUrl += getjobapi.retval.'job'.'@editurl'.text()
			
			def newjob = new IBMTransJob( owner: owner,
									resource: resource,
									title: jobTITLEparameter,
									jobId:jobid,
									url:joburl,
									createDate:new Date(),
									status:jobstatus,
									transcript:null,
									vsxmlTranscript:null,
									xmlTranscript:null,
									editUrl:jobEditUrl,
									saved:false)
			if(!newjob.save())
			{
				throw new IBMTransJobException("Job has been successfully created in IBM HTS, but failed in Synote!")
			}
			log.debug "JobTitle: ${jobTITLEparameter} JobId: ${jobid} for ${userID} added. From URL: ${joburl}"
		}
		else
		{
			//Get error message
			String errorMessage = api.description.text()
			log.debug "Error adding job to IBM HTS.${errorMessage}"
			throw new IBMTransJobException(errorMessage)
			
		}
	}
	
	def updateJobs() throws IBMTransJobException, ConnectException, UnknownHostException
	{
		
		def jobs = IBMTransJob.findAllByStatus(IBMTransJobStatus.PROCESSING.value())
		//To see if the job is missed on the server side
		log.debug("${jobs.size()} jobs are processing...")
		def missedJobList = []
		
		jobs.each{ job->
			def jobUrl = new URL("${getJobURL()}userid=${userID}&jobid=${job.jobId}")
			def jobResponse = new XmlSlurper().parseText(jobUrl.getText())
			log.debug("Parsing job {job.jobId}")
			if(jobResponse.success.text() == "true")
			{
				String updatedStatus = jobResponse.retval.job.'@status'.text()
				log.debug("${job.jobId} status:${updatedStatus}")
				if(job.status != IBMTransJobStatus.valueOfString(updatedStatus).value())
				{
					job.status = IBMTransJobStatus.valueOfString(updatedStatus).value()
					if(!job.save())
						throw new IBMTransJobException("Fail to update Job ${job.jobId} to status ${updatedStatus}")
						
					log.info "Job ${job.jobId} has been updated to status ${updatedStatus}."
				}
			}
			else
			{
				log.error "Cannot find Job ${job.jobId}."
				missedJobList << job
			}
		}
		
		//If by any reason, a job is missing, we set it to failed.
		if(missedJobList.size() != 0)
		{
			missedJobList.each {job->
				job.status = IBMTransJobStatus.valueOfString("failed").value()
				if(!job.save())
					throw new IBMTransJobException("Fail to update missed Job ${job.jobId} to status failed")
				log.warn "Set job ${job.jobId} to failed."
			}
		}
	}
	
	/*
	 * Get if IBM server has planned downtime for patching
	 */
	def getIBMServerAutoDowntime()
	{
		return configurationService.getConfigValue("org.synote.integration.ibmhts.server.downtime.auto")
	}
	
	def setIBMServerAutoDowntime(String autoDowntime)
	{
		configurationService.setConfigValue("org.synote.integration.ibmhts.server.downtime.auto", autoDowntime)
	}
	/*
	 * 
	 * Stop adding job time is defined in database, it consists of:
	 * The week of the day (e.g. Tuesday)
	 * and
	 * the time (e.g. 00:00:00)
	 * the stop adding jobs time span is: stop adding job time and downtime end time
	 */
	def withinStopAddingJobsTime()
	{
		boolean autoDowntime = Boolean.valueOf(getIBMServerAutoDowntime().toLowerCase())
		if(!autoDowntime)
			return false
			
		//TODO: write locale information into system property
		Calendar nowCal = Calendar.getInstance(Locale.UK)
		
		//TODO: save the TUESDAY into database
		if(nowCal.get(Calendar.DAY_OF_WEEK) == getDayOfWeek(getStopAddingJobDayOfWeek()))
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
			String dateNow = sdf.format(nowCal.getTime())
			//TODO: Set stop adding Job time
			String stopAddingJobTimeStr = dateNow+" "+getStopAddingJobTime()
			String downtimeEndTimeStr = dateNow +" "+getDowntimeEndTime()
			sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
			Date stopAddingJobTime =  sdf.parse(stopAddingJobTimeStr)
			Date downtimeEnd = sdf.parse(downtimeEndTimeStr)
			if(nowCal.getTime().after(stopAddingJobTime) && nowCal.getTime().before(downtimeEnd))
			{
				return true
			}
		}
		return false
	}
	
	/*
	 * Within downtime is defined in database, it consists of
	 * downtime day of the week
	 * downtime start time
	 * downtime end time
	 * 
	 * Within downtime should be some time before the restart as well as some time after the restart
	 */
	def isWithinDowntime()
	{
		
		boolean autoDowntime = Boolean.valueOf(getIBMServerAutoDowntime().toLowerCase())
		if(!autoDowntime)
			return false
		
		//20 mins before the restart time
		//1 hour after the restart time
		Calendar nowCal = Calendar.getInstance(Locale.UK)
		def nowDayOfWeek = nowCal.get(Calendar.DAY_OF_WEEK)
		if(nowDayOfWeek == getDayOfWeek(getDowntimeDayOfWeek()))
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
			String dateNow = sdf.format(nowCal.getTime())
			String downtimeStartTimeStr = dateNow + " "+getDowntimeStartTime()
			String downtimeEndTimeStr = dateNow +" "+getDowntimeEndTime()
			sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
			Date downtimeStart = sdf.parse(downtimeStartTimeStr)
			Date downtimeEnd = sdf.parse(downtimeEndTimeStr)
			
			if(nowCal.getTime().after(downtimeStart) && nowCal.getTime().before(downtimeEnd))
			{
				return true	
			}
		}
		return false
	}
	
	def getStopAddingJobDayOfWeek()
	{
		String day = configurationService.getConfigValue("org.synote.integration.ibmhts.server.stopAddingJob.dayOfWeek")
		return day
	}
	
	def setStopAddingJobDayOfWeek(String day)
	{
		configurationService.setConfigValue("org.synote.integration.ibmhts.server.stopAddingJob.dayOfWeek", day)
	}
	
	def getStopAddingJobTime()
	{
		return configurationService.getConfigValue("org.synote.integration.ibmhts.server.stopAddingJob.time")
	}
	
	def setStopAddingJobTime(String time)
	{
		configurationService.setConfigValue("org.synote.integration.ibmhts.server.stopAddingJob.time", time)
	}
	
	def getDowntimeDayOfWeek()
	{
		String day = configurationService.getConfigValue("org.synote.integration.ibmhts.server.downtime.dayOfWeek")
		return day
	}
	
	def setDowntimeDayOfWeek(String day)
	{
		configurationService.setConfigValue("org.synote.integration.ibmhts.server.downtime.dayOfWeek", day)
	}
	
	def getDowntimeStartTime()
	{
		return configurationService.getConfigValue("org.synote.integration.ibmhts.server.downtime.startTime")
	}
	
	def setDowntimeStartTime(String time)
	{
		configurationService.setConfigValue("org.synote.integration.ibmhts.server.downtime.startTime", time)
	}
	
	def getDowntimeEndTime()
	{
		return configurationService.getConfigValue("org.synote.integration.ibmhts.server.downtime.endTime")
	}
	
	def setDowntimeEndTime(String time)
	{
		configurationService.setConfigValue("org.synote.integration.ibmhts.server.downtime.endTime", time)
	}
	
	/*
	 * Handle the exception if connection is lost
	 */
	def handleConnectException(ConnectException conex)
	{
		log.debug "handle connect exception"
		boolean wasConnected = getConnected()
		if(wasConnected)
			setConnected(false)
		
		conex.printStackTrace()
		log.error "java.net.ConnectException:"+conex.getMessage()
		if(!IBMTransJobService.isWithinDowntime() && wasConnected)
		{
			log.error "Lose connection to IBM HTS server"
			//TODO: send email
		}
		log.error "Cannot connect to IBM HTS server"
		return
	}
	
	private getDayOfWeek(String day)
	{
		switch(day.toUpperCase())
		{
			case "MONDAY":
				return Calendar.MONDAY
			case "TUESDAY":
				return Calendar.TUESDAY
			case "WEDNESDAY":
				return Calendar.WEDNESDAY
			case "THRUSDAY":
				return Calendar.TUESDAY
			case "FRIDAY":
				return Calendar.FRIDAY
			case "SATURDAY":
				return Calendar.SATURDAY
			case "SUNDAY":
				return Calendar.SUNDAY
			default:
				return Calendar.SUNDAY	
		}
	}
}

package org.synote.user.admin

import grails.converters.*
import java.io.InputStreamReader
import java.io.IOException;
import java.net.*
import java.security.Security
import java.util.Properties

import org.synote.resource.compound.MultimediaResource
import org.synote.resource.compound.TranscriptResource
import org.synote.resource.compound.PresentationResource
import org.synote.resource.single.binary.PresentationSlide
import org.synote.resource.compound.WebVTTResource
import org.synote.resource.single.text.WebVTTCue
import org.synote.annotation.ResourceAnnotation
import org.synote.annotation.synpoint.Synpoint
import org.synote.permission.PermissionValue
import org.synote.search.resource.ResourceSearchService
import org.synote.user.User
import org.synote.user.UserRole
import org.synote.integration.ibmhts.IBMTransJobService
import org.synote.config.ConfigurationService
import org.synote.player.server.PlayerService
import org.synote.search.sitemap.SitemapService

import org.synote.player.client.TranscriptData
import org.synote.player.client.TranscriptDataSimple
import org.synote.player.client.TranscriptItemData
import org.synote.player.client.TranscriptSynpoint

import grails.plugins.springsecurity.Secured

@Secured(['ROLE_ADMIN'])
class AdminController {

	def securityService
//	def emailService
	def resourceSearchService
	def IBMTransJobService
	def configurationService
	def playerService
	def adminService
	def sitemapService
	
	def index = {
		//do nothing
	}
	
	def cancel = {
		redirect(action:"index")
		return
	}
	
	def listUsers = {
		//Do nothing
	}
	
	def resetPassword = {
		def userList = User.list().sort{it.userName}
		return [userList:userList]
	}
	
	def handleResetPassword ={
		if(!params.userName)
		{
			flash.error ="Please select a user"
			render(view:'resetPassword',userList:User.list())
			return
		}
		
		if(!params.newPassword)
		{
			flash.error ="Please input your new password"
			render(view:'resetPassword',userList:User.list())
			return
		}
		
		if(!params.confirmNewPassword)
		{
			flash.error="Please confirm your new password"
			render(view:'resetPassword',userList:User.list())
			return
		}
		
		if(!params.newPassword.equals(params.confirmNewPassword))
		{
			flash.error="The new passworld and new confirmed password do not match!"
			redirect(action:'resetPassword')
			return
		}
		
		def user = User.findByUserName(params.userName)
		
		if(!user)
		{
			flash.error = "Cannot find user with user name ${params.userName}"
			render(view:'resetPassword',userList:User.list())
			return
		}
		
		
		user.password = securityService.encodePassword(user.userName, params.newPassword)
		user.confirmedPassword = securityService.encodePassword(user.userName, params.confirmNewPassword)
		
		if (user.hasErrors() || !user.merge(flush:true))
		{
			flash.error ="Cannot set the new password for user ${params.userName}"
			render(view: 'resetPassword', userList:User.list())
			return
		}
		
		flash.message = "${params.userName}'s password has been successfully changed to ${params?.newPassword}."
		redirect(action:'index')
		return
	}
	
	def changePermission  = {
		def userList = User.list().sort{it.userName}
		return [userList:userList]
	}
	
	def changeMultimediaPermission = {
		
		//println 'returnMultimediaList'
		if(!params.userName)
		{
			flash.error ="Please select a user"
			render(view:'changePermission',userList:User.list())
			return
		}
		
		def user = User.findByUserName(params.userName)
		
		if(!user)
		{
			flash.error = "Cannot find user with user name ${params.userName}"
			render(view:'changePermission',userList:User.list())
			return
		}
		
		PermissionValue oldPerm = null
		if (!params.oldPerm.trim().equals(""))
			oldPerm = PermissionValue.findByName(params.oldPerm)
		
		PermissionValue newPerm = null
		if (!params.newPerm.trim().equals(""))
			newPerm = PermissionValue.findByName(params.newPerm)
		
		def multimediaList = MultimediaResource.findAllByOwnerAndPerm(user,oldPerm)
		multimediaList.each{multimedia->
			multimedia.perm=newPerm
			if(!multimedia.save())
			{
				println "Save multimedia permission with id ${multimedia.id} error!"
				flash.error ="Save multimedia permission with id ${multimedia.id} error!";
				return
			}
			multimedia.reindex()
		}
		
		flash.message = "${multimediaList.size()} recordings have been updated."
		render (view:'changePermission', model:[userList:User.list().sort{it.userName}])
		return
	}
	
	def changeUserRole = {
		def userList = User.list().sort{it.userName}
		return [userList:userList]
	}
	
	def handleChangeUserRole = {
		
		if(!params.userName)
		{
			flash.error ="Please select a user"
			render(view:'changeUserRole',userList:User.list())
			return
		}
		
		if(!params.role)
		{
			flash.error ="Please select a role"
			render(view:'changeUserRole',userList:User.list())
			return
		}
		
		def user = User.findByUserName(params.userName)
		
		if(!user)
		{
			flash.error = "Cannot find user with user name ${params.userName}"
			render(view:'resetPassword',userList:User.list())
			return
		}
		
		user.role = UserRole.valueOfString(params.role)
		if(!user.save())
		{
			flash.error = "Cannot save the user role"
			render(view:'changeUserRole',userList:User.list())
			return
		}
		
		flash.message = "User "+params.userName+" has been changed to role "+params.role
		redirect(action:"index")
		return
	}
	/*
	def testEmail ={
		// Each "email" is a simple Map
		def email = [
		to: [ 'ps3@ecs.soton.ac.uk' ],        // "to" expects a List, NOT a single email address
		subject: 'test email',
		text: 'This is a test message from Rabbit'         // "text" is the email body
		]
		// sendEmails expects a List
		emailService.sendEmails([email])
		flash.message="email has been send"
		redirect(action:'index')
	}
	*/
	
	def changeIBMTransJobSettings = {
		return [IBMTransJobEnabled:IBMTransJobService.getIBMTransJobEnabled(),
				IBMServerProtocol: IBMTransJobService.getIBMServerProtocol(),
				IBMServerName: IBMTransJobService.getIBMServerName(),
				IBMServerPort: IBMTransJobService.getIBMServerPort(),
				IBMServerAppPath: IBMTransJobService.getIBMServerAppPath(),
				IBMServerSourceDir: IBMTransJobService.getSourceDir(),
				stopAddingJobDayOfWeek: IBMTransJobService.getStopAddingJobDayOfWeek(),
				stopAddingJobTime: IBMTransJobService.getStopAddingJobTime(),
				downtimeDayOfWeek: IBMTransJobService.getDowntimeDayOfWeek(),
				downtimeStartTime: IBMTransJobService.getDowntimeStartTime(),
				downtimeEndTime: IBMTransJobService.getDowntimeEndTime(),
				downtimeCheckInterval:IBMTransJobService.getDowntimeCheckInterval(),
				connected: IBMTransJobService.getConnected(),
				allowAddingJobs: IBMTransJobService.getAllowAddingJobs(),
				withinStopAddingJobsTime: IBMTransJobService.withinStopAddingJobsTime(),
				autoDowntime:IBMTransJobService.getIBMServerAutoDowntime()
		]
	}
	
	def enableIBMTransJob = {
		
		IBMTransJobService.setIBMTransJobEnabled("true")
		flash.message="IBM Transcribing service has been enabled."
		log.info "IBM Transcribing service has been enabled."
		redirect(action:'changeIBMTransJobSettings')
		return
	}
	
	def disableIBMTransJob = {
		IBMTransJobService.setIBMTransJobEnabled("false")
		flash.message="IBM Transcribing service has been disabled."
		log.info "IBM Transcribing service has been disabled."
		redirect(action:'changeIBMTransJobSettings')
		return
	}
	
	//TODO: document the what admin has to do to enable ibm hts
	//TODO: document admin's password
	def enableAddingJobs = {
		
		if(IBMTransJobService.getConnected() && !IBMTransJobService.getAllowAddingJobs() && !IBMTransJobService.withinStopAddingJobsTime())
		{
			IBMTransJobService.setAllowAddingJobs(true)
			flash.message="Adding new jobs has been enabled."
			
			log.info "Adding new jobs has been enabled."
			redirect(action:'changeIBMTransJobSettings')
			return
		}
		else
		{
			if(!IBMTransJobService.withinStopAddingJobsTime())
			{
				flash.error="It's within stop adding jobs time. You cannot enable adding jobs."
				
				log.error "It's within stop adding jobs time. You cannot enable adding jobs."
				redirect(action:'changeIBMTransJobSettings')
				return
			}
			else if(!IBMTransJobService.getAllowAddingJobs())
			{
				flash.error="Adding job has already been enabled"
				
				log.error "Adding job has already been enabled"
				redirect(action:'changeIBMTransJobSettings')
				return
			}
			else
			{
				flash.error="Connection to IBM HTS service has been setup yet."
				
				log.error "Connection to IBM HTS service has been setup yet."
				redirect(action:'changeIBMTransJobSettings')
				return
			}
		}
	}
	
	def disableAddingJobs = {
		
		if(IBMTransJobService.getConnected() && IBMTransJobService.getAllowAddingJobs())
		{
			IBMTransJobService.setAllowAddingJobs(false)
			flash.message="Adding new jobs has been disabled."
			
			log.info "Adding new jobs has been disabled."
			redirect(action:'changeIBMTransJobSettings')
			return
		}
		else if(!IBMTransJobService.getAllowAddingJobs())
		{
			flash.error="Adding job has already been enabled"
			
			log.error "Adding job has already been enabled"
			redirect(action:'changeIBMTransJobSettings')
			return
		}
		else
		{
			flash.error="Connection to IBM HTS service has been setup yet."
			
			log.error "Connection to IBM HTS service has been setup yet."
			redirect(action:'changeIBMTransJobSettings')
			return
		}
	}
	
	def saveIBMTransJobSettings = {
		
		String message="Following settings has been changed:"
		
		if(params.IBMServerProtocol && params.IBMServerProtocol != IBMTransJobService.getIBMServerProtocol())
		{
			IBMTransJobService.setIBMServerProtocol(params.IBMServerProtocol)
			message="\nIBM transcribing service protocol has been set to ${params.IBMServerProtocol}."
		}
		
		if(params.IBMServerName && params.IBMServerName != IBMTransJobService.getIBMServerName())
		{
			IBMTransJobService.setIBMServerName(params.IBMServerName)
			message += "\nIBM transcribing service name has been changed to ${params.IBMServerName}"
		}
		if(params.IBMServerPort && params.IBMServerPort != IBMTransJobService.getIBMServerPort())
		{
			IBMTransJobService.setIBMServerPort(params.IBMServerPort)
			message += "\nIBM transcribing service Port has been changed to ${params.IBMServerPort}"
		}
		if(params.IBMServerAppPath && params.IBMServerAppPath != IBMTransJobService.getIBMServerAppPath())
		{
			IBMTransJobService.setIBMServerAppPath(params.IBMServerAppPath)
			message += "\nIBM transcribing service Application Path has been changed to ${params.IBMServerAppPath}"
		}
		if(params.IBMServerSourceDir && params.IBMServerSourceDir != IBMTransJobService.getSourceDir())
		{
			IBMTransJobService.setSourceDir(params.IBMServerSourceDir)
			message += "\nIBM transcribing service Source Directory has been changed to ${params.IBMServerSourceDir}"
		}
		if(params.stopAddingJobDayOfWeek && params.stopAddingJobDayOfWeek != IBMTransJobService.getStopAddingJobDayOfWeek())
		{
			IBMTransJobService.setStopAddingJobDayOfWeek(params.stopAddingJobDayOfWeek)
			message += "\nIBM transcribing service stop adding job day of week has been changed to ${params.stopAddingJobDayOfWeek}"
		}
		if(params.stopAddingJobTime && params.stopAddingJobTime != IBMTransJobService.getStopAddingJobTime())
		{
			IBMTransJobService.setStopAddingJobTime(params.stopAddingJobTime)
			message += "\nIBM transcribing service stop adding job time has been changed to ${params.IBMServerProtocol}"
		}
		if(params.downtimeDayOfWeek && params.downtimeDayOfWeek != IBMTransJobService.getDowntimeDayOfWeek())
		{
			IBMTransJobService.setDowntimeDayOfWeek(params.downtimeDayOfWeek)
			message += "\nIBM transcribing service downtime day of week has been changed to ${params.downtimeDayOfWeek}"
		}
		if(params.downtimeStartTime && params.downtimeStartTime != IBMTransJobService.getDowntimeStartTime())
		{
			IBMTransJobService.setDowntimeStartTime(params.downtimeStartTime)
			message += "\nIBM transcribing service downtime start time has been changed to ${params.downtimeStartTime}"
		}
		
		if(params.downtimeEndTime && params.downtimeEndTime != IBMTransJobService.getDowntimeEndTime())
		{
			IBMTransJobService.setDowntimeEndTime(params.downtimeEndTime)
			message += "\nIBM transcribing service downtime end time has been changed to ${params.downtimeEndTime}"
		}
		
		if(params.downtimeCheckInterval && params.downtimeCheckInterval != IBMTransJobService.getDowntimeCheckInterval())
		{
			IBMTransJobService.setDowntimeCheckInterval(params.downtimeCheckInterval)
			message += "\nIBM transcribing service downtime check interval has been changed to ${params.downtimeCheckInterval} \nSynote needs to be restarted to apply this change."
		}
		
		if(params.autoDowntime && params.autoDowntime != IBMTransJobService.getIBMServerAutoDowntime())
		{
			IBMTransJobService.setIBMServerAutoDowntime(params.autoDowntime)
			message += "\nIBM transcribing service auto downtime has been changed to ${params.autoDowntime}."
		}
		
		flash.message = message
		log.info(message)
		redirect(action:'index')
		return
	}
	
	def configSearch = {
		//Do nothing
	}
	
	/**
	 * Perform a bulk index of every searchable object in the database
	 */
	def searchIndexAll = {
		resourceSearchService.indexResources()
		flash.message = "bulk index started in a background thread"
		redirect(action:'index')
	}
	
	/**
	 * Perform a bulk index of every searchable object in the database
	 */
	def searchUnindexAll = {
		resourceSearchService.unindexResources()
		flash.message = "unindexAll done"
		redirect(action:'index')
	}
	/*
	 * Edit terms and conditions
	 */
	def setTermsAndConditions = {
		def content = configurationService.getConfigValue("org.synote.metadata.legal.termsAndConditions")
		println content
		render(view:"setTermsAndConditions", model:[content:content.encodeAsHTML()])
		return
	}
	
	def saveTermsAndConditions = {
		if(!params.termsAndConditions)
		{
			flash.error = "The content of the terms and conditions cannot be empty."
			redirect(action:"setTermsAndConditions")
			return
		}
		configurationService.setConfigValue("org.synote.metadata.legal.termsAndConditions",params.termsAndConditions)
		flash.message="New terms and conditions has been saved"
		redirect(action:"index")
		return
	}
	
	/*
	 * Edit contact page
	 */
	def setContactPage = {
		def content = configurationService.getConfigValue("org.synote.metadata.contact.page.content")
		render(view:"setContactPage", model:[content:content])
		return
	}
	
	def saveContactPage = {
		if(!params.contact)
		{
			flash.error = "The content of the terms and conditions cannot be empty."
			redirect(action:"setContactPage")
			return
		}
		configurationService.setConfigValue("org.synote.metadata.contact.page.content",params.contact)
		flash.message="New contact page has been saved"
		redirect(action:"index")
		return
	}
	/*
	 * A page about some secret operations
	 */
	def secretOperations = {
		//Do nothing	
	}
	def convertTranscriptResourceToWebVTTResource =
	{
		if(params.id)
		{
			def transcript = TranscriptResource.get(params.id)
			if(transcript)
				adminService.convertOneTranscriptResourceToWebVTTResource(transcript)
		}
		else
		{
			adminService.convertTranscriptResourceToWebVTTResource()
		}
		flash.message = "It's running at the background now."
		render (view:'secretOperations')
		return
	}
	def removeTranscriptResources =
	{
		adminService.removeTranscriptResources()
		
		flash.message = "All the old transcript has been removed."
		render (view:'secretOperations')
		return
	}
	
	def generateReplaySitemap = {
		sitemapService.createReplaySitemap()
		flash.message = "Replay sitemap has been created."
		render (view:'secretOperations')
		return
	}
	
	def generateResourcesSitemap = {
		sitemapService.createResourcesSitemap()
		flash.message = "Resource sitemap has been created."
		render (view:'secretOperations')
		return
	}

	def generateAnnotationsSitemap = {
		sitemapService.createAnnotationsSitemap()
		flash.message = "Annotation sitemap has been created."
		render (view:'secretOperations')
		return
	}
	
	def generateUsersSitemap = {
		sitemapService.createUsersSitemap()
		flash.message = "User sitemap has been created."
		render (view:'secretOperations')
		return
	}

	def addSlides = {
		def multimedia = MultimediaResource.get(params.id.toLong())
		if(!multimedia)
		{
			println 1
			return
		}
		def owner = User.findByUserName("yunjiali")
		if(!owner)
		{
			println 2
			return
		}
		
		def presentation = new PresentationResource(owner: owner)
		presentation.addToSlides(new PresentationSlide(presentation: presentation, owner:owner, index: 0, url: "http://synote.org/resource/yunjiali/tbl_ted/img0.png"));
		presentation.addToSlides(new PresentationSlide(presentation: presentation, owner:owner, index: 1, url: "http://synote.org/resource/yunjiali/tbl_ted/img2.png"));
		presentation.addToSlides(new PresentationSlide(presentation: presentation, owner:owner, index: 2, url: "http://synote.org/resource/yunjiali/tbl_ted/img3.png"));
		presentation.addToSlides(new PresentationSlide(presentation: presentation, owner:owner, index: 3, url: "http://synote.org/resource/yunjiali/tbl_ted/img4.png"));
		presentation.addToSlides(new PresentationSlide(presentation: presentation, owner:owner, index: 4, url: "http://synote.org/resource/yunjiali/tbl_ted/img5.png"));
		presentation.addToSlides(new PresentationSlide(presentation: presentation, owner:owner, index: 5, url: "http://synote.org/resource/yunjiali/tbl_ted/img6.png"));
		presentation.addToSlides(new PresentationSlide(presentation: presentation, owner:owner, index: 6, url: "http://synote.org/resource/yunjiali/tbl_ted/img12.png"));
		presentation.addToSlides(new PresentationSlide(presentation: presentation, owner:owner, index: 7, url: "http://synote.org/resource/yunjiali/tbl_ted/img14.png"));
		presentation.addToSlides(new PresentationSlide(presentation: presentation, owner:owner, index: 8, url: "http://synote.org/resource/yunjiali/tbl_ted/img17.png"));
		presentation.addToSlides(new PresentationSlide(presentation: presentation, owner:owner, index: 9, url: "http://synote.org/resource/yunjiali/tbl_ted/img18.png"));
		presentation.addToSlides(new PresentationSlide(presentation: presentation, owner:owner, index: 10, url: "http://synote.org/resource/yunjiali/tbl_ted/img19.png"));
		presentation.addToSlides(new PresentationSlide(presentation: presentation, owner:owner, index: 11, url: "http://synote.org/resource/yunjiali/tbl_ted/img20.png"));
		presentation.addToSlides(new PresentationSlide(presentation: presentation, owner:owner, index: 12, url: "http://synote.org/resource/yunjiali/tbl_ted/img22.png"));
		presentation.addToSlides(new PresentationSlide(presentation: presentation, owner:owner, index: 13, url: "http://synote.org/resource/yunjiali/tbl_ted/img24.png"));
		presentation.addToSlides(new PresentationSlide(presentation: presentation, owner:owner, index: 14, url: "http://synote.org/resource/yunjiali/tbl_ted/img29.png"));
		
		def annotation = new ResourceAnnotation(owner: owner, source: presentation, target: multimedia)
		if (!presentation.save(flush:true))
		{
			println 3
			return
		}
		
		annotation.addToSynpoints(new Synpoint(annotation: annotation, sourceStart: 0, targetStart: 0, targetEnd:274000))
		annotation.addToSynpoints(new Synpoint(annotation: annotation, sourceStart: 1, targetStart: 275000,targetEnd:296000))
		annotation.addToSynpoints(new Synpoint(annotation: annotation, sourceStart: 2, targetStart: 297000,targetEnd:363000))
		annotation.addToSynpoints(new Synpoint(annotation: annotation, sourceStart: 3, targetStart: 364000,targetEnd:467000))
		annotation.addToSynpoints(new Synpoint(annotation: annotation, sourceStart: 4, targetStart: 468000,targetEnd:500000))
		annotation.addToSynpoints(new Synpoint(annotation: annotation, sourceStart: 5, targetStart: 501000,targetEnd:529000))
		annotation.addToSynpoints(new Synpoint(annotation: annotation, sourceStart: 6, targetStart: 530000,targetEnd:654000))
		annotation.addToSynpoints(new Synpoint(annotation: annotation, sourceStart: 7, targetStart: 655000,targetEnd:727000))
		annotation.addToSynpoints(new Synpoint(annotation: annotation, sourceStart: 8, targetStart: 728000,targetEnd:748000))
		annotation.addToSynpoints(new Synpoint(annotation: annotation, sourceStart: 9, targetStart: 749000,targetEnd:759000))
		annotation.addToSynpoints(new Synpoint(annotation: annotation, sourceStart: 10, targetStart: 760000,targetEnd:768000))
		annotation.addToSynpoints(new Synpoint(annotation: annotation, sourceStart: 11, targetStart: 769000,targetEnd:831000))
		annotation.addToSynpoints(new Synpoint(annotation: annotation, sourceStart: 12, targetStart: 832000,targetEnd:856000))
		annotation.addToSynpoints(new Synpoint(annotation: annotation, sourceStart: 13, targetStart: 857000,targetEnd:875000))
		annotation.addToSynpoints(new Synpoint(annotation: annotation, sourceStart: 14, targetStart: 876000,targetEnd:900000))
		
		if (!annotation.save(flush:true))
		{
			println 4
			return
		}
		flash.message = "Successfully add TBL TED talk."
		render (view:'secretOperations')
		return
	}
	
	/*
	 * Dump all the RDF data to one file
	 */
	def dumpRDFToOneFile = {
		adminService.dumpRDFToOneFile()
		flash.message = "A new thread of data dumping has been running in the background..."
		render (view:'secretOperations')
		return
	}
	
	def putNERDIntoTripleStroe ={
			
	}
}

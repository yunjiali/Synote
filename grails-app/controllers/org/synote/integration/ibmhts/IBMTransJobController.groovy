package org.synote.integration.ibmhts

import grails.converters.*
import groovy.util.XmlSlurper
import org.xml.sax.SAXParseException
import java.io.*
import org.apache.commons.lang.ArrayUtils

import org.synote.integration.ibmhts.exception.IBMTransJobException
import org.synote.integration.viascribe.exception.ViascribeException
import org.synote.permission.exception.ResourcePermissionException
import org.synote.user.SecurityService
import org.synote.integration.viascribe.ViascribeService
import org.synote.permission.PermService
import org.synote.utils.RegExService
import org.synote.resource.Resource
import org.synote.resource.compound.MultimediaResource
import org.synote.permission.PermissionValue
import org.synote.utils.FileService

import org.codehaus.groovy.grails.plugins.springsecurity.Secured

@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
class IBMTransJobController {

	def IBMTransJobService
	def securityService
	def viascribeService
	def permService
	def regExService
	def fileService
	
	def beforeInterceptor = [action: this.&auth]
	//the delete, save and update actions only accept POST requests
	//def allowedMethods = [delete:'POST', save:'POST', update:'POST']
	
	//TODO: test this function
	private auth()
	{
		if(!securityService.isAllowedIPAddress(request.remoteAddr))
		{
			flash.message = "You are not entitled to use Transcribing service."
			redirect(controller:'user', action:'index')
			return false
		}
		
		if(IBMTransJobService.getIBMTransJobEnabled().toLowerCase() != "true")
		{
			flash.message = "The Transcript Service has been disabled."
			redirect(controller:'user', action:'index')
			return false
		}
	}
	
	def index = {
		redirect(action:list,params:params)
	}
	
	def list = {
		def user = securityService.getLoggedUser()
		
		if(!params.max)
		params.max = 10
		
		if(!params.offset)
		params.offset = 0
		
		if(params.offset instanceof String)
		{
			params.offset = Integer.parseInt(params.offset)
		}
		
		if(params.max instanceof String)
		{
			params.max = Integer.parseInt(params.max)
		}
		
		def allJobList = IBMTransJob.list(sort:"dateCreated", order:"desc")
		def IBMTransJobList = []
		allJobList.each{ job->
			if(permService.getPerm(job.resource, user).val >= PermissionValue.findByName("WRITE").val)
			{
				IBMTransJobList << job
			}
		}
		//def IBMTransJobList = IBMTransJob.findAll("From IBMTransJob as i, Resource as r where i.resource = r and r.perm =:perm order by i.createDate desc",
		//		[perm:PermissionValue.WRITE])
		int fromIndex
		if(params.offset > IBMTransJobList.size())
		{
			fromIndex = 0
		}
		else
		{
			fromIndex = params.offset
		}
		int toIndex = 0
		if(IBMTransJobList.size() > 0)
		{
			toIndex = params.offset+params.max > IBMTransJobList.size()? IBMTransJobList.size()-1:params.offset-1+params.max
			return [ IBMTransJobList: IBMTransJobList.getAt(new IntRange(fromIndex, toIndex)), IBMTransJobCount: IBMTransJobList.size()]
		}
		else
		{
			return [ IBMTransJobList: IBMTransJobList, IBMTransJobCount: 0]
		}
	}
	
	def show = {
		def iBMTransJob = IBMTransJob.get( params.id )
		if(!iBMTransJob) {
			flash.error = "Cannot find job with id ${params.id}"
			log.error("Cannot find job with id ${params.id}")
			redirect(action:list)
			return
		}
		
		//In case someone type the id directly into 
		def multimediaResource = iBMTransJob.resource
		def perm = permService.getPerm(multimediaResource)
		
		if(perm.val < PermissionValue.findByName("WRITE").val)
		{
			flash.error = "You are not entitled to view the job detail"
			redirect(action:'list')
			return
		}
		String replaceStr = "userid="+IBMTransJobService.getUserID()
		String editUrl = iBMTransJob.editUrl.replaceFirst(regExService.getUserGuidRegEx().pattern(),replaceStr)
		return [ iBMTransJob : iBMTransJob, perm:perm,editUrl:editUrl, connected: IBMTransJobService.getConnected()]
	}
	
	/* Ajax method to get transcript, haven't implemented yet.
	def getTranscript =
	{
		def transcript = null
		String replaceStr = "userid="+IBMTransJobService.getUserID()
		String editUrl = iBMTransJob.editUrl.replaceFirst(regExService.getUserGuidRegEx().pattern(),replaceStr)
		
		try
		{
			if(iBMTransJob.status == IBMTransJobStatus.DONE.value())
			{
				IBMTransJobService.saveTranscript(iBMTransJob)
				def file = new File("transcript.vsxml")
				file.setText(iBMTransJob.vsxmlTranscript)
				def xml = new XmlSlurper().parseText(file.getText())
				transcript = xml.VSTextData.text().replace('<BRN>', '\r\n').substring(1)
			}
			
		}
		catch(FileNotFoundException fnfex)
		{
			flash.error = "Cannot found file:"+fnfex.getMessage()+". It may has been removed from the server."
		}
	}
	*/
	
	def handleUpload = {
		//Create new multimedia resource
		//change job status to uploaded
		//remove job from IBM HTS server
		//println "here0"
		def iBMTransJob = IBMTransJob.get(params.id)
		//println "here1:"+params.id
		if(iBMTransJob != null)
		{
			//check if the logged in user is the owner
			def owner = securityService.getLoggedUser()
			if(!securityService.isOwnerOrAdmin(iBMTransJob.owner.id))
			{
				flash.message = "You are not entitled to upload this recording!"
				redirect(action:'list')
				return
			}
			
			if(iBMTransJob.status != IBMTransJobStatus.DONE.value())
			{
				flash.message = "The transcript is not available!"
				redirect(action:'list')
				return
			}
			
			try
			{
				IBMTransJobService.saveTranscript(iBMTransJob)
				def file = fileService.createTempFile("temp","transcript.xml")
				//Rabbit: I have to do this, if you read vsxmlTranscript directly from database,there will be
				//some illegal characters
				file.setText(iBMTransJob.vsxmlTranscript)
				def xml = new XmlSlurper().parseText(file.getText())
				//def xml = new XmlSlurper().parseText(iBMTransJob.vsxmlTranscript)
				//def multimediaResource= viascribeService.upload(
				//	session.user,
				//	iBMTransJob.title,
				//	xml,
				//	iBMTransJob.url,
				//	PermissionValue.valueOfString(params.perm))
				
				//println "here6"
				viascribeService.uploadVSXMLTranscript(owner, iBMTransJob.resource, 'Transcript', xml)
				
				if(!iBMTransJob.save())
				{
					throw new IBMTransJobException("Job update failed!")
				}
				
				//IBMTransJobService.removeJob(iBMTransJob.jobId)
				flash.message = "Transcript has been successfully uploaded"
			}
			catch(ConnectException conex)
			{
				IBMTransJobService.handleConnectException(conex)
				flash.error = "Failed to connect to IBM Transcript service. "
			}
			catch (ViascribeException ex)
			{
				log.error ex.getMessage()+"::"+ex.getLocalizedMessage()
				flash.error = ex.getMessage()
			}
			catch (SAXParseException saxEx)
			{
				log.error saxEx.getMessage()+"::"+saxEx.getLocalizedMessage()
				flash.error = "The content you get from IBM Transcript service is not valid, please update the transcript."
			}
			catch (ResourcePermissionException rpe)
			{
				log.error saxEx.getMessage()+"::"+saxEx.getLocalizedMessage()
				flash.error = rpe.getMessage()
			}
			catch(IBMTransJobException exp)
			{
				log.error saxEx.getMessage()+"::"+saxEx.getLocalizedMessage()
				flash.error = exp.getMessage()
			}
			catch(FileNotFoundException fnfex)
			{
				log.error fnfex.getMessage()+"::"+fnfex.getLocalizedMessage()
				flash.error = "Cannot found file:"+fnfex.getMessage()+". It may has been removed from the server."
			}
			catch(IOException ioe)
			{
				log.error ioe.getMessage()+"::"+ioe.getLocalizedMessage()
				flash.error = "Failed to connect to IBM Transcript service. Please try again later."
			}
			catch(Exception exception)
			{
				log.error exception.getMessage()+"::"+exception.getLocalizedMessage()
				flash.error = exception.getMessage()
			}
			finally
			{
				redirect(action: 'show', params: params)
				return
			}
		}
		else
		{
			flash.message = "IBMTransJob not found with id ${params.id}"
			redirect(action:'list')
		}
	}
	
	def downloadTranscript ={
		
		if(!params.id)
		{
			redirect(action:'list')
		}
		
		def iBMTransJob = IBMTransJob.get(params.id)
		if(!iBMTransJob)
		{
			flash.meessage = "Cannot find transcript Job with id ${params.id}"
			redirect(action:'list')
		}
		
		def owner = securityService.getLoggedUser()
		if(!securityService.isOwnerOrAdmin(iBMTransJob.owner.id))
		{
			flash.message = "You are not entitled to download the transcript!"
			redirect(action:'show', model:[iBMTransJob:iBMTransJob])
			return
		}
		
		if(iBMTransJob.status != IBMTransJobStatus.DONE.value())
		{
			flash.message = "The transcript is not available!"
			redirect(action:'show', model:[iBMTransJob:iBMTransJob])
			return
		}
		
		if(!iBMTransJob.saved)
		{
			flash.message = "Please upload the transcript first."
			redirect(action:'show', model:[iBMTransJob:iBMTransJob])
			return
		}
		//Add BOM, when we save the xml file, the BOM is gone
		byte[] bom = new byte[3]
		bom[0] = (byte) 0xEF
		bom[1] = (byte) 0xBB
		bom[2] = (byte) 0xBF
		byte[] xmlOutput = ArrayUtils.addAll(bom,iBMTransJob.vsxmlTranscript.bytes)
		
		response.setContentType("application/octet-stream")
		response.setHeader("Content-disposition", "attachment;filename=transcript${params.id}.vsxml")
		
		response.outputStream << new ByteArrayInputStream(xmlOutput)
		return
	}
	
	/*
	 * remove the job
	 */
	def delete =
	{
		if(!params.id)
		{
			redirect(action:'list')
		}
		def iBMTransJob = IBMTransJob.get(params.id)
		if(!iBMTransJob)
		{
			flash.message = "Cannot find transcript Job with id ${params.id}"
			redirect(action:'show', id:params.id)
		}
		
		def owner = securityService.getLoggedUser()
		if(!securityService.isOwnerOrAdmin(iBMTransJob.owner.id))
		{
			flash.message = "You are not entitled to delete the transcript!"
			redirect(action:'show', id:params.id)
			return
		}
		
		String title = iBMTransJob.title
		
		//maybe the job hasn't been uploaded yet, so we'd better delete it
		IBMTransJobService.removeJob(iBMTransJob.jobId)
		iBMTransJob.delete()
		log.debug "Transcript job ${title} has been deleted."
		flash.message = "Transcript job ${title} has been deleted."
		redirect(action:'list')
	}
}

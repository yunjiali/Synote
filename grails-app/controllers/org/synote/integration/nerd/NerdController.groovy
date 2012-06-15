package org.synote.integration.nerd

import org.codehaus.groovy.grails.plugins.springsecurity.Secured
import org.synote.resource.compound.*
import org.synote.resource.Resource
import org.synote.resource.single.text.TagResource
import org.synote.resource.single.text.TextNoteResource
import org.synote.resource.single.text.WebVTTCue
import org.synote.annotation.ResourceAnnotation
import org.synote.api.APIStatusCode
import org.synote.permission.PermService
import grails.converters.*

import java.util.UUID

import fr.eurecom.nerd.client.*
import fr.eurecom.nerd.client.schema.*

@Secured(['ROLE_ADMIN','ROLE_NORMAL'])
class NerdController {
	
	final static String NERD_KEY = "56532eb996e0eb8ab30e1f8cd6d1be97b49fdf38"
	
	def nerdService
	def permService
	/*
	 * Extract named entity using nerd
	 * params: extractor, language (default "en"), text
	 */
	def extractAjax = {
		def text =""
		if(!params.text)
		{
			if(!params.id)
			{
				render(contentType:"text/json"){
					error(stat:APIStatusCode.NERD_ID_MiSSING, description:"Resource id is missing.")
				}
				return
			}
			
			def resource = Resource.get(params.id.toLong())
			if(!resource)
			{
				render(contentType:"text/json"){
					error(stat:APIStatusCode.NERD_RESOURCE_NOT_FOUND, description:"Cannot find the resource with id ${params.id}.")
				}
				return
			}
			text = nerdService.getTextFromResource(resource)?.text
		}
		else
		{
			text =params.text	
		}
		
		
		if(!params.extractor)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.NERD_EXTRACTOR_NOT_FOUND, description:"Cannot find the extractor.")
			}
			return
		}
		
		def nerdExtractor = nerdService.getNerdExtractor(params.extractor)
		if(!nerdExtractor)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.NERD_EXTRACTOR_NOT_FOUND, description:"Cannot find the extractor with name ${params.extractor}.")
			}
			return
		}
		
		if(!text || text?.trim()?.size() ==0)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.NERD_TEXT_NOT_FOUND, description:"Extraction text cannot be empty.")
			}
			return	
		}
		
		//default language is English. We will add more language support later.
		if(!params.lang)
			params.lang = "en"
		try
		{
			//String uuid = UUID.randomUUID().toString()
			//println uuid
			NERD nerd = new NERD(NERD_KEY)
			//println "${params.id} ${params.extractor} text:"+text
			def result= nerd.extractionJSON(nerdExtractor, text?.trim(),"en",true)
			//def result2= nerd.extractionJSON(Extractor.ZEMANTA, "African","en",true)
			//response.contentType = "text/json"
			def jsObj = JSON.parse(result)
			String entityStr = ""
			jsObj.each{j->
				entityStr+= j.entity+" "
			}
			//println " ${params.id} ${params.extractor} entities:"+entityStr
			//println uuid
			render JSON.parse(result) as JSON
			return
		}
		catch(Exception ex)
		{
			render(contentType:"text/json"){
				error(stat:APIStatusCode.NERD_EXTRACTOR_INTERAL_ERROR, description:"Connection failure to the extractor.")
			}
			return
		}
		
	}
	
	/*
	 * This method nerd tag, description, note as separate documents, which may not be efficient
	 */
	def nerdit = {
		//if there is an id, we will use params.id
		//if no id, we are looking for params.fields
		if(!params.id && !params.fields)
		{
			flash.error = "Cannot find the resource."
			redirect(controller:'user',action:'index')
			return	
		}
		
		if(!params.extractor || params.list('extractor')?.size() ==0)
		{
			flash.error = "No extractor is selected."
			redirect(controller:'user',action:'index')
			return
		}
		
		def idList = []
		if(params.id)
		{
			idList << params.id.toLong()	
		}
		else if(params.fields)
		{
			
			params.list('fields').each{ f->	
				idList << f.toLong()
			}
			
		}
		
		if(idList.size() == 0)
		{
			flash.error = "No resource is indicated."
			redirect(controller:'user',action:'index')
			return
		}
		
		def resourceList = []
		def extractors = []
		
		for(int n=0;n<idList.size();n++)
		{
			def i = idList.get(n)
			def resource = Resource.get(i)
			if(!resource)
			{
				flash.error = "Cannot find the resource with id ${i}."
				redirect(controller:'user',action:'index')
				return
			}
			
			def textField = nerdService.getTextFromResource(resource)
			
			extractors = params.list('extractor')
			
			def item = [
					id: i,
					field:textField.field,
					text:textField.text,
				]
			
			resourceList << item
		}
		
		def results = [rows:resourceList,extractors:extractors] as Map
		return [resourceList:results]
	}
	
	/*
	 * This method is very similar to nerdit, but we treat the input as just one document
	 * It saves time because we don't need to send multiple documents related to the same media fragment,
	 * but the NIF start and end character won't be useful anymore because we have mixed up the documents
	 */
	def nerditone = {
		if(!params.id && !params.fields)
		{
			flash.error = "Cannot find the resource."
			redirect(controller:'user',action:'index')
			return
		}
		
		if(!params.extractor || params.list('extractor')?.size() ==0)
		{
			flash.error = "No extractor is selected."
			redirect(controller:'user',action:'index')
			return
		}
		
		def idList = []
		if(params.id)
		{
			idList << params.id.toLong()
		}
		else if(params.fields)
		{
			
			params.list('fields').each{ f->
				idList << f.toLong()
			}
			
		}
		
		if(idList.size() == 0)
		{
			flash.error = "No resource is indicated."
			redirect(controller:'user',action:'index')
			return
		}
		
		//def resourceList = []
		def extractors = params.list('extractor')
		StringBuilder strBuilder = new StringBuilder()
		//String eol = System.getProperty("line.separator")
		for(int n=0;n<idList.size();n++)
		{
			def i = idList.get(n)
			def resource = Resource.get(i)
			if(!resource)
			{
				flash.error = "Cannot find the resource with id ${i}."
				redirect(controller:'user',action:'index')
				return
			}
			
			def textField = nerdService.getTextFromResource(resource)
			
			strBuilder.append(textField.text)
			strBuilder.append(",")
		}
		
		def results = [text:strBuilder.toString(),extractors:extractors] as Map
		return [textResource:results]
	}
	
	def nerdmm = { 
		def multimediaResource 	= MultimediaResource.get(params.id)
		
		if(!multimediaResource)
		{
			flash.error = "Cannot find the recording"
			redirect(action:'index', controller:'user')
			return	
		}
		
		def perm = permService.getPerm(multimediaResource)
		if(perm?.val <=0)
		{
			flash.error = "Access denied! You don't have permission to access this recording"
			redirect(controller:'user',action: 'index')
			return
		}
		
		return [multimedia:multimediaResource]
	}
	
	/*
	 * Nerd synmark
	 */
	def nerdsmk={
		def synmarkResource = SynmarkResource.get(params.id)
		
		if(!synmarkResource)
		{
			flash.error = "Cannot find the synmark"
			redirect(action:'index', controller:'user')
			return
		}
		
		
		def anno = ResourceAnnotation.findBySource(synmarkResource)
		if(!anno || !anno.target)
		{
			flash.error = "Cannot find the annotation"
			redirect(action:'index', controller:'user')
			return
		}
		def perm = permService.getPerm(anno.target)
		if(perm?.val <=0)
		{
			flash.error = "Access denied! You don't have permission to access this synmark"
			redirect(controller:'user',action: 'index')
			return
		}
		
		return [synmark:synmarkResource]
	}
}

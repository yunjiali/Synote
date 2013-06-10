package org.synote.linkeddata

import java.util.Calendar;

import org.synote.resource.Resource
import javax.servlet.http.HttpServletResponse
import org.synote.resource.compound.MultimediaResource
import org.synote.resource.compound.CompoundResource
import org.synote.resource.single.text.TextResource
import org.synote.resource.single.text.MultimediaTag
import org.synote.resource.single.text.MultimediaTextNote
import org.synote.resource.single.text.SynmarkTag
import org.synote.resource.single.text.SynmarkTextNote
import org.synote.resource.compound.SynmarkResource
import org.synote.resource.compound.PresentationResource
import org.synote.resource.single.binary.PresentationSlide
import org.synote.resource.compound.WebVTTResource
import org.synote.resource.single.text.WebVTTCue
import org.synote.annotation.ResourceAnnotation
import org.synote.annotation.synpoint.Synpoint
import org.synote.user.User
import org.synote.linkeddata.Vocabularies as V

import org.synote.permission.PermService
import org.synote.linkeddata.LinkedDataService
import org.synote.user.SecurityService
import org.synote.utils.UtilsService

import org.synote.linkeddata.RDFBuilder
import org.synote.linkeddata.RedirectData
import org.synote.linkeddata.exception.RDFGenerationException
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime
import com.hp.hpl.jena.query.ResultSetFormatter
import com.hp.hpl.jena.sparql.resultset.ResultsFormat
import com.hp.hpl.jena.sparql.resultset.ResultSetFormat
import com.hp.hpl.jena.query.ResultSet
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.sdb.SDBFactory
import com.hp.hpl.jena.sdb.store.DatasetStore
import com.hp.hpl.jena.query.*

/**
*
* @author Yunjia Li
* This is the controller to do the dereferencing of rdf and redirect it to the replay page.
*
*/
class LinkedDataController {

	static allowedMethods = [resources:'GET',resourcesData:'GET', annotations:'GET',annotationsData:'GET',users:'GET',usersData:'GET']
	
	
	def g= new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()
	def permService
	def linkedDataService
	def securityService
	def utilsService
    
	/*
	 * The ID for resources, use 303 redirection according to content negotiation
	 */
	def resources = {
		//println "index"
		//Yunjia: Try to throw exceptions
		log.debug("request format:"+ request.format)
		def resource = Resource.findById(params.id?.toString())
		if(!resource )
		{
			log.debug "resource with id {params.id} not found"
			response.sendError(404)
			response.contentType="text/plain"
			response.outputStream.flush()
			return
		}
		else
		{
			RedirectData redirectData = linkedDataService.getRedirectDataFromResource(resource)
			if(!redirectData)
			{
				log.debug "3 possibilities: (1) the resource is not published as linked data yet. (2) You are note permitted to see it (3) it is not related to any annotation"
				response.sendError(404)
				response.contentType="text/plain"
				response.outputStream.flush()
				return
			}
			else
			{
				def redirectURL = null
				//if request for webvtt, we can just return the webvtt file
				request.withFormat{
					vtt{
						if(resource.instanceOf(WebVTTResource) || resource.instanceOf(WebVTTCue))
						{
							redirectURL = g.createLink(controller:'webVTTResource',action:'getWebVTT', id:resource.id).toString()
						}
						else
						{
							redirectURL = linkedDataService.getHTMLRepresentationURI(redirectData)
						}
					}
					rdf {
						//303 Redirect to data
						redirectURL = g.createLink(action:'resourcesData',id:params.id).toString()
					}
					//We can add video, images later
					html {
						//303 Redirect to replay page with fragment information if needed
						//Get the resource and check what resource it is, then pass the fragment t=xx,xx to replay action in recording controller
						redirectURL = linkedDataService.getHTMLRepresentationURI(redirectData)
					}
				}
				//if redirectURL is null, follow the html page
				if(redirectURL == null)
				{
					redirectURL = linkedDataService.getHTMLRepresentationURI(redirectData)
				}
				response.setStatus(HttpServletResponse.SC_SEE_OTHER)
				response.setHeader("Location",redirectURL)
				response.contentType="text/plain"
				response.outputStream.flush()
				//println "here"
				return
			}
		}
	}
	
	/*
	 * The ID for annotations, use 303 redirection according to content negotiation
	 */
	def annotations = {
		log.debug("request format:"+ request.format)
		def annotation = ResourceAnnotation.findById(params.id?.toString())
		if(!annotation)
		{
			response.sendError(404)
			response.contentType="text/plain"
			response.outputStream.flush()
			return
		}
		else
		{
			RedirectData redirectData = linkedDataService.getRedirectDataFromAnnotation(annotation)
			if(!redirectData)
			{
				response.sendError(404)
				response.contentType="text/plain"
				response.outputStream.flush()
				return
			}
			else
			{
				def redirectURL = null
				//if request for webvtt, we can just return the webvtt file
				request.withFormat{
					rdf {
						//303 Redirect to data
						redirectURL = g.createLink(action:'annotationsData',id:params.id).toString()
					}
					//We can add video, images later
					html {
						//303 Redirect to replay page with fragment information if needed
						//Get the resource and check what resource it is, then pass the fragment t=xx,xx to replay action in recording controller
						redirectURL = linkedDataService.getHTMLRepresentationURI(redirectData)
					}
				}
				if(redirectURL == null)
				{
					redirectURL = linkedDataService.getHTMLRepresentationURI(redirectData)
				}
				response.setStatus(HttpServletResponse.SC_SEE_OTHER)
				response.setHeader("Location",redirectURL)
				response.contentType="text/plain"
				response.outputStream.flush()
				return
			}
		}
	}
	
	/*
	 * The ID for users, use 303 redirection according to content negotiation
	 */
	def users = {
		log.debug("request format:"+ request.format)
		def user = User.findById(params.id?.toString())
		//The use must exist and it should not be the admin
		if(!user || securityService.isAdmin(user))
		{
			response.sendError(404)
			response.contentType="text/plain"
			response.outputStream.flush()
			return
		}
		else
		{
			def redirectURL = null
			
			//if request for webvtt, we can just return the webvtt file
			request.withFormat{
				rdf {
					//303 Redirect to data
					redirectURL = g.createLink(action:'usersData',id:params.id).toString()
				}
				//We can add video, images later
				html {
					redirectURL = g.createLink(controller:'user',action:'show',id:params.id).toString()
				}
			}
			if(redirectURL == null)
			{
				redirectURL = g.createLink(controller:'user',action:'show',id:params.id).toString()
			}
			response.setStatus(HttpServletResponse.SC_SEE_OTHER)
			response.setHeader("Location",redirectURL)
			response.contentType="text/plain"
			response.outputStream.flush()
			return
		}
	}
	
	/*
	 * return RDF data for resources
	 */
	def resourcesData = {
		//check the permission first
		
		def resource = Resource.findById(params.id?.toString())
		try
		{
			linkedDataService.buildResourceData(response.outputStream,resource)	
			
			//println output
			response.status = 200
			response.contentType="application/rdf+xml"
			//response.outputStream << output
			response.outputStream.flush()
			return
		}
		catch(RDFGenerationException rdfEx)
		{
			log.debug rdfEx.getMessage()
			response.sendError(404)
			response.contentType="text/plain"
			response.outputStream.flush()
			return
		}
	}
	
	/*
	 * return RDF data for annotations
	 */
	def annotationsData = {
		log.debug("request format:"+ request.format)
		def annotation = ResourceAnnotation.findById(params.id?.toString())
		try
		{
			linkedDataService.buildAnnotationData(response.outputStream,annotation)
			
			//println output
			response.status = 200
			response.contentType="application/rdf+xml"
			//response.outputStream << output
			response.outputStream.flush()
			return
		}
		catch(RDFGenerationException rdfEx)
		{
			log.debug rdfEx.getMessage()
			response.sendError(404)
			response.contentType="text/plain"
			response.outputStream.flush()
			return
		}
	}
	
	/*
	 * return RDF data for users
	 */
	def usersData = {
		log.debug("request format:"+ request.format)
		
		def user = User.findById(params.id?.toString())
		try
		{
			linkedDataService.buildUserData(response.outputStream,user)
			
			//println output
			response.status = 200
			response.contentType="application/rdf+xml"
			//response.outputStream << output
			response.outputStream.flush()
			return
		}
		catch(RDFGenerationException rdfEx)
		{
			log.debug rdfEx.getMessage()
			response.sendError(404)
			response.contentType="text/plain"
			response.outputStream.flush()
			return
		}
	}
	
	/*
	 * the sparql endpoint for Synote, implemented by Jena ARQ
	 * 
	 */
	def sparql = {
		
		//ask for the page
		//println params.query
		if(!params.query)
		{
			/*response.status = 200
			response.contentType="text/plain"
			response.outputStream << "Query string is empty"
			response.outputStream.flush()
			return*/
			def prefixList = V.getVocabularies()
			StringBuilder builder = new StringBuilder()
			prefixList.each{p->
				builder.append(("PREFIX "+p[0]+":"+"<"+p[1]+">").encodeAsHTML())
				builder.append("\n")
			}
			return [prefixString:builder.toString()]
		}
		
		//use result formatter to get the correct response type
		String queryString = params.query
		if(!params.output)
			params.output = "json"

		def store = linkedDataService.getSDBInstance()
		//Model model = SDBFactory.connectDefaultModel(store)
		Dataset ds = DatasetStore.create(store)
		Query sparqlQuery
		QueryExecution qexec
		try
		{
			sparqlQuery = QueryFactory.create(queryString)
			qexec = QueryExecutionFactory.create(sparqlQuery, ds)
		}
		catch(Exception exp)
		{	
			exp.printStackTrace()
			response.contentType = "text/plain"
			response.outputStream << "Error!"+exp.getMessage()
			response.outputStream.flush()
			throw exp
			return
		}
		
		try
		{
			if(sparqlQuery.isSelectType())
			{
				ResultSet results = null
				results = qexec.execSelect()
				
				if(results == null)
				{
					response.contentType = "text/plain"
					response.outputStream << "No result is found."
				}
				else
				{
					switch (params.output) {
						case "json":
							response.contentType = "application/json"
							ResultSetFormatter.outputAsJSON(response.outputStream,results)
							break
						case "htmltab":
							ResultSetFormatter.outputAsJSON(response.outputStream, results);
							break
						case "rdfxml":
							response.contentType = "application/rdf+xml"
							ResultSetFormatter.outputAsRDF(response.outputStream,results)
							break
						default:
							response.contentType = "application/json"
							ResultSetFormatter.output(response.outputStream,results,ResultSetFormat.syntaxText)
							break
					}
				}
			}
			else if(sparqlQuery.isDescribeType() || sparqlQuery.isConstructType())
			{
				Model results = null
				if(sparqlQuery.isDescribeType())
				{
					results = qexec.execDescribe()
				}
				else
				{
					results = qexec.execConstruct()	
				}
				
				if(results == null)
				{
					response.contentType = "text/plain"
					response.outputStream << "No result is found"	
				}
				else
				{
					switch (params.output) {
						case "rdfxml":
							response.contentType = "application/rdf+xml"
							//response.setHeader("Content-disposition", "attachment;filename=sparql")
							results.write(response.outputStream,"RDF/XML")
							break
						default:
							response.contentType = "text/rdf+n3"
							//response.setHeader("Content-disposition", "attachment;filename=sparql.n3")
							results.write(response.outputStream,"N3")
							break
					}
				}
			}
			else
			{
				throw new RDFGenerationException("Cannot recognise the sparql query.")	
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace()
			response.contentType = "text/plain"
			response.outputStream << "Error!"+ex.getMessage()
			throw ex
		}
		finally
		{
			response.outputStream.flush()
			qexec.close()
			return
		}
	}
}

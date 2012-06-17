package org.synote.linkeddata

import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import org.springframework.web.context.request.RequestContextHolder as RCH
import org.synote.linkeddata.RedirectData
import org.synote.user.User
import org.synote.resource.Resource
import org.synote.resource.single.text.MultimediaTag
import org.synote.resource.single.text.MultimediaTextNote
import org.synote.resource.compound.MultimediaResource
import org.synote.resource.single.text.SynmarkTag
import org.synote.resource.single.text.SynmarkTextNote
import org.synote.resource.compound.SynmarkResource
import org.synote.resource.compound.PresentationResource
import org.synote.resource.single.binary.PresentationSlide
import org.synote.resource.compound.WebVTTResource
import org.synote.annotation.ResourceAnnotation
import org.synote.annotation.Annotation
import org.synote.resource.single.text.WebVTTCue
import org.synote.annotation.synpoint.Synpoint
import org.synote.linkeddata.exception.RDFGenerationException

import org.synote.config.ConfigurationService
import org.synote.permission.PermService
import org.synote.utils.UtilsService
import org.synote.user.SecurityService
import org.synote.linkeddata.Vocabularies as V
import org.synote.linkeddata.RDFBuilder
import org.synote.linkeddata.exception.RDFGenerationException
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime

import org.synote.player.client.TimeFormat

import net.rootdev.javardfa.jena.RDFaReader
import com.hp.hpl.jena.rdf.model.Model
import org.w3c.tidy.Tidy

import java.util.Calendar
import java.io.InputStream
import java.io.ByteArrayInputStream

class LinkedDataService {

    static transactional = true
	def configurationService
	def permService
	def utilsService
	def securityService

    def getBaseURI() {
		return configurationService.getConfigValue("org.synote.linkeddata.settings.baseURI")+
			configurationService.getConfigValue("org.synote.linkeddata.settings.serverContext")
    }
	/*
	* Get the base uris for the ids, the id is not included
	*/
	def getResourceBaseURI()
	{
		getBaseURI()+"resources/"
	}
	
	def getAnnotationBaseURI()
	{
		getBaseURI()+"annotations/"
	}
	
	def getUserBaseURI()
	{
		getBaseURI()+"users/"
	}
	
	def getHTMLBaseURI()
	{
		getBaseURI()+"recording/replay/"	
	}
	
	def getDefaultThumbnailImageURI()
	{
		getBaseURI()+"images/default.png"
	}
	
	/*
	 * Get the base uris for the rdf data, the id is not included
	 */
	def getResourceDataBaseURI()
	{
		getBaseURI()+"resources/data/"
	}
	
	def getAnnotationDataBaseURI()
	{
		getBaseURI()+"annotations/data/"
	}
	
	def getUserDataBaseURI()
	{
		getBaseURI()+"users/data/"
	}
	
	/*
	 * get the data necessary for redirection from the requested resource
	 * if the return is null, it means there is something wrong with the resource, e.g. you are not entitled to view it
	 * 
	 * If later new resources are added, we need to update this programme
	 */
	def getRedirectDataFromResource(Resource resource)
	{
		def recording =  null
		def annotation = null
		String frag = ""
		def compound_resource = null
		def synpoint = null
		
		if(resource.instanceOf(MultimediaResource))
		{
			recording = resource
		}
		else if(resource.instanceOf(MultimediaTextNote)|| resource.instanceOf(MultimediaTag))
		{
			recording = resource.multimedia
		}
		else
		{
			//resource is presentation or webvtt resource, go to the multimedia directly, with no fragment
			if(resource.instanceOf(PresentationResource) || resource.instanceOf(WebVTTResource))
			{
				compound_resource = resource
			}
			else if(resource.instanceOf(SynmarkResource))
			{
				compound_resource = resource
				
			}
			else if(resource.instanceOf(WebVTTCue))
			{
				compound_resource = resource.webVTTFile
			}
			else if(resource.instanceOf(PresentationSlide))
			{
				compound_resource = resource.presentation
			}
			else if(resource.instanceOf(SynmarkTag)|| resource.instanceOf(SynmarkTextNote))
			{
				compound_resource = resource.synmark
			}
			
			else
			{
				return null
			}
			
			if(compound_resource != null)
				annotation = ResourceAnnotation.findBySource(compound_resource)
			if(annotation)
			{
				recording = annotation.target
				def perm = permService.getPerm(recording)
				if(perm?.val <=0)
				{
					return null
				}
				
				if(resource.instanceOf(WebVTTCue))
				{
					//println resource.cueIndex
					synpoint = annotation.synpoints.find{it.sourceStart == resource.cueIndex}
				}
				else if(resource.instanceOf(PresentationSlide))
				{
					synpoint = annotation.synpoints.find{it.sourceStart == resource.index}
				}
				else if(resource.instanceOf(SynmarkTag)|| resource.instanceOf(SynmarkTextNote) || resource.instanceOf(SynmarkResource))
				{
					synpoint = annotation.synpoints?.toArray()[0]
				}
			}
			else
				return null
			
		}
		frag = getFragmentStringFromSynpoint(synpoint)
		
		return new RedirectData(recording,compound_resource,annotation,synpoint,frag)
	}
	
	/*
	* get the data necessary for redirection from the requested annotation
	* if the return is null, it means there is something wrong with the annotation, e.g. you are not entitled to view it
	*/
	def getRedirectDataFromAnnotation(ResourceAnnotation annotation)
	{
		def recording =  null
		String frag = ""
		def compound_resource = null
		def synpoint = null
		if(annotation)
		{
			recording =  annotation.target
			compound_resource = annotation.source
			def perm = permService.getPerm(recording)
			if(perm?.val <=0)
			{
				return null
			}
			
			if(compound_resource.instanceOf(SynmarkResource))
			{
				synpoint = annotation.synpoints?.toArray()[0]
			}
			
			if(synpoint != null)
			{
				frag = "t="+TimeFormat.getInstance().toWebVTTTimeString(synpoint.targetStart)
				if(synpoint.targetEnd)
				{
					frag+=","+TimeFormat.getInstance().toWebVTTTimeString(synpoint.targetEnd)
				}
			}
			
			return new RedirectData(recording,compound_resource,annotation,synpoint,frag)
		}
		else
			return null
		
		//return getRedirectDataFromResource(annotation.source)
	}
	
	/*
	 * Get fragmentString from Synpoint
	 */
	def getFragmentStringFromSynpoint(Synpoint synpoint)
	{
		return getFragmentString(synpoint?.targetStart, synpoint?.targetEnd)
	}
	
	/*
	 * get media fragment string from start and end
	 */
	def getFragmentString(int start, int end)
	{
		String frag=""
		
		if(start)
		{
			frag = "t="+TimeFormat.getInstance().toWebVTTTimeString(start)
		}
		else
			frag="t=0"
		
		if(end)
		{
			frag+=","+TimeFormat.getInstance().toWebVTTTimeString(end)
		}
		return frag
	}
	
	/*
	 * The the html representation uri from the redirected data
	 */
	def getHTMLRepresentationURI(RedirectData rData)
	{
		//def g= new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()
		
		def htmlURI = getHTMLBaseURI()+rData.recording?.id
		if(rData.hasFragment())
		{
			//println "frag:"+redirectData.frag?.trim()
			htmlURI = utilsService.attachFragmentToURI(htmlURI,rData.getFragment())
		}
		
		return htmlURI
	}
	
	/*
	 * The the resource rdf representation uri from the redirected data
	 */
	def getResourceRDFRepresentationURI(RedirectData rData)
	{
		
		def dataURI = getResourceDataBaseURI()+rData.recording?.id
		if(rData.hasFragment())
		{
			//println "frag:"+redirectData.frag?.trim()
			dataURI = utilsService.attachFragmentToURI(dataURI,rData.getFragment())
		}
		
		return dataURI
	}
	
	/*
	 * parse RDFa in the xhtml string
	 * MUST catch the exception when use
	 */
	def parseRDFa(String xhtmlStr, Model model, String baseURI)
	{
		if(xhtmlStr!=null && model!=null && baseURI != null)
		{
			Class.forName("net.rootdev.javardfa.jena.RDFaReader")
			InputStream is = new ByteArrayInputStream(xhtmlStr.getBytes("UTF-8"))
			Tidy tidy = new Tidy()
			tidy.setShowErrors(0)
			tidy.setShowWarnings(false)
			tidy.setHideComments(true)
			tidy.setQuiet(true)
			tidy.setXHTML(true)
			ByteArrayOutputStream baos = new ByteArrayOutputStream()
			tidy.parse(is,baos)
			InputStream tidy_xhtml = new ByteArrayInputStream(baos.toByteArray())
			try
			{
				model.read(tidy_xhtml,baseURI,"XHTML")
			}
			catch(Exception ex)
			{
				log.debug("The note is not a valid xhtml:"+xhtmlStr)
			}
		}
	}
	
	/*
	 * The reusable method to build resource data
	 * params: output stream
	 */
	def buildResourceData(outputStream,resource) throws RDFGenerationException
	{
		if(!outputStream)
		{
			throw new RDFGenerationException("Missing output stream")
			return
		}
		else if(!resource)
		{
			throw new RDFGenerationException("Cannot find resource")
			return
		}
		else
		{
			RedirectData redirectData = getRedirectDataFromResource(resource)
			if(!redirectData)
			{
				throw new RDFGenerationException("Cannot get RDF from the resource. 3 possibilities: (1) the resource is not published as linked data yet. (2) You are note permitted to see it (3) it is not related to any annotation")
				return
			}
			else
			{
				String resURI = getResourceBaseURI()+resource.id
				def rdfBuilder = new RDFBuilder(outputStream)
				
				rdfBuilder.model.setNsPrefix("ma",V.MAONT_NS[1])
				rdfBuilder.model.setNsPrefix("dc",V.DC_NS[1])
				rdfBuilder.model.setNsPrefix("rdfs",V.RDFS_NS[1])
				rdfBuilder.model.setNsPrefix("dcterms",V.DCTERMS_NS[1])
				rdfBuilder.model.setNsPrefix("ore",V.ORE_NS[1])
				rdfBuilder.model.setNsPrefix("schemaorg",V.SCHEMAORG_NS[1])
				rdfBuilder.model.setNsPrefix("oac",V.OAC_NS[1])
				
				//rdfBuilder.model.setNsPrefix("dbpedia","http://dbpedia.org/resource/")
				//rdfBuilder.model.setNsPrefix("lode","http://linkedevents.org/ontology/")
				def output = null
				//println "class:"+resource.class.name
				//Now start to build rdf based on different resource classes
				output = rdfBuilder.xml{
					defaultNamespace resURI
					namespace rdf:V.RDF_NS[1]
					namespace rdfs:V.RDFS_NS[1]
					namespace dc:V.DC_NS[1]
					namespace dcterms:V.DCTERMS_NS[1]
					namespace ma:V.MAONT_NS[1]
					namespace ore:V.ORE_NS[1]
					namespace schemaorg:V.SCHEMAORG_NS[1]
					namespace oac:V.OAC_NS[1]
					//namespace dbpedia:"http://dbpedia.org/resource/"
					//namespace lode:"http://linkedevents.org/ontology/"
					subject(""){
						//Some common properties
						if(resource.title)
						{
							property "dc:title":resource.title
						}
						//dcterms creator is better, it is not the creator of the multimedia resource it self, he is just the uploader
						predicate "dcterms:creator":rdfBuilder.model.createResource(getUserBaseURI()+resource.owner?.id)
						def cal = Calendar.getInstance()
						cal.setTimeInMillis(resource.dateCreated.getTime())
						predicate "schemaorg:dateCreated":new XSDDateTime(cal)
						cal.setTimeInMillis(resource.lastUpdated.getTime())
						predicate "schemaorg:dateModified":new XSDDateTime(cal)
						subject("")
						{
							predicate("rdfs:isDefinedBy"){
								subject(getResourceDataBaseURI()+resource.id){
									property "dc:format":"application/rdf+xml"
								}
							}
						}
						subject("")
						{
							predicate ("rdfs:seeAlso"){
								subject(getHTMLRepresentationURI(redirectData)){
									property "dc:format":"application/xhtml+xml"
								}
							}
						}
						switch(resource.class.name) //class specific properties
						{
							case "org.synote.resource.compound.MultimediaResource":
								predicate "rdf:type":rdfBuilder.model.createResource(V.MAONT_NS[1]+"MediaResource")
								predicate "rdf:type":rdfBuilder.model.createResource(V.OAC_NS[1]+"Target")
								property "rdfs:label":resource.title
								property "ma:locator":resource.url?.url
								//hasFragment
								def annotations = ResourceAnnotation.findAllByTarget(resource)
								for(a in annotations)
								{
									def sourceResource = a.source
									def syns = Synpoint.findAllByAnnotation(a)
									for(syn in syns)
									{
										//a.synpoints.each{syn->
										subject(""){
											predicate("ma:hasFragment"){
												String f = getFragmentStringFromSynpoint(syn)
												String mfURIStr = utilsService.attachFragmentToURI(resURI,f)
												subject(mfURIStr){
													predicate "rdf:type":rdfBuilder.model.createResource(V.MAONT_NS[1]+"MediaFragment")
													predicate "ma:isFragmentOf":rdfBuilder.model.createResource(resURI)
													predicate "dcterms:creator":rdfBuilder.model.createResource(getUserBaseURI()+a.owner?.id)
													property "ma:locator":utilsService.attachFragmentToURI(resource.url?.url,f)
													predicate("rdfs:seeAlso"){
														subject(utilsService.attachFragmentToURI(getHTMLRepresentationURI(redirectData),f)){
															property "dc:format":"application/xhtml+xml"
														}
													}
													subject(mfURIStr)
													{
														predicate("rdfs:isDefinedBy")
														{
															subject(utilsService.attachFragmentToURI(getResourceRDFRepresentationURI(redirectData),f)){
																property "dc:format":"application/rdf+xml"
															}
														}
													}
													subject(mfURIStr){
													//check the source resource of the annotation and add predicates to the media fragments
														switch(sourceResource.class.name){
															case "org.synote.resource.compound.SynmarkResource":
																predicate "rdf:type":rdfBuilder.model.createResource(V.OAC_NS[1]+"Target")
																if(sourceResource.title)
																	property "ma:title":sourceResource.title?.encodeAsHTML()
																	
																if(sourceResource.tags?.size() > 0)
																{
																	for(t in sourceResource.tags)
																	{
																		//the keyword should be a URI, see http://www.w3.org/TR/mediaont-10/#correspondance-id3
																		predicate "ma:hasKeyword":rdfBuilder.model.createResource(getResourceBaseURI()+t.id)
																	}
																}
																//Yunjia: we can use RDFa richtext editor tinyMCE etc, and then use some programme
																//to extract RDFa triples from the note and embed it here!
																if(sourceResource.note)
																{
																	//parse note.content and get triples first
																	property "ma:description":sourceResource.note?.content?.encodeAsHTML()
																	parseRDFa(sourceResource.note?.content, rdfBuilder.model, mfURIStr)
																}
																
																break
															case "org.synote.resource.compound.PresentationResource":
																def slide = PresentationSlide.findByPresentationAndIndex(sourceResource,syn.sourceStart)
																if(slide)
																	predicate "ma:hasRelatedImage":rdfBuilder.model.createResource(getResourceBaseURI()+slide.id)
																break
															//check both cue and webvttresource
															case "org.synote.resource.compound.WebVTTResource":
																def cue = WebVTTCue.findByWebVTTFileAndCueIndex(sourceResource,syn.sourceStart)
																if(cue)
																{
																	predicate "ma:hasSubtitling":rdfBuilder.model.createResource(getResourceBaseURI()+cue.id)
																}
																subject(""){
																	predicate "ma:hasSubtitling":rdfBuilder.model.createResource(getResourceBaseURI()+sourceResource.id)
																}
																break
															default:
																break
														}
													}
												}
											}
										}
									}
								}
								break
							case "org.synote.resource.compound.SynmarkResource":
								predicate "rdf:type":rdfBuilder.model.createResource(V.OAC_NS[1]+"Body")
								predicate "rdf:type":rdfBuilder.model.createResource(V.ORE_NS[1]+"Aggregation")
								if(resource.note)
								{
									predicate "ore:aggregates":rdfBuilder.model.createResource(getResourceBaseURI()+resource.note?.id)
								}
								if(resource.tags?.size()>0)
								{
									for(t in resource.tags)
									{
										predicate "ore:aggregates":rdfBuilder.model.createResource(getResourceBaseURI()+t.id)
									}
								}
								break;
							case "org.synote.resource.single.text.SynmarkTag":
								predicate "rdf:type":rdfBuilder.model.createResource(V.ORE_NS[1]+"AggregatedResource")
								predicate "ore:isAggregatedBy":	rdfBuilder.model.createResource(getResourceBaseURI()+resource.synmark?.id)
								predicate "rdfs:label":resource.content?.encodeAsHTML()
								break;
							case "org.synote.resource.single.text.SynmarkTextNote":
								predicate "rdf:type":rdfBuilder.model.createResource(V.ORE_NS[1]+"AggregatedResource")
								predicate "ore:isAggregatedBy":	rdfBuilder.model.createResource(getResourceBaseURI()+resource.synmark?.id)
								predicate "rdfs:label":resource.content?.encodeAsHTML()
								break;
							case "org.synote.resource.compound.PresentationResource":
								predicate "rdf:type":rdfBuilder.model.createResource(V.OAC_NS[1]+"Body")
								predicate "rdf:type":rdfBuilder.model.createResource(V.ORE_NS[1]+"Aggregation")
								if(resource.slides?.size()>0)
								{
									for(s in resource.slides)
									{
										predicate "ore:aggregates":rdfBuilder.model.createResource(getResourceBaseURI()+s.id)
									}
								}
								break;
							case "org.synote.resource.single.binary.PresentationSlide":
								//locator
								predicate "rdf:type":rdfBuilder.model.createResource(V.ORE_NS[1]+"AggregatedResource")
								predicate "rdf:type":rdfBuilder.model.createResource(V.MAONT_NS[1]+"Image")
								predicate "ore:isAggregatedBy":	rdfBuilder.model.createResource(getResourceBaseURI()+resource.presentation?.id)
								predicate "ma:locator":resource.url
								break;
							case "org.synote.resource.compound.WebVTTResource":
								predicate "rdf:type":rdfBuilder.model.createResource(V.OAC_NS[1]+"Body")
								predicate "rdf:type":rdfBuilder.model.createResource(V.ORE_NS[1]+"Aggregation")
								if(resource.cues?.size()>0)
								{
									for(c in resource.cues)
									{
										predicate "ore:aggregates":rdfBuilder.model.createResource(getResourceBaseURI()+c.id)
									}
								}
								break;
							case "org.synote.resource.single.text.WebVTTCue":
								predicate "rdf:type":rdfBuilder.model.createResource(V.ORE_NS[1]+"AggregatedResource")
								predicate "ore:isAggregatedBy":	rdfBuilder.model.createResource(getResourceBaseURI()+resource.webVTTFile?.id)
								predicate "rdfs:label":resource.content?.encodeAsHTML()
								break;
							default:
								break;
						}
					}
				}
				return
			}
			
		}
	}
	
	/*
	* The reusable method to build annotation data
	* params: output stream
	*/
   def buildAnnotationData(outputStream,annotation) throws RDFGenerationException
   {
	   if(!outputStream)
	   {
		   throw new RDFGenerationException("Missing output stream")
		   return
	   }
	   else if(!annotation)
	   {
		   throw new RDFGenerationException("Cannot find annotation")
		   return
	   }
	   else
	   {
		   RedirectData redirectData = getRedirectDataFromAnnotation(annotation)
		   if(!redirectData)
		   {
			   throw new RDFGenerationException("Cannot get RDF from the annotation. 3 possibilities: (1) the resource is not published as linked data yet. (2) You are note permitted to see it (3) it is not related to any annotation")
			   return
		   }
		   else
		   {
			   //Now start to build rdf based on different resource classes
			   def rdfBuilder = new RDFBuilder(outputStream)
			   rdfBuilder.model.setNsPrefix("dc",V.DC_NS[1])
			   rdfBuilder.model.setNsPrefix("rdfs",V.RDFS_NS[1])
			   rdfBuilder.model.setNsPrefix("dcterms",V.DCTERMS_NS[1])
			   rdfBuilder.model.setNsPrefix("oac",V.OAC_NS[1])
			   def output = null
			   output = rdfBuilder.xml{
				   defaultNamespace getAnnotationBaseURI()
				   namespace rdf:V.RDF_NS[1]
				   namespace rdfs:V.RDFS_NS[1]
				   namespace dc:V.DC_NS[1]
				   namespace dcterms:V.DCTERMS_NS[1]
				   namespace oac:V.OAC_NS[1]
				   subject(getAnnotationBaseURI()+annotation.id){
					   predicate "rdf:type":rdfBuilder.model.createResource(V.OAC_NS[1]+"Annotation")
					   if(redirectData.compoundResource?.owner?.id)
					   {
						   property "dcterms:creator":rdfBuilder.model.createResource(getUserBaseURI()+redirectData.compoundResource.owner.id)
						   property "oac:hasBody":rdfBuilder.model.createResource(getResourceBaseURI()+redirectData.compoundResource.id)
					   }
					   property "oac:hasTarget":rdfBuilder.model.createResource(
							   utilsService.attachFragmentToURI(getResourceBaseURI()+redirectData.recording.id,
								   (redirectData.hasFragment()?(redirectData.getFragment()):"")))
					   predicate("rdfs:seeAlso"){
						   subject(getHTMLRepresentationURI(redirectData)){
							   property "dc:format":"application/xhtml+xml"
						   }
					   }
					   
					   subject(getAnnotationBaseURI()+annotation.id){
						   predicate("rdfs:isDefinedBy"){
							   subject(getAnnotationDataBaseURI()+annotation.id){
								   property "dc:format":"application/xhtml+xml"
							   }
						   }
					   }
				   }
			   }
			   return
		   } 
	   }
   }
   
   /*
   * The reusable method to build user data
   * params: output stream
   */
  def buildUserData(outputStream,user) throws RDFGenerationException
  {
	  if(!outputStream)
	  {
		  throw new RDFGenerationException("Missing output stream")
		  return
	  }
	  else if(!user || securityService.isAdmin(user))
	  {
		  throw new RDFGenerationException("Cannot find user")
		  return
	  }
	  else
	  {
		  def rdfBuilder = new RDFBuilder(outputStream)
		  rdfBuilder.model.setNsPrefix("foaf",V.FOAF_NS[1])
		  def output = null
		  output = rdfBuilder.xml{
			  defaultNamespace getUserBaseURI()
			  namespace rdf:V.RDF_NS[1]
			  //namespace rdfs:"http://www.w3.org/2000/01/rdf-schema#"
			  namespace foaf:V.FOAF_NS[1]
			  subject(getUserBaseURI()+user.id){
				  predicate "rdf:type":rdfBuilder.model.createResource(V.FOAF_NS[1]+"Person")
				  property "foaf:name":user.userName
				  property "foaf:mbox":user.email
				  property "foaf:givenName":user.firstName
				  property "foaf:familyName":user.lastName
			  }
		  }
		  return
	  }
  }
}

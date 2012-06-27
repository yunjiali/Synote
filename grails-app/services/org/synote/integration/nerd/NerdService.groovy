package org.synote.integration.nerd

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken
import fr.eurecom.nerd.client.*
import fr.eurecom.nerd.client.schema.*

import java.lang.reflect.Type
import java.util.UUID

import org.synote.resource.compound.*
import org.synote.resource.Resource
import org.synote.resource.single.text.TagResource
import org.synote.resource.single.text.TextNoteResource
import org.synote.resource.single.text.WebVTTCue
import org.synote.annotation.AnnotationAnnotation;
import org.synote.annotation.ResourceAnnotation

class NerdService {

    static transactional = true

    def getNerdExtractor(String extractor_name) 
	{
		switch(extractor_name?.toLowerCase()){
			case "alchemyapi":
				return Extractor.ALCHEMYAPI
			case "spotlight":
				return Extractor.DBPEDIA_SPOTLIGHT
			case "evri":
				return Extractor.EVRI
			case "extractiv":
				return Extractor.EXTRACTIV
			case "ontotext":
				return Extractor.ONTOTEXT
			case "opencalais":
				return Extractor.OPENCALAIS
			case "saplo":
				return Extractor.SAPLO
			case "wikimeta":
				return Extractor.WIKIMETA
			case "yahoo":
				return Extractor.YAHOO
			case "zemanta":
				return Extractor.ZEMANTA
			default:
				return null	
		}
    }
	
	def getNerdExtractorName(Extractor extr)
	{
		switch(extr){
			case Extractor.ALCHEMYAPI:
				return "alchemyapi"
			case Extractor.DBPEDIA_SPOTLIGHT:
				return "spotlight"
			case Extractor.EVRI:
				return "evri"
			case Extractor.EXTRACTIV:
				return "extractiv"
			case Extractor.ONTOTEXT:
				return "ontotext"
			case Extractor.OPENCALAIS:
				return "opencalais"
			case Extractor.SAPLO:
				return "saplo"
			case Extractor.WIKIMETA:
				return "wikimeta"
			case Extractor.YAHOO:
				return "yahoo"
			case Extractor.ZEMANTA:
				return "zemanta"
			default:
				return null
		}
	}
	
	/*
	 * Get text, field name and the corresponding synpointid from the resource
	 */
	def getTextFromResource(Resource resource)
	{
		def text=''
		def field = 'Title' //the type of the field, title, description or tag
		if(resource.instanceOf(MultimediaResource) || resource.instanceOf(SynmarkResource))
		{
			text = resource.title
		}
		else if(resource.instanceOf(TagResource))
		{
			text = resource.content
			field = 'Tag'
		}
		else if(resource.instanceOf(TextNoteResource))
		{
			text = resource.content
			field = "Description"
		}
		else if(resource.instanceOf(WebVTTCue))
		{
			//Maybe speaker?
			text = resource.content
			field = ""
		}
		
		return [text:text,field:field]
	}
	
	
	/*
	 * return the Extraction object defined in fr.eurecom.nerd.client.schema.Extraction from json response
	 * return:
	 * List<Extraction>
	 */
	def getExtractionFromJSON(String jsonExtraction)
	{
		Gson gson = new Gson()
		Type listType = new TypeToken<List<Extraction>>(){}.getType()
		List<Extraction> extractions = gson.fromJson(jsonExtraction, listType)
		return extractions
	}
}

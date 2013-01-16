package org.synote.integration.nerd

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken
import fr.eurecom.nerd.client.*
import fr.eurecom.nerd.client.type.*
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
				return ExtractorType.ALCHEMYAPI
			case "spotlight":
				return ExtractorType.DBPEDIA_SPOTLIGHT
			case "evri":
				return ExtractorType.EVRI
			case "extractiv":
				return ExtractorType.EXTRACTIV
			case "lupedia":
				return ExtractorType.LUPEDIA
			case "opencalais":
				return ExtractorType.OPENCALAIS
			case "saplo":
				return ExtractorType.SAPLO
			case "uep":
				return ExtractorType.SEMITAGS
			case "wikimeta":
				return ExtractorType.WIKIMETA
			case "yahoo":
				return ExtractorType.YAHOO
			case "zemanta":
				return ExtractorType.ZEMANTA
			case "combined":
				return ExtractorType.COMBINED
			default:
				return null	
		}
    }
	
	def getNerdExtractorName(ExtractorType extr)
	{
		switch(extr){
			case ExtractorType.ALCHEMYAPI:
				return "alchemyapi"
			case ExtractorType.DBPEDIA_SPOTLIGHT:
				return "spotlight"
			case ExtractorType.EVRI:
				return "evri"
			case ExtractorType.EXTRACTIV:
				return "extractiv"
			case ExtractorType.LUPEDIA:
				return "lupedia"
			case ExtractorType.OPENCALAIS:
				return "opencalais"
			case ExtractorType.SAPLO:
				return "saplo"
			case ExtractorType.SEMITAGS:
				return "uep"
			case ExtractorType.WIKIMETA:
				return "wikimeta"
			case ExtractorType.YAHOO:
				return "yahoo"
			case ExtractorType.ZEMANTA:
				return "zemanta"
			case ExtractorType.COMBINED:
				return "combined"
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
	 * Given the extractor's name as string, return the URI of the extractor, such as:
	 * http://nerd.eurecome.fr, http://www.zemanta.com
	 */
	def getExtractorURI(String extractor_name)
	{
		switch(extractor_name?.toLowerCase()){
			case "alchemyapi":
				return "http://www.alchemyapi.com/"
			case "spotlight":
				return "http://spotlight.dbpedia.org/"
			case "evri":
				return "http://www.evri.com/"
			case "extractiv":
				return "http://extractiv.com/"
			case "lupedia":
				return "http://lupedia.ontotext.com/"
			case "opencalais":
				return "http://www.opencalais.com/"
			case "saplo":
				return "http://saplo.com/"
			case "semitags": 
				return "http://www.tag.fr/"
			case "wikimeta":
				return "http://www.wikimeta.com/"
			case "yahoo":
				return "http://www.yahoo.com/"
			case "zemanta":
				return "http://www.zemanta.com/"
			case "combined":
				return "http://nerd.eurecom.fr/"
			default:
				return "http://nerd.eurecom.fr/"
		}
	}
	
	/*
	 * return the Entity object defined in fr.eurecom.nerd.client.schema.Entity from json response
	 * return:
	 * List<Entity>
	 */
	def getEntityFromJSON(String jsonEntity)
	{
		Gson gson = new Gson()
		Type listType = new TypeToken<List<Entity>>(){}.getType()
		List<Entity> entities = gson.fromJson(jsonEntity, listType)
		return entities
	}
	
	/*
	 * Get the OffsetString defined in NIF
	 */
	def getNIFOffsetString(Entity e)
	{
		String strOffset = "offset_"+e.getStartChar()+"_"+e.getEndChar()+"_"+e.getLabel()
		return URLEncoder.encode(strOffset , "UTF-8").replaceAll("\\+","%20")
	}
}

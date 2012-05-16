package org.synote.integration.nerd

import fr.eurecom.nerd.client.*
import fr.eurecom.nerd.client.schema.*

import org.synote.resource.compound.*
import org.synote.resource.Resource
import org.synote.resource.single.text.TagResource
import org.synote.resource.single.text.TextNoteResource
import org.synote.resource.single.text.WebVTTCue

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
	
	/*
	 * Get text and field name from the resource
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
}

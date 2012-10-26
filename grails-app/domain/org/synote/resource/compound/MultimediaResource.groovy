package org.synote.resource.compound

import org.synote.resource.single.text.MultimediaTag
import org.synote.resource.single.text.MultimediaTextNote
import org.synote.resource.single.binary.MultimediaUrl
import java.util.UUID

import org.synote.search.resource.converter.MultimediaConverter

class MultimediaResource extends CompoundResource {

	String indexString = ""
	
	static transients = ['indexString']
	
	static searchable = { 
		  indexString converter:'multimediaConverter',boost:2.0 //the description on the whole multimedia level is more important
	}
	
	static hasMany = [tags:MultimediaTag]
	
	MultimediaUrl url
	MultimediaTextNote note
	
	/*the real start and end time of the recording*/
	Date realStarttime
	Date realEndtime
	
	/*duration in milliseconds*/
	long duration
	
	/*universal identifier*/
	String uuid = UUID.randomUUID().toString()
	
	/*if this multimedia resource is a video or audio*/
	boolean isVideo = true //We need to give a default value, or all the hibernate functions will not work
	
	static mapping = {
    	url column:'child_multimedia_url_id'
		note column:'child_note_id'
	}

    static constraints = {
    	url(nullable:false)
		note(nullable:true)
		uuid(nullable:false)
		isVideo(nullable:true)
		realStarttime(nullable:true)
		realEndtime(nullable:true)
	}
	
	void saveUrl(String newUrl)
	{
    	if(!url?.url.equals(newUrl))
		{
			url?.url = newUrl
		}
	}
	
	void saveNote(String newNote)
	{
			
		if(!note?.content.equals(newNote))
		{
			note?.content = newNote	
		}
	}
	
	void saveTags(String tags)
	{
		
	}

	public String toNIFString()
	{
		StringBuilder str = new StringBuilder()
		if(title)
		{
			str.append("<h1>"+title+"</h1>")
		}
		
		if(tags)
		{
			str.append("<span>")
			tags.each {tag ->
				str.append(tag.content+" ")
			}
			str.append("</span>")
		}
		if(note)
		{
			str.append("<br/>"+"<p>"+note.content+"</p>")
		}
		return str.toString()
	}
	
	public String toString()
	{
		String mmStr = ''
		if(title)
		{
			mmStr += title+"\r\n"
		}
		if(tags)
		{
			tags.each {tag ->
				mmStr += tag.content+" "
			}
		}
		if(note)
		{
			mmStr += "\r\n"+note.content
		}
		return mmStr
	}
	
	def afterInsert()
	{
		this.index()
	}
	
	def afterUpdate()
	{
		this.reindex()
	}
	
	def beforeDelete()
	{
		this.unindex()
	}
}

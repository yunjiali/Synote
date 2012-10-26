package org.synote.resource.compound

import org.synote.resource.single.text.SynmarkTag
import org.synote.resource.single.text.SynmarkTextNote
import org.synote.search.resource.converter.SynmarkConverter

class SynmarkResource extends CompoundResource{

	String indexString = ""
	
	static transients = ['indexString']
	
	static searchable = { 
		  indexString converter:'synmarkConverter'
	}
		
	static hasMany = [tags: SynmarkTag]
	SynmarkTextNote note
	SynmarkResource next
	
	static constraints = {
		next(nullable: true)
	}
	
	static mapping = {
		note column: 'child_note_id'
		next column: 'synmark_next_id'
	}
	
	public String toString()
	{
		String synmarkStr = ''
		if(title)
		{
			synmarkStr += title+"\r\n"
		}
		if(tags)
		{
			tags.each {tag ->
				synmarkStr += tag.content+" "
			}
		}
		if(note)
		{
			synmarkStr += "\r\n"+note.content
		}
		return synmarkStr
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

	public String TagsToString()
	{
		def tagsStr = ""
		tags.each{ tag ->
			tagsStr+=tag.content+" "
		}
		return tagsStr
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

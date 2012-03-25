package org.synote.resource.compound

import org.synote.resource.single.text.SynmarkTag
import org.synote.resource.single.text.SynmarkTextNote

class SynmarkResource extends CompoundResource{

	static searchable = {
		only:['title','note','tags']
		note component:[cascade: 'all']
		tags component:[cascade: 'all']
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

	def propertiesToString ={params ->

		def fields = params.findAll{it.key.startsWith("synmark_") == true}
		boolean allFields = fields.size() == 0?true:false

		def propMap = [:]
		if(title && (fields.synmark_title || allFields))
			propMap.put("title", title)
		if(note && note.content && (fields.synmark_note || allFields))
			propMap.put("note", note.content)
		if(tags && (fields.synmark_tags || allFields))
			propMap.put("tags", TagsToString())

		return propMap
	}

	public String TagsToString()
	{
		def tagsStr = ""
		tags.each{ tag ->
			tagsStr+=tag.content+" "
		}
		return tagsStr
	}

	public static getSearchableFields()
	{
		def fields = []
		fields << 'title'
		fields << 'note'
		fields << 'tags'
		return fields
	}

	public static String getResourceAlias() {
		return "SynmarkResource"
	}
}

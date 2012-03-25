package org.synote.resource.compound

import org.synote.resource.single.text.MultimediaTag
import org.synote.resource.single.text.MultimediaTextNote
import org.synote.resource.single.binary.MultimediaUrl

class MultimediaResource extends CompoundResource {

	//TODO: Add tags and note later
	static searchable = { only:['title'] }
	
	static hasMany = [tags:MultimediaTag]
	
	MultimediaUrl url
	MultimediaTextNote note
	
	static mapping = {
    	url column:'child_multimedia_url_id'
		note column:'child_note_id'
	}

    static constraints = {
    	url(nullable:false)
		note(nullable:true)
	}
	
	void saveUrl(String newUrl)
	{
    	if(!url?.url.equals(newUrl))
		{
			url?.url = newUrl
		}
	}
	
	def propertiesToString ={

		def propMap = [:]
		if(title)
			propMap.put("title", title)

		return propMap
	}

	public static ArrayList getSearchableFields()
	{
		def fields = ['title']
	}

	public static String getResourceAlias() {
		return "MultimediaResource"
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

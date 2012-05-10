package org.synote.resource.compound

import org.synote.resource.single.text.MultimediaTag
import org.synote.resource.single.text.MultimediaTextNote
import org.synote.resource.single.binary.MultimediaUrl
import java.util.UUID

class MultimediaResource extends CompoundResource {

	//TODO: Add tags and note later
	static searchable = { only:['title'] }
	
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

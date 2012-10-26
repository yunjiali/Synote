package org.synote.resource.single.text

import org.synote.resource.compound.WebVTTResource
import org.synote.search.resource.converter.WebVTTCueConverter

class WebVTTCue extends TextResource {

    String indexString = ""
	
	static transients = ['indexString']
	
	static searchable = { 
		  indexString converter:'webVTTCueConverter'
	}
	
	static belongsTo=[webVTTFile:WebVTTResource]
	
	int cueIndex
	String cueSettings
	
	//String content is the cueText in JSON
	
	static mapping = {
		webVTTFile column: 'parent_resource_id'
		cueSettings type:'text'
	}
	
	static constraints = {
    	cueSettings(nullable:true,blank:true,size:0..65536)
		cueIndex(nullable:false)
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

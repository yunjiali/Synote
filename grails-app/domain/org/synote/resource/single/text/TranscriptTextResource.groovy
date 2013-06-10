package org.synote.resource.single.text

import org.synote.resource.compound.TranscriptResource
import org.synote.i18n.Language

class TranscriptTextResource extends TextResource{
	
	//static searchable ={
	//	only:['content']
	//}
	static belongsTo = [transcript:TranscriptResource]
	
	static mapping = {
		transcript column:'parent_resource_id'
	}
	
	/*
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
	}*/
}

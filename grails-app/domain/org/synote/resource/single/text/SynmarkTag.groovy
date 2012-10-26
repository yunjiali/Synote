package org.synote.resource.single.text

import org.synote.resource.compound.SynmarkResource

class SynmarkTag extends TagResource{

	//static searchable={
	//	only:['content']
	//}
	
	static belongsTo = [synmark: SynmarkResource]
	
	static mapping = {
    	synmark column:'parent_resource_id'
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
	}
	*/
}

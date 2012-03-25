package org.synote.search.resource.analysis

import org.synote.resource.Resource

class ResultRecord extends SearchRecord{

    static belongsTo =[query:QueryRecord]
	
	Resource resource
	String resourceClass
	String content
	boolean selected = false
	
	static mapping = {
		content type:'text'
		query column:'query_id'
		resource column:'resource_id'
	}
	
	static constraints = 
	{
    	resource()
		resourceClass()
		content()
		selected()
	}
}

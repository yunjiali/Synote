package org.synote.resource.single.text

import org.synote.resource.compound.WebVTTResource

class WebVTTCue extends TextResource {

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
}

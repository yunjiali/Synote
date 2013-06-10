package org.synote.resource.compound

import org.synote.resource.single.text.WebVTTCue
import org.synote.resource.compound.TranscriptResource

class WebVTTResource extends TranscriptResource {

	static hasMany=[cues:WebVTTCue]
	
	String fileHeader
	
	static mapping = {
		fileHeader type:'text'
	}
	
	//static mappedBy = [cues:'webVTTFile',annotates:"source",annotations:"target"]
	
	static constraints = {
    	fileHeader(nullable:false,blank:false,size:1..65536)
	}
	
}

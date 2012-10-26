package org.synote.resource.compound

import org.synote.resource.single.text.TranscriptTextResource

/*
 * Deprecated
 */
class TranscriptResource extends CompoundResource{

	/*
	* The content is Deprecated. But this class is still used as the superclass of WebVTTResource
	*/
	
	TranscriptTextResource transcript
	
	static constraints = {
		
		transcript(nullable: true)
	}
	
	static mapping = {
		transcript column: 'child_transcript_text_id'
	}
}

package org.synote.resource.compound

import org.synote.resource.single.text.TranscriptTextResource


class TranscriptResource extends CompoundResource{

	/*
	* The content is Deprecated. But this class is still used as the superclass of WebVTTResource
	*/
	static searchable =
	{
		only:['transcript']
		transcript component:[cascade: 'all']
	}
	
	TranscriptTextResource transcript
	
	static constraints = {
		
		transcript(nullable: true)
	}
	
	static mapping = {
		transcript column: 'child_transcript_text_id'
	}

	//TODO: Change it to "transcript" later
	def propertiesToString ={

		def propMap = [:]
		if(transcript.content)
			propMap.put("content", transcript.content)

		return propMap
	}

	public static ArrayList getSearchableFields()
	{
		def fields = ['content']
	}

	public static String getResourceAlias() {
		return "TranscriptResource"
	}
}

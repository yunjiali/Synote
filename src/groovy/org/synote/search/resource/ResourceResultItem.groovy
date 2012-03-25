package org.synote.search.resource;

/**
 * 
 * @author Rabbit
 * Representing a synmark, multimedia or transcript search result
 */
class ResourceResultItem implements Serializable {
	
	long id
	def recording=[:]
	def highlights = [:]
	//The hits for MultimediaResource SynmarkResource TranscriptResource respectively
	def counts = [:]
	float score
	
	public ResourceResultItem()
	{
		//Do nothing
	}
	
	//How to deal with score? We need to consider later
	public float getScore()
	{
		return score
	}
	
	public String toString()
	{
		return "id:"+id+"\r\n"+
		"recording:"+recording+"\r\n"+
		"highlights:"+highlights+"\r\n"+
		"score:"+score
	}
}

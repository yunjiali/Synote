package org.synote.search.resource;

/**
 * 
 * @author Rabbit
 * The result set of search
 */
class ResourceSearchResult implements Serializable{
	
	/*
	 * By default, return 10 results at a time
	 */
	static int MAX_RESULTS = 10
	def results = []
	def offset = 0
	def max = MAX_RESULTS
	//Every search i.e. MultimediaResource, SynmarkResource, TranscriptResource
	//may have a suggestQuery
	def suggestedQuery
	
	public ResourceSearchResult()
	{
		//Do nothing
	}
	
	public static setDefaultMaxResults(int MAX)
	{
		MAX_RESULTS = MAX
	}
	
	//If the offset and max do not match the length of results, modify the offset and max value
	/*public validateOffsetAndMax()
	 {
	 if(results.size() < max)
	 {
	 offset = 0
	 max = results.size()
	 }
	 }*/
}

package org.synote.search.resource.analysis

class QueryRecord extends SearchRecord {

    static hasMany =[results:ResultRecord, pages:PageSelectionRecord]
	
	/*
     * The query string
     */
	String queryString
	/*
	 * if it's advanced search or not
	 */
	boolean advanced=false
	
	/*
	 * if it's mine resource only search 
	 */
	boolean mineOnly = false
	/*
	 * How many results are returned in terms of Multimedia, synmarks and transcripts
	 */
	int resultNum=0
	
	/*
	 * How many resources are allowed to be seen (permission control)
	 */
	int allowedNum
	/*
	 * How many recordings are returned. How the results in resultNum are distributed into recordings
	 */
	int recordingNum=0
	/*
	 * How many hits for a search query
	 */
	int hits=0
	
	static mapping = {
		queryString type:'text'
	}
	
	static constraints = 
	{
    	queryString(blank:false)
		advanced()
		resultNum()
	}
}

package org.synote.search.resource.analysis

class PageSelectionRecord extends SearchRecord{

	static belongsTo = [query:QueryRecord]
	/*
	 * The offset of this search. This number indicates the page number
	 */
	int offset
	
	/*
	 * The max number per page
	 */
	int max
	
	static mapping = {
		query column:'query_id'
	}
	
	static constrains ={
		pageNum(min:1)	
	}
}

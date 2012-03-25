package org.synote.search.resource.analysis

import org.synote.user.User

class SearchRecord {

    String sessionId
	User user
	Date dateCreated
	Date lastUpdated
	
	static mapping = {
		user column:'user_id'	
	}
	static constraints = 
	{
    	sessionId(nullable:false)
		user(nullable:true)
	}
}

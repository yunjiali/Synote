package org.synote.user

import java.util.Date
//import org.synote.user.User

class UserAPIKey {

	static mapping = {
		table 'user_api_key'	
	}
	
    static belongsTo =[user:User]
	String userKey
	boolean expired = false
	Date dateCreated 
	Date lastUpdated
	
	static constraints = {
		userKey(unique:true,nullable:false)
    }
}

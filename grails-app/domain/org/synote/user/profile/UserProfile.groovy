package org.synote.user.profile

import org.synote.user.User

class UserProfile {

	static belongsTo = [owner: User]
	
	static hasMany = [entries: ProfileEntry]
	
	String name
	boolean defaultProfile
	
	static constraints = {
		name(blank: false, unique: 'owner')
	}
	
	String toString()
	{
		return defaultProfile ? "$name (default)" : "$name"
	}
}

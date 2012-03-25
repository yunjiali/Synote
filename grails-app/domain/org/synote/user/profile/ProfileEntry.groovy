package org.synote.user.profile

import org.synote.resource.Resource

class ProfileEntry {

	static belongsTo = [profile: UserProfile]
	
	Resource resource
	String name
	String value
	
	static constraints = {
		resource(nullable: true)
		name(blank: false, unique: ['profile', 'resource'], validator: {name, entry ->
			if (entry.resource)
			return true
			
			def e = ProfileEntry.withCriteria(uniqueResult: true) {
				eq('profile', entry.profile)
				isNull('resource')
				eq('name', name)
			}
			
			return e == null
		})
	}
	
	String toString()
	{
		return "$name=$value"
	}
}

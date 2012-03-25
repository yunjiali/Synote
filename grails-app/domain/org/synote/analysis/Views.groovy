package org.synote.analysis

import java.util.Date;

import org.synote.user.User
import org.synote.resource.Resource

/*
 * Save the statistical info about a user (may be anonymous user) views some resources
 */
class Views {

	static belongsTo = [resource:Resource]
	User user

	Date dateCreated
	Date lastUpdated
	
    static constraints = {
		user(nullable:true)
		resource(nullable:false)
    }
}

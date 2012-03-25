package org.synote.user

import org.synote.user.User

/**
 * Authority domain class.
 */
class UserRole {

	static hasMany = [people: User]

	/** description */
	String description
	/** ROLE String */
	String authority

	static constraints = {
		authority(blank: false, unique: true)
		description()
	}
}

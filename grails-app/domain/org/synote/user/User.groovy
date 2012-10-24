package org.synote.user

import org.synote.user.group.UserGroup
import org.synote.user.group.UserGroupMember
import org.synote.resource.Resource
import org.synote.annotation.Annotation

/**
 * User domain class.
 */
class User {
	
	static transients = ['confirmedPassword','accountExpired','accountLocked','passwordExpired','springSecurityService']
	def springSecurityService
	                     
	static hasMany = [authorities: UserRole,memberOf: UserGroupMember, groups: UserGroup, resources: Resource, annotations: Annotation]
	static belongsTo = UserRole
	
	
	/** Username */
	String userName
	/** User Real first Name*/
	String firstName
	/** User Real last Name*/
	String lastName
	/** MD5 Password */
	String password
	/** enabled*/
	boolean enabled //DB
	
	boolean accountExpired =false
	boolean accountLocked =false
	boolean passwordExpired=false

	/** email address*/
	String email
	
	/*registered date*/
	Date dateCreated //DB
	/*Last time the user is updated*/
	Date lastUpdated //DB
	/*last login time*/
	Date lastLogin //DB

	/** The confirmed password */
	String confirmedPassword

	static constraints = {
		userName(blank: false, unique: true, validator: {userName ->
			if (userName.size() == 0)
				return false

			if (!Character.isLetter(userName.charAt(0)))
				return false

			for (int i = 1; i < userName.size(); i++)
				if (!Character.isLetterOrDigit(userName.charAt(i)) && !userName.charAt(i) != '_')
					return false

			return true
		})
		password(blank: false, validator: {password, user ->
			if (!user.confirmedPassword)
				return true

			return password == user.confirmedPassword
		})
		firstName(blank: false)
		lastName(blank: false)
		email(blank: false, email: true)
		enabled()
	}

	String toString()
	{
		return "${userName}"
	}
	
	//Sometimes throw: Cannot invoke method encodePassword() on null object.
	//def beforeInsert() {
	//	encodePassword()
	//}

	//def beforeUpdate() {
	//	if (isDirty('password')) {
	//		encodePassword()
	//	}
	//}

	//protected void encodePassword() {
	//	password = springSecurityService.encodePassword(userName.toLowerCase()+password)
	//}
}

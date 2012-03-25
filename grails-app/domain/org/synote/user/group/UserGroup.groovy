package org.synote.user.group

import org.synote.user.User
import java.util.Date;

import org.synote.permission.*;

class UserGroup {

	static belongsTo = [owner: User]
	static hasMany = [members: UserGroupMember, permissions: Permission]
	
	String name
	boolean shared
	/*
	 * A short description of the group
	 */
	String description
	
	Date dateCreated
	Date lastUpdated
	
	static constraints = {
		//TODO: test max size
		name(blank: false, maxSize: 20, unique: 'owner')
		description(blank:true, nullable:true, size:0..255)
	}
	
	String toString()
	{
		return "$name"
	}
}

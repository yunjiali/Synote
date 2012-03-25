package org.synote.permission

import java.util.Date;

import org.synote.user.group.UserGroup

class Permission {
	
	static belongsTo = [group:UserGroup]

	PermissionValue perm
	
	Date dateCreated
	Date lastUpdated
	
	static mapping = {
		perm column: 'perm_id'
	}
	static constraints ={
		perm(nullable:false)
	}
}

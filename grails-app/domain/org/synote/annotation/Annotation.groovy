package org.synote.annotation

import org.synote.user.User
import org.synote.resource.Resource
import org.synote.permission.AnnotationPermission
import org.synote.permission.PermissionValue

class Annotation {

	static belongsTo = [owner: User, source: Resource]
	static hasMany = [permissions: AnnotationPermission, annotations: AnnotationAnnotation]
	
	PermissionValue perm
	
	static constraints = {
		perm(nullable: true)
	}
	
	static mapping = {
		source column: 'source_resource_id'
		perm column:'perm_id'
	}
	
	String toString()
	{
		return "${getClass().getSimpleName()} ${getId()}"
	}
}

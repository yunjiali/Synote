package org.synote.resource

import org.synote.user.User
import java.util.Date;

import org.synote.permission.*
import org.synote.annotation.*
import org.synote.analysis.Views

class Resource 
{
	
	static belongsTo =[owner:User]
	static hasMany = [permissions: ResourcePermission, annotates:Annotation, annotations:ResourceAnnotation, views:Views]

	String title
	PermissionValue perm
	/*url of the thumbnail picture*/
	String thumbnail
	
	/*created date*/
	Date dateCreated //DB
	/*Last time the resource is updated*/
	Date lastUpdated //DB
	
	static mappedBy = [annotates:"source", annotations:"target"]

	static mapping = {
		perm column:'perm_id'
	}
	                   
    static constraints = 
	{
		title(nullable:true, blank: false, maxSize:255)
		perm(nullable:true)
		thumbnail(nullable:true)
	}
	
	public String toString()
	{
		if(title)
			return title
		
		return (title) ? title:"${getClass().getSimpleName()} ${getId()}"
	}
}

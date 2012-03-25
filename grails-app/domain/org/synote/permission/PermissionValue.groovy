package org.synote.permission

/*
 * By default, there are four values:
 * PRIVATE 0
 * READ 100
 * ANNOTATE 200
 * WRITE 300
 */
class PermissionValue {

	String name
	int val
	
	static constraints = {
		//Add unique
		name(blank:false,maxSize: 255, unique:true)
		val(unique:true)
    }
	
	public String toString()
	{
		return name 
	}
}

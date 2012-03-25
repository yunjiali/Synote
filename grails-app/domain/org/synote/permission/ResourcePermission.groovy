package org.synote.permission

import org.synote.resource.Resource

class ResourcePermission extends Permission{

	static belongsTo =[resource:Resource]

	static constraints = {
		resource(unique:'group')
	}
}

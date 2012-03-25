package org.synote.annotation

import org.synote.resource.Resource
import org.synote.annotation.synpoint.Synpoint

class ResourceAnnotation extends Annotation {

	static belongsTo = [target: Resource]
	static hasMany = [synpoints: Synpoint]
	
	static mapping = {
		target column: 'target_resource_id'
	}
}

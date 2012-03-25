package org.synote.permission

import org.synote.annotation.Annotation

class AnnotationPermission extends Permission{
	
	static belongsTo= [annotation:Annotation]
	static constraints = {
		annotation(unique:'group')
    }
}

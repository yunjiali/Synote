package org.synote.annotation

class AnnotationAnnotation extends Annotation{

	static belongsTo = [target: Annotation]
	
	static mapping = {
		target column: 'target_annotation_id'
	}
}

package org.synote.resource.single.binary

import org.synote.resource.compound.PresentationResource

class PresentationSlide extends UrlResource{

	static belongsTo=[presentation:PresentationResource]

	int index
	                  
	static constraints = {
		index(min:0)
    }
	
	static mapping = {
		index column:'slide_index'
		presentation column:'parent_resource_id'
	}
}

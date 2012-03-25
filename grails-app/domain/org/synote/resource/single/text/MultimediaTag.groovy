package org.synote.resource.single.text

import org.synote.resource.compound.MultimediaResource

class MultimediaTag extends TagResource{

	static belongsTo = [multimedia: MultimediaResource]
	
	static mapping = {
		multimedia column:'parent_resource_id'
	}
}

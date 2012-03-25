package org.synote.resource.single.binary

import org.synote.resource.compound.MultimediaResource

class MultimediaUrl extends UrlResource{
	
	static belongsTo=[multimedia:MultimediaResource]
	
	static mapping = {
		multimedia column:'parent_resource_id'
	}
	
	String toString()
	{
		return url
	}
}

package org.synote.resource.single.text

import org.synote.resource.compound.MultimediaResource

class MultimediaTextNote extends TextNoteResource{

	static belongsTo = [multimedia:MultimediaResource]

	static mapping = {
		multimedia column:'parent_resource_id'
	}
}

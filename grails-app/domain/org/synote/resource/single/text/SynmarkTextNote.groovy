package org.synote.resource.single.text

import org.synote.resource.compound.SynmarkResource

class SynmarkTextNote extends TextNoteResource{

	static searchable = {
		only:['content']
	}
	
	static belongsTo = [synmark: SynmarkResource]
	
	static mapping = {
		synmark column:'parent_resource_id'
	}
	
}

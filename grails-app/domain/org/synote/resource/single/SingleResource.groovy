package org.synote.resource.single

import org.synote.i18n.Language
import org.synote.resource.Resource

class SingleResource extends Resource{
	/*
	 * default is Null
	 */
	Language language
	
	static constraints = {
		language(nullable:true)
    }
}

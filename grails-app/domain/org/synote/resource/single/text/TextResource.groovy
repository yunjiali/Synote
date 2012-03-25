package org.synote.resource.single.text

import org.synote.resource.single.SingleResource

class TextResource extends SingleResource{

	String content
	
	static constraints = {
		content (nullable:true, blank: false, widget: "textarea")
    }
	
	static mapping = {
		content type:'text'
	}
	
	String toString()
	{
		return content
	}

	String toShortString()
	{
		if (title || !content)
			return super.toString()

		return (content.size() > 16) ? "${content.substring(0, 12)} ..." : content
	}
}

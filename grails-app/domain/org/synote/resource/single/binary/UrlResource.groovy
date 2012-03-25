package org.synote.resource.single.binary

class UrlResource extends BinaryResource{

	String url
	
	static constraints = {
		url(nullable:false, blank:false)
    }

	@Override
	public String toString()
	{
		return url
	}
}

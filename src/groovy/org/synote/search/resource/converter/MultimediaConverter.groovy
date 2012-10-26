package org.synote.search.resource.converter

import org.compass.core.converter.ConversionException;
import org.compass.core.converter.basic.AbstractBasicConverter
import org.compass.core.mapping.ResourcePropertyMapping
import org.compass.core.marshall.MarshallingContext

import org.synote.resource.compound.MultimediaResource

class MultimediaConverter extends AbstractBasicConverter {
	
	MultimediaConverter() {
		//do nothing
	}
 
	@Override
	protected Object doFromString(String str, ResourcePropertyMapping resourcePropertyMapping, MarshallingContext context) throws ConversionException {
		return null // we don't need this
	}
 
	@Override
	protected String doToString(Object o, ResourcePropertyMapping resourcePropertyMapping, MarshallingContext context) {
		def multimedia = (MultimediaResource)o
		return multimedia.toString()
	}
}

package org.synote.search.resource.converter

import org.compass.core.converter.ConversionException;
import org.compass.core.converter.basic.AbstractBasicConverter
import org.compass.core.mapping.ResourcePropertyMapping;
import org.compass.core.marshall.MarshallingContext;
import org.synote.permission.PermissionValue
import org.synote.resource.Resource
import org.synote.resource.compound.MultimediaResource

class PermissionValueConverter extends AbstractBasicConverter<PermissionValue> {
	PermissionValueConverter() {
	//do nothing
	}

	@Override
	protected Object doFromString(String str, ResourcePropertyMapping resourcePropertyMapping, MarshallingContext context) throws ConversionException {
		return null // we don't need this
	}
	
	@Override
	protected String doToString(PermissionValue o, ResourcePropertyMapping resourcePropertyMapping, MarshallingContext context) {
		//def r = (Resource)o
		println "permvalue:"+o.toString()
		return o.toString()
	}
}

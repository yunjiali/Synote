/*
 * Author: Yunjia Li
 * A Microdata helper to help embed microdata into webpage based on jQuery
 */
var MicrodataHelper = Base.extend({
	
	microdataEnabled:false, //set if microdata is enabled
	constructor:function(microdataEnabled)
	{
		//console.log("constructor");
		this.microdataEnabled = microdataEnabled;
	},
	//create a meta tag, returns the meta jquery object
	createMetaTag:function()
	{
		return $("<meta/>");
	},
	setMetaTag:function(elem,itemprop,content)
	{
		if($.isEmptyObject(elem))
			return null;
		return elem.attr("itemprop",itemprop).attr("content",content);
	},
	//set the Content property for the elem
	setContent:function(elem,content)
	{
		if($.isEmptyObject(elem))
			return null;
		
		return elem.attr("content",content);
	},
	//create or set an item
	createItem:function(elem,itemtype)
	{
		if($.isEmptyObject(elem))
			return null;
		//add itemscope first
		return elem.attr("itemscope","itemscope").attr("itemtype",itemtype);
	},
	
	//set itemId for an item
	setItemid:function(elem,idStr)
	{
		if($.isEmptyObject(elem))
			return null;
		return elem.attr("itemid",idStr);
	},
	//set itemprop
	setItemprop:function(elem,itemprop)
	{
		if($.isEmptyObject(elem))
			return null;
		return elem.attr("itemprop",itemprop);
	},
	
	//set itemref
	setItemref:function(elem,itemref)
	{
		if($.isEmptyObject(elem))
			return null;
		return elem.attr("itemref",itemref);
	},
	
	//set VideoObject or AudioObject based on the type of the media
	setMediaObject:function(elem, isAuido)
	{
		if($.isEmptyObject(elem))
			return null;
		if(isAuido)
		{
			return elem.attr("itemscope","itemscope").attr("itemtype","http://schema.org/AudioObject");
		}
		else
		{
			return elem.attr("itemscope","itemscope").attr("itemtype","http://schema.org/VideoObject");
		}
	}
});

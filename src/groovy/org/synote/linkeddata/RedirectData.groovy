package org.synote.linkeddata

import org.synote.resource.compound.CompoundResource
import org.synote.resource.compound.MultimediaResource
import org.synote.annotation.ResourceAnnotation
import org.synote.annotation.Annotation
import org.synote.annotation.synpoint.Synpoint

/*
 * This is the data structure to save all the information used for 303 redirection
 */
class RedirectData {
	public MultimediaResource recording = null
	public CompoundResource compoundResource = null
	public ResourceAnnotation annotation = null
	public Synpoint synpoint = null
	public String frag = ""
	
	public RedirectData()
	{
		//Do nothing	
	}
	
	public RedirectData(MultimediaResource recording, CompoundResource compoundResource, ResourceAnnotation annotation, Synpoint synpoint, String frag)
	{
		this.recording = recording
		this.compoundResource = compoundResource
		this.annotation = annotation
		this.synpoint = synpoint
		this.frag = frag	
	}
	
	public boolean hasFragment()
	{
		if(!frag)
		{
			return false	
		}
		
		if(frag?.trim().size()>0)
		{
			return true	
		}
		else
		{
			return false	
		}	
	}
	
	public String getFragment()
	{
		return frag?.trim()	
	}
}

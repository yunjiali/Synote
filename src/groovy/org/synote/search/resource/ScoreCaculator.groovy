package org.synote.search.resource;

import org.synote.resource.compound.MultimediaResource
import org.synote.resource.single.text.*
import org.synote.resource.Resource

class ScoreCaculator 
{
	/*
	 * get the score according to the alias of the result, say:
	 * MultimediaResource.title is 10, TranscriptTextResource.text is 5, etc
	 */
	public static float getScoreFromAlias(float oldScore, String alias)
	{
		return oldScore
	}
	
	/*
	 * get the score according to the resource type and field of the resource
	 */
	public static float getScoreFromResource(float oldScore, Resource resource)
	{
		return oldScore
	}
}

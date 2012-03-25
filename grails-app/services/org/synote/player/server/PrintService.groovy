package org.synote.player.server

import org.synote.resource.compound.*
import org.synote.annotation.ResourceAnnotation
import org.synote.annotation.synpoint.Synpoint


class PrintService {

	boolean transactional = true
	
	def getSynpoints(recording, from, to, synmarkedUsers, transcript, presentation, synmarksUsers)
	{
		def synmarked = null
		
		if (synmarkedUsers != null)
		{
			synmarked = []
			
			ResourceAnnotation.findAllByTarget(recording).each {annotation ->
				if (annotation.source instanceof SynmarkResource && synmarkedUsers.contains(annotation.owner.id))
				synmarked += annotation.synpoints.findAll {synpoint ->
					synpoint.targetStart && synpoint.targetEnd
				}
			}
		}
		
		def synpoints = []
		
		ResourceAnnotation.findAllByTarget(recording).each {annotation ->
			if (annotation.source instanceof TranscriptResource && transcript)
			synpoints += annotation.synpoints
			
			if (annotation.source instanceof PresentationResource && presentation)
			synpoints += annotation.synpoints
			
			if (annotation.source instanceof SynmarkResource && synmarksUsers.contains(annotation.owner.id))
			synpoints += annotation.synpoints
		}
		
		synpoints = synpoints.findAll {synpoint ->
			(!from || synpoint.targetStart >= from) && (!to || synpoint.targetStart <= to)
		}
		
		if (synmarked != null)
		{
			def selected = []
			
			synpoints.each {synpoint ->
				if (synmarked.find {it.targetStart <= synpoint.targetStart && it.targetEnd >= synpoint.targetStart})
				selected << synpoint
			}
			
			synpoints = selected
		}
		
		return synpoints.sort {synpoint -> synpoint.targetStart}
	}
	
	/*
	 * Get all synpoints regardless of the synmarksUsers and synmarkedUsers, from and to should not be null
	 */
	def getAllSynpoints(recording, from, to)
	{
		//println "avc:"+to
		if(from > to)
			return null
		
		def interval = new IntRange(from,to)
		
		/*synmarked = []
			
		ResourceAnnotation.findAllByTarget(recording).each {annotation ->
			if (annotation.source.instanceOf(SynmarkResource))
				synmarked += annotation.synpoints.findAll {synpoint ->
					synpoint.targetStart && synpoint.targetEnd
				}
		}*/
		
		def synpoints = []
		
		ResourceAnnotation.findAllByTarget(recording).each {annotation ->
			if (annotation.source.instanceOf(WebVTTResource))
				synpoints += annotation.synpoints
			
			if (annotation.source.instanceOf(PresentationResource))
				synpoints += annotation.synpoints
			
			if (annotation.source.instanceOf(SynmarkResource))
				synpoints += annotation.synpoints
		}
		
		synpoints = synpoints.findAll {synpoint ->
			//(!from || synpoint.targetStart >= from) && (!to || synpoint.targetStart <= to)
			//select ever span that has something in the interval
			(interval.contains(synpoint.targetStart) && interval.contains(synpoint.targetEnd))
			//or use (interval.contains(synpoint.targetStart) || interval.contains(synpoint.targetEnd))
		}
		
		/*if (synmarked != null)
		{
			def selected = []
			
			synpoints.each {synpoint ->
				if (synmarked.find {it.targetStart <= synpoint.targetStart && it.targetEnd >= synpoint.targetStart})
				selected << synpoint
			}
			
			synpoints = selected
		}*/
		
		return synpoints.sort {synpoint -> synpoint.targetStart}
	}
	
	def getEnds(synpoints, to)
	{
		def ends = []
		
		synpoints.each {synpoint ->
			if (!(synpoint.annotation.source.instanceOf(WebVTTResource)) && synpoint.targetEnd &&
			(!to || synpoint.targetEnd <= to) && !ends.contains(synpoint.targetEnd))
			{
				ends << synpoint.targetEnd
			}
		}
		
		if (to && !ends.contains(to))
		ends << to
		
		return ends.sort {it}
	}
}

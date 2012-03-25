package org.synote.user.admin

import grails.converters.*
import java.io.InputStreamReader
import java.io.IOException;
import java.net.*
import java.security.Security
import java.util.Properties

import org.synote.resource.compound.MultimediaResource
import org.synote.resource.compound.TranscriptResource
import org.synote.resource.compound.WebVTTResource
import org.synote.resource.single.text.WebVTTCue
import org.synote.annotation.ResourceAnnotation
import org.synote.annotation.synpoint.Synpoint
import org.synote.permission.PermissionValue
import org.synote.search.resource.ResourceSearchService
import org.synote.user.User
import org.synote.user.UserRole
import org.synote.integration.ibmhts.IBMTransJobService
import org.synote.config.ConfigurationService
import org.synote.player.server.PlayerService
import org.synote.linkeddata.DataDumpService

import org.synote.player.client.TranscriptData
import org.synote.player.client.TranscriptDataSimple
import org.synote.player.client.TranscriptItemData
import org.synote.player.client.TranscriptSynpoint

import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.orm.hibernate3.SessionFactoryUtils
import org.springframework.orm.hibernate3.SessionHolder
import org.springframework.transaction.support.TransactionSynchronizationManager

import org.apache.commons.logging.LogFactory

class AdminService {

    static transactional = true
	private static final log = LogFactory.getLog(this)

	def playerService
	def dataDumpService
	
    /*
	 * This method convert all the old transcriptResource in v5 to WebVTTResource, and we will use WEbVTTResource for any furher version
	 * Get transcript as SRT => save it in new WebVTT class => delete old transcriptResources => create new annotations for WebVTTResource
	 */
	def convertTranscriptResourceToWebVTTResource = 
	{
		//it's time consuming, so we start a new thread
		Thread.start{
		log.debug("############################")
		def ctx = org.codehaus.groovy.grails.web.context.ServletContextHolder.servletContext.getAttribute(org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes.APPLICATION_CONTEXT);
		def sessionFactory = ctx.getBean("sessionFactory")
		def session = SessionFactoryUtils.getSession(sessionFactory, true)
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session))
			TranscriptResource.list().each{tr->
				def anno = ResourceAnnotation.findBySource(tr)
				if(anno)
				{
					def multimedia = anno.target
					def owner = multimedia.owner
					TranscriptData[] transList = playerService.getTranscripts(String.valueOf(multimedia.id))
					//log.debug "convert to srt object."
					def srtList = playerService.convertToSRTObject(transList[0]) //see TranscriptItemSRT.java
					def webVTTResource = new WebVTTResource(owner:owner,fileHeader:"WebVTT", title:"WebVTTResource")
					
					//if the getText() give blank string, we need to fill in something
					//We can't simply ignore it because the index will not be correct then
					srtList.each{srt->
						def cue = new WebVTTCue(
							content: srt.getText().trim().size()>0?srt.getText():".",
							owner: owner,
							title:"WebVTTCue",
							cueIndex:srt.getIndex(),
							cueSettings:"")
						webVTTResource.addToCues(cue)
					}
					
					if(webVTTResource.hasErrors())
					{
						webVTTResource.errors.each {
							println "\r\nerror:"+it
						}
					}
					webVTTResource.save(flush:true)
					def new_anno = new ResourceAnnotation(owner:owner, source:webVTTResource, target: multimedia)
					srtList.each{srt->
						def sourceStart = srt.getIndex()
						def targetStart = srt.getStart()
						def targetEnd = srt.getEnd()
						new_anno.addToSynpoints(new Synpoint(sourceStart: sourceStart,
								targetStart: targetStart,
								targetEnd: targetEnd))
					}
					new_anno.save(flush:true)
					log.debug("finish transcript ${tr.id}")
				}
			}
		}
	}
	
	/*
	 * Delete all TranscriptResources not webVTTResource
	 */
	def removeTranscriptResources = 
	{
		def trList = TranscriptResource.list()
		trList.each{ tr->
			if(!tr.instanceOf(WebVTTResource))
			{
				tr.delete()	
			}
		}
	}
	/*
	 * Do one transcript
	 */
	def convertOneTranscriptResourceToWebVTTResource(tr)
	{
		log.debug("############################")
		def anno = ResourceAnnotation.findBySource(tr)
		if(anno)
		{
			def multimedia = anno.target
			def owner = multimedia.owner
			TranscriptData[] transList = playerService.getTranscripts(String.valueOf(multimedia.id))
			//log.debug "convert to srt object."
			def srtList = playerService.convertToSRTObject(transList[0]) //see TranscriptItemSRT.java
			def webVTTResource = new WebVTTResource(owner:owner,fileHeader:"WebVTT", title:"WebVTTResource")
			
			srtList.each{srt->
				def cue = new WebVTTCue(
					content: srt.getText().trim().size()>0?srt.getText():".",
					owner: owner,
					title:"WebVTTCue",
					cueIndex:srt.getIndex(),
					cueSettings:"")
				webVTTResource.addToCues(cue)
			}
			
			log.debug(webVTTResource.validate())
			if(webVTTResource.hasErrors())
			{
				webVTTResource.errors.each {
					log.debug("\r\nerror:"+it)
				}
			}
			
			
			if(!webVTTResource.save(flush:true))
			{
				log.debug("something wrong")	
			}
			def new_anno = new ResourceAnnotation(owner:owner, source:webVTTResource, target: multimedia)
			srtList.each{srt->
				def sourceStart = srt.getIndex()
				def targetStart = srt.getStart()
				def targetEnd = srt.getEnd()
				new_anno.addToSynpoints(new Synpoint(sourceStart: sourceStart,
						targetStart: targetStart,
						targetEnd: targetEnd))
			}
			new_anno.save(flush:true)
			log.debug("finish transcript ${tr.id}")
		}
	}
	
	/*
	 * Dump RDF
	 */
	def dumpRDFToOneFile()
	{
		Thread.start{
			log.debug("############DumpRDFToOneFile#############")
			def ctx = org.codehaus.groovy.grails.web.context.ServletContextHolder.servletContext.getAttribute(org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes.APPLICATION_CONTEXT);
			def sessionFactory = ctx.getBean("sessionFactory")
			def session = SessionFactoryUtils.getSession(sessionFactory, true)
			TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session))
			dataDumpService.dumpRDFToOneFile()
		}
	}
}

package org.synote.search.resource

import org.synote.search.resource.exception.ResourceSearchException
import org.apache.log4j.Logger
import java.util.LinkedHashMap
import org.compass.core.CompassHighlighter

import org.synote.permission.PermService
import org.synote.user.User
import org.synote.user.SecurityService
import org.synote.utils.RegExService
import org.synote.resource.compound.*
import org.synote.resource.Resource
import org.synote.permission.PermissionValue
import org.synote.annotation.ResourceAnnotation
import org.synote.config.ConfigurationService

import org.synote.search.resource.analysis.QueryRecord
import org.synote.search.resource.analysis.ResultRecord
import org.synote.search.resource.analysis.PageSelectionRecord

import org.synote.resource.compound.MultimediaResource

class ResourceSearchService {

	boolean transactional = true
	
	def searchableService
	def permService
	def securityService
	def regExService
	def configurationService
	
	private Logger log = Logger.getLogger(getClass());
	
	/**
	* Perform a bulk index of every searchable object in the database
	* Resource.index() throws MissingMethodException, that's wired!
	*/
   def indexResources = {
	   //Thread.start {
	   //	searchableService.index()
	   //}
	   //Do nothing
   }
   
   /**
	* Unindex all resources
	*/
   def unindexResources = {
	   searchableService.unindex()
   }
   
   /*
	* Reindex all resources
	*/
   def reindexResources = {
	   Thread.start{
	   		MultimediaResource.reindex()
	   }
   }
}

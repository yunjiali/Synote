
package org.synote.player.server

import org.apache.commons.logging.LogFactory;

import org.synote.player.client.MultimediaData
import org.synote.player.client.PlayerException
import org.synote.player.client.PresentationData
import org.synote.player.client.PresentationSlideData
import org.synote.player.client.ClientProfileEntry
import org.synote.player.client.SynmarkData
import org.synote.player.client.TranscriptData
import org.synote.player.client.TranscriptDataSimple
import org.synote.player.client.TranscriptItemData
import org.synote.player.client.TranscriptSynpoint
import org.synote.player.client.UserData
import org.synote.player.client.TimeFormat
import org.synote.player.client.TranscriptItemSRT

import org.synote.player.client.WebVTTCueData
import org.synote.player.client.WebVTTData

import org.synote.resource.compound.WebVTTResource
import org.synote.resource.single.text.WebVTTCue

import org.synote.resource.Resource
import org.synote.resource.compound.*
import org.synote.resource.single.text.*
import org.synote.resource.single.binary.*
import org.synote.user.User
import org.synote.user.SecurityService
import org.synote.annotation.Annotation
import org.synote.annotation.ResourceAnnotation
import org.synote.annotation.synpoint.Synpoint
import org.synote.user.profile.UserProfile
import org.synote.user.profile.ProfileEntry
import org.synote.permission.PermService
import org.synote.permission.PermissionValue
import org.synote.permission.Permission
import org.synote.permission.ResourcePermission
import org.synote.integration.viascribe.exception.ViascribeException
import org.synote.api.APIStatusCode


class PlayerService {
	
	static expose = ['gwt:org.synote.player.client']
    boolean transactional = true
	private static final log = LogFactory.getLog(this)
	
	def securityService
	def permService
	
	def transcriptContent = [:]
	
	int[] storeProfileEntries(List<ClientProfileEntry> clientEntries)
	{
		def stored = []
		
		def user = securityService.getLoggedUser()
		
		if (!user)
			return stored
		
		def defaultProfile = UserProfile.findByOwnerAndDefaultProfile(user, true)
		if (!defaultProfile)
		{
			defaultProfile = new UserProfile(owner: user, name: 'Default', defaultProfile: true)
			if (!defaultProfile.save())
				return stored
		}
		
		clientEntries.each {clientEntry ->
			def resource = clientEntry.getResourceId() ? Resource.get(clientEntry.getResourceId().toLong()) : null
			
			def entry = null
			if (resource)
			{
				entry = ProfileEntry.withCriteria(uniqueResult: true) {
					eq('profile', defaultProfile)
					eq('resource', resource)
					eq('name', clientEntry.getName())
				}
			}
			else
			{
				entry = ProfileEntry.withCriteria(uniqueResult: true) {
					eq('profile', defaultProfile)
					isNull('resource')
					eq('name', clientEntry.getName())
				}
			}
			
			if (entry)
			{
				if (clientEntry.getValue())
					entry.value = clientEntry.getValue()
				else
					entry.delete()
			}
			else
			{
				if (clientEntry.getValue())
				{
					defaultProfile.addToEntries(new ProfileEntry
							( profile: defaultProfile
							, resource: resource
							, name: clientEntry.getName()
							, value: clientEntry.getValue() ))
					
					if (!defaultProfile.save())
						return stored
				}
			}
			
			stored += clientEntry.getVersion()
		}
		
		return stored
	}
	
	MultimediaData getMultimedia(String multimediaId)
	{
		def multimedia = MultimediaResource.get(multimediaId.toLong())
		
		if (!multimedia || !canRead(multimedia))
			return null
			
		log.debug "canCreateSynmark:"+canCreateSynmark(multimedia)
		return new MultimediaData
		( multimedia.id.toString()
		, getOwner(multimedia)
		, canEdit(multimedia)
		, canDelete(multimedia)
		, multimedia.title
		, multimedia.url?.url
		, canCreateTranscript(multimedia)
		, canCreatePresentation(multimedia)
		, canCreateSynmark(multimedia) )
		
	}
	
	/*
	 * Deprecated!
	 */
	
	TranscriptData[] getTranscripts(String multimediaId)
	{
		log.debug("getTranscripts")
		def multimedia = MultimediaResource.get(multimediaId.toLong())
		def annotations = ResourceAnnotation.findAllByTarget(multimedia)
		
		def transcripts = []
		
		annotations.each {annotation ->
			if (annotation.source.instanceOf(TranscriptResource) && !annotation.source.instanceOf(WebVTTResource) && canRead(annotation))
			{
				def transcript = annotation.source
				def synpoints =[]
				
				annotation.synpoints.sort{synpoint -> synpoint.sourceStart}.each{synpoint->
					//println synpoint.id
					synpoints << new TranscriptSynpoint
					(synpoint.sourceStart,
					synpoint.sourceEnd,
					synpoint.targetStart,
					synpoint.targetEnd)
				}
				
				transcripts << new TranscriptData
						( transcript.id.toString()
						, getOwner(transcript)
						, canEdit(transcript)
						, canDelete(transcript)
						, transcript.transcript?.content
						, synpoints)
			}
		}
		
		return transcripts
	}
	
	//Get the transcriptResource from multimediaId
	//Not used
	def getTranscriptResource(String multimediaId)
	{
		def multimedia = MultimediaResource.get(multimediaId.toLong())
		def annotations = ResourceAnnotation.findAllByTarget(multimedia)
		def transcriptResource = null
		for(Annotation a in annotations)
		{
			if(a.source.instanceOf(TranscriptResource) && !annotation.source.instanceOf(WebVTTResource))
			{
				transcriptResource = a.source
				break;	
			}	
		}
		return transcriptResource
	}
	
	/*
	 * Not used
	 */
	def createTranscript(multimediaId, transcriptData)
	{
		def multimedia = MultimediaResource.get(multimediaId.toLong())
		//The owner of the transcript should be the same as the owner of multimedia
		//def user = securityService.getLoggedUser()
		
		//TODO: and guest enabled, configuration settings
		def user = getLoggedUser()
		
		def owner = multimedia.owner
		
		def transcriptText = new TranscriptTextResource(
				content: transcriptData.getText(),
				owner: owner,
				title:"TranscriptText")
		def transcript = new TranscriptResource(
				transcript: transcriptText,
				owner: owner,
				title: "Transcript")
		//Do we have to check the user permission here?
		
		
		if (!transcript.save(flush:true))
		{
			//TODO: Why throw out viascribeException?
			log.error("Cannot create transcript resource for multimedia:"+multimediaId)
			throw new ViascribeException("Cannot create transcript resource")
		}
		
		//transcript.index()
		//create new annotation
		def annotation = new ResourceAnnotation(owner: owner, source: transcript, target: multimedia)
		def items = []
		transcriptData.getSynpoints().each {tsp ->
			if (tsp.getId() == null)
			{
				def sourceStart = tsp.getStartIndex()
				def sourceEnd = tsp.getEndIndex()
				def targetStart = tsp.getStartTime()
				def targetEnd = tsp.getEndTime()
				annotation.addToSynpoints(new Synpoint(sourceStart: sourceStart,
						sourceEnd: sourceEnd,
						targetStart: targetStart,
						targetEnd: targetEnd))
			}
		}
		
		if (!annotation.save(flush:true))
		{
			log.error("Cannot create transcript annotation")
			throw new ViascribeException("Cannot create transcript annotation")
		}
	}
	
	/*
	 * Not used
	 */
	def editTranscript(multimediaId, transcriptId, transcriptData)
	{
		def user = getLoggedUser()
		
		def multimedia = MultimediaResource.get(multimediaId.toLong())
		def transcript = TranscriptResource.get(transcriptId.toLong())
		
		def transcripts = []
		def items = []
		
		transcript.transcript?.content = transcriptData.getText()
		if (!transcript.save(flush:true))
		{
			def message = "Cannot save transcript resource with ID ${transcript.id}:"
			
			transcript.errors.each {error ->
				message += "\n${error}"
			}
			
			log.error("Editing error for transcript ${transcriptId}:"+message)
			throw new PlayerException(message)
		}
		
		//transcript.reindex()
		
		def annotation = ResourceAnnotation.findBySourceAndTarget(transcript, multimedia)
		def synpoints = Synpoint.findAllByAnnotation(annotation)
		
		synpoints.each
				{
					synpoint ->annotation.removeFromSynpoints(synpoint)
				}
		
		synpoints*.delete()
		//remove all of synpoint for this annotation first and then add in all of them
		
		transcriptData.getSynpoints().each {tsp ->
			if (tsp.getId() == null)
			{
				def newsynpoint = new Synpoint
						( annotation: annotation
						, sourceStart: tsp.getStartIndex()
						, sourceEnd: tsp.getEndIndex()
						, targetStart: tsp.getStartTime()
						, targetEnd: tsp.getEndTime() )
				if(!newsynpoint.save())
				{
					def message = "Cannot save new synpoint with startIndex: ${tsp.getStartIndex()}:"
					newsynpoint.errors.each {error ->
						message += "\n${error}"
					}
					log.error(message)
					throw new PlayerException(message)
				}
				annotation.addToSynpoints(newsynpoint)
			}
		}
		
		if (!annotation.save(flush:true))
		{
			def message = "Cannot save transcript annotation with ID ${annotation.id}:"
			
			annotation.errors.each {error ->
				message += "\n${error}"
			}
			log.error(message)
			throw new PlayerException(message)
		}
	}
	//Not used
	def deleteTranscript(transcriptId)
	{
		def transcriptResource = TranscriptResource.findById(transcriptId)
		transcriptResource.delete(flush:true)
		//Yunjia:implement thoroughly later
	}
	
	String getTranscriptFromFile()
	{
		def user = getLoggedUser()
		
		def file = transcriptContent.get(user)
		String content = file.getText();
		
		//Read the content from a file and then delete it.
		if(content != null)
		{
			if(!content.trim().equals(""))
			{
				log.info("return content")
				return content
			}
			else
			{
				log.info("return empty")
				return " "
			}
		}
		else
		{
			log.info("return null")
			return null
		}
	}
	
	PresentationData[] getPresentations(String multimediaId)
	{
		def multimedia = MultimediaResource.get(multimediaId.toLong())
		def annotations = ResourceAnnotation.findAllByTarget(multimedia)

		def presentations = []
		annotations.each {annotation ->
			if (annotation.source.instanceOf(PresentationResource) && canRead(annotation))
			{
				
				def presentation = annotation.source
				def slides = []
				
				annotation.source.slides.sort {slide -> slide.index}.each {slide ->
					def synpoint = annotation.synpoints.find {synpoint -> synpoint.sourceStart == slide.index}
					slides << new PresentationSlideData(slide.id.toString(), synpoint?.targetStart, synpoint?.targetEnd, slide.url)
				}
				
				//canEdit and canDelete should pass presentation as the param
				//But as currently, presentations have no permission defined at all
				//We use multimedia instead
				presentations << new PresentationData
						( presentation.id.toString()
						, getOwner(presentation)
						, canEdit(multimedia)
						, canDelete(multimedia)
						, (PresentationSlideData[]) slides )
				
			}
			
		}
		
		return presentations
	}
	
	def createSlide(MultimediaResource multimedia, PresentationResource presentation, int index, PresentationSlideData slideData)
	{
		def user = getLoggedUser()
		
		if (!multimedia)
		{
			log.error("Multimedia with not found")
			throw new PlayerException("Multimedia not found")
		}
		
		def owner = multimedia.owner
		def annotation = null
		if (presentation)
		{			
			if (index < 0)
				index = 0
			
			if (index > presentation.slides.size())
				index = presentation.slides.size()
			
			presentation.slides.each {slide ->
				if (slide.index >= index)
					slide.index++
			}
			
			annotation = ResourceAnnotation.findBySourceAndTarget(presentation, multimedia)
			if (!annotation)
				throw new PlayerException("Annotation for multimedia with ID ${multimedia.id} and presentation with ID ${presentation.id} not found")
			
			annotation.synpoints.each {synpoint ->
				if (synpoint.sourceStart >= index)
					synpoint.sourceStart++
			}
		}
		else
		{
			// check permissions
			
			presentation = new PresentationResource(owner: owner)
			
			annotation = new ResourceAnnotation(owner: owner, source: presentation, target: multimedia)
			
			index = 0
		}
		
		presentation.addToSlides(new PresentationSlide(presentation: presentation, owner:owner, index: index, url: slideData.getUrl()));
		
		if (!presentation.save())
		{
			def message = "Cannot save presentation resource with ID ${presentation.id}:"
			
			presentation.errors.each {error ->
				message += "\n${error}"
			}
			log.error(message)
			throw new PlayerException(message)
		}
		
		annotation.addToSynpoints(new Synpoint(annotation: annotation, sourceStart: index, targetStart: slideData.getStart()))
		
		if (!annotation.save(flush:true))
		{
			def message = "Cannot save presentation annotation with ID ${annotation.id}:"
			
			annotation.errors.each {error ->
				message += "\n${error}"
			}
			log.error(message)
			throw new PlayerException(message)
		}
		
		return
	}
	
	def editSlide(MultimediaResource multimedia, PresentationResource presentation, int index, int newIndex, PresentationSlideData slideData)
	{
		def user = getLoggedUser()
		println "old index:"+index
		println "new index:"+newIndex
		if (!multimedia)
		{
			log.error("Multimedia not found")
			throw new PlayerException("Multimedia not found")
		}
		if (!presentation)
		{
			log.error("Presentation not found")
			throw new PlayerException("Presentation not found")
		}
		
		// check permissions
		
		def slide = PresentationSlide.get(slideData.getId().toLong())
		if (!slide)
		{
			log.error("Slide with index ${index} in presentation with ID ${presentation.id} not found")
			throw new PlayerException("Slide with index ${index} in presentation with ID ${presentation.id} not found")
		}
		
		if(slide.url != slideData.getUrl())
		{
			slide.url = slideData.getUrl()
			
			if (!slide.save())
			{
				def message = "Cannot update slide with index ${slide.index} in presentation with ID ${presentation.id}:"
				
				slide.errors.each {error ->
					message += "\n${error}"
				}
				
				log.error(message)
				throw new PlayerException(message)
			}
		}
		
		def annotation = ResourceAnnotation.findBySourceAndTarget(presentation, multimedia)
		if (!annotation)
		{
			log.error("Annotation for multimedia with ID ${multimedia.id} and presentation with ID ${presentation.id} not found")
			throw new PlayerException("Annotation for multimedia with ID ${multimedia.id} and presentation with ID ${presentation.id} not found")
		}
		
		def synpoint = annotation.synpoints.find {it.sourceStart == index}
		if (synpoint)
		{
			synpoint.targetStart = slideData.getStart()
			
			if (!synpoint.save())
			{
				def message = "Cannot update synpoint for slide with index ${slide.index} in presentation with ID ${presentation.id}:"
				
				synpoint.errors.each {error ->
					message += "\n${error}"
				}
				log.error(message)
				throw new PlayerException(message)
			}
		}
		
		if (newIndex < 0)
			newIndex = 0
		
		if (newIndex > presentation.slides.size() - 1)
			newIndex = presentation.slides.size() - 1
		
		if (index != newIndex)
		{
			presentation.slides.each {
				if (it.index > index)
					it.index--
				
				if (it.index >= newIndex)
					it.index++
			}
			
			slide.index = newIndex
			
			annotation.synpoints.each {
				if (it.sourceStart > index)
					it.sourceStart--
				
				if (it.sourceStart >= newIndex)
					it.sourceStart++
			}
			
			if (synpoint)
				synpoint.sourceStart = newIndex
		}
		return
	}
	
	def deleteSlide(MultimediaResource multimedia, PresentationResource presentation, int index)
	{
		def user = getLoggedUser()
		
		if (!multimedia)
		{
			log.error("Multimedia not found")
			throw new PlayerException("Multimedia with not found")
		}
		if (!presentation)
		{
			log.error("Presentation with ID ${presentationId} not found")
			throw new PlayerException("Presentation with ID ${presentationId} not found")
		}
		
		// check permissions
		
		def slide = PresentationSlide.findByPresentationAndIndex(presentation, index)
		if (!slide)
		{
			log.error("Slide with index ${index} in presentation with ID ${presentation.id} not found")
			throw new PlayerException("Slide with index ${index} in presentation with ID ${presentation.id} not found")
		}
		if (PresentationSlide.countByPresentation(presentation) > 1)
		{
			presentation.removeFromSlides(slide)
			slide.delete()
			
			presentation.slides.each {
				if (it.index > index)
					it.index--
			}
			
			if (!presentation.save(flush:true))
			{
				def message = "Cannot update presentation with ID ${presentation.id}:"
				
				presentation.errors.each {error ->
					message += "\n${error}"
				}
				log.error(message)
				throw new PlayerException(message)
			}
			
			def annotation = ResourceAnnotation.findBySourceAndTarget(presentation, multimedia)
			if (!annotation)
			{
				log.error("Annotation for multimedia with ID ${multimedia.id} and presentation with ID ${presentation.id} not found")
				throw new PlayerException("Annotation for multimedia with ID ${multimedia.id} and presentation with ID ${presentation.id} not found")
			}
			
			def synpoint = annotation.synpoints.find {it.sourceStart == index}
			if (synpoint)
			{
				annotation.removeFromSynpoints(synpoint)
				synpoint.delete()
			}
			
			annotation.synpoints.each {
				if (it.sourceStart > index)
					it.sourceStart--
			}
			
			if (!annotation.save(flush:true))
			{
				def message = "Cannot update presentation annotation with ID ${annotation.id}:"
				
				annotation.errors.each {error ->
					message += "\n${error}"
				}
				log.error(message)
				throw new PlayerException(message)
			}
		}
		else
		{
			presentation.delete(flush:true)
		}
		
		return
		//return getPresentations(multimediaId)
	}
	
	SynmarkData[] getSynmarks(String multimediaId)
	{
		log.debug("Start to get Synmarks...")
		def multimedia = MultimediaResource.get(multimediaId.toLong())
		def annotations = ResourceAnnotation.findAllByTarget(multimedia)
		
		def synmarks = []
		
		annotations.each {annotation ->
			if (annotation.source.instanceOf(SynmarkResource) && canRead(annotation.source))
				synmarks << createSynmarkData(annotation)
		}
		log.debug("Got ${synmarks.size()} synmarks.")
		return synmarks.sort {synmark -> synmark.getStart()}
	}
	
	public SynmarkData createSynmarkData(ResourceAnnotation annotation)
	{
		def synmark = annotation.source
		
		return new SynmarkData
		( synmark.id.toString()
		, getOwner(synmark)
		, canEdit(synmark)
		, canDelete(synmark)
		, annotation.synpoints.targetStart[0]
		, annotation.synpoints.targetEnd[0]
		, synmark.title
		, synmark.note?.content
		, synmark.tags ? (String[]) synmark.tags.sort {tag -> tag.content}.collect {tag -> tag.content} : new String[0]
		, synmark.next ? synmark.next.id.toString() : null
		, synmark.thumbnail)
	}
	
	SynmarkData createSynmark(String multimediaId, SynmarkData synmarkData)
	{
		def user = securityService.getLoggedUser()
		// check permissions
		
		def synmark = new SynmarkResource
				( owner: user
				, title: synmarkData.getTitle()?.trim() ? synmarkData.getTitle().trim() : null
				, note: new SynmarkTextNote(owner: user, content: synmarkData.getNote()?.trim() ? synmarkData.getNote().trim() : null)
				, next: synmarkData.getNext()?.trim() ? SynmarkResource.get(synmarkData.getNext().trim().toLong()) : null
				, thumbnail:synmarkData.getThumbnail() )
		
		synmarkData.getTags().each {content ->
			if (content?.trim() && !synmark?.tags.find {tag -> tag.content.equalsIgnoreCase(content.trim())})
				synmark.addToTags(new SynmarkTag(owner: user, content: content.trim()))
		}
		
		if (!synmark.save(flush:true))
		{
			def message = "Cannot create synmark resource"
			synmark.errors.each {error ->
				message += "\n${error}"
			}
			
			log.error(message)
			throw new PlayerException(message)
		}
		
		//synmark.index()
		
		def multimedia = MultimediaResource.get(multimediaId.toLong())
		
		def annotation = new ResourceAnnotation(owner: user, source: synmark, target: multimedia)
		
		annotation.addToSynpoints(new Synpoint(targetStart: synmarkData.getStart(), targetEnd: synmarkData.getEnd()))
		
		if (!annotation.save(flush:true))
		{
			def message = "Cannot save synmark annotation:"
			
			annotation.errors.each {error ->
				message += "\n${error}"
			}
			log.error(message)
			throw new PlayerException("Cannot create synmark annotation")
		}
		
		return createSynmarkData(annotation)
	}
	
	SynmarkData editSynmark(String synmarkId, SynmarkData synmarkData)
	{
		def synmark = SynmarkResource.get(synmarkId.toLong())
		
		if (!synmark)
		{
			log.error("Synmark with ID ${synmarkId} not found")
			throw new PlayerException("Synmark with ID ${synmarkId} not found")
		}
		
		// check permissions
		
		def user = securityService.getLoggedUser()
		
		if (synmark.owner.id != user?.id && user?.role != UserRole.ADMIN)
			throw new PlayerException("Permission denied")
		
		synmark.title = synmarkData.getTitle()?.trim() ? synmarkData.getTitle().trim() : null
		synmark.note.content = synmarkData.getNote()?.trim() ? synmarkData.getNote().trim() : null
		synmark.next = synmarkData.getNext()?.trim() ? SynmarkResource.get(synmarkData.getNext().trim().toLong()) : null
		synmark.thumbnail = synmarkData.getThumbnail()? synmarkData.getThumbnail():null
		
		synmark.tags?.findAll {tag ->
			!synmarkData.getTags().find {content ->
				content?.trim() && content == tag.content
			}
		}.each {tag ->
			synmark.removeFromTags(tag)
			tag.delete()
		}
		
		synmarkData.getTags().findAll {content ->
			content?.trim() && !synmark.tags?.find {tag ->
				tag.content == content
			}
		}.each {content ->
			if (content?.trim() && !synmark?.tags.find {tag -> tag.content.equalsIgnoreCase(content.trim())})
				synmark.addToTags(new SynmarkTag(owner: synmark.owner, content: content.trim()))
		}
		
		if (!synmark.save(flush:true))
		{
			def message="Cannot update synmark resource:"
			synmark.errors.each {error ->
				message += "\n${error}"
			}
			log.error(message)
			throw new PlayerException("Cannot update synmark resource")
		}
		
		//synmark.reindex()
		
		def annotation = ResourceAnnotation.findBySource(synmark)
		
		def synpoint = annotation.synpoints.find {true}
		synpoint.targetStart = synmarkData.getStart()
		synpoint.targetEnd = synmarkData.getEnd()
		
		if (!synpoint.save())
		{
			def message="Cannot update synmark annotation"
			synpoint.errors.each {error ->
				message += "\n${error}"
			}
			log.error(message)
			throw new PlayerException("Cannot update synmark annotation")
		}
		
		return createSynmarkData(annotation)
	}
	
	String deleteSynmark(String synmarkId)
	{
		def synmark = SynmarkResource.get(synmarkId.toLong())

		log.debug("Delete synmark ${synmarkId}")
		if (!synmark)
			throw new PlayerException("Synmark with ID ${synmarkId} not found")
		
		// check permissions
		
		def user = securityService.getLoggedUser()
		
		if (!(synmark.owner.id == user?.id || securityService.isAdminLoggedIn()))
		{
			log.error("Permission denied when deleting synmark with ID ${synmarkId}")
			throw new PlayerException("Permission denied")
		}
		
		//synmark.unindex()
		
		synmark.delete()
		
		return synmarkId
	}

//###########################  permission control for synote player  ######################################################################
	//>=READ
	public canRead(Resource resource)
	{
		/*def user = securityService.getLoggedUser()
		 if(permService.getPerm(resource, user) >= PermissionValue.READ)
		 {
		 return true
		 }
		 else
		 {
		 return false
		 }*/
		return true
		
	}
	
	//>=READ
	public canRead(Annotation annotation)
	{
		//TODO: haven't implementated in permService.groovy
		return true
	}
	
	public getOwner(resource)
	{
		return new UserData(resource.owner.id.toString(), resource.owner.firstName, resource.owner.lastName)
	}
	
	//IS OWNER OR ADMIN, OR HAS WRITE PERMISSION
	public canEdit(resource)
	{
		//There's no refactoring function in Eclipse for groovy
		//So in order to avoid the ambigous function in PermService.groovy
		//I have to change the programme as following:
		//User user= securityService.getLoggedUser()
		if(!securityService.isLoggedIn())
			return false
		
		def perm = permService.getPerm(resource)
		//log.debug("${resource.class.getName()} id:${resource.id}, perm:${perm}")
		//if(user)
		//	perm = permService.getPerm(resource)
		//else
		//	perm = PermissionValue.findByName("READ")
		//println "perm CAN EDIT:"+perm.toString()+":"+resource.class+":"+resource.owner
		//println "Class:"+resource.class
		//println "Perm:"+perm
		if(perm.val == PermissionValue.findByName("WRITE")?.val)
		{
			return true
		}
		else
		{
			//println "return false"
			return false
		}
	}
	
	public canDelete(resource)
	{
		//println "canDelete:"+resource.owner.id
		return canEdit(resource)
	}
	
	public canCreateTranscript(multimedia)
	{
		//println "canCreateTranscript:"+canEdit(multimedia)
		return canEdit(multimedia)
	}
	
	public canCreatePresentation(multimedia)
	{
		return canEdit(multimedia)
	}
	
	//IS OWNER OR ADMIN, OR HAS ANNOTATE PERMISSION
	public canCreateSynmark(multimedia)
	{
		//User user= securityService.getLoggedUser()
		if(!securityService.isLoggedIn())
			return false
			
		def perm = permService.getPerm(multimedia)
		//log.debug("perm.val:"+perm.val)
		//if(user)
		//	perm = permService.getPerm(multimedia)
		//else
		//	perm = PermissionValue.findByName("READ")
		//println "perm CREATE SYNMARK:"+perm.toString()+":"+multimedia.toString()
		
		if(perm.val >= PermissionValue.findByName("ANNOTATE")?.val)
		{
			//println "return true"
			return true
		}
		else
		{
			//println "return false"
			return false
		}
	}
	
	private getLoggedUser()
	{
		def user = securityService.getLoggedUser()
		if (!user)
		{
			log.error("User login required")
			throw new PlayerException("User login required");
		}
		
		return user
	}
	
//###########################  read transcript from database and generate srt  ######################################################################
	/*
	 * Extract the time and character from the old transcriptData format to start, end time and sentence mode
	 */
	private TranscriptDataSimple convertToSimple(TranscriptData transcript)
	{
		TranscriptDataSimple newTranscriptsSimple;
		TranscriptItemData[] transcriptItems = new TranscriptItemData[transcript.getSynpoints().size()];
		String text;
		int startTime;
		int endTime;
		//println "transcriptid:"+transcript.getId()
		for(int j=0;j<transcript.getSynpoints().size();j++)
		{
			startTime = transcript.getSynpoint(j).getStartTime();
			endTime = transcript.getSynpoint(j).getEndTime();
			try
			{
				text = transcript.getText().substring(transcript.getSynpoint(j).getStartIndex()
					, transcript.getSynpoint(j).getEndIndex()+1);
				transcriptItems[j] = new TranscriptItemData(startTime, endTime,text);
			}
			catch(IndexOutOfBoundsException ex)
			{
				Logger.debug("out of boundary.");
				text = transcript.getText().substring(transcript.getSynpoint(j).getStartIndex()
						, transcript.getSynpoint(j).getEndIndex());
				transcriptItems[j] = new TranscriptItemData(startTime, endTime,text);
			}
		}
		newTranscriptsSimple =
			new TranscriptDataSimple(transcript.getId(),transcript.getOwner(), transcript.canEdit(),
					transcript.canDelete(),transcriptItems);

		return newTranscriptsSimple;
	}
	
	private String[] newLine(String textBlock, String[] newLineString)
	{
		//If cannot identfy any sign of new line, we decide that we have to have a line break when the string is above
		// 50 characters
		
		if(newLineString.size() == 0)
		{
			if(textBlock?.size() > 50)
				return (String[])[textBlock]
			else
				return null
		}
		else
		{
			for(String str : newLineString)
			{
				if(textBlock.matches(".*"+str+".*"))
				{
					return textBlock.split(str,2)
				}
			}
			return null
		}
		
	}
	
	/*
	 * Get which characters should be treated as a sign of a new line or sentence. Such string can be
	 * \r\n "," "." "<BRN>" "<br>" etc
	 * Please note that the newLine character is represented in regular expression format!
	 */
	private getNewLineList(TranscriptData data)
	{
		def newLineList = []
		if(!data.getText()?.contains(".") && data.getText()?.contains(","))
		{
			newLineList.add("\\,")
		}
		
		if(data.getText()?.contains("\r\n"))
		{
			log.debug("transcript contains line break.")
			newLineList.add("\\r?\\n")	
		}
		
		if(data.getText()?.contains("\n\n"))
		{
			log.debug("transcript contains line break.")
			newLineList.add("\\n?\\n")
		}
		
		if(data.getText()?.contains("."))
		{
			newLineList.add("\\.")	
		}
		
		//println "newLinelist:"+newLineList.size()
		return (String[])newLineList
	}
	
	/*
	 * Convert TranscriptData to SRT
	 */
	public String convertToSRT(TranscriptData data)
	{
		TranscriptDataSimple simpleData = convertToSimple(data)
		StringBuilder builder = new StringBuilder()
		String[] newLineList =  getNewLineList(data)
		int seqCount = 1
		
		int startTime = Integer.MAX_VALUE
		
		String textBlock = ""
		def items = simpleData.items.sort{it.getStart()}
		items.each{item ->
			if(startTime == Integer.MAX_VALUE)
			{
				startTime = item.getStart()	
			}
			
			//println "newline?"+newLine(item.getText(),newLineList)
			String[] splitedItemText = newLine(item.getText(),newLineList)
			if( splitedItemText || item == items.last())
			{
				if(splitedItemText)
				{
					textBlock+=splitedItemText[0]
				}
				
				builder.append(seqCount.toString()+"\r\n")
				String startTimeStr = TimeFormat.getInstance().toSRTTimeString(startTime)
				builder.append(startTimeStr)
				builder.append(" --> ")
				String endTimeStr = TimeFormat.getInstance().toSRTTimeString(item.getEnd())
				builder.append(endTimeStr)
				builder.append("\r\n")
				builder.append(textBlock)
				builder.append("\r\n\r\n")
				
				//reset starttime, textblock
				seqCount++
				startTime = Integer.MAX_VALUE
				//Yunjia: split the string with the separator into two parts. The second part goes to the next textblock
				if(splitedItemText)
				{
					textBlock=splitedItemText[1]
				}
			}
			else
			{
				textBlock += item.getText()
			}	
		}
		return builder.toString()	
	}
	/*
	 * convert srt string to transcriptitemsrt
	 * Not used
	 */
	public TranscriptItemSRT[] convertToSRTObjectFromString(String content)
	{
		def srtList = []
		String[] srtItems = content.split("\\r\\n\\r\\n");
		if(srtItems.length > 0)
		{
			for(int i=0;i<srtItems.length;i++)
			{
				//split a srt block into 3 parts
				//first the index
				//second the time
				//third the text
				if(srtItems[i].length() == 0)
				{
					continue
				}
				String[] srtContent =srtItems[i].split("\\r\\n",3);
				
				if(srtContent.length == 3)
				{
					int seqCount = Integer.parseInt(srtContent[0]) //the index for srt
					int startTime=0;
					int endTime=0;
					
					//Check the srt time
					String[] times = srtContent[1].split("-->");
					if(times.length == 2)
					{
						startTime = getSRTFormatTime(times[0]);
						endTime = getSRTFormatTime(times[1]);
						if(endTime == -1 || startTime == -1 || startTime > endTime)
						{
							//println "1"
							throw new PlayerException(APIStatusCode.TRANSCRIPT_DRAFT_INVALID,"Saved draft format error: The time format at index "+ String.valueOf(i)+" is bad formatted.");
						}
					}
					else
					{
						throw new PlayerException(APIStatusCode.TRANSCRIPT_DRAFT_INVALID, "Saved draft format error: The time format at index "+ String.valueOf(i)+" is bad formatted.");
					}
					
					String text = srtContent[2];
					TranscriptItemSRT srtLine = new TranscriptItemSRT(seqCount, startTime, endTime, text);
					srtList << srtLine
				}
				else
				{
					throw new PlayerException(APIStatusCode.TRANSCRIPT_DRAFT_INVALID,"Saved draft file format error: The content at index "+ String.valueOf(i)+" is bad formatted.");
				}
			}
			
			return srtList;
		}
		else
		{
			//Logger.debug("5 return empty transcript");
			//createEmptyTranscript();
			throw new PlayerException(APIStatusCode.TRANSCRIPT_DRAFT_INVALID,"Saved draft file format error: The .srt file is not valid.");
		}
	}
	//convert to transcriptitemsrt from transcriptdata
	public TranscriptItemSRT[] convertToSRTObject(TranscriptData data)
	{
		TranscriptDataSimple simpleData = convertToSimple(data)
		String[] newLineList =  getNewLineList(data)
		def srtList = []
		int seqCount = 1
		int startTime = Integer.MAX_VALUE
		
		String textBlock = ""
		def items = simpleData.items.sort{it.getStart()}
		//println "##################################"
		items.each{item ->
			//println "\r\nText:"+item.getText()
			if(startTime == Integer.MAX_VALUE)
			{
				startTime = item.getStart()
			}
			
			String[] splitedItemText = newLine(item.getText(),newLineList)
			
			if( splitedItemText || item == items.last())
			{
				//println "size:"+splitedItemText?.size()
				//println "\r\n0:"+splitedItemText?splitedItemText[0]:"empty!!"
				if(splitedItemText)
				{
					textBlock+=splitedItemText[0]+"."
				}
				
				TranscriptItemSRT srtLine = new TranscriptItemSRT(seqCount, startTime, item.getEnd(),textBlock);
				srtList << srtLine
				//reset starttime, textblock
				seqCount++
				startTime = Integer.MAX_VALUE
				//Yunjia: split the string with the separator into two parts. The second part goes to the next textblock
				if(splitedItemText && splitedItemText.size()>=2)
				{
					textBlock=splitedItemText[1]
				}
			}
			else
			{
				textBlock += item.getText()
			}
		}
		return srtList
	}
	
	/*
	* Convert TranscriptData to TimedText w3c standard
	*/
	public String convertToTT(TranscriptData data)
	{
		//Not implemented yet
		return null	
	}
//###########################  save srt to database  ######################################################################
	/*
	* validate the srt json objects
	* Not used
	*/
   def validateSRTJSON(transcriptsJSON)
   {
	   //not implemented
	   return true
   }
   /*
	* Convert srt json (TranscriptItemSRT) objects to srt string
	* Not used
	*/
   def convertJSONToSRT(transcriptsJSON)
   {
	   StringBuilder builder = new StringBuilder()
	   int i = 1
	   //sort it by start time first
	   transcriptsJSON.sort{it.start}.each{srt->
		   if(srt.text != null && srt.text.trim()?.length() != 0)
		   {
			   builder.append(i++)
			   builder.append("\r\n")
			   builder.append(TimeFormat.getInstance().toSRTTimeString(srt.start))
			   builder.append(" --> ")
			   builder.append(TimeFormat.getInstance().toSRTTimeString(srt.end))
			   builder.append("\r\n")
			   builder.append(srt.text)
			   builder.append("\r\n\r\n")
		   }	   
	   }
	   return builder.toString();
   }
   
   /*
    * Convert srt into TranscriptData
    * Not used
    */
   def createTranscriptFromSRT(String content) throws PlayerException
   {
	   //println "content:"+content
	   String[] srtItems = content.split("\\r\\n\\r\\n");
	   if(srtItems.length > 0)
	   {
		   TranscriptData transcriptData = new TranscriptData(null, null, true, true, content, null);
		   int characterCount = 0;
		   StringBuilder builder = new StringBuilder();
		   for(int i=0;i<srtItems.length;i++)
		   {
			   //split a srt block into 3 parts
			   //first the index
			   //second the time
			   //third the text
			   //if the srtitem[i] is a empty string, continue
			   if(srtItems[i].length() == 0)
			   {
			   		continue
			   }
			   String[] srtContent =srtItems[i].split("\\r\\n",3);
			   
			   if(srtContent.length == 3)
			   {
				   int startTime=0;
				   int endTime=0;
				   int startChar=0;
				   int endChar=0;
				   int currentTextLength = 0;
				   
				   //Check the srt time
				   String[] times = srtContent[1].split("-->");
				   if(times.length == 2)
				   {
					   startTime = getSRTFormatTime(times[0]);
					   endTime = getSRTFormatTime(times[1]);
					   if(endTime == -1 || startTime == -1 || startTime > endTime)
					   {
						   //println "1"
						   throw new PlayerException(APIStatusCode.TRANSCRIPT_SRT_INVALID,"Srt file format error: The time format at index "+ String.valueOf(i)+" is bad formatted.");
					   }
				   }
				   else
				   {
					   throw new PlayerException(APIStatusCode.TRANSCRIPT_SRT_INVALID, "Srt file format error: The time format at index "+ String.valueOf(i)+" is bad formatted.");
				   }
				   
				   String text = srtContent[2].trim();
				   if(text!= null && text.length()>0)
				   {
					   log.debug("text length:"+text.length())
					   //println "text length:"+text.length()
					   //Attach \r\n at the last line
					   text += "\r\n"
					   currentTextLength = text.length();
					   startChar = characterCount;
					   endChar = characterCount + currentTextLength -1;
					   characterCount += currentTextLength;
					   TranscriptSynpoint synpoint = new TranscriptSynpoint(startChar, endChar, startTime, endTime);
					   transcriptData.addSynpoint(synpoint);
					   builder.append(text);
				   }
			   }
			   else
			   {
				   throw new PlayerException(APIStatusCode.TRANSCRIPT_SRT_INVALID,"Srt file format error: The content at index "+ String.valueOf(i)+" is bad formatted.");
			   }
		   }
		   if(transcriptData.getSynpoints().size() > 0)
		   {
			   transcriptData.setText(builder.toString());
		   }
		   else
		   {
			   //Logger.debug("4 return empty transcript");
			   //createEmptyTranscript();
			   throw new PlayerException(APIStatusCode.TRANSCRIPT_SRT_INVALID,"Srt file format error: The .srt file is not valid.");
		   }
		   return transcriptData;
	   }
	   else
	   {
		   //Logger.debug("5 return empty transcript");
		   //createEmptyTranscript();
		   throw new PlayerException(APIStatusCode.TRANSCRIPT_SRT_INVALID,"Srt file format error: The .srt file is not valid.");
	   }
   }
   
   /*
    * Get the real time in milisecs from srt time string format
    * Not used
    */
   private int getSRTFormatTime(String timeStr)
   {
	   //SRT time format: hh:mm:ss,mmm
	   int time = -1;
	   String[] hhmmssmmm = timeStr.trim().split(",");
	   //println "timestr:"+timeStr
	   //println "size:"+hhmmssmmm.length
	   if(hhmmssmmm.length ==2)
	   {
		   String[] hhmmss = hhmmssmmm[0].split(":");
		   if(hhmmss.length == 3)
		   {
			   String mmm = hhmmssmmm[1];
			   try
			   {
				   //println hhmmss[0]+":"+hhmmss[1]+":"+hhmmss[2]+","+mmm
				   time = 0;
				   time = time + Integer.parseInt(hhmmss[0].trim())*3600;
				   time = time + Integer.parseInt(hhmmss[1].trim())*60;
				   time = time + Integer.parseInt(hhmmss[2].trim());
				   time = time * 1000 + Integer.parseInt(mmm.trim());
				   return time;
			   }
			   catch(NumberFormatException nfe)
			   {
				   //Logger.debug("1 return -1");
				   return -1;
			   }
		   }
		   else
		   {
			   //Logger.debug("2 return -1");
			   return -1;
		   }

	   }
	   else
	   {
		   //Logger.debug("3 return -1");
		   return -1;
	   }
   }
 
//##############################  SRT Draft  ##################################################################
   
   /*
    * Operations about the srt draft
    */
   def createSRTDraft(srtStr,user, multimediaId)
   {
	   def catalinaBase = System.properties.getProperty('catalina.base')
	   if (!catalinaBase)
		   catalinaBase = '.'
	   
	   def destPath = "${catalinaBase}/temp/${user.id}/transcript/"
	   def parentDir = new File(destPath)
	   if(!parentDir.exists())
		   parentDir.mkdirs()
	   
	   def file = new File(destPath,"${multimediaId}.srt")
	   file.setText(srtStr, "utf-8")
   }
   
   /*
    * Not used
    */
   def getSRTDraft(user,multimediaId)
   {
	   def catalinaBase = System.properties.getProperty('catalina.base')
	   if (!catalinaBase)
		   catalinaBase = '.'
	   
	   def destPath = "${catalinaBase}/temp/${user.id}/transcript/"
	   def file = new File(destPath,"${multimediaId}.srt")
	   if(file.exists())
	   {
			return file   
	   }
	   else
	   		return null
   }
   
   def deleteSRTDraft(user,multimediaId)
   {
	   def catalinaBase = System.properties.getProperty('catalina.base')
	   if (!catalinaBase)
		   catalinaBase = '.'
	   
	   def destPath = "${catalinaBase}/temp/${user.id}/transcript/"
	   def file = new File(destPath,"${multimediaId}.srt")
	   if(file.exists())
	   {
			file.delete()
	   }
   }
}

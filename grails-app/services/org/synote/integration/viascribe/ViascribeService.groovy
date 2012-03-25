package org.synote.integration.viascribe

import org.synote.resource.compound.*
import org.synote.integration.viascribe.exception.ViascribeException
import org.synote.annotation.*
import org.synote.annotation.synpoint.Synpoint
import org.synote.resource.single.text.SynmarkTag
import org.synote.resource.single.text.SynmarkTextNote
import org.synote.resource.single.binary.PresentationSlide
import org.synote.resource.single.binary.MultimediaUrl
import org.synote.resource.single.text.TranscriptTextResource

class ViascribeService {

	boolean transactional = true
	
	def upload(user, title, xml, url, perm)
	{
		if (!url.endsWith('/'))
		url += '/'
		
		def multimedia = uploadMultimedia(user, title, xml, url, perm)
		uploadTranscript(user, multimedia, 'Transcript', xml)
		uploadPresentation(user, multimedia, 'Presentation', xml, url)
		
		if (xml.slides.slide.size()!= 0)
		{
			//			Create Synamark with Slide's text data
			uploadSynmark(user,multimedia,xml)
		}
		
		return multimedia
	}
	
	public uploadMultimedia(user, title, xml, url, perm)
	{
		def multimedia = new MultimediaResource()
		if(xml.media.size() != 1)
		{
			throw new ViascribeException("The sync XML is not valid.No or more than one media tag(s) in the xml!")
		}
		
		if(xml.media.@file.text().trim().equals(""))
		{
			throw new ViascribeException("The file attribute in media tag cannot be empty")
		}
		
		multimedia.owner = user
		multimedia.title = title
		//Url should not have spaces
		String urlStr = (url + xml.media.@file.text()).replaceAll(" ", "%20")
		def multimediaUrl = new MultimediaUrl(url:urlStr, owner:user)
		multimedia.url = multimediaUrl
		multimedia.perm = perm
		
		multimedia.save()
		//if (!multimedia.save())
		//	throw new ViascribeException("Cannot create multimedia resource")
		multimedia.index()
		
		return multimedia
	}
	
	//TODO: It is still unclear when should we find the transcriptResource which annotates
	//this multimedia and add a new transcriptTextResource into this TranscriptResource
	public uploadTranscript(user, multimedia, title, xml)
	{
		if (xml.VSTextData.size() != 1)
		return
		
		if(xml.VSTextData.text().trim().size() == 0)
		return
		
		def transcript = new TranscriptResource()
		
		transcript.owner = user
		transcript.title = title
		String content = xml.VSTextData.text().replace('<BRN>', '\r\n').substring(1)
		def transcriptTextResource = new TranscriptTextResource(content:content, owner:user)
		transcript.transcript = transcriptTextResource
		//println "Transcript :" + xml.VSTextData.text().replace('<BRN>', '\r\n').substring(1)
		
		if (!transcript.save())
			throw new ViascribeException("Cannot create transcript resource")
		
		transcript.index()
		
		def annotation = new ResourceAnnotation()
		
		annotation.owner = user
		annotation.source = transcript
		annotation.target = multimedia
		
		xml.timing.each() {item ->
			
			if(item.@begin.text().equals("") || item.@end.text().equals("") || item.@time.text().equals("") || item.@endingtime.text().equals(""))
			{
				throw new ViascribeException("Missing attributes in the timing tag in xml.")
			}
			
			try
			{
				def sourceStart = Integer.parseInt(item.@begin.text())
				def sourceEnd = Integer.parseInt(item.@end.text()) - 1
				def targetStart = Integer.parseInt(item.@time.text())
				def targetEnd = Integer.parseInt(item.@endingtime.text())
				annotation.addToSynpoints(new Synpoint(sourceStart: sourceStart, sourceEnd: sourceEnd, targetStart: targetStart, targetEnd: targetEnd))
			}
			catch(Exception e)
			{
				throw new ViascribeException("The information in some attribute is not valid.");
			}
		}
		
		if (!annotation.save())
		throw new ViascribeException("Cannot create transcript annotation")
		
		return annotation
	}
	
	public uploadVSXMLTranscript(user, multimedia, title, xml)
	{
		if (xml.VSTextData.size() != 1)
		return
		
		if(xml.VSTextData.text().trim().size() == 0)
		return
		
		//TODO: it is still unknow if the following code is necessary, or maybe it's
		//better to add another transcriptTextResource instance for TranscriptResource
		def annotationList = ResourceAnnotation.findAllByTarget(multimedia)
		annotationList.each{ annotation ->
			if(annotation.source instanceof TranscriptResource)
			{
				//Log
				def oldTranscript = annotation.source
				
				oldTranscript.unindex()
				
				oldTranscript.delete()
				annotation.delete()
			}
		}
		
		def annotation = new ResourceAnnotation()
		//old one def text = xml.VSTextData.text().replace('<BRN>', '\n').substring(1)
		def text = xml.VSTextData.text().replace('<BRN>', '\n').substring(3)
		int leadingSpaces=0
		
		xml.timing.each() {item ->
			
			if(item.@begin.text().equals("") || item.@end.text().equals("") || item.@time.text().equals("") || item.@endingtime.text().equals(""))
			{
				throw new ViascribeException("Missing attributes in the timing tag in xml.")
			}
			
			try
			{
				if(Integer.parseInt(item.@begin.text()) == 0)
				{
					String leadingChars = text.substring(0, Integer.parseInt(item.@end.text()))
					if(leadingChars.trim().length() == 0)
					{
						leadingSpaces=Integer.parseInt(item.@end.text())
						//println "leadingSpaces:"+leadingSpaces
					}
					else
					{
						def sourceStart = Integer.parseInt(item.@begin.text())
						def sourceEnd = Integer.parseInt(item.@end.text())-1
						def targetStart = Integer.parseInt(item.@time.text())
						def targetEnd = Integer.parseInt(item.@endingtime.text())
						annotation.addToSynpoints(new Synpoint(sourceStart: sourceStart, sourceEnd: sourceEnd, targetStart: targetStart, targetEnd: targetEnd))
					}
				}
				//Two white spaces are put into the beginning of the text when uploading by IBM HTS
				else if(leadingSpaces !=0 && Integer.parseInt(item.@begin.text()) == leadingSpaces)
				{
					def sourceStart = 0
					def sourceEnd = Integer.parseInt(item.@end.text())-1
					def targetStart = Integer.parseInt(item.@time.text())
					def targetEnd = Integer.parseInt(item.@endingtime.text())
					annotation.addToSynpoints(new Synpoint(sourceStart: sourceStart, sourceEnd: sourceEnd, targetStart: targetStart, targetEnd: targetEnd))
					//println "${sourceStart}-->${sourceEnd}:#"+text.substring(sourceStart,sourceEnd+1)+"#"
				}
				else
				{
					def sourceStart = Integer.parseInt(item.@begin.text())
					def sourceEnd = Integer.parseInt(item.@end.text())-1
					def targetStart = Integer.parseInt(item.@time.text())
					def targetEnd = Integer.parseInt(item.@endingtime.text())
					annotation.addToSynpoints(new Synpoint(sourceStart: sourceStart, sourceEnd: sourceEnd, targetStart: targetStart, targetEnd: targetEnd))
				}
				
				//println "${sourceStart}-->${sourceEnd}:#"+text.substring(sourceStart,sourceEnd+1)+"#"
			}
			catch(Exception e)
			{
				println "error:"+e.getMessage()
				throw new ViascribeException("The information in some attribute is not valid.");
			}
		}
		
		def transcript = new TranscriptResource()
		
		transcript.owner = user
		transcript.title = title
		def transcriptTextResource = new TranscriptTextResource(content:text, owner:user)
		transcript.transcript = transcriptTextResource
		//println "vsTextDataLength:"+xml.VSTextData.text().length()
		//println "transcriptContentLength:"+transcript.content.length()
		//println xml.VSTextData.text().replace('<BRN>', '\r\n').substring(leadingSpaces+1)
		
		if (!transcript.save())
		throw new ViascribeException("Cannot create transcript resource")
		
		transcript.index()
		
		annotation.owner = user
		annotation.source = transcript
		annotation.target = multimedia
		
		if (!annotation.save())
		throw new ViascribeException("Cannot create transcript annotation")
		
		return annotation
	}
	
	public uploadSynmark(user, multimedia, xml)
	{
		xml.slides.slide.each() {item ->
			def synmark = new SynmarkResource
			( owner: user
			, title: item.slideTitle.text()
			, note: new SynmarkTextNote(owner: user, content: item.slideText.text())
			)
			
			if (!synmark.save())
			throw new ViascribeException("Cannot create Synmark annotation")
			
			synmark.index()
			
			def annotation1 = new ResourceAnnotation(owner: user, source: synmark, target: multimedia)
			
			def start = Integer.parseInt(item.@start.text())
			//println "start :" + Integer.parseInt(item.@start.text())
			
			annotation1.addToSynpoints(new Synpoint(targetStart: start))
			
			if (!annotation1.save())
			throw new ViascribeException("Cannot create Synmark annotation")
		}
	}
	
	public uploadPresentation(user, multimedia, title, xml, url)
	{
		if (xml.timeItem.size() == 0)
		return
		
		def presentation = new PresentationResource()
		
		presentation.owner = user
		presentation.title = title
		
		def index = 0
		xml.timeItem.each() {slide ->
			def slideUrl = url + slide.@file.text()
			presentation.addToSlides(new PresentationSlide(index: index++, url: slideUrl,owner:user))
		}
		
		presentation.validate()
		if(presentation.hasErrors())
		{
			presentation.errors.each {
				println "error:"+it
			}
		}
		
		if (!presentation.save())
		throw new ViascribeException("Cannot create presentation resource")
		
		def annotation = new ResourceAnnotation()
		
		annotation.owner = user
		annotation.source = presentation
		annotation.target = multimedia
		
		def sourceStart = 0
		xml.timeItem.each() {slide ->
			try
			{
				def targetStart = Integer.parseInt(slide.@timeMS.text())
				annotation.addToSynpoints(new Synpoint(sourceStart: sourceStart++, targetStart: targetStart))
			}
			catch(Exception e)
			{
				throw new ViascribeException("The information in some attribute is not valid.");
			}
		}
		
		if (!annotation.save())
		throw new ViascribeException("Cannot create presentation annotation")
		
		return annotation
	}
}

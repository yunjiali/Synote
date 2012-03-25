package org.synote.search.sitemap

import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import org.apache.commons.logging.LogFactory

import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.synote.resource.compound.*
import org.synote.resource.Resource
import org.synote.annotation.ResourceAnnotation
import org.synote.linkeddata.RedirectData
import org.synote.user.User
import org.synote.user.UserRole

import org.synote.linkeddata.LinkedDataService
import org.synote.permission.PermService
import org.synote.utils.UtilsService

class SitemapService {

    static transactional = true

	def linkedDataService
	def permService
	def utilsService
	
	private String contextPath = SCH.getServletContext()?.getRealPath("/")
	private static final log = LogFactory.getLog(this)
	//Yunjia: add them to configuration
	private static final String REPLAY_CHANGE_FREQ = "weekly"
	private static final String REPLAY_PRIORITY = "0.9"
	private static final String RESOURCES_CHANGE_FREQ = "weekly"
	private static final String RESOURCES_PRIORITY = "0.9"
	private static final String ANNOTATIONS_CHANGE_FREQ = "weekly"
	private static final String ANNOTATIONS_PRIORITY = "0.5"
	private static final String USERS_CHANGE_FREQ = "monthly"
	private static final String USERS_PRIORITY = "0.3"
	
	/*
	 * Generate replay page maps for google
	 */
    def createReplaySitemap() {
		
		def resBaseURI = linkedDataService.getResourceBaseURI()
		def htmlBaseURI = linkedDataService.getHTMLBaseURI()
		def cal = Calendar.getInstance()
		def xmlBuilder = new StreamingMarkupBuilder()
		xmlBuilder.encoding= "UTF-8"
		def writer = xmlBuilder.bind{
			mkp.xmlDeclaration()
			mkp.declareNamespace("":"http://www.sitemaps.org/schemas/sitemap/0.9")
			//mkp.declareNamespace(xsi:"http://www.w3.org/2001/XMLSchema-instance")
			mkp.declareNamespace(video:"http://www.google.com/schemas/sitemap-video/1.1")
			//mkp.declareNamespace(schemaLocation:"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd")
			urlset{
				def baseURI = linkedDataService.getBaseURI()
				url{
					loc(baseURI)
					changefreq("daily")
					priority(RESOURCES_PRIORITY)
				}
				url{
					loc(baseURI+"user/accessiblity/")
					changefreq("monthly")
					priority(RESOURCES_PRIORITY)
				}
				url{
					loc(baseURI+"user/contact/")
					changefreq("monthly")
					priority(RESOURCES_PRIORITY)
				}
				url{
					loc(baseURI+"user/termsAndConditions/")
					changefreq("monthly")
					priority(RESOURCES_PRIORITY)
				}
				def multimediaList = MultimediaResource.list()
				def urlList = []
				multimediaList.each{multimedia->
					//We only consider public permission here
					def perm = multimedia.perm?.val
					if(perm > 0)
					{
						//MultimediaResource /resources/id
						def resHTMLBaseURI = htmlBaseURI+multimedia.id
						//MultimediaResource /recording/replay/id
						if(!urlList.contains(resHTMLBaseURI))
						{
							urlList << resHTMLBaseURI
							def annotations = ResourceAnnotation.findAllByTarget(multimedia)
							
							//Use presentation as the thumbnail image if there are any
							def presentationAnnotation = annotations.find{it.source?.instanceOf(PresentationResource)}
							def thumbnail = null
							if(!presentationAnnotation)
							{
								thumbnail = linkedDataService.getDefaultThumbnailImageURI()	
							}
							else
							{
								def leng = presentationAnnotation.source?.slides.size()
								if(leng > 0)
									thumbnail = presentationAnnotation.source?.slides.toArray()[leng-1].url
								else
									thumbnail = linkedDataService.getDefaultThumbnailImageURI()
							}
							
							//Check if the multimedia location is a landing page or an actual file
							def locationURL = multimedia.url?.url
							boolean content_loc = true
							if(!utilsService.isMediaFile(locationURL))
							{
								content_loc = false		
							}
							boolean isVideo=true
							if(!utilsService.isVideo(locationURL))
							{
								isVideo = false	
							}
							url{
								loc(resHTMLBaseURI)
								lastmod(utilsService.convertSQLTimeStampToFormattedTimeString(multimedia.lastUpdated,"yyyy-MM-dd"))
								if(isVideo)
								{
									video.video{
										video.title(multimedia.title)
										video.description(multimedia.title)
										if(content_loc)
										{
											video.thumbnail_loc(thumbnail)
											video.content_loc(locationURL)	
										}
										else
										{
											//Yunjia: we can get thumbnail image from Youtube, dailyMotion, etc
											video.thumbnail_loc(thumbnail)
											video.player_loc(locationURL)	
										}
									}
								}
					
							}
							annotations.each{annotation->
								//Do not index TranscriptResource
								if(!annotation.source.instanceOf(TranscriptResource) || annotation.source.instanceOf(WebVTTResource))
								{
									String lastMod = utilsService.convertSQLTimeStampToFormattedTimeString(annotation.source?.lastUpdated,"yyyy-MM-dd")
									def synpoints = annotation.synpoints
									synpoints.each{synpoint->
										String frag = linkedDataService.getFragmentStringFromSynpoint(synpoint)
										String urlStr =  utilsService.attachFragmentToURIForGoogleAjaxCrawl(resHTMLBaseURI,frag)
										String locationFrag = utilsService.attachFragmentToURI(locationURL,frag)
										if(!urlList.contains(urlStr))
										{
											urlList << urlStr
											url{
												loc(urlStr)
												lastmod(lastMod)
												if(isVideo)
												{
													video.video{
														//duplicate video title will result in warnings in Google Web master tool
														video.title(multimedia.title + " fragment ${frag}")
														//Yunjia: get thumbnail from synpoint later
														video.thumbnail_loc(thumbnail)
														video.description(multimedia.title + " fragment ${frag}")
														if(content_loc)
														{
															video.content_loc(locationFrag)	
														}
														else
														{
															video.player_loc(locationFrag)	
														}
													}
												}
											}
										}
									}	
								}
							}
						}
					}	
				}
			}	
		}
		//writeTo(new File("replay.xml").newWriter())
		def fileWriter = new File(contextPath+"/replay.xml").newWriter()
		XmlUtil.serialize(writer,fileWriter)
		//def replayXMLWriter = new FileWriter("/replay.xml")
		//replayXMLWriter << writer
		return
    }

	//Only RDF data
	def createResourcesSitemap()
	{
		def resDataBaseURI = linkedDataService.getResourceDataBaseURI()
		
		def xmlBuilder = new StreamingMarkupBuilder()
		xmlBuilder.encoding= "UTF-8"
		def writer = xmlBuilder.bind{
			mkp.xmlDeclaration()
			mkp.declareNamespace("":"http://www.sitemaps.org/schemas/sitemap/0.9")
			mkp.declareNamespace(xsi:"http://www.w3.org/2001/XMLSchema-instance")
			mkp.declareNamespace(schemaLocation:"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd")
			urlset{
				def resources = Resource.list()
				resources.each{res->
					def rData = linkedDataService.getRedirectDataFromResource(res)
					if(rData!=null)
					{
						if(rData.recording?.perm?.val>0)
						{
							url{
								loc(resDataBaseURI+res.id)
								lastmod(utilsService.convertSQLTimeStampToFormattedTimeString(res.lastUpdated,"yyyy-MM-dd"))
								changefreq(RESOURCES_CHANGE_FREQ)
								priority(RESOURCES_PRIORITY)
							}
						}
					}
				}
			}	
		}
		def fileWriter = new File(contextPath+"/resources.xml").newWriter()
		XmlUtil.serialize(writer,fileWriter)
		//def replayXMLWriter = new FileWriter("/replay.xml")
		//replayXMLWriter << writer
		return
	}
	def createAnnotationsSitemap() {
		def annoDataBaseURI =  linkedDataService.getAnnotationDataBaseURI()
		def xmlBuilder = new StreamingMarkupBuilder()
		xmlBuilder.encoding= "UTF-8"
		def writer = xmlBuilder.bind{
			mkp.xmlDeclaration()
			mkp.declareNamespace("":"http://www.sitemaps.org/schemas/sitemap/0.9")
			mkp.declareNamespace(xsi:"http://www.w3.org/2001/XMLSchema-instance")
			mkp.declareNamespace(schemaLocation:"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd")
			urlset{
				def annotations = ResourceAnnotation.list()
				annotations.each{annotation->
					def rData = linkedDataService.getRedirectDataFromAnnotation(annotation)
					if(rData!=null)
					{
						if(rData.recording?.perm?.val>0)
						{
							url{
								loc(annoDataBaseURI+annotation.id)
								lastmod(utilsService.convertSQLTimeStampToFormattedTimeString(rData.compoundResource?.lastUpdated,"yyyy-MM-dd"))
								changefreq(ANNOTATIONS_CHANGE_FREQ)
								priority(ANNOTATIONS_PRIORITY)
							}
						}
					}
				}
			}
		}
		def fileWriter = new File(contextPath+"/annotations.xml").newWriter()
		XmlUtil.serialize(writer,fileWriter)
		//def replayXMLWriter = new FileWriter("/replay.xml")
		//replayXMLWriter << writer
		return
	}
	def createUsersSitemap() {
		def userDataBaseURI =  linkedDataService.getUserDataBaseURI()
		def xmlBuilder = new StreamingMarkupBuilder()
		xmlBuilder.encoding= "UTF-8"
		def writer = xmlBuilder.bind{
			mkp.xmlDeclaration()
			mkp.declareNamespace("":"http://www.sitemaps.org/schemas/sitemap/0.9")
			mkp.declareNamespace(xsi:"http://www.w3.org/2001/XMLSchema-instance")
			mkp.declareNamespace(schemaLocation:"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd")
			urlset{
				def normal_role = UserRole.findByAuthority("ROLE_NORMAL")
				def users = normal_role.people
				users.each{user->
					url{
							loc(userDataBaseURI+user.id)
							lastmod(utilsService.convertSQLTimeStampToFormattedTimeString(user.lastUpdated,"yyyy-MM-dd"))
							changefreq(USERS_CHANGE_FREQ)
							priority(USERS_PRIORITY)
					}
				}
			}
		}
		def fileWriter = new File(contextPath+"/users.xml").newWriter()
		XmlUtil.serialize(writer,fileWriter)
		//def replayXMLWriter = new FileWriter("/replay.xml")
		//replayXMLWriter << writer
		return
	}
	
}

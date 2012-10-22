package org.synote.resource.service

import org.synote.resource.SynotemmService

class ThumbnailsJob {
	
	def synotemmService
	
    static triggers = {
      //simple name:'thumbnailsTrigger', startDelay:0, repeatCount:1
    }

    def execute(context) {
		//println "thumb job start:"
		def webVTTResource = context.mergedJobDataMap.get('vtt')
		def multimedia = context.mergedJobDataMap.get('multimedia')
		synotemmService.generateWebVTTThumbnails(webVTTResource, multimedia)
		//println "thumb job finished:"
    }
}

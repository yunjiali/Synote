package org.synote.search.sitemap

import org.synote.search.sitemap.SitemapService
class AnnotationsJob {
    def sitemapService
	
	static triggers = {
		cron name:'annotationsSitemapTrigger', cronExpression:"0 0 5 * * ?"//execute job 4am everyday
	}
	
	def group = "Sitemap"
	def name = "AnnotationsSitemap"
	
    def execute() {
		log.info("create annotations sitemap...")
		sitemapService.createAnnotationsSitemap()
    }
}

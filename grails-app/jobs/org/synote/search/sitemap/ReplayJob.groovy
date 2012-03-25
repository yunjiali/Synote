package org.synote.search.sitemap

import org.synote.search.sitemap.SitemapService

class ReplayJob {
	def sitemapService
	
	static triggers = {
		cron name:'replaySitemapTrigger', cronExpression:"0 0 5 * * ?"//execute job 4am everyday
	}
	
	def group = "Sitemap"
	def name = "ReplaySitemap"
	
    def execute() {
		log.info("create replay sitemap...")
		sitemapService.createReplaySitemap()
    }
}

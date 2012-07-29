package org.synote.search.sitemap

import org.synote.search.sitemap.SitemapService
class ResourcesJob {
    def sitemapService
	
	static triggers = {
		cron name:'resourcesSitemapTrigger', cronExpression:"0 0 5 * * ?"//execute job 4am everyday
	}
	
	def group = "Sitemap"
	def name = "ResourcesSitemap"
	
    def execute() {
		//log.info("create resources sitemap...")
		//sitemapService.createResourcesSitemap()
    }
}

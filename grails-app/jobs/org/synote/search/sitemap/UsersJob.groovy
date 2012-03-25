package org.synote.search.sitemap

import org.synote.search.sitemap.SitemapService

class UsersJob {
    def sitemapService
	
	static triggers = {
		cron name:'usersSitemapTrigger', cronExpression:"0 0 5 * * ?"//execute job 4am everyday
	}
	
	def group = "Sitemap"
	def name = "UsersSitemap"
	
    def execute() {
		log.info("create users sitemap...")
		sitemapService.createUsersSitemap()
    }
}

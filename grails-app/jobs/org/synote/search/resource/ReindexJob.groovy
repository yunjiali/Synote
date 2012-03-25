package org.synote.search.resource

class ReindexJob {
   
	def resourceSearchService
	
	static triggers = {
		cron name:'resourceReindexTrigger', cronExpression:"0 0 4 * * ?"//execute job 4am everyday
	}
	
	def group = "Search"
	def name = "ResoruceReindex"
	
    def execute() {
		
		log.info "Start to reindex resources..."
        resourceSearchService.indexResources()
    }
}

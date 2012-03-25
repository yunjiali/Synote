package org.synote.utils

import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ApplicationAttributes

class DataSourceUtils 
{
	static final Logger log = Logger.getLogger(DataSourceUtils)
	
	private static final ms = 1000 * 60 * 30
	
	public static tune = { servletContext ->
		
		def ctx = servletContext.getAttribute(ApplicationAttributes.APPLICATION_CONTEXT)
		ctx.dataSourceUnproxied.with {d ->
				d.setMinEvictableIdleTimeMillis(ms)
				d.setTimeBetweenEvictionRunsMillis(ms)
				d.setNumTestsPerEvictionRun(3)
				d.setTestOnBorrow(true)
				d.setTestWhileIdle(true)
				d.setTestOnReturn(true)
				d.setValidationQuery('select 1')
		}
	
		if (log.infoEnabled) {
			
			log.info "Configured Datasource properties:"
			ctx.dataSource.properties.findAll {k, v -> !k.contains('password') }.each {p ->
				log.info "  $p"
			}
		}
	}
}
